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


package com.jaspersoft.jasperserver.rest.model;

import java.util.Iterator;
import java.util.Map;
import javax.activation.DataSource;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * This class represent the model for the resource Report.
 * A report is the result of a report execution which in REST is performed using
 * the POST method on a repository resource (usually of type ReportUnit).
 * The execution creates a this REST resource and put it in the session, even if
 * it could be stored in a persistent storage to be used even after the
 * session expiration.
 *
 * By default this type of resource is not persistent, and it is stored in the
 * @author gtoffoli
 */
public class Report {

    private String uuid;

    private String originalUri;
    private JasperPrint jasperPrint;
    private Map<String, DataSource> attachments;

    /**
     * Get the xml representation of this report;
     * 
     * @return
     */
    public String toXml()
    {
        StringBuilder xml = new StringBuilder();

        xml.append("<report>\n");
        xml.append("\t").append("<uuid>").append(getUuid()).append("</uuid>\n");
        xml.append("\t").append("<originalUri>").append(getOriginalUri()).append("</originalUri>\n");
        xml.append("\t").append("<totalPages>").append(getJasperPrint().getPages().size()).append("</totalPages>\n");
        xml.append("\t").append("<startPage>").append(1).append("</startPage>\n");
        xml.append("\t").append("<endPage>").append(getJasperPrint().getPages().size()).append("</endPage>\n");

        if (getAttachments() != null)
        {
            Iterator<String> namesIterator = getAttachments().keySet().iterator();
            while (namesIterator.hasNext())
            {
                String name = namesIterator.next();
                DataSource ds = getAttachments().get(name);
                xml.append("\t").append("<file type=\"").append(ds.getContentType()).append("\"><![CDATA[").append( name ).append("]]></file>\n");
            }
        }

        xml.append("</report>");

        return xml.toString();
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the jasperPrint
     */
    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    /**
     * @param jasperPrint the jasperPrint to set
     */
    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    /**
     * @return the attachments
     */
    public Map<String, DataSource> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(Map<String, DataSource> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the originalUri
     */
    public String getOriginalUri() {
        return originalUri;
    }

    /**
     * @param originalUri the originalUri to set
     */
    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

}
