/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
package com.jaspersoft.jasperserver.dto.dashboard;

import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

import com.jaspersoft.jasperserver.dto.reports.ReportParameter;

/**
*
* @author Lucian Chirita
*/
public class ComponentReplace implements RuntimeComponentAttribute {

	private String resource;
	
	private List<ReportParameter> parameters;

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	@XmlElementWrapper(name = "parameters")
	public List<ReportParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ReportParameter> parameters) {
		this.parameters = parameters;
	}

}
