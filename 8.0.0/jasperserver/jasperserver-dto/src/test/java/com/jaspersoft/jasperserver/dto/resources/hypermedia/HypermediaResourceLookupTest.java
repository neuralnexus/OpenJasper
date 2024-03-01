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

package com.jaspersoft.jasperserver.dto.resources.hypermedia;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class HypermediaResourceLookupTest extends BaseDTOTest<HypermediaResourceLookup> {

    @Override
    protected List<HypermediaResourceLookup> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setEmbedded(new HypermediaEmbeddedContainer().setResourceLookup(Collections.singletonList(new ClientResourceLookup()))),
                createFullyConfiguredInstance().setLinks(new HypermediaResourceLinks().setSelf("self2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setCreationDate("creationDate2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setDescription("description2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setLabel("label2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setPermissionMask(24)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setResourceType("resourceType2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setThumbnailData("thumbnail2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setUri("uri2")),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setVersion(24)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setUpdateDate("updateDate2")),
                // with null values
                createFullyConfiguredInstance().setEmbedded(null),
                createFullyConfiguredInstance().setLinks(null),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setCreationDate(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setDescription(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setLabel(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setPermissionMask(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setResourceType(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setThumbnailData(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setUri(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setVersion(null)),
                ((HypermediaResourceLookup) createFullyConfiguredInstance().setUpdateDate(null))
        );
    }

    @Override
    protected HypermediaResourceLookup createFullyConfiguredInstance() {
        HypermediaResourceLookup hypermediaResourceLookup = new HypermediaResourceLookup();
        hypermediaResourceLookup.setEmbedded(new HypermediaEmbeddedContainer().setResourceLookup(Collections.<ClientResourceLookup>emptyList()));
        hypermediaResourceLookup.setLinks(new HypermediaResourceLinks().setSelf("self"));
        hypermediaResourceLookup.setCreationDate("creationDate");
        hypermediaResourceLookup.setDescription("description");
        hypermediaResourceLookup.setLabel("label");
        hypermediaResourceLookup.setPermissionMask(23);
        hypermediaResourceLookup.setResourceType("resourceType");
        hypermediaResourceLookup.setThumbnailData("thumbnail");
        hypermediaResourceLookup.setUri("uri");
        hypermediaResourceLookup.setVersion(23);
        hypermediaResourceLookup.setUpdateDate("updateDate");
        return hypermediaResourceLookup;
    }

    @Override
    protected HypermediaResourceLookup createInstanceWithDefaultParameters() {
        return new HypermediaResourceLookup();
    }

    @Override
    protected HypermediaResourceLookup createInstanceFromOther(HypermediaResourceLookup other) {
        return new HypermediaResourceLookup(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaResourceLookup expected, HypermediaResourceLookup actual) {
        assertNotSame(expected.getEmbedded(), actual.getEmbedded());
        assertNotSame(expected.getLinks(), actual.getLinks());
    }

    @Test
    public void instanceCanBeCreatedFromClientResourceLookup() {
        ClientResourceLookup resourceLookup = new ClientResourceLookup()
                .setResourceType("resourceType")
                .setThumbnailData("thumbnail");
        HypermediaResourceLookup instance = new HypermediaResourceLookup(resourceLookup);

        assertNotEquals(instance, resourceLookup);
        assertEquals(instance.getResourceType(), resourceLookup.getResourceType());
        assertEquals(instance.getThumbnailData(), resourceLookup.getThumbnailData());
    }
}
