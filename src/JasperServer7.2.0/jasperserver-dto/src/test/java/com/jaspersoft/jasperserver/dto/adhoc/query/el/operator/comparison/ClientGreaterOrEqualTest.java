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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.AbstractClientOperatorTest;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientGreaterOrEqualTest extends AbstractClientOperatorTest<ClientGreaterOrEqual> {

    private static ClientExpression VARIABLE_A = new ClientVariable("A");
    private static ClientExpression VARIABLE_B = new ClientVariable("B");

    @Override
    protected ClientGreaterOrEqual createInstanceWithOperands(List<ClientExpression> operands) {
        return new ClientGreaterOrEqual(operands);
    }

    @Override
    protected String operatorId() {
        return ClientOperation.GREATER_OR_EQUAL.getName();
    }

    @Override
    protected String separator() {
        return ClientOperation.GREATER_OR_EQUAL.getDomelOperator();
    }

    @Override
    protected List<ClientGreaterOrEqual> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().unsetParen(),
                new ClientGreaterOrEqual(asList(VARIABLE_A)),
                new ClientGreaterOrEqual(asList(VARIABLE_A)).setParen(),
                new ClientGreaterOrEqual(asList(VARIABLE_A, VARIABLE_B)),
                new ClientGreaterOrEqual(asList(VARIABLE_A, VARIABLE_B)).setParen()
        );
    }

    @Override
    protected ClientGreaterOrEqual createFullyConfiguredInstance() {
        return new ClientGreaterOrEqual(TEST_CLIENT_TWO_EXPRESSIONS).setParen();
    }

    @Override
    protected ClientGreaterOrEqual createInstanceWithDefaultParameters() {
        return new ClientGreaterOrEqual();
    }

    @Override
    protected ClientGreaterOrEqual createInstanceFromOther(ClientGreaterOrEqual other) {
        return new ClientGreaterOrEqual(other);
    }
}