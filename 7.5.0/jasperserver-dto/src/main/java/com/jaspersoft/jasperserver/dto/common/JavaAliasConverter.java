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

package com.jaspersoft.jasperserver.dto.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.*;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 13.02.2017
 */
public class JavaAliasConverter {
    public static Map<String, JavaAliasType> map = new HashMap<String, JavaAliasType>() {{
       put(BOOLEAN,     new JavaAliasType(BOOLEAN, Boolean.class));
       put(STRING,      new JavaAliasType(STRING, String.class));
       put(BYTE,        new JavaAliasType(BYTE, Byte.class));
       put(SHORT,       new JavaAliasType(SHORT, Short.class));
       put(INTEGER,     new JavaAliasType(INTEGER, Integer.class));
       put(LONG,        new JavaAliasType(LONG, Long.class));
       put(BIG_INTEGER, new JavaAliasType(BIG_INTEGER, BigInteger.class));
       put(FLOAT,       new JavaAliasType(FLOAT, Float.class));
       put(DOUBLE,      new JavaAliasType(DOUBLE, Double.class));
       put(BIG_DECIMAL, new JavaAliasType(BIG_DECIMAL, BigDecimal.class));
       put(DATE,        new JavaAliasType(DATE, java.util.Date.class, java.sql.Date.class));
       put(TIME,        new JavaAliasType(TIME, Time.class));
       put(TIMESTAMP,   new JavaAliasType(TIMESTAMP, Timestamp.class));
    }};

    public static String toAlias(String javaType) {
        for (Map.Entry<String, JavaAliasType> type : map.entrySet()) {
            if (type.getValue().getTypes().contains(javaType)) {
                return type.getKey();
            }
        }
        throw new IllegalArgumentException("Can not find alias for java type: " + javaType);
    }

    public static String toJavaType(String alias) {
        JavaAliasType result = map.get(alias);
        if (result != null) {
            return result.getDefaultJavaType().getName();
        }
        throw new IllegalArgumentException("Can not find java type for alias: " + alias);
    }

    private static class JavaAliasType {
        private String name;
        private Class defaultJavaType;
        private Set<String> types = new HashSet<String>();

        public JavaAliasType(String name, Class defaultJavaType, Class... possibleTypes) {
            this.name = name;
            this.defaultJavaType = defaultJavaType;

            types.add(defaultJavaType.getName());
            for (Class t : possibleTypes) {
                types.add(t.getName());
            }
        }

        public JavaAliasType(String name, Class defaultJavaType) {
            this.name = name;
            this.defaultJavaType = defaultJavaType;
            types.add(defaultJavaType.getName());
        }

        public String getName() {
            return name;
        }

        public Class getDefaultJavaType() {
            return defaultJavaType;
        }

        public Set<String> getTypes() {
            return types;
        }
    }
}
