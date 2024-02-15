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
 * Exclusive filter based on list of URIs.
 * It excludes not only resource with exact uri but all children,
 * so the filter can be used both for resources and folders.
 * If the string begins with "*", the match can be anywhere in the URI.
 * @author asokolnikov
 *
 */
public class UriExclusiveTreeDataFilterImpl implements TreeDataFilter, Serializable {
    
    private List uriList;

    /**
     * Returns false if node is a resource or a child of a resource with uri 
     * which is in uriList.
     * Returns true otherwise.
     * Returns true if no uriList configured.
     */
    public boolean filter(TreeNode node) {
        if (uriList != null) {
            String nodeUri = node.getUriString();
            for (Iterator iter = uriList.iterator(); iter.hasNext(); ) {
                String uri = (String) iter.next();
                if (uri.startsWith("*")) {
                	uri = uri.substring(1);
                    if (nodeUri.indexOf(uri) >= 0) {
                        return false;
                    }
                } else if (nodeUri.indexOf(uri) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public List getUriList() {
        return uriList;
    }

    public void setUriList(List uriList) {
        this.uriList = uriList;
    }

}
