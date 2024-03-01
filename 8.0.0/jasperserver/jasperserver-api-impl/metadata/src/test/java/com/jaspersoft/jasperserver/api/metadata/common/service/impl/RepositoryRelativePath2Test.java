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
public class RepositoryRelativePath2Test {
	private String contextPath;

	@Parameterized.Parameters
	public static Iterable<Object[]> invalidContextPaths() {
		return Arrays.asList(new Object[][] {
				{null},
				{""},
				{"foo"},
		});
	}

	public RepositoryRelativePath2Test(String contextPath) {
		this.contextPath = contextPath;
	}
	
	@Test
	public void invalidContextPath() {
		try {
			RepositoryUtils.resolveRelativePath(contextPath, "foo");
			Assert.fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
	}
}
