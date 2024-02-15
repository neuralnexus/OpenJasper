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
package com.jaspersoft.jasperserver.api.metadata.xml.domain.impl;

/**
 * @author tkavanagh
 * @version $Id: ResourceDescriptor.java 4307 2006-08-24 08:13:55Z giulio $
 */

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.jaspersoft.jasperserver.core.util.XMLUtil;

public class ResourceDescriptor {

    public static final String TYPE_FOLDER = "folder";
    public static final String TYPE_REPORTUNIT = "reportUnit";
    public static final String TYPE_OLAPUNIT = "olapUnit";

    /* PRO OBJECTS */
    public static final String TYPE_DASHBOARD_STATE = "dashboardState";
    public static final String TYPE_DASHBOARD = "dashboard";
    public static final String TYPE_REPORT_OPTIONS = "reportOptions";
    public static final String TYPE_DATASOURCE_DOMAIN = "domain";
    public static final String TYPE_DOMAIN_TOPICS = "domainTopic";
    public static final String TYPE_ADHOC_REPORT = "adhocReport";
    public static final String TYPE_ADHOC_DATA_VIEW = "adhocDataView";

    public static final String TYPE_DATASOURCE = "datasource";
    public static final String TYPE_DATASOURCE_JDBC = "jdbc";
    public static final String TYPE_DATASOURCE_JNDI = "jndi";
    public static final String TYPE_DATASOURCE_BEAN = "bean";
    public static final String TYPE_DATASOURCE_VIRTUAL = "virtual";
    public static final String TYPE_DATASOURCE_CUSTOM = "custom";
    public static final String TYPE_DATASOURCE_AWS = "aws";

    public static final String TYPE_IMAGE = FileResource.TYPE_IMAGE;
    public static final String TYPE_FONT = FileResource.TYPE_FONT;
    public static final String TYPE_JRXML = FileResource.TYPE_JRXML;
    public static final String TYPE_CLASS_JAR = FileResource.TYPE_JAR;
    public static final String TYPE_RESOURCE_BUNDLE = FileResource.TYPE_RESOURCE_BUNDLE;
    public static final String TYPE_REFERENCE = "reference";
    public static final String TYPE_INPUT_CONTROL = "inputControl";
    public static final String TYPE_DATA_TYPE = "dataType";
    public static final String TYPE_OLAP_MONDRIAN_CONNECTION = "olapMondrianCon";
    public static final String TYPE_OLAP_XMLA_CONNECTION = "olapXmlaCon";
    public static final String TYPE_MONDRIAN_SCHEMA = "olapMondrianSchema";
	public static final String TYPE_ACCESS_GRANT_SCHEMA = FileResource.TYPE_ACCESS_GRANT_SCHEMA; // Pro-only
    public static final String TYPE_UNKNOW = "unknow";
    public static final String TYPE_LOV = "lov"; // List of values...
    public static final String TYPE_QUERY = "query"; // List of values...
    public static final String TYPE_CONTENT_RESOURCE = "contentResource";
    public static final String TYPE_STYLE_TEMPLATE = FileResource.TYPE_STYLE_TEMPLATE;
    public static final String TYPE_XML_FILE = FileResource.TYPE_XML;

    public static final String XML_ATT_NAME = "name";
    public static final String XML_ATT_WSTYPE = "wsType";
    public static final String XML_ATT_URI_STRING = "uriString";
    public static final String XML_ATT_IS_NEW = "isNew";


    /**
     * These constants are copied here from DataType for facility
     */
    public static final byte DT_TYPE_TEXT = 1;
    public static final byte DT_TYPE_NUMBER = 2;
    public static final byte DT_TYPE_DATE = 3;
    public static final byte DT_TYPE_DATE_TIME = 4; 
    
    /**
     * These constants are copied here from InputControl for facility
     */
    public static final byte IC_TYPE_BOOLEAN = 1;
    public static final byte IC_TYPE_SINGLE_VALUE = 2;
    public static final byte IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES = 3;
    public static final byte IC_TYPE_SINGLE_SELECT_QUERY = 4;
    public static final byte IC_TYPE_MULTI_VALUE = 5;
    public static final byte IC_TYPE_MULTI_SELECT_LIST_OF_VALUES = 6;
    public static final byte IC_TYPE_MULTI_SELECT_QUERY = 7;
    
    public static final byte IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO = 8;
    public static final byte IC_TYPE_SINGLE_SELECT_QUERY_RADIO = 9;
    public static final byte IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX = 10;
    public static final byte IC_TYPE_MULTI_SELECT_QUERY_CHECKBOX = 11;
    
