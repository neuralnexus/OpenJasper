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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.security.NonMutableAclCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.orm.hibernate5.HibernateTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ObjectPermissionServiceImplTest.java 26738 2012-12-12 15:00:54Z ykovalchyk $
 */
@RunWith(MockitoJUnitRunner.class)
public class ObjectPermissionServiceImplTest {
    @InjectMocks
    private ObjectPermissionServiceImpl objectPermissionService;
    @Mock
    private NonMutableAclCache nonMutableAclCache;
    @Mock
    private UserAuthorityService userAuthorityServiceMock;
    @Mock
    private HibernateRepositoryService repoService;

    @Test
    public void putObjectPermission() {
        IllegalArgumentException exception = null;
        try {
            objectPermissionService.putObjectPermission(null, null);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        // Permission can't be null
        Assert.assertNotNull(exception);
        exception = null;
        ObjectPermission objectPermission = new ObjectPermissionImpl();
        try {
            objectPermissionService.putObjectPermission(null, objectPermission);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        //Permission recipient can't be null
        Assert.assertNotNull(exception);
        exception = null;
        objectPermission.setPermissionRecipient(new Object());
        try {
            objectPermissionService.putObjectPermission(null, objectPermission);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        //Unknown type of permissionRecipient
        Assert.assertNotNull(exception);

        User user = new UserImpl();
        exception = null;
        objectPermission.setPermissionRecipient(user);
        try {
            objectPermissionService.putObjectPermission(null, objectPermission);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        //User name can't be null
        Assert.assertNotNull(exception);

        exception = null;
        user.setUsername("someUserName");
        objectPermission.setPermissionRecipient(user);
        try {
            objectPermissionService.putObjectPermission(null, objectPermission);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        //User 'someUserName' doesn't exists and can't be used as permissionRecipient for permission
        Assert.assertNotNull(exception);

        exception = null;
        Role role = new RoleImpl();
        objectPermission.setPermissionRecipient(role);
        try {
            objectPermissionService.putObjectPermission(null, objectPermission);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        //Role name can't be null
        Assert.assertNotNull(exception);
        exception = null;
        role.setRoleName("someRoleName");
        objectPermission.setPermissionRecipient(role);
        try {
            objectPermissionService.putObjectPermission(null, objectPermission);
        } catch (IllegalArgumentException ex) {
            exception = ex;
        }
        //Role 'someRoleName' doesn't exists and can't be used as permissionRecipient for permission
        Assert.assertNotNull(exception);
    }

    @Test
    public void clearAclEntriesCache_success() {
        String masterResourceUri = "/f1/f2/Resource";
        objectPermissionService.clearAclEntriesCache(masterResourceUri);
        verify(nonMutableAclCache, times(1)).evictFromCache(eq(new InternalURIDefinition(masterResourceUri)));
    }

    @Test
    public void updateObjectPermissionRepositoryPath_shouldClearCacheInBothPlaces() {
        ResourceFactory persistentClassFactory = mock(ResourceFactoryImpl.class);
        when(persistentClassFactory.getImplementationClassName(ObjectPermission.class)).thenReturn(ObjectPermission.class.getName());
        objectPermissionService.setPersistentClassFactory(persistentClassFactory);

        HibernateTemplate hibernateTemplate = mock(HibernateTemplate.class);
        when(hibernateTemplate.findByNamedParam(anyString(), anyString(), any())).thenReturn(null);
        objectPermissionService.setHibernateTemplate(hibernateTemplate);

        String oldResourceUri = "/f1/Resource";
        String newResourceUri = "/f1/f2/Resource";
        objectPermissionService.updateObjectPermissionRepositoryPath(oldResourceUri, newResourceUri);

        verify(nonMutableAclCache).evictFromCache(eq(new InternalURIDefinition(oldResourceUri)));
        verify(nonMutableAclCache).evictFromCache(eq(new InternalURIDefinition(newResourceUri)));
    }

}