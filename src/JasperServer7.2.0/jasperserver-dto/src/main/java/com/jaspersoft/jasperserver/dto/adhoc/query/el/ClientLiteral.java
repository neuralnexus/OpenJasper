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
package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.common.ValueHolder;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 */
    public abstract class ClientLiteral<T, F extends ClientLiteral<T, F>> implements ValueHolder<T,F>, ClientExpression<F> {

    protected T value;

    protected ClientLiteral() {

    }

    protected  ClientLiteral(T value) {
        this.value = value;
    }

    protected ClientLiteral(ClientLiteral<T, F> source){
        checkNotNull(source);

        value = source.getValue();
    }

    /**
     * Abstract method declaration is required here, because otherwise clientLiteral.deepClone() returns Object
     * @return
     */
    public abstract F deepClone();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientLiteral<?, ?> that = (ClientLiteral<?, ?>) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value == null ? ClientExpressions.MISSING_REPRESENTATION : value.toString();
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
    }
}
