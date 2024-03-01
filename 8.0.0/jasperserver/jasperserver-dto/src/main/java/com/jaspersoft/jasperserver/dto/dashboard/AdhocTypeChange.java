/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */
package com.jaspersoft.jasperserver.dto.dashboard;

/**
*
* @author Lucian Chirita
*/
public class AdhocTypeChange implements RuntimeComponentAttribute {

	private String updatedChartType;

	public String getUpdatedChartType() {
		return updatedChartType;
	}

	public void setUpdatedChartType(String updatedChartType) {
		this.updatedChartType = updatedChartType;
	}
	
}
