/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class RoleAccessUrisTest {
    private static final List<UriDescriptor> URI_DESCRIPTOR_LIST = singletonList(new UriDescriptor());
    private static final String ROLE_NAME = "roleName";
    private static final String TENANT_ID = "tenantId";

    @Test
    void getAndSet_instanceWithDefaultValues() {
        final RoleAccessUris instance = new RoleAccessUris();

        assertAll("an instance with default values",
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getUris());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getRoleName());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getTenantId());
                    }
                });
    }

    @Test
    void getAndSet_fullyConfiguredInstance() {
        final RoleAccessUris instance = new RoleAccessUris();
        instance.setUris(URI_DESCRIPTOR_LIST);
        instance.setRoleName(ROLE_NAME);
        instance.setTenantId(TENANT_ID);

        assertAll("a fully configured instance",
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(URI_DESCRIPTOR_LIST, instance.getUris());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(ROLE_NAME, instance.getRoleName());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(TENANT_ID, instance.getTenantId());
                    }
                });
    }
}
