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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class LiteralELSerializationToXMLTest extends FilterTest {

    @Test
    public void booleanAsLiteral() throws Exception {
        assertThat(xml(new ClientBoolean(true)), is(
                "<boolean>\n" +
                "    <value>true</value>\n" +
                "</boolean>"));

    }

    @Test
    public void integerAsLiteral() throws Exception {
        assertThat(xml(new ClientNumber(555)), is(
                "<number>\n" +
                "    <value>555</value>\n" +
                "</number>"));
    }

    @Test
    public void bigIntegerAsLiteral() throws Exception {
        assertThat(xml(new ClientNumber(new BigInteger("55555555555555555"))), is(
                "<number>\n" +
                "    <value>55555555555555555</value>\n" +
                "</number>"));
    }
}
