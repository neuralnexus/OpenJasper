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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import org.junit.Test;

import java.math.BigDecimal;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.function;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.range;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 *
 * @version $Id$
 */
public class InOperatorELSerializationToJSONTest extends FilterTest {

    @Test
    public void ensureWhereInRange_integer() throws Exception {
        ClientIn in = variable("sales").in(range(1, 20));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"range\" : {\n" +
                "            \"start\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"number\" : {\n" +
                "                  \"value\" : \"1\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"end\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"number\" : {\n" +
                "                  \"value\" : \"20\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

    @Test
    public void ensureWhereInRange_variable() throws Exception {
        ClientIn in = variable("sales").in(range(variable("a"), variable("b")));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"range\" : {\n" +
                "            \"start\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"variable\" : {\n" +
                "                  \"name\" : \"a\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"end\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"variable\" : {\n" +
                "                  \"name\" : \"b\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

    @Test
    public void ensureWhereInRange_attributeFunction() throws Exception {
        ClientIn in = variable("sales").in(range(function("attribute", literal("birth_year")), function("attribute", literal("start_year"))));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"range\" : {\n" +
                "            \"start\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"function\" : {\n" +
                "                  \"operands\" : [ {\n" +
                "                    \"string\" : {\n" +
                "                      \"value\" : \"birth_year\"\n" +
                "                    }\n" +
                "                  } ],\n" +
                "                  \"functionName\" : \"attribute\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"end\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"function\" : {\n" +
                "                  \"operands\" : [ {\n" +
                "                    \"string\" : {\n" +
                "                      \"value\" : \"start_year\"\n" +
                "                    }\n" +
                "                  } ],\n" +
                "                  \"functionName\" : \"attribute\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }


    @Test
    public void ensureWhereInRange_string() throws Exception {
        ClientIn in = variable("user_name").in(range("a", "z"));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"user_name\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"range\" : {\n" +
                "            \"start\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"string\" : {\n" +
                "                  \"value\" : \"a\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"end\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"string\" : {\n" +
                "                  \"value\" : \"z\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

    @Test
    public void ensureWhereInRange_double() throws Exception {
        ClientIn in = variable("pH").in(range(0.0d, 14.0d));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"pH\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"range\" : {\n" +
                "            \"start\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"number\" : {\n" +
                "                  \"value\" : \"0.0\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"end\" : {\n" +
                "              \"boundary\" : {\n" +
                "                \"number\" : {\n" +
                "                  \"value\" : \"14.0\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

    /**
     * @see <a href="http://jira.jaspersoft.com/browse/JRS-8014">JRS-8014</a>
     */
    @Test
    public void ensureWhereInList_ofIntegers() throws Exception {
        ClientIn in = variable("sales").in(literal(1), literal(2), literal(3));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"list\" : {\n" +
                "            \"items\" : [ {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"1\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"2\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"3\"\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

    @Test
    public void ensureWhereInList_ofStrings() throws Exception {
        ClientIn in = variable("sales").in(literal("ok"), literal("not-ok"), literal("don't care"));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"list\" : {\n" +
                "            \"items\" : [ {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"ok\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"not-ok\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"don't care\"\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

    @Test
    public void ensureWhereInList_ofDecimals() throws Exception {
        ClientIn in = variable("sales").in(new ClientNumber(new BigDecimal("5.55")), new ClientNumber(new BigDecimal("6.66")),
                new ClientNumber(new BigDecimal("7.77")));
        ClientWhere w = new ClientWhere(in);

        String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"list\" : {\n" +
                "            \"items\" : [ {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"5.55\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"6.66\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"number\" : {\n" +
                "                \"value\" : \"7.77\"\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertThat(json(w), is(jsonString));
    }

}