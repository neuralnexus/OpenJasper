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

package com.jaspersoft.jasperserver.dto.jdbcdrivers;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class JdbcDriverInfoTest extends BaseDTOPresentableTest<JdbcDriverInfo> {

    @Override
    protected List<JdbcDriverInfo> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAllowSpacesInDbName(false),
                createFullyConfiguredInstance().setAvailable(false),
                createFullyConfiguredInstance().setDefault(false),
                createFullyConfiguredInstance().setDefaultValues(Arrays.asList(new ClientProperty().setValue("value2"), new ClientProperty().setKey("ley"))),
                createFullyConfiguredInstance().setJdbcDriverClass("class2"),
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setJdbcUrl("url2"),
                // with null values
                createFullyConfiguredInstance().setAllowSpacesInDbName(null),
                createFullyConfiguredInstance().setAvailable(null),
                createFullyConfiguredInstance().setDefault(null),
                createFullyConfiguredInstance().setDefaultValues(null),
                createFullyConfiguredInstance().setJdbcDriverClass(null),
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setJdbcUrl(null)
        );
    }

    @Override
    protected JdbcDriverInfo createFullyConfiguredInstance() {
        JdbcDriverInfo jdbcDriverInfo = new JdbcDriverInfo();
        jdbcDriverInfo.setAllowSpacesInDbName(true);
        jdbcDriverInfo.setAvailable(true);
        jdbcDriverInfo.setDefault(true);
        jdbcDriverInfo.setDefaultValues(Arrays.asList(new ClientProperty().setValue("value"), new ClientProperty()));
        jdbcDriverInfo.setJdbcDriverClass("class");
        jdbcDriverInfo.setName("name");
        jdbcDriverInfo.setLabel("label");
        jdbcDriverInfo.setJdbcUrl("url");
        return jdbcDriverInfo;
    }

    @Override
    protected JdbcDriverInfo createInstanceWithDefaultParameters() {
        return new JdbcDriverInfo();
    }

    @Override
    protected JdbcDriverInfo createInstanceFromOther(JdbcDriverInfo other) {
        return new JdbcDriverInfo(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(JdbcDriverInfo expected, JdbcDriverInfo actual) {
        assertNotSameCollection(expected.getDefaultValues(), actual.getDefaultValues());
    }
}
