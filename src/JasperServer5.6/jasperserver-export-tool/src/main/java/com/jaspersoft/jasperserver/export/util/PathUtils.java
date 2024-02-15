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

package com.jaspersoft.jasperserver.export.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PathUtils.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class PathUtils {

	public static final char FILE_SEPARATOR = '/';
	
	protected static final Pattern MULTI_FILE_SEP_PATTERN = Pattern.compile("/{2,}");
	protected static final String MULTI_FILE_SEP_REPLACEMENT = "/";
	
	protected static final Pattern SPLIT_URI_PATTERN = Pattern.compile("^(.*)/([^/]+)/*$");
	protected static final int SPLIT_URI_PATTERN_PARENT_IDX = 1;
	protected static final int SPLIT_URI_PATTERN_NAME_IDX = 2;

    protected static final Pattern PATH_COMPONENT_INVALID_CHAR =
            Pattern.compile("[^\\p{L}\\p{N}]");

    protected static final String PATH_COMPONENT_CHAR_REPLACEMENT = "_";


    public static class SplittedPath {
		public final String parentPath;
		public final String name;
		
		public SplittedPath(final String parentPath, final String name) {
			this.parentPath = parentPath;
			this.name = name;
		}
	}
	
	public static SplittedPath splitPath(String uri) {
		Matcher matcher = SPLIT_URI_PATTERN.matcher(uri);
		SplittedPath splUri;
		if (matcher.matches()) {
			String parentURI = matcher.group(SPLIT_URI_PATTERN_PARENT_IDX);
			if (parentURI.length() == 0) {
				parentURI = null;
			}

			String name = matcher.group(SPLIT_URI_PATTERN_NAME_IDX);
			
			splUri = new SplittedPath(parentURI, name);
		} else {
			splUri = null;
		}
		return splUri;
	}

	public static String concatPaths(String path1, String path2) {
		if (path1 == null) {
			if (path2 == null) {
				return null;
			}
			
			return normalizePath(path2);
		}
		if (path2 == null) {
			return normalizePath(path1);
		}
		
		return normalizePath(path1 + FILE_SEPARATOR + path2);
	}

	public static String normalizePath(String path) {
		if (path == null) {
			return null;
		}

		path = MULTI_FILE_SEP_PATTERN.matcher(path).replaceAll(MULTI_FILE_SEP_REPLACEMENT);
		
		if (path.length() > 1 && path.charAt(path.length() - 1) == FILE_SEPARATOR) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

    public static String preparePathComponent(String name) {

        return PATH_COMPONENT_INVALID_CHAR.matcher(name).
                replaceAll(PATH_COMPONENT_CHAR_REPLACEMENT);
    }


}
