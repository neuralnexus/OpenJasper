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
package com.jaspersoft.jasperserver.api.security.externalAuth;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserTests;

/**
 * User: dlitvak
 * Date: 3/22/13
 */
public class ExternalUserDetailsTest extends UserTests {

	public static final String TEST_USER_NAME = "testUser";
	public static final String TEST_ROLE = "testRole";
	public static final boolean USER_ACCOUNT_ENABLED = true;
	public static final boolean USER_ACCOUNT_NOT_EXPIRED = true;
	public static final boolean USER_CREDS_NOT_EXPIRED = true;
	public static final boolean USER_ACCOUNT_NON_LOCKED = true;
	public static final String TEST_PASSWORD = "testPassword";

	public void setAuthoritiesTest() {
		ExternalUserDetails externalUserDetails= new ExternalUserDetails(TEST_USER_NAME);
		assertNotNull("GrantedAuthorities array should not be null after ExternalUserDetails instantiation.", externalUserDetails.getAuthorities());
		assertTrue("GrantedAuthorities array should be empty after ExternalUserDetails instantiation.", externalUserDetails.getAuthorities().length == 0);

		externalUserDetails.setAuthorities(new GrantedAuthority[] {new GrantedAuthorityImpl(TEST_ROLE)});
		assertNotNull("externalUserDetails GrantedAuthorities array should not be null", externalUserDetails.getAuthorities());
		assertTrue("externalUserDetails GrantedAuthority array should condain only 1 role: " + TEST_ROLE, externalUserDetails.getAuthorities().length == 1 && TEST_ROLE.equalsIgnoreCase(externalUserDetails.getAuthorities()[0].getAuthority()));
	}

	public void construstorExternalUserDetailsTest() {
		ExternalUserDetails externalUserDetails= new ExternalUserDetails(TEST_USER_NAME, TEST_PASSWORD,
				USER_ACCOUNT_ENABLED, USER_ACCOUNT_NOT_EXPIRED, USER_CREDS_NOT_EXPIRED, USER_ACCOUNT_NON_LOCKED,
				new GrantedAuthority[] {new GrantedAuthorityImpl(TEST_ROLE)});

		assertTrue("Test user name did not match after externalUserDetails instantiation", TEST_USER_NAME.equals(externalUserDetails.getUsername()));
		assertTrue("Test user password did not match after externalUserDetails instantiation", TEST_PASSWORD.equals(externalUserDetails.getPassword()));
		assertTrue("Test user is not enabled after externalUserDetails instantiation", externalUserDetails.isEnabled());
		assertTrue("Test user is expired after externalUserDetails instantiation", externalUserDetails.isAccountNonExpired());
		assertTrue("Test user credentials are expired after externalUserDetails instantiation", externalUserDetails.isCredentialsNonExpired());
		assertTrue("Test user account is locked after externalUserDetails instantiation", externalUserDetails.isAccountNonLocked());
		assertNotNull("externalUserDetails has a null additionalDetailMap after instantiation", externalUserDetails.getAdditionalDetailsMap());

		assertNotNull("externalUserDetails GrantedAuthorities array should not be null after externalUserDetails instantiation", externalUserDetails.getAuthorities());
		assertTrue("externalUserDetails GrantedAuthority array should condain only 1 role  after externalUserDetails instantiation: " + TEST_ROLE, externalUserDetails.getAuthorities().length == 1 && TEST_ROLE.equalsIgnoreCase(externalUserDetails.getAuthorities()[0].getAuthority()));

	}
}
