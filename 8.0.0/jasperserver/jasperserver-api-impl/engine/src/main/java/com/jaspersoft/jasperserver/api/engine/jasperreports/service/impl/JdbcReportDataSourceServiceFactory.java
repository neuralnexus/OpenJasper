/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectCache;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectEntry;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessResourceFailureException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JdbcReportDataSourceServiceFactory implements ReportDataSourceServiceFactory {

    private static final Log log = LogFactory.getLog(JdbcReportDataSourceServiceFactory.class);
    private static final String GOOGLE_BIG_QUERY_SIMBA_DRIVER_NAME = "com.simba.googlebigquery.jdbc41.Driver";
	private static final String GOOGLE_BIG_QUERY_PROGRESS_PRIVATE_KEY = "ServiceAccountPrivateKey";
	private static final String GOOGLE_BIG_QUERY_SIMBA_PRIVATE_KEY = "OAuthPvtKeyPath";
	private static final String GOOGLE_BIG_QUERY_PROGRESS_SERVICE_ACCOUNT = "ServiceAccountEmail";
	private static final String GOOGLE_BIG_QUERY_PROGRESS_PROJECT = "Project";
	private static final String GOOGLE_BIG_QUERY_PROGRESS_DATASET = "Dataset";
	protected static final String SNOWFLAKE_DRIVER_CLASS = "net.snowflake.client.jdbc.SnowflakeDriver";

	protected static class PooledDataSourcesCache {

        protected static class DataSourceEntry extends PooledObjectEntry {
			final PooledDataSource ds;

			public DataSourceEntry(Object key, PooledDataSource ds) {
				super(key);
				this.ds = ds;
			}

            public boolean isActive() {
                return ds.isActive();
            }

		}

        static class PooledDataSourcesCacheLog implements PooledObjectCache.PooledObjectCacheLog {

            public void debug(Object key, DebugCode code) {
                if (!log.isDebugEnabled()) return;
                switch (code) {
                    case STILL_ACTIVE:
						log.debug("Connection pool for " + key + " is still active, not expiring");
                        break;
                    case EXPIRING:
                        log.debug("Expiring connection pool for " + key);
                        break;
                }

            }
        }

        protected PooledObjectCache  pooledObjectCache;

		public PooledDataSourcesCache() {
		    pooledObjectCache = new PooledObjectCache();
            pooledObjectCache.setLog(new PooledDataSourcesCacheLog());
		}

		public PooledDataSource get(Object key, long now) {
            PooledObjectEntry entry = pooledObjectCache.get(key, now);
			if ((entry != null) && (entry instanceof DataSourceEntry)) return ((DataSourceEntry)entry).ds;
		    return null;
		}

		public void put(Object key, PooledDataSource ds, long now) {
			DataSourceEntry entry = new DataSourceEntry(key, ds);
			pooledObjectCache.put(key, entry, now);
		}
		
		public List removeExpired(long now, int timeout, int athenaTimeOut) {
            List expired = new ArrayList();
            List<PooledObjectEntry> expiredEntries = pooledObjectCache.removeExpired(now, timeout, athenaTimeOut);
            for (PooledObjectEntry objectEntry : expiredEntries) expired.add(((DataSourceEntry)objectEntry).ds);
			return expired;
		}

	}

	private PooledJdbcDataSourceFactory pooledJdbcDataSourceFactory;
	private PooledDataSourcesCache poolDataSources;
	private ProfileAttributesResolver profileAttributesResolver;
	private int poolTimeout;
	private int athenaPoolTimeOut;
    private Set<String> autoCommitUnsupportedDrivers = new HashSet<String>(); // protect from NPE
    private Map<String, String> driverAuthMethMap = new HashMap<String, String>(); // protect from NPE
    private Map<String, String> driverNoAuthMethMap = new HashMap<String, String>(); // protect from NPE
    private boolean defaultReadOnly = true;
	private boolean defaultAutoCommit = false;
	private String privateKeyTempDir;
	private RepositoryService repositoryService;
	protected MessageSource messageSource;
	@Autowired(required = false)
	protected Tracer tracer;

	public JdbcReportDataSourceServiceFactory () {
		poolDataSources = new PooledDataSourcesCache();
		//clean up google big query progress JDBC schema in temp folder after re-starting tomcat
		cleanup("gbq_", System.getProperty("java.io.tmpdir"));
	}

    protected TimeZone getTimeZoneByDataSourceTimeZone(String dataSourceTimeZone) {
        String timezoneId = dataSourceTimeZone == null ? "" : dataSourceTimeZone;
        return timezoneId.isEmpty() ? TimeZone.getDefault() : TimeZone.getTimeZone(timezoneId);
    }

	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof JdbcReportDataSource)) {
			throw new JSException("jsexception.invalid.jdbc.datasource", new Object[] {reportDataSource.getClass()});
		}
		JdbcReportDataSource jdbcDataSource = updateJdbcReportDataSource((JdbcReportDataSource) reportDataSource);
		DataSource dataSource = getPoolDataSource(jdbcDataSource.getDriverClass(), jdbcDataSource.getConnectionUrl(),
		jdbcDataSource.getUsername(), jdbcDataSource.getPassword());
		return getDataDataServiceInstance(dataSource, getTimeZoneByDataSourceTimeZone(jdbcDataSource.getTimezone()));
	}

	protected JdbcReportDataSource updateJdbcReportDataSource(JdbcReportDataSource jdbcDataSource) {
		if (getProfileAttributesResolver() != null) {
			JdbcReportDataSource tmpJdbcDataSource = getProfileAttributesResolver().mergeResource(jdbcDataSource);
			if (tmpJdbcDataSource != null) jdbcDataSource = tmpJdbcDataSource;
		}
		String driverClass = jdbcDataSource.getDriverClass();
		String userName = jdbcDataSource.getUsername();
		String password = jdbcDataSource.getPassword();
		String connectionUrl = jdbcDataSource.getConnectionUrl();
		if (driverClass.startsWith("tibcosoftware.jdbc")) {
			userName = (( (userName != null) && (!userName.isEmpty())) ? userName : "dummy");
			password = (( (password != null) && (!password.isEmpty())) ? password : "dummy");
		} else if (driverAuthMethMap.get(driverClass) != null) {
			if (connectionUrl.toLowerCase().indexOf("authmech") < 0) {
				String authMech = "";
				if (( userName != null) && (!userName.isEmpty())) {
					authMech = driverAuthMethMap.get(driverClass);
				} else {
					authMech = driverNoAuthMethMap.get(driverClass);
					if (!authMech.equals("0")) userName = "anonymous";
				}

				connectionUrl = connectionUrl + (connectionUrl.endsWith(";") ? "authmech=" : ";authmech=") + authMech;
			}
		}
		if(driverClass.equals(TibcoDriverManagerImpl.GOOGLE_BIGQUERY_PROGRESS_DRIVER_CLASS) || driverClass.equals(GOOGLE_BIG_QUERY_SIMBA_DRIVER_NAME)) {
			String privateKeyResourcePath ="";
			String connParams[] = connectionUrl.split(";");
			String configFileName = "gbq_";
			for(String param : connParams){
				if(param.startsWith(GOOGLE_BIG_QUERY_PROGRESS_PRIVATE_KEY) || param.startsWith(GOOGLE_BIG_QUERY_SIMBA_PRIVATE_KEY)){
          			privateKeyResourcePath = param.split("=")[1];
				}
				if (param.startsWith(GOOGLE_BIG_QUERY_PROGRESS_PROJECT) || param.startsWith(GOOGLE_BIG_QUERY_PROGRESS_DATASET) || param.startsWith(GOOGLE_BIG_QUERY_PROGRESS_SERVICE_ACCOUNT)) {
					configFileName = configFileName + param.split("=")[1] + "-";
				}
			}
			configFileName = configFileName.replace(".","-");
			if (configFileName.endsWith("-")) {
				configFileName = configFileName.substring(0, configFileName.lastIndexOf("-"));
			}
			String privateKeyFilePath = saveResourceToDisk(privateKeyResourcePath);
			if (privateKeyFilePath != null)
				connectionUrl = connectionUrl.replace(privateKeyResourcePath, privateKeyFilePath);
			    connectionUrl = connectionUrl+";schemaMap="+getPrivateKeyTempDir()+"\\"+configFileName+".config";
		}

		connectionUrl = getRuntimeConnectionURL(connectionUrl);
		jdbcDataSource.setConnectionUrl(connectionUrl);
		jdbcDataSource.setUsername(userName);
		jdbcDataSource.setPassword(password);
		return jdbcDataSource;
	}

	private String saveResourceToDisk(String privateKeyPath){
		if(privateKeyPath.contains(getPrivateKeyTempDir())){
			return privateKeyPath;
		}
    long now = System.currentTimeMillis();
		String absoluteFileLocation = null;
    try {
      FileResource fileResource = (FileResource) repositoryService.getResource(null, privateKeyPath, com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource.class);
      if(fileResource==null)
        throw new JSResourceNotFoundException(messageSource.getMessage("service.account.private.key.path.does.not.exist", null, LocaleContextHolder.getLocale() ));
      String key = fileResource.getURIString() + "_" + fileResource.getCreationDate() + "_" + fileResource.getUpdateDate();
      String privateKeyPrefix = "gbq_"+ GOOGLE_BIG_QUERY_PROGRESS_PRIVATE_KEY + "-" + key.hashCode();
			File outputLocation = new File(privateKeyTempDir, privateKeyPrefix + ".json");
      // if file exists, return existing private key path
      if (outputLocation.exists()) {
        return outputLocation.getAbsolutePath();
      }
      String decodedData = null;
      try {
        // get file data
        String encodedData = new String(
            repositoryService.getResourceData(null, fileResource.getURIString()).getData());
        // decode if encrypted
        decodedData = PasswordCipherer.getInstance().decodePassword(encodedData);
      } catch (JSResourceNotFoundException e) {
        log.error(messageSource.getMessage("failed.read.private.key.from.repository", null, LocaleContextHolder.getLocale() ));
      } catch (DataAccessResourceFailureException e) {
				log.warn(messageSource.getMessage("failed.to.decrypt.private.key", null, LocaleContextHolder.getLocale() ));
			} catch (Exception e) {
				log.error(e.getMessage());
			}

      try {
        FileOutputStream output = new FileOutputStream(outputLocation);
        output.write(decodedData.getBytes(), 0, decodedData.length());
        output.close();
        outputLocation.deleteOnExit();
        return outputLocation.getAbsolutePath();
      } catch (IOException e) {
        log.error(e.getMessage());
      }

    } catch(JSResourceNotFoundException ex){
			log.error(ex.getMessage());
			throw ex;
		} catch(Exception ex){
    	log.error(ex.getMessage());
		} finally {
      releaseExpiredPools(now);
    }
		return absoluteFileLocation;
  }

    protected JdbcDataSourceService getDataDataServiceInstance(DataSource dataSource, TimeZone timezone) {
        return new JdbcDataSourceService(dataSource, timezone).withTracer(tracer);
    }

	protected DataSource getPoolDataSource(String driverClass, String url, String username, String password) {
		long now = System.currentTimeMillis();
		releaseExpiredPools(now);
			
		Object poolKey = createJdbcPoolKey(driverClass, url, username, password);
		PooledDataSource dataSource;
		synchronized (poolDataSources.pooledObjectCache) {
			dataSource = poolDataSources.get(poolKey, now);
			if (dataSource == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating connection pool for " + poolKey + ".");
				}
				boolean isAutoCommit = (getAutoCommitUnsupportedDrivers().contains(driverClass) ? true : defaultAutoCommit);
				dataSource = pooledJdbcDataSourceFactory.createPooledDataSource(
						driverClass, url, username, password,
						isReadOnlyConnection(driverClass),
						isAutoCommit);
				poolDataSources.put(poolKey, dataSource, now);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Using cached connection pool for " + poolKey + ".");
				}
			}
		}

		return dataSource.getDataSource();
	}

	protected boolean isReadOnlyConnection(String driverClass){
		if(driverClass != null)
		  return driverClass.equals(SNOWFLAKE_DRIVER_CLASS) ? false : defaultReadOnly;
		return defaultReadOnly;
  }

	protected void releaseExpiredPools(long now) {
		List expired = null;
		synchronized (poolDataSources.pooledObjectCache) {
			if (getPoolTimeout() > 0) {
				expired = poolDataSources.removeExpired(now, getPoolTimeout(), getAthenaPoolTimeOut());
			}
		}

		if (expired != null && !expired.isEmpty()) {
			for (Iterator it = expired.iterator(); it.hasNext();) {
				PooledDataSource ds = (PooledDataSource) it.next();
				try {
					ds.release();
				} catch (Exception e) {
					log.error("Error while releasing connection pool.", e);
					// ignore
				}
			}
		}
	}

	public PooledJdbcDataSourceFactory getPooledJdbcDataSourceFactory() {
		return pooledJdbcDataSourceFactory;
	}

	public void setPooledJdbcDataSourceFactory(
			PooledJdbcDataSourceFactory jdbcDataSourceFactory) {
		this.pooledJdbcDataSourceFactory = jdbcDataSourceFactory;
	}

	protected Object createJdbcPoolKey(String driverClass,
			String url, String username, String password) {
		return new JdbcPoolKey(driverClass, url, username, password);
	}
	
	protected static class JdbcPoolKey {
		private final String driverClass;
		private final String url;
		private final String username;
		private final String password;
		private final int hash;

		public JdbcPoolKey(String driverClass, String url, String username,
				String password) {
			this.driverClass = driverClass;
			this.url = url;
			this.username = username;
			this.password = password;

			int hashCode = 559;
			if (driverClass != null) {
				hashCode += driverClass.hashCode();
			}
			hashCode *= 43;
			if (url != null) {
				hashCode += url.hashCode();
			}
			hashCode *= 43;
			if (username != null) {
				hashCode += username.hashCode();
			}
			hashCode *= 43;
			if (password != null) {
				hashCode += password.hashCode();
			}
			
			hash = hashCode;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof JdbcPoolKey)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			
			JdbcPoolKey key = (JdbcPoolKey) obj;
			return
				(driverClass == null ? key.driverClass == null : (key.driverClass != null && driverClass.equals(key.driverClass))) &&
				(url == null ? key.url == null : (key.url != null && url.equals(key.url))) &&
				(username == null ? key.username == null : (key.username != null && username.equals(key.username))) &&
				(password == null ? key.password == null : (key.password != null && password.equals(key.password)));
		}

		public int hashCode() {
			return hash;
		} 

		public String toString() {
			return "driver=\"" + driverClass + "\", url=\"" 
					+ url + "\", username=\"" + username + "\"";
		}
	}

	public int getPoolTimeout() {
		return poolTimeout;
	}

	public int getAthenaPoolTimeOut() {
		return athenaPoolTimeOut;
	}

	public void setAthenaPoolTimeOut(int athenaPoolTimeOut) {
		this.athenaPoolTimeOut = athenaPoolTimeOut;
	}

	public void setPoolTimeout(int poolTimeout) {
		this.poolTimeout = poolTimeout;
	}
	
	public boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}

	public void setDefaultReadOnly(boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

    public Set<String> getAutoCommitUnsupportedDrivers() {
        return autoCommitUnsupportedDrivers;
    }

    public void setAutoCommitUnsupportedDrivers(Set<String> autoCommitUnsupportedDrivers) {
        this.autoCommitUnsupportedDrivers = autoCommitUnsupportedDrivers;
    }

    public Map<String, String> getDriverAuthMethMap() {
        return driverAuthMethMap;
    }

    public void setDriverAuthMethMap(Map<String, String> driverAuthMethMap) {
        this.driverAuthMethMap = driverAuthMethMap;
    }

    public Map<String, String> getDriverNoAuthMethMap() {
        return driverNoAuthMethMap;
    }

    public void setDriverNoAuthMethMap(Map<String, String> driverNoAuthMethMap) {
        this.driverNoAuthMethMap = driverNoAuthMethMap;
    }

    private String getRuntimeConnectionURL(String connectionUrl) {
        // return relative temp folder to full path
        int index = connectionUrl.indexOf("=[TMPDIR/]");
        if (index > 0) {
            return connectionUrl.substring(0, index) + "=" + System.getProperty("java.io.tmpdir") + File.separator + connectionUrl.substring(index + 10);
        }
        return connectionUrl;
    }

	public ProfileAttributesResolver getProfileAttributesResolver() {
		return profileAttributesResolver;
	}

	public void setProfileAttributesResolver(ProfileAttributesResolver profileAttributesResolver) {
		this.profileAttributesResolver = profileAttributesResolver;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public String getPrivateKeyTempDir() {
		return privateKeyTempDir;
	}

	public void setPrivateKeyTempDir(String privateKeyTempDir) {
		this.privateKeyTempDir = privateKeyTempDir;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void cleanup(String filePrefix, String schemaDefinitionDirectory) {
		File schemaDirectory = new File(schemaDefinitionDirectory);
		File[] deleteFileList = schemaDirectory.listFiles(new FileNameFilterImpl(filePrefix));
		if (deleteFileList == null) return;
		for (File deleteFile : deleteFileList) {
			try {
				deleteFile.delete();
			} catch (Exception ex) {
				log.debug("Fail to delete JDBC schema: " + deleteFile.getAbsolutePath(), ex);
			}
		}
	}

	class FileNameFilterImpl implements FilenameFilter {

		private String schemaPrefix;

		public FileNameFilterImpl(String schemaPrefix) {
			this.schemaPrefix = schemaPrefix.toLowerCase();
		}

		public boolean accept(File dir, String name) {
			return (name.toLowerCase().startsWith(schemaPrefix));
		}
	}
}
