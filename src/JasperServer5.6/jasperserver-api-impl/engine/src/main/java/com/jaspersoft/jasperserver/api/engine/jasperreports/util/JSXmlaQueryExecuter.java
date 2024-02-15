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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.net.MalformedURLException;
import java.util.Map;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuter;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;

import org.w3c.tools.codec.Base64Encoder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JSXmlaQueryExecuter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JSXmlaQueryExecuter extends JRXmlaQueryExecuter {

	public JSXmlaQueryExecuter(JRDataset dataset, Map parametersMap) {
		super(dataset, parametersMap);
	}

	@Override
	protected SOAPMessage createQueryMessage() {
		SOAPMessage message = super.createQueryMessage();
		
		String user = (String) getParameterValue(
				JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER, true);
		if (user != null && user.length() > 0) {
			// set the Basic Auth header
			String auth = user;
			String password = (String) getParameterValue(
					JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, true);
			if (password != null && password.length() > 0) {
				auth += ":" + password;
			}
			
			String encodedAuth = new Base64Encoder(auth).processString();
			MimeHeaders headers = message.getMimeHeaders();
			headers.addHeader("Authorization", "Basic " + encodedAuth);
		}
		
		return message;
	}

	@Override
	protected String getSoapUrl() throws MalformedURLException {
		// do not include user & password in the URL
		return (String) getParameterValue(
				JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL);
	}
	
	

}
