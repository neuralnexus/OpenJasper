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

package com.jaspersoft.jasperserver.dto.reports.options;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ReportOptionsSummaryListTest extends BaseDTOPresentableTest<ReportOptionsSummaryList> {

    private static final List<ReportOptionsSummary> TEST_SUMMARY_LIST = Arrays.asList(
            new ReportOptionsSummary().setId("TEST_ID"),
            new ReportOptionsSummary().setLabel("TEST_LABEL")
    );
    private static final List<ReportOptionsSummary> TEST_SUMMARY_LIST_1 = Arrays.asList(
            new ReportOptionsSummary().setId("TEST_ID_1"),
            new ReportOptionsSummary().setLabel("TEST_LABEL_1")
    );

    @Override
    protected List<ReportOptionsSummaryList> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSummaryList(TEST_SUMMARY_LIST_1)
        );
    }

    @Override
    protected ReportOptionsSummaryList createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setSummaryList(TEST_SUMMARY_LIST);
    }

    @Override
    protected ReportOptionsSummaryList createInstanceWithDefaultParameters() {
        return new ReportOptionsSummaryList();
    }

    @Override
    protected ReportOptionsSummaryList createInstanceFromOther(ReportOptionsSummaryList other) {
        return new ReportOptionsSummaryList(other);
    }

}
