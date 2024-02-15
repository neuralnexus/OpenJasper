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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

public class XMLUtil {
    private static DocumentBuilderFactory domFactory;
    private static XPathFactory xPathFactory;


    static{
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        xPathFactory = XPathFactory.newInstance();

    }

    public static Document toDocument(InputStream is)throws Exception {
        SAXBuilder builder = new SAXBuilder();
		return builder.build(is);
    }

    public static Document toDocument(String xmlString)throws Exception {
        InputStream is = new ByteArrayInputStream(xmlString.getBytes());
    	return toDocument(is);
    }


    /**
     * Add Quotes if not a valid JRXML expression (eg $R())
     * @param expr
     * @return
     */
    public static String getAsJRXMLTextFieldExpression(String expr, boolean renderBlank) {
        //should use regex - and will if necessary
        if (expr == null || renderBlank) {
            return "\"\"";
        }
        if (!expr.startsWith("$")) {
            return "\"" + expr + "\"";
        }
        return expr;

    }

    public static Date getUniqueDate(int counter) {
    	return new Date((new Date()).getTime()+counter);
    }

    public static DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException{
          return domFactory.newDocumentBuilder();
    }

    public static XPath getNewXPath(){
        return xPathFactory.newXPath();
    }


}
