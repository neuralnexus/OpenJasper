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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class QueryResultRowTest extends BaseDTOTest<QueryResultRow> {

    @Override
    protected List<QueryResultRow> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setValues(Arrays.<Object>asList((""), "other2")),
                // with null values
                createFullyConfiguredInstance().setValues(null)
        );
    }

    @Override
    protected QueryResultRow createFullyConfiguredInstance() {
        QueryResultRow queryResultRow = new QueryResultRow();
        queryResultRow.setValues(Arrays.asList(((Object) ""), "other"));
        return queryResultRow;
    }

    @Override
    protected QueryResultRow createInstanceWithDefaultParameters() {
        return new QueryResultRow();
    }

    @Override
    protected QueryResultRow createInstanceFromOther(QueryResultRow other) {
        return new QueryResultRow(other);
    }

    @Test
    public void instanceCanBeCreatedFromParameters() {
        QueryResultRow result = new QueryResultRow();
        result.getValues().addAll(fullyConfiguredTestInstance.getValues());
        assertEquals(fullyConfiguredTestInstance, result);
    }
}
