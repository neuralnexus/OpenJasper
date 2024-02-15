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
package com.jaspersoft.jasperserver.war.model.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * This is a container for logical AND operation over all children filters assigned 
 * @author asokolnikov
 *
 */
public class ANDTreeDataFilterImpl implements TreeDataFilter, Serializable {
    
    public List filterList;

    /**
     * Returns true if all children filters return true.
     * Returns true if no children filters assigned.
     * Returns false otherwise
     */
    public boolean filter(TreeNode node) {
        if (filterList != null) {
            for (Iterator iter = filterList.iterator(); iter.hasNext(); ) {
                TreeDataFilter filter = (TreeDataFilter) iter.next();
                if (!filter.filter(node)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List getFilterList() {
        return filterList;
    }

    public void setFilterList(List filterList) {
        this.filterList = filterList;
    }

}
