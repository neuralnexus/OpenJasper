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

package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */
class ClientExpressionContainerTest extends BaseDTOJSONPresentableTest<ClientExpressionContainer> {

    private static final String TEST_STRING = "TEST_STRING";
    private static final String TEST_STRING_ALT = "TEST_STRING_ALT";

    private static final ClientExpression TEST_EXPRESSION = new ClientNumber(1);
    private static final ClientExpression TEST_EXPRESSION_ALT = new ClientNumber(10);

    @Override
    protected List<ClientExpressionContainer> prepareInstancesWithAlternativeParameters() {
        return asList(
                new ClientExpressionContainer().setString(TEST_STRING),
                new ClientExpressionContainer().setObject(TEST_EXPRESSION_ALT),
                new ClientExpressionContainer().setObject(null)
        );
    }

    @Override
    protected ClientExpressionContainer createFullyConfiguredInstance() {
        return new ClientExpressionContainer()
                .setObject(TEST_EXPRESSION);
    }

    @Override
    protected ClientExpressionContainer createInstanceWithDefaultParameters() {
        return new ClientExpressionContainer();
    }

    @Override
    protected ClientExpressionContainer createInstanceFromOther(ClientExpressionContainer other) {
        return new ClientExpressionContainer(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientExpressionContainer expected, ClientExpressionContainer actual) {
        assertNotSame(expected.getObject(), actual.getObject());
    }

    @Override
    public void generatedStringBeginsWithClassName() {
        // skip this test
    }

    @Test
    public void getExpression_withObject() {
        ClientExpressionContainer container = new ClientExpressionContainer().setObject(TEST_EXPRESSION);
        assertEquals(TEST_EXPRESSION.toString(), container.getExpression());
    }

    @Test
    public void getExpression_withEmptyObject() {
        ClientExpressionContainer container = new ClientExpressionContainer().setObject(null);
        assertNull(container.getExpression());
    }

    @Test
    public void getExpression_withString() {
        ClientExpressionContainer container = new ClientExpressionContainer().setString(TEST_STRING);
        assertEquals(TEST_STRING, container.getExpression());
    }

    @Test
    public void getExpression_withEmptyString() {
        ClientExpressionContainer container = new ClientExpressionContainer().setString(null);
        assertNull(container.getExpression());
    }

    @Test
    public void equalsWithTwoDifferentStrings() {
        ClientExpressionContainer first = new ClientExpressionContainer().setString(TEST_STRING);
        ClientExpressionContainer second = new ClientExpressionContainer().setString(TEST_STRING_ALT);
        assertNotEquals(first, second);
    }

    @Test
    public void equalsWithStringAndNullString() {
        ClientExpressionContainer first = new ClientExpressionContainer().setString(null);
        ClientExpressionContainer second = new ClientExpressionContainer().setString(TEST_STRING_ALT);
        assertNotEquals(first, second);
    }

    @Test
    public void equalsWithNullStringAndString() {
        ClientExpressionContainer first = new ClientExpressionContainer().setString(TEST_STRING);
        ClientExpressionContainer second = new ClientExpressionContainer().setString(null);
        assertNotEquals(first, second);
    }
}