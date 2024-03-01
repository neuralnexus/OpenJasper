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

package com.jaspersoft.jasperserver.dto.resources.domain;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
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
public class ResourceGroupElementTest extends BaseDTOPresentableTest<ResourceGroupElement> {

    @Override
    protected List<ResourceGroupElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setKind("kind2"),
                createFullyConfiguredInstance().setSourceName("sourcingName2"),
                createFullyConfiguredInstance().setElements(Arrays.<SchemaElement>asList(new QueryResourceGroupElement(), new QueryResourceGroupElement().setName("name2"))),
                createFullyConfiguredInstance().setFilterExpression(new ClientExpressionContainer().setObject(new ClientString("filterExpression2"))),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setKind(null),
                createFullyConfiguredInstance().setSourceName(null),
                createFullyConfiguredInstance().setElements(null),
                createFullyConfiguredInstance().setFilterExpression(null)
        );
    }

    @Override
    protected ResourceGroupElement createFullyConfiguredInstance() {
        return new ResourceGroupElement()
                .setName("name")
                .setKind("kind")
                .setSourceName("sourcingName")
                .setElements(Arrays.<SchemaElement>asList(new QueryResourceGroupElement(), new QueryResourceGroupElement().setName("name")))
                .setFilterExpression(new ClientExpressionContainer().setObject(new ClientString("filterExpression")));
    }

    @Override
    protected ResourceGroupElement createInstanceWithDefaultParameters() {
        return new ResourceGroupElement();
    }

    @Override
    protected ResourceGroupElement createInstanceFromOther(ResourceGroupElement other) {
        return new ResourceGroupElement(other);
    }

}