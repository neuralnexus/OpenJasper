/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.export.modules.common;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.modules.Attributes;
import com.jaspersoft.jasperserver.export.modules.repository.beans.RepositoryObjectPermissionBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ProfileAttributeBean implements Encryptable {

	private String name;
	private String value;

	private RepositoryObjectPermissionBean[] permissions;

	public void copyFrom(ProfileAttribute attribute) {
		setName(attribute.getAttrName());
        String attrValue = attribute.getAttrValue();
        if (attribute.isSecure()) {
            attrValue = encrypt(attrValue);
        }
        setValue(attrValue);
	}

	public void copyTo(ProfileAttribute attribute) {
		attribute.setAttrName(getName());
		attribute.setAttrValue(getValue());

        String attrValue = getValue();
        if (isEncrypted(getValue())) {
            attrValue = decrypt(attrValue);
            attribute.setSecure(true);
        }
        attribute.setAttrValue(attrValue);
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public RepositoryObjectPermissionBean[] getPermissions() {
		return permissions;
	}

	public void setPermissions(RepositoryObjectPermissionBean[] permissions) {
		this.permissions = permissions;
	}
}
