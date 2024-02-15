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
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import static org.unitils.mock.ArgumentMatchers.eq;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ObjectPermissionServiceImplTest.java 26738 2012-12-12 15:00:54Z ykovalchyk $
 */
public class ObjectPermissionServiceImplTest extends UnitilsJUnit4 {
    @TestedObject
    private ObjectPermissionServiceImpl objectPermissionService;

    @InjectInto(property = "userService")
    private Mock<UserAuthorityService> userAuthorityServiceMock;

    @InjectInto(property = "nonMutableAclCache")
    private Mock<NonMutableAclCache> nonMutableAclCache;

    @InjectInto(property = "repoService")
    private Mock<HibernateRepositoryService> repoService;


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
        nonMutableAclCache.assertInvoked().evictFromCache(eq(new InternalURIDefinition(masterResourceUri)));
    }

}