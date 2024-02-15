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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.range;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement
public class ClientRangeBoundary implements ClientExpression<ClientRangeBoundary>, DeepCloneable<ClientRangeBoundary> {

    private ClientExpression boundary;

    public ClientRangeBoundary() {
    }

    public ClientRangeBoundary(ClientLiteral literal) {
        this.boundary = literal.deepClone();
    }

    public ClientRangeBoundary(ClientExpression literal) {
        this.boundary = (ClientExpression) literal.deepClone();
    }

    public ClientRangeBoundary(ClientRangeBoundary boundary) {
        checkNotNull(boundary);

        this.boundary = copyOf(boundary.getBoundary());
    }

    @XmlElements({
            @XmlElement(name = ClientNumber.EXPRESSION_ID,
                    type = ClientNumber.class),
            @XmlElement(name = ClientString.EXPRESSION_ID,
                    type = ClientString.class),
            @XmlElement(name = ClientBoolean.EXPRESSION_ID,
                    type = ClientBoolean.class),
            @XmlElement(name = ClientDate.EXPRESSION_ID,
                    type = ClientDate.class),
            @XmlElement(name = ClientTimestamp.EXPRESSION_ID,
                    type = ClientTimestamp.class),
            @XmlElement(name = ClientTime.EXPRESSION_ID,
                    type = ClientTime.class),
            @XmlElement(name = ClientVariable.EXPRESSION_ID,
                    type = ClientVariable.class),
            @XmlElement(name = ClientFunction.EXPRESSION_ID,
                    type = ClientFunction.class)
    })
    public ClientExpression getBoundary() {
        return boundary;
    }
    public ClientRangeBoundary setBoundary(ClientExpression bound) {
        this.boundary = bound;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientRangeBoundary that = (ClientRangeBoundary) o;

        return boundary != null ? boundary.equals(that.boundary) : that.boundary == null;
    }

    @Override
    public int hashCode() {
        return boundary != null ? boundary.hashCode() : 0;
    }

    @Override
    public ClientRangeBoundary deepClone() {
        return new ClientRangeBoundary(this);
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (boundary != null) {
            boundary.accept(visitor);
        }
    }

    @Override
    public String toString() {
        return "ClientRangeBoundary{" +
                "boundary=" + boundary +
                '}';
    }
}
