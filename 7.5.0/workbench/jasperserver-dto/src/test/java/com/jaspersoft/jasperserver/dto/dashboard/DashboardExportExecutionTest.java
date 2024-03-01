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
import com.jaspersoft.jasperserver.dto.reports.ReportParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class DashboardExportExecutionTest extends BaseDTOPresentableTest<DashboardExportExecution> {

    private static final int TEST_WIDTH = 100;
    private static final int TEST_WIDTH_1 = 1001;

    private static final int TEST_HEIGHT = 101;
    private static final int TEST_HEIGHT_1 = 1011;

    private static final int TEST_REFERENCE_WIDTH = 102;
    private static final int TEST_REFERENCE_WIDTH_1 = 1021;

    private static final int TEST_REFERENCE_HEIGHT = 103;
    private static final int TEST_REFERENCE_HEIGHT_1 = 1031;

    private static final DashboardExportExecution.ExportFormat TEST_FORMAT = DashboardExportExecution.ExportFormat.pdf;
    private static final DashboardExportExecution.ExportFormat TEST_FORMAT_1 = DashboardExportExecution.ExportFormat.docx;

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final DashboardParameters TEST_PARAMETERS = new DashboardParameters().setDashboardParameters(
            Collections.singletonList(new ReportParameter().setName("TEST_NAME"))
    );
    private static final DashboardParameters TEST_PARAMETERS_1 = new DashboardParameters().setDashboardParameters(
            Collections.singletonList(new ReportParameter().setName("TEST_NAME_1"))
    );

    private static final String TEST_MARKUP = "TEST_MARKUP";
    private static final String TEST_MARKUP_1 = "TEST_MARKUP_1";

    private static final List<String> TEST_JR_STYLE = Arrays.asList("TEST_JR_STYLE_1", "TEST_JR_STYLE_2");
    private static final List<String> TEST_JR_STYLE_1 = Arrays.asList("TEST_JR_STYLE_1_1", "TEST_JR_STYLE_2_1");
    private static final List<String> TEST_JR_STYLE_EMPTY = new ArrayList<String>();

    private static final String TEST_OUTPUT_TIME_ZONE = "TEST_OUTPUT_TIME_ZONE";
    private static final String TEST_OUTPUT_TIME_ZONE_1 = "TEST_OUTPUT_TIME_ZONE_1";

    private static final String TEST_OUTPUT_LOCALE = "TEST_OUTPUT_LOCALE";
    private static final String TEST_OUTPUT_LOCALE_1 = "TEST_OUTPUT_LOCALE_1";

    /*
     * Preparing
     */

    @Override
    protected List<DashboardExportExecution> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setWidth(TEST_WIDTH_1),
                createFullyConfiguredInstance().setHeight(TEST_HEIGHT_1),
                createFullyConfiguredInstance().setReferenceWidth(TEST_REFERENCE_WIDTH_1),
                createFullyConfiguredInstance().setReferenceHeight(TEST_REFERENCE_HEIGHT_1),
                createFullyConfiguredInstance().setFormat(TEST_FORMAT_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_1),
                createFullyConfiguredInstance().setMarkup(TEST_MARKUP_1),
                createFullyConfiguredInstance().setJrStyle(TEST_JR_STYLE_1),
                createFullyConfiguredInstance().setJrStyle(TEST_JR_STYLE_EMPTY),
                createFullyConfiguredInstance().setOutputTimeZone(TEST_OUTPUT_TIME_ZONE_1),
                createFullyConfiguredInstance().setOutputLocale(TEST_OUTPUT_LOCALE_1),
                // default values
                createFullyConfiguredInstance().setWidth(0),
                createFullyConfiguredInstance().setHeight(0),
                createFullyConfiguredInstance().setReferenceWidth(0),
                createFullyConfiguredInstance().setReferenceHeight(0),
                createFullyConfiguredInstance().setFormat(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setParameters(null),
                createFullyConfiguredInstance().setMarkup(null),
                createFullyConfiguredInstance().setJrStyle(null),
                createFullyConfiguredInstance().setOutputTimeZone(null),
                createFullyConfiguredInstance().setOutputLocale(null)
        );
    }

    @Override
    protected DashboardExportExecution createFullyConfiguredInstance() {
        return new DashboardExportExecution()
                .setWidth(TEST_WIDTH)
                .setHeight(TEST_HEIGHT)
                .setReferenceWidth(TEST_REFERENCE_WIDTH)
                .setReferenceHeight(TEST_REFERENCE_HEIGHT)
                .setFormat(TEST_FORMAT)
                .setUri(TEST_URI)
                .setId(TEST_ID)
                .setParameters(TEST_PARAMETERS)
                .setMarkup(TEST_MARKUP)
                .setJrStyle(TEST_JR_STYLE)
                .setOutputTimeZone(TEST_OUTPUT_TIME_ZONE)
                .setOutputLocale(TEST_OUTPUT_LOCALE);
    }

    @Override
    protected DashboardExportExecution createInstanceWithDefaultParameters() {
        return new DashboardExportExecution();
    }

    @Override
    protected DashboardExportExecution createInstanceFromOther(DashboardExportExecution other) {
        return new DashboardExportExecution(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(DashboardExportExecution expected, DashboardExportExecution actual) {
        assertNotSame(expected.getParameters(), actual.getParameters());
        assertNotSame(expected.getJrStyle(), actual.getJrStyle());
    }
}