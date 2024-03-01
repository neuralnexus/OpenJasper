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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InputControlPagination {
    public static final String OUT_OF_RANGE_ERROR = "error.out.of.range";
    public static final String SELECT = "select";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String CRITERIA = "criteria";

    /**
     * Get offset from parameters and validate it.
     *
     * @param inputControl
     * @param parameters
     * @param size
     * @return offset
     */
    public int getOffset(InputControl inputControl, Map<String, Object> parameters, int size, Map<String, String> errors) {
        Object offsetParamValue = parameters.get(inputControl.getName() + "_" + OFFSET);
        if (offsetParamValue != null) {
            int offset = 0;
            try {
                offset = Integer.parseInt((String) offsetParamValue);
            } catch (NumberFormatException e) {
                throwException(OFFSET, offset, errors);
                return offset;
            }

            //check if the offset is out of range
            validateOffset(offset, size, errors);
            return offset;
        } else {
            return 0;
        }
    }

    /**
     * validate the offset with the result size.
     *
     * @param offset
     * @param size
     * @return offset
     */
    public int validateOffset(int offset, int size, Map<String, String> errors) {
        if ((offset < 0) || (size > 0 && offset >= size)) {
            throwException(OFFSET, offset, errors);
        }
        return offset;
    }

    public void throwException(String fieldName, int value, Map<String, String> errors) {
        if (errors != null) {
            errors.put(fieldName, String.valueOf(value));
        } else {
            throw new InputControlValidationException(OUT_OF_RANGE_ERROR, new Object[]{fieldName, String.valueOf(value)}, null, null);
        }
    }

    /**
     * Get Limit from parameters and validate it.
     *
     * @param inputControl
     * @param parameters
     * @return limit
     */
    public int getLimit(InputControl inputControl, Map<String, Object> parameters, Map<String, String> errors) {
        Object limitParamValue = parameters.get(inputControl.getName() + "_" + LIMIT);
        if (limitParamValue != null) {
            int limit = Integer.MAX_VALUE;

            try {
                limit = Integer.parseInt((String) limitParamValue);
            } catch (NumberFormatException e) {
                throwException(LIMIT, limit, errors);
                return limit;
            }

            //check if the limit is out of range
            if (limit <= 0) {
                throwException(LIMIT, limit, errors);
            }
            return limit;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public void checkLimitOffsetRange(Map<String, String> errors) {
        if (!errors.isEmpty()) {
            Object[] args = new Object[]{errors.keySet(), errors.values()};
            throw new InputControlValidationException(OUT_OF_RANGE_ERROR, args, null, null);
        }
    }
}
