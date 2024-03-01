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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

public class DataSourceTableDescriptorTest extends BaseDTOPresentableTest<DataSourceTableDescriptor> {

    private static final String SCHEMA_NAME = "schema";
    private static final String DATASOURCE_TABLE_NAME = "table";

    private static final String SCHEMA_NAME_ALT = "schema1";
    private static final String DATASOURCE_TABLE_NAME_ALT = "table1";
    @Override
    protected List<DataSourceTableDescriptor> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                // fields with null values
                createFullyConfiguredInstance().setSchemaName(null),
                createFullyConfiguredInstance().setDatasourceTableName(null)
        );
    }

    @Override
    protected DataSourceTableDescriptor createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setSchemaName(SCHEMA_NAME)
                .setDatasourceTableName(DATASOURCE_TABLE_NAME);
    }

    @Override
    protected DataSourceTableDescriptor createInstanceWithDefaultParameters() {
        return new DataSourceTableDescriptor();
    }

    @Override
    protected DataSourceTableDescriptor createInstanceFromOther(DataSourceTableDescriptor other) {
        return new DataSourceTableDescriptor(other);
    }

}