    public static final String PROP_VERSION = "PROP_VERSION";
    public static final String PROP_PARENT_FOLDER = "PROP_PARENT_FOLDER";
    public static final String PROP_RESOURCE_TYPE = "PROP_RESOURCE_TYPE";
    public static final String PROP_CREATION_DATE = "PROP_CREATION_DATE";
    
    // File resource properties
    public static final String PROP_FILERESOURCE_HAS_DATA = "PROP_HAS_DATA";
    public static final String PROP_FILERESOURCE_IS_REFERENCE = "PROP_IS_REFERENCE";
    public static final String PROP_FILERESOURCE_REFERENCE_URI = "PROP_REFERENCE_URI";
    public static final String PROP_FILERESOURCE_WSTYPE = "PROP_WSTYPE";
    public static final String PROP_DATA = "PROP_DATA";
    public static final String PROP_DATASOURCE_MAPPING = "DATASOURCE_MAPPING";
    
    // Datasource properties
    public static final String PROP_DATASOURCE_DRIVER_CLASS = "PROP_DATASOURCE_DRIVER_CLASS";
    public static final String PROP_DATASOURCE_CONNECTION_URL = "PROP_DATASOURCE_CONNECTION_URL";
    public static final String PROP_DATASOURCE_USERNAME = "PROP_DATASOURCE_USERNAME";
    public static final String PROP_DATASOURCE_PASSWORD = "PROP_DATASOURCE_PASSWORD";
    public static final String PROP_DATASOURCE_JNDI_NAME = "PROP_DATASOURCE_JNDI_NAME";
    public static final String PROP_DATASOURCE_BEAN_NAME = "PROP_DATASOURCE_BEAN_NAME";
    public static final String PROP_DATASOURCE_BEAN_METHOD = "PROP_DATASOURCE_BEAN_METHOD";
    //
    public static final String PROP_DATASOURCE_CUSTOM_SERVICE_CLASS = "PROP_DATASOURCE_CUSTOM_SERVICE_CLASS";
    public static final String PROP_DATASOURCE_CUSTOM_PROPERTY_MAP = "PROP_DATASOURCE_CUSTOM_PROPERTY_MAP";
    //VDS
    public static final String PROP_DATASOURCE_SUB_DS_ID = "PROP_DATASOURCE_SUB_DS_ID";
    //AWS datasource specific properties
    public static final String PROP_DATASOURCE_AWS_ACCESS_KEY = "PROP_DATASOURCE_AWS_ACCESS_KEY";
    public static final String PROP_DATASOURCE_AWS_SECRET_KEY = "PROP_DATASOURCE_AWS_SECRET_KEY";
    public static final String PROP_DATASOURCE_AWS_ROLE_ARN = "PROP_DATASOURCE_AWS_ROLE_ARN";
    public static final String PROP_DATASOURCE_AWS_REGION = "PROP_DATASOURCE_AWS_REGION";
    public static final String PROP_DATASOURCE_AWS_DB_NAME = "PROP_DATASOURCE_AWS_DB_NAME";
    public static final String PROP_DATASOURCE_AWS_DB_INSTANCE_IDENTIFIER = "PROP_DATASOURCE_AWS_DB_INSTANCE_IDENTIFIER";
    public static final String PROP_DATASOURCE_AWS_DB_SERVICE = "PROP_DATASOURCE_AWS_DB_SERVICE";

    // ReportUnit resource properties
    public static final String PROP_RU_DATASOURCE_TYPE = "PROP_RU_DATASOURCE_TYPE";
    public static final String PROP_RU_IS_MAIN_REPORT = "PROP_RU_IS_MAIN_REPORT";
    public static final String PROP_RU_INPUTCONTROL_RENDERING_VIEW = "PROP_RU_INPUTCONTROL_RENDERING_VIEW";
    public static final String PROP_RU_REPORT_RENDERING_VIEW = "PROP_RU_REPORT_RENDERING_VIEW";
    public static final String PROP_RU_ALWAYS_PROPMT_CONTROLS = "PROP_RU_ALWAYS_PROPMT_CONTROLS";
    public static final String PROP_RU_CONTROLS_LAYOUT = "PROP_RU_CONTROLS_LAYOUT";
    
    public static final byte RU_CONTROLS_LAYOUT_POPUP_SCREEN = 1;
    public static final byte RU_CONTROLS_LAYOUT_SEPARATE_PAGE = 2;
    public static final byte RU_CONTROLS_LAYOUT_TOP_OF_PAGE = 3;
    public static final byte RU_CONTROLS_LAYOUT_IN_PAGE = 4;

