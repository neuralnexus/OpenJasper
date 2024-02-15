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

package com.jaspersoft.jasperserver.dto.resources.domain;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class JoinInfoTest extends BaseDTOTest<JoinInfo> {

    @Override
    protected List<JoinInfo> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setIncludeAllDataIslandJoins(false),
                createFullyConfiguredInstance().setIncludeAllJoinsForQueryFieldTables(false),
                createFullyConfiguredInstance().setJoins(Arrays.asList(new Join(), new Join().setLeft("left2"))),
                createFullyConfiguredInstance().setMandatoryTables(Arrays.asList("table2", "mandatory2")),
                createFullyConfiguredInstance().setSuppressCircularJoins(false),
                // with null values
                createFullyConfiguredInstance().setIncludeAllDataIslandJoins(null),
                createFullyConfiguredInstance().setIncludeAllJoinsForQueryFieldTables(null),
                createFullyConfiguredInstance().setJoins(null),
                createFullyConfiguredInstance().setMandatoryTables(null),
                createFullyConfiguredInstance().setSuppressCircularJoins(null)
        );
    }

    @Override
    protected JoinInfo createFullyConfiguredInstance() {
        JoinInfo joinInfo = new JoinInfo();
        joinInfo.setIncludeAllDataIslandJoins(true);
        joinInfo.setIncludeAllJoinsForQueryFieldTables(true);
        joinInfo.setJoins(Arrays.asList(new Join(), new Join().setLeft("left")));
        joinInfo.setMandatoryTables(Arrays.asList("table", "mandatory"));
        joinInfo.setSuppressCircularJoins(true);
        return joinInfo;
    }

    @Override
    protected JoinInfo createInstanceWithDefaultParameters() {
        return new JoinInfo();
    }

    @Override
    protected JoinInfo createInstanceFromOther(JoinInfo other) {
        return new JoinInfo(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(JoinInfo expected, JoinInfo actual) {
        assertNotSameCollection(expected.getJoins(), actual.getJoins());
    }
}
