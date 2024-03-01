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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JdbcMetaConfiguration {
    /**
     * @return - defined type code to jdbc type map.
     */
    public Map<Integer, String> getCodeToJdbcTypeMapping();

    /**
     *
     * @param sqlType
     * @param dbVendorType
     * @return - Java Type
     */
    public String getJavaTypeForJdbcType(String sqlType, String dbVendorType);

    /**
     *
     * @param vendorSqlType
     * @param typeCode
     * @return - JDBC Type from the TypeCode
     * @throws SQLException
     */
    public String getJavaType(String vendorSqlType, int typeCode) throws SQLException;

    /**
     *
     * @param typeCode
     * @return - JavaType
     */
    public String getJdbcTypeName(int typeCode);

    /**
     * check if you use Oracle synonyms in Domains
     * @return
     */
    public boolean isIncludeSynonymsForOracle();

    /**
     * check to see if ColumnLabel or ColumnName to be used for column references
     * @return
     */
    public boolean isUseColumnLabelInDerivedTables();

    /**
     *
     * @return - escaping pattern for Column name
     */
    public String getColumnNameEscapingRegexp();

    /**
     *
     * @return - List of Table Types like TABLE, VIEW, So on
     */
    public List getTableTypes();

    /**
     *
     * @return - pattern regex
     */
    public String getExcludeTablesPattern();

    /**
     *
     * @return - pattern regex
     */
    public String getIncludeTablesPattern();

    /**
     *
     * @return - Map of JDBC Type -> Java Type
     */
    public Map getJdbc2JavaTypeMapping();

    /**
     *
     * @return - all supported Java types
     */
    public Set<String> getSupportedJavaTypes();
}
