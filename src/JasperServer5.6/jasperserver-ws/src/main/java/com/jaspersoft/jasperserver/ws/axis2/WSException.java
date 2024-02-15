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
package com.jaspersoft.jasperserver.ws.axis2;

/**
 *
 * @author gtoffoli
 */
public class WSException extends java.lang.Exception {
    
    public static final int NO_ERROR = 0;
    public static final int GENERAL_ERROR = 1;
    public static final int GENERAL_ERROR2 = 2;
    public static final int REFERENCED_RESOURCE_NOT_FOUND = 3;
    public static final int EXPORT_ERROR = 4;
    public static final int FILL_ERROR = 5;
    public static final int GENERAL_REQUEST_ERROR = 6;
    
    private int errorCode = 0;
    
    /** Creates a new instance of WSException */
    public WSException(int code, String message) {
        super(message);
        this.errorCode = code;
    }
    
    public WSException(Exception e) {
    	this(GENERAL_ERROR, e.getMessage());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
}
