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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.validators;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import static org.mockito.Mockito.mock;

public class ComparableValidatorTest {
    ComparableValidator cv;
    MessageSource messageSource;

    @Before
    public void setup() {
        cv = new ComparableValidator();
        messageSource = mock(MessageSource.class);
    }

    @Test(expected = InputControlValidationException.class)
    public void validateSingleValue_withInvalidMaxInteger() {
        DataType dt = getDataType(10, 1000);
        cv.setMessageSource(messageSource);
        cv.validateSingleValue(1100, dt);
    }

    @Test
    public void validateSingleValue_withvalidnteger() {
        DataType dt = getDataType(10, 1000);
        cv.setMessageSource(messageSource);
        cv.validateSingleValue(500, dt);
    }

    @Test(expected = InputControlValidationException.class)
    public void validateSingleValue_withInvalidMinInteger() {
        DataType dt = getDataType(10,1000);
        cv.setMessageSource(messageSource);
        cv.validateSingleValue(-1,dt);
    }


    public DataType getDataType(int min, int max) {
        DataType dt = new DataTypeImpl();
        dt.setName("IC");
        dt.setLabel("IC_label");
        dt.setDescription("IC_Description");
        dt.setDataTypeType(DataType.TYPE_NUMBER);
        dt.setMinValue(0);
        dt.setMaxLength(new Integer(min));
        dt.setMaxValue(new Integer(max));
        dt.setStrictMax(true);
        return dt;
    }
}
