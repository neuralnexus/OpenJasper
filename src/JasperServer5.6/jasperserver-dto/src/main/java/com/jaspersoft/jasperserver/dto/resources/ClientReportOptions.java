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
package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.reports.ReportParameter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author vsabadosh
 * @version $Id: ClientReportOptions.java 32880 2013-08-09 07:09:12Z inesterenko $
 */
@XmlRootElement(name = ResourceMediaType.REPORT_OPTIONS_CLIENT_TYPE)
public class ClientReportOptions extends ClientResource<ClientReportOptions> {
    private String reportUri;
    private List<ReportParameter> reportParameters;

    public String getReportUri() {
        return reportUri;
    }

    public ClientReportOptions setReportUri(String reportUri) {
        this.reportUri = reportUri;
        return this;
    }

    @XmlElementWrapper(name = "reportParameters")
    @XmlElement(name = "reportParameter")
    public List<ReportParameter> getReportParameters() {
        return reportParameters;
    }

    public ClientReportOptions setReportParameters(List<ReportParameter> reportParameters) {
        this.reportParameters = reportParameters;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientReportOptions that = (ClientReportOptions) o;

        if (reportParameters != null ? !reportParameters.equals(that.reportParameters) : that.reportParameters != null)
            return false;
        if (reportUri != null ? !reportUri.equals(that.reportUri) : that.reportUri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (reportUri != null ? reportUri.hashCode() : 0);
        result = 31 * result + (reportParameters != null ? reportParameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientReportOptions{" +
                "reportUri='" + reportUri + '\'' +
                ", reportParameters=" + reportParameters +
                ", version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }
}
