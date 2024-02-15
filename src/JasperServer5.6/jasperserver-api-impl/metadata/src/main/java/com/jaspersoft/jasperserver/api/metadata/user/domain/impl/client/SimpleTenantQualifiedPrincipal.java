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

package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client;

import java.io.Serializable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SimpleTenantQualifiedPrincipal.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class SimpleTenantQualifiedPrincipal implements TenantQualifiedPrincipal, Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String tenantId;

	public SimpleTenantQualifiedPrincipal(String name, String tenantId) {
		this.name = name;
		this.tenantId = tenantId;
	}
	
	public String getName() {
		return name;
	}

	public String getTenantId() {
		return tenantId;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof SimpleTenantQualifiedPrincipal)) {
			return false;
		}
		
		if (o == this) {
			return true;
		}
		
		SimpleTenantQualifiedPrincipal p = (SimpleTenantQualifiedPrincipal) o;
		return name.equals(p.name)
			&& (tenantId == null ? p.tenantId == null : (tenantId.equals(p.tenantId)));
	}
	
	public int hashCode() {
		return 37 * name.hashCode() 
			+ (tenantId == null ? 0 : tenantId.hashCode());
	}
	
	public String toString() {
		return name + (tenantId == null ? "" : ("|" + tenantId));
	}

}
