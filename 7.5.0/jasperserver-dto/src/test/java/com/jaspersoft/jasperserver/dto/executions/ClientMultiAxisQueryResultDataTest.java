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

import com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientMultiAxisDataset;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */
public class ClientMultiAxisQueryResultDataTest extends BaseDTOPresentableTest<ClientMultiAxisQueryResultData> {

    @Override
    protected List<ClientMultiAxisQueryResultData> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setTotalCounts(Arrays.asList(3, 4, 1)),
                createFullyConfiguredInstance().setTruncated(false),
                createFullyConfiguredInstance().setQueryParams(new ClientQueryParams().setOffset(new int[]{5, 6})),
                createFullyConfiguredInstance().setDataSet(new ClientMultiAxisDataset().setCounts(Arrays.asList(1, 3, 4)).setData(Collections.singletonList(new String[]{"1", "3"}))),
                // with null values
                createFullyConfiguredInstance().setTotalCounts(null),
                createFullyConfiguredInstance().setTruncated(null),
                createFullyConfiguredInstance().setQueryParams(null),
                createFullyConfiguredInstance().setDataSet(null)
        );
    }

    @Override
    protected ClientMultiAxisQueryResultData createFullyConfiguredInstance() {
        ClientMultiAxisQueryResultData clientMultiAxisQueryResultData = new ClientMultiAxisQueryResultData();
        clientMultiAxisQueryResultData.setTotalCounts(Arrays.asList(2, 3, 4));
        clientMultiAxisQueryResultData.setTruncated(true);
        clientMultiAxisQueryResultData.setQueryParams(new ClientQueryParams().setOffset(new int[]{2, 3}));
        clientMultiAxisQueryResultData.setDataSet(new ClientMultiAxisDataset().setCounts(Arrays.asList(2, 3, 4)).setData(Collections.singletonList(new String[]{"2", "3", "4"})));
        return clientMultiAxisQueryResultData;
    }

    @Override
    protected ClientMultiAxisQueryResultData createInstanceWithDefaultParameters() {
        return new ClientMultiAxisQueryResultData();
    }

    @Override
    protected ClientMultiAxisQueryResultData createInstanceFromOther(ClientMultiAxisQueryResultData other) {
        return new ClientMultiAxisQueryResultData(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientMultiAxisQueryResultData expected, ClientMultiAxisQueryResultData actual) {
        assertNotSame(expected.getDataSet(), actual.getDataSet());
        assertNotSame(expected.getQueryParams(), actual.getQueryParams());
    }

    @Test
    public void instanceIsCreatedFromClientMultiAxisDatasetParameter() {
        ClientMultiAxisQueryResultData result = new ClientMultiAxisQueryResultData(fullyConfiguredTestInstance.getDataSet());
        assertEquals(fullyConfiguredTestInstance.getDataSet(), result.getDataSet());
    }
}
