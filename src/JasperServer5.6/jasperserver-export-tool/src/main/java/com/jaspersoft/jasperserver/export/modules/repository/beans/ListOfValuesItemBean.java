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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;

/**
 * @author tkavanagh
 * @version $Id: ListOfValuesItemBean.java 47331 2014-07-18 09:13:06Z kklein $
 */

/*
 * This bean class represents ListOfValuesItem. 
 * 
 * ListOfValuesItem does not inherit from Resource, therefore this bean does not
 * extend ResourceBean.
 * 
 */
public class ListOfValuesItemBean {

	private String label;
	private Object value;
	
	public ListOfValuesItemBean() {
	}
	
	public ListOfValuesItemBean(ListOfValuesItem item) {
		this.label = item.getLabel();
		this.value = item.getValue();
	}
	
	public void copyTo(ListOfValuesItem item) {
		item.setLabel(getLabel());
		item.setValue(getValue());
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}
