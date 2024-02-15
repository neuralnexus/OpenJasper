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

package com.jaspersoft.jasperserver.war.util;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MimeWordEncoder.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class MimeWordEncoder implements StringEncoder {

	public String encode(String text, String charset) {
		String mimeCharset = MimeUtility.mimeCharset(charset);
		try {
			return MimeUtility.encodeText(text, mimeCharset, null);
		} catch (UnsupportedEncodingException e) {
			throw new JSExceptionWrapper(e);
		}
	}

}