    // DataType resource properties
    public static final String PROP_DATATYPE_STRICT_MAX = "PROP_DATATYPE_STRICT_MAX";
    public static final String PROP_DATATYPE_STRICT_MIN = "PROP_DATATYPE_STRICT_MIN";
    public static final String PROP_DATATYPE_MIN_VALUE = "PROP_DATATYPE_MIN_VALUE";
    public static final String PROP_DATATYPE_MAX_VALUE = "PROP_DATATYPE_MAX_VALUE";
    public static final String PROP_DATATYPE_PATTERN = "PROP_DATATYPE_PATTERN";
    public static final String PROP_DATATYPE_TYPE = "PROP_DATATYPE_TYPE";
    
     // ListOfValues resource properties
    public static final String PROP_LOV = "PROP_LOV";
    public static final String PROP_LOV_LABEL = "PROP_LOV_LABEL";
    public static final String PROP_LOV_VALUE = "PROP_LOV_VALUE";
    
    
    // InputControl resource properties
    public static final String PROP_INPUTCONTROL_TYPE = "PROP_INPUTCONTROL_TYPE";
    public static final String PROP_INPUTCONTROL_IS_MANDATORY = "PROP_INPUTCONTROL_IS_MANDATORY";
    public static final String PROP_INPUTCONTROL_IS_READONLY = "PROP_INPUTCONTROL_IS_READONLY";
    public static final String PROP_INPUTCONTROL_IS_VISIBLE = "PROP_INPUTCONTROL_IS_VISIBLE";
    
    // SQL resource properties
    public static final String PROP_QUERY = "PROP_QUERY";
    public static final String PROP_QUERY_VISIBLE_COLUMNS = "PROP_QUERY_VISIBLE_COLUMNS";
    public static final String PROP_QUERY_VISIBLE_COLUMN_NAME = "PROP_QUERY_VISIBLE_COLUMN_NAME";
    public static final String PROP_QUERY_VALUE_COLUMN = "PROP_QUERY_VALUE_COLUMN";
    public static final String PROP_QUERY_LANGUAGE = "PROP_QUERY_LANGUAGE";
    
    
    // SQL resource properties
    public static final String PROP_QUERY_DATA = "PROP_QUERY_DATA";
    public static final String PROP_QUERY_DATA_ROW = "PROP_QUERY_DATA_ROW";
    public static final String PROP_QUERY_DATA_ROW_COLUMN = "PROP_QUERY_DATA_ROW_COLUMN";
    
    
    // OLAP XMLA Connection
    public static final String PROP_XMLA_URI = "PROP_XMLA_URI";
    public static final String PROP_XMLA_CATALOG = "PROP_XMLA_CATALOG";
    public static final String PROP_XMLA_DATASOURCE = "PROP_XMLA_DATASOURCE";
    public static final String PROP_XMLA_USERNAME = "PROP_XMLA_USERNAME";
    public static final String PROP_XMLA_PASSWORD = "PROP_XMLA_PASSWORD";

    // OLAP Unit
    public static final String PROP_MDX_QUERY = "PROP_MDX_QUERY";
    
    // Content resource properties
    public static final String PROP_CONTENT_RESOURCE_TYPE = "CONTENT_TYPE";
    public static final String PROP_DATA_ATTACHMENT_ID = "DATA_ATTACHMENT_ID";
    public static final String PROP_REPORT_URI = "PROP_REPORT_URI";
    public static final String PROP_INPUT_VALUES = "PROP_INPUT_VALUES";
    public static final String PROP_OPTIONS_NAME = "PROP_OPTIONS_NAME";

    // Security permission mask of current user for resource properties
    public static final String PROP_SECURITY_PERMISSION_MASK = "PROP_SECURITY_PERMISSION_MASK";


    public static final String CONTENT_TYPE_PDF = "pdf";
    public static final String CONTENT_TYPE_HTML = "html";
    public static final String CONTENT_TYPE_XLS = "xls";
    public static final String CONTENT_TYPE_RTF = "rtf";
    public static final String CONTENT_TYPE_CSV = "csv";
    public static final String CONTENT_TYPE_IMAGE = "img";

    public static final String REFERENCE_TYPE = "referenceType";
    
    private java.util.List properties = new java.util.ArrayList(); 
    private java.util.HashMap hm = new java.util.HashMap();
    
    // the following come
    // from Resource interface
    // Main Attributes
    private String name;
    private String label;
    private String description;
    private boolean isNew = false;
    private String wsType;			// this it object/xml type
    private String uriString;
    
    private Date creationDate;
    
    private List children = new ArrayList();	// list of ResourceDescriptors
    private List parameters = new ArrayList();	// list of ListItem

    // This data is used to store the data for sunsequent calls to getQueryData....
    private java.util.List queryDataCache = null;
    private byte[] data;
    private String fileType;
    private String referenceType;

    public byte[] getData(){
        return data;
    }

    public void setData(byte[] data){
        this.data = data;
    }


    public String getWsType() {
            return wsType;
    }

    public void setWsType(String wsType) {
            this.wsType = wsType;
    }

