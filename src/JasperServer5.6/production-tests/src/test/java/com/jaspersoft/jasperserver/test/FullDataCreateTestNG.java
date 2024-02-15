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
package com.jaspersoft.jasperserver.test;

import java.io.InputStream;
import java.util.*;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.impl.OlapConnectionServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclEntry;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

/**
 * @author srosen
 *
 * Creates the full production data for CE using the TestNG framework
 *
 * 2013-08-14: tkavanagh
 *   Removed sample report unit resources that were found in the /reports/samples folder. These
 *   used to be sample reports given the customers as standard samples. But these are now
 *   replaced with nicer reports in /public/samples as of the 5.5 release. The samples are now
 *   considered test resources for integration-tests. The are created and deleted in a different
 *   file. 
 */
public class FullDataCreateTestNG extends BaseServiceSetupTestNG {

    protected final Log m_logger = LogFactory.getLog(FullDataCreateTestNG.class);

    @BeforeClass()
	protected void onSetUp() throws Exception {
		m_logger.info("onSetUp() called");
	}

    @AfterClass()
	public void onTearDown() {
		m_logger.info("onTearDown() called");
	}

    /*
    * This method is the starting point for adding resources that comprise the Full production
    * data for the Community Edition (CE) product.
    *   Full Data == Sample Data
    */
    @Test()
	public void createFullDataResources() throws Exception {
        m_logger.info("createFullDataResources() called");

        addHibernateRepositoryDataSourceResources();
        addHibernateRepositoryReportResources();
        addContentRepositoryTestResources();
        addUserAuthorityServiceTestResources();
        addOlapConnectionResources();
    }


    private void addUserAuthorityServiceTestResources() {
        m_logger.info("addUserAuthorityServiceTestResources() called");

        User newUser = createUser(BaseServiceSetupTestNG.USER_JOEUSER, BaseServiceSetupTestNG.USER_JOEUSER, BaseServiceSetupTestNG.USER_JOEUSER_FULLNAME);
        addRole(newUser, BaseServiceSetupTestNG.ROLE_USER);
    }

    private void addContentRepositoryTestResources() {
        m_logger.info("addContentRepositoryTestResources() called");

        m_logger.info("addContentRepositoryTestResources() => creating /ContentFiles");
        Folder dsFolder = new FolderImpl();
        dsFolder.setName("ContentFiles");
        dsFolder.setLabel("Content Files");
        dsFolder.setDescription("Content files generated by reports");
        getUnsecureRepositoryService().saveFolder(null, dsFolder);

        m_logger.info("addContentRepositoryTestResources() => creating /ContentFiles/html");
        Folder newFolder = new FolderImpl();
        newFolder.setParentFolder(dsFolder);
        newFolder.setName("html");
        newFolder.setLabel("html");
        getUnsecureRepositoryService().saveFolder(null, newFolder);

        m_logger.info("addContentRepositoryTestResources() => creating /ContentFiles/pdf");
        newFolder = new FolderImpl();
        newFolder.setParentFolder(dsFolder);
        newFolder.setName("pdf");
        newFolder.setLabel("pdf");
        getUnsecureRepositoryService().saveFolder(null, newFolder);

        m_logger.info("addContentRepositoryTestResources() => creating /ContentFiles/xls");
        newFolder = new FolderImpl();
        newFolder.setParentFolder(dsFolder);
        newFolder.setName("xls");
        newFolder.setLabel("xls");
        getUnsecureRepositoryService().saveFolder(null, newFolder);
    }

    private void addHibernateRepositoryDataSourceResources()  {
        m_logger.info("addHibernateRepositoryDataSourceResources() called");

        // create the main datasources folder
        // create the JndiDS, the RepoDS, the JdbcDS, the BeanDS, and the TableModelDS
        m_logger.info("addHibernateRepositoryDataSourceResources() => creating /datasources");
        Folder dsFolder = new FolderImpl();
        dsFolder.setName("datasources");
        dsFolder.setLabel("Data Sources");
        dsFolder.setDescription("Data Sources used by reports");
        getUnsecureRepositoryService().saveFolder(null, dsFolder);

        createJndiDS();
        createRepoDS();
        createJdbcDS();
    }

