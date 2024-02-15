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
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.util.Arrays;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class ReportUnitImplTest extends UnitilsJUnit4 {
    Mock<ResourceReference> dataSourceReference;
    Mock<ResourceReference> mainReportReference;
    Mock<ResourceReference> queryReference;
    Mock<ResourceReference> inputControlReference;
    Mock<ResourceReference> resourceReference;

    Mock<ResourceVisitor> visitor;

    @Test
    public void accept_notNullDependencies_success() {
        ReportUnitImpl reportUnit = new ReportUnitImpl();
        reportUnit.setDataSource(dataSourceReference.getMock());
        reportUnit.setMainReport(mainReportReference.getMock());
        reportUnit.setQuery(queryReference.getMock());
        reportUnit.setInputControls(Arrays.asList(null, inputControlReference.getMock()));
        reportUnit.setResources(Arrays.asList(null, resourceReference.getMock()));

        reportUnit.accept(visitor.getMock());
        dataSourceReference.assertInvoked().accept(visitor.getMock());
        mainReportReference.assertInvoked().accept(visitor.getMock());
        queryReference.assertInvoked().accept(visitor.getMock());
        inputControlReference.assertInvoked().accept(visitor.getMock());
        resourceReference.assertInvoked().accept(visitor.getMock());

        visitor.assertInvoked().visit(reportUnit);
    }

    @Test
    public void accept_nullDependencies_success() {
        ReportUnitImpl reportUnit = new ReportUnitImpl();

        reportUnit.accept(visitor.getMock());

        visitor.assertInvoked().visit(reportUnit);
    }

}
