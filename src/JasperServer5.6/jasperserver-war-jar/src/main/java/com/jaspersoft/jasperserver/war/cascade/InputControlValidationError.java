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

package com.jaspersoft.jasperserver.war.cascade;

import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author inesterenko
 */

@XmlRootElement(name = "error")
public class InputControlValidationError extends ValidationErrorImpl{

    private String inputControlUri;

    private String invalidValue;

    public InputControlValidationError(String errorCode, Object[] arguments, String defaultMessage, String inputControlUri, String invalidValue) {
        super(errorCode, arguments, defaultMessage, null);
        this.inputControlUri =  inputControlUri;
        this.invalidValue = invalidValue;
    }

    public String getInputControlUri() {
        return inputControlUri;
    }

    public void setInputControlUri(String inputControlUri) {
        this.inputControlUri = inputControlUri;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }
}
