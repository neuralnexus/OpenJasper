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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryPatternsUtil {

    // non_space_name OR "quoted name"
    public static final String NAME_2GROUPS = "\\s*(?:([\\S]+)|(?:[\"]([^\"]+)[\"]))\\s*";

    public static final String BY_GROUP = "(?:\\s*(?i)BY (.+))?";
    // [name] AS [optional alias]
    public static final String NAME_OPT_ALIAS_4GROUPS = NAME_2GROUPS + "(?:(?i)AS" + NAME_2GROUPS + ")?";
    public static final Pattern NAME_OPT_ALIAS_4GROUPS_PATTERN = Pattern.compile(NAME_OPT_ALIAS_4GROUPS);
    // [name][ AS optional alias][ BY optional expression]
    public static final String NAME_OPT_ALIAS_OPT_EXPRESSION_5GROUPS = NAME_OPT_ALIAS_4GROUPS + BY_GROUP;
    public static final Pattern NAME_OPT_ALIAS_OPT_EXPRESSION_5GROUPS_PATTERN = Pattern.compile(NAME_OPT_ALIAS_OPT_EXPRESSION_5GROUPS);

    // order field [ASC|DESC]
    public static final String ORDER_4GROUPS = NAME_2GROUPS + BY_GROUP + "(?: ((?i)ASC|DESC)\\s*)";
    public static final Pattern ORDER_4GROUPS_PATTERN = Pattern.compile(ORDER_4GROUPS);

    public static final String ORDER_4GROUPS2 = NAME_2GROUPS + BY_GROUP;
    public static final Pattern ORDER_4GROUPS_PATTERN2 = Pattern.compile(ORDER_4GROUPS2);



    public static class NameAlias {
        public String name;
        public String alias;
    }

    public static class NameAliasExpression {

        public String name;
        public String alias;
        public String expression;
    }

    public static class NameExpressionOrder {
        public String name;
        public String expression;
        public String order;
    }

    public static NameAliasExpression parseNameAliasExpression(String string) {
        NameAliasExpression nameAliasExpression = new NameAliasExpression();
        Matcher matcher = NAME_OPT_ALIAS_OPT_EXPRESSION_5GROUPS_PATTERN.matcher(string);
        if (matcher.matches()) {
            nameAliasExpression.name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            String alias = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
            if (alias != null) {
                nameAliasExpression.alias = alias;
            }
            String expression = matcher.group(5);
            if (expression != null) {
                nameAliasExpression.expression = expression;
            }
        }
        return nameAliasExpression;
    }

    public static NameExpressionOrder parseNameExpressionOrder(String order) {
        NameExpressionOrder nameExpressionOrder = new NameExpressionOrder();
        Matcher matcher = ORDER_4GROUPS_PATTERN.matcher(order);
        if (matcher.matches()) {
            nameExpressionOrder.name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            String expression = matcher.group(3);
            if (expression != null) {
                nameExpressionOrder.expression = expression;
            }
            String trimOrder = order.trim().toUpperCase();
            if (trimOrder.endsWith("ASC")) nameExpressionOrder.order = "ASC";
            else if (trimOrder.endsWith("DESC")) nameExpressionOrder.order = "DESC";
        } else {
            matcher = ORDER_4GROUPS_PATTERN2.matcher(order);
            if (matcher.matches()) {
                nameExpressionOrder.name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                String expression = matcher.group(3);
                if (expression != null) {
                    nameExpressionOrder.expression = expression;
                }
                nameExpressionOrder.order = "ASC";
            }
        }
        return nameExpressionOrder;
    }

}
