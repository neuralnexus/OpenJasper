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
package com.jaspersoft.jasperserver.dto.dashboard;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.dashboard.DashboardExportExecutionStatus.Status;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DashboardExportExecutionStatusTest extends BaseDTOPresentableTest<DashboardExportExecutionStatus> {

    private static final String TEST_ID = "dashboard_export_execution_id";
    private static final String TEST_ID_1 = "dashboard_export_execution_id_1";

    private static final int TEST_PROGRESS = 100;
    private static final int TEST_PROGRESS_1 = 101;

    private static final Status TEST_STATUS = Status.execution;
    private static final Status TEST_STATUS_1 = Status.cancelled;

    /*
     * Preparing
     */

    @Override
    protected List<DashboardExportExecutionStatus> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setProgress(TEST_PROGRESS_1),
                createFullyConfiguredInstance().setStatus(TEST_STATUS_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setProgress(0),
                createFullyConfiguredInstance().setStatus(null)
        );
    }

    @Override
    protected DashboardExportExecutionStatus createFullyConfiguredInstance() {
        return new DashboardExportExecutionStatus()
                .setId(TEST_ID)
                .setProgress(TEST_PROGRESS)
                .setStatus(TEST_STATUS);
    }

    @Override
    protected DashboardExportExecutionStatus createInstanceWithDefaultParameters() {
        return new DashboardExportExecutionStatus();
    }

    @Override
    protected DashboardExportExecutionStatus createInstanceFromOther(DashboardExportExecutionStatus other) {
        return new DashboardExportExecutionStatus(other);
    }
}
