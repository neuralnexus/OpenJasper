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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;


import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @author ativodar
 * @version $Id$
 * @see
 */
public class JoinResourceGroupElementTest extends BaseDTOTest<JoinResourceGroupElement> {

    JoinResourceGroupElement sourceElement;
    JoinResourceGroupElement clonedElement;


    @Override
    protected JoinResourceGroupElement createInstanceWithDefaultParameters() {
        return new JoinResourceGroupElement();
    }

    @Override
    protected JoinResourceGroupElement createFullyConfiguredInstance() {
        JoinResourceGroupElement JoinResourceGroupElement = new JoinResourceGroupElement();
        JoinResourceGroupElement.setJoinInfo(new JoinInfo().setIncludeAllDataIslandJoins(true));
        JoinResourceGroupElement.setElements(Arrays.asList(new SchemaElement(), new SchemaElement().setName("name")));
        JoinResourceGroupElement.setFilterExpression(new ClientExpressionContainer().setString("string"));
        JoinResourceGroupElement.setName("name");
        JoinResourceGroupElement.setSourceName("source");
        return JoinResourceGroupElement;
    }

    @Override
    protected List<JoinResourceGroupElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setJoinInfo(new JoinInfo().setIncludeAllDataIslandJoins(false)),
                createFullyConfiguredInstance().setElements(Arrays.asList(new SchemaElement(), new SchemaElement().setName("name2"))),
                createFullyConfiguredInstance().setFilterExpression(new ClientExpressionContainer().setString("string2")),
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setSourceName("source2"),
                // with null values
                createFullyConfiguredInstance().setJoinInfo(null),
                createFullyConfiguredInstance().setElements(null),
                createFullyConfiguredInstance().setFilterExpression(null),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setSourceName(null)
        );
    }

    @Override
    protected JoinResourceGroupElement createInstanceFromOther(JoinResourceGroupElement other) {
        return new JoinResourceGroupElement(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(JoinResourceGroupElement expected, JoinResourceGroupElement actual) {
        assertNotSameCollection(expected.getElements(), actual.getElements());
        assertNotSame(expected.getJoinInfo(), actual.getJoinInfo());
        assertNotSame(expected.getFilterExpression(), actual.getFilterExpression());
    }
}