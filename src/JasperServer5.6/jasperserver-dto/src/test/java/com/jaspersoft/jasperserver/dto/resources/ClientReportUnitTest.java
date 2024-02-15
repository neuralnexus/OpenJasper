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
package com.jaspersoft.jasperserver.dto.resources;


import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ClientReportUnitTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ClientReportUnitTest {
    private ClientReportUnit reportUnit = new ClientReportUnit();

    @Test
    public void testEquals() {
        ClientReportUnit reportUnit1 = new ClientReportUnit();
        ClientReportUnit reportUnit2 = new ClientReportUnit();
        String label = "sdfa";
        String descr = "lall";

        reportUnit1.setLabel(label);
        reportUnit2.setLabel(label);

        reportUnit1.setDescription(descr);
        reportUnit2.setDescription(descr);

        assertTrue(reportUnit1.equals(reportUnit2));
        assertEquals(reportUnit1.hashCode(), reportUnit2.hashCode());
        assertFalse(reportUnit1 == reportUnit2);
    }
}
