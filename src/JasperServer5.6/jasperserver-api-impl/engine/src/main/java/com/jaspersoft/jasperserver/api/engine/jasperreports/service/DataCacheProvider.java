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

import java.util.Map;

import net.sf.jasperreports.engine.ReportContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotPersistentMetadata;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataCacheProvider.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface DataCacheProvider {
	
	DataCacheSnapshot setReportExecutionCache(ExecutionContext context, 
			ReportUnitRequestBase request, ReportUnit reportUnit, Map<String, Object> dataParameters);
	
	DataSnapshotPersistentMetadata loadSavedDataSnapshot(ExecutionContext context, ReportUnit reportUnit);
	
	void handleInvalidSnapshot(ExecutionContext context, 
			ReportUnitRequestBase request, ReportUnit reportUnit, Map<String, Object> dataParameters);
	
	boolean parametersMatch(Map<String, Object> dataParameters,
			Map<String, Object> snapshotParameters);

	DataCacheSnapshot getDataSnapshot(ExecutionContext context,
			ReportContext reportContext);

	void snapshotSaved(ExecutionContext context, ReportContext reportContext,
			DataCacheSnapshot snapshot, Long savedSnapshotId);

	enum SnapshotSaveStatus {
		NO_CHANGE, NEW, UPDATED;
	}
	
	SnapshotSaveStatus getSnapshotSaveStatus(ReportContext reportContext);

}
