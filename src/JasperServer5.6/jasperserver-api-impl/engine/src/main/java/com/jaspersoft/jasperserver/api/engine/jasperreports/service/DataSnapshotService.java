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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service;

import net.sf.jasperreports.data.cache.DataSnapshot;
import net.sf.jasperreports.engine.ReportContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CopyDestinationExistsException;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshotData;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotPersistentMetadata;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSnapshotService.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface DataSnapshotService {

	boolean isSnapshotRecordingEnabled();

	boolean isSnapshotPersistenceEnabled();
	
	DataSnapshotPersistentMetadata getSnapshotMetadata(ExecutionContext context, long snapshotId);
	
	DataSnapshot getSnapshotContents(ExecutionContext context, long contentsId);

	long saveDataSnapshot(ExecutionContext context, DataCacheSnapshot snapshot,
			Long existingSnapshotId);

	void deleteSnapshot(ExecutionContext context, long snapshotId);

	boolean saveAutoReportDataSnapshot(ExecutionContext context, 
			ReportContext reportContext, ReportUnit reportUnit);

	boolean saveReportDataSnapshot(ExecutionContext context, 
			ReportContext reportContext, ReportUnit reportUnit);

	String saveReportDataSnapshotCopy(ExecutionContext context, 
			ReportContext reportContext, ReportUnit reportUnit,
    		String folderUri, String label, boolean overwrite) throws CopyDestinationExistsException;
	
	DataSnapshotPersistResult persistDataSnapshot(ExecutionContext context, ReportContext reportContext, 
			Long existingSnapshotId);

	DataCacheSnapshotData loadSnapshotData(ExecutionContext context, Long snapshotId);

	long saveDataSnapshot(ExecutionContext context, DataCacheSnapshotData dataSnapshot);

	Long copyDataSnapshot(Long snapshotId);

}
