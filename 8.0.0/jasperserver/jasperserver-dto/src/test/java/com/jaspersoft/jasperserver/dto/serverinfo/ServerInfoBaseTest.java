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

package com.jaspersoft.jasperserver.dto.serverinfo;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ServerInfoBaseTest extends BaseDTOPresentableTest<ServerInfo> {

    @Override
    protected List<ServerInfo> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setBuild("build2"),
                createFullyConfiguredInstance().setVersion("version2"),
                createFullyConfiguredInstance().setDateFormatPattern("dfp2"),
                createFullyConfiguredInstance().setDatetimeFormatPattern("dtfp2"),
                createFullyConfiguredInstance().setEdition(ServerInfo.ServerEdition.PRO),
                createFullyConfiguredInstance().setEditionName("edName2"),
                createFullyConfiguredInstance().setExpiration("exp2"),
                createFullyConfiguredInstance().setLicenseType("licenseType2"),
                createFullyConfiguredInstance().setFeatures("features2"),
                // with null values
                createFullyConfiguredInstance().setBuild(null),
                createFullyConfiguredInstance().setVersion(null),
                createFullyConfiguredInstance().setDateFormatPattern(null),
                createFullyConfiguredInstance().setDatetimeFormatPattern(null),
                createFullyConfiguredInstance().setEdition(null),
                createFullyConfiguredInstance().setEditionName(null),
                createFullyConfiguredInstance().setExpiration(null),
                createFullyConfiguredInstance().setLicenseType(null),
                createFullyConfiguredInstance().setFeatures(null)
        );
    }

    @Override
    protected ServerInfo createFullyConfiguredInstance() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setBuild("build");
        serverInfo.setVersion("version");
        serverInfo.setDateFormatPattern("dfp");
        serverInfo.setDatetimeFormatPattern("dtfp");
        serverInfo.setEdition(ServerInfo.ServerEdition.CE);
        serverInfo.setEditionName("edName");
        serverInfo.setExpiration("exp");
        serverInfo.setLicenseType("licenseType");
        serverInfo.setFeatures("features");
        return serverInfo;
    }

    @Override
    protected ServerInfo createInstanceWithDefaultParameters() {
        return new ServerInfo();
    }

    @Override
    protected ServerInfo createInstanceFromOther(ServerInfo other) {
        return new ServerInfo(other);
    }

    @Test
    public void valueOfProServerEditionIsPro () {
        ServerInfo.ServerEdition result = ServerInfo.ServerEdition.valueOf("PRO");
        assertEquals(ServerInfo.ServerEdition.PRO, result);
    }
}
