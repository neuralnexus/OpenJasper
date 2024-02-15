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
package com.jaspersoft.jasperserver.war.model;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

/**
 * The base interface for tree data provider.
 * Implementing classes make a bridge to any datasource (Repository, 
 * File System, etc.)
 * 
 * @author asokolnikov
 */
@JasperServerAPI
public interface TreeDataProvider {

    /**
     * Returns an instance of TreeNode for a given URI.
     * <b>depth</b> parameter controls how many levels of children
     * are going to be preloaded within the requested node 
     * (0 - no children preloaded, 1 - only immeadiate children preloaded, etc.)
     * @param executionContext ExecutionContext instance 
     * @param uri a unique string indentifying the node (uses "/node1/node11" convention)
     * @param depth children tree depth to be preloaded
     * @return TreeNode instance OR null of not found
     */
    public TreeNode getNode(ExecutionContext executionContext, String uri, int depth);
    
    /**
     * Returns a list of TreeNode instances which are immediate children of 
     * a node identified by a given URI. Each node in the list may have its
     * children preloaded if depth is greater that 0
     * Returns an empty list if no children found. 
     * @param executionContext ExecutionContext instance 
     * @param parentUri a unique string indentifying the node
     * @param depth children tree depth to be preloaded
     * @return List of TreeNode instances OR null if parent not found
     */
    public List/*<TreeNode>*/ getChildren(ExecutionContext executionContext, String parentUri, int depth);

}
