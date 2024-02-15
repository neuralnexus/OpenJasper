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
package com.jaspersoft.jasperserver.war.webflow;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JsonFlowViewResolver.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JsonFlowViewResolver implements ViewResolver, PriorityOrdered {
	
	private static final Log log = LogFactory.getLog(JsonModelView.class);

	private int order = Ordered.LOWEST_PRECEDENCE - 10;//default
	private String viewPrefix = "json:";//default value

	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		if (viewName.startsWith(viewPrefix)) {
			if (log.isDebugEnabled()) {
				log.debug("creting json view for " + viewName);
			}
			
			String modelName = viewName.substring(viewPrefix.length());
			// include flow execution key by default 
			return new JsonModelView("flowExecutionKey", modelName);
		}
		
		return null;
	}

	public String getViewIdByConvention(String viewStateId) {
		return viewStateId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getViewPrefix() {
		return viewPrefix;
	}

	public void setViewPrefix(String viewPrefix) {
		this.viewPrefix = viewPrefix;
	}

}
