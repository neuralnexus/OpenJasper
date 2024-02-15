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

package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JasperReportsContext;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: CsvReportOutput.java 19940 2010-12-13 09:29:40Z tmatyashovsky $
 */
public abstract class AbstractReportOutput implements Output 
{
	private Boolean isIgnorePagination;
	private boolean compress = false;
	private JasperReportsContext jasperReportsContext;
	
	/** 
	 *
	 */
	public Boolean isIgnorePagination()
	{
		return this.isIgnorePagination; 
	}
	
	/** 
	 *
	 */
	public void setIgnorePagination(Boolean isIgnorePagination)
	{
		this.isIgnorePagination = isIgnorePagination; 
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}
	
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		return isIgnorePagination() == null ? null : !isIgnorePagination();
	}
}
