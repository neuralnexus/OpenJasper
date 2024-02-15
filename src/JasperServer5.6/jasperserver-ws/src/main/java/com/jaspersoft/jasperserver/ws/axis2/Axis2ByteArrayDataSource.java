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
package com.jaspersoft.jasperserver.ws.axis2;

import java.io.IOException;
import javax.activation.DataSource;
import java.io.ByteArrayInputStream;

/**
 * This class is used to transfer file usimg MTOM and SOAP with Attachments
 * @author gtoffoli
 */
public class Axis2ByteArrayDataSource implements DataSource{
    
   private byte[] byteArray = null;
    private String name = "";
    
    /** Creates a new instance of ResourceDataSource */
    public Axis2ByteArrayDataSource(String name, byte[] byteArray) {
        this.name = name;
        this.byteArray = byteArray;
    }

    public java.io.InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(byteArray);
    }

    public java.io.OutputStream getOutputStream() throws IOException {
        throw new java.io.IOException();
    }

    public String getContentType() {
        return "application/octet-stream";
    }

    public String getName() {
        return name;
         
    }
    
}
