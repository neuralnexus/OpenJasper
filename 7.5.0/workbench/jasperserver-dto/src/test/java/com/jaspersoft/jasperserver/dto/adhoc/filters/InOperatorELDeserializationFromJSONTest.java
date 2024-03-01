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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 * @version $Id$
 */
public class InOperatorELDeserializationFromJSONTest extends FilterTest {

    @Test
    public void ensureWhereVariableInNumberRange() throws Exception {
        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"range\" : {\n" +
                "             \"start\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"number\" : {\n" +
                "                   \"value\" : 1\n" +
                "                 }\n" +
                "               }\n" +
                "             },\n" +
                "             \"end\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"number\" : {\n" +
                "                   \"value\" : 20\n" +
                "                 }\n" +
                "               }\n" +
                "             }\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";


        ClientWhere w = dtoFromJSONString(jsonString, ClientWhere.class);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        ClientRange clientRange = ((ClientRange) in.getOperands().get(1));
        assertThat(((ClientNumber) clientRange.getStart().getBoundary()).getValue(), is((Number) BigDecimal.valueOf(1)));
        assertThat(((ClientNumber) clientRange.getEnd().getBoundary()).getValue(), is((Number) BigDecimal.valueOf(20)));
    }

    @Test
    public void ensureWhereVariableInStringRange() throws Exception {
        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"user_name\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"range\" : {\n" +
                "             \"start\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"string\" : {\n" +
                "                   \"value\" : \"a\"\n" +
                "                 }\n" +
                "               }\n" +
                "             },\n" +
                "             \"end\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"string\" : {\n" +
                "                   \"value\" : \"z\"\n" +
                "                 }\n" +
                "               }\n" +
                "             }\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "     }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(jsonString, ClientWhere.class);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertTrue(in instanceof ClientOperator);
        assertEquals(ClientOperation.IN, in.getOperator());
        assertThat(in.getOperands().get(1), is(instanceOf(ClientRange.class)));
        assertThat(in.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(((ClientString) ((ClientRange) in.getOperands().get(1)).getStart().getBoundary()).getValue(), is("a"));
        assertThat(((ClientString) ((ClientRange) in.getOperands().get(1)).getEnd().getBoundary()).getValue(), is("z"));
    }

    @Test
    public void ensureWhereVariableInVariableRange() throws Exception {
        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"pH\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"range\" : {\n" +
                "             \"start\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"variable\" : {\n" +
                "                   \"name\" : \"a\"\n" +
                "                 }\n" +
                "               }\n" +
                "             },\n" +
                "             \"end\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"variable\" : {\n" +
                "                   \"name\" : \"b\"\n" +
                "                 }\n" +
                "               }\n" +
                "             }\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(jsonString, ClientWhere.class);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertTrue(in instanceof ClientOperator);
        assertEquals(ClientOperation.IN, in.getOperator());
        assertThat(in.getOperands().get(1), is(instanceOf(ClientRange.class)));
        assertThat(in.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(((ClientRange)in.getOperands().get(1)).getEnd().getBoundary(), is(instanceOf(ClientVariable.class)));
        assertThat(((ClientVariable)((ClientRange)in.getOperands().get(1)).getEnd().getBoundary()).getName(), is("b"));

    }

    @Test
    public void ensureWhereVariableInFunctionRange() throws Exception {
        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"range\" : {\n" +
                "             \"start\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"function\" : {\n" +
                "                   \"operands\" : [ {\n" +
                "                     \"string\" : {\n" +
                "                       \"value\" : \"birth_year\"\n" +
                "                     }\n" +
                "                   } ],\n" +
                "                   \"functionName\" : \"attribute\"\n" +
                "                 }\n" +
                "               }\n" +
                "             },\n" +
                "             \"end\" : {\n" +
                "               \"boundary\" : {\n" +
                "                 \"function\" : {\n" +
                "                   \"operands\" : [ {\n" +
                "                     \"string\" : {\n" +
                "                       \"value\" : \"start_year\"\n" +
                "                     }\n" +
                "                   } ],\n" +
                "                   \"functionName\" : \"attribute\"\n" +
                "                 }\n" +
                "               }\n" +
                "             }\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";


        ClientWhere w = dtoFromJSONString(jsonString, ClientWhere.class);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertTrue(in instanceof ClientOperator);
        assertEquals(ClientOperation.IN, in.getOperator());
        assertTrue(in.getOperands().get(1) instanceof ClientRange);
        assertTrue(in.getOperands().get(0) instanceof ClientVariable);
        assertTrue(((ClientRange)in.getOperands().get(1)).getEnd().getBoundary() instanceof ClientFunction);
        assertThat(((ClientFunction)((ClientRange)in.getOperands().get(1)).getEnd().getBoundary()).getFunctionName(), is("attribute"));
    }

    @Test
    public void ensureWhereVariableInList_ofNumbers() throws Exception {

        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         },\n" +
                "          \n" +
                "             { \"number\" : { \"value\" : \"1\" } },          { \"number\" : { \"value\" : \"2\" } },          { \"number\" : { \"value\" : \"3\" } }    ]    }\n" +
                "       \n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertTrue(in instanceof ClientOperator);
        assertEquals(ClientOperation.IN, in.getOperator());
        assertEquals(4, in.getOperands().size());
        assertTrue(in.getOperands().get(1) instanceof ClientNumber);
    }

    @Test
    public void ensureWhereVariableInList_ofStrings() throws Exception {

        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         },\n" +
                "          \n" +
                "             { \"string\" : { \"value\" : \"ok\" } },          { \"string\" : { \"value\" : \"not-ok\" } },          { \"string\" : { \"value\" : \"don't care\" } }    ]    }\n" +
                "       \n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientIn in = (ClientIn) w.getFilterExpression().getObject();

        assertTrue(in instanceof ClientOperator);
        assertEquals(ClientOperation.IN, in.getOperator());
        assertEquals(4, in.getOperands().size());
        assertTrue(in.getOperands().get(1) instanceof ClientString);
    }


}