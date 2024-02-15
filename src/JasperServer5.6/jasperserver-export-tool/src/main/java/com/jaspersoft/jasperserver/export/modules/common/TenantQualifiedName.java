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

package com.jaspersoft.jasperserver.export.modules.common;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: TenantQualifiedName.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class TenantQualifiedName {

	private String tenantId;
	private String name;
	
	public TenantQualifiedName() {
	}
	
	public TenantQualifiedName(String tenantId, String name) {
		this.tenantId = tenantId;
		this.name = name;
	}

	public String getTenantId() {
		return tenantId;
	}
	
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof TenantQualifiedName)) {
			return false;
		}
		
		if (o == this) {
			return true;
		}
		
		TenantQualifiedName n = (TenantQualifiedName) o;
		return name.equals(n.name)
			&& (tenantId == null ? n.tenantId == null : (tenantId.equals(n.tenantId)));
	}
	
	public int hashCode() {
		return 37 * name.hashCode() 
			+ (tenantId == null ? 0 : tenantId.hashCode());
	}
	
}
