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
package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is just a wrapper of the current default management service configuration.
 * If we need more, we will provide more.
 *
 * @author gtoffoli
 */
@Service("remoteServiceConfiguration")
public class DefaultServicesConfiguration implements ServicesConfiguration {
    @Resource
    private Map exportParametersMap = new HashMap();
    @Resource
    private String tempFolder;
    @Resource
    private String roleToAccessTempFolder;
    @Resource(name = "remoteExportersMap")
    private Map exportersMap = new HashMap();

    /**
     * Returns the map of export parameters beans indexed by output format.
     *
     * @return the map of export parameters beans
     */
    public Map getExportParametersMap() {
        return exportParametersMap;
    }

    /**
     * Sets a map of export parameters beans indexed by output format.
     *
     * @param exportParametersMap the export parameters beans map
     * @see #getExportParameters(String)
     */
    public void setExportParametersMap(Map exportParametersMap) {
        this.exportParametersMap = exportParametersMap;
    }

    /**
     * Returns the export parameters object from the map
     */
    public ExportParameters getExportParameters(String outputFormat) {
        return (ExportParameters) exportParametersMap.get(outputFormat);
    }

    /**
     * Returns the map of exporter beans indexed by output format.
     * @return Returns the map of exporter beans.
     */
    public Map getExportersMap() {
        return exportersMap;
    }

    /**
     * Sets a map of exporter beans indexed by output format.
     * @param exportersMap The map of exporter beans to set.
     */
    public void setExportersMap(Map exportersMap) {
        this.exportersMap = exportersMap;
    }

    /**
     * Returns the exporter object from the map
     */
    public ReportExporter getExporter(String outputFormat) {
        return (ReportExporter)exportersMap.get(outputFormat);
    }

    /**
     * {@inheritDoc}
     */
    public String getTempFolder() {
        return tempFolder;
    }

    /**
     * Sets uri of temp folder.
     *
     * @param tempFolder uri of temp folder.
     */
    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    /**
     * {@inheritDoc}
     */
    public String getRoleToAccessTempFolder() {
        return roleToAccessTempFolder;
    }

    /**
     * Sets role name for which temp folder should be visible.
     *
     * @param roleToAccessTempFolder role name for which temp folder should be visible.
     */
    public void setRoleToAccessTempFolder(String roleToAccessTempFolder) {
        this.roleToAccessTempFolder = roleToAccessTempFolder;
    }
}
