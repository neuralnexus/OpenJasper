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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class HypermediaResourceTest extends BaseDTOTest<HypermediaResource> {

    @Override
    protected List<HypermediaResource> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setEmbedded(new HypermediaEmbeddedContainer().setResourceLookup(Collections.singletonList(new ClientResourceLookup()))),
                createFullyConfiguredInstance().setLinks(new HypermediaResourceLinks().setContent("content2")),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setVersion(24),
                // with null values
                createFullyConfiguredInstance().setEmbedded(null),
                createFullyConfiguredInstance().setLinks(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setUpdateDate(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null)
        );
    }

    @Override
    protected HypermediaResource createFullyConfiguredInstance() {
        HypermediaResource hypermediaResource = new HypermediaResource();
        hypermediaResource.setEmbedded(new HypermediaEmbeddedContainer().setResourceLookup(Collections.<ClientResourceLookup>emptyList()));
        hypermediaResource.setLinks(new HypermediaResourceLinks().setContent("content"));
        hypermediaResource.setCreationDate("creationDate");
        hypermediaResource.setDescription("description");
        hypermediaResource.setLabel("label");
        hypermediaResource.setPermissionMask(23);
        hypermediaResource.setUpdateDate("updateDate");
        hypermediaResource.setUri("uri");
        hypermediaResource.setVersion(23);
        return hypermediaResource;
    }

    @Override
    protected HypermediaResource createInstanceWithDefaultParameters() {
        return new HypermediaResource();
    }

    @Override
    protected HypermediaResource createInstanceFromOther(HypermediaResource other) {
        return new HypermediaResource(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaResource expected, HypermediaResource actual) {
        assertNotSame(expected.getEmbedded(), actual.getEmbedded());
        assertNotSame(expected.getLinks(), actual.getLinks());
    }
}
