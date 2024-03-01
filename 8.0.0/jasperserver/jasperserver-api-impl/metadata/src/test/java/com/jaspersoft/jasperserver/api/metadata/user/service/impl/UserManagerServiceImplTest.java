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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class UserManagerServiceImplTest {

    @InjectMocks
    private UserManagerServiceImpl managerService;

    @Mock
    private UserAuthorityService userService;

    String demo;
    String first;
    String second; 

    User demoUser;
    User firstUser;
    User secondUser;

    Role demoRole;
    Role firstRole;
    Role secondRole;


    @Before
    public void before() {
        demo = "demo";
        first = "first";
        second = "second";

        demoUser = new UserImpl();
        demoUser.setUsername(demo);

        firstUser = new UserImpl();
        firstUser.setUsername(first);
        firstUser.setEnabled(true);

        secondUser = new UserImpl();
        secondUser.setUsername(second);
        secondUser.setEnabled(false);        

        demoRole = new RoleImpl();
        demoRole.setRoleName(demo);

        firstRole = new RoleImpl();
        firstRole.setRoleName(first);

        secondRole = new RoleImpl();
        secondRole.setRoleName(second);

        reset(userService);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void enableAll() {
        List<String> users = new ArrayList<String>();
        users.add(demo);
        users.add(first);
        users.add(second);

        when(userService.getUser(null, demo)).thenReturn(clone(demoUser));
        when(userService.getUser(null, first)).thenReturn(clone(firstUser));
        when(userService.getUser(null, second)).thenReturn(clone(secondUser));

        managerService.enableAll(null, users);

        verify(userService, times(1)).getUser(null, demo);

        ArgumentCaptor<User> updatedDemoUser = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).updateUser(eq(null), eq(demo), updatedDemoUser.capture());
        assertNotEquals(demoUser.isEnabled(), updatedDemoUser.getValue().isEnabled());

        verify(userService, times(1)).getUser(null, first);

        verify(userService, times(1)).getUser(null, second);

        ArgumentCaptor<User> updatedSecondUser = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).updateUser(eq(null), eq(second), updatedSecondUser.capture());
        assertNotEquals(secondUser.isEnabled(), updatedSecondUser.getValue().isEnabled());

    }

    @Test
    public void enableAllIfNull() {
        // check if null
        managerService.enableAll(null, null);
    }

    @Test
    public void enableAllIfEmptyList() {
        managerService.enableAll(null, new ArrayList<String>());
    }

    @Test
    public void enableAllIfListWithNullAndEmptyValue() {
        List<String> users = new ArrayList<String>();
        users.add(null);
        users.add("");

        managerService.enableAll(null, users);
    }

    @Test
    public void disableAll() {
        List<String> users = new ArrayList<String>();
        users.add(demo);
        users.add(first);
        users.add(second);

        when(userService.getUser(null, demo)).thenReturn(clone(demoUser));
        when(userService.getUser(null, first)).thenReturn(clone(firstUser));
        when(userService.getUser(null, second)).thenReturn(clone(secondUser));

        managerService.disableAll(null, users);

        verify(userService, times(1)).getUser(null, demo);

        verify(userService, times(1)).getUser(null, first);

        ArgumentCaptor<User> updatedFirstUser = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).updateUser(eq(null), eq(first), updatedFirstUser.capture());
        assertNotEquals(firstUser.isEnabled(), updatedFirstUser.getValue().isEnabled());

        verify(userService, times(1)).getUser(null, second);
    }

    private void checkUserUpdated(User expected, User actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertNotEquals(expected.isEnabled(), actual.isEnabled());
    }

    @Test
    public void disableAllIfNull() {
        managerService.disableAll(null, null);
    }

    @Test
    public void disableAllIfEmptyList() {
        managerService.disableAll(null, new ArrayList<String>());
    }

    @Test
    public void disableAllIfListWithNullAndEmptyValue() {
        List<String> users = new ArrayList<String>();
        users.add(null);
        users.add("");

        managerService.disableAll(null, users);
    }

    @Test
    public void deleteAll() {
        // in ideal case
        List<String> users = new ArrayList<String>();
        users.add(demo);
        users.add(first);
        users.add(second);

        managerService.deleteAll(null, users);
        
        verify(userService, times(1)).deleteUser(null, demo);
        verify(userService, times(1)).deleteUser(null, first);
        verify(userService, times(1)).deleteUser(null, second);
    }

    @Test
    public void deleteAllIfNull() {
        managerService.deleteAll(null, null);
    }

    @Test
    public void deleteAllIfEmptyList() {
        managerService.deleteAll(null, new ArrayList<String>());
    }

    @Test
    public void deleteAllIfListWithNullAndEmptyValue() {
        List<String> users = new ArrayList<String>();
        users.add(null);
        users.add("");

        managerService.deleteAll(null, users);
    }

//    @Test
    public void updateUser() {
        Set assignedNameSet = new HashSet();
        Set unassignedNameSet = new HashSet();

        assignedNameSet.add(first);
        assignedNameSet.add(second);

        unassignedNameSet.add(demo);

        List<Role> available = new ArrayList<Role>();
        Set<Role> assignedSet = new HashSet<Role>();
        Set<Role> unassignedSet = new HashSet<Role>();

        available.add(firstRole);
        available.add(secondRole);

        assignedSet.add(firstRole);
        assignedSet.add(secondRole);

        unassignedSet.add(demoRole);

        FilterCriteria filter = FilterCriteria.createFilter(Role.class);

        when(userService.getUser(null, demo)).thenReturn(clone(demoUser));
        when(userService.getRoles(null, filter)).thenReturn(available);

        managerService.updateUser(null, demo, demoUser, assignedSet, unassignedSet);

        verify(userService, times(1)).getUser(null, demo);
        verify(userService, times(1)).getRoles(null, filter);
        verify(userService, times(1)).updateUser(null, eq(demo), eq(demoUser));
    }


    private User clone(User user) {
        UserImpl clone = new UserImpl();

        clone.setUsername(user.getUsername());
        clone.setTenantId(user.getTenantId());
        clone.setEnabled(user.isEnabled());

        return clone;
    }

}
