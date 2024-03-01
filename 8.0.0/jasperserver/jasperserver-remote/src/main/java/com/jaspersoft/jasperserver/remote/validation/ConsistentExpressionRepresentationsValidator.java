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
package com.jaspersoft.jasperserver.remote.validation;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
import com.jaspersoft.jasperserver.dto.bridge.BridgeRegistry;
import com.jaspersoft.jasperserver.dto.bridge.ExpressionParsingBridge;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
public class ConsistentExpressionRepresentationsValidator implements ClientValidator<ClientExpressionContainer> {

    @Override
    public List<Exception> validate(ExecutionContext ctx, ClientExpressionContainer value) {
        List<Exception> exceptions  = new ArrayList<Exception>();
        ClientExpression expressionObject = null;
        String expressionString = null;
        if (value != null) {
            expressionObject = value.getObject();
            expressionString = value.getString();
        }
        final boolean nothing = expressionObject == null && expressionString == null;
        final boolean objectOnly = expressionObject != null && expressionString == null;
        final boolean stringOnly = expressionObject == null && expressionString != null;
        boolean bothConsistent = expressionObject != null && expressionString != null
                && expressionObject.toString().equals(expressionString);
        if (expressionObject != null && expressionString != null && !bothConsistent) {
            final ExpressionParsingBridge expressionParsingBridge = BridgeRegistry.getBridge(ExpressionParsingBridge.class);
            if (expressionParsingBridge == null) {
                // string representations don't match and parsing bridge isn't registered. We can't parse expression
                // string and check if object model is equal. As far as same object model could have multiple string
                // representations we can't say for sure, that expression object and expression string are not
                // consistent by string representation only. So, le's return true to avoid possible validation failure
                bothConsistent = true;
            } else {
                ClientExpression anotherExpressionObject = null;
                final Set<String> variables = new HashSet<String>();
                // collect variables names
                expressionObject.accept(new ClientELVisitorAdapter() {
                    @Override
                    public void visit(ClientVariable expression) {
                        variables.add(expression.getName());
                    }
                });
                try {
                    // string representations don't match and expression parsing bridge is available. Now we can get another
                    // expression object out of given expression string.
                    // Let's parse string to object model and check if object.
                    anotherExpressionObject = expressionParsingBridge.parseExpression(expressionString, variables);
                } catch (Exception e) {
                    // unable to parse expression string. Let anotherExpressionObject be null then.
                }
                bothConsistent = expressionObject.equals(anotherExpressionObject);
            }
        }
        if (! (nothing || objectOnly || stringOnly || bothConsistent)) {
            exceptions.add(new IllegalParameterValueException(new ErrorDescriptor()
                    .setErrorCode("domel.expressions.not.consistent")
                    .setMessage("Domel expression is not consistent")
                    .addProperties(new ClientProperty("domelExpression", value.getExpression()))));
        }
        return exceptions;
    }

}
