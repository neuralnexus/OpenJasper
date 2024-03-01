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

package com.jaspersoft.hibernate;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BlobType;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

@Deprecated
public class ByteWrappingBlobType extends BlobType {
	
	public static Logger log = LogManager.getLogger(ByteWrappingBlobType.class);
	
	public static final String MAP_BLOBS_TO_BINARY_TYPE = "mapBlobsToBinaryType";

	public ByteWrappingBlobType() {

	}

	public void nullSafeSet(CallableStatement st, Object value, String name, SessionImplementor session) throws SQLException {
		// we are using the useInputStreamToInsertBlob() as a proxy for mapping blobs to binaries
		String mapProp = session.getFactory().getDialect().getDefaultProperties().getProperty(MAP_BLOBS_TO_BINARY_TYPE);
		final boolean mapBlobToStreams = String.valueOf(true).equals(mapProp);

		// if we are setting a NULL value, it's important that we know what the real DB type is
		// if we are mapping to a binary type, then use that type to set null
		if (value == null) {
			st.setNull(name, mapBlobToStreams ? Types.BINARY : Types.BLOB);
		} else {
			Blob blob = (Blob)value;
			if (mapBlobToStreams) {
				st.setBinaryStream( name, blob.getBinaryStream(), (int) blob.length() );
			}
			else {
				super.nullSafeSet(st, value, name, session);
			}
		}

		super.nullSafeSet(st, value, name, session);
	}



	/**
	 * on calling get(), let's just call getObject() and see what happens, then turn it
	 * into a blob if it isn't one.
	 */

	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException {
		Object value = rs.getObject(name);
		if (rs.wasNull()) {
			return null;
		}
		if (value instanceof Blob) {
			log.debug("getting blob for " + name);
			return DataContainerStreamUtil.createComparableBlob((Blob) value);
		} else if (value instanceof byte[]) {
			log.debug("getting byte[" + ((byte[])value).length + "] for " + name);
			return DataContainerStreamUtil.createComparableBlob((byte[]) value);
		} else if (value instanceof InputStream) {
			try {
				log.debug("getting input stream for " + name);
				return DataContainerStreamUtil.createComparableBlob((InputStream) value);
			} catch (Exception e) {
				throw new HibernateException("exception creating blob from input stream", e);
			}
		} else {
			throw new HibernateException("I don't know how to map the type " + value.getClass().getName() + " to a blob");
		}
	}




	// super implementation uses ==, which doesn't work because we recreate the blob
	// we have created a ComparableBlob class which lets us make sure that the byte array is the same
//	TODO: we can`t override isEquals - so look at super implementation which uses TypeDescriptor, it`s possible we need to introduce it ....
//	public boolean isEqual(Object x, Object y) {
//		if (x != null && y != null) {
//			return x.equals(y);
//		}
//		return super.isEqual(x, y);
//	}




/*
	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
		byte[] bytes = (byte[]) cached;
		try {
			return (bytes == null) ? null : new SerialBlob(bytes);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Serializable disassemble(Object value, SessionImplementor session, Object owner)	throws HibernateException {
		if (value instanceof SerialBlob) {
			SerialBlob cblob = (SerialBlob) value;
			try {
				return cblob.getBytes(1, (int) cblob.length());
			} catch (SerialException e) {
				e.printStackTrace();
			}
		} else if (value == null) {
			return null;
		} else {
            try {
                Blob blob = (Blob) value;
                byte[] data = new byte[(int)blob.length()];
                blob.getBinaryStream().read(data);
                return data;
            } catch (Exception e) {
                return super.disassemble(value, session, owner);
            }
		}
		return null;
	}
*/

}
