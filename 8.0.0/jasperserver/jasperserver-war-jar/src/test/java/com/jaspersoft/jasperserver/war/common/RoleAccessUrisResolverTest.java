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

package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class RoleAccessUrisResolverTest {
    private static final String ROLE_NAME = "name";
    private static final String ROLE_NAME_2 = "name2";
    private static final String ROLE_NAME_3 = "name3";
    private static final String ROLE_NAME_4 = "name4";

    private static final List<UriDescriptor> URI_DESCRIPTOR_LIST = singletonList(new UriDescriptor());
    private static final List<UriDescriptor> URI_DESCRIPTOR_LIST_2 = singletonList(new UriDescriptor());

    private RoleAccessUrisResolver objectUnderTest = new RoleAccessUrisResolver();
    private Authentication authentication = mock(Authentication.class);
    private User principal = mock(User.class);
    private Authentication previousAuthentication;
    private Role role = mock(Role.class);
    private Role role2 = mock(Role.class);

    @BeforeEach
    void setup() {
        List<RoleAccessUris> roleAccessUrisList = prepareRoleAccessUrisList();
        Set<Role> roles = new HashSet<Role>(Arrays.asList(role, role2));
        objectUnderTest.setRoleAccessUrisList(roleAccessUrisList);

        doReturn(principal).when(authentication).getPrincipal();
        doReturn(roles).when(principal).getRoles();

        previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void release() {
        SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
    }

    @Test
    void getRestrictedUris_alreadyHasRole_emptyuriDescriptorsList() {
        doReturn(ROLE_NAME).when(role).getRoleName();
        doReturn(ROLE_NAME_2).when(role2).getRoleName();

        List<UriDescriptor> result = objectUnderTest.getRestrictedUris();
        assertEquals(emptyList(), result);
    }

    @Test
    void getRestrictedUris_doesNotHaveRole_uriDescriptorsList() {
        List<UriDescriptor> expected = new ArrayList<UriDescriptor>(URI_DESCRIPTOR_LIST);
        expected.addAll(URI_DESCRIPTOR_LIST_2);

        doReturn(ROLE_NAME_3).when(role).getRoleName();
        doReturn(ROLE_NAME_4).when(role2).getRoleName();

        List<UriDescriptor> result = objectUnderTest.getRestrictedUris();
        assertEquals(expected, result);
    }

    @Test
    void getAbsoluteUri_samePath() {
        String path = "path";
        String result = objectUnderTest.getAbsoluteUri(path);
        assertEquals(path, result);
    }

    private List<RoleAccessUris> prepareRoleAccessUrisList() {
        RoleAccessUris roleAccessUris = new RoleAccessUris();
        roleAccessUris.setRoleName(ROLE_NAME);
        roleAccessUris.setUris(URI_DESCRIPTOR_LIST);

        RoleAccessUris roleAccessUris2 = new RoleAccessUris();
        roleAccessUris2.setRoleName(ROLE_NAME_2);
        roleAccessUris2.setUris(URI_DESCRIPTOR_LIST_2);

        return Arrays.asList(roleAccessUris, roleAccessUris2);
    }
}