    private void addHibernateRepositoryReportResources()  {
        m_logger.info("addHibernateRepositoryReportResources() called");

        // create the reports folder
        m_logger.info("addHibernateRepositoryReportResources() => creating /reports");
        Folder reportsFolder = new FolderImpl();
        reportsFolder.setName("reports");
        reportsFolder.setLabel("Reports");
        reportsFolder.setDescription("Reports");
        getUnsecureRepositoryService().saveFolder(null, reportsFolder);

        // bug #22094. ROLE_USER should have write access to the /reports folder.
        Role userRole = getRole(ROLE_USER);
        createObjectPermission("/reports", userRole, JasperServerAclEntry.READ_WRITE_CREATE_DELETE);

        // create the interactive folder and its resources
        createInteractiveFolderAndReportResources(reportsFolder);

        // create an images folder and sample image
        createImagesFolderAndSampleImage();

        // 2013-08-14: removed creation of /reports/samples reports in this file 

        // create the main OLAP analysis folder
        m_logger.info("addHibernateRepositoryReportResources() => adding /analysis");
        Folder olapFolder = new FolderImpl();
        olapFolder.setName("analysis");
        olapFolder.setLabel("Analysis Components");
        olapFolder.setDescription("Analysis Components");
        getUnsecureRepositoryService().saveFolder(null, olapFolder);

        // create the OLAP analysis/connections folder
        m_logger.info("addHibernateRepositoryReportResources() => adding /analysis/connections");
        Folder connectionsFolder = new FolderImpl();
        connectionsFolder.setName("connections");
        connectionsFolder.setLabel("Analysis Connections");
        connectionsFolder.setDescription("Connections used by Analysis");
        connectionsFolder.setParentFolder(olapFolder);
        getUnsecureRepositoryService().saveFolder(null, connectionsFolder);

        // create the OLAP analysis/schemas folder
        m_logger.info("addHibernateRepositoryReportResources() => adding /analysis/schemas");
        Folder schemasFolder = new FolderImpl();
        schemasFolder.setName("schemas");
        schemasFolder.setLabel("Analysis Schemas");
        schemasFolder.setDescription("Schemas used by Analysis");
        schemasFolder.setParentFolder(olapFolder);
        getUnsecureRepositoryService().saveFolder(null, schemasFolder);

        // create the OLAP analysis/datasources folder
        m_logger.info("addHibernateRepositoryReportResources() => adding /analysis/datasources");
        Folder olapDsFolder = new FolderImpl();
        olapDsFolder.setName("datasources");
        olapDsFolder.setLabel("Analysis Data Sources");
        olapDsFolder.setDescription("Data sources used by Analysis");
        olapDsFolder.setParentFolder(olapFolder);
        getUnsecureRepositoryService().saveFolder(null, olapDsFolder);

        // create the OLAP analysis/views folder
        m_logger.info("addHibernateRepositoryReportResources() => adding /analysis/views");
        Folder olapViewsFolder = new FolderImpl();
        olapViewsFolder.setName("views");
        olapViewsFolder.setLabel("Analysis Views");
        olapViewsFolder.setDescription("Analysis Views");
        olapViewsFolder.setParentFolder(olapFolder);
        getUnsecureRepositoryService().saveFolder(null, olapViewsFolder);

        // setup the execute only permissions
        setupExecuteOnlyPermissions();

        // setup the no access permissions
        setupNoAccessPermissions();
    }

    private void setupExecuteOnlyPermissions() {
        Role userRole = getRole(ROLE_USER);
        createObjectPermission("/datasources", userRole, JasperServerAclEntry.EXECUTE);

        // bug 21880; we still need read access for datatypes
        // createObjectPermission("/datatypes", userRole, JasperServerAclEntry.EXECUTE);
        createObjectPermission("/images", userRole, JasperServerAclEntry.EXECUTE);
        createObjectPermission("/themes", userRole, JasperServerAclEntry.EXECUTE);
    }

    private void setupNoAccessPermissions() {
        // bug 29635; we need prevent access for User role, so only
        // ROLE_SUPERUSER & ROLE_ADMINISTRATOR should be able to execute that data source.
        Role userRole = getRole(ROLE_USER);
        createObjectPermission("/datasources/repositoryDS", userRole, JasperServerAclEntry.NOTHING);
    }

    private void createImagesFolderAndSampleImage() {
        m_logger.info("createImagesFolderAndSampleImage() => creating /images");
        Folder folder = new FolderImpl();
        folder.setName("images");
        folder.setLabel("Images");
        folder.setDescription("Folder containing reusable images");
        getUnsecureRepositoryService().saveFolder(null, folder);

        m_logger.info("createImagesFolderAndSampleImage() => creating /images/JRLogo");
        FileResource image = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        image.setFileType(FileResource.TYPE_IMAGE);
        image.readData(getClass().getResourceAsStream("/images/jasperreports.png"));
        image.setName("JRLogo");
        image.setLabel("JR logo");
        image.setDescription("JR logo");
        image.setParentFolder(folder);

        getUnsecureRepositoryService().saveResource(null, image);
    }

    private void createJndiDS() {
        JndiJdbcReportDataSource datasource = (JndiJdbcReportDataSource) getUnsecureRepositoryService().newResource(null, JndiJdbcReportDataSource.class);
        datasource.setName("JServerJNDIDS");
        datasource.setLabel("JServer JNDI Data Source");
        datasource.setDescription("JServer JNDI Data Source");
        datasource.setJndiName(getJdbcProps().getProperty("test.jndi"));
        datasource.setParentFolder("/datasources");

        getUnsecureRepositoryService().saveResource(null, datasource);
    }

    private void createRepoDS() {
        JndiJdbcReportDataSource datasource = (JndiJdbcReportDataSource) getUnsecureRepositoryService().newResource(null, JndiJdbcReportDataSource.class);
        datasource.setName("repositoryDS");
        datasource.setLabel("Jasperserver Repository SQL data source");
        datasource.setDescription("Jasperserver Repository SQL data source for reporting");
        datasource.setJndiName(getJdbcProps().getProperty("metadata.jndi"));
        datasource.setParentFolder("/datasources");

        getUnsecureRepositoryService().saveResource(null, datasource);
    }

    private void createJdbcDS() {
        JdbcReportDataSource datasource = (JdbcReportDataSource) getUnsecureRepositoryService().newResource(null, JdbcReportDataSource.class);
        datasource.setName("JServerJdbcDS");
        datasource.setLabel("JServer Jdbc Data Source");
        datasource.setDescription("JServer Jdbc Data Source");
        datasource.setParentFolder("/datasources");

        datasource.setDriverClass(getJdbcProps().getProperty("test.jdbc.driverClassName"));
        datasource.setConnectionUrl(getJdbcProps().getProperty("test.jdbc.url"));
        datasource.setUsername(getJdbcProps().getProperty("test.jdbc.username"));

		String passwd = getJdbcProps().getProperty("test.jdbc.password");
		if (EncryptionEngine.isEncrypted(passwd)) {
			KeystoreManager ksManager = KeystoreManager.getInstance();
			passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
		}
		datasource.setPassword(passwd);

        getUnsecureRepositoryService().saveResource(null, datasource);
    }

