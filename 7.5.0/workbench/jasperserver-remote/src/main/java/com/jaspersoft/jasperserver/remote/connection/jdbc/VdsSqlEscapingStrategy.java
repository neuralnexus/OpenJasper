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

package com.jaspersoft.jasperserver.remote.connection.jdbc;

import com.jaspersoft.jasperserver.api.metadata.jdbc.SqlAndMetadataEscapingStrategy;

import java.sql.DatabaseMetaData;

/**
 * @author serhii.blazhyievskyi
 * @version $Id: GenericSqlEscapingStrategy.java 61656 2016-12-27 13:55:07Z ykovalch $
 */
public class VdsSqlEscapingStrategy implements SqlAndMetadataEscapingStrategy {
    @Override
    public String sqlEscape(DatabaseMetaData databaseMetaData, String sql) {
        /*
         *  name is going to be used in LIKE condition, need to escape wildcard chars
         *  Bug 31086 - Domains: Moving all VDS tables cause error: java.lang.IllegalArgumentException: fromKey > toKey
         *  name is going to be used in LIKE condition, need to escape wildcard chars
         */
        return sql.replace("\\","\\\\").replace("_", "\\_");
    }

    @Override
    public String schemaNameEscape(DatabaseMetaData databaseMetaData, String schemaName) {
        /*
         *      JRS-19662 Story JRS-18801: VDS created in legacy domain designer cannot be opened in new DD
         */
        return schemaName.replace("\\","\\\\");
    }

    @Override
    public String schemaNameUnEscape(DatabaseMetaData databaseMetaData, String schemaName) {
        return schemaName.replace("\\\\","\\");
    }

}
