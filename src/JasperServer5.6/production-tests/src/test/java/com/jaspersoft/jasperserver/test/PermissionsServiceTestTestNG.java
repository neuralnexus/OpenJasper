/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
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
package com.jaspersoft.jasperserver.test;

import java.util.Iterator;
import java.util.List;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.springframework.security.context.SecurityContextHolder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.AclService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * @author swood
 *
 */
public class PermissionsServiceTestTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(PermissionsServiceTestTestNG.class);

    public static String reportsFolderPath = Folder.SEPARATOR + "reports";
    public static String reportUnitFolder = reportsFolderPath + Folder.SEPARATOR + "samples";
    public static String reportUnitName = "AllAccounts";
    public static String reportUnitPath = reportUnitFolder + Folder.SEPARATOR + reportUnitName;

    public static final String USER_ADMIN = "admin";
    public static String USER_TEST = "TestUser";
    public static String ROLE_TEST = "ROLE_TEST";

    ReportUnit m_unit;
    Role m_userRole, m_adminRole, m_testRole;
    User m_adminUser, m_testUser;
    Folder m_reportsFolder;

    public PermissionsServiceTestTestNG(){
        m_logger.info("PermissionsServiceTestTestNG => constructor() called");
    }

    @BeforeClass()
    protected void onSetUp() throws Exception {
        m_logger.info("PermissionsServiceTestTestNG => onSetUp() called");

        // setup the test roles and test users
        setUpRoles();
        setUpUsers();

        // set the admin user to be the authenticated user
        setAuthenticatedUser(USER_ADMIN);
        createObjectPermission("/", m_adminRole, SimpleAclEntry.ADMINISTRATION);

        m_reportsFolder = getRepositoryService().getFolder(null, reportsFolderPath);
        assertTrue("null m_reportsFolder", m_reportsFolder != null);

        // Set up some data
        m_unit = (ReportUnit) getRepositoryService().getResource(null, reportUnitPath);
        assertNotNull("Null ReportUnit", m_unit);
        assertEquals("Unmatched ReportUnit name", reportUnitName, m_unit.getName());

    }

    /**
     * setUpRoles
     *      - gets the userRole (created in the CoreDataCreateTestNG class)
     *      - gets the adminRole (created in the CoreDataCreateTestNG class)
     *      - creates a new testRole
     */
    private void setUpRoles() {
        m_userRole = getRole(BaseServiceSetupTestNG.ROLE_USER);
        m_adminRole = getRole(BaseServiceSetupTestNG.ROLE_ADMINISTRATOR);
        m_testRole = createRole(ROLE_TEST);
    }

    /**
     * setUpUsers
     *      - creates a new testUser
     *      - adds a testRole to the new testUser
     *      - creates a new adminUser
     */
    private void setUpUsers() {
        m_testUser = createUser(USER_TEST, USER_TEST, USER_TEST);
        getUserAuthorityService().addRole(null, m_testUser, m_testRole);
        getUserAuthorityService().addRole(null, m_testUser, m_adminRole);

        //m_adminUser = createUser(USER_ADMIN, USER_ADMIN, USER_ADMIN);
        //getUserAuthorityService().addRole(null, m_adminUser, m_adminRole);
    }

    @AfterClass()
    public void onTearDown() {
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() called");

        setAuthenticatedUser(USER_ADMIN);

        // Remove block permissions at the folder level
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - Deleting permission for " + BaseServiceSetupTestNG.ROLE_USER + " and " + BaseServiceSetupTestNG.ROLE_ADMINISTRATOR + " for " + reportUnitFolder);

        deleteObjectPermission(reportUnitFolder, m_testRole);
        deleteObjectPermission(reportsFolderPath, m_adminRole);

        // Remove block permissions at the folder level
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - Deleting permission for " + ROLE_USER + " and " + Folder.SEPARATOR);
        deleteObjectPermission("/", m_userRole);

        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - Removing role: " + m_adminRole.getRoleName() + " from user: " + m_testUser.getUsername());
        getUserAuthorityService().removeRole(null, m_testUser, m_adminRole);
        getUserAuthorityService().removeRole(null, m_testUser, m_testRole);

        // remove the test user and the test role
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - delete the " + USER_TEST);
        getUserAuthorityService().deleteUser(null, USER_TEST);

    }

    /**
     * doObjectPermissionSetupTest
     *
     * For our report unit without a parent:
     *      ROLE_ADMINISTRATOR: SimpleAclEntry.ADMINISTRATION
     *      testUserName: TestUser has role: ROLE_TEST
     *      adminUserName: admin has role: ROLE_ADMINISTRATOR
     */
    @Test()
    public void doObjectPermissionSetupTest() {
        m_logger.info("PermissionsServiceTestTestNG => doObjectPermissionSetupTest() called");

        setAuthenticatedUser(USER_TEST);

        try {
	        int i1 = getPermissionsService().getAppliedPermissionMaskForObjectAndCurrentUser(reportUnitPath);
	        assertEquals(i1, SimpleAclEntry.ADMINISTRATION);

	        createObjectPermission(reportsFolderPath, m_adminRole, SimpleAclEntry.READ_WRITE);
	        createObjectPermission(reportUnitFolder, m_testRole, SimpleAclEntry.DELETE);
	
	        int i2 = getPermissionsService().getAppliedPermissionMaskForObjectAndCurrentUser(reportUnitPath);
	        assertEquals(i2, SimpleAclEntry.READ_WRITE|SimpleAclEntry.DELETE);

	        int i3 = getPermissionsService().getAppliedPermissionMaskForObjectAndCurrentUser(reportsFolderPath);
	        assertEquals(i3, SimpleAclEntry.READ_WRITE);

        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }
}
