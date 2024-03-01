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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class StringFunctionELDeserializationFromXMLTest extends FilterTest {

    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";

    @Test
    public void ensureStartsWithInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <function>\n" +
                "           <functionName>startsWith</functionName>\n " +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>David Byrne</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "        </function>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);
        ClientFunction filters = (ClientFunction) where.getFilterExpression().getObject();

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertEquals(ClientOperation.FUNCTION, filters.getOperator());
        assertThat(filters.getFunctionName(), is(STARTS_WITH));
        assertThat(filters.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(filters.getOperands().get(1), is(instanceOf(ClientString.class)));
        assertThat(((ClientString) filters.getOperands().get(1)).getValue().toString(), is("David Byrne"));

    }

    @Test
    public void ensureEndsWithInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <function>\n" +
                "            <functionName>endsWith</functionName>" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>David Byrne</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "        </function>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);
        ClientFunction filters = (ClientFunction) where.getFilterExpression().getObject();

        assertEquals(ClientOperation.FUNCTION, filters.getOperator());
        assertThat(filters.getFunctionName(), is(ENDS_WITH));
        assertThat((filters.getOperands().get(0)), is(instanceOf(ClientVariable.class)));
        assertThat((filters.getOperands().get(1)), is(instanceOf(ClientString.class)));
        assertThat(((ClientString) filters.getOperands().get(1)).getValue().toString(), is("David Byrne"));
    }

}