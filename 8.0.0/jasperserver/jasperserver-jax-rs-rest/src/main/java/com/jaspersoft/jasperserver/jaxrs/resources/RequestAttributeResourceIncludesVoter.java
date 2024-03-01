/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.jaxrs.resources;

import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RequestAttributeResourceIncludesVoter implements ResourceIncludesVoter {

	private static final Log log = LogFactory.getLog(RequestAttributeResourceIncludesVoter.class);
	
	private String includeName;
	private String requestAttributeName;
	private int requestAttributeScope = RequestAttributes.SCOPE_REQUEST;
	private Predicate<Object> attributeValueValidator = new Predicate<Object>() {
		@Override
		public boolean test(Object value) {
			return Boolean.TRUE.equals(value);
		}
	};
	
	@Override
	public boolean allowInclude(String include) {
		if (includeName.equals(include)) {
			RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
			Object attribute = attributes == null ? null 
					: attributes.getAttribute(requestAttributeName, requestAttributeScope);
			boolean validAttribute = attributeValueValidator.test(attribute);
			if (!validAttribute) {
				log.warn("Rejecting resource include " + include 
						+ ", attribute " + requestAttributeName + " not valid");
			}
			return validAttribute;
		}
		
		return true;
	}

	public String getIncludeName() {
		return includeName;
	}

	public void setIncludeName(String includeName) {
		this.includeName = includeName;
	}

	public String getRequestAttributeName() {
		return requestAttributeName;
	}

	public void setRequestAttributeName(String requestAttributeName) {
		this.requestAttributeName = requestAttributeName;
	}

	public int getRequestAttributeScope() {
		return requestAttributeScope;
	}

	public void setRequestAttributeScope(int requestAttributeScope) {
		this.requestAttributeScope = requestAttributeScope;
	}

	public Predicate<Object> getAttributeValueValidator() {
		return attributeValueValidator;
	}

	public void setAttributeValueValidator(Predicate<Object> attributeValueValidator) {
		this.attributeValueValidator = attributeValueValidator;
	}

}
