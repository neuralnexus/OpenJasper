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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;


/**
 *
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar
 * @version $Id$
 */
public class LogicalOperatorELDeserializationFromJSONTest extends FilterTest {


    @Test
    public void ensureNot() throws Exception {
        String json = "{\n" +
                "  \"operands\" : [ {\"boolean\": {" +
                "   \"value\": true " +
                "}} ]," +
                "  \"operator\" : \"not\"\n" +
                "}";

        ClientNot comparison = dtoFromJSONString(json, ClientNot.class);

        assertThat(comparison, is(instanceOf(ClientNot.class)));

        assertEquals(ClientOperation.NOT, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(1));
    }


    @Test
    public void ensureNotParen() throws Exception {
        String json = "{\n" +
                "  \"operands\" : [ {\"boolean\": {" +
                "   \"value\": true " +
                "}} ]," +
                "  \"operator\" : \"not\",\n" +
                "  \"paren\": true" +
                "}";

        ClientNot comparison = dtoFromJSONString(json, ClientNot.class);

        assertThat(comparison, is(instanceOf(ClientNot.class)));

        assertEquals(ClientOperation.NOT, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(1));
    }

    @Test
    public void ensureOrInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"or\" : {\n" +
                "        \"operator\": \"or\"," +
                "        \"operands\" : [ " +
                "             {\"number\": { \"value\": \"5\"}},\n" +
                "             {\"boolean\": { \"value\": true}}" +
                "         ]" +
                "      }" +
                "    }" +
                "}" +
                "};";
        ClientWhere where = dtoFromJSONString(json, ClientWhere.class);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientOr.class)));
        ClientOr comparison = (ClientOr) where.getFilterExpression().getObject();

        assertEquals(ClientOperation.OR, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
    }

    @Test
    public void ensureAndInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"and\" : {\n" +
                "        \"operands\" : [ " +
                "             {\"boolean\": { \"value\": false}},\n" +
                "             {\"boolean\": { \"value\": true}}" +
                "         ]" +
                "      }" +
                "    }" +
                "}" +
                "};";
        ClientWhere where = dtoFromJSONString(json, ClientWhere.class);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientAnd.class)));
        ClientAnd comparison = (ClientAnd) where.getFilterExpression().getObject();

        assertEquals(ClientOperation.AND, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
    }

    @Test
    public void ensureNotShortForm() throws Exception {
        String json = "{\n" +
                "  \"operands\" : [ {\n" +
                "    \"greaterOrEqual\" : {\n" +
                "      \"operands\" : [ {\n" +
                "        \"variable\" : {\n" +
                "          \"name\" : \"sales\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"number\" : {\n" +
                "          \"value\" : \"5\"\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  } ]\n" +
                "}";

        ClientNot comparison = dtoFromJSONString(json, ClientNot.class);

        assertThat(comparison, is(instanceOf(ClientNot.class)));

        assertEquals(ClientOperation.NOT, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(1));
    }

    @Test
    public void ensureWhereNot() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"not\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"greaterOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"5\"\n" +
                "              }\n" +
                "            } ],\n" +
                "            \"operator\" : \"greaterOrEqual\"\n" +
                "          }\n" +
                "        } ],\n" +
                "        \"operator\" : \"not\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere where = dtoFromJSONString(json, ClientWhere.class);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientNot.class)));
        ClientNot comparison = (ClientNot)where.getFilterExpression().getObject();

        assertEquals(ClientOperation.NOT, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(1));
    }

    @Test
    public void ensureWhereNotShortForm() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"not\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"greaterOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"number\" : {" +
                "                \"value\" : \"5\"\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere where = dtoFromJSONString(json, ClientWhere.class);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientNot.class)));
        ClientNot comparison = (ClientNot)where.getFilterExpression().getObject();

        assertEquals(ClientOperation.NOT, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(1));
    }

}