    private void setCommon(Resource res, String id) {
        res.setName(id);
        res.setLabel(id + " Label");
        res.setDescription(id + " description");
    }

    /*
     * OLAP Connection Resources
     */
    protected final String foodMartSchema2012URI   = "/queries/FoodmartSchema2012.xml";
    protected final String foodMartSchema2012UpperURI   = "/queries/FoodmartSchema2012Upper.xml";
    protected final String foodMartJaSchemaURI = "/queries/FoodMart_ja.xml";
    protected final String sugarCRMSchemaURI   = "/queries/SugarCRMSchema.xml";
    protected final String sugarCRMSchemaUpperURI   = "/queries/SugarcrmSchemaUpper.xml";

    // direct access to impl to call methods that aren't in the interface...
    private OlapConnectionServiceImpl olapConnectionServiceTarget;

    protected void createFoodmartSchemaResource() {
        m_logger.info("createFoodmartSchemaResource() => creating /analysis/schemas/FoodmartSchema");
        createOrUpdateSchemaResource("/analysis/schemas",
                "FoodmartSchema",
                "FoodmartSchema",
                "Foodmart Analysis Schema",
                foodMartSchema2012URI);
    }

    protected void createFoodmartSchemaUpperResource() {
        m_logger.info("createFoodmartSchemaUpperResource() => creating /analysis/schemas/FoodmartSchemaUpper");
        createOrUpdateSchemaResource("/analysis/schemas",
                "FoodmartSchemaUpper",
                "Foodmart Schema Uppercase",
                "Foodmart Analysis Schema Uppercase",
                foodMartSchema2012UpperURI);
    }

    protected void createSugarCRMSchemaResource() {
        m_logger.info("createSugarCRMSchemaResource() => creating /analysis/schemas/SugarCRMSchema");
        createOrUpdateSchemaResource("/analysis/schemas",
                "SugarCRMSchema",
                "SugarCRM Schema",
                "SugarCRM Analysis Schema",
                sugarCRMSchemaURI);
    }

    protected void createSugarCRMSchemaUpperResource() {
        m_logger.info("createSugarCRMSchemaUpperResource() => creating /analysis/schemas/SugarCRMSchemaUpper");
        createOrUpdateSchemaResource("/analysis/schemas",
                "SugarCRMSchemaUpper",
                "SugarCRM Schema Uppercase",
                "SugarCRM Analysis Schema Upper Case",
                sugarCRMSchemaUpperURI);
    }

    protected void createFoodmartJDBCDataSourceResource() throws Exception {
        m_logger.info("createFoodmartJDBCDataSourceResource() => creating /analysis/datasources/FoodmartDataSource");

		String passwd = getJdbcProps().getProperty("foodmart.jdbc.password");
		if (EncryptionEngine.isEncrypted(passwd)) {
			KeystoreManager ksManager = KeystoreManager.getInstance();
			passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
		}
		createJDBCDataSourceResource("/analysis/datasources",
                        "FoodmartDataSource",
                        "Foodmart Data Source",
                        "Foodmart Data Source",
                        getJdbcProps().getProperty("foodmart.jdbc.driverClassName"),
                        getJdbcProps().getProperty("foodmart.jdbc.url"),
                        getJdbcProps().getProperty("foodmart.jdbc.username"),
                        passwd);
    }

    protected void createFoodmartJNDIDataSourceResource() {
        m_logger.info("createFoodmartJNDIDataSourceResource() => creating /analysis/datasources/FoodmartDataSourceJNDI");
        createJNDIDataSourceResource("/analysis/datasources",
                        "FoodmartDataSourceJNDI",
                        "Foodmart Data Source JNDI",
                        "Foodmart Data Source JNDI",
                        "jdbc/foodmart");
    }

    protected void createSugarCRMDataSourceResource() throws Exception {
        m_logger.info("createSugarCRMDataSourceResource() => creating /analysis/datasources/SugarCRMDataSource");

		String passwd = getJdbcProps().getProperty("test.jdbc.password");
		if (EncryptionEngine.isEncrypted(passwd)) {
			KeystoreManager ksManager = KeystoreManager.getInstance();
			passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
		}
		createJDBCDataSourceResource("/analysis/datasources",
                        "SugarCRMDataSource",
                        "SugarCRM Data Source",
                        "SugarCRM Data Source",
                        getJdbcProps().getProperty("test.jdbc.driverClassName"),
                        getJdbcProps().getProperty("test.jdbc.url"),
                        getJdbcProps().getProperty("test.jdbc.username"),
                        passwd);
    }

    protected void createSugarCRMDataSourceResourceJNDI() {
        m_logger.info("createSugarCRMDataSourceResourceJNDI() => creating /analysis/datasources/SugarCRMDataSourceJNDI");
        createJNDIDataSourceResource("/analysis/datasources",
                        "SugarCRMDataSourceJNDI",
                        "SugarCRM Data Source JNDI",
                        "SugarCRM Data Source JNDI",
                        "jdbc/sugarcrm");
    }

    protected void createSugarFoodmartDataSourceResourceVirtual() {
        m_logger.info("createSugarCRMDataSourceResourceVirtual() => creating /datasources/SugarFoodmartVDS");
        Map<String, String> dataSourceUriMap = new HashMap<String, String>();
        dataSourceUriMap.put("FoodmartDataSource", "FoodmartDataSourceJNDI");
        dataSourceUriMap.put("SugarCRMDataSource", "SugarCRMDataSourceJNDI");
        createVirtualDataSourceResource("/datasources",
                "/analysis/datasources",
                "SugarFoodmartVDS",
                "SugarCRM-Foodmart Virtual Data Source",
                "Virtual Data Source Combining SugarCRM and Foodmart",
                dataSourceUriMap);
    }

