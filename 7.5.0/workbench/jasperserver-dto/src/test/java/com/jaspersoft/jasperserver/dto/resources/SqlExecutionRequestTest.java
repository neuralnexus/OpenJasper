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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class SqlExecutionRequestTest extends BaseDTOPresentableTest<SqlExecutionRequest> {

    @Override
    protected List<SqlExecutionRequest> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setSql("sql2"),
                createFullyConfiguredInstance().setDataSourceUri("dataDourceUri2"),
                createFullyConfiguredInstance().setCreationDate("creationDate2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setPermissionMask(24),
                createFullyConfiguredInstance().setUri("uri2"),
                createFullyConfiguredInstance().setVersion(24),
                createFullyConfiguredInstance().setUpdateDate("updateDate2"),
                // fields with null values
                createFullyConfiguredInstance().setSql(null),
                createFullyConfiguredInstance().setDataSourceUri(null),
                createFullyConfiguredInstance().setCreationDate(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setUpdateDate(null)
        );
    }

    @Override
    protected SqlExecutionRequest createFullyConfiguredInstance() {
        SqlExecutionRequest SqlExecutionRequest = new SqlExecutionRequest();
        SqlExecutionRequest.setSql("sql");
        SqlExecutionRequest.setDataSourceUri("dataDourceUri");
        SqlExecutionRequest.setCreationDate("creationDate");
        SqlExecutionRequest.setDescription("description");
        SqlExecutionRequest.setLabel("label");
        SqlExecutionRequest.setPermissionMask(23);
        SqlExecutionRequest.setUri("uri");
        SqlExecutionRequest.setVersion(23);
        SqlExecutionRequest.setUpdateDate("updateDate");
        return SqlExecutionRequest;
    }

    @Override
    protected SqlExecutionRequest createInstanceWithDefaultParameters() {
        return new SqlExecutionRequest();
    }

    @Override
    protected SqlExecutionRequest createInstanceFromOther(SqlExecutionRequest other) {
        return new SqlExecutionRequest(other);
    }
}
