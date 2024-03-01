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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientVariableTest extends BaseDTOPresentableTest<ClientVariable> {

    private static final String NAME = "name";
    private static final String NAME_ALTERNATIVE = "nameAlt";

    private static final String USER_EXPECTED_TYPE = "variable";

    private ClientELVisitor clientELVisitor;

    @BeforeAll
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientVariable> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(NAME_ALTERNATIVE),
                createFullyConfiguredInstance().setName(null)
        );
    }

    @Override
    protected ClientVariable createFullyConfiguredInstance() {
        return new ClientVariable()
                .setName(NAME);
    }

    @Override
    protected ClientVariable createInstanceWithDefaultParameters() {
        return new ClientVariable();
    }

    @Override
    protected ClientVariable createInstanceFromOther(ClientVariable other) {
        return new ClientVariable(other);
    }

    @Override
    @Test
    public void generatedStringBeginsWithClassName() {
        assertEquals(fullyConfiguredTestInstance.toString(), NAME);
    }

    @Test
    public void typeForClientVariableIsVariable() {
        ClientVariable instance = new ClientVariable();
        assertEquals(USER_EXPECTED_TYPE, instance.getType());
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientVariable instance = new ClientVariable();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }

    @Test
    public void getMeReturnThisInstance() {
        ClientVariable instance = new ClientVariable();
        assertSame(instance, instance.getMe());
    }

    @Test
    public void clientInForStringsReturnsClientIn() {
        String one = "one";
        String two = "two";
        ClientExpression oneClientString = new ClientString(one);
        ClientExpression twoClientString = new ClientString(two);
        List<ClientExpression> expressionsList = Arrays.asList(oneClientString, twoClientString);
        ClientList clientExpressionsList = new ClientList(expressionsList);

        ClientVariable instance = createFullyConfiguredInstance();
        ClientIn result = instance.in(one, two);

        assertEquals(clientExpressionsList, result.getRhs());
    }

    @Test
    public void clientInForBooleanReturnsClientIn() {
        Boolean one = true;
        Boolean two = false;
        ClientExpression oneClientBoolean = new ClientBoolean(one);
        ClientExpression twoClientBoolean = new ClientBoolean(two);
        List<ClientExpression> expressionsList = Arrays.asList(oneClientBoolean, twoClientBoolean);
        ClientList clientExpressionsList = new ClientList(expressionsList);

        ClientVariable instance = createFullyConfiguredInstance();
        ClientIn result = instance.in(one, two);

        assertEquals(clientExpressionsList, result.getRhs());
    }
}
