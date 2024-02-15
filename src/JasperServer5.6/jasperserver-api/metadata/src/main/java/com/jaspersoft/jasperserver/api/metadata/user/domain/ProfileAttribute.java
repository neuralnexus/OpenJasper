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
package com.jaspersoft.jasperserver.api.metadata.user.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * ProfileAttribute interface manages attaching of extra information to users and roles. For example, you may assign
 * �State� attribute and �CA� value for it to users, and then use it as a default state for logged user in reports or
 * views.
 *
 * @author sbirney
 * @version $Id: ProfileAttribute.java 47331 2014-07-18 09:13:06Z kklein $
 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl
 * @since 2.0.0
 */
@JasperServerAPI
public interface ProfileAttribute {

    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute.
     */
    public String getAttrName();

    /**
     * Sets the name of the attribute.
     *
     * @param s the name of the attribute.
     */
    public void setAttrName(String s);

    /**
     * Returns the value of the attribute.
     *
     * @return the value of the attribute.
     */
    public String getAttrValue();

    /**
     * Sets the value of the attribute.
     *
     * @param s the value of the attribute.
     */
    public void setAttrValue(String s);

    /**
     * Returns the principal object (see {@link #setPrincipal(Object)}).
     *
     * @return the principal object.
     */
    public Object getPrincipal();

    /**
     * Sets the principal object ({@link User} or {@link Role}) to which this profile attribute is attached.
     *
     * @param o the principal object
     */
    public void setPrincipal(Object o);
}
