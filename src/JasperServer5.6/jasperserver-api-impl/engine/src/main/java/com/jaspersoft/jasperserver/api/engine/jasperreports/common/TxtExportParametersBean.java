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

package com.jaspersoft.jasperserver.api.engine.jasperreports.common;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: TxtExportParametersBean.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class TxtExportParametersBean extends AbstractExportParameters {
	
	public static final String PROPERTY_TEXT_PAGINATED = "com.jaspersoft.jrs.export.text.paginated";

	private Float characterWidth;
	private Float characterHeight;
	private Integer pageWidth;
	private Integer pageHeight;
	
	public Object getObject(){
		return this;
	}
	
	public void setPropertyValues(Object object){
		if(object instanceof TxtExportParametersBean){
			TxtExportParametersBean bean =(TxtExportParametersBean)object;
			this.setCharacterHeight(bean.getCharacterHeight());
			this.setCharacterWidth(bean.getCharacterWidth());
			this.setPageHeight(bean.getPageHeight());
			this.setPageWidth(bean.getPageWidth());
		}
	}

	/**
	 * @return Returns the characterWidth.
	 */
	public Float getCharacterWidth() {
		return characterWidth;
	}

	/**
	 * @param characterWidth The characterWidth to set.
	 */
	public void setCharacterWidth(Float characterWidth) {
		this.characterWidth = characterWidth;
	}

	/**
	 * @return Returns the characterHeight.
	 */
	public Float getCharacterHeight() {
		return characterHeight;
	}

	/**
	 * @param characterHeight The characterHeight to set.
	 */
	public void setCharacterHeight(Float characterHeight) {
		this.characterHeight = characterHeight;
	}

	/**
	 * @return Returns the pageWidth.
	 */
	public Integer getPageWidth() {
		return pageWidth;
	}

	/**
	 * @param pageWidth The pageWidth to set.
	 */
	public void setPageWidth(Integer pageWidth) {
		this.pageWidth = pageWidth;
	}

	/**
	 * @return Returns the pageHeight.
	 */
	public Integer getPageHeight() {
		return pageHeight;
	}

	/**
	 * @param pageHeight The pageHeight to set.
	 */
	public void setPageHeight(Integer pageHeight) {
		this.pageHeight = pageHeight;
	}
}
