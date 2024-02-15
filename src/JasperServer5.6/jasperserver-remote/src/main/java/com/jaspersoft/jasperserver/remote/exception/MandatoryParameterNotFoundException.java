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
package com.jaspersoft.jasperserver.remote.exception;

import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: MandatoryParameterNotFoundException.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class MandatoryParameterNotFoundException extends RemoteException {
    public final static String MANDATORY_PARAMETER_ERROR = "mandatory.parameter.error";

    public MandatoryParameterNotFoundException(String parameterName){
        this("mandatory parameter " + (parameterName != null ? "'" + parameterName + "'" : "") +" not found", parameterName);
    }

    public MandatoryParameterNotFoundException(String message, String... parameters){
        super();
        setErrorDescriptor(new ErrorDescriptor.Builder().setMessage(message).setErrorCode(MANDATORY_PARAMETER_ERROR).setParameters(parameters).getErrorDescriptor());
    }
}
