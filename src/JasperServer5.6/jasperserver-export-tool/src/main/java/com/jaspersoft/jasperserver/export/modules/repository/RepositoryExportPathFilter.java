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

package com.jaspersoft.jasperserver.export.modules.repository;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryExportPathFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class RepositoryExportPathFilter implements RepositoryExportFilter {

	private static final Log log = LogFactory.getLog(RepositoryExportPathFilter.class);
	
	private String[] excludeContentsFolderPatterns;
	private Pattern[] excludeContentsFolderCompiledPatterns;
	
	public String[] getExcludeContentsFolderPatterns() {
		return excludeContentsFolderPatterns;
	}

	public void setExcludeContentsFolderPatterns(
			String[] excludeContentsFolderPatterns) {
		this.excludeContentsFolderPatterns = excludeContentsFolderPatterns;
		compileExcludeContentsFolderPatterns();
	}

	protected void compileExcludeContentsFolderPatterns() {
		if (excludeContentsFolderPatterns == null 
				|| excludeContentsFolderPatterns.length == 0) {
			excludeContentsFolderCompiledPatterns = null;
		} else {
			excludeContentsFolderCompiledPatterns = 
				new Pattern[excludeContentsFolderPatterns.length];
			for (int i = 0; i < excludeContentsFolderPatterns.length; i++) {
				excludeContentsFolderCompiledPatterns[i] = 
					compilePattern(excludeContentsFolderPatterns[i]);
			}
		}
	}

	protected Pattern compilePattern(String pattern) {
		return Pattern.compile(pattern);
	}

	public boolean toExportContents(Folder folder) {
		if (excludeContentsFolderCompiledPatterns == null) {
			return true;
		}
		
		String folderPath = folder.getURIString();
		for (int i = 0; i < excludeContentsFolderCompiledPatterns.length; i++) {
			Pattern pattern = excludeContentsFolderCompiledPatterns[i];
			if (pattern.matcher(folderPath).matches()) {
				if (log.isDebugEnabled()) {
					log.debug("Folder path " + folderPath 
							+ " matched exclude contents pattern " + pattern);
				}
				
				return false;
			}
		}
		
		return true;
	}

}
