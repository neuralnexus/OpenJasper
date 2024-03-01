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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientWhereTest extends BaseDTOJSONPresentableTest<ClientWhere> {
    private static final ClientExpressionContainer FILTER_EXPRESSION =
            new ClientExpressionContainer().setObject(new ClientString("expressionString"));
    private static final Map<String, ClientExpressionContainer> PARAMETERS =
            Collections.singletonMap("key", new ClientExpressionContainer().setObject(new ClientString("parametErsexpressionString")));

    private static final ClientExpressionContainer FILTER_EXPRESSION_ALTERNATIVE =
            new ClientExpressionContainer().setObject(new ClientString("expressionStringAlternative"));
    private static final Map<String, ClientExpressionContainer> PARAMETERS_ALTERNATIVE =
            Collections.singletonMap("key", new ClientExpressionContainer().setObject(new ClientString("parametErsexpressionStringAlternative")));

    private ClientQueryVisitor clientQueryVisitor;

    @BeforeEach
    public void init() {
        clientQueryVisitor = Mockito.mock(ClientQueryVisitor.class);
    }

    @Override
    protected List<ClientWhere> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFilterExpression(FILTER_EXPRESSION_ALTERNATIVE),
                createFullyConfiguredInstance().setParameters(PARAMETERS_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setFilterExpression(null),
                createFullyConfiguredInstance().setParameters(null)
        );
    }

    @Override
    protected ClientWhere createFullyConfiguredInstance() {
        return new ClientWhere()
                .setFilterExpression(FILTER_EXPRESSION)
                .setParameters(PARAMETERS);
    }

    @Override
    protected ClientWhere createInstanceWithDefaultParameters() {
        return new ClientWhere();
    }

    @Override
    protected ClientWhere createInstanceFromOther(ClientWhere other) {
        return new ClientWhere(other);
    }

    @Test
    public void generatedStringBeginsWithClassName() {

    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientWhere expected, ClientWhere actual) {
        assertNotSame(expected.getFilterExpression(), actual.getFilterExpression());
        assertNotSameCollection(expected.getParameters().values(), actual.getParameters().values());
    }

    @Test
    public void clientQueryVisitorCanBeAccepted() {
        ClientNull clientObject  = Mockito.spy(new ClientNull());

        ClientWhere instance = createInstanceWithDefaultParameters();
        instance.setFilterExpression(new ClientExpressionContainer().setObject(clientObject));

        instance.accept(clientQueryVisitor);

        verify(clientQueryVisitor).visit(instance);
        verify(clientQueryVisitor).visit(clientObject);
    }

    @Test
    public void constructor_parameters_instance() {
        ClientWhere result = new ClientWhere(PARAMETERS);
        assertEquals(PARAMETERS, result.getParameters());
    }

    @Test
    public void constructor_parametersAndClientExpression_instance() {
        ClientNull clientObject  = new ClientNull();
        ClientExpressionContainer expressionContainer = new ClientExpressionContainer().setObject(clientObject);

        ClientWhere result = new ClientWhere(clientObject, PARAMETERS);
        assertEquals(PARAMETERS, result.getParameters());
        assertEquals(expressionContainer, result.getFilterExpression());
    }
}
