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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.Serializable;
import java.util.Date;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.AttributedObject;


/**
 * The base interface for any JasperServer resource. It has many implementations.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: Resource.java 47331 2014-07-18 09:13:06Z kklein $
 */
@JasperServerAPI
public interface Resource extends AttributedObject, InternalURI, Serializable
{
	public static String URI_PROTOCOL = "repo";
	
	int VERSION_NEW = -1;
	
    /**
     * Returns the version of the resource.
     *
     * @return version number
     */
	public int getVersion();

    /**
     * Sets the version for the resource.
     *
     * @param version
     */
	public void setVersion(int version);

    /**
     * Returns the URI address of the resource.
     *
     * @return URI
     */
	public String getURIString();

    /**
     * Sets the URI address for the resource.
     *
     * @param uri
     */
	public void setURIString(String uri);

    /**
     * Returns the URI address of parent folder of the resource.
     *
     * @return URI
     */
	public String getParentFolder();

    /**
     * Sets the URI address of parent folder for the resource.
     *
     * @param uri
     */
	public void setParentFolder(String uri);

    /**
     * Sets the reference of parent folder for the resource.
     *
     * @param folder
     */
	public void setParentFolder(Folder folder);

	/**
     * Returns the name of the resource.
	 *
     * @return name
	 */
	public String getName();

    /**
     * Sets the name of the resource.
     *
     * @param name
     */
	public void setName(String name);

    /**
     * Returns the label of the resource.
     *
     * @return label
     */
	public String getLabel();

    /**
     * Sets the label of the resource.
     *
     * @param label
     */
	public void setLabel(String label);

    /**
     * Returns the description of the resource.
     *
     * @return description
     */
	public String getDescription();

    /**
     * Sets the description of the resource.
     *
     * @param description
     */
	public void setDescription(String description);


    /**
     * Returns the name of actual resource class.
     *
     * @return class name
     */
	public String getResourceType();

    /**
     * Compares the types of two resources.
     *
     * @return <code>true</code> if the resources have same type.
     */
	public boolean isSameType(Resource resource);

    /**
     * Returns the date when resource was created.
     *
     * @return date
     */
	public Date getCreationDate();

    /**
     * Sets the resource creation date.
     *
     * @param timestamp
     */
	public void setCreationDate(Date timestamp);

    /**
     * Returns the date when resource was updated last time.
     *
     * @return date
     */
	public Date getUpdateDate();

    /**
     * Sets the resource update date.
     *
     * @param timestamp
     */
	public void setUpdateDate(Date timestamp);

    /**
     * Returns <code>true</code> if the resource was never updated yet.
     *
     * @return <code>true</code> if the resource is new
     */
    public boolean isNew();
}
