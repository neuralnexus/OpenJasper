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

package com.jaspersoft.jasperserver.export.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ZipFileInput.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ZipFileInput extends BaseImportInput {
	
	private final File file;
	private final ZipFileInputManager manager;
	private PathProcessor pathProcessor;
	
	private ZipFile zipFile;

	public ZipFileInput(String zipFilename, PathProcessor pathProcessor, ZipFileInputManager manager) {
        this(new File(zipFilename), pathProcessor, manager);
	}

    public ZipFileInput(File zipFile, PathProcessor pathProcessor, ZipFileInputManager manager) {
		this.file = zipFile;
		this.pathProcessor = pathProcessor;
		this.manager = manager;
	}

	public void open() throws IOException {
		zipFile = new ZipFile(file, ZipFile.OPEN_READ);
	}

	public void close() throws IOException {
		zipFile.close();
	}

	public boolean fileExists(String path) {
		ZipEntry entry = zipFile.getEntry(getZipPath(path));
		return entry != null && !entry.isDirectory();
	}

	public boolean folderExists(String path) {
		ZipEntry entry = zipFile.getEntry(getZipPath(path));
		return entry != null && entry.isDirectory();
	}

	public InputStream getFileInputStream(String path) throws IOException {
		ZipEntry entry = zipFile.getEntry(getZipPath(path));
		return zipFile.getInputStream(entry);
	}

	protected String getZipPath(String path) {
		return pathProcessor.processPath(path);
	}
	
	public void propertiesRead(Properties properties) {
		manager.updateInputProperties(this, properties);
	}

	public void setPathProcessor(PathProcessor pathProcessor) {
		this.pathProcessor = pathProcessor;
	}

}