    public String getReferenceType() {
            return referenceType;
    }

    public void setReferenceType(String referenceType) {
            this.referenceType = referenceType;
    }

    public String getUriString() {
            return uriString;
    }

    public void setUriString(String uriString) {
            this.uriString = uriString;
    }

    public String getName()	{
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }

    public String getLabel() {
            return label;
    }

    public void setLabel(String label) {
            this.label = label;
    }

    public String getDescription() {
            return description;
    }

    public void setDescription(String description) {
            this.description = description;
    }

    /**
     * Return the value for the property PROP_VERSION.
     * If no version is set, 0 is returned
     */
    public int getVersion() {
        Integer i = getResourcePropertyValueAsInteger( PROP_VERSION );
        if (i != null) return i.intValue();
        return 0;
    }

    public void setVersion(int version) {
            setResourceProperty(PROP_VERSION, ""+version );
    }

    // this is a uri string (like uriString member)
    public String getParentFolder() {
            return getResourcePropertyValue( PROP_PARENT_FOLDER );
    }

    public void setParentFolder(String parentFolder) {
            setResourceProperty(PROP_PARENT_FOLDER, ""+parentFolder ); ;
    }

    public String getResourceType() {
            return getResourcePropertyValue( PROP_RESOURCE_TYPE );
    }

    public void setResourceType(String resourceType) {
            setResourceProperty(PROP_RESOURCE_TYPE, ""+resourceType );
    }

    public boolean getIsNew() {
            return isNew;
    }

    public void setIsNew(boolean isNew) {
            this.isNew = isNew;
    }

    public Date getCreationDate() {
            return creationDate;
    }

