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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.job.ClientJobSummary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientJobSummariesListWrapperTest extends BaseDTOPresentableTest<ClientJobSummariesListWrapper> {

    private static final ClientJobSummary TEST_JOB_SUMMARY = new ClientJobSummary().setId(1L);
    private static final List<ClientJobSummary> TEST_JOB_SUMMARIES = Collections.singletonList(TEST_JOB_SUMMARY);

    private static final ClientJobSummary TEST_JOB_SUMMARY_1 = new ClientJobSummary().setId(2L);
    private static final List<ClientJobSummary> TEST_JOB_SUMMARIES_1 = Collections.singletonList(TEST_JOB_SUMMARY_1);

    @Override
    protected List<ClientJobSummariesListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setJobsummary(TEST_JOB_SUMMARIES_1),
                // null values
                createFullyConfiguredInstance().setJobsummary(null)
        );
    }

    @Override
    protected ClientJobSummariesListWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setJobsummary(TEST_JOB_SUMMARIES);
    }

    @Override
    protected ClientJobSummariesListWrapper createInstanceWithDefaultParameters() {
        return new ClientJobSummariesListWrapper();
    }

    @Override
    protected ClientJobSummariesListWrapper createInstanceFromOther(ClientJobSummariesListWrapper other) {
        return new ClientJobSummariesListWrapper(other);
    }
}