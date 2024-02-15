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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;


/**
 *
 * 
 * Created 5/15/2013 @author nthapa
 * 
 */
public class DataSourceDTO implements DataSource{
// TODO UDI check

	private String contentType;
	private String name;
	private byte[] bInputStream;
	private byte[] bOutputStream;
	
	public void setContentType(String content_Type)
	{
		this.contentType=content_Type;
	}
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		ByteArrayInputStream bstream= new ByteArrayInputStream(bInputStream);
		return (InputStream) bstream;
		
		//return null;
	}

	public void setiByteArray(InputStream br) throws IOException
	{
		br.read(bInputStream);
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public void setName(String name)
	{
		this.name=name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bostream= new ByteArrayOutputStream();
		return (OutputStream)bostream;
		
	}	

  
}
