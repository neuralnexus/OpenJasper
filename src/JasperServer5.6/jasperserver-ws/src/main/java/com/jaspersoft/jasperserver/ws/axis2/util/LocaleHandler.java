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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocaleHandler.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LocaleHandler extends BasicHandler {

	private static final String PROPERTY_CHANGED_LOCALE_CONTEXT = "com.jaspersoft.jasperserver.changed.locale.context";
	private static final String PROPERTY_ORIGINAL_LOCALE_CONTEXT = "com.jaspersoft.jasperserver.original.locale.context";
	
	private static final List headers;
	
	static {
		headers = new ArrayList(1);
		headers.add(new QName(Constants.NAMESPACE, Constants.HEADER_LOCALE));
	}
	
	public List getUnderstoodHeaders() {
		return headers;
	}
	
	public void invoke(MessageContext msgContext) throws AxisFault {
		if (msgContext.getPastPivot()) {
			resetLocale(msgContext);
		} else {
			setLocale(msgContext);
		}
	}

	public void onFault(MessageContext msgContext) {
		resetLocale(msgContext);
	}

	protected void setLocale(MessageContext msgContext) throws AxisFault {
		SOAPEnvelope envelope = msgContext.getCurrentMessage().getSOAPEnvelope();
		SOAPHeaderElement header = envelope.getHeaderByName(Constants.NAMESPACE, Constants.HEADER_LOCALE);
		if (header != null) {
			String localeCode = header.getValue();
			if (localeCode != null && localeCode.length() > 0) {
				LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
				msgContext.setProperty(PROPERTY_CHANGED_LOCALE_CONTEXT, Boolean.TRUE);
				msgContext.setProperty(PROPERTY_ORIGINAL_LOCALE_CONTEXT, localeContext);
				
				Locale locale = LocaleHelper.getInstance().getLocale(localeCode);
				LocaleContextHolder.setLocale(locale);
			}
		}
	}

	protected void resetLocale(MessageContext msgContext) {
		if (msgContext.containsProperty(PROPERTY_CHANGED_LOCALE_CONTEXT)) {
			LocaleContext originalContext = (LocaleContext) msgContext.getProperty(PROPERTY_ORIGINAL_LOCALE_CONTEXT);
			LocaleContextHolder.setLocaleContext(originalContext);
			
			msgContext.removeProperty(PROPERTY_CHANGED_LOCALE_CONTEXT);
			msgContext.removeProperty(PROPERTY_ORIGINAL_LOCALE_CONTEXT);
		}
	}

}
