/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.sql.Blob;

import org.hibernate.lob.BlobImpl;
import org.hibernate.lob.SerializableBlob;

public class ComparableBlob extends SerializableBlob {
	private static final long serialVersionUID = -7928795808929208452L;
	private byte[] bytes;
	private transient Blob myBlob;
	
	public ComparableBlob(byte[] bytes) {
		super(new BlobImpl(bytes));
		this.bytes = bytes;
	}
	
	// superclass implements Serializable but doesn't actually work after serialization, so can't be cached.
	// we are saving the byte[] so we can create blob as needed
	public Blob getWrappedBlob() {
		if (myBlob == null) {
			myBlob = new BlobImpl(bytes);
		}
		return myBlob;
	}
	
	// same byte array? fine!
	public boolean equals(Object o) {
		return o instanceof ComparableBlob && ((ComparableBlob) o).bytes == bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}


}