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

package com.jaspersoft.jasperserver.api.common.error.handling;

import junit.framework.Assert;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

/**
 * Unit tests for {@link ExceptionOutputManagerImpl} class.
 *
 * @author dlitvak
 * @version $Id$
 */

@RunWith(MockitoJUnitRunner.class)
public class ExceptionOutputManagerImplTest {

    @Mock
    private Logger loggerMock;
    @Mock
    private Authentication authenticationMock;

    private ExceptionOutputManagerImpl outputManager;
    private Map<String, List<String>> outputControlMap;

    @Before
    public void before() {
        doReturn(true).when(authenticationMock).isAuthenticated();

        outputManager = new ExceptionOutputManagerImpl() {
            @Override
            Authentication getAuthentication() {
                return authenticationMock;
            }
        };
        outputControlMap = new LinkedHashMap<String, List<String>>() {{
            put("ERROR_UID", singletonList("ROLE_USER"));
            put("STACKTRACE", singletonList("ROLE_SUPERUSER"));
            put("MESSAGE", asList("ROLE_SUPERUSER", "ROLE_ADMINISTRATOR"));
        }};
    }

    @Test
    public void shouldReturnProperExceptionMessageAllowedFragIfUserHasPermissionToSeeExceptionMessageInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null),
                new MyTenantAwareGrantedAuthority("ROLE_ADMINISTRATOR", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isExceptionMessageAllowed = outputManager.isExceptionMessageAllowed();

        // then
        assertTrue(isExceptionMessageAllowed);
    }

    @Test
    public void shouldReturnProperExceptionMessageAllowedFragIfUserDoesNotHavePermissionToSeeExceptionMessageInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isExceptionMessageAllowed = outputManager.isExceptionMessageAllowed();

        // then
        assertFalse(isExceptionMessageAllowed);
    }

    @Test
    public void shouldReturnProperUIDOutputOnFragIfUserHasPermissionToSeeUniqueIdentifierInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isUIDOutputOn = outputManager.isUIDOutputOn();

        // then
        assertTrue(isUIDOutputOn);
    }

    @Test
    public void shouldReturnProperUIDOutputOnFragIfUserDoesNotHavePermissionToSeeUniqueIdentifierInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_SUPERUSER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isUIDOutputOn = outputManager.isUIDOutputOn();

        // then
        assertFalse(isUIDOutputOn);
    }

    @Test
    public void shouldReturnProperStackTraceAllowedFragIfUserHasPermissionToSeeStackTraceInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_SUPERUSER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isStackTraceAllowed = outputManager.isStackTraceAllowed();

        // then
        Assert.assertTrue(isStackTraceAllowed);
    }

    @Test
    public void shouldReturnProperStackTraceAllowedFragIfUserDoesNotHavePermissionToSeeStackTraceInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null),
                new MyTenantAwareGrantedAuthority("ROLE_ADMINISTRATOR", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isStackTraceAllowed = outputManager.isStackTraceAllowed();

        // then
        assertFalse(isStackTraceAllowed);
    }

    @After
    public void after() {
        reset(loggerMock, authenticationMock);
    }

    /**
     * For test purposes only.
     */
    private final class MyTenantAwareGrantedAuthority implements GrantedAuthority {
        private String authority;
        private String tenantId;

        public MyTenantAwareGrantedAuthority(String authority, String tenantId) {
            this.authority = authority;
            this.tenantId = tenantId;
        }

        @Override
        public String getAuthority() {
            return (tenantId == null) ? authority : authority + "|" + tenantId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }
    }
}
