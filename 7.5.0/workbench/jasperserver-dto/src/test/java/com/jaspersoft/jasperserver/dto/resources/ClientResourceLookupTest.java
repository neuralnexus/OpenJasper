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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientResourceLookupTest extends BaseDTOPresentableTest<ClientResourceLookup> {

    private static final String RESOURCE_TYPE = "resourceType";
    private static final String THUMBNAIL_DATA = "thumbnailData";
    private static final String CREATION_DATE = "creationDate";
    private static final String DESCRIPTION = "description";
    private static final String LABEL = "label";
    private static final int PERMISSION_MASK = 23;
    private static final String URI = "uri";
    private static final int VERSION = 2;
    private static final String UPDATE_DATE = "updateDate";

    private static final String RESOURCE_TYPE_2 = "resourceType2";
    private static final String THUMBNAIL_DATA_2 = "thumbnailData2";
    private static final String CREATION_DATE_2 = "creationDate2";
    private static final String DESCRIPTION_2 = "description2";
    private static final String LABEL_2 = "label2";
    private static final int PERMISSION_MASK_2 = 21;
    private static final String URI_2 = "uri2";
    private static final int VERSION_2 = 3;
    private static final String UPDATE_DATE_2 = "updateDate2";

    @Override
    protected List<ClientResourceLookup> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setResourceType(RESOURCE_TYPE_2),
                createFullyConfiguredInstance().setThumbnailData(THUMBNAIL_DATA_2),
                createFullyConfiguredInstance().setCreationDate(CREATION_DATE_2),
                createFullyConfiguredInstance().setDescription(DESCRIPTION_2),
                createFullyConfiguredInstance().setLabel(LABEL_2),
                createFullyConfiguredInstance().setPermissionMask(PERMISSION_MASK_2),
                createFullyConfiguredInstance().setUri(URI_2),
                createFullyConfiguredInstance().setVersion(VERSION_2),
                createFullyConfiguredInstance().setUpdateDate(UPDATE_DATE_2),
                // fields with null values
                createFullyConfiguredInstance().setResourceType(null),
                createFullyConfiguredInstance().setThumbnailData(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUpdateDate(null)
        );
    }

    @Override
    protected ClientResourceLookup createFullyConfiguredInstance() {
        ClientResourceLookup clientResourceLookup = new ClientResourceLookup();
        clientResourceLookup.setResourceType(RESOURCE_TYPE);
        clientResourceLookup.setThumbnailData(THUMBNAIL_DATA);
        clientResourceLookup.setCreationDate(CREATION_DATE);
        clientResourceLookup.setDescription(DESCRIPTION);
        clientResourceLookup.setLabel(LABEL);
        clientResourceLookup.setPermissionMask(PERMISSION_MASK);
        clientResourceLookup.setUri(URI);
        clientResourceLookup.setVersion(VERSION);
        clientResourceLookup.setUpdateDate(UPDATE_DATE);
        return clientResourceLookup;
    }

    @Override
    protected ClientResourceLookup createInstanceWithDefaultParameters() {
        return new ClientResourceLookup();
    }

    @Override
    protected ClientResourceLookup createInstanceFromOther(ClientResourceLookup other) {
        return new ClientResourceLookup(other);
    }
}
