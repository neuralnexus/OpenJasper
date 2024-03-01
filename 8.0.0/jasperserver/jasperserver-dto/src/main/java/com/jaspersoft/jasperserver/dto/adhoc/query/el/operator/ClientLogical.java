/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 */
public abstract class ClientLogical<F extends ClientLogical<F>> extends ClientOperator<F> {

    public ClientLogical(){
        super();
    }

    public ClientLogical(ClientOperation type) {
        super(type);
    }

    public ClientLogical(ClientLogical source) {
        super(source);
    }

    public ClientLogical(ClientOperation operation, List<? extends ClientExpression> operands) {
        super(operation, operands);
    }

    protected ClientLogical(ClientOperation operation, List<ClientExpression> operands, Boolean paren) {
        super(operation, operands, paren);
    }

    public static <T extends ClientLogical> T createLogical(ClientOperation operator, List<ClientExpression> operands) {
        return (T) createLogical(operator.getName(), operands);
    }

    public static <T extends ClientLogical> T createLogical(String name, List<ClientExpression> operands) {
        ClientOperation operation = ClientOperation.fromString(name);
        if (operation != null) {
            if (operation.equals(ClientOperation.AND)) {
                return (T) new ClientAnd().setOperands(operands);
            } else if (operation.equals(ClientOperation.OR)) {
                return (T) new ClientOr().setOperands(operands);
            }
            return null;
        }
        return null;
    }

    public static <T extends ClientLogical> T createLogical(String name, List<ClientExpression> operands, boolean isParen) {
        ClientLogical result = createLogical(name, operands);
        if (isParen && result != null) {
            result.setParen(true);
        }
        return (T)result;

    }

    public static ClientAnd and(ClientExpression lhs, ClientExpression rhs) {
        checkArguments(lhs, rhs);
        return new ClientAnd().setOperands(asList(lhs, rhs));
    }

    public static ClientOr or(ClientExpression lhs, ClientExpression rhs) {
        checkArguments(lhs, rhs);
        return new ClientOr().setOperands(asList(lhs, rhs));
    }

    static void checkArguments(ClientExpression ... expressions) throws IllegalArgumentException {
        if (expressions.length < 2) {
            throw new IllegalArgumentException("Error creating 'and/or' logical expression. Expression is not defined!");
        }
    }

    public ClientOr or(ClientExpression expression) {
        return or(this, expression);
    }

    public ClientAnd and(ClientExpression expression) {
        return and(this, expression);
    }

    @Override
    public F setOperands(List<ClientExpression> operands) {
        if (operands != null && operands.size() > 2) {
            throw new UnsupportedOperationException("Failed to create ClientLogical with more than 2 operands");
        }
        super.setOperands(operands);
        return (F) this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean hasOperands = !operands.isEmpty();
        String lhsString = hasOperands && operands.get(0) != null ? operands.get(0).toString() :
                ClientExpressions.MISSING_REPRESENTATION;
        String rhsString = hasOperands && operands.size() > 1 && operands.get(1) !=  null ? operands.get(1).toString() :
                ClientExpressions.MISSING_REPRESENTATION;
        if (hasParen()) {
            sb.append("(");
        }

        sb
                .append(lhsString)
                .append(" ")
                .append(getOperator().getDomelOperator())
                .append(" ")
                .append(rhsString);

        if (hasParen()) {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if(operands != null && !operands.isEmpty()) {
            if (operands.get(0) != null) {
                operands.get(0).accept(visitor);
            }
            if (operands.size() > 1 && operands.get(1) != null) {
                operands.get(1).accept(visitor);
            }
        }
    }
}
