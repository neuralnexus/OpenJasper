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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 */
public enum ClientOperation {
    EQUALS(ClientEquals.EXPRESSION_ID, "==", 2),
    NOT_EQUAL(ClientNotEqual.EXPRESSION_ID, "!=", 2),
    GREATER_OR_EQUAL(ClientGreaterOrEqual.EXPRESSION_ID, ">=", 2),
    LESS_OR_EQUAL(ClientLessOrEqual.EXPRESSION_ID, "<=", 2),
    GREATER(ClientGreater.EXPRESSION_ID, ">", 2),
    LESS(ClientLess.EXPRESSION_ID, "<", 2),
    IN(ClientIn.EXPRESSION_ID, "in", 2),
    NOT(ClientNot.EXPRESSION_ID, "not", 3),
    AND(ClientAnd.EXPRESSION_ID, "and", 1),
    OR(ClientOr.EXPRESSION_ID, "or", 0),
    ADD(ClientAdd.EXPRESSION_ID, "+", 4),
    SUBTRACT(ClientSubtract.EXPRESSION_ID, "-", 4),
    DIVIDE(ClientDivide.EXPRESSION_ID, "/", 5),
    MULTIPLY(ClientMultiply.EXPRESSION_ID, "*", 5),
    FUNCTION(ClientFunction.EXPRESSION_ID, "", 6),
    PERCENT_FIELD_RATIO(ClientPercentRatio.EXPRESSION_ID, "%", 5),
    UNDEFINED("undefined", "$missing$", 6);

    private final String name;
    private final String domelOperator;
    //TODO ykovalch use this value for parentheses handling
    private final int priority;

    ClientOperation(String name, String domelOperator, int priority) {
        this.name = name;
        this.domelOperator = domelOperator;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getDomelOperator() {
        return domelOperator;
    }

    public static ClientOperation fromString(String text) {
        for (ClientOperation o : values()) {
            if (o.getName().equals(text)) {
                return o;
            }
        }

        return null;
    }

    public static ClientOperation fromDomElOperator(String operator) {
        for (ClientOperation o : values()) {
            if (o.getName().equals(operator)) {
                return o;
            }
        }

        return null;
    }

    public static boolean isSupported(String text) {
        ClientOperation co = ClientOperation.fromString(text);
        return co != null;
    }
}
