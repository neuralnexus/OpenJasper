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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p></p>
 *
 * @author Hemakumar.Gokulakannan
 * @version $Id$
 */
public class ClientOperatorTest {


    private ClientOperator clientOperator;
    @Test
    public void checkOperands_withNoOperandsIsValid()
    {
        clientOperator = new ClientFunction();
        ((ClientFunction) clientOperator).setFunctionName("Today");
        assertEquals(new ArrayList<ClientExpression>(),clientOperator.getOperands());
    }

    @Test
    public void addStringOperand_withOperatorInstance_noParenExpected() {
        clientOperator = new ClientAdd();
        ClientExpression operand = new ClientNumber();
        ((ClientNumber) operand).setValue(1);
        assertEquals("1",clientOperator.addStringOperand(operand));
    }
}
