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

package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class VariableOperationsTest {

    private VariableOperations clientVariable;

    @BeforeEach
    public void setup() {
        clientVariable = new ClientVariable("name");
    }

    @Test
    public void in_clientLiterals_clientIn() {
        ClientLiteral expression1 = new ClientNumber(23F);
        ClientLiteral expression2 = new ClientNumber(23);

        ClientList clientList = new ClientList(Arrays.<ClientExpression>asList(expression1, expression2));

        ClientIn result = clientVariable.in(expression1, expression2);

        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientList, result.getRhs());
    }

    @Test
    public void in_strings_clientIn() {
        String value = "string";
        String value2 = "string2";

        ClientString expression1 = new ClientString(value);
        ClientString expression2 = new ClientString(value2);

        ClientList clientList = new ClientList(Arrays.<ClientExpression>asList(expression1, expression2));

        ClientIn result = clientVariable.in(value, value2);

        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientList, result.getRhs());
    }

    @Test
    public void in_booleans_clientIn() {
        Boolean value = true;
        Boolean value2 = false;

        ClientBoolean expression1 = new ClientBoolean(value);
        ClientBoolean expression2 = new ClientBoolean(value2);

        ClientList clientList = new ClientList(Arrays.<ClientExpression>asList(expression1, expression2));

        ClientIn result = clientVariable.in(value, value2);

        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientList, result.getRhs());
    }

    @Test
    public void in_integers_clientIn() {
        Integer value = 23;
        Integer value2 = 24;

        ClientNumber expression1 = new ClientNumber(value);
        ClientNumber expression2 = new ClientNumber(value2);

        ClientList clientList = new ClientList(Arrays.<ClientExpression>asList(expression1, expression2));

        ClientIn result = clientVariable.in(value, value2);

        assertEquals(clientVariable, result.getLhs());
        assertTrue(((ClientList) result.getOperands().get(1)).getItems().containsAll(clientList.getItems()));
    }

    @Test
    public void in_clientRange_clientIn() {
        ClientRange expression = new ClientRange(new ClientNumber(23F), new ClientString("string"));

        ClientIn result = clientVariable.in(expression);

        assertEquals(clientVariable, result.getLhs());
        assertEquals(expression, result.getRhs());
    }

    @Test
    public void inRange_strings_clientIn() {
        String value = "string";
        String value2 = "string2";

        ClientString expression1 = new ClientString(value);
        ClientString expression2 = new ClientString(value2);

        ClientRange clientRange = new ClientRange(expression1, expression2);

        ClientIn result = clientVariable.inRange(value, value2);

        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientRange, result.getRhs());
    }


    @Test
    public void inRange_bigDecimals_clientIn() {
        BigDecimal value = BigDecimal.valueOf(23L);
        BigDecimal value2 = BigDecimal.valueOf(24L);

        ClientNumber expression1 = new ClientNumber(value);
        ClientNumber expression2 = new ClientNumber(value2);

        ClientRange clientRange = new ClientRange(expression1, expression2);

        ClientIn result = clientVariable.inRange(value, value2);

        assertEquals(clientVariable, result.getLhs());
        assertEquals(clientRange, result.getRhs());
    }
}
