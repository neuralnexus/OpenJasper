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

package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientMultiLevelDataset;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientMultiLevelQueryResultDataTest extends BaseDTOPresentableTest<ClientMultiLevelQueryResultData> {

    @Override
    protected List<ClientMultiLevelQueryResultData> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setTotalCounts(24),
                createFullyConfiguredInstance().setTruncated(false),
                createFullyConfiguredInstance().setQueryParams(new ClientQueryParams().setOffset(new int[]{5, 6})),
                createFullyConfiguredInstance().setDataSet(new ClientMultiLevelDataset().setCounts(24)),
                // with null values
                createFullyConfiguredInstance().setTotalCounts(null),
                createFullyConfiguredInstance().setTruncated(null),
                createFullyConfiguredInstance().setQueryParams(null),
                createFullyConfiguredInstance().setDataSet(null)
        );
    }

    @Override
    protected ClientMultiLevelQueryResultData createFullyConfiguredInstance() {
        ClientMultiLevelQueryResultData clientMultiLevelQueryResultData = new ClientMultiLevelQueryResultData();
        clientMultiLevelQueryResultData.setTotalCounts(23);
        clientMultiLevelQueryResultData.setTruncated(true);
        clientMultiLevelQueryResultData.setQueryParams(new ClientQueryParams().setOffset(new int[]{2, 3}));
        clientMultiLevelQueryResultData.setDataSet(new ClientMultiLevelDataset().setCounts(23));
        return clientMultiLevelQueryResultData;
    }

    @Override
    protected ClientMultiLevelQueryResultData createInstanceWithDefaultParameters() {
        return new ClientMultiLevelQueryResultData();
    }

    @Override
    protected ClientMultiLevelQueryResultData createInstanceFromOther(ClientMultiLevelQueryResultData other) {
        return new ClientMultiLevelQueryResultData(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiLevelQueryResultData expected, ClientMultiLevelQueryResultData actual) {
        assertNotSame(expected.getDataSet(), actual.getDataSet());
        assertNotSame(expected.getQueryParams(), actual.getQueryParams());
    }

    @Test
    public void instanceIsCreatedFromClientMultiLevelDatasetParameter() {
        ClientMultiLevelQueryResultData result = new ClientMultiLevelQueryResultData(fullyConfiguredTestInstance.getDataSet());
        assertEquals(fullyConfiguredTestInstance.getDataSet(), result.getDataSet());
    }
}
