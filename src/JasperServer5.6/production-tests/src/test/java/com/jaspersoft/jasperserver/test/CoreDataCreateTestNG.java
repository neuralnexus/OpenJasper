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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclEntry;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.TenantImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.impl.calendar.HolidayCalendar;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import org.springframework.security.acl.basic.SimpleAclEntry;
import org.springframework.security.Authentication;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import javax.annotation.Resource;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * @author srosen
 *
 * Creates the core production data for CE using the TestNG framework
 */
public class CoreDataCreateTestNG extends BaseServiceSetupTestNG {

    protected final Log m_logger = LogFactory.getLog(CoreDataCreateTestNG.class);

	SessionFactory m_sessionFactory;
    HibernateTemplate m_template;
	HibernateDaoSupport m_jasperServerDao;

	public HibernateDaoSupport getJasperServerDao() {
		return m_jasperServerDao;
	}

    @Resource(name = "jasperServerDao")
	public void setJasperServerDao(HibernateDaoSupport jasperServerDao) {
		m_logger.info("setJasperServerDao() called");
		this.m_jasperServerDao = jasperServerDao;
	}

	public SessionFactory getSessionFactory() {
		return m_sessionFactory;
	}

    @Resource(name = "sessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		m_logger.info("setSessionFactory() called");
		this.m_sessionFactory = sessionFactory;
	}

    @BeforeClass()
	public void onSetUp() throws Exception {
		m_logger.info("onSetUp() called");

		/*
		 * The TransactionSynchronizationManager work is only needed in tests to allow multiple
		 * Hibernate transactions to occur. Otherwise, each "template." call is a transaction.
		 * Lazy initialization of collections will not occur otherwise. In a web app, there is Spring
		 * configuration to do a transaction per web request - OpenSessionInViewFilter.
		 */
		Session s = m_sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(m_sessionFactory, new SessionHolder(s));
		m_template = m_jasperServerDao.getHibernateTemplate();
	}

    @AfterClass()
	public void onTearDown() {
		m_logger.info("onTearDown() called");

		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(m_sessionFactory);
		Session s = holder.getSession();
		s.flush();
		TransactionSynchronizationManager.unbindResource(m_sessionFactory);
		SessionFactoryUtils.releaseSession(s, m_sessionFactory);
	}

    /*
     * This method is the starting point for adding resources that comprise the
     * core production data for the Community Edition (CE) product.
     */
    @Test()
	public void createCoreDataResources() throws Exception {
        m_logger.info("createCoreDataResources() called");

        createRootFolderIfMissing();
        createTenantForRootIfMissing();
        createUsersAndRoles();
        addSchedulerResources();
        createDefaultTheme();
	}
    /*
     * create users and roles needed for the core ce data
     */
    private void createUsersAndRoles() {
        m_logger.info("createUsersAndRoles() called");

        // create user jasperadmin
        // create and add role ROLE_ADMINISTRATOR to jasperadmin
        // create and add role ROLE_USER to jasperadmin
        User jasperadmin = createUser(BaseServiceSetupTestNG.USER_JASPERADMIN, BaseServiceSetupTestNG.USER_JASPERADMIN, "jasperadmin User");
        Role roleAdministrator = createRole( BaseServiceSetupTestNG.ROLE_ADMINISTRATOR );
        addRole(jasperadmin, BaseServiceSetupTestNG.ROLE_ADMINISTRATOR);
        Role roleUser = createRole( BaseServiceSetupTestNG.ROLE_USER );
        addRole(jasperadmin, BaseServiceSetupTestNG.ROLE_USER);

        // add folder perm: root folder, ROLE_ADMINISTRATOR (administrator)
        // add folder perm: root folder, ROLE_USER (read_only)
        createObjectPermission("/", roleAdministrator, SimpleAclEntry.ADMINISTRATION);
        createObjectPermission("/", roleUser, SimpleAclEntry.READ);    // root folder read only for ROLE_USER

        // create user anonymousUser
        // create and add role ROLE_ANONYMOUS to anonymousUser
        // note: anonymous user does not have a password set
        User anonUser = createUser(BaseServiceSetupTestNG.USER_ANONYMOUS, null, BaseServiceSetupTestNG.USER_ANONYMOUS);
        createRole(BaseServiceSetupTestNG.ROLE_ANONYMOUS);
		addRole(anonUser, BaseServiceSetupTestNG.ROLE_ANONYMOUS);

        // set the authenticated user to be jasperadmin
        setAuthenticatedUser(BaseServiceSetupTestNG.USER_JASPERADMIN);
    }

