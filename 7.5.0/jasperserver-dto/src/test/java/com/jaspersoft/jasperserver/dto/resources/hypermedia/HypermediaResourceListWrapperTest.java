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

package com.jaspersoft.jasperserver.dto.resources.hypermedia;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class HypermediaResourceListWrapperTest extends BaseDTOPresentableTest<HypermediaResourceListWrapper> {

    @Override
    protected List<HypermediaResourceListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setLinks(new HypermediaResourceLookupLinks().setNext("next2")),
                createFullyConfiguredInstance().setResourceLookups(Arrays.asList(new HypermediaResourceLookup(),
                        ((HypermediaResourceLookup) new HypermediaResourceLookup().setLabel("label2")))),
                // with null values
                createFullyConfiguredInstance().setLinks(null),
                createFullyConfiguredInstance().setResourceLookups(null)
        );
    }

    @Override
    protected HypermediaResourceListWrapper createFullyConfiguredInstance() {
        HypermediaResourceListWrapper hypermediaResourceListWrapper = new HypermediaResourceListWrapper();
        hypermediaResourceListWrapper.setLinks(new HypermediaResourceLookupLinks().setNext("next"));
        hypermediaResourceListWrapper.setResourceLookups(Arrays.asList(new HypermediaResourceLookup(),
                ((HypermediaResourceLookup) new HypermediaResourceLookup().setLabel("label"))));
        return hypermediaResourceListWrapper;
    }

    @Override
    protected HypermediaResourceListWrapper createInstanceWithDefaultParameters() {
        return new HypermediaResourceListWrapper();
    }

    @Override
    protected HypermediaResourceListWrapper createInstanceFromOther(HypermediaResourceListWrapper other) {
        return new HypermediaResourceListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaResourceListWrapper expected, HypermediaResourceListWrapper actual) {
        assertNotSameCollection(expected.getResourceLookups(), actual.getResourceLookups());
        assertNotSame(expected.getLinks(), actual.getLinks());
    }

    @Test
    public void instanceCanBeCreatedFromParameters() {
        HypermediaResourceListWrapper result = new HypermediaResourceListWrapper(fullyConfiguredTestInstance.getResourceLookups());
        assertEquals(fullyConfiguredTestInstance.getResourceLookups(), result.getResourceLookups());
    }
}
