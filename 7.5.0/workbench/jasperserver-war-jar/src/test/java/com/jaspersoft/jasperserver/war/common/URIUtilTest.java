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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */
class URIUtilTest {

    private static List<ValueHolder> prepareSpecialSymbols() {
        return asList(
                new ValueHolder("%2B", "+"),
                new ValueHolder("%2C", ","),
                new ValueHolder("%2F", "/"),
                new ValueHolder("%3A", ":"),
                new ValueHolder("%3B", ";"),
                new ValueHolder("%3F", "?"),
                new ValueHolder("%40", "@"),
                new ValueHolder("%40%40", "@@"),
                new ValueHolder("", ""),
                new ValueHolder(" ", " "),
                new ValueHolder("+", " "),
                new ValueHolder("++", "  "),
                new ValueHolder("a+b", "a b"),
                new ValueHolder("%FF", ""),
                new ValueHolder("%88", ""),
                new ValueHolder("%CC", ""),
                new ValueHolder("%EE", ""),
                new ValueHolder("%F1", ""),
                new ValueHolder("%F9", ""),
                new ValueHolder("%FD", ""),
                new ValueHolder("%C0%80", "\u0000")
        );
    }

    @Test
    void uriDecode_nullString() {
        String actual = URIUtil.uriDecode(null);
        assertNull(actual);
    }

    @ParameterizedTest
    @MethodSource(value = "prepareSpecialSymbols")
    void uriDecode(ValueHolder holder) {
        String actual = URIUtil.uriDecode(holder.decodedValue);

        assertEquals(holder.originalValue, actual);
    }

    private static class ValueHolder {
        private String decodedValue;
        private String originalValue;

        private ValueHolder(String decodedValue, String originalValue) {
            this.decodedValue = decodedValue;
            this.originalValue = originalValue;
        }
    }
}