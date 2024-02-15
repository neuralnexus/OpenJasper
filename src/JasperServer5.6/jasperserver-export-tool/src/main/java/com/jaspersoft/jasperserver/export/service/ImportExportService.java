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

package com.jaspersoft.jasperserver.export.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This is a facade on top of import/export tool
 *
 * @author ztomchenco
 */
public interface ImportExportService {
    /**
     * Entry point to import process
     *
     * @param input - stream, which contains zipped file with data to import
     * @param importParams - parameters for import
     * @param locale - locale for messages while importing
     * @throws ImportFailedException - if input data is corrupted or error occurs during import
     */
    public void doImport(InputStream input, Map<String, Boolean> importParams, Locale locale) throws ImportFailedException;

    /**
     * Entry point to import process
     *
     * @param input - pointer to file
     * @param importParams - parameters for import
     * @param locale - locale for messages while importing
     * @throws ImportFailedException - if input data is corrupted or error occurs during import
     */
    public void doImport(File input, Map<String, Boolean> importParams, Locale locale) throws ImportFailedException;

    /**
     * Entry point to the export process
     *
     * @param output - stream to output zip file with exports
     * @param exportParams - parameters for export process
     * @param urisOfResources - list of resources URI to be exported, or null if this option not used. Can't be empty
     * @param urisOfScheduledJobs - list of scheduled jobs URI to be exported, or null if this option not used. Can't be empty
     * @param rolesToExport - list of roles to be exported, or null if this option not used. If list is empty, all roles will be exported
     * @param usersToExport - list of users to be exported, or null if this option not used. If list is empty, all users will be exported
     * @param locale - locale for messages while exporting
     * @throws ExportFailedException - if something goes wrong during export process
     */
    public void doExport(OutputStream output, Map<String, Boolean> exportParams, List<String> urisOfResources, List<String> urisOfScheduledJobs, List<String> rolesToExport, List<String> usersToExport, Locale locale ) throws ExportFailedException;
}
