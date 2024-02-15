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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.InternalURIDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.Authentication;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class IsAdministrableObjectPermissionArgumentVoterTest {
    Authentication authentication;
    ObjectPermission permission = new ObjectPermissionImpl();
    User user = new UserImpl();
    Object object;

    @InjectMocks
    IsAdministrableObjectPermissionArgumentVoter voter = new IsAdministrableObjectPermissionArgumentVoter();
    @Mock AclProvider provider;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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
    public void testSupports() throws Exception {
        BasicAclEntry[] entries = new BasicAclEntry[1];
        entries[0] = new SimpleAclEntry(user, new InternalURIDefinition(permission.getURI()), new InternalURIDefinition(permission.getURI()), JasperServerAclEntry.ADMINISTRATION);

        when(provider.getAcls(permission.getURI(), authentication)).thenReturn(entries);

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_not() throws Exception {
        BasicAclEntry[] entries = new BasicAclEntry[1];
        entries[0] = new SimpleAclEntry(user, new InternalURIDefinition(permission.getURI()), new InternalURIDefinition(permission.getURI()), JasperServerAclEntry.READ);

        when(provider.getAcls(permission.getURI(), authentication)).thenReturn(entries);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_notMany() throws Exception {
        BasicAclEntry[] entries = new BasicAclEntry[2];
        entries[0] = new SimpleAclEntry(user, new InternalURIDefinition(permission.getURI()), new InternalURIDefinition(permission.getURI()), JasperServerAclEntry.READ);
        entries[1] = new SimpleAclEntry(user, new InternalURIDefinition(permission.getURI()), new InternalURIDefinition(permission.getURI()), JasperServerAclEntry.WRITE);

        when(provider.getAcls(permission.getURI(), authentication)).thenReturn(entries);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_many() throws Exception {
        BasicAclEntry[] entries = new BasicAclEntry[2];
        entries[0] = new SimpleAclEntry(user, new InternalURIDefinition(permission.getURI()), new InternalURIDefinition(permission.getURI()), JasperServerAclEntry.READ);
        entries[1] = new SimpleAclEntry(user, new InternalURIDefinition(permission.getURI()), new InternalURIDefinition(permission.getURI()), JasperServerAclEntry.ADMINISTRATION);

        when(provider.getAcls(permission.getURI(), authentication)).thenReturn(entries);

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_empty() throws Exception {
        BasicAclEntry[] entries = new BasicAclEntry[0];

        when(provider.getAcls(permission.getURI(), authentication)).thenReturn(entries);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_null() throws Exception {
        when(provider.getAcls(permission.getURI(), authentication)).thenReturn(null);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }


}
