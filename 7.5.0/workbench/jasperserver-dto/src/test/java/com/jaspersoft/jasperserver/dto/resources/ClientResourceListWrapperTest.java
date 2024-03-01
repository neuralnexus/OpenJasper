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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientResourceListWrapperTest extends BaseDTOPresentableTest<ClientResourceListWrapper> {

    @Override
    protected List<ClientResourceListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setResourceLookups(Arrays.asList(new ClientResourceLookup(), new ClientResourceLookup().setLabel("label2"))),
                // fields with null values
                createFullyConfiguredInstance().setResourceLookups(null)
        );
    }

    @Override
    protected ClientResourceListWrapper createFullyConfiguredInstance() {
        ClientResourceListWrapper ClientResourceListWrapper = new ClientResourceListWrapper();
        ClientResourceListWrapper.setResourceLookups(Arrays.asList(new ClientResourceLookup(), new ClientResourceLookup().setLabel("label")));
        return ClientResourceListWrapper;
    }

    @Override
    protected ClientResourceListWrapper createInstanceWithDefaultParameters() {
        return new ClientResourceListWrapper();
    }

    @Override
    protected ClientResourceListWrapper createInstanceFromOther(ClientResourceListWrapper other) {
        return new ClientResourceListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientResourceListWrapper expected, ClientResourceListWrapper actual) {
        assertNotSameCollection(expected.getResourceLookups(), actual.getResourceLookups());
    }

    @Test
    public void instanceCanBeCreatedWithResourceLookupsParameter() {
        List<ClientResourceLookup> resourceLookups = Collections.singletonList(new ClientResourceLookup().setLabel("label"));

        ClientResourceListWrapper result = new ClientResourceListWrapper(resourceLookups);
        assertEquals(resourceLookups, result.getResourceLookups());
    }

}