    public void createVirtualDataSourceResource(String dataSourceFolderParentURI,
                String subDataSourceFolderParentURI,
                String dataSourceName,
                String dataSourceLabel,
                String dataSourceDescription,
                Map<String, String> dataSourceIDMap) {
        VirtualReportDataSource dataSourceResource = (VirtualReportDataSource) getRepositoryService().getResource(null, dataSourceFolderParentURI + Folder.SEPARATOR + dataSourceName, VirtualReportDataSource.class);
        if (dataSourceResource == null) {
            dataSourceResource = (VirtualReportDataSource) getRepositoryService().newResource( null, VirtualReportDataSource.class );
            dataSourceResource.setParentFolder(dataSourceFolderParentURI);
            dataSourceResource.setName( dataSourceName );
        }
        dataSourceResource.setLabel( dataSourceLabel );
        dataSourceResource.setDescription( dataSourceDescription );
        Map<String, ResourceReference> dataSourceUriMap = new LinkedHashMap<String, ResourceReference>();

        for (Map.Entry<String, String> entry : dataSourceIDMap.entrySet()) {
            String subDataSourceUri = subDataSourceFolderParentURI + Folder.SEPARATOR + entry.getValue();
            dataSourceUriMap.put(entry.getKey(), new ResourceReference(subDataSourceUri));
        }
        dataSourceResource.setDataSourceUriMap(dataSourceUriMap);
        getRepositoryService().saveResource(null, dataSourceResource);

    }

    protected void createFoodmartMondrianConnectionResource() throws Exception {
        m_logger.info("createFoodmartMondrianConnectionResource() => creating /analysis/connections/Foodmart");

        String schemaResourceReference = "/analysis/schemas/FoodmartSchema";

        if (getJdbcProps().getProperty("foodmart.upperCaseNames") != null &&
        		getJdbcProps().getProperty("foodmart.upperCaseNames").equalsIgnoreCase("true")) {
            schemaResourceReference = "/analysis/schemas/FoodmartSchemaUpper";
        }

        createMondrianConnectionResource("/analysis/connections",
                        "Foodmart",
                        "Foodmart Mondrian Connection",
                        "Foodmart Mondrian Analysis Connection",
                        schemaResourceReference,
                       "/analysis/datasources/FoodmartDataSourceJNDI");
    }

    protected void createFoodmartXMLAConnectionResource() {
        m_logger.info("createFoodmartXMLAConnectionResource() => creating /analysis/connections/FoodmartXmlaConnection");
        createXMLAConnectionResource("/analysis/connections",
                        "FoodmartXmlaConnection",
                        "Foodmart XML/A Connection",
                        "Foodmart XML/A Connection",
                        "Foodmart",
                        "Provider=Mondrian;DataSource=JRS",
                        "http://localhost:8080/jasperserver/xmla",
                        "jasperadmin",
                        "jasperadmin");
    }

    protected void createSugarCRMMondrianConnectionResource() throws Exception {
        m_logger.info("createSugarCRMMondrianConnectionResource() => creating /analysis/connections/SugarCRM");

        String schemaResourceReference = "/analysis/schemas/SugarCRMSchema";

        if (getJdbcProps().getProperty("sugarcrm.upperCaseNames") != null &&
        		getJdbcProps().getProperty("sugarcrm.upperCaseNames").equalsIgnoreCase("true")) {
            schemaResourceReference = "/analysis/schemas/SugarCRMSchemaUpper";
        }

        createMondrianConnectionResource("/analysis/connections",
                        "SugarCRM",
                        "SugarCRM Mondrian Connection",
                        "SugarCRM Mondrian Analysis Connection: only opportunities",
                        schemaResourceReference,
                       "/analysis/datasources/SugarCRMDataSourceJNDI");
    }

    protected void createSugarCRMXMLAConnectionResource() {
        m_logger.info("createSugarCRMXMLAConnectionResource() => creating /analysis/connections/SugarCRMXmlaConnection");
        createXMLAConnectionResource("/analysis/connections",
                        "SugarCRMXmlaConnection",
                        "SugarCRM XML/A Connection",
                        "SugarCRM XML/A Connection",
                        "SugarCRM",
                        "Provider=Mondrian;DataSource=JRS",
                        "http://localhost:8080/jasperserver/xmla",
                        "jasperadmin",
                        "jasperadmin");
    }

    public static String SAMPLE_FOODMART_MDX_QUERY =
	"select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns, " +
	"{([Promotion Media].[All Media], [Product].[All Products])} ON rows " +
	"from Sales ";

    protected void createFoodmartOlapUnit() {
        m_logger.info("createFoodmartOlapUnit() => creating /analysis/views/Foodmart_sample");
        creatOlapUnitResource("/analysis/views",
                        "Foodmart_sample",
                        "Foodmart Sample Analysis View",
                        "Sample Analysis View: Foodmart Unit Sales",
                        "/analysis/connections/Foodmart",
                        SAMPLE_FOODMART_MDX_QUERY,
                        true);
    }

    private String SAMPLE_SUGAR_CRM_MDX_QUERY =
	"select {[Measures].[Total Sale Amount], [Measures].[Number of Sales], [Measures].[Avg Sale Amount], [Measures].[Avg Time To Close (Days)], [Measures].[Avg Close Probability]} ON COLUMNS, " +
	" NON EMPTY {([Account Categorization].[All Accounts], [Close Period].[All Periods])} ON ROWS " +
	" from [SalesAnalysis] " +
	" where [Sale State].[All Types].[Closed Won]";

