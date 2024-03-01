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
package com.jaspersoft.jasperserver.dto.resources.domain.validation;

import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.dto.resources.domain.ConstantsResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.JoinResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.QueryResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ReferenceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.Schema;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class DomainJsonPathHelperTest {

    @Test
    public void getResourcePath(){
        List<ResourceElement> resources = Arrays.asList((ResourceElement)
                new ResourceGroupElement().setName("dataSource").setElements(Arrays.asList(
                        (SchemaElement)new ResourceGroupElement().setName("schema1"),
                        new QueryResourceGroupElement().setName("queryGroup"),
                        new ResourceGroupElement().setName("schema2").setElements(Arrays.asList(
                                (SchemaElement) new ResourceGroupElement().setName("table1"),
                                new ResourceGroupElement().setName("table2").setElements(Arrays.asList(
                                        (SchemaElement) new ResourceSingleElement().setName("column1"),
                                        new ResourceSingleElement().setName("column2"),
                                        new ResourceSingleElement().setName("column3")
                                )),
                                new ResourceGroupElement().setName("table3")
                        )),
                        new ResourceGroupElement().setName("schema3")
                )),
                new ConstantsResourceGroupElement().setName("constantsGroup1").setElements(Arrays.asList(
                        (SchemaElement) new ResourceSingleElement().setName("constant1"),
                        new ResourceSingleElement().setName("constant2"),
                        new ResourceSingleElement().setName("constant3")
                )),
                new JoinResourceGroupElement().setName("joinGroup1").setElements(Arrays.asList(
                        (SchemaElement) new ReferenceElement().setName("reference1"),
                        new ReferenceElement().setName("reference2"),
                        new ReferenceElement().setName("reference3")
                )),
                new JoinResourceGroupElement().setName("joinGroup2")
        );
        ClientDomain domain = new ClientDomain().setSchema(new Schema().setResources(resources));
        assertEquals("dataSource", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0]"));
        assertEquals("dataSource.schema1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[0]"));
        assertEquals("dataSource.queryGroup", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[1]"));
        assertEquals("dataSource.schema2", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2]"));
        assertEquals("dataSource.schema2.table1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2].elements[0]"));
        assertEquals("dataSource.schema2.table2", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2].elements[1]"));
        assertEquals("dataSource.schema2.table2.column1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2].elements[1].elements[0]"));
        assertEquals("dataSource.schema2.table2.column2", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2].elements[1].elements[1]"));
        assertEquals("dataSource.schema2.table2.column3", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2].elements[1].elements[2]"));
        assertEquals("dataSource.schema2.table3", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[2].elements[2]"));
        assertEquals("dataSource.schema3", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[0].elements[3]"));
        assertEquals("constantsGroup1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[1]"));
        assertEquals("constantsGroup1.constant1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[1].elements[0]"));
        assertEquals("constantsGroup1.constant2", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[1].elements[1]"));
        assertEquals("constantsGroup1.constant3", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[1].elements[2]"));
        assertEquals("joinGroup1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[2]"));
        assertEquals("joinGroup1.reference1", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[2].elements[0]"));
        assertEquals("joinGroup1.reference2", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[2].elements[1]"));
        assertEquals("joinGroup1.reference3", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[2].elements[2]"));
        assertEquals("joinGroup2", DomainJsonPathHelper.getResourcePath(domain, "schema.resources[3]"));
    }
    
    @Test
    public void getHierarchicalPath(){
        List<PresentationGroupElement> dataIslands = Arrays.asList(
                new PresentationGroupElement().setName("dataIsland1").setElements(Arrays.asList(
                        (PresentationElement) new PresentationGroupElement().setName("set1"),
                        new PresentationGroupElement().setName("set2").setElements(Arrays.asList(
                                (PresentationElement) new PresentationSingleElement().setName("item2")
                        )),
                        new PresentationSingleElement().setName("item1"),
                        new PresentationGroupElement().setName("set3")
                )),
                new PresentationGroupElement().setName("dataIsland2")
        );

        final ClientDomain domain = new ClientDomain().setSchema(new Schema().setPresentation(dataIslands));
        DomainJsonPathHelper.PresentationPath hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[0]");
        assertEquals("", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland1", hierarchicalPath.getDataIslandName());
        hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[0].elements[0]");
        assertEquals("set1", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland1", hierarchicalPath.getDataIslandName());
        hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[0].elements[1]");
        assertEquals("set2", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland1", hierarchicalPath.getDataIslandName());
        hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[0].elements[1].elements[0]");
        assertEquals("set2.item2", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland1", hierarchicalPath.getDataIslandName());
        hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[0].elements[2]");
        assertEquals("item1", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland1", hierarchicalPath.getDataIslandName());
        hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[0].elements[3]");
        assertEquals("set3", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland1", hierarchicalPath.getDataIslandName());
        hierarchicalPath = DomainJsonPathHelper.getPresentationPath(domain, "schema.presentation[1]");
        assertEquals("", hierarchicalPath.getHierarchicalName());
        assertEquals("dataIsland2", hierarchicalPath.getDataIslandName());
    }
}
