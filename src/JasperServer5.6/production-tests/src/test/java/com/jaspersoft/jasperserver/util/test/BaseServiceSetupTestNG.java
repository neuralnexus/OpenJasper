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

package com.jaspersoft.jasperserver.util.test;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.TestingAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JdbcReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JndiJdbcReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JndiJdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.VirtualReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.TenantImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ObjectPermissionServiceImpl;
import com.jaspersoft.jasperserver.common.test.MockServletContextLoader;
import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.export.CommandBean;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.ParametersImpl;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;

/**
 * @author srosen
 *
 * The base class to support all integration tests based on the TestNG framework.
 */
@ContextConfiguration(loader = MockServletContextLoader.class,locations={"classpath:applicationContext*.xml", "classpath:prod-tests-applicationContext-testProviders.xml"})
public class BaseServiceSetupTestNG extends AbstractTestNGSpringContextTests {

    // MOD: the variable below are from the removed BaseExportTestCase class
    protected static final String FORWARD_SLASH = "/";
    protected static final String TEST_BASE_DIR = "target";
    protected static final String EXPORT_COMMAND_BEAN_NAME = "exportCommandBean";
    protected static final String IMPORT_COMMAND_BEAN_NAME = "importCommandBean";
    protected static final String PARAM_EXPORT_DIR = "output-dir";
    protected static final String PARAM_EXPORT_URIS = "uris";
    protected static final String PARAM_EXPORT_REPORT_JOB_URIS = "report-jobs";
    protected static final String PARAM_EXPORT_USERS = "users";
    protected static final String PARAM_EXPORT_ROLES = "roles";
    protected static final String PARAM_IMPORT_DIR = "input-dir";
    protected static final String PARAM_IMPORT_ZIP = "input-zip";
    protected static final String PARAM_EXPORT_ZIP = "output-zip";
    protected static final String PARAM_EVERYTHING = "everything";
    protected static final String OUTPUT_ZIP_FILE_START = "js-catalog";
    protected static final String OUTPUT_ZIP_MINIMAL = "minimal";
    protected static final String OUTPUT_DASH = "-";
    protected static final String OUTPUT_DOT = ".";
    protected static final String OUTPUT_EDITION_CE = "ce";
    protected static final String OUTPUT_EDITION_PRO = "pro";
    protected static final String OUTPUT_ZIP_EXT = "zip";
    protected static final String PARAM_IMPORT_PREPEND_PATH = "prepend-path";
    protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
    protected static final Random random = new Random(System.currentTimeMillis());
    private List exportFolders = new ArrayList();

    public static final String USER_JASPERADMIN = "jasperadmin";
    public static final String USER_CALIFORNIA_USER = "CaliforniaUser";
    public static final String USER_CALIFORNIA_USER_FULLNAME = "California User";
    public static final String USER_JOEUSER = "joeuser";
    public static final String USER_JOEUSER_FULLNAME = "Joe User";
    public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public static final String USER_ANONYMOUS = "anonymousUser";
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_SUPERMART_MANAGER = "ROLE_SUPERMART_MANAGER";
    protected static final String ROLE_PORTLET = "ROLE_PORTLET";
    protected static final String HOLIDAY_CALENDAR_NAME = "New Years Days";

	protected ExecutionContext m_exContext = new ExecutionContextImpl();

    private Properties m_jdbcProps;
    private JdbcReportDataSourceServiceFactory m_jdbcDataSourceServiceFactory;
    private JndiJdbcReportDataSourceServiceFactory m_jndiJdbcDataSourceServiceFactory;
    private RepositoryService m_repositoryService;
    private RepositoryService m_unsecureRepositoryService;
    private UserAuthorityService m_userAuthorityService;
    private ProfileAttributeService m_profileAttributeService;
	private ObjectPermissionService m_objectPermissionService;
	private PermissionsService m_permissionsService;
	private ReportSchedulingService m_reportSchedulingService;
	private OlapConnectionService m_olapConnectionService;
    private EngineService m_engineService;
	private TenantService m_tenantService;
    private ReportJobsScheduler reportScheduler;

    private MessageSource messages;

    protected final Log m_logger = LogFactory.getLog(BaseServiceSetupTestNG.class);

