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
package com.jaspersoft.jasperserver.api.engine.common.domain;
import com.jaspersoft.jasperserver.api.JSException;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class JdbcMetaConfigurationImpl implements JdbcMetaConfiguration {

    private Map/*<String,String>*/ jdbc2JavaTypeMapping;
    private Map<Integer, String> codeToJdbcTypeMapping;
    private List/*String*/ tableTypes;
    private String excludeTablesPattern;
    private String includeTablesPattern;
    private boolean includeSynonymsForOracle;
    private boolean useColumnLabelInDerivedTables;
    private String columnNameEscapingRegexp;
    private static final String OTHER_SQL_TYPE = "OTHER";
    private Map otherTypesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final Map<Integer, String> JDBC_TYPES_BY_CODE;
    static {
        Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            Field[] fields = Types.class.getFields();
            for (Field f : fields) {
                map.put((Integer)f.get(null), f.getName());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cannot access java.sql.Types !");
        }
        JDBC_TYPES_BY_CODE = Collections.unmodifiableMap(map);
    }

    /**
     * @param typeCode
     * @return - JDBC Type from the TypeCode
     */
    public String getJdbcTypeName(int typeCode) {
        // Look for user defined types
        String jdbcType = getCodeToJdbcTypeMapping().get(typeCode);
        if (jdbcType == null) {
            return JDBC_TYPES_BY_CODE.get(typeCode);
        }
        return jdbcType;
    }

    /**
     * @param vendorSqlType
     * @param typeCode
     * @return - JavaType
     * @throws SQLException
     */
    public String getJavaType(String vendorSqlType, int typeCode) throws SQLException {
        String sqlType = getJdbcTypeName(typeCode);
        return getJavaTypeForJdbcType(sqlType,vendorSqlType);
    }

    public Map getJdbc2JavaTypeMapping() {
        return Collections.unmodifiableMap(jdbc2JavaTypeMapping);
    }

    public void setJdbc2JavaTypeMapping(Map sql2JavaTypeMapping) {
        this.jdbc2JavaTypeMapping = sql2JavaTypeMapping;
        // validation
        for (Object o : sql2JavaTypeMapping.values()) {
            if (o instanceof String) {
                continue;
            }
            if (o instanceof Map) {
                Map m = (Map) o;
                for (Object o2 : m.values()) {
                    if (o2 instanceof String) {
                        continue;
                    }
                    throw new JSException("Invalid jdbc2JavaTypeMapping configuration!");
                }
                continue;
            }
            throw new JSException("Invalid jdbc2JavaTypeMapping configuration!");
        }
    }

    public Set<String> getSupportedJavaTypes() {
        Set<String> retSet = new HashSet<String>();
        for (Object o : jdbc2JavaTypeMapping.values()) {
            if (o instanceof String) {
                retSet.add((String) o);
                continue;
            }
            if (o instanceof Map) {
                Map m = (Map) o;
                for (Object o2 : m.values()) {
                    if (o2 instanceof String) {
                        retSet.add((String) o2);
                        continue;
                    }
                }
            }
        }
        return retSet;
    }

    public String getJavaTypeForJdbcType(String sqlType, String dbVendorType) {
        Object o = jdbc2JavaTypeMapping.get(sqlType);
        if (o == null) {
            o = jdbc2JavaTypeMapping.get(OTHER_SQL_TYPE);
        }
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof Map) {
            if(otherTypesMap.isEmpty()) {
                otherTypesMap.putAll((Map) o);
            }
            return (dbVendorType != null)?(String) otherTypesMap.get(dbVendorType):null;
        }
        return null;
    }

    public List getTableTypes() {
        return Collections.unmodifiableList(tableTypes);
    }

    public void setTableTypes(List tableTypes) {
        this.tableTypes = tableTypes;
    }

    public Map<Integer, String> getCodeToJdbcTypeMapping() {
        return codeToJdbcTypeMapping;
    }

    public void setCodeToJdbcTypeMapping(Map<Integer, String> codeToJdbcTypeMapping) {
        this.codeToJdbcTypeMapping = codeToJdbcTypeMapping;
    }

    public String getExcludeTablesPattern() {
        return excludeTablesPattern;
    }

    public void setExcludeTablesPattern(String excludeTablesPattern) {
        this.excludeTablesPattern = excludeTablesPattern;
    }

    public String getIncludeTablesPattern() {
        return includeTablesPattern;
    }

    public void setIncludeTablesPattern(String includeTablesPattern) {
        this.includeTablesPattern = includeTablesPattern;
    }

    public boolean isIncludeSynonymsForOracle() {
        return includeSynonymsForOracle;
    }

    public void setIncludeSynonymsForOracle(boolean includeSynonymsForOracle) {
        this.includeSynonymsForOracle = includeSynonymsForOracle;
    }

    public boolean isUseColumnLabelInDerivedTables() {
        return useColumnLabelInDerivedTables;
    }

    public void setUseColumnLabelInDerivedTables(boolean useColumnLabelInDerivedTables) {
        this.useColumnLabelInDerivedTables = useColumnLabelInDerivedTables;
    }

    public String getColumnNameEscapingRegexp() {
        return columnNameEscapingRegexp;
    }

    public void setColumnNameEscapingRegexp(String columnNameEscapingRegexp) {
        this.columnNameEscapingRegexp = columnNameEscapingRegexp;
    }
}
