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

package com.jaspersoft.jasperserver.export;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CommandMetadataImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class CommandMetadataImpl implements CommandMetadata, InitializingBean {

	private List argumentNames;
	private Set argumentNameSet;

	public void afterPropertiesSet() {
		argumentNameSet = new HashSet();
		argumentNameSet.add(BaseExportImportCommand.ARG_CONFIG_FILES);
		argumentNameSet.add(BaseExportImportCommand.ARG_CONFIG_RESOURCES);
		argumentNameSet.add(BaseExportImportCommand.ARG_COMMAND_BEAN);
		if (argumentNames != null) {
			argumentNameSet.addAll(argumentNames);
		}
	}
	
	public List getArgumentNames() {
		return argumentNames;
	}

	public void validateParameters(Parameters parameters) {
		for (Iterator it = parameters.getParameterNames(); it.hasNext();) {
			String argument = (String) it.next();
			if (!argumentNameSet.contains(argument)) {
                // Adding non localized message cause import-export tool does not support localization.
                StringBuilder message = new StringBuilder("Option ");
                message.append(argument);
                message.append(" is not recognized.");
                throw new JSException(message.toString());
			}
		}
	}

	public void setArgumentNames(List argumentNames) {
		this.argumentNames = argumentNames;
	}

}
