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
package com.jaspersoft.jasperserver.api.common.domain;

import com.jaspersoft.jasperserver.api.common.domain.Id;

/**
 * 
 * @author tkavanagh
 * @version $Id: ValidationDetail.java 47331 2014-07-18 09:13:06Z kklein $
 *
 */
public interface ValidationDetail {

	public Id getId();
	
	/**
	 * 
	 * @return Class - Class of Resource being validated
	 */
	public Class getValidationClass();
	
	public String getName();
	
	public String getLabel();
	
	/**
	 * 
	 * @return String - such as VALID, VALID_STATIC, VALID_DYNAMIC, ERROR
	 */
	public String getResult();
	
	/**
	 * 
	 * @return String - holds value if result in non-valid
	 */
	public String getMessage();
	
	/**
	 * 
	 * @return Exception - potentially holds value if result if non-valid
	 */
	public Exception getException();
	
}
