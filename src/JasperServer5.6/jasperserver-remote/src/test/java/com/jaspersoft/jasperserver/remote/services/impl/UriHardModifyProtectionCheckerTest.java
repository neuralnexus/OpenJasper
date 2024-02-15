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
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.remote.services.impl;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.jgroups.util.Util.assertFalse;
import static org.jgroups.util.Util.assertTrue;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: UriHardModifyProtectionCheckerTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class UriHardModifyProtectionCheckerTest {

    private UriHardModifyProtectionChecker checker = new UriHardModifyProtectionChecker();
    @Test
    public void isHardModifyProtected(){
        final String protectedUri = "protectedUri";
        final String uriProtectionPattern = ".*pattern.*";
        Set<String> patterns = new HashSet<String>();
        patterns.add(protectedUri);
        patterns.add(uriProtectionPattern);
        checker.modifyProtected = patterns;
        assertFalse(checker.isHardModifyProtected("nonProtectedUri"));
        assertTrue(checker.isHardModifyProtected(protectedUri));
        assertTrue(checker.isHardModifyProtected("patternProtectedUri1"));
        assertTrue(checker.isHardModifyProtected("/pattern/Protected/Uri/2"));
    }
}