    protected void createSugarCRMOlapUnit() {
        m_logger.info("createSugarCRMOlapUnit() => creating /analysis/views/SugarCRM_sample");
        creatOlapUnitResource("/analysis/views",
                        "SugarCRM_sample",
                        "SugarCRM Sample Analysis View",
                        "Sample SugarCRM Analysis View: Sales Performance by Industry/Account",
                        "/analysis/connections/SugarCRM",
                        SAMPLE_SUGAR_CRM_MDX_QUERY,
                        true);
    }

    protected void createSugarCRMXmlaOlapUnit() {
        m_logger.info("createSugarCRMXmlaOlapUnit() => creating /analysis/views/SugarCRM_xmla_sample");
        creatOlapUnitResource("/analysis/views",
                        "SugarCRM_xmla_sample",
                        "SugarCRM Sample XMLA Analysis View",
                        "Sample SugarCRM Analysis View (XMLA): Sales Performance by Industry/Account",
                        "/analysis/connections/SugarCRMXmlaConnection",
                        SAMPLE_SUGAR_CRM_MDX_QUERY,
                        true);
    }

    protected void createFoodmartMondrianXMLADefinitionResource() {
        m_logger.info("createFoodmartMondrianXMLADefinitionResource() => creating /analysis/xmla/definitions/FoodmartXmlaDefinition");
        creatMondrianXMLADefinitionResource("/analysis/xmla/definitions",
                        "FoodmartXmlaDefinition",
                        "Foodmart Mondrian XMLA definition",
                        "Foodmart Mondrian XMLA definition",
                        "Foodmart",
                        "/analysis/connections/Foodmart");
    }

    protected void createSugarCRMMondrianXMLADefinitionResource() {
        m_logger.info("createSugarCRMMondrianXMLADefinitionResource() => creating /analysis/xmla/definitions/SugarCRMXmlaDefinition");
        creatMondrianXMLADefinitionResource("/analysis/xmla/definitions",
                        "SugarCRMXmlaDefinition",
                        "SugarCRM Mondrian XMLA definition",
                        "SugarCRM Mondrian XMLA definition",
                        "SugarCRM",
                        "/analysis/connections/SugarCRM");
    }

    // Pseudo Japanese schema version
    protected void createFoodmartJaSchemaResource() {
        m_logger.info("createFoodmartJaSchemaResource() => creating /analysis/schemas/FoodmartJaSchema");
        createOrUpdateSchemaResource("/analysis/schemas",
                "FoodmartJaSchema",
                "Foodmart Schema Pseudo Japanese",
                "Foodmart Analysis Pseudo Japanese Schema",
                foodMartJaSchemaURI);
    }

    protected void createFoodmartJaDataSourceResource() throws Exception {
        m_logger.info("createFoodmartJaDataSourceResource() => creating /analysis/datasources/FoodmartJaDataSource");

		String passwd = getJdbcProps().getProperty("foodmart_ja.jdbc.password");
		if (EncryptionEngine.isEncrypted(passwd)) {
			KeystoreManager ksManager = KeystoreManager.getInstance();
			passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
		}
		createJDBCDataSourceResource("/analysis/datasources",
					"FoodmartJaDataSource",
					"Foodmart Japanese Data Source",
					"Foodmart Japanese Data Source",
					getJdbcProps().getProperty("foodmart_ja.jdbc.driverClassName"),
					getJdbcProps().getProperty("foodmart_ja.jdbc.url"),
					getJdbcProps().getProperty("foodmart_ja.jdbc.username"),
					passwd);
    }

    protected void createFoodmartJaMondrianConnectionResource() {
        m_logger.info("createFoodmartJaMondrianConnectionResource() => creating /analysis/connections/FoodmartJa");
        createMondrianConnectionResource("/analysis/connections",
                        "FoodmartJa",
                        "FoodmartJa Mondrian Connection",
                        "FoodmartJa Mondrian Analysis Connection",
                        "/analysis/schemas/FoodmartJaSchema",
                        "/analysis/datasources/FoodmartJaDataSource");
    }

    public static String SAMPLE_FOODMART_JA_MDX_QUERY =
	"select {[Measures].[Unit Sales日本語], [Measures].[Store Cost日本語], [Measures].[Store Sales日本語]} on columns, {([Promotion Media日本語].[All Media日本語], [Product日本語].[All Products日本語])} ON rows from [Sales日本語]";

    protected void createFoodmartJaOlapUnit() {
        m_logger.info("createFoodmartJaOlapUnit() => creating /analysis/views/FoodmartJa_sample_unit_1");
        creatOlapUnitResource("/analysis/views",
                        "FoodmartJa_sample_unit_1",
                        "FoodmartJa Sample Analysis View",
                        "Sample Analysis View: FoodmartJa Unit Sales",
                        "/analysis/connections/FoodmartJa",
                        SAMPLE_FOODMART_JA_MDX_QUERY,
                        true);
    }

    // move this to configuration file if people want to use
    // this for testing on a regular basis...
    private final boolean DO_PSEUDO_JAPANESE_FOODMART = false;

