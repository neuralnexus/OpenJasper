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

package com.jaspersoft.jasperserver.api.metadata.olap.service;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link XMLATestResult} class.
 *
 * @author Andrey Kasych
 */
public class XMLATestResultTest {

    @Test
    public void obfuscatePasswordInErrorMessage() {

        XMLATestResult xmlaTestResult;
        String message;
        String expectedResult;

        message = "java.io.FileNotFoundException: http://jasperadmin|organization_1:my_password@localhost:8080/jasperserver-pro/xmla";
        expectedResult = "java.io.FileNotFoundException: http://jasperadmin|organization_1:********@localhost:8080/jasperserver-pro/xmla";
        xmlaTestResult = new XMLATestResult(XMLATestResult.XMLATestCode.BAD_CREDENTIALS, new ErrorDescriptor().setMessage(message));
        assertEquals("Password exposed in error message", expectedResult, xmlaTestResult.getMessage());

        message = "https://jasperadmin:my@password@jaspersoft.com/~js-pro-instance/xmla";
        expectedResult = "https://jasperadmin:********@jaspersoft.com/~js-pro-instance/xmla";
        xmlaTestResult = new XMLATestResult(XMLATestResult.XMLATestCode.BAD_CREDENTIALS, new ErrorDescriptor().setMessage(message));
        assertEquals("Password exposed in error message", expectedResult, xmlaTestResult.getMessage());
    }
}
