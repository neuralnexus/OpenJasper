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

package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.PARAMETER_NAME_IN_NOT_VALID;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ParameterMapValidatorTest {

    private ParameterMapValidator objectUnderTests = new ParameterMapValidator();

    @Test
    public void initialize() {
        ParameterMap container = mock(ParameterMap.class);

        objectUnderTests.initialize(container);
        verifyZeroInteractions(container);
    }

    /*
     * isValid
     */

    @Test
    public void isValid_nullMap_true() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        boolean isValid = objectUnderTests.isValid(null, stub);

        assertTrue(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void isValid_emptyMap_true() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        Map<String, ClientExpressionContainer> map = new HashMap<String, ClientExpressionContainer>();

        boolean isValid = objectUnderTests.isValid(map, stub);

        assertTrue(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void isValid_mapWithKeyIsNull_false() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        Map<String, ClientExpressionContainer> map = new HashMap<String, ClientExpressionContainer>();
        map.put(null, null);

        boolean isValid = objectUnderTests.isValid(map, stub);

        assertFalse(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void isValid_mapWithKeyContainsDot_false() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        Map<String, ClientExpressionContainer> map = new HashMap<String, ClientExpressionContainer>();
        map.put(".", null);

        boolean isValid = objectUnderTests.isValid(map, stub);

        assertFalse(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void isValid_mapWithKeyIsDigit_false() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        Map<String, ClientExpressionContainer> map = new HashMap<String, ClientExpressionContainer>();
        map.put("1", null);

        boolean isValid = objectUnderTests.isValid(map, stub);

        assertFalse(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void isValid_mapWithKeyContainsSpecialSymbol_false() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        Map<String, ClientExpressionContainer> map = new HashMap<String, ClientExpressionContainer>();
        map.put("a+", null);

        boolean isValid = objectUnderTests.isValid(map, stub);

        assertFalse(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void isValid_mapWithKeyIsString_true() {
        ConstraintValidatorContext stub = mock(ConstraintValidatorContext.class);

        Map<String, ClientExpressionContainer> map = new HashMap<String, ClientExpressionContainer>();
        map.put("key", null);

        boolean isValid = objectUnderTests.isValid(map, stub);

        assertTrue(isValid);
        verifyZeroInteractions(stub);
    }

    @Test
    public void build_someViolationWithErrorArgs_returnErrorDescriptor() {
        ConstraintViolation violation = mock(ConstraintViolation.class);
        assertEquals(PARAMETER_NAME_IN_NOT_VALID.createDescriptor(), objectUnderTests.build(violation));
    }

}