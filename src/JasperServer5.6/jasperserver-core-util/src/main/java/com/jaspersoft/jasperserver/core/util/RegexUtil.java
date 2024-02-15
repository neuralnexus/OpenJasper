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

package com.jaspersoft.jasperserver.core.util;

import java.util.regex.Pattern;

public class RegexUtil {

     /**
     * This method is used to create a regex pattern format
     * @param patternFormat String you want to create a pattern form
     * @return Pattern object created based on string.
     */
    public static Pattern getResourceKeyPattern(String patternFormat) {
        return Pattern.compile(patternFormat);
    }

    /**
     * This method is used to get the types for each argument
     * @param testArgs array of arguments
     * @return array of types
     */
    public static Class[] getArgTypesFromArgs(Object[] testArgs) {
		Class[] result = new Class[testArgs.length];
		for (int i=0; i<testArgs.length; i++) {
			result[i] = testArgs[i].getClass();
		}
		return result;
	}



}
