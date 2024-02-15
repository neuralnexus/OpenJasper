/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;

/**
 * @author tkavanagh
 * @version $Id$
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
