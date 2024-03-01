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

package com.jaspersoft.jasperserver.dto.resources.domain;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 * @see
 */
public class QueryResourceGroupElementTest extends BaseDTOTest<QueryResourceGroupElement> {

    @Override
    protected List<QueryResourceGroupElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setQuery("query2"),
                createFullyConfiguredInstance().setSourceName("sourceName2"),
                createFullyConfiguredInstance().setElements(Arrays.asList(new SchemaElement(), new ResourceSingleElement().setName("name2"))),
                createFullyConfiguredInstance().setFilterExpression(new ClientExpressionContainer().setString("filterExpression2")),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setQuery(null),
                createFullyConfiguredInstance().setSourceName(null),
                createFullyConfiguredInstance().setElements(null),
                createFullyConfiguredInstance().setFilterExpression(null)
        );
    }

    @Override
    protected QueryResourceGroupElement createFullyConfiguredInstance() {
        return new QueryResourceGroupElement()
                .setName("name")
                .setQuery("query")
                .setSourceName("sourceName")
                .setElements(Arrays.asList(new SchemaElement(), new ResourceSingleElement().setName("name")))
                .setFilterExpression(new ClientExpressionContainer().setString("filterExpression"));
    }

    @Override
    protected QueryResourceGroupElement createInstanceWithDefaultParameters() {
        return new QueryResourceGroupElement();
    }

    @Override
    protected QueryResourceGroupElement createInstanceFromOther(QueryResourceGroupElement other) {
        return new QueryResourceGroupElement(other);
    }
}