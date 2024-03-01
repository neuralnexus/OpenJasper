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

package com.jaspersoft.jasperserver.dto.adhoc.query.group;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientQueryGroupByTest extends BaseDTOPresentableTest<ClientQueryGroupBy> {
    private static final List<ClientQueryGroup> GROUPS = Collections.singletonList(new ClientQueryGroup().setId("id"));
    private static final List<ClientQueryGroup> GROUPS_ALTERNATIVE = Collections.singletonList(new ClientQueryGroup().setId("idAlternative"));
    private static final List<ClientQueryGroup> GROUPS_ALTERNATIVE2 = Collections.singletonList(
            new ClientQueryGroup().setId("idAlternative").setExpressionContainer(
                    new ClientExpressionContainer("sum(field1)")));

    private static final ClientGroupAxis CLIENT_GROUP_AXIS = new ClientGroupAxis(GROUPS);
    private static final List<ClientGroupAxis> CLIENT_GROUP_AXIS_LIST = Collections.singletonList(CLIENT_GROUP_AXIS);

    @Override
    protected List<ClientQueryGroupBy> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setGroups(GROUPS_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setGroups(null),
                // group with expression
                createFullyConfiguredInstance().setGroups(GROUPS_ALTERNATIVE2)
        );
    }

    @Override
    protected ClientQueryGroupBy createFullyConfiguredInstance() {
        return new ClientQueryGroupBy()
                .setGroups(GROUPS);
    }

    @Override
    protected ClientQueryGroupBy createInstanceWithDefaultParameters() {
        return new ClientQueryGroupBy();
    }

    @Override
    protected ClientQueryGroupBy createInstanceFromOther(ClientQueryGroupBy other) {
        return new ClientQueryGroupBy(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientQueryGroupBy expected, ClientQueryGroupBy actual) {
        assertNotSameCollection(expected.getGroups(), actual.getGroups());
    }

    @Test
    public void getAxisReturnGroupsForGroupsName() {
        ClientQueryGroupBy instance = createFullyConfiguredInstance();

        ClientGroupAxis result = instance.getAxis(ClientGroupAxisEnum.GROUPS);

        assertEquals(CLIENT_GROUP_AXIS, result);
    }

    @Test
    public void getAxisThrowExceptionForRowsName() {
        final ClientQueryGroupBy instance = createFullyConfiguredInstance();

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.getAxis(ClientGroupAxisEnum.ROWS);
            }
        });
    }

    @Test
    public void getAxisReturnGroupsFor0Index() {
        ClientQueryGroupBy instance = createFullyConfiguredInstance();
        int axisIndex = 0;

        ClientGroupAxis result = instance.getAxis(axisIndex);

        assertEquals(CLIENT_GROUP_AXIS, result);
    }

    @Test
    public void getAxisThrowExceptionFor1Index() {
        final ClientQueryGroupBy instance = createFullyConfiguredInstance();
        final int axisIndex = 1;

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.getAxis(axisIndex);
            }
        });
    }

    @Test
    public void getAxesReturnsSingletonListOfGroupAsix() {
        ClientQueryGroupBy instance = createFullyConfiguredInstance();

        List<ClientGroupAxis> result = instance.getAxes();

        assertEquals(CLIENT_GROUP_AXIS_LIST, result);
    }

    @Test
    public void nullTest() {
        Exception ex = null;
        try {
            ClientQueryGroup clientQueryGroup = new ClientQueryGroup((ClientQueryGroup) null);
        } catch (Exception ex2) {
            ex = ex2;
        }
        assertTrue(ex != null);
    }

}
