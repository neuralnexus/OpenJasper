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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Vasyl Spachynskyi
 * @version $Id: Id $
 * @since 25.01.2017
 */
public class AggregationExpressionSerializationToJSONTest extends FilterTest {
    @Test
    public void ensureStringExpression() throws Exception {
        ClientQueryAggregatedField field = new ClientQueryAggregatedField()
                .setExpressionContainer(new ClientExpressionContainer("Sum(sales)"));

        assertThat(json(field), is("{\n" +
                "  \"expression\" : {\n" +
                "    \"string\" : \"Sum(sales)\"\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureObjectExpressionWithStringLiteral() throws Exception {
        ClientQueryAggregatedField field = new ClientQueryAggregatedField()
                .setExpressionContainer(new ClientExpressionContainer(new ClientString("test")));

        assertThat(json(field), is("{\n" +
                "  \"expression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"string\" : {\n" +
                "        \"value\" : \"test\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureObjectExpressionWithEquals() throws Exception {
        ClientQueryAggregatedField field = new ClientQueryAggregatedField()
                .setExpressionContainer(new ClientExpressionContainer(
                        new ClientEquals().setOperands(Arrays.asList((ClientExpression) new ClientVariable("sales"),
                                new ClientNumber(1)))));

        assertThat(json(field), is("{\n" +
                "  \"expression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"equals\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"number\" : {\n" +
                "            \"value\" : \"1\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }
}
