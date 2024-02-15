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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceVisitor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Volodya Sabadosh
 */
public class VirtualReportDataSourceImplTest extends UnitilsJUnit4 {
    Mock<ResourceReference> datasourceReference1;
    Mock<ResourceReference> datasourceReference2;
    Mock<ResourceVisitor> visitor;


    @Test
    public void accept_notNullDependencies_success() {
        Map<String, ResourceReference> dataSourceUriMap = new HashMap<>();
        dataSourceUriMap.put("ref1", datasourceReference1.getMock());
        dataSourceUriMap.put("ref2", datasourceReference2.getMock());
        dataSourceUriMap.put("null", null);

        VirtualReportDataSourceImpl virtualReportDataSource = new VirtualReportDataSourceImpl();
        virtualReportDataSource.setDataSourceUriMap(dataSourceUriMap);

        virtualReportDataSource.accept(visitor.getMock());

        datasourceReference1.assertInvoked().accept(visitor.getMock());
        datasourceReference2.assertInvoked().accept(visitor.getMock());
        visitor.assertInvoked().visit(virtualReportDataSource);
    }

    @Test
    public void accept_nullDependencies_success() {
        VirtualReportDataSourceImpl virtualReportDataSource = new VirtualReportDataSourceImpl();

        virtualReportDataSource.accept(visitor.getMock());

        visitor.assertInvoked().visit(virtualReportDataSource);
    }

}
