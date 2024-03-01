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

package com.jaspersoft.jasperserver.dto.resources.domain;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DomainSchemaHelperTest {
    private static final String RESOURCE_NAME = "resourceName";
    private static final String RESOURCE_NAME_2 = "resourceName2";
    private static final String RESOURCE_NAME_3 = "resourceName3";

    private static final ResourceGroupElement RESOURCE_GROUP_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME = new ResourceGroupElement().setName(RESOURCE_NAME_2);
    private static final SchemaElement RESOURCE_SINGLE_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME = new ResourceSingleElement().setName(RESOURCE_NAME_2);

    private static final List<SchemaElement> RESOURCE_GROUP_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME_LIST =
            Collections.<SchemaElement>singletonList(RESOURCE_GROUP_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME);
    private static final List<SchemaElement> RESOURCE_SINGLE_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME_LIST =
            Collections.singletonList(RESOURCE_SINGLE_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME);
    private static final List<ResourceElement> RESOURCE_ELEMENTS_WITH_SINGLE_ELEMENTS_LIST = Collections.<ResourceElement>singletonList(new ResourceGroupElement()
            .setName(RESOURCE_NAME)
            .setElements(RESOURCE_GROUP_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME_LIST));
    private static final List<ResourceElement> RESOURCE_ELEMENTS_WITH_GROUP_ELEMENTS_LIST = Collections.<ResourceElement>singletonList(new ConstantsResourceGroupElement()
            .setName(RESOURCE_NAME)
            .setElements(RESOURCE_SINGLE_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME_LIST));
    private static final ReferenceElement RESOURCE_NAME_3_ELEMENT = new ReferenceElement().setName(RESOURCE_NAME_3);
    private static final ReferenceElement RESOURCE_NAME_2_ELEMENT_WITH_RESOURCE_NAME_3_PATH = new ReferenceElement()
            .setName(RESOURCE_NAME_2)
            .setReferencePath(RESOURCE_NAME_3);
    private static final ResourceGroupElement RESOURCE_NAME_3_ELEMENT_WITH_ELEMENTS = new ResourceGroupElement()
            .setName(RESOURCE_NAME_3)
            .setElements(Collections.<SchemaElement>singletonList(RESOURCE_NAME_3_ELEMENT));
    private static final List<SchemaElement> RESOURCE_NAME_2_ELEMENT_WITH_RESOURCE_NAME_3_PATH_LIST =
            Collections.<SchemaElement>singletonList(RESOURCE_NAME_2_ELEMENT_WITH_RESOURCE_NAME_3_PATH);
    public static final List<ResourceElement> RESOURCE_ELEMENTS_LIST_WITH_DIFFERENT_ELEMENTS = Arrays.<ResourceElement>asList(new ResourceGroupElement()
            .setName(RESOURCE_NAME)
            .setElements(RESOURCE_NAME_2_ELEMENT_WITH_RESOURCE_NAME_3_PATH_LIST), RESOURCE_NAME_3_ELEMENT_WITH_ELEMENTS);


    @Test
    public void getDataIslandPathFromGroup_recursionAndMockedGetDataIslandIdFromElement_correctResult() {
        final String expectedId = "expected.id";
        final ArrayList<ResourceElement> clientResources = new ArrayList<ResourceElement>();
        final PresentationSingleElement singleElement = new PresentationSingleElement().setResourcePath(expectedId + ".constantA");
        final PresentationSingleElement constant1 = new PresentationSingleElement().setResourcePath("constants1.constantB");
        final PresentationSingleElement constant2 = new PresentationSingleElement().setResourcePath("constants2.constantC");
        final PresentationGroupElement group = new PresentationGroupElement();
        group.setElements(new ArrayList<PresentationElement>() {{
            add(new PresentationGroupElement().setElements((List) Arrays.asList(
                    new PresentationSingleElement(),
                    constant1,
                    singleElement,
                    constant2,
                    new PresentationSingleElement())));
        }});
        final String result = DomainSchemaHelper.getDataIslandPathFromGroup(group, clientResources, Arrays.asList("constants2", "constants1"));
        assertEquals(result, expectedId);
    }

    @Test
    public void getDataIslandPathFromGroup_constantsOnly_constantResourcePath() {
        final PresentationGroupElement group = new PresentationGroupElement();
        final String expectedId = "constants";
        group.setElements(new ArrayList<PresentationElement>() {{
            add(new PresentationGroupElement().setElements((List) Arrays.asList(
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantA"),
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantB"),
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantC"),
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantD"))));
        }});

        final String result = DomainSchemaHelper.getDataIslandPathFromGroup(group, new ArrayList<ResourceElement>(),
                Arrays.asList("constants"));

        assertEquals(result, expectedId);
    }

    @Test
    public void getDataIslandPathFromElement_elementRefersJoinGroup_joinGroupPath() {
        final String joinGroupName = "someJoinGroup";
        final ArrayList<ResourceElement> clientResources = new ArrayList<ResourceElement>() {{
            add(new JoinResourceGroupElement().setName(joinGroupName));
        }};
        final PresentationSingleElement singleElement = new PresentationSingleElement()
                .setResourcePath(joinGroupName + ".some_table.some_column");
        final String result = DomainSchemaHelper.getDataIslandPathFromElement(singleElement, clientResources);
        assertEquals(result, joinGroupName);
    }

    @Test
    public void getDataIslandPathFromElement_elementRefersNonJoinedElement_substringOfResourcePath() {
        final String dataSourceGroupName = "dataSourceGroup";
        final ArrayList<ResourceElement> clientResources = new ArrayList<ResourceElement>() {{
            add(new ResourceGroupElement().setName(dataSourceGroupName));
        }};
        final String resourcePath = dataSourceGroupName + ".some_schema.some_table.some_column";
        final PresentationSingleElement singleElement = new PresentationSingleElement()
                .setResourcePath(resourcePath);
        final String result = DomainSchemaHelper.getDataIslandPathFromElement(singleElement, clientResources);
        assertEquals(result, resourcePath.substring(0, resourcePath.lastIndexOf(".")));
    }

    @Test
    public void findResourceElement_abstractResourceGroupElement_elements() {
        SchemaElement result = DomainSchemaHelper.findResourceElement(RESOURCE_NAME + "." + RESOURCE_NAME_2, RESOURCE_ELEMENTS_WITH_SINGLE_ELEMENTS_LIST);
        assertEquals(RESOURCE_GROUP_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME, result);
    }

    @Test
    public void findResourceElement_constantsResourceGroupElement_elements() {
        SchemaElement result = DomainSchemaHelper.findResourceElement(RESOURCE_NAME + "." + RESOURCE_NAME_2, RESOURCE_ELEMENTS_WITH_GROUP_ELEMENTS_LIST);
        assertEquals(RESOURCE_SINGLE_ELEMENT_WITH_ANOTHER_RESOURCE_PATH_NAME, result);
    }

    @Test
    public void findResourceElement_referenceElement_elements() {
        SchemaElement result = DomainSchemaHelper.findResourceElement(RESOURCE_NAME + "." + RESOURCE_NAME_2 + "." + RESOURCE_NAME_3, RESOURCE_ELEMENTS_LIST_WITH_DIFFERENT_ELEMENTS);
        assertEquals(RESOURCE_NAME_3_ELEMENT, result);
    }
}
