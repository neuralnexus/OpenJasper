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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ClientParameterExpressionTest extends BaseDTOTest<ClientParameterExpression> {

    private static final String NAME = "name";
    private static final ClientExpressionContainer EXPRESSION_CONTAINER = new ClientExpressionContainer().setString("string");

    private static final String NAME_ALTERNATIVE = "nameAlternative";
    private static final ClientExpressionContainer EXPRESSION_CONTAINER_ALTERNATIVE = new ClientExpressionContainer().setString("stringAlternative");

    @Override
    protected List<ClientParameterExpression> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(NAME_ALTERNATIVE),
                createFullyConfiguredInstance().setExpression(EXPRESSION_CONTAINER_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setExpression(null)
        );
    }

    @Override
    protected ClientParameterExpression createFullyConfiguredInstance() {
        return new ClientParameterExpression()
                .setName(NAME)
                .setExpression(EXPRESSION_CONTAINER);
    }

    @Override
    protected ClientParameterExpression createInstanceWithDefaultParameters() {
        return new ClientParameterExpression();
    }

    @Override
    protected ClientParameterExpression createInstanceFromOther(ClientParameterExpression other) {
        return new ClientParameterExpression(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientParameterExpression expected, ClientParameterExpression actual) {
        assertNotSame(expected.getExpression(), actual.getExpression());
    }
}
