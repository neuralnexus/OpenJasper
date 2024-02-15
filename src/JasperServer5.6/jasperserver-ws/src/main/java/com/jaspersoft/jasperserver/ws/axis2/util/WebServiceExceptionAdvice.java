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

package com.jaspersoft.jasperserver.ws.axis2.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.AxisFault;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: WebServiceExceptionAdvice.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class WebServiceExceptionAdvice implements ThrowsAdvice, MessageSourceAware {

	private MessageSource messageSource;
	private MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
	}

	public void afterThrowing(JSException ex) throws Throwable {
		String message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), getLocale());
		Element stackTraceEl = createStackTraceElement(ex);
		throw new AxisFault(org.apache.axis.Constants.FAULT_CLIENT, message, null, new Element[]{stackTraceEl});
	}

	protected Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	protected Element createStackTraceElement(JSException ex) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element stackTraceEl = doc.createElementNS(
				org.apache.axis.Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME.getNamespaceURI(),
				org.apache.axis.Constants.QNAME_FAULTDETAIL_EXCEPTIONNAME.getLocalPart());
		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));
		CDATASection data = doc.createCDATASection(writer.toString());
		stackTraceEl.appendChild(data);
		return stackTraceEl;
	}

	public void afterThrowing(JSValidationException ex) throws Throwable {
		StringBuffer msgBuffer = new StringBuffer();
		for (Iterator it = ex.getErrors().getErrors().iterator(); it.hasNext();) {
			ValidationError error = (ValidationError) it.next();
			String[] codes;
			if (error.getField() == null) {
				codes = messageCodesResolver.resolveMessageCodes(error.getErrorCode(), null);
			} else {
				codes = messageCodesResolver.resolveMessageCodes(error.getErrorCode(), null, error.getField(), null);
			}
			MessageSourceResolvable messageResolvable = new DefaultMessageSourceResolvable(codes, 
					error.getErrorArguments(), error.getDefaultMessage());
			String message = messageSource.getMessage(messageResolvable, getLocale());
			msgBuffer.append(message);
			msgBuffer.append('\n');
		}
		
		Element stackTraceEl = createStackTraceElement(ex);
		throw new AxisFault(org.apache.axis.Constants.FAULT_CLIENT, msgBuffer.toString(), null, 
				new Element[]{stackTraceEl});
	}

}
