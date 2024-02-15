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

package com.jaspersoft.jasperserver.dto.reports;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ReportParametersTest extends BaseDTOPresentableTest<ReportParameters> {

    private static final List<ReportParameter> TEST_REPORT_PARAMETERS = Collections.singletonList(
            new ReportParameter()
                    .setName("TEST_NAME")
                    .setValues(Collections.singletonList("TEST_VALUE"))
    );
    private static final List<ReportParameter> TEST_REPORT_PARAMETERS_1 = Collections.singletonList(
            new ReportParameter()
                    .setName("TEST_NAME_1")
                    .setValues(Collections.singletonList("TEST_VALUE_1"))
    );
    private static final List<ReportParameter> TEST_REPORT_PARAMETERS_EMPTY = new ArrayList<ReportParameter>();

    @Test
    public void testConstructor() {
        ReportParameters instance = new ReportParameters(TEST_REPORT_PARAMETERS);
        assertEquals(TEST_REPORT_PARAMETERS, instance.getReportParameters());
    }

    @Override
    protected List<ReportParameters> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setReportParameters(TEST_REPORT_PARAMETERS_1),
                createFullyConfiguredInstance().setReportParameters(TEST_REPORT_PARAMETERS_EMPTY),
                // null values
                createFullyConfiguredInstance().setReportParameters(null)
        );
    }

    @Override
    protected ReportParameters createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setReportParameters(TEST_REPORT_PARAMETERS);
    }

    @Override
    protected ReportParameters createInstanceWithDefaultParameters() {
        return new ReportParameters();
    }

    @Override
    protected ReportParameters createInstanceFromOther(ReportParameters other) {
        return new ReportParameters(other);
    }
}
