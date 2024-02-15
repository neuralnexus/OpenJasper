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

package com.jaspersoft.jasperserver.export.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.list.UnmodifiableList;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ModuleRegisterImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ModuleRegisterImpl implements ModuleRegister {

	private List exporterModules;
	private List importerModules;
	private Map importerModulesMap;
	
	public void setExporterModules(List modules) {
		this.exporterModules = modules;
	}

	public List getExporterModules() {
		return UnmodifiableList.decorate(exporterModules);
	}

	public List getImporterModules() {
		return UnmodifiableList.decorate(importerModules);
	}

	public void setImporterModules(List importerModules) {
		this.importerModules = importerModules;
		refreshImporterMap();
	}

	protected void refreshImporterMap() {
		importerModulesMap = new HashMap();
		for (Iterator it = importerModules.iterator(); it.hasNext();) {
			ImporterModule module = (ImporterModule) it.next();
			importerModulesMap.put(module.getId(), module);
		}
	}

	public ImporterModule getImporterModule(String id) {
		return (ImporterModule) importerModulesMap.get(id);
	}

}
