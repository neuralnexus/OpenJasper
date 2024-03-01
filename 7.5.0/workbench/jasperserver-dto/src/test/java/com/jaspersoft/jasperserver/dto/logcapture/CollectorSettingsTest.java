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

package com.jaspersoft.jasperserver.dto.logcapture;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class CollectorSettingsTest extends BaseDTOPresentableTest<CollectorSettings> {

    @Override
    protected List<CollectorSettings> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId("id2"),
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setStatus("status2"),
                createFullyConfiguredInstance().setVerbosity("verbosity2"),
                createFullyConfiguredInstance().setLogFilterParameters(new LogFilterParameters().setUserId("userId2")),
                // with null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setStatus(null),
                createFullyConfiguredInstance().setVerbosity(null),
                createFullyConfiguredInstance().setLogFilterParameters(null)
        );
    }

    @Override
    protected CollectorSettings createFullyConfiguredInstance() {
        CollectorSettings collectorSettings = new CollectorSettings();
        collectorSettings.setId("id");
        collectorSettings.setName("name");
        collectorSettings.setStatus("status");
        collectorSettings.setVerbosity("verbosity");
        collectorSettings.setLogFilterParameters(new LogFilterParameters().setUserId("userId"));
        return collectorSettings;
    }

    @Override
    protected CollectorSettings createInstanceWithDefaultParameters() {
        return new CollectorSettings();
    }

    @Override
    protected CollectorSettings createInstanceFromOther(CollectorSettings other) {
        return new CollectorSettings(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(CollectorSettings expected, CollectorSettings actual) {
        assertNotSame(expected.getLogFilterParameters(), actual.getLogFilterParameters());
    }

    @Test
    public void exportIsNotEnabledForNullLogFilterParameters() {
        fullyConfiguredTestInstance.setLogFilterParameters(null);
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(false, result);
    }

    @Test
    public void exportIsEnabledWhenExportDataSnapshotDisabled() {
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(false, result);
    }

    @Test
    public void exportIsEnabledWhenExportDataSnapshotEnabled() {
        ResourceAndSnapshotFilter resourceAndSnapshotFilter = new ResourceAndSnapshotFilter()
                .setResourceUri("uri")
                .setIncludeDataSnapshots(true);

        fullyConfiguredTestInstance.setLogFilterParameters(new LogFilterParameters()
                .setResourceAndSnapshotFilter(resourceAndSnapshotFilter));
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(true, result);
    }
}