    private String m_multiTenancyPrefix = null;

    public BaseServiceSetupTestNG() {
    }

    /**
     * Check for the multi-tenancy folder structure being in place.
     * If the multi-tenancy folder structure is in place, then ruturn a string
     * representing this folder structure URI.
     * Otherwise, return an empty string.
     *
     * @return String
     */
    protected String getMultiTenancyPrefix() {

        if (m_multiTenancyPrefix == null) {

            m_multiTenancyPrefix = "/organizations/organization_1";
            Folder f = getRepositoryService().getFolder(null, m_multiTenancyPrefix);

            if (f == null) {
                m_multiTenancyPrefix = "";
            }
            m_logger.info("getMultiTenancyPrefix(): MT Prefix=<" + m_multiTenancyPrefix + ">");
        }
        return m_multiTenancyPrefix;
    }

    /**
     * Check for and return the multi-tenancy prefix folder path string.
     * If we do not find the multi-tenancy structure in place then return
     * the root folder URI string which is "/"
     *
     * @return String
     */
    protected String getMultiTenancyPrefixOrRoot() {

        if (m_multiTenancyPrefix == null) {

            m_multiTenancyPrefix = "/organizations/organization_1";
            Folder f = getRepositoryService().getFolder(null, m_multiTenancyPrefix);

            if (f == null) {
                m_multiTenancyPrefix = "/";
            }
            m_logger.info("getMultiTenancyPrefixOrRoot(): MT Prefix=<" + m_multiTenancyPrefix + ">");
        }
        return m_multiTenancyPrefix;
    }

    /**
     * MOD: Adding methods and variable from BaseExportTestCase
     *      The BaseExportTestCase class can then be removed
     */
    protected Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

    protected void performExport(Parameters params) {
		CommandBean exporter = (CommandBean) getBean(EXPORT_COMMAND_BEAN_NAME);
		exporter.process(params);
	}

	protected void performImport(Parameters params) {
		CommandBean importer = (CommandBean) getBean(IMPORT_COMMAND_BEAN_NAME);
		importer.process(params);
	}

	protected Parameters createParameters() {
		return new ParametersImpl();
	}

    public String getNameFromURI(String uri) {
        String[] pathParts = uri.split("/");
        return pathParts[pathParts.length - 1];
    }

    protected FileResource loadAndPrepareFileResource(String resourcePath, String uri, String fileType) throws Exception {
        String resultString = loadFile(resourcePath);

        String name = getNameFromURI(uri);
        return prepareFile(name, name, name, resultString, fileType);
    }

    protected String loadFile(String fileName) throws IOException {
        InputStream is = getClass().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        char[] cbuf = new char[1024];
        int k = 0;
        while ( (k = br.read(cbuf)) !=  -1 ) {
            sb.append(cbuf, 0, k);
        }
        br.close();
        return sb.toString();
    }

    protected FileResource prepareFile(String name, String label, String description, String content, String fileType) {

        FileResource schemaRes = (FileResource) getRepositoryService().newResource(m_exContext, FileResource.class);

        schemaRes.setFileType(fileType);
        schemaRes.setName(name);
        schemaRes.setLabel(label);
        schemaRes.setDescription(description);


        // Update/set the data
        schemaRes.setData(content.getBytes());

        return schemaRes;

    }

    // MOD: remove this method
    //      we should know whether a folder exists or not so should not
    //      need to use this method
    protected Folder ensureParentFolderExists(String uri) {

    	String[] pathParts;
        StringBuffer folderPath = getParentURI(uri);

    	Folder got = getRepositoryService().getFolder(m_exContext, folderPath.toString());

    	if (got != null) {
    		m_logger.debug("Folder: " + folderPath.toString() + " exists");
    		return got;
    	}

    	m_logger.debug("Creating Folder: " + folderPath.toString());

    	pathParts = folderPath.toString().split("/");

    	folderPath = new StringBuffer();
    	Folder parentFolder = null;

    	for (int i = 0; i < pathParts.length; i++) {
    		if (pathParts[i].length() == 0) {
    			continue;
    		}

    		folderPath.append("/").append(pathParts[i]);

    		got = getRepositoryService().getFolder(m_exContext, folderPath.toString());

        	if (got == null) {
                got = new FolderImpl();
                got.setName(pathParts[i]);
                got.setLabel(pathParts[i]);
                got.setDescription(pathParts[i] + " description");
                got.setParentFolder(parentFolder);
                getRepositoryService().saveFolder(null, got);
        	}

        	parentFolder = got;
    	}

    	return got;
    }

