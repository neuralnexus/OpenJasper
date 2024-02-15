/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.reports.ReportParameter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        if ((reportParameters != null && that.reportParameters == null)
                || (reportParameters == null && that.reportParameters != null)) return false;
        if (reportParameters != null && that.reportParameters != null) {
            Set<ReportParameter> set1 = new  HashSet<ReportParameter>();
            set1.addAll(reportParameters);
            Set<ReportParameter> set2 = new HashSet<ReportParameter>();
            set2.addAll(that.reportParameters);
            if (!set1.equals(set2)) return false;
        }
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
