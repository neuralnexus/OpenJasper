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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:45PM
 * @version $Id$
 */
public class BigIntegerComparisonELDeserializationFromXMLTest extends FilterTest {

    @Test
    public void ensureGreaterOrEqualInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <greaterOrEqual>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <number>\n" +
                "                    <value>55555555555</value>\n" +
                "                </number>\n" +
                "            </operands>\n" +
                "        </greaterOrEqual>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientGreaterOrEqual.class)));
        assertEquals(((ClientGreaterOrEqual) where.getFilterExpression().getObject()).getOperator(), ClientOperation.GREATER_OR_EQUAL);
        assertThat((((ClientGreaterOrEqual) where.getFilterExpression().getObject()).getOperands().get(0)), is(instanceOf(ClientVariable.class)));
        assertThat((((ClientGreaterOrEqual) where.getFilterExpression().getObject()).getOperands().get(1)), is(instanceOf(ClientNumber.class)));

    }

    @Test
    public void ensureLessOrEqualInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <lessOrEqual>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <number>\n" +
                "                    <value>55555555555</value>\n" +
                "                </number>\n" +
                "            </operands>\n" +
                "        </lessOrEqual>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientLessOrEqual.class)));
        assertEquals(((ClientLessOrEqual) where.getFilterExpression().getObject()).getOperator(), ClientOperation.LESS_OR_EQUAL);
        assertThat((((ClientLessOrEqual) where.getFilterExpression().getObject()).getOperands().get(0)), is(instanceOf(ClientVariable.class)));
        assertThat((((ClientLessOrEqual) where.getFilterExpression().getObject()).getOperands().get(1)), is(instanceOf(ClientNumber.class)));
    }
}