    public StringBuffer getParentURI(String uri) {
        String[] pathParts = uri.split("/");

    	StringBuffer folderPath = new StringBuffer();

    	for (int i = 0; i < pathParts.length - 1; i++) {
    		if (pathParts[i].length() == 0) {
    			continue;
    		}
    		folderPath.append("/").append(pathParts[i]);
    		//log.debug("" + i + ": " + folderPath.toString());
    	}
        return folderPath;
    }

    public Properties getJdbcProps() {
        return m_jdbcProps;
    }

    @javax.annotation.Resource(name = "jdbcProps")
    public void setJdbcProps(Properties jdbcProps) {
		m_logger.info("setJdbcProps() called");
    	this.m_jdbcProps = jdbcProps;
    }

    /**
     * Returns database product name to decide which schema should be used
     * (database specific schemas with upper case for Oracle and special date functions for SQLServer)
     * Suppose to use it instead of
     *      test.databaseFlavor=oracle
     *      test.foodmart.upperCaseNames=true
     * @param dsUri
     *      datasource uri
     * @return String
     *      database vendor name, has to be one of listed in applicationContext-semanticLayer.xml beanId = sqlGeneratorFactory
     */
    public String getDatabaseProductName(String dsUri) {
        ReportDataSource ds = (ReportDataSource) getRepositoryService().getResource(m_exContext, dsUri);
        ReportDataSourceService jdss;
        if (ds instanceof JndiJdbcReportDataSource) {
            jdss = m_jndiJdbcDataSourceServiceFactory.createService(ds);
        } else {
            jdss = m_jdbcDataSourceServiceFactory.createService(ds);
        }
        HashMap params = new HashMap();
        jdss.setReportParameterValues(params);
        try {
            Connection conn = (Connection) params.get(JRParameter.REPORT_CONNECTION);
            DatabaseMetaData metadata = conn.getMetaData();
            jdss.closeConnection();
            return metadata.getDatabaseProductName();
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Cannot get database vendor name", ex);
        }
    }

    // MOD: adding additional getDatabaseProductName method because the Core CE set of resources
    //      does not have an existing Datasource in the repository to feed into the method above
    //      This is support for consolidating Export operations
    public String getDatabaseProductNameFromProp() {

    	if (getJdbcProps().getProperty("test.databaseFlavor") != null &&
		   (getJdbcProps().getProperty("test.databaseFlavor").startsWith("postgresql") ||
				getJdbcProps().getProperty("test.databaseFlavor").startsWith("oracle") ||
				getJdbcProps().getProperty("test.databaseFlavor").startsWith("sqlserver") ||
				getJdbcProps().getProperty("test.databaseFlavor").startsWith("mysql") ||
				getJdbcProps().getProperty("test.databaseFlavor").startsWith("db2"))) {

    		m_logger.info("Info: Database Flavor is: " + getJdbcProps().getProperty("test.databaseFlavor"));
    		return getJdbcProps().getProperty("test.databaseFlavor");
    	} else {
    		m_logger.warn("WARNING: Unknown database type (flavor): " + getJdbcProps().getProperty("test.databaseFlavor"));
    		m_logger.warn("WARNING: setting databaseFlavor to: mysql");
    		m_logger.warn("WARNING: output js-catalog file will be named -mysql");
    		return "mysql";
    	}
	}

    /**
     * Checks if foodmart.upperCaseNames property is set
     * @return boolean
     * @throws Exception 
     */
    public boolean useUpperCaseNames() throws Exception {
        return Boolean.parseBoolean(getJdbcProps().getProperty("foodmart.upperCaseNames"));
    }
    

    public JdbcReportDataSourceServiceFactory getJdbcDataSourceServiceFactory() {
        return m_jdbcDataSourceServiceFactory;
    }

