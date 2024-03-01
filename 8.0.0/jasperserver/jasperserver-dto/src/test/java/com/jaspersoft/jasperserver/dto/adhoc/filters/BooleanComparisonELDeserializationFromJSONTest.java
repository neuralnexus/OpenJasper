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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class BooleanComparisonELDeserializationFromJSONTest extends FilterTest {

    @Test
    public void ensureNotEqualInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"notEqual\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"boolean\" : {\n" +
                "             \"value\" : false\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientComparison comparison = (ClientComparison) w.getFilterExpression().getObject();

        assertThat(comparison, is(instanceOf(ClientNotEqual.class)));
        assertEquals(ClientOperation.NOT_EQUAL, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientBoolean.class)));
    }

    @Test
    public void ensureEqualsInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "       \"equals\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"boolean\" : {\n" +
                "             \"value\" : false\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientComparison comparison = (ClientComparison) w.getFilterExpression().getObject();

        assertThat(comparison, is(instanceOf(ClientEquals.class)));
        assertEquals(ClientOperation.EQUALS, comparison.getOperator());
        assertThat(comparison.getOperands().size(), is(2));
        assertThat(comparison.getOperands().get(1), is(instanceOf(ClientBoolean.class)));
    }


}