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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.and;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.function;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.integer;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.not;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.or;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison.eq;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * <p/>
 * </p>
 *
 * @author Stas Chubar (schubar@tibco.com)
 * @version $Id$
 */
public class ClientElVisitorTest {

    @Test
    public void ensureVisitedAllPartsOfComparison() throws Exception {
        ClientExpression q = variable("v1").eq(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfComparison_Greater() throws Exception {
        ClientExpression q = variable("v1").gt(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfComparison_GreaterOrEqual() throws Exception {
        ClientExpression q = variable("v1").gtOrEq(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfComparison_Less() throws Exception {
        ClientExpression q = variable("v1").lt(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfComparison_LessOrEqual() throws Exception {
        ClientExpression q = variable("v1").ltOrEq(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfComparison_NotEqual() throws Exception {
        ClientExpression q = variable("v1").notEq(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfLogical() throws Exception {
        ClientExpression e1 = variable("v1").eq(1);
        ClientExpression e2 = variable("v2").startsWith("A");

        final List<String> visitedVariables = new ArrayList<String>(2);
        final List<String> visitedLiterals = new ArrayList<String>(2);

        final AtomicInteger totalVisited = new AtomicInteger(0);
        and(e1, e2).accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientFunction expression) {
                totalVisited.incrementAndGet();
                assertThat(expression.getFunctionName(), is("startsWith"));
            }

            @Override
            public void visit(ClientEquals expression) {
                totalVisited.incrementAndGet();
                assertEquals(ClientOperation.EQUALS, expression.getOperator());
            }

            @Override
            public void visit(ClientAnd expression) {
                totalVisited.incrementAndGet();
                assertEquals(ClientOperation.AND, expression.getOperator());
            }

            @Override
            public void visit(ClientVariable expression) {
                totalVisited.incrementAndGet();
                visitedVariables.add(expression.getName());
            }

            @Override
            public void visit(ClientLiteral expression) {
                totalVisited.incrementAndGet();
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedVariables, contains("v1", "v2"));
        assertThat(visitedLiterals, contains("1", "A"));

        assertThat(totalVisited.get(), is(7));

    }

    @Test
    public void ensureVisitedAllPartsOfLogicalOr() throws Exception {
        final ClientExpression v1 = variable("v1");
        final ClientExpression v2 = variable("v2");

        final AtomicInteger totalVisited = new AtomicInteger(0);

        or(v1, v2).accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientOr or) {
                totalVisited.incrementAndGet();
                assertEquals(ClientOperation.OR, or.getOperator());
                assertTrue(or.getOperands().containsAll(asList(v1, v2)));
            }
        });

        assertThat(totalVisited.get(), is(1));
    }

    @Test
    public void ensureVisitedAllPartsOfLogicalNot() throws Exception {
        ClientExpression v1 = variable("v1");
        ClientExpression v2 = variable("v2");

        final ClientExpression eq = eq(v1, v2);

        final AtomicInteger totalVisited = new AtomicInteger(0);

        not(eq).accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientNot not) {
                totalVisited.incrementAndGet();
                assertEquals(ClientOperation.NOT, not.getOperator());
                assertTrue(not.getOperands().contains(eq));
            }
        });

        assertThat(totalVisited.get(), is(1));
    }

    @Test
    public void ensureVisitedAllPartsOfAddition() throws Exception {
        ClientExpression e1 = new ClientAdd().setOperands(asList((ClientExpression) literal(1), literal(2)));

        final List<String> visitedLiterals = new ArrayList<String>(2);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientAdd expression) {
                assertThat(expression, is(instanceOf(ClientAdd.class)));
                assertThat(visitedLiterals, contains("1", "2"));
            }
            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertTrue(visitedLiterals.containsAll(asList("1", "2")));
    }

    @Test
    public void ensureVisitedAllPartsOfDividing() throws Exception {
        ClientExpression e1 = new ClientDivide().setOperands(asList((ClientExpression) integer(1), integer(2)));

        final List<String> visitedLiterals = new ArrayList<String>(2);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientDivide expression) {
                assertThat(expression, is(instanceOf(ClientDivide.class)));
                assertThat(visitedLiterals, contains("1", "2"));
            }
            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1", "2"));
    }

    @Test
    public void ensureVisitedAllPartsOfMultiplying() throws Exception {
        ClientExpression e1 = new ClientMultiply().setOperands(asList((ClientExpression) literal(1), literal(2)));

        final List<String> visitedLiterals = new ArrayList<String>(2);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientMultiply expression) {
                assertThat(expression, is(instanceOf(ClientMultiply.class)));
                assertThat(visitedLiterals, contains("1", "2"));
            }
            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1", "2"));
    }

    @Test
    public void ensureVisitedAllPartsOfPercentRadio() throws Exception {
        ClientExpression e1 = new ClientPercentRatio(asList(literal(1), literal(2)));

        final List<String> visitedLiterals = new ArrayList<String>(2);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientPercentRatio expression) {
                assertThat(expression, is(instanceOf(ClientPercentRatio.class)));
                assertThat(visitedLiterals, contains("1", "2"));
            }
            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1", "2"));
    }

    @Test
    public void ensureVisitedAllPartsOfSubstracting() throws Exception {
        ClientExpression e1 = new ClientSubtract().setOperands(asList((ClientExpression) literal(1), literal(2)));

        final List<String> visitedLiterals = new ArrayList<String>(2);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientSubtract expression) {
                assertThat(expression, is(instanceOf(ClientSubtract.class)));
                assertThat(visitedLiterals, contains("1", "2"));
            }
            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1", "2"));
    }

    @Test
    public void ensureVisitedAllPartsOfFunction() throws Exception {
        ClientExpression e1 = function("do", literal(1), variable("v1"));

        final List<String> visitedVariables = new ArrayList<String>(1);
        final List<String> visitedLiterals = new ArrayList<String>(1);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                visitedVariables.add(expression.getName());
            }

            @Override
            public void visit(ClientFunction expression) {
                assertThat(expression, is(notNullValue()));
                assertThat(expression.getFunctionName(), is("do"));

                assertThat(visitedLiterals, contains("1"));
                assertThat(visitedVariables, contains("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1"));
        assertThat(visitedVariables, contains("v1"));

    }
}