    public void addOlapConnectionResources() throws Exception {
        m_logger.info("addOlapConnectionResources() called");

        // create olap connection test metadata in the repository
    	createFoodmartSchemaResource();
        createFoodmartSchemaUpperResource();

        createSugarCRMSchemaResource();
        createSugarCRMSchemaUpperResource();

        createFoodmartJDBCDataSourceResource();
        createFoodmartJNDIDataSourceResource();

        createSugarCRMDataSourceResource();
        createSugarCRMDataSourceResourceJNDI();

        // create virtual data source
        createSugarFoodmartDataSourceResourceVirtual();

        createFoodmartMondrianConnectionResource();
        createFoodmartXMLAConnectionResource();

        createSugarCRMMondrianConnectionResource();
        createSugarCRMXMLAConnectionResource();

        createFoodmartOlapUnit();
        createSugarCRMOlapUnit();
	    createSugarCRMXmlaOlapUnit();

	    createFoodmartMondrianXMLADefinitionResource();
	    createSugarCRMMondrianXMLADefinitionResource();

        if (DO_PSEUDO_JAPANESE_FOODMART) {
            createFoodmartJaSchemaResource();
            createFoodmartJaDataSourceResource();
            createFoodmartJaMondrianConnectionResource();
            createFoodmartJaOlapUnit();
        }

	    Folder analysisReports = createAnalysisReportsFolder();
	    createMondrianFoodmartReport(analysisReports);
    }

    private final static String FOODMART_REPORT_JRXML = "/reports/jasper/MondrianFoodMartSalesReport2012.jrxml";

    protected void createMondrianFoodmartReport(Folder parent) {
	    createOlapFoodmartReport(parent,
				 "/analysis/reports/",
				 "FoodmartSalesMondrianReport",
				 "Foodmart Mondrian Sales Report",
				 "Report with Mondrian Datasource",
				 "/analysis/connections/Foodmart");
    }

    protected Folder createAnalysisReportsFolder() {
        return createOrUpdateFolder("/analysis", "reports", "Analysis Reports");
    }

    public void createOlapFoodmartReport(Folder parent,
					    String path,
					    String name,
					    String label,
					    String desc,
					    String connectionUri) {

        boolean newReportUnit = false;
        ExecutionContext executionContext = JasperServerUtil.getExecutionContext();

        ReportUnit unit = (ReportUnit) getRepositoryService().getResource(null, path + name, ReportUnit.class);

        if (unit == null) {
            unit = (ReportUnit) getRepositoryService().newResource(executionContext, ReportUnit.class);
            unit.setParentFolder(parent);
            unit.setName(name);
            newReportUnit = true;
        }
        unit.setLabel(label);
        unit.setDescription(desc);

        FileResource report = null;

        if (!newReportUnit) {
            report = (FileResource) unit.getMainReport().getLocalResource();
            assert(report != null);
        } else {
            report = (FileResource) getRepositoryService().newResource(executionContext, FileResource.class);
            report.setFileType(FileResource.TYPE_JRXML);
            report.setName("FoodmartSalesReportJRXML");
            report.setLabel("FoodmartSalesReportJRXML");
        }
        report.readData(getClass().getResourceAsStream(FOODMART_REPORT_JRXML));

        unit.setMainReport(report);

        unit.setDataSourceReference(connectionUri);

        getRepositoryService().saveResource(executionContext, unit);
    }

    public Folder createOrUpdateFolder(String folderParentURI, String folderName, String folderLabel) {
       	Folder aFolder = null;
        ExecutionContext executionContext = JasperServerUtil.getExecutionContext();

        aFolder = getRepositoryService().getFolder(null, folderParentURI + Folder.SEPARATOR + folderName);

        if (aFolder == null) {
            aFolder = new FolderImpl();
            aFolder.setParentFolder(folderParentURI);
            aFolder.setName(folderName);
        }
    	aFolder.setLabel(folderLabel);
    	getRepositoryService().saveFolder(executionContext, aFolder);
        return aFolder;
    }

    public void createOrUpdateSchemaResource(String schemaFolderParentURI,
                String schemaName,
                String schemaLabel,
                String schemaDescription,
                String resourcePath) {
        FileResource schemaResource = (FileResource) getRepositoryService().getResource(null, schemaFolderParentURI + Folder.SEPARATOR + schemaName, FileResource.class);

         if (schemaResource == null) {
             schemaResource = (FileResource) getRepositoryService().newResource( null, FileResource.class );
            schemaResource.setParentFolder(schemaFolderParentURI);
            schemaResource.setName( schemaName );
        }
        schemaResource.setLabel( schemaLabel );
        schemaResource.setDescription( schemaDescription );

        schemaResource.setFileType(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA);
        InputStream in = getClass().getResourceAsStream( resourcePath );
        schemaResource.readData(in);

        getOlapConnectionService().saveResource( null, schemaFolderParentURI, schemaResource );
    }

    public void createJDBCDataSourceResource(String dataSourceFolderParentURI,
                String dataSourceName,
                String dataSourceLabel,
                String dataSourceDescription,
                String driverClassName,
                String connectionURL,
                String username,
                String password) {
        JdbcReportDataSource dataSourceResource = (JdbcReportDataSource) getRepositoryService().getResource(null, dataSourceFolderParentURI + Folder.SEPARATOR + dataSourceName, JdbcReportDataSource.class);
        if (dataSourceResource == null) {
            dataSourceResource = (JdbcReportDataSource) getRepositoryService().newResource( null, JdbcReportDataSource.class );
            dataSourceResource.setParentFolder(dataSourceFolderParentURI);
            dataSourceResource.setName( dataSourceName );
        }
        dataSourceResource.setLabel( dataSourceLabel );
        dataSourceResource.setDescription( dataSourceDescription );
        dataSourceResource.setDriverClass(driverClassName);
        dataSourceResource.setConnectionUrl(connectionURL);
        dataSourceResource.setUsername(username);
        dataSourceResource.setPassword(password);
        getOlapConnectionService().saveResource( null, dataSourceFolderParentURI, dataSourceResource );
    }

