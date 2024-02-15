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
import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Implementation of TreeDataFilter.
 * Includes a TreeNode instance into a data set if its type is one of the
 * configured types
 * @author asokolnikov
 *
 */public class TypeInclusiveTreeDataFilterImpl implements TreeDataFilter, Serializable {
    
    private List includeTypesList;

    /**
     * Returns true if excludeTypesList contains a given node type.
     * Returns false otherwise
     */
    public boolean filter(TreeNode node) {
        if (includeTypesList.contains(node.getType())) {
            return true;
        }
        return false;
    }

    public List getIncludeTypesList() {
        return includeTypesList;
    }

    public void setIncludeTypesList(List includeTypesList) {
        this.includeTypesList = includeTypesList;
    }

}
