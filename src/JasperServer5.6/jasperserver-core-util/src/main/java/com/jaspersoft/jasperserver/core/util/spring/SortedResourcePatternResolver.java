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

package com.jaspersoft.jasperserver.core.util.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SortedResourcePatternResolver.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class SortedResourcePatternResolver extends PathMatchingResourcePatternResolver {

	private final static String NAME_SUFFIX = ".xml";
	private final static int NAME_SUFFIX_LENGTH = 4;

	private final static Comparator RESOURCE_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			String name1 = ((Resource) o1).getFilename();
			if (name1.endsWith(NAME_SUFFIX)) {
				name1 = name1.substring(0, name1.length() - NAME_SUFFIX_LENGTH);
			}

			String name2 = ((Resource) o2).getFilename();
			if (name2.endsWith(NAME_SUFFIX)) {
				name2 = name2.substring(0, name2.length() - NAME_SUFFIX_LENGTH);
			}

			return name1.compareTo(name2);
		}
	};

	public SortedResourcePatternResolver(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}
	
	public Resource getResource(String location) {
		return ((AbstractApplicationContext) getResourceLoader()).getResource(location);
	}
 

	protected Set doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
			throws IOException {
		Set resources = super.doFindPathMatchingFileResources(rootDirResource, subPattern);
		if (resources != null && resources.size() > 1) {
			resources = sortResources(resources);
		}
		return resources;
	}

	protected Set sortResources(Set resourceSet) {
		ArrayList resourceList = new ArrayList(resourceSet);
		Collections.sort(resourceList, RESOURCE_COMPARATOR);
		LinkedHashSet sortedSet = new LinkedHashSet(resourceList);
		return sortedSet;
	}

}
