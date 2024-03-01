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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;


/**
 * Created by stas on 4/8/15.
 * @version $Id$
 */
public class IntegerComparisonELDeserializationFromJSONTest extends FilterTest {

    @Test
    public void ensureGreaterOrEqualWithoutOperands() throws Exception {
        String json = "{\n  \"operator\" : \"greaterOrEqual\" \n}";

        ClientComparison comparison = dtoFromJSONString(json, ClientGreaterOrEqual.class);

        assertThat(comparison, is(instanceOf(ClientComparison.class)));

        assertEquals(ClientOperation.GREATER_OR_EQUAL, comparison.getOperator());
    }

   @Test
    public void ensureGreaterOrEqualEmptyOperands() throws Exception {
        String json = "{\n  \"operator\" : \"greaterOrEqual\",\n" +
                "  \"operands\" : [  ]\n}";

        ClientComparison comparison = dtoFromJSONString(json, ClientGreaterOrEqual.class);

        assertThat(comparison, is(instanceOf(ClientComparison.class)));

       assertEquals(ClientOperation.GREATER_OR_EQUAL, comparison.getOperator());
    }

    @Test
    public void ensureGreaterOrEqualShortForm() throws Exception {
        String json = "{\n" +
                "  \"operator\" : \"greaterOrEqual\",\n" +
                "  \"operands\" : [ {\n" +
                "    \"variable\" : {\n" +
                "      \"name\" : \"sales\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"number\" : {\n" +
                "      \"value\" : \"5\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";

        ClientComparison comparison = dtoFromJSONString(json, ClientGreaterOrEqual.class);

        assertThat(comparison, is(instanceOf(ClientComparison.class)));
        assertEquals(ClientOperation.GREATER_OR_EQUAL, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientNumber.class)));
    }

    @Test
    public void ensureGreaterOrEqual() throws Exception {
        String json = "{\n  \"operator\" : \"greaterOrEqual\",\n  \"operands\" : [ {\n    \"variable\" : {\n      \"name\" : \"sales\"\n    }\n  }, {\n    \"number\" : {\n   \"value\" : \"5\"\n    }\n  } ]\n}";


        ClientComparison comparison = dtoFromJSONString(json, ClientGreaterOrEqual.class);

        assertThat(comparison, is(instanceOf(ClientComparison.class)));
        assertEquals(ClientOperation.GREATER_OR_EQUAL, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientNumber.class)));
    }

    @Test
    public void ensureLessOrEqualInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"lessOrEqual\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"number\" : {\n" +
                "            \"value\" : \"5\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientComparison comparison = (ClientComparison) w.getFilterExpression().getObject();

        assertThat(comparison, is(instanceOf(ClientLessOrEqual.class)));
        assertEquals(ClientOperation.LESS_OR_EQUAL, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientNumber.class)));
    }

    @Test
    public void ensureLessInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"less\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"number\" : {\n" +
                "            \"value\" : \"5\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientComparison comparison = (ClientComparison) w.getFilterExpression().getObject();

        assertThat(comparison, is(instanceOf(ClientLess.class)));
        assertEquals(ClientOperation.LESS, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientNumber.class)));
    }

    @Test
    public void ensureNotEqualInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"notEqual\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"number\" : {\n" +
                "            \"value\" : \"5\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientComparison comparison = (ClientComparison) w.getFilterExpression().getObject();

        assertThat(comparison, is(instanceOf(ClientNotEqual.class)));
        assertEquals(ClientOperation.NOT_EQUAL, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientNumber.class)));
    }

    @Test
    public void ensureEqualsInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"equals\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"number\" : {\n" +
                "            \"value\" : \"5\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientComparison comparison = (ClientComparison) w.getFilterExpression().getObject();

        assertThat(comparison, is(instanceOf(ClientEquals.class)));
        assertEquals(ClientOperation.EQUALS, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientNumber.class)));
    }

}