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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.literal;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public enum ClientLiteralType {
    BYTE(new ClientNumber()),
    SHORT(new ClientNumber()),
    INTEGER(new ClientNumber()),
    LONG(new ClientNumber()),
    BIG_INTEGER(new ClientNumber()),
    FLOAT(new ClientNumber()),
    DOUBLE(new ClientNumber()),
    BIG_DECIMAL(new ClientNumber()),
    DATE(new ClientDate()),
    RELATIVE_DATE_RANGE(new ClientRelativeDateRange()),
    RELATIVE_TIMESTAMP_RANGE(new ClientRelativeTimestampRange()),
    TIME(new ClientTime()),
    TIMESTAMP(new ClientTimestamp()),
    STRING(new ClientString()),
    BOOLEAN(new ClientBoolean()),
    NULL(new ClientNull());

    private String name;
    private final ClientLiteral literal;

    ClientLiteralType(ClientLiteral literal) {
        this.literal = literal;
        final XmlRootElement xmlRootElement = literal.getClass().getAnnotation(XmlRootElement.class);
        this.name = xmlRootElement.name();
    }

    @XmlValue
    public String getName() {
        return name;
    }

    public static ClientLiteralType fromString(String text) {
        for (ClientLiteralType t : values()) {
            if (t.getName().equalsIgnoreCase(text)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid literal type: " + text);
    }

    public <T extends ClientLiteral> T getLiteralInstance(){
        return (T) literal.deepClone();
    }
}
