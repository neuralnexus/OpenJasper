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
package com.jaspersoft.jasperserver.remote.exporters;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: OdsExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service("remoteOdsExporter")
@Scope("prototype")
public class OdsExporter extends XlsExporter{
    @Override
    public String getContentType() {
        return "application/vnd.oasis.opendocument.spreadsheet";
    }

    @Override
    public JRExporter createExporter() throws Exception {
        return new JROdsExporter(getJasperReportsContext());
    }

    @Resource(name = "odsExportParameters")
    @Override
    public void setExportParams(XlsExportParametersBean exportParams) {
        super.setExportParams(exportParams);
    }
}