    public void createJNDIDataSourceResource(String dataSourceFolderParentURI,
                String dataSourceName,
                String dataSourceLabel,
                String dataSourceDescription,
                String jndiURI) {
        JndiJdbcReportDataSource dataSourceResource = (JndiJdbcReportDataSource) getRepositoryService().getResource(null, dataSourceFolderParentURI + Folder.SEPARATOR + dataSourceName, JndiJdbcReportDataSource.class);
        if (dataSourceResource == null) {
            dataSourceResource = (JndiJdbcReportDataSource) getRepositoryService().newResource( null, JndiJdbcReportDataSource.class );
            dataSourceResource.setParentFolder(dataSourceFolderParentURI);
            dataSourceResource.setName( dataSourceName );
        }
        dataSourceResource.setLabel( dataSourceLabel );
        dataSourceResource.setDescription( dataSourceDescription );
        dataSourceResource.setJndiName(jndiURI);
        getOlapConnectionService().saveResource( null, dataSourceFolderParentURI, dataSourceResource );
    }



    public void createMondrianConnectionResource(String mondrianConnectionFolderParentURI,
                String mondrianConnectionName,
                String mondrianConnectionLabel,
                String mondrianConnectionDescription,
                String schemaURI,
                String dataSourceURI) {
        MondrianConnection mondrianConnectionResource = (MondrianConnection) getRepositoryService().getResource(null, mondrianConnectionFolderParentURI + Folder.SEPARATOR + mondrianConnectionName, MondrianConnection.class);
        if (mondrianConnectionResource == null) {
            mondrianConnectionResource = (MondrianConnection) getRepositoryService().newResource( null, MondrianConnection.class );
            mondrianConnectionResource.setParentFolder(mondrianConnectionFolderParentURI);
            mondrianConnectionResource.setName( mondrianConnectionName );
        }
        mondrianConnectionResource.setLabel(mondrianConnectionName);
        mondrianConnectionResource.setDescription(mondrianConnectionDescription);

        mondrianConnectionResource.setSchemaReference(schemaURI);
        mondrianConnectionResource.setDataSourceReference(dataSourceURI);
        getOlapConnectionService().saveResource( null, mondrianConnectionFolderParentURI, mondrianConnectionResource );
    }

    public void createXMLAConnectionResource(String xmlaConnectionFolderParentURI,
                String xmlaConnectionName,
                String xmlaConnectionLabel,
                String xmlaConnectionDescription,
                String catalog,
                String xmlaDataSource,
                String URI,
                String username,
                String password) {
        XMLAConnection xmlaConnection = (XMLAConnection) getRepositoryService().getResource(null, xmlaConnectionFolderParentURI + Folder.SEPARATOR + xmlaConnectionName, XMLAConnection.class);
        if (xmlaConnection == null) {
            xmlaConnection = (XMLAConnection) getRepositoryService().newResource( null, XMLAConnection.class );
            xmlaConnection.setParentFolder(xmlaConnectionFolderParentURI);
            xmlaConnection.setName( xmlaConnectionName );
        }
        xmlaConnection.setLabel(xmlaConnectionLabel);
        xmlaConnection.setDescription(xmlaConnectionDescription);
        xmlaConnection.setCatalog(catalog);
        xmlaConnection.setDataSource(xmlaDataSource);
        xmlaConnection.setURI(URI);

        // TODO: create a sample USER_XMLA and ROLE_XMLA_USER
        xmlaConnection.setUsername(username);
        xmlaConnection.setPassword(password);
        getOlapConnectionService().saveResource( null, xmlaConnectionFolderParentURI, xmlaConnection );
    }

    public void creatMondrianXMLADefinitionResource(String xmlaDefinitionFolderParentURI,
                String xmlaDefinitionName,
                String xmlaDefinitionLabel,
                String xmlaDefinitionDescription,
                String catalog,
                String mondrianConnectionURI) {
        MondrianXMLADefinition mondrianXmlaConnection = (MondrianXMLADefinition) getRepositoryService().getResource(null, xmlaDefinitionFolderParentURI + Folder.SEPARATOR + xmlaDefinitionName, MondrianXMLADefinition.class);
        if (mondrianXmlaConnection == null) {
            mondrianXmlaConnection = (MondrianXMLADefinition) getRepositoryService().newResource( null, MondrianXMLADefinition.class );
            mondrianXmlaConnection.setParentFolder(xmlaDefinitionFolderParentURI);
            mondrianXmlaConnection.setName( xmlaDefinitionName );
        }
        mondrianXmlaConnection.setLabel(xmlaDefinitionLabel);
        mondrianXmlaConnection.setDescription(xmlaDefinitionDescription);
        mondrianXmlaConnection.setCatalog(catalog);
        mondrianXmlaConnection.setMondrianConnectionReference(mondrianConnectionURI);

        getOlapConnectionService().saveResource( null, xmlaDefinitionFolderParentURI, mondrianXmlaConnection );
    }

    public OlapUnit creatOlapUnitResource(String olapUnitFolderParentURI,
                String olapUnitName,
                String olapUnitLabel,
                String olapUnitDescription,
                String connectionReference,
                String mdxQuery,
                boolean saveUnit) {
        OlapUnit olapUnit = (OlapUnit) getRepositoryService().getResource(null, olapUnitFolderParentURI + Folder.SEPARATOR + olapUnitName, OlapUnit.class);
        if (olapUnit == null) {
            olapUnit = (OlapUnit) getRepositoryService().newResource( null, OlapUnit.class );
            olapUnit.setParentFolder(olapUnitFolderParentURI);
            olapUnit.setName( olapUnitName );
        }
        olapUnit.setLabel(olapUnitLabel);
        olapUnit.setDescription(olapUnitDescription);
        olapUnit.setOlapClientConnectionReference(connectionReference);
        olapUnit.setMdxQuery(mdxQuery);

        if (saveUnit) {
            getOlapConnectionService().saveResource( null, olapUnitFolderParentURI, olapUnit );
        }

        return olapUnit;
    }

