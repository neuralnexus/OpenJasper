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
package com.jaspersoft.jasperserver.war.dto;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: ListOfValuesDTO.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ListOfValuesDTO extends BaseDTO
{
	private ListOfValues listOfValues;
	private String newLabel;
	private String newValue;

	public ListOfValuesDTO(ListOfValues listOfValues)
	{
		this.listOfValues = listOfValues;
	}

	public String getNewLabel()
	{
		return newLabel;
	}

	public void setNewLabel(String newLabel)
	{
		this.newLabel = newLabel;
	}

	public String getNewValue()
	{
		return newValue;
	}

	public void setNewValue(String newValue)
	{
		this.newValue = newValue;
	}

	public ListOfValues getListOfValues()
	{
		return listOfValues;
	}

	public void setListOfValues(ListOfValues listOfValues)
	{
		this.listOfValues = listOfValues;
	}
}
