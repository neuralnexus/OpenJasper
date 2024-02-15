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

import com.jaspersoft.jasperserver.api.common.domain.Id;
import com.jaspersoft.jasperserver.api.common.domain.ValidationDetail;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ValidationDetailImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ValidationDetailImpl implements ValidationDetail {
	
	private Class validationClass;
	private String name;
	private String label;
	private String state;
	private String message;
	private Exception exception;
	
	public ValidationDetailImpl() {
		
	}

	public Id getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValidationClass(Class validationClass) {
		this.validationClass = validationClass;
	}

	public Class getValidationClass() {
		return validationClass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setResult(String state) {
		this.state = state;
	}

	public String getResult() {
		return state;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

}
