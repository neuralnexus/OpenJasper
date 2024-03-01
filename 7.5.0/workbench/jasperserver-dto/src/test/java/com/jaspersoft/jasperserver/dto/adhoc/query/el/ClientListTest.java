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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ClientListTest extends BaseDTOPresentableTest<ClientList> {
    private static final ClientString CLIENT_STRING = new ClientString("string");
    private static final ClientNumber CLIENT_INTEGER = new ClientNumber(23);

    private static final List<ClientExpression> CLIENT_EXPRESSIONS = new ArrayList<ClientExpression>(Arrays.<ClientExpression>asList(CLIENT_STRING, CLIENT_INTEGER));
    private static final List<ClientExpression> CLIENT_EXPRESSIONS_ALTERNATIVE = Arrays.<ClientExpression>asList(new ClientString("string2"), new ClientNumber(24));

    private ClientELVisitor clientELVisitor;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientList> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setItems(CLIENT_EXPRESSIONS_ALTERNATIVE),
                createFullyConfiguredInstance().setItems(new ArrayList<ClientExpression>(0))
        );
    }

    @Override
    protected ClientList createFullyConfiguredInstance() {
        return new ClientList()
                .setItems(new ArrayList<ClientExpression>(CLIENT_EXPRESSIONS));
    }

    @Override
    protected ClientList createInstanceWithDefaultParameters() {
        return new ClientList();
    }

    @Override
    protected ClientList createInstanceFromOther(ClientList other) {
        return new ClientList(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientList expected, ClientList actual) {
        assertNotSame(expected.getItems(), actual.getItems());
    }

    @Override
    public void generatedStringBeginsWithClassName() {
        // preventing test from run
    }

    @Test
    public void visit_clientElVisitor_accepted() {
        ClientExpression clientExpression = Mockito.spy(new ClientNull());
        ClientExpression clientExpression2 = Mockito.spy(new ClientString("string"));

        ClientList instance = createInstanceWithDefaultParameters();
        instance.setItems(Arrays.asList(clientExpression, clientExpression2));

        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
        verify(clientExpression).accept(clientELVisitor);
        verify(clientExpression2).accept(clientELVisitor);
    }

    @Test
    public void constructor_expressionsList_instance() {
        ClientList result = new ClientList(CLIENT_EXPRESSIONS);
        assertEquals(CLIENT_EXPRESSIONS, result.getItems());
    }

    @Test
    public void addItem_expression_listWithExpression() {
        ClientExpression clientExpression = new ClientNumber(23.5F);
        ClientList instance = createFullyConfiguredInstance();

        instance.addItem(clientExpression);

        assertTrue(instance.getItems().contains(clientExpression));
    }

    @Test
    public void deleteItem_expression_listWithoutExpression() {
        ClientList instance = createFullyConfiguredInstance();

        instance.deleteItem(CLIENT_STRING);

        assertFalse(instance.getItems().contains(CLIENT_STRING));
    }

    @Test
    public void toString_allItemsInParenDividedByComma() {
        ClientList instance = createFullyConfiguredInstance();

        String result = instance.toString();

        assertEquals("('string', 23)", result);
    }
}
