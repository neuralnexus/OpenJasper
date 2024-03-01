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

package com.jaspersoft.jasperserver.dto.query;

import com.jaspersoft.jasperserver.dto.adhoc.query.QueryPatternsUtil;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryPatternsUtilTest {

    public static final String ClientQueryFieldStr =
            "ClientQueryField{id='$ID', type='null', measure=false, field='$FIELD', expression='$EXP'}";
    public static final String ClientQueryGroupStr =
            "ClientQueryGroup{categorizer='null', id='$ID', type='null', fieldName='$FIELD', expression='$EXP', includeAll=null}";
    public static final String ClientQueryAggregatedFieldStr =
            "ClientQueryAggregatedField{id='$ID', aggregateFunction='null', aggregateFirstLevelFunction='null', expression=$EXP, aggregateType='null', aggregateArg='null', name='$NAME'}";
    public static final String ClientGenericOrderStr =
            "ClientGenericOrder{isAscending=$ASC, isAggregationLevel=false, fieldReference='$FIELD', expression='$EXP'}";

    public static final String ExpressionContainerStr = "ClientExpressionContainer{object=null, string='$EXP'}";

    @Test
    public void testDetailFieldPatterns() {

        // names
        ClientQueryField field1 = new ClientQueryField("field1");
        assertEquals(ClientQueryFieldStr.replace("$ID", "null").replace("$FIELD", "field1")
                .replace("'$EXP'", "null"), field1.toString());

        // name and alias
        ClientQueryField field2 = new ClientQueryField("field2 AS F2");
        assertEquals(ClientQueryFieldStr.replace("$ID", "F2").replace("$FIELD", "field2")
                .replace("'$EXP'", "null"), field2.toString());

        // name, alias and expression
        ClientQueryField field3 = new ClientQueryField("field2 AS F2 BY Sum(F4)");
        assertEquals(ClientQueryFieldStr.replace("$ID", "F2").replace("$FIELD", "field2")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), field3.toString());


        ClientQueryField field4 = new ClientQueryField("field2 BY Sum(F4)");
        assertEquals(ClientQueryFieldStr.replace("$ID", "null").replace("$FIELD", "field2")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), field4.toString());

    }

    @Test
    public void testAggregatedFieldPatterns() {

        ClientQueryAggregatedField aggregatedField1 = new ClientQueryAggregatedField("aggField1");
        assertEquals(ClientQueryAggregatedFieldStr.replace("$ID", "null").replace("$NAME", "aggField1")
                .replace("$EXP", "null"), aggregatedField1.toString());

        ClientQueryAggregatedField aggregatedField2 = new ClientQueryAggregatedField("aggField2 AS AF2");
        assertEquals(ClientQueryAggregatedFieldStr.replace("$ID", "AF2").replace("$NAME", "aggField2")
                .replace("$EXP", "null"), aggregatedField2.toString());

        // name, alias, expression
        ClientQueryAggregatedField aggregatedField3 = new ClientQueryAggregatedField("aggField3 AS AF3 BY Sum(AF3)");
        assertEquals(ClientQueryAggregatedFieldStr.replace("$ID", "AF3").replace("$NAME", "aggField3")
                .replace("$EXP", ExpressionContainerStr.replace("$EXP", "Sum(AF3)")), aggregatedField3.toString());

        // name and expression
        ClientQueryAggregatedField aggregatedField4 = new ClientQueryAggregatedField("aggField4 By Average(AF4)");
        assertEquals(ClientQueryAggregatedFieldStr.replace("$ID", "null").replace("$NAME", "aggField4")
                .replace("$EXP", ExpressionContainerStr.replace("$EXP", "Average(AF4)")), aggregatedField4.toString());

        ClientQueryAggregatedField aggregatedField5 = new ClientQueryAggregatedField("\"aggField 5\" AS \"AF 5\"");
        assertEquals(ClientQueryAggregatedFieldStr.replace("$ID", "AF 5").replace("$NAME", "aggField 5")
                .replace("$EXP", "null"), aggregatedField5.toString());
    }

    @Test
    public void testGroupByFieldPatterns() {

        ClientQueryGroup group1 = new ClientQueryGroup("group1");
        assertEquals(ClientQueryGroupStr.replace("$ID", "null").replace("$FIELD", "group1")
                .replace("'$EXP'", "null"), group1.toString());

        ClientQueryGroup group2 = new ClientQueryGroup("group2 AS G2");
        assertEquals(ClientQueryGroupStr.replace("$ID", "G2").replace("$FIELD", "group2")
                .replace("'$EXP'", "null"), group2.toString());

        ClientQueryGroup group3 = new ClientQueryGroup("field2 AS F2 BY Sum(F4)");
        assertEquals(ClientQueryGroupStr.replace("$ID", "F2").replace("$FIELD", "field2")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), group3.toString());

        ClientQueryGroup group4 = new ClientQueryGroup("field2 AS F2 BY Sum(F4)");
        assertEquals(ClientQueryGroupStr.replace("$ID", "F2").replace("$FIELD", "field2")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), group4.toString());

        ClientQueryGroup group5 = new ClientQueryGroup("\"group 5\" AS \"G 5\"");
        assertEquals(ClientQueryGroupStr.replace("$ID", "G 5").replace("$FIELD", "group 5").replace("'$EXP'", "null"), group5.toString());

    }

    @Test
    public void testOrderByFieldPatterns() {
        // order
        ClientGenericOrder order1 = new ClientGenericOrder("order1");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "true")
                .replace("$FIELD", "order1")
                .replace("'$EXP'", "null"), order1.toString());
        ClientGenericOrder order2 = new ClientGenericOrder("order2 ASC");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "true")
                .replace("$FIELD", "order2")
                .replace("'$EXP'", "null"), order2.toString());
        ClientGenericOrder order3 = new ClientGenericOrder("order3 DESC");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "false")
                .replace("$FIELD", "order3")
                .replace("'$EXP'", "null"), order3.toString());

        ClientGenericOrder order4 = new ClientGenericOrder("order1 BY Sum(F4)");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "true")
                .replace("$FIELD", "order1")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), order4.toString());

        ClientGenericOrder order5 = new ClientGenericOrder("order2 BY Sum(F4) ASC");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "true")
                .replace("$FIELD", "order2")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), order5.toString());

        ClientGenericOrder order6 = new ClientGenericOrder("order3 BY Sum(F4) DESC");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "false")
                .replace("$FIELD", "order3")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), order6.toString());

    }

    @Test
    public void testOrderByFieldPatterns_witDifferentCase() {
        ClientGenericOrder order6 = new ClientGenericOrder("order3 By Sum(F4) asc");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "true")
                .replace("$FIELD", "order3")
                .replace("'$EXP'", ExpressionContainerStr.replace("$EXP", "Sum(F4)")), order6.toString());
    }

    @Test
    public void testNameAndAliasWithExtraSpace() {
        // name and alias with spaces
        ClientQueryField field5 = new ClientQueryField(" \"field 5\"  AS      \"F 5\" ");
        assertEquals(ClientQueryFieldStr.replace("$ID", "F 5").replace("$FIELD", "field 5")
                .replace("'$EXP'", "null"), field5.toString());
    }

    @Test
    public void testFieldNameWithExtraSpace() {
        // name and alias with spaces
        ClientQueryField field5 = new ClientQueryField("     field ");
        assertEquals(ClientQueryFieldStr.replace("$ID", "null").replace("$FIELD", "field")
                .replace("'$EXP'", "null"), field5.toString());
    }

    @Test
    public void testCaseInAliasMarker() {
        // name and alias with spaces
        ClientQueryField field5 = new ClientQueryField("\"field 5\"  as      \"F 5\"");
        assertEquals(ClientQueryFieldStr.replace("$ID", "F 5").replace("$FIELD", "field 5")
                .replace("'$EXP'", "null"), field5.toString());
    }

    @Test
    public void testNameAndAliasWithJapaneseCharacters() {
        // name and alias in Japanese
        ClientQueryField field6 = new ClientQueryField("和製漢字 AS 和製漢字");
        assertEquals(ClientQueryFieldStr.replace("$ID", "和製漢字").replace("$FIELD", "和製漢字")
                .replace("'$EXP'", "null"), field6.toString());
    }

    @Test
    public void testGroupNameAndAliasWithJapaneseCharacters() {
        // name and alias in Japanese
        ClientQueryGroup group2 = new ClientQueryGroup("和製漢字 AS 和製漢字");
        assertEquals(ClientQueryGroupStr.replace("$ID", "和製漢字").replace("$FIELD", "和製漢字")
                .replace("'$EXP'", "null"), group2.toString());
    }


    @Test
    public void testOrderbyNameAndAliasWithJapaneseCharacters() {
        // name and alias in Japanese
        ClientGenericOrder order2 = new ClientGenericOrder("和製漢字 ASC   ");
        assertEquals(ClientGenericOrderStr.replace("$ASC", "true")
                .replace("$FIELD", "和製漢字")
                .replace("'$EXP'", "null"), order2.toString());
    }

    @Test
    public void parseNameAlias_test() {
        Exception ex = null;
            try {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression(null);
        } catch (Exception ex2) {
            ex = ex2;
        }
        Assert.assertTrue(ex != null);
    }

    @Test
    public void parseNameAlias_test2() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("ABC");
        Assert.assertTrue(nameAliasExpression.name.equals("ABC"));
    }

    @Test
    public void parseNameAlias_test3() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("ABC BY");
        Assert.assertTrue(nameAliasExpression.name == null);
        Assert.assertTrue(nameAliasExpression.alias == null);
        Assert.assertTrue(nameAliasExpression.expression == null);
    }

    @Test
    public void parseNameAlias_test4() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("\"ABC\"");
        Assert.assertTrue(nameAliasExpression.name.equals("\"ABC\""));
        Assert.assertTrue(nameAliasExpression.alias == null);
        Assert.assertTrue(nameAliasExpression.expression == null);
    }

    @Test
    public void parseNameAlias_test5() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("ABC BY EXP");
        Assert.assertTrue(nameAliasExpression.name.equals("ABC"));
        Assert.assertTrue(nameAliasExpression.alias == null);
        Assert.assertTrue(nameAliasExpression.expression.equals("EXP"));
    }

    @Test
    public void parseNameAlias_test6() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("ABC AS");
        Assert.assertTrue(nameAliasExpression.name == null);
        Assert.assertTrue(nameAliasExpression.alias == null);
        Assert.assertTrue(nameAliasExpression.expression == null);
    }

    @Test
    public void parseNameAlias_test7() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("AS BY");
        Assert.assertTrue(nameAliasExpression.name == null);
        Assert.assertTrue(nameAliasExpression.alias == null);
        Assert.assertTrue(nameAliasExpression.expression == null);
    }

    @Test
    public void parseNameAlias_test8() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("ABC AS LABEL");
        Assert.assertTrue(nameAliasExpression.name.equals("ABC"));
        Assert.assertTrue(nameAliasExpression.alias.contains("LABEL"));
        Assert.assertTrue(nameAliasExpression.expression == null);
    }

    @Test
    public void parseNameAlias_test9() {
        QueryPatternsUtil.NameAliasExpression nameAliasExpression = QueryPatternsUtil.parseNameAliasExpression("ABC as LABEL by EXP");
        Assert.assertTrue(nameAliasExpression.name.equals("ABC"));
        Assert.assertTrue(nameAliasExpression.alias.contains("LABEL"));
        Assert.assertTrue(nameAliasExpression.expression.equals("EXP"));
    }

    @Test
    public void parseNameOrder_test() {
        Exception ex = null;
        try {
            QueryPatternsUtil.NameExpressionOrder nameAliasExpression = QueryPatternsUtil.parseNameExpressionOrder(null);
        } catch (Exception ex2) {
            ex = ex2;
        }
        Assert.assertTrue(ex != null);
    }

    @Test
    public void parseNameOrder_test2() {
        Exception ex = null;
        try {
            QueryPatternsUtil.NameExpressionOrder nameAliasExpression = QueryPatternsUtil.parseNameExpressionOrder("ABC ASC");
        } catch (Exception ex2) {
            ex = ex2;
        }
        Assert.assertTrue(ex == null);
    }

    @Test
    public void parseNameOrder_test3() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC ASCD");
        Assert.assertTrue(nameExpressionOrder.name == null);
        Assert.assertTrue(nameExpressionOrder.expression == null);
        Assert.assertTrue(nameExpressionOrder.order == null);
    }

    @Test
    public void parseNameOrder_test4() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC BY DESC");
        Assert.assertTrue(nameExpressionOrder.name.equals("ABC"));
        Assert.assertTrue(nameExpressionOrder.expression.equals("DESC"));
        Assert.assertTrue(nameExpressionOrder.order.equals("ASC"));
    }

    @Test
    public void parseNameOrder_test5() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC BY ASC");
        Assert.assertTrue(nameExpressionOrder.name.equals("ABC"));
        Assert.assertTrue(nameExpressionOrder.expression.equals("ASC"));
        Assert.assertTrue(nameExpressionOrder.order.equals("ASC"));
    }

    @Test
    public void parseNameOrder_test6() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC BY");
        Assert.assertTrue(nameExpressionOrder.name == null);
        Assert.assertTrue(nameExpressionOrder.expression == null);
        Assert.assertTrue(nameExpressionOrder.order == null);
    }

    @Test
    public void parseNameOrder_test7() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC BY EXP ASCD");
        Assert.assertTrue(nameExpressionOrder.name.equals("ABC"));
        Assert.assertTrue(nameExpressionOrder.expression.equals("EXP ASCD"));
        Assert.assertTrue(nameExpressionOrder.order.equals("ASC"));
    }

    @Test
    public void parseNameOrder_test8() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("BY EXP ASC");
        Assert.assertTrue(nameExpressionOrder.name == null);
        Assert.assertTrue(nameExpressionOrder.expression == null);
        Assert.assertTrue(nameExpressionOrder.order == null);
    }
    @Test
    public void parseNameOrder_test9() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC BY DESC DESC");
        Assert.assertTrue(nameExpressionOrder.name.equals("ABC"));
        Assert.assertTrue(nameExpressionOrder.expression.equals("DESC"));
        Assert.assertTrue(nameExpressionOrder.order.equals("DESC"));
    }

    @Test
    public void parseNameOrder_test10() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC BY ASC DESC");
        Assert.assertTrue(nameExpressionOrder.name.equals("ABC"));
        Assert.assertTrue(nameExpressionOrder.expression.equals("ASC"));
        Assert.assertTrue(nameExpressionOrder.order.equals("DESC"));
    }

    @Test
    public void parseNameOrder_test11() {
        QueryPatternsUtil.NameExpressionOrder nameExpressionOrder = QueryPatternsUtil.parseNameExpressionOrder("ABC AS ASC DESC2");
        Assert.assertTrue(nameExpressionOrder.name == null);
        Assert.assertTrue(nameExpressionOrder.expression == null);
        Assert.assertTrue(nameExpressionOrder.order == null);
    }
}
