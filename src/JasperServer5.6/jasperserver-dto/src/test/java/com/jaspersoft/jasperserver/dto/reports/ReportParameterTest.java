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

package com.jaspersoft.jasperserver.dto.reports;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ReportParameterTest {
    private final ReportParameter param1 = new ReportParameter();
    private final ReportParameter param2 = new ReportParameter();

    @BeforeMethod
    public void setUp() throws Exception {
        param1.setValues(Arrays.asList("a", "b"));
        param2.setValues(Arrays.asList("b", "a"));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(param1.equals(param2));
        assertTrue(param2.equals(param1));
        assertFalse(param2.getValues().equals(param1.getValues()));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(param1.hashCode(), param2.hashCode());
    }
}
