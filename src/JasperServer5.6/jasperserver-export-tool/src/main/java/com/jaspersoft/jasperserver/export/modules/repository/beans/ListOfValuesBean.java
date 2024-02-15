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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;


/**
 * @author tkavanagh
 * @version $Id: ListOfValuesBean.java 47331 2014-07-18 09:13:06Z kklein $
 */


public class ListOfValuesBean extends ResourceBean {

	
	private ListOfValuesItemBean[] items;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		ListOfValues lov = (ListOfValues) res;
		copyItemsFrom(lov);
	}

	protected void copyItemsFrom(ListOfValues lov) {
		ListOfValuesItem[] lovItems = lov.getValues();
		if (lovItems == null || lovItems.length == 0) {
			items = null;
		} else {
			items = new ListOfValuesItemBean[lovItems.length];
			for (int i = 0; i < lovItems.length; i++) {
				ListOfValuesItem lovItem = lovItems[i];
				ListOfValuesItemBean itemBean = new ListOfValuesItemBean(lovItem);
				items[i] = itemBean;
			}
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		ListOfValues lov = (ListOfValues) res;
		copyItemsTo(lov);
	}

	protected void copyItemsTo(ListOfValues lov) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				ListOfValuesItemBean item = items[i];
				ListOfValuesItem lovItem = new ListOfValuesItemImpl();
				item.copyTo(lovItem);
				lov.addValue(lovItem);
			}
		}
	}

	public ListOfValuesItemBean[] getItems() {
		return items;
	}

	public void setItems(ListOfValuesItemBean[] values) {
		this.items = values;
	}

}
