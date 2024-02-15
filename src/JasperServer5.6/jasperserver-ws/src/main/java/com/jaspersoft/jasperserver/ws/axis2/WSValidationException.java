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

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: WSValidationException.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class WSValidationException extends WSException {

	private final String objectName;
	private final ValidationErrors errors;
	
	public WSValidationException(String objectName, ValidationErrors errors) {
		super(GENERAL_ERROR2, errors.toString());
		
		this.objectName = objectName;
		this.errors = errors;
	}

	public ValidationErrors getErrors() {
		return errors;
	}

	public String getObjectName() {
		return objectName;
	}

}
