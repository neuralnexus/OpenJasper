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

package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientReportDetailsTest extends BaseDTOPresentableTest<ClientReportDetails> {

    @Override
    protected List<ClientReportDetails> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDataSourceUri("DsUri2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setLocation("location2"),
                createFullyConfiguredInstance().setTemplate("template2"),
                // with null values
                createFullyConfiguredInstance().setDataSourceUri(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setLocation(null),
                createFullyConfiguredInstance().setTemplate(null)
        );
    }

    @Override
    protected ClientReportDetails createFullyConfiguredInstance() {
        ClientReportDetails clientReportDetails = new ClientReportDetails();
        clientReportDetails.setDataSourceUri("DsUri");
        clientReportDetails.setLabel("label");
        clientReportDetails.setLocation("location");
        clientReportDetails.setTemplate("template");
        return clientReportDetails;
    }

    @Override
    protected ClientReportDetails createInstanceWithDefaultParameters() {
        return new ClientReportDetails();
    }

    @Override
    protected ClientReportDetails createInstanceFromOther(ClientReportDetails other) {
        return new ClientReportDetails(other);
    }
}
