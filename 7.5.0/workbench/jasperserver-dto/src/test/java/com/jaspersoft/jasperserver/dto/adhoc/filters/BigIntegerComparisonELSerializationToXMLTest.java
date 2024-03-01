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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import org.junit.Test;

import java.math.BigInteger;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:45PM
 * @version $Id$
 */
public class BigIntegerComparisonELSerializationToXMLTest extends FilterTest {

    @Test
    public void ensureGreaterOrEqualInWhere() throws Exception {
        ClientComparison comparison = variable("sales").gtOrEq(literal(new BigInteger("55555555555")));
        ClientWhere w = new ClientWhere(comparison);

        assertThat(xml(w), is(
                "<where>\n" +
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
                "</where>"));
    }

    @Test
    public void ensureLessInWhere() throws Exception {
        ClientComparison comparison = variable("sales").lt(literal(new BigInteger("55555555555")));
        ClientWhere w = new ClientWhere(comparison);

        assertThat(xml(w), is(
                "<where>\n" +
                "    <filterExpression>\n" +
                "        <less>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <number>\n" +
                "                    <value>55555555555</value>\n" +
                "                </number>\n" +
                "            </operands>\n" +
                "        </less>\n" +
                "    </filterExpression>\n" +
                "</where>"));
    }

}