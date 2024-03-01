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

package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests ResourceLookupImpl class implementation.
 *
 * @author Yuriy Plakosh
 */
public class ResourceLookupImplTest {
    private ResourceLookupImpl resourceLookup = new ResourceLookupImpl();

    @Test
    public void setURIString() {
        resourceLookup.setURIString("/organization/test_folder/test_resource1");
        assertEquals("Wrong parent folder URI", "/organization/test_folder", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource1", resourceLookup.getName());

        resourceLookup.setURIString("/organization/test_resource2");
        assertEquals("Wrong parent folder URI", "/organization", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource2", resourceLookup.getName());

        resourceLookup.setURIString("/test_resource3");
        assertEquals("Wrong parent folder URI", "/", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource3", resourceLookup.getName());

        resourceLookup.setURIString("test_resource3");
        assertEquals("Wrong parent folder URI", null, resourceLookup.getParentFolder());
        assertEquals("Wrong name", null, resourceLookup.getName());

        //noinspection NullableProblems
        resourceLookup.setURIString(null);
        assertEquals("Wrong parent folder URI", null, resourceLookup.getParentFolder());
        assertEquals("Wrong name", null, resourceLookup.getName());
    }
}
