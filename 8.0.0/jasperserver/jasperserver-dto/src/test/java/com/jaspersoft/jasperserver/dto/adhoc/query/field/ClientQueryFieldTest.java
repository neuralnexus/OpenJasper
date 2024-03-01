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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientQueryFieldTest extends BaseDTOJSONPresentableTest<ClientQueryField> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_ALT = "TEST_ID_ALT";

    private static final String TEST_FIELD_NAME = "TEST_FILENAME";
    private static final String TEST_FIELD_NAME_ALT = "TEST_FILENAME_ALT";

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_ALT = "TEST_TYPE_ALT";

    // At the moment ClientDataSourceField and all its subclasses have measure = false;
    private static final ClientDataSourceField TEST_DATASOURCE_FIELD = new ClientDataSourceField() {
        @Override
        public boolean isMeasure() {
            return true;
        }
    }.setType(TEST_TYPE).setName(TEST_FIELD_NAME);
    private static final ClientDataSourceField TEST_DATASOURCE_FIELD_ALT = new ClientDataSourceField() {
        @Override
        public boolean isMeasure() {
            return true;
        }
    }.setName(TEST_FIELD_NAME_ALT);
    private static final ClientDataSourceField TEST_DATASOURCE_FIELD_ALT_2 = new ClientDataSourceField().setType(TEST_TYPE_ALT).setName(TEST_FIELD_NAME_ALT);

    @Override
    protected List<ClientQueryField> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_ALT),
                createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD),
                createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT),
                createFullyConfiguredInstance().setDataSourceField(TEST_DATASOURCE_FIELD_ALT_2),
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setFieldName(null),
                createFullyConfiguredInstance().setDataSourceField(null),
                createFullyConfiguredInstance().setExpressionContainer(
                        new ClientExpressionContainer("sum(field1)"))
        );
    }

    @Override
    protected ClientQueryField createFullyConfiguredInstance() {
        return new ClientQueryField()
                .setId(TEST_ID)
                .setFieldName(TEST_FIELD_NAME);
    }

    @Override
    protected ClientQueryField createInstanceWithDefaultParameters() {
        return new ClientQueryField();
    }

    @Override
    protected ClientQueryField createInstanceFromOther(ClientQueryField other) {
        return new ClientQueryField(other);
    }

    @Test
    public void nullTest() {
        Exception ex = null;
        try {
            ClientQueryField clientQueryField = new ClientQueryField((ClientQueryField) null);
        } catch (Exception ex2) {
            ex = ex2;
        }
        assertTrue(ex != null);
    }
}