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

package com.jaspersoft.jasperserver.dto.adhoc.query.field;

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel.ClientLevelAggregationsRef;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel.ClientLevelAggregationsRef.AGGREGATIONS_ID;
import static com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel.ClientLevelAggregationsRef.AGGREGATIONS_LEVEL_NAME;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientLevelAggregationsRefTest extends BaseDTOJSONPresentableTest<ClientLevelAggregationsRef> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_ALT = "TEST_ID_ALT";

    private static final String TEST_FIELD_NAME = "TEST_FILE_NAME";

    private static final String TEST_TYPE = "TEST_TYPE";

    private static final String TEST_DIMENSION = "TEST_DIMENSION";
    private static final String TEST_DIMENSION_ALT = "TEST_DIMENSION_ALT";

    private static final String TEST_HIERARCHY_NAME = "TEST_HIERARCHY_NAME";
    private static final String TEST_HIERARCHY_NAME_ALT = "TEST_HIERARCHY_NAME_ALT";

    // At the moment ClientDataSourceField and all its subclasses have measure = false;
    private static final ClientDataSourceLevel TEST_DATASOURCE_FIELD = new ClientDataSourceLevel()
            .setType(TEST_TYPE)
            .setName(TEST_FIELD_NAME)
            .setDimensionName(TEST_DIMENSION)
            .setHierarchyName(TEST_HIERARCHY_NAME);
    private static final ClientDataSourceLevel TEST_DATASOURCE_FIELD_ALT = new ClientDataSourceLevel()
            .setType(TEST_TYPE)
            .setName(TEST_FIELD_NAME);
    private static final ClientDataSourceLevel TEST_DATASOURCE_FIELD_ALT_1 = new ClientDataSourceLevel()
            .setType(TEST_TYPE)
            .setName(TEST_FIELD_NAME)
            .setDimensionName(TEST_DIMENSION);
    private static final ClientDataSourceLevel TEST_DATASOURCE_FIELD_ALT_2 = new ClientDataSourceLevel()
            .setType(TEST_TYPE)
            .setName(TEST_FIELD_NAME)
            .setDimensionName(TEST_DIMENSION)
            .setHierarchyName(TEST_HIERARCHY_NAME_ALT);
    private static final ClientDataSourceLevel TEST_DATASOURCE_FIELD_ALT_3 = new ClientDataSourceLevel()
            .setType(TEST_TYPE)
            .setName(TEST_FIELD_NAME)
            .setDimensionName(TEST_DIMENSION_ALT)
            .setHierarchyName(TEST_HIERARCHY_NAME_ALT);

    private static final String TEST_CATEGORIZER = "TEST_CATEGORIZER";
    private static final String TEST_CATEGORIZER_ALT = "TEST_CATEGORIZER_ALT";

    private static final Boolean TEST_INCLUDE_ALL = true;
    private static final Boolean TEST_INCLUDE_ALL_ALT = false;

    @Override
    protected List<ClientLevelAggregationsRef> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setId(TEST_ID_ALT),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setCategorizer(TEST_CATEGORIZER_ALT),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT_1),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT_2),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT_3),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setIncludeAll(TEST_INCLUDE_ALL_ALT),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setId(null),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setFieldName(null),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setCategorizer(null),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setDataSourceField(null),
                (ClientLevelAggregationsRef)createFullyConfiguredInstance().setIncludeAll(null)
        );
    }

    @Override
    protected ClientLevelAggregationsRef createFullyConfiguredInstance() {
        return (ClientLevelAggregationsRef)new ClientLevelAggregationsRef()
                .setId(AGGREGATIONS_ID)
                .setFieldName(AGGREGATIONS_LEVEL_NAME)
                .setDimension(TEST_DIMENSION)
                .setHierarchyName(TEST_HIERARCHY_NAME)
                .setCategorizer(TEST_CATEGORIZER)
                .setIncludeAll(TEST_INCLUDE_ALL);
    }

    @Override
    protected ClientLevelAggregationsRef createInstanceWithDefaultParameters() {
        return new ClientLevelAggregationsRef();
    }

    @Override
    protected ClientLevelAggregationsRef createInstanceFromOther(ClientLevelAggregationsRef other) {
        return new ClientLevelAggregationsRef(other);
    }
}
