/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataTypeValueClassResolver;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

/**
 * @author Anton Fomin
 * @version $Id: InputControlValueClassResolver.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class InputControlValueClassResolver {

    public static Class<?> getValueClass(DataType dataType, ReportInputControlInformation info, boolean returnNestedType) {
        Class<?> valueClass = getInputControlValueClass(info, returnNestedType);
        if (valueClass == null && dataType != null) {
            valueClass = DataTypeValueClassResolver.getValueClass(dataType);
        }
        if (valueClass == null)
            valueClass = String.class;
        return valueClass;
    }

    public static Class<?> getValueClass(DataType dataType, ReportInputControlInformation info) {
        return getValueClass(dataType, info, true);
    }

    private static Class<?> getInputControlValueClass(ReportInputControlInformation info, boolean returnNestedType) {
        Class<?> parameterType = null;
        if (info != null && info.getValueType() != null) {
            if (Collection.class.isAssignableFrom(info.getValueType())) {
                if (!returnNestedType) {
                    parameterType = info.getValueType();
                } else if (info.getNestedType() != null) {
                    parameterType = info.getNestedType();
                }
            } else {
                parameterType = info.getValueType();
            }
        }
        return parameterType;
    }
}
