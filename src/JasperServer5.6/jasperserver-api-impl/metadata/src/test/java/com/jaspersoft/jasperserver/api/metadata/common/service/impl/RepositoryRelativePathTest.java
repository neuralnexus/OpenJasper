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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryRelativePathTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RepositoryRelativePathTest {
	
	@Test(dataProvider = "relativePaths")
	public void relativePath(String contextPath, String path, String expectedPath) {
		String resolvedPath = RepositoryUtils.resolveRelativePath(contextPath, path);
		Assert.assertNotNull(resolvedPath);
		Assert.assertEquals(resolvedPath, expectedPath);
	}
	
	@DataProvider
	public Object[][] relativePaths() {
		return new Object[][] {
				{"/foo", "/bar", "/bar"},
				{"/foo", "bar", "/foo/bar"},
				{"/", "bar", "/bar"},
				{"/foo/x", "bar", "/foo/x/bar"},
				{"/foo", "bar/x", "/foo/bar/x"},
				{"/foo", "./bar/x", "/foo/bar/x"},
				{"/foo", ".", "/foo"},
				{"/foo", "./", "/foo"},
				{"/foo", "../bar", "/bar"},
				{"/", "../bar", "/bar"},
				{"/", "../../bar", "/bar"},
				{"/foo/x", "../bar", "/foo/bar"},
				{"/foo/x", "../../bar", "/bar"},
				{"/foo", "bar/../x", "/foo/x"},
		};
	}
	
	@Test(dataProvider = "invalidContextPaths")
	public void invalidContextPath(String contextPath) {
		try {
			RepositoryUtils.resolveRelativePath(contextPath, "foo");
			Assert.fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
	}
	
	@DataProvider
	public Object[][] invalidContextPaths() {
		return new Object[][] {
				{null},
				{""},
				{"foo"},
		};
	}
	
	@Test(dataProvider = "invalidRelativePaths")
	public void invalidRelativePath(String path) {
		try {
			RepositoryUtils.resolveRelativePath("/foo", path);
			Assert.fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
	}
	
	@DataProvider
	public Object[][] invalidRelativePaths() {
		return new Object[][] {
				{null},
				{""},
		};
	}
}
