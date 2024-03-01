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

package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ELUtilsTest {

    @Test
    public void isNumericString_stringWithNumbersOnly_true() {
        boolean result = ELUtils.isNumericString("23");

        assertTrue(result);
    }

    @Test
    public void isNumericString_stringWithNumbersAndSign_true() {
        boolean result = ELUtils.isNumericString("-23");

        assertTrue(result);
    }

    @Test
    public void isNumericString_stringWithNotNumbers_false() {
        boolean result = ELUtils.isNumericString("ff");

        assertFalse(result);
    }

    @Test
    public void isFloatingPointString_stringWithNumbersOnly_true() {
        boolean result = ELUtils.isFloatingPointString("23");

        assertTrue(result);
    }

    @Test
    public void isFloatingPointString_stringWithNumbersAndDotInTheMiddle_true() {
        boolean result = ELUtils.isFloatingPointString("23.23");

        assertTrue(result);
    }


    @Test
    public void isFloatingPointString_stringWithNumbersAndDotInTheEnd_false() {
        boolean result = ELUtils.isFloatingPointString("23.");

        assertTrue(result);
    }

    @Test
    public void isFloatingPointString_stringWithNormalizedFloatingPoint_true() {
        boolean result = ELUtils.isFloatingPointString("1.574e10");

        assertTrue(result);
    }
}
