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
package com.jaspersoft.jasperserver.dto.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import org.junit.Assert;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BIG_DECIMAL;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BIG_INTEGER;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BOOLEAN;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.BYTE;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.DATE;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.DOUBLE;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.FLOAT;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.INTEGER;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.LONG;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.SHORT;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.STRING;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.TIME;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.TIMESTAMP;
import static com.jaspersoft.jasperserver.dto.common.JavaAliasConverter.toAlias;
import static com.jaspersoft.jasperserver.dto.common.JavaAliasConverter.toJavaType;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 17.02.2017
 */
public class JavaAliasConverterTest {
    @Test
    public void shouldConvertAliasToJavaType() {
        Assert.assertEquals(toJavaType(BOOLEAN), Boolean.class.getName());
        Assert.assertEquals(toJavaType(STRING), String.class.getName());
        Assert.assertEquals(toJavaType(BYTE), Byte.class.getName());
        Assert.assertEquals(toJavaType(SHORT), Short.class.getName());
        Assert.assertEquals(toJavaType(INTEGER), Integer.class.getName());
        Assert.assertEquals(toJavaType(LONG), Long.class.getName());
        Assert.assertEquals(toJavaType(BIG_INTEGER), BigInteger.class.getName());
        Assert.assertEquals(toJavaType(FLOAT), Float.class.getName());
        Assert.assertEquals(toJavaType(DOUBLE), Double.class.getName());
        Assert.assertEquals(toJavaType(BIG_DECIMAL), BigDecimal.class.getName());
        Assert.assertEquals(toJavaType(DATE), java.util.Date.class.getName());
        Assert.assertEquals(toJavaType(TIME), Time.class.getName());
        Assert.assertEquals(toJavaType(TIMESTAMP), Timestamp.class.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForInvalidAlias() {
        toJavaType("invalidType");
    }

    @Test
    public void shouldConvertJavaTypeToAlias() {
        Assert.assertEquals(toAlias(Boolean.class.getName()), BOOLEAN);
        Assert.assertEquals(toAlias(String.class.getName()), STRING);
        Assert.assertEquals(toAlias(Byte.class.getName()), BYTE);
        Assert.assertEquals(toAlias(Short.class.getName()), SHORT);
        Assert.assertEquals(toAlias(Integer.class.getName()), INTEGER);
        Assert.assertEquals(toAlias(Long.class.getName()), LONG);
        Assert.assertEquals(toAlias(BigInteger.class.getName()), BIG_INTEGER);
        Assert.assertEquals(toAlias(Float.class.getName()), FLOAT);
        Assert.assertEquals(toAlias(Double.class.getName()), DOUBLE);
        Assert.assertEquals(toAlias(BigDecimal.class.getName()), BIG_DECIMAL);
        Assert.assertEquals(toAlias(java.util.Date.class.getName()), DATE);
        Assert.assertEquals(toAlias(java.sql.Date.class.getName()), DATE);
        Assert.assertEquals(toAlias(Time.class.getName()), TIME);
        Assert.assertEquals(toAlias(Timestamp.class.getName()), TIMESTAMP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForInvalidJavaType() {
        toAlias("invalidType");
    }

}