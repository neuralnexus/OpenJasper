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
package com.jaspersoft.jasperserver.remote.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: ExportsContainer.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ExportsContainer {
    private final Map<String, ExportExecution> executions = new HashMap<String, ExportExecution>();

    public ExportExecution get(String exportId) {
        return executions.get(exportId);
    }

    public void put(ExportExecution exportExecution) {
        executions.put(exportExecution.getId(), exportExecution);
    }

    public void putAll(Collection<ExportExecution> exportExecutions){
        for(ExportExecution currentExportExecution : exportExecutions)put(currentExportExecution);
    }

    public Set<ExportExecution> values() {
        return new HashSet<ExportExecution>(executions.values());
    }

    public boolean isEmpty(){
        return executions.isEmpty();
    }

    public void clear(){
        executions.clear();
    }
}
