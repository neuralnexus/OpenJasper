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

/**
 * Represents data that is returned from the web services calls.
 *
 */

public class FileContent {
	private byte[] bytes;
	private String mimeType;
	private String name;

	public FileContent() {
	}

	public void setData(byte[] bytes) { this.bytes = bytes; }
	public void setMimeType(String mimeType) { this.mimeType = mimeType; }

	public byte[] getData() { return bytes; }
	public String getMimeType() { return mimeType; }

	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
}
