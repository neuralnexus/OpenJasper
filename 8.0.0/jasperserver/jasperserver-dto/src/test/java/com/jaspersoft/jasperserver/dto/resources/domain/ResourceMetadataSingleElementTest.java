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
public class ResourceMetadataSingleElementTest extends BaseDTOTest<ResourceMetadataSingleElement> {

    @Override
    protected List<ResourceMetadataSingleElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setSourceName("sourceName2"),
                createFullyConfiguredInstance().setType("type2"),
                createFullyConfiguredInstance().setIsIdentifier(false),
                createFullyConfiguredInstance().setReferenceTo("reference2"),
                createFullyConfiguredInstance().setExpression(new ClientExpressionContainer().setString("Expression2 ")),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setSourceName(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setIsIdentifier(null),
                createFullyConfiguredInstance().setReferenceTo(null),
                createFullyConfiguredInstance().setExpression(null)
        );
    }

    @Override
    protected ResourceMetadataSingleElement createFullyConfiguredInstance() {
        return new ResourceMetadataSingleElement()
                .setName("name")
                .setSourceName("sourceName")
                .setType("type")
                .setIsIdentifier(true)
                .setReferenceTo("reference")
                .setExpression(new ClientExpressionContainer().setString("Expression"));
    }

    @Override
    protected ResourceMetadataSingleElement createInstanceWithDefaultParameters() {
        return new ResourceMetadataSingleElement();
    }

    @Override
    protected ResourceMetadataSingleElement createInstanceFromOther(ResourceMetadataSingleElement other) {
        return new ResourceMetadataSingleElement(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ResourceMetadataSingleElement expected, ResourceMetadataSingleElement actual) {
        assertNotSame(expected.getExpression(), actual.getExpression());
    }
}