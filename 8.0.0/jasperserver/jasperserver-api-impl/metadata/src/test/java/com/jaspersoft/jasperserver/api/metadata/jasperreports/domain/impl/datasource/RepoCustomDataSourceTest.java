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

package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RepoCustomDataSourceTest {

    RepoCustomDataSource repoCustomDataSource = new RepoCustomDataSource();

    @Test
    public void getPropertyMap_NullValue() {
        Set<RepoCustomDataSourceProperty> properties = new HashSet<RepoCustomDataSourceProperty>();
        properties.add(RepoCustomDataSourceProperty.newProperty(repoCustomDataSource).setName("Schema").setValue(null));
        properties.add(RepoCustomDataSourceProperty.newProperty(repoCustomDataSource).setName("Version").setValue(""));
        properties.add(RepoCustomDataSourceProperty.newProperty(repoCustomDataSource).setName("DbName").setValue("mysql"));

        repoCustomDataSource.setProperties(properties);
        Map finalPropertyMap = repoCustomDataSource.getPropertyMap();

        Assert.assertEquals("", finalPropertyMap.get("Schema"));
        Assert.assertEquals("mysql", finalPropertyMap.get("DbName"));
        Assert.assertEquals("", finalPropertyMap.get("Version"));
    }

}

