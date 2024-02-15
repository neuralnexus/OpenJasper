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
package com.jaspersoft.jasperserver.war.action;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

public class ListReportsAction extends FormAction
{
	private RepositoryService repository;
	private ConfigurationBean configuration;

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public ConfigurationBean getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(ConfigurationBean configuration)
	{
		this.configuration = configuration;
	}

    private boolean isReportFilteredOut(ResourceLookup report) {
        boolean isFilteredOut = false;
        for (int i = 0; i < configuration.getViewReportsFilterList().size(); i++) {
            String regexp = (String)configuration.getViewReportsFilterList().get(i);
            Matcher matcher = Pattern.compile(regexp).matcher(report.getURIString());
            if (matcher.find()) {
                isFilteredOut = true;
                break;
            }
        }

        return isFilteredOut;
    }

	public Event listReports(RequestContext context)
	{
		List reportUnits = loadReports(context);
		
        // filter out everything under /temp/, /adhoc/topic/ etc.,  fix bug #8100, #12512
		List filteredReportUnits = new ArrayList();
		for (int i=0; i<reportUnits.size(); i++) {
			if (!isReportFilteredOut((ResourceLookup)reportUnits.get(i))) {
				//String parentUri = ((Resource)reportUnits.get(i)).getParentFolder();
				//((Resource)reportUnits.get(i)).setName(getParentFolderDisplayName(parentUri));
				filteredReportUnits.add(reportUnits.get(i));
			}				
		}
		context.getRequestScope().put("reportUnits", filteredReportUnits);
		return success();
	}

	protected List loadReports(RequestContext context)
	{
		List reportUnits = repository.loadResourcesList(
			JasperServerUtil.getExecutionContext(context),
			FilterCriteria.createFilter(ReportUnit.class)
			);
		return reportUnits;
	}

	public Event goToPage(RequestContext context)
	{
		context.getFlowScope().put(
			PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER, 
			context.getRequestParameters().get(PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER)
			);

		return success();
	}
	
	private String getParentFolderDisplayName(String uri) {
		int fromIndex = 1;

		if (uri.equals("/")) {
		   return "/root";
		}
		
	    StringBuffer displayLabel = new StringBuffer("/root");
		if (uri.length() > 1) {
			int lastIndex = uri.lastIndexOf("/");
			while ((fromIndex = uri.indexOf('/', fromIndex)) != -1) {	    		   
				String currentUri = uri.substring(0, uri.indexOf('/', fromIndex));	 	
 
				displayLabel.append("/").append(repository.getFolder(null, currentUri).getLabel());	 


				if (lastIndex == fromIndex) {
					break; 
				}
				fromIndex++;
			}
			displayLabel.append("/").append(repository.getFolder(null, uri).getLabel()); 	    		   


		}	       
	
		return displayLabel.toString();
	}
}
