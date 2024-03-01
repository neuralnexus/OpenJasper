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

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientQueryGroupTest extends BaseDTOJSONPresentableTest<ClientQueryGroup> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_ALT = "TEST_ID_ALT";

    private static final String TEST_FILE_NAME = "TEST_FILE_NAME";
    private static final String TEST_FILE_NAME_ALT = "TEST_FILE_NAME_ALT";

    private static final ClientDataSourceField TEST_DATASOURCE_FIELD = new ClientDataSourceField().setType("TEST_TYPE").setName("TEST_NAME");
    private static final ClientDataSourceField TEST_DATASOURCE_FIELD_ALT = new ClientDataSourceField().setType("TEST_TYPE").setFormat("TEST_FORMAT");

    private static final String TEST_CATEGORIZER = "TEST_CATEGORIZER";
    private static final String TEST_CATEGORIZER_ALT = "TEST_CATEGORIZER_ALT";

    private static final Boolean TEST_INCLUDE_ALL = true;
    private static final Boolean TEST_INCLUDE_ALL_ALT = false;

    @Override
    protected List<ClientQueryGroup> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_ALT),
                createFullyConfiguredInstance().setFieldName(TEST_FILE_NAME_ALT),
                createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD),
                createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT),
                createFullyConfiguredInstance().setCategorizer(TEST_CATEGORIZER_ALT),
                createFullyConfiguredInstance().setIncludeAll(TEST_INCLUDE_ALL_ALT),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setFieldName(null),
                createFullyConfiguredInstance().setDataSourceField(null),
                createFullyConfiguredInstance().setCategorizer(null),
                createFullyConfiguredInstance().setIncludeAll(null)
        );
    }

    @Override
    protected ClientQueryGroup createFullyConfiguredInstance() {
        return new ClientQueryGroup()
                .setId(TEST_ID)
                .setFieldName(TEST_FILE_NAME)
                .setCategorizer(TEST_CATEGORIZER)
                .setIncludeAll(TEST_INCLUDE_ALL);
    }

    @Override
    protected ClientQueryGroup createInstanceWithDefaultParameters() {
        return new ClientQueryGroup();
    }

    @Override
    protected ClientQueryGroup createInstanceFromOther(ClientQueryGroup other) {
        return new ClientQueryGroup(other);
    }
}