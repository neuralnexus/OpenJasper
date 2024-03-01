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
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Volodya Sabadosh
 */
public class VirtualReportDataSourceImplTest {
    ResourceReference datasourceReference1 = mock(ResourceReference.class);
    ResourceReference datasourceReference2 = mock(ResourceReference.class);
    ResourceVisitor visitor = mock(ResourceVisitor.class);


    @Test
    public void accept_notNullDependencies_success() {
        Map<String, ResourceReference> dataSourceUriMap = new HashMap<>();
        dataSourceUriMap.put("ref1", datasourceReference1);
        dataSourceUriMap.put("ref2", datasourceReference2);
        dataSourceUriMap.put("null", null);

        VirtualReportDataSourceImpl virtualReportDataSource = new VirtualReportDataSourceImpl();
        virtualReportDataSource.setDataSourceUriMap(dataSourceUriMap);

        virtualReportDataSource.accept(visitor);

        verify(datasourceReference1, times(1)).accept(visitor);
        verify(datasourceReference2, times(1)).accept(visitor);
        verify(visitor, times(1)).visit(virtualReportDataSource);
    }

    @Test
    public void accept_nullDependencies_success() {
        VirtualReportDataSourceImpl virtualReportDataSource = new VirtualReportDataSourceImpl();

        virtualReportDataSource.accept(visitor);

        verify(visitor, times(1)).visit(virtualReportDataSource);
    }

}
