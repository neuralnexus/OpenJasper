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
package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.GlobalDefaultValueProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface DataConverterService extends GlobalDefaultValueProvider {

    String formatSingleValue(Object typedValue, InputControl inputControl, ReportInputControlInformation info)
            throws CascadeResourceNotFoundException;

    Object convertSingleValue(String rawValue, InputControl inputControl, ReportInputControlInformation info)
            throws CascadeResourceNotFoundException, InputControlValidationException;

    String formatSingleValue(Object typedValue, DataType dataType, Class<?> valueClass);

    String formatSingleValue(Object typedValue);

    Object convertSingleValue(String rawValue, DataType dataType, Class<?> valueClass)
            throws InputControlValidationException;

    Object convertSingleValue(String rawValue, DataType dataType) throws InputControlValidationException;

}
