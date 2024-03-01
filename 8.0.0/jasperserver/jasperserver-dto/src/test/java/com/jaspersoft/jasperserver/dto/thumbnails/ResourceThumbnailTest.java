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

package com.jaspersoft.jasperserver.dto.thumbnails;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ResourceThumbnailTest extends BaseDTOPresentableTest<ResourceThumbnail> {

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final String TEST_THUMBNAIL_DATA = "TEST_THUMBNAIL_DATA";
    private static final String TEST_THUMBNAIL_DATA_1 = "TEST_THUMBNAIL_DATA_1";

    @Override
    protected List<ResourceThumbnail> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setThumbnailData(TEST_THUMBNAIL_DATA_1),
                // null values
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setThumbnailData(null)
        );
    }

    @Override
    protected ResourceThumbnail createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setUri(TEST_URI)
                .setThumbnailData(TEST_THUMBNAIL_DATA);
    }

    @Override
    protected ResourceThumbnail createInstanceWithDefaultParameters() {
        return new ResourceThumbnail();
    }

    @Override
    protected ResourceThumbnail createInstanceFromOther(ResourceThumbnail other) {
        return new ResourceThumbnail(other);
    }

}
