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
package com.jaspersoft.jasperserver.api.metadata.data.cache;

import net.sf.jasperreports.data.cache.DataSnapshot;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSnapshotPersistenceService.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface DataSnapshotPersistenceService {

	boolean matchesVersion(ExecutionContext context, long snapshotId, int version);

	DataSnapshotPersistentMetadata loadDataSnapshotMetadata(ExecutionContext context, long snapshotId);

	DataSnapshot loadDataSnapshot(ExecutionContext context, long contentsId);

	DataCacheSnapshotData loadSnapshotData(ExecutionContext context, long snapshotId);

	DataSnapshotSavedId saveDataSnapshot(ExecutionContext context, DataCacheSnapshot snapshot,
			Long existingSnapshotId);

	long saveDataSnapshot(ExecutionContext context, DataCacheSnapshotData snapshot);

	void deleteSnapshot(long snapshotId);

	Long copyDataSnapshot(long snapshotId);
	
}
