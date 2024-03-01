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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class PresentationGroupElementTest extends BaseDTOPresentableTest<PresentationGroupElement> {

    @Override
    protected List<PresentationGroupElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setDescriptionId("descriptionId2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setLabelId("labelId2"),
                createFullyConfiguredInstance().setKind("kind2"),
                createFullyConfiguredInstance().setElements(Arrays.asList(new PresentationElement(), new PresentationSingleElement().setName("name2"))),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setDescriptionId(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setLabelId(null),
                createFullyConfiguredInstance().setKind(null),
                createFullyConfiguredInstance().setElements(null)
        );
    }

    @Override
    protected PresentationGroupElement createFullyConfiguredInstance() {
        return new PresentationGroupElement()
                .setName("name")
                .setDescription("description")
                .setDescriptionId("descriptionId")
                .setLabel("label")
                .setLabelId("labelId")
                .setKind("kind")
                .setElements(Arrays.asList(new PresentationElement(), new PresentationSingleElement().setName("name")));
    }

    @Override
    protected PresentationGroupElement createInstanceWithDefaultParameters() {
        return new PresentationGroupElement();
    }

    @Override
    protected PresentationGroupElement createInstanceFromOther(PresentationGroupElement other) {
        return new PresentationGroupElement(other);
    }

    @Test
    public void elementsCanBeAddedWhenElementsDoesNotExist() {
        PresentationGroupElement instance = new PresentationGroupElement();

        PresentationSingleElement element = new PresentationSingleElement().setName("nameForAdd");
        PresentationSingleElement anotherElement = new PresentationSingleElement().setName("nameForAdd2");

        instance.addElements(element, anotherElement);
        assertTrue(instance.getElements().contains(element));
        assertTrue(instance.getElements().contains(anotherElement));
    }

    @Test
    public void elementsCanBeAddedWhenElementsExist() {
        PresentationGroupElement instance = new PresentationGroupElement();
        instance.setElements(new ArrayList<PresentationElement>() {{
            add(new PresentationSingleElement().setName("element"));
        }});

        PresentationSingleElement element = new PresentationSingleElement().setName("nameForAdd");
        PresentationSingleElement anotherElement = new PresentationSingleElement().setName("nameForAdd2");

        instance.addElements(element, anotherElement);
        assertTrue(instance.getElements().contains(element));
        assertTrue(instance.getElements().contains(anotherElement));
    }
}