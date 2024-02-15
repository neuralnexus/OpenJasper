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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRXhtmlExporter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRHtmlExportUtils.java 19932 2010-12-11 15:24:29Z tmatyashovsky $
 */
public class HtmlExportUtil 
{

	public static AbstractHtmlExporter getHtmlExporter(JasperReportsContext jasperReportsContext)
	{
		if (jasperReportsContext == null)
		{
			jasperReportsContext = DefaultJasperReportsContext.getInstance();
		}
		String htmlType = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty("com.jaspersoft.jasperreports.export.html.type");
		if ("xhtml".equalsIgnoreCase(htmlType))
		{
			return new JRXhtmlExporter(jasperReportsContext);
		}
		if ("html2".equalsIgnoreCase(htmlType))
		{
			return new HtmlExporter(jasperReportsContext);
		}
		return new JRHtmlExporter(jasperReportsContext);
	}
		
}
