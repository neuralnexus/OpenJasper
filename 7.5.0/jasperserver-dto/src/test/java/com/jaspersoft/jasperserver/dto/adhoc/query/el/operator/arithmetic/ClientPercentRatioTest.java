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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic;

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

class ClientPercentRatioTest extends AbstractClientOperatorTest<ClientPercentRatio> {

    private static ClientExpression VARIABLE_A = new ClientVariable("A");
    private static ClientExpression VARIABLE_B = new ClientVariable("B");

    @Override
    protected ClientPercentRatio createInstanceWithOperands(List<ClientExpression> operands) {
        return new ClientPercentRatio(operands);
    }

    @Override
    protected String operatorId() {
        return ClientPercentRatio.EXPRESSION_ID;
    }

    @Override
    protected String separator() {
        return ClientOperation.PERCENT_FIELD_RATIO.getDomelOperator();
    }

    @Override
    protected List<ClientPercentRatio> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().unsetParen(),
                new ClientPercentRatio(asList(VARIABLE_A)),
                new ClientPercentRatio(asList(VARIABLE_A)).setParen(),
                new ClientPercentRatio(asList(VARIABLE_A, VARIABLE_B)),
                new ClientPercentRatio(asList(VARIABLE_A, VARIABLE_B)).setParen()
        );
    }

    @Override
    protected ClientPercentRatio createFullyConfiguredInstance() {
        return new ClientPercentRatio(TEST_CLIENT_TWO_EXPRESSIONS).setParen();
    }

    @Override
    protected ClientPercentRatio createInstanceWithDefaultParameters() {
        return new ClientPercentRatio();
    }

    @Override
    protected ClientPercentRatio createInstanceFromOther(ClientPercentRatio other) {
        return new ClientPercentRatio(other);
    }
}