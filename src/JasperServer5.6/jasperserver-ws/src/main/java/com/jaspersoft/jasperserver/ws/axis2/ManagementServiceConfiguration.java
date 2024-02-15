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

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;

/**
 * A configuration interface for {@link ManagementService}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ManagementServiceConfiguration.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface ManagementServiceConfiguration {

	/**
	 * Returns an export parameters object for a specific output format.
	 * 
	 * @param outputFormat the output format, as given by the
	 * {@link Argument#RUN_OUTPUT_FORMAT RUN_OUTPUT_FORMAT argument}.
	 * @return an export parameters object for the output format, or <code>null</code>
	 * if no export parameters configured for the format
	 */
	ExportParameters getExportParameters(String outputFormat);

    /**
     * Returns uri of temp folder.
     *
     * @return uri of temp folder.
     */
    String getTempFolder();

    /**
     * Returns role name for which temp folder should be visible.
     *
     * @return role name for which temp folder should be visible.
     */
    String getRoleToAccessTempFolder();
	/**
	 * Returns an exporter object for a specific output format.
	 * 
	 * @param outputFormat the output format, as given by the
	 * {@link Argument#RUN_OUTPUT_FORMAT RUN_OUTPUT_FORMAT argument}.
	 * @return an exporter object for the output format, or <code>null</code>
	 * if no export parameters configured for the format
	 */
	WSExporter getExporter(String outputFormat);
	
}
