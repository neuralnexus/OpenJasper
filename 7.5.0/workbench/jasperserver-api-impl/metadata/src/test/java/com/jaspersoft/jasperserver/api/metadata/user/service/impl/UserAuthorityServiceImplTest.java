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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

/**
 * Tests for {@link com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl}
 *
 * @author vsabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAuthorityServiceImplTest {

    @Spy
    private UserAuthorityServiceImpl userAuthorityService;

    @Test
    public void getDiagnosticDataTest() {
        int total_users_count = 5;
        int total_enabled_users_count = 6;
        int total_roles_count = 7;

        doReturn(total_users_count).when(userAuthorityService).getUsersCountExceptExcluded(any(), eq(null), eq(false));
        doReturn(total_enabled_users_count).when(userAuthorityService).getUsersCountExceptExcluded(any(), eq(null), eq(true));
        doReturn(total_roles_count).when(userAuthorityService).getTenantRolesCount(any(), eq(null), eq(null));
        doReturn(total_roles_count).when(userAuthorityService).getTotalRolesCount(any());

        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = userAuthorityService.getDiagnosticData();

        //Test total size of diagnostic attributes collected from UserAuthorityServiceImpl
        assertEquals(3, resultDiagnosticData.size());

        assertEquals(total_users_count, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_USERS_COUNT, null, null)).getDiagnosticAttributeValue());

        assertEquals(total_enabled_users_count, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_ENABLED_USERS_COUNT, null, null)).getDiagnosticAttributeValue());

        assertEquals(total_roles_count, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_ROLES_COUNT, null, null)).getDiagnosticAttributeValue());
    }

}
