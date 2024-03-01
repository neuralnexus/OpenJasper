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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceVisitor;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class ReportUnitImplTest {

    ResourceReference dataSourceReference = mock(ResourceReference.class);
    ResourceReference mainReportReference = mock(ResourceReference.class);
    ResourceReference queryReference = mock(ResourceReference.class);
    ResourceReference inputControlReference = mock(ResourceReference.class);
    ResourceReference resourceReference = mock(ResourceReference.class);

    ResourceVisitor visitor = mock(ResourceVisitor.class);

    @Test
    public void accept_notNullDependencies_success() {
        ReportUnitImpl reportUnit = new ReportUnitImpl();
        reportUnit.setDataSource(dataSourceReference);
        reportUnit.setMainReport(mainReportReference);
        reportUnit.setQuery(queryReference);
        reportUnit.setInputControls(Arrays.asList(null, inputControlReference));
        reportUnit.setResources(Arrays.asList(null, resourceReference));

        reportUnit.accept(visitor);
        verify(dataSourceReference, times(1)).accept(visitor);
        verify(mainReportReference, times(1)).accept(visitor);
        verify(queryReference, times(1)).accept(visitor);
        verify(inputControlReference, times(1)).accept(visitor);
        verify(resourceReference, times(1)).accept(visitor);

        verify(visitor, times(1)).visit(reportUnit);
    }

    @Test
    public void accept_nullDependencies_success() {
        ReportUnitImpl reportUnit = new ReportUnitImpl();

        reportUnit.accept(visitor);

        verify(visitor, times(1)).visit(reportUnit);
    }

}
