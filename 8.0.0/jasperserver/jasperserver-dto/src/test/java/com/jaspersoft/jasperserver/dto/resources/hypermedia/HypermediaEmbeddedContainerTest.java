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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class HypermediaEmbeddedContainerTest extends BaseDTOPresentableTest<HypermediaEmbeddedContainer> {

    @Override
    protected List<HypermediaEmbeddedContainer> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setResourceLookup(Arrays.asList(new ClientResourceLookup(), new ClientResourceLookup().setLabel("label2"))),
                // with null values
                createFullyConfiguredInstance().setResourceLookup(null)
        );
    }

    @Override
    protected HypermediaEmbeddedContainer createFullyConfiguredInstance() {
        HypermediaEmbeddedContainer hypermediaEmbeddedContainer = new HypermediaEmbeddedContainer();
        hypermediaEmbeddedContainer.setResourceLookup(Arrays.asList(new ClientResourceLookup(), new ClientResourceLookup().setLabel("label")));
        return hypermediaEmbeddedContainer;
    }

    @Override
    protected HypermediaEmbeddedContainer createInstanceWithDefaultParameters() {
        return new HypermediaEmbeddedContainer();
    }

    @Override
    protected HypermediaEmbeddedContainer createInstanceFromOther(HypermediaEmbeddedContainer other) {
        return new HypermediaEmbeddedContainer(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaEmbeddedContainer expected, HypermediaEmbeddedContainer actual) {
        assertNotSameCollection(expected.getResourceLookup(), actual.getResourceLookup());
    }
}
