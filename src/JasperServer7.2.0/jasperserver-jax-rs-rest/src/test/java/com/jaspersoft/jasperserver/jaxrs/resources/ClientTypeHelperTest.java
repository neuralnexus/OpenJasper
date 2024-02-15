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
package com.jaspersoft.jasperserver.jaxrs.resources;

import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
public class ClientTypeHelperTest {
    @Test
    public void extractClientType_mediaTypeIsNull_success() {
        assertNull(ClientTypeHelper.extractClientType((MediaType) null));
    }

    @Test
    public void extractClientType_mediaTypeHasClientType_success() {
        String result = ClientTypeHelper.extractClientType(ResourceMediaType.FOLDER_JSON);

        assertEquals(result, ResourceMediaType.FOLDER_CLIENT_TYPE);
    }

    @Test
    public void extractClientType_mediaTypeMissesClientType_success() {
        String result = ClientTypeHelper.extractClientType(MediaType.APPLICATION_JSON);

        assertNull(result);
    }

    @Test
    public void extractClientType_mediaTypeHasWrongServiceName_success() {
        String result = ClientTypeHelper.extractClientType("application/execution.multiAxesQuery+json");

        assertNull(result, ResourceMediaType.FOLDER_CLIENT_TYPE);
    }
}