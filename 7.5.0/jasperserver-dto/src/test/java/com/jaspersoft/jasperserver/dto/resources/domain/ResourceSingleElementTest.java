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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotSame;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 * @see
 */
public class ResourceSingleElementTest extends BaseDTOTest<ResourceSingleElement> {

    @Override
    protected List<ResourceSingleElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setSourceName("sourceName2"),
                createFullyConfiguredInstance().setType("type2"),
                createFullyConfiguredInstance().setExpression(new ClientExpressionContainer().setString("string2")),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setSourceName(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setExpression(null)
        );
    }

    @Override
    protected ResourceSingleElement createFullyConfiguredInstance() {
        return new ResourceSingleElement()
                .setName("name")
                .setSourceName("sourceName")
                .setType("type")
                .setExpression(new ClientExpressionContainer().setString("Expression"));
    }

    @Override
    protected ResourceSingleElement createInstanceWithDefaultParameters() {
        return new ResourceSingleElement();
    }

    @Override
    protected ResourceSingleElement createInstanceFromOther(ResourceSingleElement other) {
        return new ResourceSingleElement(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ResourceSingleElement expected, ResourceSingleElement actual) {
        assertNotSame(expected.getExpression(), actual.getExpression());
    }
}