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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id$
 */
@XmlRootElement(name = ClientString.EXPRESSION_ID)
public class ClientString extends ClientLiteral<String, ClientString> {

    public static final String EXPRESSION_ID = "string";

    public ClientString() {
    }

    public ClientString(String string) {
        super(string);
    }

    public ClientString(ClientString s) {
        super(s);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ClientString setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        String currentValue = getValue();
        if(currentValue != null){
            currentValue = currentValue.replaceAll("'", "''");
        }
        return "\'" + currentValue + "\'";
    }

    public static ClientString valueOf(Character character){
        return new ClientString().setValue(character.toString());
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }

    @Override
    public ClientString deepClone() {
        return new ClientString(this);
    }
}
