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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.Assert.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ResourceThumbnailsListWrapperTest extends BaseDTOPresentableTest<ResourceThumbnailsListWrapper> {

    private static final List<ResourceThumbnail> TEST_THUMBNAILS = Arrays.asList(
            new ResourceThumbnail().setUri("TEST_URI"),
            new ResourceThumbnail().setThumbnailData("TEST_THUMBNAIL_DATA")
    );
    private static final List<ResourceThumbnail> TEST_THUMBNAILS_1 = Arrays.asList(
            new ResourceThumbnail().setUri("TEST_URI_1"),
            new ResourceThumbnail().setThumbnailData("TEST_THUMBNAIL_DATA_1")
    );
    private static final List<ResourceThumbnail> TEST_THUMBNAILS_EMPTY = Arrays.asList(
            new ResourceThumbnail().setUri("TEST_URI_1"),
            new ResourceThumbnail().setThumbnailData("TEST_THUMBNAIL_DATA_1")
    );

    @Test
    public void testConstructor() {
        ResourceThumbnailsListWrapper instance = new ResourceThumbnailsListWrapper(TEST_THUMBNAILS);
        assertEquals(TEST_THUMBNAILS, instance.getThumbnails());
    }

    @Override
    protected List<ResourceThumbnailsListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setThumbnails(TEST_THUMBNAILS_1),
                createFullyConfiguredInstance().setThumbnails(TEST_THUMBNAILS_EMPTY),
                // null values
                createFullyConfiguredInstance().setThumbnails(null)
        );
    }

    @Override
    protected ResourceThumbnailsListWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setThumbnails(TEST_THUMBNAILS);
    }

    @Override
    protected ResourceThumbnailsListWrapper createInstanceWithDefaultParameters() {
        return new ResourceThumbnailsListWrapper();
    }

    @Override
    protected ResourceThumbnailsListWrapper createInstanceFromOther(ResourceThumbnailsListWrapper other) {
        return new ResourceThumbnailsListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ResourceThumbnailsListWrapper expected, ResourceThumbnailsListWrapper actual) {
        assertNotSameCollection(expected.getThumbnails(), actual.getThumbnails());
    }
}
