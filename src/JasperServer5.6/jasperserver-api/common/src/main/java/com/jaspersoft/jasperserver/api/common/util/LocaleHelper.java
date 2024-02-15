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
package com.jaspersoft.jasperserver.api.common.util;

import java.util.Locale;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocaleHelper.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class LocaleHelper {
	
	private static final LocaleHelper instance = new LocaleHelper();
	
	protected LocaleHelper() {
	}
	
	public static LocaleHelper getInstance() {
		return instance;
	}
	
	public String getCode(Locale locale) {
		return locale.toString();
	}
	
	public Locale getLocale(String code) {
		String language;
		String country;
		String variant;
		
		int firstSep = code.indexOf('_');
		if (firstSep < 0) {
			language = code;
			country = variant = "";
		} else {
			language = code.substring(0, firstSep);
			
			int secondSep = code.indexOf('_', firstSep + 1);
			if (secondSep < 0) {
				country = code.substring(firstSep + 1);
				variant = "";
			} else {
				country = code.substring(firstSep + 1, secondSep);
				variant = code.substring(secondSep + 1);
			}
		}
		
		return new Locale(language, country, variant);
	}
}
