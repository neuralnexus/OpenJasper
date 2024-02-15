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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportInputDataParameterContributors.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportInputDataParameterContributors implements ReportDataParameterContributor {
	
	private ReportLoadingService reportLoadingService;

	@Override
	public Map<String, Object> getDataParameters(ExecutionContext context, 
			ReportUnitRequestBase request, ReportUnit reportUnit, JasperReport report) {
		// input control values are used as data parameters
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> params = request.getReportParameters();
		List<ResourceReference> inputControls = getReportLoadingService().getInputControlReferences(context, reportUnit);
		if (inputControls != null) {
			for (ResourceReference ref : inputControls) {
				String paramName = getInputControlParameterName(ref);
				if (paramName != null) {
					Object value = params == null ? null : params.get(paramName);
					inputParams.put(paramName, value);
				}
			}
		}
		return inputParams;
	}
	
	protected String getInputControlParameterName(ResourceReference inputControlRef) {
		// we only need the input control name
		String name = null;
		if (inputControlRef.isLocal()) {
			name = inputControlRef.getLocalResource().getName();
		} else {
			String referenceURI = inputControlRef.getReferenceURI();
			if (referenceURI != null) {
				name = RepositoryUtils.getName(referenceURI);
			}
		}
		return name;
	}

	public ReportLoadingService getReportLoadingService() {
		return reportLoadingService;
	}

	public void setReportLoadingService(ReportLoadingService reportLoadingService) {
		this.reportLoadingService = reportLoadingService;
	}

}
