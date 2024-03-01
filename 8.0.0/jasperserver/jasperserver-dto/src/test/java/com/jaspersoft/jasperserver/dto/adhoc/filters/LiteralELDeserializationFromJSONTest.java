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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters.DomELCommonSimpleDateFormats;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class LiteralELDeserializationFromJSONTest extends FilterTest {

    @Test
    public void ensureBoolean() throws Exception {
        final String booleanLiteral = "{\"value\": true}";
        ClientBoolean cl = dtoFromJSONString(booleanLiteral, ClientBoolean.class);

        assertThat(cl, is(instanceOf(ClientBoolean.class)));
        assertThat(cl.getValue(), is(instanceOf(Boolean.class)));
    }

    @Test
    public void ensureDateRange() throws Exception {
        final String dateRangeLiteral = "{\"value\": \"YEAR\"}";
        ClientRelativeDateRange cl = dtoFromJSONString(dateRangeLiteral, ClientRelativeDateRange.class);

        assertThat(cl, is(instanceOf(ClientRelativeDateRange.class)));
        assertThat(cl.getValue(), is(instanceOf(String.class)));
    }

    @Test
    public void ensureNumber() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 7\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
    }

    @Test
    public void ensureInteger() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 555\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
    }

    @Test
    public void ensureIntegerFromString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"999\"\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
    }

    @Test
    public void ensureBigIntegerFromString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"999999999999\"\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
    }

    @Test
    public void ensureString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"Ukraine\"\n" +
                "}";
        ClientString cl = dtoFromJSONString(literal, ClientString.class);

        assertThat(cl, is(instanceOf(ClientString.class)));
        assertThat(cl.getValue(), is(instanceOf(String.class)));
    }

    @Test
    public void ensureFloat() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 6.666\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
    }

    @Test
    public void ensureDouble() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 6.666\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
    }

    @Test
    public void ensureBigDecimalFromString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"9.99234345542999123456523458574238592993011193433339\"\n" +
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
        assertThat(cl.getValue().toString(), is("9.99234345542999123456523458574238592993011193433339"));
    }

    @Test
    public void ensureBigDecimal() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"1.7976931348623157E310\"\n" + // Larger than Double.MAX_VALUE by 2 orders of magnitude
                "}";
        ClientNumber cl = dtoFromJSONString(literal, ClientNumber.class);

        assertThat(cl, is(instanceOf(ClientNumber.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
        assertThat(cl.getValue().toString(), is("1.7976931348623157E+310"));
    }

    @Test
    public void ensureTime() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"23:33:15\"\n" +
                "}";
        ClientTime cl = dtoFromJSONString(literal, ClientTime.class);

        assertThat(cl, is(instanceOf(ClientTime.class)));
        assertThat(cl.getValue(), is(instanceOf(Time.class)));
    }

    @Test
    public void ensureTimestampISOFormatting_withMilliseconds() throws Exception {
        final String timestampLiteral = "{\"value\": \"2018-01-26T03:33:30.420\"}";
        ClientTimestamp timestamp = dtoFromJSONString(timestampLiteral, ClientTimestamp.class);

        assertTrue(timestamp != null &&
                timestamp.getValue() != null &&
                timestamp.getValue() instanceof Timestamp &&
                timestamp.getValue().equals(new Timestamp(DateUtils.parseDate("2018-01-26T03:33:30.420",
                        DomELCommonSimpleDateFormats.ISO_TIMESTAMP_FORMAT_STRING).getTime()))
        );
        assertEquals("ts'2018-01-26T03:33:30.420'", timestamp.toString());
    }

    @Test
    public void ensureTimestampISOFormatting_unspecifiedMilliseconds() throws Exception {
        final String timestampLiteral = "{\"value\": \"2018-01-26T03:33:30\"}";
        ClientTimestamp timestamp = dtoFromJSONString(timestampLiteral, ClientTimestamp.class);

        assertTrue(timestamp != null);
        assertTrue(timestamp.getValue() != null &&
                timestamp.getValue() instanceof Timestamp &&
                timestamp.getValue().equals(new Timestamp(DateUtils.parseDate("2018-01-26T03:33:30",
                        DomELCommonSimpleDateFormats.ISO_TIMESTAMP_FORMAT_STRING_NO_MILLISECONDS).getTime()))
        );
        assertEquals("ts'2018-01-26T03:33:30'", timestamp.toString());
    }

    @Test
    public void ensureNonIsoTimestamp() throws Exception {
        final String literal = " {\n" +
                "  \"value\" : \"2016-01-26 23:33:15\"\n" +
                "}";
        ClientTimestamp cl = dtoFromJSONString(literal, ClientTimestamp.class);

        assertThat(cl, is(instanceOf(ClientTimestamp.class)));
        assertThat(cl.getValue(), is(instanceOf(Timestamp.class)));
    }

    @Test
    public void ensureNonIsoTimestampWithMilliseconds() throws Exception {
        final String literal = " {\n" +
                "  \"value\" : \"2016-01-26 23:33:15.123\"\n" +
                "}";
        ClientTimestamp cl = dtoFromJSONString(literal, ClientTimestamp.class);

        assertThat(cl, is(instanceOf(ClientTimestamp.class)));
        assertThat(cl.getValue(), is(instanceOf(Timestamp.class)));
        assertEquals("2016-01-26 23:33:15.123", cl.getValue().toString());
    }

    @Test
    public void ensureDate() throws Exception {

        final String literal = "{\n" +
                "  \"value\" : \"2016-01-26\"\n" +
                "}";

        ClientDate cl = dtoFromJSONString(literal, ClientDate.class);

        assertThat(cl, is(instanceOf(ClientDate.class)));
        assertThat(cl.getValue(), is(instanceOf(Date.class)));
    }

}
