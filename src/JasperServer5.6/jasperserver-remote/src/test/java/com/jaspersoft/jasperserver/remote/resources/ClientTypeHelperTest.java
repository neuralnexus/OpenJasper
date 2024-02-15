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
package com.jaspersoft.jasperserver.remote.resources;

import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ClientTypeHelperTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ClientTypeHelperTest {

    public static final String NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION = "nameFromXmlRootElementAnnotation";
    public static final String NAME_FROM_XML_TYPE_ANNOTATION = "nameFromXmlTypeAnnotation";

    @Test
    public void extractClientType_XmlRootElementAnnotation() {
        assertEquals(NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION, ClientTypeHelper.extractClientType(TestClientObjectWithXmlRootAnnotation.class));
    }

    @Test
    public void extractClientType_XmlTypeAnnotation() {
        assertEquals(NAME_FROM_XML_TYPE_ANNOTATION, ClientTypeHelper.extractClientType(TestClientObjectWithXmlTypeAnnotation.class));
    }

    @Test
    public void extractClientType_NoAnnotation() {
        String simpleName = TestClientObjectWithoutAnnotation.class.getSimpleName();
        final String expectedClientType = simpleName.replaceFirst("^.", simpleName.substring(0, 1).toLowerCase());
        assertEquals(expectedClientType, ClientTypeHelper.extractClientType(TestClientObjectWithoutAnnotation.class));
    }

    @XmlRootElement(name = NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION)
    private class TestClientObjectWithXmlRootAnnotation {
    }

    @XmlType(name = NAME_FROM_XML_TYPE_ANNOTATION)
    private class TestClientObjectWithXmlTypeAnnotation {
    }

    private class TestClientObjectWithoutAnnotation {
    }
}
