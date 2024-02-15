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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;
import net.sf.jasperreports.engine.JRParameter;

import java.io.Serializable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JasperReportInputControlInformation.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JasperReportInputControlInformation implements
		ReportInputControlInformation, Serializable {

	private static final long serialVersionUID = 1L;
	
	private JRParameter reportParameter;
	private String promptLabel;
	private Object defaultValue;
	private ReportInputControlValuesInformation reportInputControlValuesInformation;

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getPromptLabel() {
		return promptLabel;
	}

    public String getParameterName() {
        return reportParameter.getName();
    }

    public Class getValueType() {
		return reportParameter.getValueClass();
	}

	public Class getNestedType() {
		return reportParameter.getNestedType();
	}

	public JRParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(JRParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public void setPromptLabel(String promptLabel) {
		this.promptLabel = promptLabel;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

    public ReportInputControlValuesInformation getReportInputControlValuesInformation() {
        return reportInputControlValuesInformation;
    }

    public void setReportInputControlValuesInformation(ReportInputControlValuesInformation valuesInformation){
        this.reportInputControlValuesInformation = valuesInformation;
    }
}
