/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class NotPermissionOwnerPermissionArgumentVoterTest {
    Authentication authentication;
    ObjectPermission permission = new ObjectPermissionImpl();
    User user = new UserImpl();
    Object object;

    final NotPermissionOwnerPermissionArgumentVoter voter = new NotPermissionOwnerPermissionArgumentVoter();

    @BeforeMethod
    public void setUp() throws Exception {
        user.setUsername("name");
        user.setTenantId("tenant");

        User recipient = new UserImpl();
        recipient.setUsername(user.getUsername());
        recipient.setTenantId(user.getTenantId());

        permission.setPermissionRecipient(recipient);
        permission.setURI("repo:/");
        permission.setPermissionMask(1);
    }

    @Test
    public void testSupports_sameUser() throws Exception {
        authentication = new UsernamePasswordAuthenticationToken(user, user, new GrantedAuthority[]{new GrantedAuthorityImpl("SSS")});

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_sameName() throws Exception {
        user.setTenantId("lala");
        authentication = new UsernamePasswordAuthenticationToken(user, user, new GrantedAuthority[]{new GrantedAuthorityImpl("SSS")});

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_sameTenant() throws Exception {
        user.setUsername("lala");
        authentication = new UsernamePasswordAuthenticationToken(user, user, new GrantedAuthority[]{new GrantedAuthorityImpl("SSS")});

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_justName() throws Exception {
        user.setTenantId(null);
        authentication = new UsernamePasswordAuthenticationToken(user, user, new GrantedAuthority[]{new GrantedAuthorityImpl("SSS")});

        assertTrue(voter.isPermitted(authentication, permission, object));
    }
}
