/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
package com.jaspersoft.jasperserver.dto.dashboard;

/**
*
* @author Lucian Chirita
*/
public class ComponentReportExecution implements RuntimeComponentAttribute {

	private String reportExecutionId;
	
	public ComponentReportExecution() {
	}
	
	public ComponentReportExecution(String reportExecutionId) {
		this.reportExecutionId = reportExecutionId;
	}

	public String getReportExecutionId() {
		return reportExecutionId;
	}

	public void setReportExecutionId(String reportExecutionId) {
		this.reportExecutionId = reportExecutionId;
	}

}
