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

package com.jaspersoft.jasperserver.dto.adhoc.query.field;

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup.ClientAllGroup;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup.ClientAllGroup.ALL_GROUP_ID;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientAllGroupTest extends BaseDTOJSONPresentableTest<ClientAllGroup> {

    private static final String TEST_ID = "TEST_ID";

    private static final ClientDataSourceField TEST_DATASOURCE_FIELD = new ClientDataSourceField().setType("TEST_TYPE");

    private static final String TEST_CATEGORIZER = "TEST_CATEGORIZER";
    private static final String TEST_CATEGORIZER_ALT = "TEST_CATEGORIZER_ALT";

    private static final Boolean TEST_INCLUDE_ALL = true;
    private static final Boolean TEST_INCLUDE_ALL_ALT = false;

    private static final String TEST_FIELD_NAME = "TEST_FIELD_NAME";

    @Override
    protected List<ClientAllGroup> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                (ClientAllGroup)createFullyConfiguredInstance().setId(TEST_ID),
                (ClientAllGroup)createFullyConfiguredInstance().setFieldName(TEST_FIELD_NAME),
                (ClientAllGroup)createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD),
                (ClientAllGroup)createFullyConfiguredInstance().setCategorizer(TEST_CATEGORIZER_ALT),
                (ClientAllGroup)createFullyConfiguredInstance().setIncludeAll(TEST_INCLUDE_ALL_ALT),
                (ClientAllGroup)createFullyConfiguredInstance().setId(null),
                (ClientAllGroup)createFullyConfiguredInstance().setFieldName(null),
                (ClientAllGroup)createFullyConfiguredInstance().setDataSourceField(null),
                (ClientAllGroup)createFullyConfiguredInstance().setCategorizer(null),
                (ClientAllGroup)createFullyConfiguredInstance().setIncludeAll(null)
        );
    }

    @Override
    protected ClientAllGroup createFullyConfiguredInstance() {
        ClientAllGroup instance = new ClientAllGroup();
        return (ClientAllGroup)instance
                .setId(ALL_GROUP_ID)
                .setFieldName(ALL_GROUP_ID)
                .setCategorizer(TEST_CATEGORIZER)
                .setIncludeAll(TEST_INCLUDE_ALL);
    }

    @Override
    protected ClientAllGroup createInstanceWithDefaultParameters() {
        return new ClientAllGroup();
    }

    @Override
    protected ClientAllGroup createInstanceFromOther(ClientAllGroup other) {
        return new ClientAllGroup(other);
    }
}
