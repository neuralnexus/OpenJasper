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

package com.jaspersoft.jasperserver.dto.adhoc.query.from;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientFromTest extends BaseDTOTest<ClientFrom> {

    private static final String DATA_SOURCE = "dataSource";
    private static final String OLAP_CUBE = "olapCube";

    private static final String DATA_SOURCE_2 = "dataSource2";
    private static final String OLAP_CUBE_2 = "olapCube2";

    @Override
    protected List<ClientFrom> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDataSource(DATA_SOURCE_2),
                createFullyConfiguredInstance().setOlapCube(OLAP_CUBE_2),
                // with null values
                createFullyConfiguredInstance().setDataSource(null),
                createFullyConfiguredInstance().setOlapCube(null)
        );
    }

    @Override
    protected ClientFrom createFullyConfiguredInstance() {
        return new ClientFrom()
                .setDataSource(DATA_SOURCE)
                .setOlapCube(OLAP_CUBE);
    }

    @Override
    protected ClientFrom createInstanceWithDefaultParameters() {
        return new ClientFrom();
    }

    @Override
    protected ClientFrom createInstanceFromOther(ClientFrom other) {
        return new ClientFrom(other);
    }

    @Test
    public void instanceCanBeCreatedFromDataSourceParameter() {
        ClientFrom instance = new ClientFrom(DATA_SOURCE);
        assertEquals(DATA_SOURCE, instance.getDataSource());
    }

    @Test
    public void instanceCanBeCreatedFromDataSourceAndOlapCubeParameters() {
        ClientFrom instance = new ClientFrom(DATA_SOURCE, OLAP_CUBE);
        assertEquals(DATA_SOURCE, instance.getDataSource());
        assertEquals(OLAP_CUBE, instance.getOlapCube());
    }
}
