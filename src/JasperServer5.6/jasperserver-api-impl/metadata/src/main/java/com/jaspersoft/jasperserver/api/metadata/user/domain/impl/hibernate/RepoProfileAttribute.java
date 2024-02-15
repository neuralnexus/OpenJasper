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
package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;

/**
 * @author sbirney
 *
 * @hibernate.class table="ProfileAttribute"
 */
public class RepoProfileAttribute implements ProfileAttribute, IdedObject {

    private long id;
    private String attrName;
    private String attrValue;
    private Object principal;
	
    /**
     * @return
     * @hibernate.id generator-class="identity"
     */
    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }
	
    /**
     * @hibernate.property column="attrName" type="string" not-null="true" length="255"
     */
    public String getAttrName() {
	return attrName;
    }

    public void setAttrName(String s) {
	this.attrName = s;
    }
	
    /**
     * @hibernate.property column="attrValue" type="string" not-null="true" length="255"
     */
    public String getAttrValue() {
	return attrValue;
    }

    public void setAttrValue(String s) {
	this.attrValue = s;
    }
	
    /**
     * This is a User or a Role
     * 
     * @hibernate.any id-type="long"
     * 
     * @hibernate.any-column name="principalobjectclass" length="100"
     * @hibernate.any-column name="principalobjectid"
     * 
     * hibernate.meta-value class="RepoRole" value="RepoRole"
     * hibernate.meta-value class="RepoUser" value="RepoUser"
     */

    public Object getPrincipal() {
	return principal;
    }

    public void setPrincipal(Object o) {
	this.principal = o;
    }

    /**
     * Copy from a client object into this one
     * 
     * @param obj
     * @param resolver
     */
    public void copyFromClient(Object obj, PersistentObjectResolver resolver) {
	ProfileAttribute other = (ProfileAttribute) obj;
	setAttrName(other.getAttrName());
	setAttrValue(other.getAttrValue());
	if (other.getPrincipal() != null) {
	    setPrincipal(resolver.getPersistentObject(other.getPrincipal()));
	} else {
	    setPrincipal(null);
	}
    }
	
    /**
     * Copy from this into a new client object
     * 
     * @param clientMappingFactory
     */
    public Object toClient(ResourceFactory clientMappingFactory) {
	ProfileAttribute other = (ProfileAttribute) clientMappingFactory.newObject(ProfileAttribute.class);

	other.setAttrName(getAttrName());
	other.setAttrValue(getAttrValue());
		
	if (getPrincipal() != null) {
	    IdedObject thisPrincipal = (IdedObject) getPrincipal();
	    Object clientPrincipal = thisPrincipal.toClient(clientMappingFactory);
	    other.setPrincipal(clientPrincipal);
	} else {
	    other.setPrincipal(null);
	}
	return other;
    }
	
    public String toString() {
	return new ToStringBuilder(this)
	    .append("profileAttributeId", getId())
	    .append("attrName", getAttrName())
	    .append("attrValue", getAttrValue())
	    .append("principal", getPrincipal())
	    .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof RepoProfileAttribute) ) return false;
        RepoProfileAttribute castOther = (RepoProfileAttribute) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

}
