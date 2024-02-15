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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientMultiAxisLevelReferenceTest extends BaseDTOJSONPresentableTest<ClientMultiAxisLevelReference> {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_NAME_ALT = "TEST_NAME_ALT";

    private static final Boolean TEST_AGGREGATION = true;
    private static final Boolean TEST_AGGREGATION_ALT = false;

    private static final String TEST_DIMENSION = "TEST_DIMENSION";
    private static final String TEST_DIMENSION_ALT = "TEST_DIMENSION_ALT";

    private static final String TEST_HIERARCHY = "TEST_HIERARCHY";
    private static final String TEST_HIERARCH_ALT = "TEST_HIERARCHY_ALT";

    @Override
    protected List<ClientMultiAxisLevelReference> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName(TEST_NAME_ALT),
                createFullyConfiguredInstance().setAggregation(TEST_AGGREGATION_ALT),
                createFullyConfiguredInstance().setDimension(TEST_DIMENSION_ALT),
                createFullyConfiguredInstance().setHierarchy(TEST_HIERARCH_ALT),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setAggregation(null),
                createFullyConfiguredInstance().setDimension(null),
                createFullyConfiguredInstance().setHierarchy(null)
        );
    }

    @Override
    protected ClientMultiAxisLevelReference createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setName(TEST_NAME)
                .setAggregation(TEST_AGGREGATION)
                .setDimension(TEST_DIMENSION)
                .setHierarchy(TEST_HIERARCHY);
    }

    @Override
    protected ClientMultiAxisLevelReference createInstanceWithDefaultParameters() {
        return new ClientMultiAxisLevelReference();
    }

    @Override
    protected ClientMultiAxisLevelReference createInstanceFromOther(ClientMultiAxisLevelReference other) {
        return new ClientMultiAxisLevelReference(other);
    }
}