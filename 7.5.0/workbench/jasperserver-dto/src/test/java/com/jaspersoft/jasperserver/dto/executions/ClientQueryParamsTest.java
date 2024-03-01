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

package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientQueryParamsTest extends BaseDTOTest<ClientQueryParams> {

    @Override
    protected List<ClientQueryParams> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setOffset(new int[]{6, 73}),
                createFullyConfiguredInstance().setPageSize(new int[]{8}),
                // with null values
                createFullyConfiguredInstance().setOffset(null),
                createFullyConfiguredInstance().setPageSize(null)
        );
    }

    @Override
    protected ClientQueryParams createFullyConfiguredInstance() {
        ClientQueryParams clientQueryParams = new ClientQueryParams();
        clientQueryParams.setOffset(new int[]{2, 3});
        clientQueryParams.setPageSize(new int[]{4, 5});
        return clientQueryParams;
    }

    @Override
    protected ClientQueryParams createInstanceWithDefaultParameters() {
        return new ClientQueryParams();
    }

    @Override
    protected ClientQueryParams createInstanceFromOther(ClientQueryParams other) {
        return new ClientQueryParams(other);
    }
}
