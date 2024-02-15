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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 * @see
 */
public class ConstantsResourceGroupElementTest extends BaseDTOTest<ConstantsResourceGroupElement> {

    ConstantsResourceGroupElement sourceElement;
    ConstantsResourceGroupElement clonedElement;

    @Override
    protected List<ConstantsResourceGroupElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setSourceName("sourceName2"),
                createFullyConfiguredInstance().setElements(Collections.singletonList(new SchemaElement().setName("name2"))),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setSourceName(null),
                createFullyConfiguredInstance().setElements(null)
        );
    }

    @Override
    protected ConstantsResourceGroupElement createFullyConfiguredInstance() {
        ConstantsResourceGroupElement constantsResourceGroupElement = new ConstantsResourceGroupElement();
        constantsResourceGroupElement.setName("name");
        constantsResourceGroupElement.setSourceName("sourceName");
        constantsResourceGroupElement.setElements(Collections.singletonList((new SchemaElement().setName("name"))));
        return constantsResourceGroupElement;
    }

    @Override
    protected ConstantsResourceGroupElement createInstanceWithDefaultParameters() {
        return new ConstantsResourceGroupElement();
    }

    @Override
    protected ConstantsResourceGroupElement createInstanceFromOther(ConstantsResourceGroupElement other) {
        return new ConstantsResourceGroupElement(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ConstantsResourceGroupElement expected, ConstantsResourceGroupElement actual) {
        assertNotSameCollection(expected.getElements(), actual.getElements());
    }
}