    public void setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
    }


    public boolean getHasData() {
            String s = getResourcePropertyValue( PROP_FILERESOURCE_HAS_DATA );
            if (s != null) return s.equals("true");
            return false;
    }

    public void setHasData(boolean hasData) {
            setResourceProperty(PROP_FILERESOURCE_HAS_DATA, ""+hasData );
    }

    public boolean getIsReference() {
            String s = getResourcePropertyValue( PROP_FILERESOURCE_IS_REFERENCE );
            if (s != null) return s.equals("true");
            return false;
    }

    public void setIsReference(boolean isReference) {
            setResourceProperty(PROP_FILERESOURCE_IS_REFERENCE, ""+isReference );
    }

    public String getReferenceUri() {
            return getResourcePropertyValue( PROP_FILERESOURCE_REFERENCE_URI );
    }

    public void setReferenceUri(String referenceUri) {
            setResourceProperty(PROP_FILERESOURCE_REFERENCE_URI, referenceUri );
    }

    public String getDriverClass() {
            return getResourcePropertyValue( PROP_DATASOURCE_DRIVER_CLASS );
    }

    public void setDriverClass(String driverClass) {
            setResourceProperty(PROP_DATASOURCE_DRIVER_CLASS, driverClass );
    }

    public String getConnectionUrl() {
            return getResourcePropertyValue( PROP_DATASOURCE_CONNECTION_URL );
    }

    public void setConnectionUrl(String connectionUrl) {
            setResourceProperty(PROP_DATASOURCE_CONNECTION_URL, connectionUrl );
    }

    public String getPassword() {
            return getResourcePropertyValue( PROP_DATASOURCE_PASSWORD );
    }

    public void setPassword(String password) {
            setResourceProperty(PROP_DATASOURCE_PASSWORD, password );
    }

    public String getUsername() {
            return getResourcePropertyValue( PROP_DATASOURCE_USERNAME );
    }

    public void setUsername(String username) {
            setResourceProperty(PROP_DATASOURCE_USERNAME, username );
    }

    public String getJndiName() {
            return getResourcePropertyValue( PROP_DATASOURCE_JNDI_NAME );
    }

    public void setJndiName(String jndiName) {
            setResourceProperty(PROP_DATASOURCE_JNDI_NAME, jndiName );
    }

    public void setServiceClass(String svcClass) {
        setResourceProperty(PROP_DATASOURCE_CUSTOM_SERVICE_CLASS, svcClass );
    }

    public String getServiceClass() {
        return getResourcePropertyValue( PROP_DATASOURCE_CUSTOM_SERVICE_CLASS );
    }
    
    public void setAwsAccessKey(String awsAccessKey) {
        setResourceProperty( PROP_DATASOURCE_AWS_ACCESS_KEY, awsAccessKey );
    }

    public String getAwsAccessKey() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_ACCESS_KEY);
    }

    public void setAwsSecretKey(String awsSecretKey) {
        setResourceProperty( PROP_DATASOURCE_AWS_SECRET_KEY, awsSecretKey );
    }

    public String getAwsSecretKey() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_SECRET_KEY);
    }

    public void setAwsRoleARN(String roleARN) {
        setResourceProperty( PROP_DATASOURCE_AWS_ROLE_ARN, roleARN );
    }

    public String getAwsRoleARN() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_ROLE_ARN);
    }

    public void setAwsRegion(String awsRegion) {
        setResourceProperty( PROP_DATASOURCE_AWS_REGION, awsRegion );
    }

    public String getAwsRegion() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_REGION);
    }
    
    public void setAwsDbName(String awsDbName) {
        setResourceProperty(PROP_DATASOURCE_AWS_DB_NAME, awsDbName);
    }

    public String getAwsDbName() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_DB_NAME);
    }

    public void setAwsDbInstanceIdentifier(String awsDbInstanceIdentifier) {
        setResourceProperty(PROP_DATASOURCE_AWS_DB_INSTANCE_IDENTIFIER, awsDbInstanceIdentifier);
    }

    public String getAwsDbInstanceIdentifier() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_DB_INSTANCE_IDENTIFIER);
    }

    public void setAwsDbService(String awsDbService) {
        setResourceProperty(PROP_DATASOURCE_AWS_DB_SERVICE, awsDbService);
    }

    public String getAwsDbService() {
        return getResourcePropertyValue( PROP_DATASOURCE_AWS_DB_SERVICE);
    }

    /* DOMAIN HANDLER FEATURES */
    public void setSchema(String schema){

        setResourceProperty("SCHEMA", schema );

    }
    public void setBundles(List bundles){

    }
    public void setDefaultBoundleLocalStr(String defaultBundleLocal){
        setResourceProperty("DEFAULT_BUNDLE_LOCAL", defaultBundleLocal );

    }
    public void setSecurityFile(String securityFile){
        setResourceProperty("DEFAULT_BUNDLE_LOCAL", securityFile );

    }

    public void setDataSources(Map<String, ResourceReference> dataSources){

    }

    public void setPropertyMap(Map map) {
        
        ResourceProperty rp = new ResourceProperty(PROP_DATASOURCE_CUSTOM_PROPERTY_MAP);
        
        Iterator ki = map.keySet().iterator();
        while (ki.hasNext()) {
            String k = (String) ki.next();
            rp.getProperties().add(new ResourceProperty(k, (String) map.get(k) ) );
        }
        setResourceProperty(rp);
    }

    public Map getPropertyMap() {
        
        ResourceProperty rp = getResourceProperty(PROP_DATASOURCE_CUSTOM_PROPERTY_MAP);
        
        Map map = new HashMap();
        if (rp != null)
        {
            for (int i=0; i<rp.getProperties().size(); ++i)
            {
                ResourceProperty rpChild = (ResourceProperty)rp.getProperties().get(i);
                map.put(rpChild.getName(), rpChild.getValue());
            }
        }
        return map;
    }

    public ResourceProperty getProperty(String key){
        return (ResourceProperty)hm.get(key);

    }

    public String getDataSourceType() {
            return getResourcePropertyValue( PROP_RU_DATASOURCE_TYPE );
    }

    public void setDataSourceType(String dataSourceType) {
            setResourceProperty(PROP_RU_DATASOURCE_TYPE, dataSourceType );
    }

    public boolean isMainReport() {
            String s = getResourcePropertyValue( PROP_RU_IS_MAIN_REPORT );
            if (s != null) return s.equals("true");
            return false;
    }

    public void setMainReport(boolean isMainReport) {
            setResourceProperty(PROP_RU_IS_MAIN_REPORT, ""+isMainReport );
    }

    public List getChildren() {
            return children;
    }

    public void setChildren(List children) {
            this.children = children;
    }	

    public boolean isStrictMax() {
        String s = getResourcePropertyValue( PROP_DATATYPE_STRICT_MAX );
        if (s != null) return s.equals("true");
        return false;
    }

    public void setStrictMax(boolean strictMax) {
       setResourceProperty(PROP_DATATYPE_STRICT_MAX, ""+strictMax );
    }

    public boolean isStrictMin() {
        String s = getResourcePropertyValue( PROP_DATATYPE_STRICT_MIN );
        if (s != null) return s.equals("true");
        return false;
    }

    public static boolean isFileType(String fileType){
        return CONTENT_TYPE_PDF.equals(fileType) || CONTENT_TYPE_HTML.equals(fileType) || CONTENT_TYPE_XLS.equals(fileType) || CONTENT_TYPE_RTF.equals(fileType) ||
               CONTENT_TYPE_CSV.equals(fileType) || CONTENT_TYPE_IMAGE.equals(fileType) || TYPE_RESOURCE_BUNDLE.equals(fileType) || TYPE_FONT.equals(fileType) ||
               TYPE_CLASS_JAR.equals(fileType) || TYPE_JRXML.equals(fileType) || TYPE_STYLE_TEMPLATE.equals(fileType) || TYPE_XML_FILE.equals(fileType);
    }

    public void setStrictMin(boolean strictMin) {
        setResourceProperty(PROP_DATATYPE_STRICT_MIN, ""+strictMin );
    }

    public String getMinValue() {
        return getResourcePropertyValue( PROP_DATATYPE_MIN_VALUE );
    }

    public void setMinValue(String minValue) {
        setResourceProperty(PROP_DATATYPE_MIN_VALUE, minValue );
    }

    public String getMaxValue() {
        return getResourcePropertyValue( PROP_DATATYPE_MAX_VALUE );
    }

    public void setMaxValue(String maxValue) {
        setResourceProperty(PROP_DATATYPE_MAX_VALUE, maxValue );
    }

    public String getPattern() {
        return getResourcePropertyValue( PROP_DATATYPE_PATTERN );
    }

    public void setPattern(String pattern) {
        setResourceProperty(PROP_DATATYPE_PATTERN, pattern );
    }

    public byte getDataType() {
        String s = getResourcePropertyValue( PROP_DATATYPE_TYPE );
        if (s == null || s.length() == 0) return 0;
        return Byte.valueOf( s ).byteValue();
    }

    public void setDataType(byte dataType) {
        setResourceProperty(PROP_DATATYPE_TYPE, ""+dataType );
    }

    public void setFileType(String fileType){
        this.fileType = fileType;
    }

    public String getFileType(){
        return this.fileType;
    }

    
    /**
     *  Returns the property PROP_LOV as a list of ListItem....
     *  Columns name are looked for in the property PROP_LOV,
     *  name="LABEL" and value="value"
     *  i.e.
     *  <resourceProperty name="PROP_LOV">
     *      <resourceProperty name="1">
     *              <value>test1</value>
     *      </resourceProperty>
     *      <resourceProperty name="2">
     *              <value>test2</value>
     *      </resourceProperty>
     *  </resourceProperty>
     *  are in the list.
     */
    public List getListOfValues() {
        
        ResourceProperty rp = getResourceProperty( PROP_LOV );
        
        java.util.List listOfValues = new java.util.ArrayList();
        if (rp != null)
        {
            for (int i=0; i<rp.getProperties().size(); ++i)
            {
                ResourceProperty rpChild = (ResourceProperty)rp.getProperties().get(i);
                ListItem li = new ListItem(rpChild.getValue() != null ? rpChild.getValue() : rpChild.getName(),  rpChild.getName() );
                listOfValues.add(li);
            }
        }
        
        return listOfValues;
    }

    /**
     * Convenient way to create tje LOV property from a list of ListItem
     */
    public void setListOfValues(List listOfValues) {
        
        ResourceProperty rp = new ResourceProperty( PROP_LOV );
        
        for (int i=0; i<listOfValues.size(); ++i)
        {
            ListItem li = (ListItem)listOfValues.get(i);
            rp.getProperties().add( new  ResourceProperty(li.getValue()+"", li.getLabel() ) );
        }
        
        setResourceProperty(rp);
    }

    public byte getControlType() {
        String s = getResourcePropertyValue( PROP_INPUTCONTROL_TYPE );
        if (s == null || s.length() == 0) return 0;
        return Byte.valueOf( s ).byteValue();
    }

    public void setControlType(byte controlType) {
        setResourceProperty(PROP_INPUTCONTROL_TYPE, ""+controlType );
    }

    public boolean isMandatory() {
        String s = getResourcePropertyValue( PROP_INPUTCONTROL_IS_MANDATORY );
        if (s != null) return s.equals("true");
        return false;
    }

    public void setMandatory(boolean mandatory) {
        setResourceProperty(PROP_INPUTCONTROL_IS_MANDATORY, ""+mandatory );
    }

    public boolean isReadOnly() {
        String s = getResourcePropertyValue( PROP_INPUTCONTROL_IS_READONLY );
        if (s != null) return s.equals("true");
        return false;
    }

    public void setReadOnly(boolean readOnly) {
        setResourceProperty(PROP_INPUTCONTROL_IS_READONLY, ""+readOnly );
    }

    public boolean isVisible() {
        String s = getResourcePropertyValue( PROP_INPUTCONTROL_IS_VISIBLE );
        if (s != null) return s.equals("true");
        return true;
    }

    public void setVisible(boolean visible) {
        setResourceProperty(PROP_INPUTCONTROL_IS_VISIBLE, "" + visible );
    }

    public String getBeanName() {
        return getResourcePropertyValue( PROP_DATASOURCE_BEAN_NAME );
    }

    public void setBeanName(String beanName) {
        setResourceProperty(PROP_DATASOURCE_BEAN_NAME, ""+beanName );
    }

    public String getBeanMethod() {
        return getResourcePropertyValue( PROP_DATASOURCE_BEAN_METHOD );
    }

    public void setBeanMethod(String beanMethod) {
        setResourceProperty(PROP_DATASOURCE_BEAN_METHOD, ""+beanMethod );
    }

    public String getSql() {
        return getResourcePropertyValue( PROP_QUERY );
    }

    public void setSql(String sql) {
        setResourceProperty(PROP_QUERY, sql );
    }

    /**
     *  Return the set of visible columns as a String array....
     *  Columns name are looked for in the property PROP_QUERY_VISIBLE_COLUMNS,
     *  all children of this property with type  PROP_QUERY_VISIBLE_COLUMN_NAME
     *  are in the list.
     */
    public String[] getQueryVisibleColumns() {
        ResourceProperty rp = getResourceProperty( PROP_QUERY_VISIBLE_COLUMNS );
        
        java.util.List columnList = new java.util.ArrayList();
        if (rp != null)
        {
            
            
            for (int i=0; i<rp.getProperties().size(); ++i)
            {
                ResourceProperty rpChild = (ResourceProperty)rp.getProperties().get(i);
                if (rpChild.getName().equals( PROP_QUERY_VISIBLE_COLUMN_NAME ))
                {
                    columnList.add( rpChild.getValue());
                }
            }
            
            String[] columns = new String[columnList.size()];
            for (int i=0; i<columnList.size(); ++i)
            {
                columns[i] = "" + columnList.get(i);
            }
            
            return columns;
        }
        return new String[0];
    }

    /**
     * Set the list of columns using a String array
     * The result is a new ResourceProperty (PROP_QUERY_VISIBLE_COLUMNS) filled with a set
     * of children, one per column.
     */
    public void setQueryVisibleColumns(String[] queryVisibleColumns) {
        
        ResourceProperty rp = new ResourceProperty(PROP_QUERY_VISIBLE_COLUMNS);
        
        for (int i=0; i<queryVisibleColumns.length; ++i)
        {
            rp.getProperties().add( new ResourceProperty(PROP_QUERY_VISIBLE_COLUMN_NAME,queryVisibleColumns[i]));
        }
        
        setResourceProperty(rp);
    }

    public String getQueryValueColumn() {
        return getResourcePropertyValue( PROP_QUERY_VALUE_COLUMN );
    }

    public void setQueryValueColumn(String queryValueColumn) {
        setResourceProperty(PROP_QUERY_VALUE_COLUMN, queryValueColumn );
    }

    /**
     *  Return the property PROP_QUERY_DATA as set of InputControlQueryDataRow
     *  the structure is as follow:
     *   PROP_QUERY_DATA { PROP_QUERY_DATA_ROW { PROP_QUERY_DATA_COLUMN_VALUE } } }
     *  This method is performed only once, and the result is cached in queryDataCache. Subsequent calls
     *  to this method will return always queryDataCache.  
     *      
     */
    public java.util.List getQueryData() {
        
        if (queryDataCache != null) return queryDataCache;
        
        queryDataCache = new java.util.ArrayList();
        
        ResourceProperty rp = getResourceProperty( PROP_QUERY_DATA );
        if (rp != null)
        {
            // Look for rows....
            for (int i=0; i<rp.getProperties().size(); ++i)
            {
                ResourceProperty rpRow = (ResourceProperty)rp.getProperties().get(i);
                if (rpRow.getName().equals( PROP_QUERY_DATA_ROW ))
                {
                    InputControlQueryDataRow icqdr = new InputControlQueryDataRow();
                    icqdr.setValue( rpRow.getValue() );
                    
                    // Look for row details...
                    for (int k=0; k<rpRow.getProperties().size(); ++k)
                    {
                        ResourceProperty rpRowChild = (ResourceProperty)rpRow.getProperties().get(k);
                        if (rpRowChild.getName().equals( PROP_QUERY_DATA_ROW_COLUMN ))
                        {
                            icqdr.getColumnValues().add(  rpRowChild.getValue() );
                        }
                    }
                    
                    queryDataCache.add(icqdr );
                }
            }
            
        }
        return queryDataCache;
    }

    /**
     *  Convenient way to create the PROP_QUERY_DATA properties from a set of InputControlQueryDataRow
     *  the structure will be create as follow:
     *   PROP_QUERY_DATA { PROP_QUERY_DATA_ROW { PROP_QUERY_DATA_COLUMN_VALUE } } }
     *  A call to this method will set to null the queryDataCache
     *      
     */
    public void setQueryData(java.util.List queryData) {
        
        queryDataCache = null;
        
        ResourceProperty rp = new ResourceProperty(PROP_QUERY_DATA);
        
        for (int i=0; i<queryData.size(); ++i)
        {
            InputControlQueryDataRow icqdr = (InputControlQueryDataRow)queryData.get(i);
            
            ResourceProperty rpRow = new ResourceProperty(PROP_QUERY_DATA_ROW, "" + icqdr.getValue());
            
            for (int k=0; k<icqdr.getColumnValues().size(); ++k)
            {
                Object columnValue = icqdr.getColumnValues().get(k);
                rpRow.getProperties().add( new ResourceProperty( PROP_QUERY_DATA_ROW_COLUMN, (columnValue == null) ? "" : ""+columnValue));
            }
            
            rp.getProperties().add( rpRow );
        }
        
        setResourceProperty(rp);
    
    }

    /**
     * Return the List of properties. Don't add properties directly!
     * Use setResourceProperty instead!
     */
    public java.util.List getProperties() {
        return properties;
    }

    /**
     * Replace all the properties with the specified list. The internal hashmap is
     * updated.
     */
    public void setProperties(java.util.List properties) {
        this.properties = properties;
        hm.clear();
        for (int i=0; i<properties.size(); ++i)
        {
            ResourceProperty rp = (ResourceProperty)properties.get(i);
            hm.put(rp.getName(), rp);
        }
    }
    
    /**
     * Setting a property to a null value is the same as remove it.
     *
     */
    public void setResourceProperty(String resourcePropertyName, String value)
    {
        if (resourcePropertyName == null) return;
        if (value == null)
        {
            removeResourceProperty(resourcePropertyName);
        }
        else
        {
            ResourceProperty rp = new ResourceProperty(resourcePropertyName);
            rp.setValue( value );
            setResourceProperty(rp);
        }
    }
    
    /**
     * Set a boolean resource property value.
     * 
     * @param resourcePropertyName the property name
     * @param value the value
     * @see #setResourceProperty(String, String)
     * @see #getResourcePropertyValueAsBoolean(String)
     */
    public void setResourceProperty(String resourcePropertyName, boolean value)
    {
    	setResourceProperty(resourcePropertyName, Boolean.toString(value));
    }
    
    /**
     * Set an integer resource property value.
     * 
     * @param resourcePropertyName the property name
     * @param value the value
     * @see #setResourceProperty(String, String)
     * @see #getResourcePropertyValueAsInteger(String)
     */
    public void setResourceProperty(String resourcePropertyName, int value)
    {
    	setResourceProperty(resourcePropertyName, Integer.toString(value));
    }
    
    /**
     * Add or replace the resource property in the
     * ResourceDescriptor.
     */
    public void setResourceProperty(ResourceProperty rp)
    {
        if (rp == null) return;
        removeResourceProperty( rp.getName());
        this.getProperties().add( rp );
        this.hm.put( rp.getName(), rp);
    }
    
    /**
     * Remove all the resource properties with name =  rp.getName()
     */
    public void removeResourceProperty(ResourceProperty rp)
    {
        removeResourceProperty(rp.getName());
    }
    
    /**
     * Remove all resources with name = resourcePropertyName
     */
    public void removeResourceProperty(String resourcePropertyName)
    {
        Object obj = this.hm.remove(resourcePropertyName);
        if (obj != null)
        {
           this.getProperties().remove( obj );
        }
    }
    
    public ResourceProperty getResourceProperty(String resourcePropertyName)
    {
        return (ResourceProperty)hm.get(resourcePropertyName);
    }
    
    /**
     * Return the value of the property resourcePropertyName as String
     * Return null if the property is not found or the [operty value is null.
     *
     */
    public String getResourcePropertyValue(String resourcePropertyName)
    {
        ResourceProperty rp = getResourceProperty( resourcePropertyName );
        if (rp != null) return rp.getValue();
        
        return null;
    }
    
    /**
     * Return the value of the property resourcePropertyName as Boolean
     * Return null if the property is not found
     *
     */
    public Boolean getResourcePropertyValueAsBoolean(String resourcePropertyName)
    {
        try {
            ResourceProperty rp = getResourceProperty( resourcePropertyName );
            if (rp != null) return Boolean.valueOf(  rp.getValue() );
        } catch (Exception ex){ }
        return null;
    }
    
    /**
     * Return the value of the property resourcePropertyName as Integer
     * Return null if the property is not found or is not an integer
     *
     */
    public Integer getResourcePropertyValueAsInteger(String resourcePropertyName)
    {
        try {
            ResourceProperty rp = getResourceProperty( resourcePropertyName );
            if (rp != null) return Integer.valueOf(  rp.getValue() );
        } catch (Exception ex){ }
        return null;
    }

    public List getParameters() {
        return parameters;
    }

    public void setParameters(List parameters) {
        this.parameters = parameters;
    }
    
}
