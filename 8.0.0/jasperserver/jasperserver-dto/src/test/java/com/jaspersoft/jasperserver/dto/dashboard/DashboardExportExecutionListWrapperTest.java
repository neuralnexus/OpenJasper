/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class DashboardExportExecutionListWrapperTest extends BaseDTOPresentableTest<DashboardExportExecutionListWrapper> {

    private static final List<DashboardExportExecution> TEST_EXECUTIONS = Collections.singletonList(new DashboardExportExecution().setFormat(DashboardExportExecution.ExportFormat.pdf));
    private static final List<DashboardExportExecution> TEST_EXECUTIONS_1 = Collections.singletonList(new DashboardExportExecution().setFormat(DashboardExportExecution.ExportFormat.docx));
    private static final List<DashboardExportExecution> TEST_EXECUTIONS_EMPTY = new ArrayList<DashboardExportExecution>();


    @Override
    protected void assertFieldsHaveUniqueReferences(DashboardExportExecutionListWrapper expected, DashboardExportExecutionListWrapper actual) {
        assertNotSame(expected.getExecutions(), actual.getExecutions());
        assertNotSame(expected.getExecutions().get(0), actual.getExecutions().get(0));
    }

    /*
     * Preparing
     */

    @Override
    protected List<DashboardExportExecutionListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExecutions(TEST_EXECUTIONS_1),
                createFullyConfiguredInstance().setExecutions(TEST_EXECUTIONS_EMPTY),
                // null values
                createFullyConfiguredInstance().setExecutions(null)
        );
    }

    @Override
    protected DashboardExportExecutionListWrapper createFullyConfiguredInstance() {
        return new DashboardExportExecutionListWrapper()
                .setExecutions(TEST_EXECUTIONS);
    }

    @Override
    protected DashboardExportExecutionListWrapper createInstanceWithDefaultParameters() {
        return new DashboardExportExecutionListWrapper();
    }

    @Override
    protected DashboardExportExecutionListWrapper createInstanceFromOther(DashboardExportExecutionListWrapper other) {
        return new DashboardExportExecutionListWrapper(other);
    }

}
