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
package com.jaspersoft.jasperserver.remote.connection.jdbc;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * <p></p>
 *
 * @author mchan
 * @version $Id: VdsSqlEscapingStrategyTest.java 68602 2018-03-15 15:37:55Z askorodu $
 */
public class VdsSqlEscapingStrategyTest {

    private VdsSqlEscapingStrategy vdsSqlEscapingStrategy = new VdsSqlEscapingStrategy();

    @Test
    public void sqlEscape_withUnderscore() throws Exception {
        assertEquals(vdsSqlEscapingStrategy.sqlEscape(null, "/\\abc_/\\_/d"), "/\\\\abc\\_/\\\\\\_/d");
    }

    @Test
    public void schemaNameUnEscape_withSlash() throws Exception {
        assertEquals(vdsSqlEscapingStrategy.schemaNameUnEscape(null, "/\\\\\\\\abc_/\\\\/d"), "/\\\\abc_/\\/d");
    }

    @Test
    public void schemaNameEscape_withUnderscore() throws Exception {
        assertEquals(vdsSqlEscapingStrategy.schemaNameEscape(null, "SugarCRMDataSourceJNDI_public"), "SugarCRMDataSourceJNDI_public");
    }

}
