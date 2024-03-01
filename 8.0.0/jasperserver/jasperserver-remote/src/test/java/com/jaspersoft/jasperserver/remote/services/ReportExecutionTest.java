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

package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableSet;
import static org.testng.Assert.*;

public class ReportExecutionTest {
    private volatile ReportExecution execution;
    private ReportExecutionOptions ops;
    private ErrorDescriptor someError;

    @BeforeMethod
    public void setUp() {
        execution = makeExecution();

        someError = new ErrorDescriptor()
                .setErrorCode("401")
                .setMessage("Ups!")
                .setProperties(
                        singletonList(new ClientProperty("retry", "true")));
    }

    private ReportExecution makeExecution() {
        ReportExecutionOptions ops = new ReportExecutionOptions()
                .setFreshData(true)
                .setRequestId(UUID.randomUUID().toString());

        ReportExecution execution = new ReportExecution();
        execution.setOptions(ops);
        execution.setRequestId(ops.getRequestId());
        execution.setReportURI("/public/report");
        execution.setRawParameters(new HashMap<>());

        return execution;
    }

    private ExportExecution makeExportExecution() {
        ExportExecutionOptions ops = new ExportExecutionOptions()
                .setBaseUrl("/public")
                .setOutputFormat("pdf");

        ExportExecution execution = new ExportExecution();
        execution.setOptions(ops);
        execution.setStatus(ExecutionStatus.ready);

        return execution;
    }

    @Test
    public void testReadyStatus() {
        assertNull(execution.getStatus());

        execution.setStatus(ExecutionStatus.ready);
        assertEquals(ExecutionStatus.ready, execution.getStatus());
        assertNull(execution.getReportUnitResult());
        assertNull(execution.getErrorDescriptor());
    }

    @Test
    public void testExecutionStatus() {
        assertNull(execution.getStatus());

        execution.setStatus(ExecutionStatus.execution);
        execution.setExportsSet(Collections.singleton( makeExportExecution()));

        assertEquals(ExecutionStatus.execution, execution.getStatus());
        assertNull(execution.getReportUnitResult());
        assertNull(execution.getErrorDescriptor());
    }

    @Test
    public void testExecutionErrorStatus() {
        assertNull(execution.getStatus());

        execution.setStatus(ExecutionStatus.execution);
        execution.setErrorDescriptor(someError);

        assertEquals(ExecutionStatus.failed, execution.getStatus());
        assertNull(execution.getReportUnitResult());
        assertNotNull(execution.getErrorDescriptor());
    }

}