    /*
     * create the default theme files for the core ce data
     */
    private void createDefaultTheme() throws Exception {
        m_logger.info("createDefaultTheme() called");

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("theme_files_list.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String filePath = null;
        getOrCreateFolder("/", "themes", "Themes");
        Role userRole = getRole(BaseServiceSetupTestNG.ROLE_USER);
        createObjectPermission("/themes", userRole, JasperServerAclEntry.EXECUTE);

        Set<String> themes = new HashSet<String>();

        // add theme resources
        while ((filePath = reader.readLine()) != null) {
            // filePath looks like "themes/themeName/styleOrImage.css"

            // We will do 1 sec pause between different themes to make sure we have different update date
            // for the similar resources
            int l = "themes/".length();
            String themeName = filePath.substring(l, filePath.indexOf("/", l));
            if (!themes.contains(themeName)) {
                Thread.sleep(1000);
                themes.add(themeName);
                m_logger.info("createDefaultTheme() => Importing " + themeName);
            }

            InputStream fileInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            createThemeFile(filePath, fileInputStream);
            fileInputStream.close();
        }

        reader.close();

        // apply permissions now that new folder and the themes are in the repository
        // /themes/default folder needs be R/O for EVERYONE, no exclusions
        Role adminRole = getRole(ROLE_ADMINISTRATOR);
        ObjectPermission objPerm = createObjectPermission("/themes/default", adminRole, SimpleAclEntry.READ);
        ExecutionContext executionContext = JasperServerUtil.getExecutionContext();
        executionContext.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
        getObjectPermissionService().putObjectPermission(executionContext, objPerm);
    }

    private void createThemeFile(String filePath, InputStream fileInputStream) throws Exception {
        int length = fileInputStream.available();
        byte[] data = new byte[length];
        int off = 0;
        while ( (off += fileInputStream.read(data, off, length - off)) < length ) {};

        String[] pathParts = filePath.split("/");
        Folder parentFolder = null;
        String baseFolderURI = "";
        for (int i = 0; i < pathParts.length - 1; i++) {
            if (parentFolder != null) {
                baseFolderURI = parentFolder.getURIString();
            }
            parentFolder = getOrCreateFolder(baseFolderURI, pathParts[i]);
        }

        String fileName = pathParts[pathParts.length - 1];
        FileResource fileResource = (FileResource) getRepositoryService().newResource(null, FileResource.class);
        fileResource.setName(fileName);
        fileResource.setLabel(fileName);
        fileResource.setParentFolder(parentFolder);
        Date now = new Date();
        fileResource.setCreationDate(now);
        fileResource.setUpdateDate(now);
        fileResource.setData(data);
        String type = (fileName.toUpperCase().endsWith(".CSS")) ? FileResource.TYPE_CSS : FileResource.TYPE_IMAGE;
        fileResource.setFileType(type);

        m_logger.info("createThemeFile() => creating file : " + fileResource.getURIString());
        getRepositoryService().saveResource(null, fileResource);
    }

    private void addSchedulerResources() {
        final HolidayCalendar holidayCalendar = new HolidayCalendar();

        Calendar calendar = new GregorianCalendar(2012, Calendar.JANUARY, 1);
        for (int year = 2012; year <= 2022; year++) {
            calendar.set(Calendar.YEAR, year);
            holidayCalendar.addExcludedDate(calendar.getTime());
        }

        holidayCalendar.setTimeZone(TimeZone.getDefault());
        holidayCalendar.setDescription(HOLIDAY_CALENDAR_NAME);
        getReportScheduler().addCalendar(HOLIDAY_CALENDAR_NAME, holidayCalendar, true, true);
    }

}
