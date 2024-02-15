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

package com.jaspersoft.jasperserver.sample;
/*
 * WSClientSingleton.java
 *
 * Created on July 12, 2006, 4:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author gtoffoli
 */

import java.io.File;

import net.sf.jasperreports.engine.JasperPrint;
import com.jaspersoft.jasperserver.irplugin.JServer;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WSClient {

    protected JServer server = null;
    protected static String reportUnitDataSourceURI = "/datasources/JServerJdbcDS";
    protected static String contentFilesFolderUri = "/ContentFiles";

    public WSClient(String webServiceUrl, String username, String password) {
        server = new JServer();
        server.setUsername(username);
        server.setPassword(password);
        server.setUrl(webServiceUrl);
    }

    public String getContentFilesFolder() {
        return contentFilesFolderUri;
    }

    public java.util.List list(String uri) throws Exception {
        ResourceDescriptor rd = new ResourceDescriptor();
        rd.setWsType(ResourceDescriptor.TYPE_FOLDER);
        rd.setUriString(uri);
        return server.getWSClient().list(rd);
    }

    public ResourceDescriptor get(String uri) throws Exception {
        return get(uri, null);
    }

    public ResourceDescriptor get(String uri, java.util.List args) throws Exception {
        return get(uri, null, args);
    }

    public ResourceDescriptor get(String uri, File outputFile, java.util.List args) throws Exception {
        ResourceDescriptor rd = new ResourceDescriptor();
        rd.setUriString(uri);
        return server.getWSClient().get(rd, outputFile, args);
    }

    public ResourceDescriptor get(ResourceDescriptor rd, File outputFile, java.util.List args) throws Exception {
        return server.getWSClient().get(rd, outputFile, args);
    }

    public JasperPrint runReport(String reportUri, java.util.Map parameters) throws Exception {
        ResourceDescriptor rd = new ResourceDescriptor();
        rd.setWsType(ResourceDescriptor.TYPE_REPORTUNIT);
        rd.setUriString(reportUri);

        return server.getWSClient().runReport(rd, parameters);
    }
    
    /**
     * This method shows how to execute a report and get back
     * one or more files (i.e. an html with several images).
     * 
     * @param reportUri
     * @param parameters
     * @param arguments - The format. Possible formats: PDF,JRPRINT,HTML,XLS,XML,CSV,RTF
     * @return a Map containing produced files.
     * @throws Exception
     */
    public Map runReport(String reportUri, java.util.Map parameters, List arguments) throws Exception {
        ResourceDescriptor rd = new ResourceDescriptor();
        rd.setWsType(ResourceDescriptor.TYPE_REPORTUNIT);
        rd.setUriString(reportUri);
        
        return server.getWSClient().runReport(rd, parameters, arguments);
    }




    public ResourceDescriptor put(String type, String name, String label, String desc, String parentFolder) throws Exception {
        ResourceDescriptor rd = new ResourceDescriptor();
        rd.setName(name);
        rd.setLabel(label);
        rd.setDescription(desc);
        rd.setParentFolder(parentFolder);
        rd.setUriString(rd.getParentFolder() + "/" + rd.getName());
        rd.setWsType(type);
        rd.setIsNew(true);

        if (type.equalsIgnoreCase(ResourceDescriptor.TYPE_FOLDER)) {
            return server.getWSClient().addOrModifyResource(rd, null);
        } else if (type.equalsIgnoreCase(ResourceDescriptor.TYPE_REPORTUNIT)) {
            return putReportUnit(rd);
        }

        //shouldn't reach here
        return null;

    }


    public ResourceDescriptor update(String name, String label, String desc, String parentFolder) throws Exception {
        ResourceDescriptor rd = get(parentFolder + "/" + name);
        rd.setLabel(label);
        rd.setDescription(desc);
        rd.setIsNew(false);

        //remove children for the update to be effective
        rd.setChildren(new java.util.ArrayList());

        return server.getWSClient().addOrModifyResource(rd, null);
    }

    public void delete(String uri) throws Exception {
        ResourceDescriptor rd = new ResourceDescriptor();
        ;
        rd.setUriString(uri);

        server.getWSClient().delete(rd);
    }

    protected String getDataSourceUri() {
        return reportUnitDataSourceURI;
    }

    private ResourceDescriptor putReportUnit(ResourceDescriptor rd) throws Exception {
        File resourceFile = null;

        ResourceDescriptor tmpDataSourceDescriptor = new ResourceDescriptor();
        tmpDataSourceDescriptor.setWsType(ResourceDescriptor.TYPE_DATASOURCE);
        tmpDataSourceDescriptor.setReferenceUri(getDataSourceUri());
        tmpDataSourceDescriptor.setIsReference(true);
        rd.getChildren().add(tmpDataSourceDescriptor);

        ResourceDescriptor jrxmlDescriptor = new ResourceDescriptor();
        jrxmlDescriptor.setWsType(ResourceDescriptor.TYPE_JRXML);
        jrxmlDescriptor.setName("test_jrxml");
        jrxmlDescriptor.setLabel("Main jrxml");
        jrxmlDescriptor.setDescription("Main jrxml");
        jrxmlDescriptor.setIsNew(true);
        jrxmlDescriptor.setHasData(true);
        jrxmlDescriptor.setMainReport(true);
        rd.getChildren().add(jrxmlDescriptor);

        resourceFile = new File(getFileResourceURL("test.jrxml"));

        return server.getWSClient().addOrModifyResource(rd, resourceFile);

    }

    /**
     * Fetches the URL of the Files in the classpath
     *
     * @return file path
     */
    private String getFileResourceURL(String name) throws FileNotFoundException, UnsupportedEncodingException{
            File f = new File (URLDecoder.decode(getClass().getClassLoader().getResource(name).getFile(), "UTF-8"));
            if (!f.exists())
                throw new FileNotFoundException();

            return f.getPath();
 	}
}
