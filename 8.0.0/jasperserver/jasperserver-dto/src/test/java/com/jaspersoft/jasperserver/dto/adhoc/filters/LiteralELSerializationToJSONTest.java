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

import com.jaspersoft.jasperserver.dto.adhoc.DateFixtures;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class LiteralELSerializationToJSONTest extends FilterTest {

    @Test
    public void booleanAsLiteral() throws Exception {
        assertThat(json(new ClientBoolean(true)), is("{\n  \"value\" : true\n}"));
    }

    @Test
    public void dateRange() throws Exception {
        assertThat(json(new ClientRelativeDateRange("YEAR")), is("{\n  \"value\" : \"YEAR\"\n}"));
    }

    @Test
    public void integerAsLiteral() throws Exception {
        assertThat(json(new ClientNumber(555)), is(
                "{\n" +
                "  \"value\" : \"555\"\n" +
                "}"));
    }

    @Test
    public void byteAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new Byte("123"))), is(
                "{\n" +
                        "  \"value\" : \"123\"\n" +
                        "}"));
    }

    @Test
    public void shortAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new Short("123"))), is(
                "{\n" +
                        "  \"value\" : \"123\"\n" +
                        "}"));
    }

    @Test
    public void integerAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new Integer("123"))), is(
                "{\n" +
                        "  \"value\" : \"123\"\n" +
                        "}"));
    }

    @Test
    public void longAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new Long("123"))), is(
                "{\n" +
                        "  \"value\" : \"123\"\n" +
                        "}"));
    }

    @Test
    public void bigIntegerAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new BigInteger("999999999999999999999999999999999999999999999999"))), is(
                "{\n" +
                        "  \"value\" : \"999999999999999999999999999999999999999999999999\"\n" +
                        "}"));
    }

    @Test
    public void floatAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new Float("9.99"))), is(
                "{\n" +
                        "  \"value\" : \"9.99\"\n" +
                        "}"));
    }

    @Test
    public void doubleAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientNumber(new BigDecimal("9.99"))), is(
                "{\n" +
                        "  \"value\" : \"9.99\"\n" +
                        "}"));
    }

    @Test
    public void bigDecimalAsLiteral_valueIsString() throws Exception {
        Locale.setDefault(Locale.FRANCE);
        assertThat(json(new ClientNumber(new BigDecimal("9.99999999999999999999999999999999999999999999999"))), is(
                "{\n" +
                        "  \"value\" : \"9.99999999999999999999999999999999999999999999999\"\n" +
                        "}"));
    }

    @Test
    public void timeAsLiteral() throws Exception {
        assertEquals("{\n" +
                        "  \"value\" : \"15:00:00.000\"\n" +
                        "}",
                json(new ClientTime(DateFixtures.TIME_15_00)));
    }

    @Test
    public void timestampAsLiteral() throws Exception {
        assertEquals("{\n" +
                        "  \"value\" : \"2009-01-01T00:00:00.000\"\n" +
                        "}",
                json(new ClientTimestamp(DateFixtures.DATE_2009_1_1T0_0_0)));
    }

    @Test
    public void dateAsLiteral() throws Exception {
        assertThat(json(new ClientDate(DateFixtures.DATE_1961_08_26)), is(
                "{\n" +
                        "  \"value\" : \"1961-08-26\"\n" +
                        "}"));
    }

    @Test
    public void decimalAsLiteral() throws Exception {
        assertThat(json(new ClientNumber(new BigDecimal("5.0"))), is(
                "{\n" +
                        "  \"value\" : \"5.0\"\n" +
                        "}"));
    }

    @Test
    public void stringAsLiteral() throws Exception {
        assertThat(json(new ClientString("Talking Heads")), is(
                "{\n" +
                        "  \"value\" : \"Talking Heads\"\n" +
                        "}"));
    }


}
