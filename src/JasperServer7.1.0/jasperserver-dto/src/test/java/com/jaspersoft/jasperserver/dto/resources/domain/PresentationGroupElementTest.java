/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.dto.resources.domain;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class PresentationGroupElementTest {

    public static final String ELEMENT_NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String DESCRIPTION_ID = "DescriptionId";
    public static final String LABEL = "Label";
    public static final String LABEL_ID = "LabelId";
    public static final String RESOURCE_PATH = "ResourcePath";
    PresentationGroupElement sourceElement;
    PresentationGroupElement clonedElement;

    @Before
    public void setUp() {
        sourceElement = new PresentationGroupElement()
                .setName(ELEMENT_NAME)
                .setDescription(DESCRIPTION)
                .setDescriptionId(DESCRIPTION_ID)
                .setLabel(LABEL)
                .setLabelId(LABEL_ID)
                .setElements(new ArrayList<PresentationElement>());

    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new PresentationGroupElement(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertFalse(sourceElement.getElements() == clonedElement.getElements());
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getDescription());
        assertEquals(DESCRIPTION, clonedElement.getDescription());
        assertNotNull(clonedElement.getDescriptionId());
        assertEquals(DESCRIPTION_ID, clonedElement.getDescriptionId());
        assertNotNull(clonedElement.getLabel());
        assertEquals(LABEL, clonedElement.getLabel());
        assertNotNull(clonedElement.getLabelId());
        assertEquals(LABEL_ID, clonedElement.getLabelId());


    }

}