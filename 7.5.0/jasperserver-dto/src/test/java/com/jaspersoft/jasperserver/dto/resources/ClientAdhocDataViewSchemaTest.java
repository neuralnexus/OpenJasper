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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.resources.domain.ConstantsResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.Assert.assertFalse;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientAdhocDataViewSchemaTest extends BaseDTOTest<ClientAdhocDataViewSchema> {

    private final static PresentationElement PRESENTATION_ELEMENT_1 = new PresentationSingleElement().setName("Presentation1");
    private final static PresentationElement PRESENTATION_ELEMENT_2 = new PresentationGroupElement().setName("Presentation2");
    private final static PresentationElement PRESENTATION_ELEMENT_3 = new PresentationGroupElement().setName("Presentation3");
    private final static PresentationElement PRESENTATION_ELEMENT_4 = new PresentationSingleElement().setName("Presentation4");

    private final static ResourceElement RESOURCE_ELEMENT_1 = new ResourceGroupElement().setName("Resource1")
            .setElements(Collections.<SchemaElement>singletonList(new ResourceSingleElement().setName("SubResource1")));
    private final static ResourceElement RESOURCE_ELEMENT_2 = new ResourceGroupElement().setName("Resource2")
            .setElements(Collections.<SchemaElement>singletonList(new ResourceSingleElement().setName("SubResource2")));
    private final static ResourceElement RESOURCE_ELEMENT_3 = new ConstantsResourceGroupElement().setName("Resource4");
    private final static ResourceElement RESOURCE_ELEMENT_4 = new ResourceSingleElement().setName("Resource5");
    private final static ResourceElement RESOURCE_ELEMENT_5 = new ResourceGroupElement().setName("Resource6");
    private final static ResourceElement RESOURCE_ELEMENT_2a = new ConstantsResourceGroupElement().setName("Resource2");
    private final static ResourceElement RESOURCE_ELEMENT_2b = new ConstantsResourceGroupElement().setName("Resource2")
            .setElements(Collections.singletonList(new SchemaElement()));

    @Override
    protected List<ClientAdhocDataViewSchema> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setPresentation(Arrays.asList(PRESENTATION_ELEMENT_3, PRESENTATION_ELEMENT_4)),
                createFullyConfiguredInstance().setResources(Arrays.asList(RESOURCE_ELEMENT_3, RESOURCE_ELEMENT_4, RESOURCE_ELEMENT_5)),
                // with null values
                createFullyConfiguredInstance().setPresentation(null),
                createFullyConfiguredInstance().setResources(null)
        );
    }

    @Override
    protected ClientAdhocDataViewSchema createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setPresentation(Arrays.asList(PRESENTATION_ELEMENT_1, PRESENTATION_ELEMENT_2))
                .setResources(Arrays.asList(RESOURCE_ELEMENT_1, RESOURCE_ELEMENT_2));
    }

    @Override
    protected ClientAdhocDataViewSchema createInstanceWithDefaultParameters() {
        return new ClientAdhocDataViewSchema();
    }

    @Override
    protected ClientAdhocDataViewSchema createInstanceFromOther(ClientAdhocDataViewSchema other) {
        return new ClientAdhocDataViewSchema(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientAdhocDataViewSchema expected, ClientAdhocDataViewSchema actual) {
        assertNotSameCollection(expected.getPresentation(), actual.getPresentation());
        assertNotSameCollection(expected.getResources(), actual.getResources());
    }

    @Test
    public void instancesWithDifferentConstantsResourceGroupElementAreNotEqual() {
        ClientAdhocDataViewSchema instance = createFullyConfiguredInstance();
        ClientAdhocDataViewSchema instanceToCompare = createFullyConfiguredInstance();

        instance.setResources(Arrays.asList(RESOURCE_ELEMENT_1, RESOURCE_ELEMENT_2a));
        instanceToCompare.setResources(Arrays.asList(RESOURCE_ELEMENT_1, RESOURCE_ELEMENT_2b));
        assertFalse(instance.equals(instanceToCompare));
    }
}