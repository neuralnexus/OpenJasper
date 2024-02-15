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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ValidationErrorImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "error")
public class ValidationErrorImpl implements ValidationError, Serializable {
	
	private static final long serialVersionUID = 1L;
    private String errorCode;
	private Object[] arguments;
	private String defaultMessage;
	private String field;

    public ValidationErrorImpl(){}

	public ValidationErrorImpl(String errorCode, Object[] arguments, String defaultMessage, String field) {
		this.errorCode = errorCode;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
		this.field = field;
	}
	
	public ValidationErrorImpl(String errorCode, Object[] arguments, String defaultMessage) {
		this(errorCode, arguments, defaultMessage, null);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorArguments() {
		return arguments;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public String getField() {
		return field;
	}

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setErrorArguments(Object... arguments){
        this.setArguments(arguments);
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public void setField(String field) {
        this.field = field;
    }


	public String toString() {
		if (getDefaultMessage() != null) {
			return MessageFormat.format(getDefaultMessage(), getErrorArguments());
		}
		
		if (getField() == null) {
			return getErrorCode();
		}
		
		return getErrorCode() + "." + getField();
	}
}
