/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.dto.reports;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ReportParameters implements DeepCloneable<ReportParameters> {
    private List<ReportParameter> reportParameters;

    public ReportParameters() { }

    public ReportParameters(List<ReportParameter> reportParameters) {
        this.reportParameters = reportParameters;
    }

    public ReportParameters(ReportParameters other) {
        checkNotNull(other);

        reportParameters = copyOf(other.getReportParameters());
    }

    @XmlElement(name = "reportParameter")
    public List<ReportParameter> getReportParameters() {
        return reportParameters;
    }

    public ReportParameters setReportParameters(List<ReportParameter> reportParameters) {
        this.reportParameters = reportParameters;
        return this;
    }

    // TODO: should this be serializable?
    public Map<String, String[]> getRawParameters(){
        Map<String, String[]> rawParameters = new HashMap<String, String[]>();
        if(reportParameters != null){
            for(ReportParameter currentParameter : reportParameters){
                rawParameters.put(
                        currentParameter.getName(),
                        currentParameter.getValues().toArray(new String[currentParameter.getValues().size()])
                );
            }
        }
        return rawParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportParameters that = (ReportParameters) o;

        return reportParameters != null ? reportParameters.equals(that.reportParameters) : that.reportParameters == null;
    }

    @Override
    public int hashCode() {
        return reportParameters != null ? reportParameters.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ReportParameters{" +
                "reportParameters=" + reportParameters +
                '}';
    }

    /*
     * DeepCloneable
     */

    @Override
    public ReportParameters deepClone() {
        return new ReportParameters(this);
    }
}
