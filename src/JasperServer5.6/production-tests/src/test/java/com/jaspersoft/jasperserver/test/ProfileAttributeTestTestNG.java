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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * @author sbirney
 *
 */
public class ProfileAttributeTestTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(ProfileAttributeTestTestNG.class);

	public ProfileAttributeTestTestNG(){
        m_logger.info("ProfileAttributeTestTestNG => constructor() called");
    }

    /**
     *  doProfileAttributeServiceTest
     */
    @Test()
    public void doProfileAttributeServiceTest() {
        m_logger.info("ProfileAttributeTestTestNG => doProfileAttributeServiceTest() called");

		User caluser = createUser(BaseServiceSetupTestNG.USER_CALIFORNIA_USER, null, BaseServiceSetupTestNG.USER_CALIFORNIA_USER);
		ProfileAttribute attr = createTestAttr( caluser, "State", "CA" );
		getProfileAttributeService().putProfileAttribute( null, attr );
		attr = createTestAttr( caluser, "Cities", "San Francisco, Oakland, San Jose, Los Angeles, Sacramento, Fresno" );
		getProfileAttributeService().putProfileAttribute( null, attr );
		List attrList = getProfileAttributeService().getProfileAttributesForPrincipal( null, caluser );
	
		assertTrue( "ProfileAttribute list was empty", attrList.size() > 0 );
		for (Iterator it = attrList.iterator(); !attrList.isEmpty() && it.hasNext();) {
		    ProfileAttribute elem = (ProfileAttribute) it.next();
		    if ("State".equals(elem.getAttrName())) {
			assertTrue( "Test attribute did not match expected value",
				    "CA".equals(elem.getAttrValue()) );
		    }
		}	

		getUserAuthorityService().deleteUser(null, BaseServiceSetupTestNG.USER_CALIFORNIA_USER);
    }

}
