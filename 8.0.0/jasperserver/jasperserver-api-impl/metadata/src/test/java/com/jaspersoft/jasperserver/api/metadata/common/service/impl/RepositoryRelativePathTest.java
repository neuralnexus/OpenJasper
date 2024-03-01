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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;


import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@RunWith(Parameterized.class)
public class RepositoryRelativePathTest {
	private String contextPath;private String path; private String expectedPath;

	@Parameterized.Parameters
	public static Iterable<Object[]> relativePaths() {
		return Arrays.asList(new Object[][] {
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
		});
	}

	public RepositoryRelativePathTest(String contextPath, String path, String expectedPath) {
		this.contextPath = contextPath;
		this.path = path;
		this.expectedPath = expectedPath;
	}

	@Test
	public void relativePath() {
		String resolvedPath = RepositoryUtils.resolveRelativePath(contextPath, path);
		Assert.assertNotNull(resolvedPath);
		Assert.assertEquals(resolvedPath, expectedPath);
	}
}
