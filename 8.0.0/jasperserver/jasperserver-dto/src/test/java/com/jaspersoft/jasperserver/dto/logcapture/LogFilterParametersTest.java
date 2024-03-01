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

package com.jaspersoft.jasperserver.dto.logcapture;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class LogFilterParametersTest extends BaseDTOPresentableTest<LogFilterParameters> {

    @Override
    protected List<LogFilterParameters> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setUserId("id2"),
                createFullyConfiguredInstance().setSessionId("sessionId2"),
                createFullyConfiguredInstance().setResourceAndSnapshotFilter(new ResourceAndSnapshotFilter().setResourceUri("resourceUri2")),
                // with null values
                createFullyConfiguredInstance().setUserId(null),
                createFullyConfiguredInstance().setSessionId(null),
                createFullyConfiguredInstance().setResourceAndSnapshotFilter(null)
        );
    }

    @Override
    protected LogFilterParameters createFullyConfiguredInstance() {
        LogFilterParameters logFilterParameters = new LogFilterParameters();
        logFilterParameters.setUserId("id");
        logFilterParameters.setSessionId("sessionId");
        logFilterParameters.setResourceAndSnapshotFilter(new ResourceAndSnapshotFilter().setResourceUri("resourceUri"));
        return logFilterParameters;
    }

    @Override
    protected LogFilterParameters createInstanceWithDefaultParameters() {
        return new LogFilterParameters();
    }

    @Override
    protected LogFilterParameters createInstanceFromOther(LogFilterParameters other) {
        return new LogFilterParameters(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(LogFilterParameters expected, LogFilterParameters actual) {
        assertNotSame(expected.getResourceAndSnapshotFilter(), actual.getResourceAndSnapshotFilter());
    }
}
