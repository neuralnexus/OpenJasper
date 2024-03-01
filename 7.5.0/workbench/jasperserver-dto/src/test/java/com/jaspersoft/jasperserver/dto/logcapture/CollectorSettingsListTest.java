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

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class CollectorSettingsListTest extends BaseDTOPresentableTest<CollectorSettingsList> {

    @Override
    protected List<CollectorSettingsList> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCollectorSettingsList(Arrays.asList(new CollectorSettings().setId("id2"), new CollectorSettings().setName("name"))),
                // with null values
                createFullyConfiguredInstance().setCollectorSettingsList(null)
        );
    }

    @Override
    protected CollectorSettingsList createFullyConfiguredInstance() {
        CollectorSettingsList collectorSettingsList = new CollectorSettingsList();
        collectorSettingsList.setCollectorSettingsList(Arrays.asList(new CollectorSettings().setId("id"), new CollectorSettings()));
        return collectorSettingsList;
    }

    @Override
    protected CollectorSettingsList createInstanceWithDefaultParameters() {
        return new CollectorSettingsList();
    }

    @Override
    protected CollectorSettingsList createInstanceFromOther(CollectorSettingsList other) {
        return new CollectorSettingsList(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(CollectorSettingsList expected, CollectorSettingsList actual) {
        assertNotSameCollection(expected.getCollectorSettingsList(), actual.getCollectorSettingsList());
    }
}
