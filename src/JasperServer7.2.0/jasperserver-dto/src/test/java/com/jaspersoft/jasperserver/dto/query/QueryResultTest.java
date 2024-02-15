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

package com.jaspersoft.jasperserver.dto.query;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class QueryResultTest extends BaseDTOPresentableTest<QueryResult> {

    @Override
    protected List<QueryResult> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setNames(Arrays.asList("name2", "other2")),
                createFullyConfiguredInstance().setRows(Collections.singletonList(new QueryResultRow())),
                // with null values
                createFullyConfiguredInstance().setNames(null),
                createFullyConfiguredInstance().setRows(null)
        );
    }

    @Override
    protected QueryResult createFullyConfiguredInstance() {
        QueryResult queryResult = new QueryResult();
        queryResult.setNames(Arrays.asList("name", "other"));
        queryResult.setRows(Arrays.asList(new QueryResultRow(), new QueryResultRow().setValues(Collections.emptyList())));
        return queryResult;
    }

    @Override
    protected QueryResult createInstanceWithDefaultParameters() {
        return new QueryResult();
    }

    @Override
    protected QueryResult createInstanceFromOther(QueryResult other) {
        return new QueryResult(other);
    }

    @Test
    public void instanceCanBeCreatedFromParameters() {
        QueryResult result = new QueryResult(fullyConfiguredTestInstance.getNames(), ((QueryResultRow[]) fullyConfiguredTestInstance.getRows().toArray()));
        assertEquals(fullyConfiguredTestInstance, result);
    }
}
