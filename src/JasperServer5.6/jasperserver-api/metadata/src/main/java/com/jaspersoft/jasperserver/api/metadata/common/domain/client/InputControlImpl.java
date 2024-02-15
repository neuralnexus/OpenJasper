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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: InputControlImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class InputControlImpl extends ResourceImpl implements InputControl
{
	
	/**
	 * 
	 */
	private byte type = TYPE_SINGLE_VALUE;
	private boolean isMandatory = false;
	private boolean isReadOnly = false;
	private boolean isVisible= true;
	private ResourceReference dataType = null;
	private ResourceReference listOfValues = null;
	private ResourceReference query = null;
	private List queryVisibleColumns = new ArrayList();
	private String queryValueColumn = null;
	private Object defaultValue = null;
	private List defaultValues = null;

    public InputControlImpl() {
    }

    public InputControlImpl(InputControlImpl another) {
        super(another);

        if (another != null) {
            this.type = another.type;
            this.isMandatory = another.isMandatory;
            this.isReadOnly = another.isReadOnly;
            this.isVisible = another.isVisible;
            // TODO: set copy of references for dataType, listOfValues and query.
            this.dataType = another.dataType;
            this.listOfValues = another.listOfValues;
            this.query = another.query;
            this.queryVisibleColumns = another.queryVisibleColumns;
            this.queryValueColumn = another.queryValueColumn;
            this.defaultValue = another.defaultValue;
            this.defaultValues = another.defaultValues != null ? Arrays.asList(another.defaultValues.toArray().clone()) : null;
        }
    }

    /**
	 * 
	 */
	public byte getType()
	{
		return type;
	}

	/**
	 * 
	 */
	public void setType(byte type)
	{
		this.type = type;
	}

	/**
	 * 
	 */
	public boolean isMandatory()
	{
		return isMandatory;
	}

	/**
	 * 
	 */
	public void setMandatory(boolean isMandatory)
	{
		this.isMandatory = isMandatory;
	}

	/**
	 * 
	 */
	public boolean isReadOnly()
	{
		return isReadOnly;
	}

	/**
	 * 
	 */
	public void setReadOnly(boolean isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	/**
	 *
	 */
	public boolean isVisible()
	{
		return isVisible;
	}

	/**
	 *
	 */
	public void setVisible(boolean visible)
	{
		isVisible = visible;
	}

	/**
	 * 
	 */
	public ResourceReference getDataType()
	{
		return dataType;
	}

	/**
	 * 
	 */
	public void setDataType(ResourceReference dataType)
	{
		this.dataType = dataType;
	}

	public void setDataType(DataType dataType) {
		setDataType(new ResourceReference(dataType));
	}

	public void setDataTypeReference(String referenceURI) {
		setDataType(new ResourceReference(referenceURI));
	}

	/**
	 * 
	 */
	public ResourceReference getListOfValues()
	{
		return listOfValues;
	}

	/**
	 * 
	 */
	public void setListOfValues(ResourceReference values)
	{
		this.listOfValues = values;
	}

	public void setListOfValues(ListOfValues listOfValues) {
		setListOfValues(new ResourceReference(listOfValues));		
	}

	public void setListOfValuesReference(String referenceURI) {
		setListOfValues(new ResourceReference(referenceURI));		
	}

	/**
	 * 
	 */
	public ResourceReference getQuery()
	{
		return query;
	}

	/**
	 * 
	 */
	public void setQuery(ResourceReference query)
	{
		this.query = query;
	}

	public void setQuery(Query query) {
		setQuery(new ResourceReference(query));
	}

	public void setQueryReference(String referenceURI) {
		setQuery(new ResourceReference(referenceURI));
	}

	/**
	 * 
	 */
	public String[] getQueryVisibleColumns()
	{
		return (String[]) queryVisibleColumns.toArray(new String[queryVisibleColumns.size()]);
	}

	/**
	 *
	 */
	public List getQueryVisibleColumnsAsList()
	{
		return queryVisibleColumns;
	}

	/**
	 * 
	 */
	public void addQueryVisibleColumn(String column)
	{
		queryVisibleColumns.add(column);
	}

	/**
	 * 
	 */
	public void removeQueryVisibleColumn(String column)
	{
		queryVisibleColumns.remove(column);
	}

	/**
	 * 
	 */
	public String getQueryValueColumn()
	{
		return queryValueColumn;
	}

	/**
	 * 
	 */
	public void setQueryValueColumn(String column)
	{
		this.queryValueColumn = column;
	}

	/**
	 * 
	 */
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * 
	 */
	public void setDefaultValue(Object value)
	{
		this.defaultValue = value;
	}

	/**
	 * 
	 */
	public List getDefaultValues()
	{
		return defaultValues;
	}

	/**
	 * 
	 */
	public void setDefaultValues(List values)
	{
		this.defaultValues = values;
	}

	protected Class getImplementingItf() {
		return InputControl.class;
	}

}
