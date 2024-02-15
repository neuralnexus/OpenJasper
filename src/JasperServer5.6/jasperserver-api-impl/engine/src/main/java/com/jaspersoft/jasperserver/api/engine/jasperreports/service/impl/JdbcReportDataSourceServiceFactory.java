/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javax.sql.DataSource;

import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectCache;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JdbcReportDataSourceServiceFactory.java 48307 2014-08-15 21:38:37Z ichan $
 */
public class JdbcReportDataSourceServiceFactory implements ReportDataSourceServiceFactory {

	private static final Log log = LogFactory.getLog(JdbcReportDataSourceServiceFactory.class);

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
		
		public List removeExpired(long now, int timeout) {
            List expired = new ArrayList();
            List<PooledObjectEntry> expiredEntries = pooledObjectCache.removeExpired(now, timeout);
            for (PooledObjectEntry objectEntry : expiredEntries) expired.add(((DataSourceEntry)objectEntry).ds);
			return expired;
		}

	}

	private PooledJdbcDataSourceFactory pooledJdbcDataSourceFactory;
	private PooledDataSourcesCache poolDataSources;
	private int poolTimeout;
	
	private boolean defaultReadOnly = true;
	private boolean defaultAutoCommit = false;

	public JdbcReportDataSourceServiceFactory () {
		poolDataSources = new PooledDataSourcesCache();
	}

    protected TimeZone getTimeZoneByDataSourceTimeZone(String dataSourceTimeZone) {
        String timezoneId = dataSourceTimeZone == null ? "" : dataSourceTimeZone;
        return timezoneId.isEmpty() ? TimeZone.getDefault() : TimeZone.getTimeZone(timezoneId);
    }

	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof JdbcReportDataSource)) {
			throw new JSException("jsexception.invalid.jdbc.datasource", new Object[] {reportDataSource.getClass()});
		}
		JdbcReportDataSource jdbcDataSource = (JdbcReportDataSource) reportDataSource;
	
		DataSource dataSource = getPoolDataSource(jdbcDataSource.getDriverClass(), jdbcDataSource.getConnectionUrl(), jdbcDataSource.getUsername(), jdbcDataSource.getPassword());

		return new JdbcDataSourceService(dataSource, getTimeZoneByDataSourceTimeZone(jdbcDataSource.getTimezone()));
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
				dataSource = pooledJdbcDataSourceFactory.createPooledDataSource(
						driverClass, url, username, password,
						defaultReadOnly, defaultAutoCommit);
				poolDataSources.put(poolKey, dataSource, now);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Using cached connection pool for " + poolKey + ".");
				}
			}
		}

		return dataSource.getDataSource();
	}

	protected void releaseExpiredPools(long now) {
		List expired = null;
		synchronized (poolDataSources.pooledObjectCache) {
			if (getPoolTimeout() > 0) {
				expired = poolDataSources.removeExpired(now, getPoolTimeout());
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
}
