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

package com.jaspersoft.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * This dialect maps blobs to binary streams for PostgreSQL.
 * This is necessary because PostgreSQL requires blob access to happen within a transaction.
 * That means we have to turn defaultAutoCommit off which has some weird side effects.
 * Using the "bytea" type should work better.
 * 
 * There are a few aspects to implementing this:
 * - Generate the "bytea" type for columns with Hibernate type "blob"
 * - Set the "mapBlobsToBinaryType" flag, which will cause ByteWrappingBlobType.set() 
 *   to use ResultSet.setBinaryStream() instead of ResultSet.setBlob()
 * - Use ByteWrappingBlobType (a subclass of BlobType) instead of "blob" in the Hibernate mapping files.
 *   This overrides BlobType.get() to call ResultSet.getObject() instead of getBlob, and then turns
 *   whatever it gets into a Blob if it isn't one already.
 *   
 *   I'm not sure why we have to use blobs, and they're only used in 4 places. However, I do know that
 *   Oracle has a brain-dead binary type (LONG RAW) that we should stay away from, and if we've already
 *   tested with blobs on other databases, then just let it be.
 */
public class PostgresqlNoBlobDialect extends PostgreSQL82Dialect {

    public PostgresqlNoBlobDialect() {
        super();
        // use "bytea" to map blob types
        registerColumnType( Types.BLOB, "bytea" );

    }


    @Override
    public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        SqlTypeDescriptor descriptor;
        switch ( sqlCode ) {
            case Types.BLOB: {
                // Force BLOB binding to byte[].
                descriptor = BinaryTypeDescriptor.INSTANCE;
                break;
            }
            default: {
                descriptor = super.getSqlTypeDescriptorOverride( sqlCode );
                break;
            }
        }
        return descriptor;
    }

}