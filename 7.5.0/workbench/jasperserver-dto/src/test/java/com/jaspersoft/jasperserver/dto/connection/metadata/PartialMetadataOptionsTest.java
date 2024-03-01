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

package com.jaspersoft.jasperserver.dto.connection.metadata;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import com.jaspersoft.jasperserver.dto.resources.DataSourceTableDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class PartialMetadataOptionsTest extends BaseDTOTest<PartialMetadataOptions> {

    private static final List<DataSourceTableDescriptor> TEST_EXPANDS = new ArrayList<DataSourceTableDescriptor>();
    private static final List<DataSourceTableDescriptor> TEST_EXPANDS_1 = new ArrayList<DataSourceTableDescriptor>();
    private static final List<DataSourceTableDescriptor> TEST_INCLUDES = new ArrayList<DataSourceTableDescriptor>();
    private static final List<DataSourceTableDescriptor> TEST_INCLUDES_1 = new ArrayList<DataSourceTableDescriptor>();
    protected void set(){
        TEST_EXPANDS.add(new DataSourceTableDescriptor().setSchemaName("EXPAND_A"));
        TEST_EXPANDS.add(new DataSourceTableDescriptor().setDatasourceTableName("EXPAND_B"));

        TEST_EXPANDS_1.add(new DataSourceTableDescriptor().setSchemaName("EXPAND_A_1"));
        TEST_EXPANDS_1.add(new DataSourceTableDescriptor().setDatasourceTableName("EXPAND_B_1"));

        TEST_INCLUDES.add(new DataSourceTableDescriptor().setSchemaName("INCLUDE_A"));
        TEST_INCLUDES.add(new DataSourceTableDescriptor().setDatasourceTableName("INCLUDE_B"));

        TEST_INCLUDES_1.add(new DataSourceTableDescriptor().setSchemaName("INCLUDE_A_1"));
        TEST_INCLUDES_1.add(new DataSourceTableDescriptor().setDatasourceTableName("INCLUDE_B_1"));
    }
    @Override
    protected List<PartialMetadataOptions> prepareInstancesWithAlternativeParameters() {
        set();
        return Arrays.asList(
                createFullyConfiguredInstance().setExpands(TEST_EXPANDS_1),
                createFullyConfiguredInstance().setIncludes(TEST_INCLUDES_1),

                // fields with null values
                createFullyConfiguredInstance().setExpands(null),
                createFullyConfiguredInstance().setIncludes(null)
        );
    }

    @Override
    protected PartialMetadataOptions createFullyConfiguredInstance() {
        set();
        return createInstanceWithDefaultParameters()
                .setExpands(TEST_EXPANDS)
                .setIncludes(TEST_INCLUDES);
    }

    @Override
    protected PartialMetadataOptions createInstanceWithDefaultParameters() {
        return new PartialMetadataOptions();
    }

    @Override
    protected PartialMetadataOptions createInstanceFromOther(PartialMetadataOptions other) {
        return new PartialMetadataOptions(other);
    }

}
