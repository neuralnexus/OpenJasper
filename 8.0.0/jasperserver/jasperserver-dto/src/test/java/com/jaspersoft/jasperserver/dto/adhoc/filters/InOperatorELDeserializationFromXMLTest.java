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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.math.BigDecimal;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class InOperatorELDeserializationFromXMLTest extends FilterTest {

    @Test
    public void ensureWhereVariableInRange_string() throws Exception {
        String inString = "<where>\n" +
                "    <filterExpression>\n" +
                "        <in>\n" +
                "            <operands>\n" +
                "                <variable name=\"city\"/>\n" +
                "                <range>\n" +
                "                    <start>\n" +
                "                        <string>\n" +
                "                            <value>a</value>\n" +
                "                        </string>\n" +
                "                    </start>\n" +
                "                    <end>\n" +
                "                        <string>\n" +
                "                            <value>m</value>\n" +
                "                        </string>\n" +
                "                    </end>\n" +
                "                </range>\n" +
                "            </operands>\n" +
                "        </in>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere w = dto(inString);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertThat(in, Is.is(instanceOf(ClientIn.class)));
        assertEquals(ClientOperation.IN, in.getOperator());
        assertThat(in.getOperands().get(1), Is.is(instanceOf(ClientRange.class)));
        final ClientRange rhs = (ClientRange) in.getOperands().get(1);
        assertThat(((ClientString)rhs.getStart().getBoundary()).getValue(), is("a"));
        assertThat(((ClientString)rhs.getEnd().getBoundary()).getValue(), is("m"));
    }

    @Test
    public void ensureWhereVariableInRange_function() throws Exception {
        String xmlString = "<where>\n" +
                "    <filterExpression>\n" +
                "        <in>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <range>\n" +
                "                    <start>\n" +
                "                        <variable name=\"a\"/>\n" +
                "                    </start>\n" +
                "                    <end>\n" +
                "                        <variable name=\"b\"/>\n" +
                "                    </end>\n" +
                "                </range>\n" +
                "            </operands>\n" +
                "        </in>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere w = dto(xmlString);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertThat(in, Is.is(instanceOf(ClientIn.class)));
        assertEquals(ClientOperation.IN, in.getOperator());
        assertThat(in.getOperands().get(1), Is.is(instanceOf(ClientRange.class)));
        assertThat(((ClientRange)in.getOperands().get(1)).getEnd().getBoundary(), is(instanceOf(ClientVariable.class)));
        assertThat(((ClientVariable)((ClientRange)in.getOperands().get(1)).getEnd().getBoundary()).getName(), is("b"));
    }

    @Test
    public void ensureWhereVariableInRange_integer() throws Exception {
        final String inString = "<where>\n" +
               "    <filterExpression>\n" +
               "         <in>\n" +
               "             <operands>\n" +
               "                 <variable name=\"city\"/>\n" +
               "                 <range>\n" +
               "                     <start>\n" +
               "                         <number>\n" +
               "                             <value>1</value>\n" +
               "                         </number>\n" +
               "                     </start>\n" +
               "                     <end>\n" +
               "                         <number>\n" +
               "                             <value>20</value>\n" +
               "                         </number>\n" +
               "                     </end>\n" +
               "                 </range>\n" +
               "             </operands>\n" +
               "         </in>\n" +
               "    </filterExpression>\n" +
               "</where>";

        ClientWhere w = dto(inString);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertThat(in, Is.is(instanceOf(ClientIn.class)));
        assertEquals(ClientOperation.IN, in.getOperator());
        assertThat(in.getOperands().get(1), Is.is(instanceOf(ClientRange.class)));
        ClientRange rhs = (ClientRange) in.getOperands().get(1);
        assertThat(((ClientNumber) rhs.getStart().getBoundary()).getValue().intValue(), is(1));
        assertThat(((ClientNumber) rhs.getEnd().getBoundary()).getValue().intValue(), is(20));
    }


    @Test
    public void ensureWhereVariableInList() throws Exception {
        String xmlString = "<where>\n" +
                "    <filterExpression>\n" +
                "        <in>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <list>\n" +
                "                    <items>\n" +
                "                        <number>\n" +
                "                            <value>1</value>\n" +
                "                        </number>\n" +
                "                        <number>\n" +
                "                            <value>2</value>\n" +
                "                        </number>\n" +
                "                        <number>\n" +
                "                            <value>3</value>\n" +
                "                        </number>\n" +
                "                    </items>\n" +
                "                </list>\n" +
                "            </operands>\n" +
                "        </in>\n" +
                "    </filterExpression>\n" +
                "</where>";


        ClientWhere w = dto(xmlString);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertThat(in, Is.is(instanceOf(ClientIn.class)));
        assertEquals(ClientOperation.IN, in.getOperator());
        assertThat(in.getRhs(), Is.is(instanceOf(ClientList.class)));
        ClientList clientList = (ClientList) in.getRhs();
        assertTrue(clientList.getItems().contains(literal(BigDecimal.valueOf(1))));
        assertTrue(clientList.getItems().contains(literal(BigDecimal.valueOf(2))));
        assertTrue(clientList.getItems().contains(literal(BigDecimal.valueOf(3))));
    }

}
