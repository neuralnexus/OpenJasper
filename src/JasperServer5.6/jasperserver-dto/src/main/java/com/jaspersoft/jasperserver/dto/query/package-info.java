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

/**
 * Package-level schema mapping needed to avoid repeating of schema URI for every xs:anyType element.
 */
@XmlSchema(xmlns = {@XmlNs(prefix = "xsi", namespaceURI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI),
        @XmlNs(prefix = "xs", namespaceURI = XMLConstants.W3C_XML_SCHEMA_NS_URI)})
package com.jaspersoft.jasperserver.dto.query;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
