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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientProvidedQueryExecutionTest extends BaseDTOPresentableTest<ClientProvidedQueryExecution> {

    @Override
    protected List<ClientProvidedQueryExecution> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDataSource(new ClientReference("uri2")),
                createFullyConfiguredInstance().setParams(new ClientQueryParams().setOffset(new int[]{3, 1})),
                // with null values
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setParams(null)
        );
    }

    @Override
    protected ClientProvidedQueryExecution createFullyConfiguredInstance() {
        ClientProvidedQueryExecution clientProvidedQueryExecution = new ClientProvidedQueryExecution();
        clientProvidedQueryExecution.setDataSource(new ClientReference("uri"));
        clientProvidedQueryExecution.setParams(new ClientQueryParams().setOffset(new int[]{2, 3}));
        return clientProvidedQueryExecution;
    }

    @Override
    protected ClientProvidedQueryExecution createInstanceWithDefaultParameters() {
        return new ClientProvidedQueryExecution();
    }

    @Override
    protected ClientProvidedQueryExecution createInstanceFromOther(ClientProvidedQueryExecution other) {
        return new ClientProvidedQueryExecution(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientProvidedQueryExecution expected, ClientProvidedQueryExecution actual) {
        assertNotSame(expected.getParams(), actual.getParams());
    }

    @Test
    public void instanceIsCreatedFromdataSourceUriParameter() {
        ClientProvidedQueryExecution result = new ClientProvidedQueryExecution(fullyConfiguredTestInstance.getDataSource());
        assertEquals(fullyConfiguredTestInstance.getDataSource(), result.getDataSource());
    }
}