    @javax.annotation.Resource(name = "jdbcDataSourceServiceFactory")
    public void setJdbcDataSourceServiceFactory(JdbcReportDataSourceServiceFactory jdbcDataSourceServiceFactory) {
		m_logger.info("setJdbcDataSourceServiceFactory() called");
        this.m_jdbcDataSourceServiceFactory = jdbcDataSourceServiceFactory;
    }

    public JndiJdbcReportDataSourceServiceFactory getJndiJdbcDataSourceServiceFactory() {
        return m_jndiJdbcDataSourceServiceFactory;
    }

    @javax.annotation.Resource(name = "jndiJdbcDataSourceServiceFactory")
    public void setJndiJdbcDataSourceServiceFactory(JndiJdbcReportDataSourceServiceFactory jndiJdbcDataSourceServiceFactory) {
		m_logger.info("setJndiJdbcDataSourceServiceFactory() called");
        this.m_jndiJdbcDataSourceServiceFactory = jndiJdbcDataSourceServiceFactory;
    }

	protected JdbcReportDataSource createJdbcReportDataSourceFromProperties(String prefix) throws Exception {
			    JdbcReportDataSourceImpl ds = new JdbcReportDataSourceImpl();
			    ds.setDriverClass(getJdbcProps().getProperty(prefix + ".jdbc.driverClassName"));
			    ds.setConnectionUrl(getJdbcProps().getProperty(prefix + ".jdbc.url"));
			    ds.setUsername(getJdbcProps().getProperty(prefix + ".jdbc.username"));

				String passwd = getJdbcProps().getProperty(prefix + ".jdbc.password");
				if (EncryptionEngine.isEncrypted(passwd)) {
					KeystoreManager ksManager = KeystoreManager.getInstance();
					passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
				}
				ds.setPassword(passwd);

			    if (ds.getDriverClass() == null || ds.getConnectionUrl() == null || ds.getUsername() == null || ds.getPassword() == null) {
			    	throw new IllegalArgumentException("some jdbc props missing for prefix " + prefix);
			    }
			    return ds;
			}

    protected JdbcReportDataSource createAndSaveJdbcDSFromProps(String dsURI, String prefix) throws Exception {
        return createAndSaveJdbcDSFromProps(dsURI, prefix, prefix);
    }

	protected JdbcReportDataSource createAndSaveJdbcDSFromProps(String dsURI, String prefix, String name) throws Exception {
		JdbcReportDataSource ds = (JdbcReportDataSource) getRepositoryService().getResource(m_exContext, dsURI);
	
	    // Create it if is not there
	    if (ds == null) {
 	    	ds = createJdbcReportDataSourceFromProperties(prefix);
	        ds.setName(name);
	        ds.setParentFolder(ensureParentFolderExists(dsURI));
		    ds.setLabel(name);
		    getRepositoryService().saveResource(m_exContext, ds);
	    }
	    return ds;
	}

    /**
     * Get a ReportDataSource. Fire an assert if the requested datasource is not found.
     * @param dsURI
     * @return
     */
    protected ReportDataSource getReportDataSource(String dsURI) {
        ReportDataSource ds = (ReportDataSource) getRepositoryService().getResource(m_exContext, dsURI);
        assertFalse("foobar", (ds == null));
        return ds;
    }