	public void setOlapConnectionServiceTarget(OlapConnectionServiceImpl olapConnectionServiceTarget) {
		this.olapConnectionServiceTarget = olapConnectionServiceTarget;
	}

	public OlapConnectionServiceImpl getOlapConnectionServiceTarget() {
		return olapConnectionServiceTarget;
	}

    private void createInteractiveFolderAndReportResources(Folder reportsFolder)  {
        m_logger.info("createInteractiveFolderAndReportResources() called");

        // create the interactive folder
        m_logger.info("createInteractiveFolderAndReportResources() => creating /reports/interactive");
        Folder interactiveFolder = new FolderImpl();
        interactiveFolder.setName("interactive");
        interactiveFolder.setLabel("Interactive");
        interactiveFolder.setDescription("Interactive");
        interactiveFolder.setParentFolder(reportsFolder);
        getUnsecureRepositoryService().saveFolder(null, interactiveFolder);

        // create various sample reports
        createCustomersReport(interactiveFolder);
        createCustomersData(interactiveFolder);
        createCustomersDataAdapter(interactiveFolder);

        createTableReport(interactiveFolder);
        createCsvData(interactiveFolder);
        createCsvDataAdapter(interactiveFolder);

        createMapReport(interactiveFolder);
    }

    private void createCustomersReport(Folder folder) {
        m_logger.info("createCustomersReport() => creating /reports/interactive/CustomersReport");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("CustomersReport");
        reportRes.setLabel("Customers Report");
        reportRes.setDescription("Customers Report");
        reportRes.setParentFolder(folder);

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/CustomersReport.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("CustomersReport");
        unit.setLabel("Customers Report");
        unit.setDescription("Customers Report");
        unit.setParentFolder(folder);

        //unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        FileResource res2 = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        res2.setFileType(FileResource.TYPE_JRXML);
        res2.readData(getClass().getResourceAsStream("/reports/jasper/OrdersReport.jrxml"));
        res2.setName("OrdersReport");
        res2.setLabel("OrdersReport");
        res2.setDescription("OrdersReport");
        unit.addResource(res2);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

    private void createCustomersData(Folder folder) {
        m_logger.info("createCustomersData() => creating /reports/interactive/CustomersData");

        FileResource xmlData = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        xmlData.setFileType(FileResource.TYPE_XML);
        xmlData.readData(getClass().getResourceAsStream("/reports/jasper/CustomersData.xml"));
        xmlData.setName("CustomersData");
        xmlData.setLabel("Customers Data");
        xmlData.setDescription("Customers Data");
        xmlData.setParentFolder(folder);

        getUnsecureRepositoryService().saveResource(null, xmlData);
    }

    private void createCustomersDataAdapter(Folder folder) {
        m_logger.info("createCustomersDataAdapter() => creating /reports/interactive/CustomersDataAdapter");

        FileResource xmlRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        xmlRes.setFileType(FileResource.TYPE_XML);
        xmlRes.readData(getClass().getResourceAsStream("/reports/jasper/CustomersDataAdapter.xml"));
        xmlRes.setName("CustomersDataAdapter");
        xmlRes.setLabel("Customers Data Adapter");
        xmlRes.setDescription("Customers Data Adapter");
        xmlRes.setParentFolder(folder);

        getUnsecureRepositoryService().saveResource(null, xmlRes);
    }

    private void createTableReport(Folder folder) {
        m_logger.info("createTableReport() => creating /reports/interactive/TableReport");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("TableReport");
        reportRes.setLabel("Table Report");
        reportRes.setDescription("Table Report");
        reportRes.setParentFolder(folder);

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/TableReport.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("TableReport");
        unit.setLabel("Table Report");
        unit.setDescription("Table Report");
        unit.setParentFolder(folder);

        //unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

    private void createCsvData(Folder folder) {
        m_logger.info("createCsvData() => creating /reports/interactive/CsvData");

        FileResource csvData = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        csvData.setFileType(FileResource.TYPE_XML);
        csvData.readData(getClass().getResourceAsStream("/reports/jasper/CsvData.csv"));
        csvData.setName("CsvData");
        csvData.setLabel("CSV Data");
        csvData.setDescription("CSV Data");
        csvData.setParentFolder(folder);

        getUnsecureRepositoryService().saveResource(null, csvData);
    }

    private void createCsvDataAdapter(Folder folder) {
        m_logger.info("createCsvDataAdapter() => creating /reports/interactive/CsvDataAdapter");

        FileResource xmlRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        xmlRes.setFileType(FileResource.TYPE_XML);
        xmlRes.readData(getClass().getResourceAsStream("/reports/jasper/CsvDataAdapter.xml"));
        xmlRes.setName("CsvDataAdapter");
        xmlRes.setLabel("CSV Data Adapter");
        xmlRes.setDescription("CSV Data Adapter");
        xmlRes.setParentFolder(folder);

        getUnsecureRepositoryService().saveResource(null, xmlRes);
    }

    private void createMapReport(Folder folder) {
        m_logger.info("createMapReport() => creating /reports/interactive/MapReport");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("MapReport");
        reportRes.setLabel("Map Report");
        reportRes.setDescription("Map Report");
        reportRes.setParentFolder(folder);

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/MapReport.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("MapReport");
        unit.setLabel("Map Report");
        unit.setDescription("Map Report");
        unit.setParentFolder(folder);

        //unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

}
