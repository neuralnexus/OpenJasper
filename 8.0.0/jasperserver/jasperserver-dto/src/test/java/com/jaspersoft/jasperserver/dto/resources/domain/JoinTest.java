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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class JoinTest extends BaseDTOTest<Join> {

    @Override
    protected List<Join> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExpression(new ClientExpressionContainer().setString("string2")),
                createFullyConfiguredInstance().setLeft("left2"),
                createFullyConfiguredInstance().setRight("right2"),
                createFullyConfiguredInstance().setType("type2"),
                createFullyConfiguredInstance().setWeight(24),
                // with null values
                createFullyConfiguredInstance().setExpression(null),
                createFullyConfiguredInstance().setLeft(null),
                createFullyConfiguredInstance().setRight(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setWeight(null)
        );
    }

    @Override
    protected Join createFullyConfiguredInstance() {
        Join join = new Join();
        join.setExpression(new ClientExpressionContainer().setString("string"));
        join.setLeft("left");
        join.setRight("right");
        join.setType("type");
        join.setWeight(23);
        return join;
    }

    @Override
    protected Join createInstanceWithDefaultParameters() {
        return new Join();
    }

    @Override
    protected Join createInstanceFromOther(Join other) {
        return new Join(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(Join expected, Join actual) {
        assertNotSame(expected.getExpression(), actual.getExpression());
    }

    @Test
    public void valueOfInnerForJoinTypeReturnsInner() {
        Join.JoinType result = Join.JoinType.valueOf("inner");
        assertEquals(Join.JoinType.inner, result);
    }
}