    /**
     * Get a JndiJdbcReportDataSource. Fire an assert if the requested datasource is not found.
     * @param dsURI
     * @return
     */
    protected JndiJdbcReportDataSource getJndiDS(String dsURI) {
        JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) getReportDataSource(dsURI);
        return ds;
    }


    protected JndiJdbcReportDataSource createAndSaveJndiDSFromProps(String dsURI, String prefix, String jndiName) throws Exception {
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) getRepositoryService().getResource(m_exContext, dsURI);
	    // Create it if is not there
	    if (ds == null) {
	        ds = new JndiJdbcReportDataSourceImpl();
            ds.setJndiName(jndiName);
	        ds.setName(prefix);
	        ds.setParentFolder(ensureParentFolderExists(dsURI));
		    ds.setLabel(prefix);
		    getRepositoryService().saveResource(m_exContext, ds);
	    }
	    return ds;
	}

    protected VirtualReportDataSource createAndSaveVirtualDSFromProps(String dsURI, String name, Map<String, ResourceReference> uriMap, Set<String> selectedSchemas) throws Exception {
		VirtualReportDataSource ds = (VirtualReportDataSource) getRepositoryService().getResource(m_exContext, dsURI);
	    // Create it if is not there
	    if (ds == null) {
	        ds = new VirtualReportDataSourceImpl();
            ds.setDataSourceUriMap(uriMap);
            if (selectedSchemas != null) ds.setSchemas(selectedSchemas);
	        ds.setName(name);
	        ds.setParentFolder(ensureParentFolderExists(dsURI));
		    ds.setLabel(name);
		    getRepositoryService().saveResource(m_exContext, ds);
	    }
	    return ds;
	}

	protected void updateResource(Resource resource, String label, String desc) throws Exception {
		resource.setLabel(label);
		resource.setDescription(desc);
	}
	
	protected ProfileAttribute createTestAttr(Object principal, String name, String value) {
	    ProfileAttribute attr = getProfileAttributeService().newProfileAttribute( null );
	    attr.setPrincipal( principal );
	    attr.setAttrName( name );
	    attr.setAttrValue( value );
	    return attr;
	}

	protected Authentication setAuthenticatedUser(String username) {
		m_logger.info("setAuthenticatedUser() called");
	    UserDetails userDetails =
	        ((UserDetailsService)getUserAuthorityService()).loadUserByUsername(username);
	    Authentication aUser =
	        new TestingAuthenticationToken(userDetails,
	                       userDetails.getPassword(),
	                       userDetails.getAuthorities());
	    aUser.setAuthenticated(true);
	    SecurityContextHolder.getContext().setAuthentication(aUser);
	
	    m_logger.debug("Principal: " + aUser.getPrincipal());
	    return aUser;
	}

    /*
    * create a role
    *
    * MOD: This method needs a tenantId parameter.
    *      - tenantId can be null (thus superuser level)
    *      - tenantId can be set to an existing tenant such as organization_1
    */
    public Role createRole(String roleName) {
		Role role = getUserAuthorityService().getRole(null, roleName);

        // check for role being null (ie shouldn't already exist)
        assertTrue("Error: trying to create a role that already exists, roleName="
                   + roleName, (role == null));

		role = getUserAuthorityService().newRole(null);
		role.setRoleName(roleName);
		role.setExternallyDefined(false);
		getUserAuthorityService().putRole(null, role);

		return role;
	}

    /*
    * add a role to an existing user
     */
    public Role addRole(User user, String roleName) {
		Role role = getUserAuthorityService().getRole(null, roleName);
		getUserAuthorityService().addRole(null, user, role);
		return role;
	}

    /*
    * remove a role from an existing user
     */
    public void removeRole(User user, String roleName) {
		Role role = getUserAuthorityService().getRole(null, roleName);
		if (role != null) {
			getUserAuthorityService().removeRole(null, user, role);
		} else {
            m_logger.warn("removeRole : Could not find role " + roleName + " for user " + user);
        }
		return;
	}

    /*
    * delete a role
     */
    public void deleteRole(String roleName) {
		Role role = getUserAuthorityService().getRole(null, roleName);
		if (role != null) {
			getUserAuthorityService().   deleteRole(null, roleName);
		} else {
            m_logger.warn("deleteRole : Could not find role " + roleName + " to delete");
        }
		return;
	}

	public Role getRole(String roleName) {
	    Role r = getUserAuthorityService().getRole(null, roleName);
	    if (r == null) {
	        r = getUserAuthorityService().newRole(null);
	        r.setRoleName(roleName);
	        r.setExternallyDefined(false);
	        getUserAuthorityService().putRole(null, r);
	    }
	    return r;
	}

    public User getUser(String username) {
        User workingUser = getUserAuthorityService().getUser(null, username);
        return workingUser;
    }

    public void deleteUser(String username) {
        getUserAuthorityService().deleteUser(null, username);
        return;
    }

    // MOD: there is also a method in BaseSemanticLayerTest which creates a multi-tenant
    //      user (ie a user that has tenantId set)
    // NOTE: This method is not correct because the password is set to "userName"
    //       Needs proper cleanup
    public User createUser(String userName, String password, String fullName) {
        User workingUser = getUserAuthorityService().getUser(null, userName);

        // check for user being null (ie shouldn't already exist)
        assertTrue("Error: trying to create a user that already exists, username="
                   + userName, (workingUser == null));

        workingUser = getUserAuthorityService().newUser(null);
        workingUser.setUsername(userName);
        if (password != null) {
            workingUser.setPassword(userName);
        } else {
            workingUser.setPassword("");
        }
        workingUser.setFullName(fullName);
        workingUser.setEnabled(true);
        workingUser.setPreviousPasswordChangeTime(new Date());
        getUserAuthorityService().putUser(null, workingUser);
        return workingUser;
    }

    // MOD: adding a createFolder() method to replace the getOrCreateFolder methods
    //      NOTE: there is already a createFolder method in SetupMTFolderStructureTest
    //            and this creates a conflict with the method in this class
    //            Needs to be straightened out. For now, I am renaming the method
    //            in this class so that there is no conflict.
    //      Also, it is better to have the parameter order as:
    //            (name, label, desc, baseFolderURI)

    /**
     * Create a folder. Fire an assert if the folder already exists
     *
     * @param baseFolderURI
     * @param name
     * @param label
     * @param description
     * @return
     */
    public Folder createFolder_BaseService(String baseFolderURI, String name, String label, String description) {

        String folderURI = baseFolderURI + FORWARD_SLASH + name;
        Folder folder = getRepositoryService().getFolder(null, folderURI);
        assertTrue("Error this folder already exists, folderURI=" + folderURI, (folder == null));

        folder = new FolderImpl();
        folder.setName(name);
        folder.setLabel(label);
        folder.setDescription(description);
        Date now = new Date();
        folder.setCreationDate(now);
        folder.setUpdateDate(now);
        folder.setParentFolder(baseFolderURI);
        getRepositoryService().saveFolder(null, folder);

        return folder;
    }

    public Folder getOrCreateFolder(String baseFolderURI, String name, String label) {
        Folder folder = getRepositoryService().getFolder(null, baseFolderURI + "/" + name);
        if (folder == null) {
            folder = new FolderImpl();
            folder.setName(name);
            folder.setLabel(label);
            Date now = new Date();
            folder.setCreationDate(now);
            folder.setUpdateDate(now);
            folder.setParentFolder(baseFolderURI);
            getRepositoryService().saveFolder(null, folder);
        }
        return folder;
    }

    public Folder getOrCreateFolder(String baseFolderURI, String name) {
        return getOrCreateFolder(baseFolderURI, name, name);
    }

    // MOD: this getFolder() method is first step to replace getOrCreateFolder() methods
    /**
     * Get a folder. Assert Fail if folder does not exist
     * @param folderURI
     * @return
     */
    public Folder getFolder(String folderURI) {
       Folder folder = getRepositoryService().getFolder(null, folderURI);

        // fail if folder does not exist
        assertFalse("Error trying to get a folder, probably does not exist, folderURI="
                + folderURI, (folder == null));

        return folder;
    }


    public void deleteFolderIfExists(String folderURI) {
        if (getRepositoryService().getFolder(null, folderURI) != null) getRepositoryService().deleteFolder(null, folderURI);
    }

    public void deleteFolder(String folderURI) {
        getRepositoryService().deleteFolder(null, folderURI);
    }

    /*
     * create the root folder if it's not there
     * This was formerly only in CoreDataCreateTestNG but we should be able to have stdalone tests
     */
    protected Folder createRootFolderIfMissing() {
    	String rootFolderName = Folder.SEPARATOR;
        Folder root = getUnsecureRepositoryService().getFolder(null, rootFolderName);
        if (root == null) {
            root = new FolderImpl();
    		root.setCreationDate(new Date());
    		root.setUpdateDate(new Date());
    		root.setName(Folder.SEPARATOR);
    		root.setLabel("root");
    		root.setDescription("Root of the folder hierarchy");
    		root.setParentFolder((Folder) null);
    		getUnsecureRepositoryService().saveFolder(null, root);
        }
        return root;
    }

	private FileResource saveOrUpdateFile(String uri, String name, String label,
			String description, String content, String fileType) {
			
			    FileResource schemaRes = (FileResource) getRepositoryService().getResource(m_exContext, uri);
			
			    // Create it if is not there
			    if (schemaRes == null) {
			
			    	m_logger.debug("File: " + uri + " being created");
			
			        schemaRes = prepareFile(name, label, description, content, fileType);
			        schemaRes.setParentFolder(ensureParentFolderExists(uri));
			
			        getRepositoryService().saveResource(m_exContext, schemaRes);
			    }
			
			    return schemaRes;
			}

	protected FileResource loadAndSaveFileResource(String resourcePath, String uri, String fileType) throws Exception {
	    String resultString = loadFile(resourcePath);
	
	    String name = getNameFromURI(uri);
	    return saveOrUpdateFile(uri, name, name, name, resultString, fileType);
	}
	

    /*
     * create a tenant for the root--used for standalone tests that can be run from a bare repo
     */
    protected void createTenantForRootIfMissing() {
    	Tenant rootTenant = getTenantService().getTenant(null, TenantService.ORGANIZATIONS);
    	if (rootTenant == null) {
	        // create root tenant
	        // setting empty strings in NOT NULL fields (fix for Oracle)
	        createTenant("", TenantService.ORGANIZATIONS, "root", TenantService.ORGANIZATIONS, " ", "/", "/", "default");
    	}
    }

    /*
     * create a tenant
     */
    protected void createTenant(String parentTenantId, String tenantId, String tenantName,
            String tenantDesc, String tenantNote, String relativeUri, String uri, String theme) {

        TenantImpl aTenant = new TenantImpl();

        if (!(TenantService.ORGANIZATIONS.equals(tenantId)) && !(TenantService.ORGANIZATIONS.equals(parentTenantId))) {
            tenantId = parentTenantId + "_" + tenantId;
        }

        aTenant.setParentId(parentTenantId);
        aTenant.setId(tenantId);
        aTenant.setAlias(tenantId);
        aTenant.setTenantName(tenantName);
        aTenant.setTenantDesc(tenantDesc);
        aTenant.setTenantNote(tenantNote);
        aTenant.setTenantUri(relativeUri);   // this is not a true repository URI (describes org hierarchy)
        aTenant.setTenantFolderUri(uri);
        aTenant.setTheme(theme);

        getTenantService().putTenant(null, aTenant);
    }


    public RepositoryService getRepositoryService() {
        return m_repositoryService;
    }

    @javax.annotation.Resource(name = "repositoryService")
    public void setRepositoryService(RepositoryService repository) {
		m_logger.info("setRepositoryService() called");
    	m_repositoryService = repository;
    }

    @javax.annotation.Resource(name = "unsecureRepositoryService")
	public void setUnsecureRepositoryService(RepositoryService unsecureRepositoryService) {
		m_logger.info("setUnsecureRepositoryService() called");
		this.m_unsecureRepositoryService = unsecureRepositoryService;
	}

	public RepositoryService getUnsecureRepositoryService() {
		return m_unsecureRepositoryService;
	}

    @javax.annotation.Resource(name = "userAuthorityService")
	public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
		m_logger.info("setUserAuthorityService() called");
		this.m_userAuthorityService = userAuthorityService;
	}

	public UserAuthorityService getUserAuthorityService() {
		return m_userAuthorityService;
	}

    @javax.annotation.Resource(name = "objectPermissionServiceUnsecure")
	public void setObjectPermissionService(ObjectPermissionService objectPermissionService) {
        m_logger.info("setObjectPermissionService() called");
		this.m_objectPermissionService = objectPermissionService;
	}

	public ObjectPermissionService getObjectPermissionService() {
		return m_objectPermissionService;
	}

    @javax.annotation.Resource(name = "permissionsService")
	public void setPermissionsService(PermissionsService permissionsService) {
        m_logger.info("setPermissionsService() called");
		this.m_permissionsService = permissionsService;
	}

	public PermissionsService getPermissionsService() {
		return m_permissionsService;
	}

	@javax.annotation.Resource(name = "reportSchedulingService")
	public void setReportSchedulingService(ReportSchedulingService reportSchedulingService) {
		m_logger.info("setReportSchedulingService() called");
		this.m_reportSchedulingService = reportSchedulingService;
	}

    public ProfileAttributeService getProfileAttributeService() {
		return m_profileAttributeService;
	}

	public ReportSchedulingService getReportSchedulingService() {
		return m_reportSchedulingService;
	}

    @javax.annotation.Resource(name = "olapConnectionService")
	public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
        m_logger.info("setOlapConnectionService() called");
		this.m_olapConnectionService = olapConnectionService;
	}

	public OlapConnectionService getOlapConnectionService() {
		return m_olapConnectionService;
	}

    protected void logPermission(ObjectPermission perm) {
        if (perm == null) return;
        Object rpt = perm.getPermissionRecipient();
        String name = "?";
        if (rpt instanceof Role) {
            Role r = (Role) rpt;
            name = r.getRoleName() + (r.getTenantId() == null ? "" : "|" + r.getTenantId());
        } else if (rpt instanceof User) {
            User u = (User) rpt;
            name = u.getUsername() + (u.getTenantId() == null ? "" : "|" + u.getTenantId());
        }
        m_logger.info("Creating permission : " + perm.getURI() + " will have mask " + perm.getPermissionMask() + " for " + name);
    }

	protected ObjectPermission createObjectPermission(String targetPath, Object recipient, int permissionMask) {
			    ObjectPermission permission = getObjectPermissionService().newObjectPermission(null);
			    permission.setURI("repo:" + targetPath);
			    permission.setPermissionRecipient(recipient);
			    permission.setPermissionMask(permissionMask);
			    ExecutionContext ectx = new ExecutionContextImpl();
			    ectx.getAttributes().add(ObjectPermissionServiceImpl.PRIVILEGED_OPERATION);
                logPermission(permission);
			    getObjectPermissionService().putObjectPermission(ectx, permission);
			    return permission;
	}

    protected void deleteObjectPermission(String targetPath, Object recipient) {

        ObjectPermission op = getObjectPermissionService().newObjectPermission(null);
        op.setURI(targetPath);
        op.setPermissionRecipient(recipient);

        m_logger.info("deleteObjectPermission: about to get: " + targetPath + ", recipient: " + recipient);
        ObjectPermission op2 = getObjectPermissionService().getObjectPermission(null, op);
        if (op2 != null) {
            m_logger.info("deleteObjectPermission: got " + op2 + ". about to delete: " + targetPath + ", recipient: " + recipient);
            getObjectPermissionService().deleteObjectPermission(null, op);
            m_logger.info("deleted permission for uri: " + targetPath + ", recipient: " + recipient);
        } else {
            m_logger.warn("Can't delete permission for uri: " + targetPath + ", recipient: " + recipient + " because it does not exist");
        }

    }

    @javax.annotation.Resource(name = "tenantService")
	public void setTenantService(TenantService tenantService) {
        m_logger.info("setTenantService() called");
		this.m_tenantService = tenantService;
	}

	public TenantService getTenantService() {
		return m_tenantService;
	}

    @javax.annotation.Resource(name = "profileAttributeService")
	public void setProfileAttributeService(
			ProfileAttributeService profileAttributeService) {
        m_logger.info("setProfileAttributeService() called");
		this.m_profileAttributeService = profileAttributeService;
	}

    @javax.annotation.Resource(name = "engineService")
	public void setEngineService(EngineService engineService) {
        m_logger.info("setEngineService() called");
		this.m_engineService = engineService;
	}

	public EngineService getEngineService() {
		return m_engineService;
	}

    public ReportJobsScheduler getReportScheduler() {
        return reportScheduler;
    }

    @javax.annotation.Resource(name = "reportScheduler")
    public void setReportScheduler(ReportJobsScheduler reportScheduler) {
        this.reportScheduler = reportScheduler;
    }

    public MessageSource getMessages() {
        return messages;
    }

    @javax.annotation.Resource(name = "messageSource")
    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public String msg(String text, Object ... info) {
        StringBuilder sb = new StringBuilder();

        Formatter formatter = new Formatter(sb, LocaleContextHolder.getLocale());
        formatter.format(text, info);

        return sb.toString();
    }
}
