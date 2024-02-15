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
package com.jaspersoft.jasperserver.api.engine.common.service;


import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Map;

/**
 * Service that provides status related to report execution
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: ReportExecutionStatusInformation.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 4.7
 */
@JasperServerAPI
public interface ReportExecutionStatusInformation {

    /**
     * get the request id of current running report job
     *
     * @return request id of current running report job
     */
    public String getRequestId();

    /**
     * get the report URI of current running report job if it`s available
     *
     * @return report URI of current running report job
     */
    public String getReportURI();

    /**
     * get all the properties of current job including: report uri, job ID, job label, fire time and parameters
     *
     * @return a Map that contains property keys and values of current job information
     */
    public Map<String, Object> getProperties();

    /**
     * Cancels/interrupts current report execution.
     *
     * @return whether the report execution has been canceled
     */
    public boolean cancel();

}
