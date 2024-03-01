/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.war.model.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author Anton Fomin
 * @author Andriy Godovanets
 */
public abstract class BaseTreeDataProvider implements TreeDataProvider {
    private RepositoryService repositoryService;

    class Extra implements JSONObject {
        private String json;
        Extra(String description, String subType, String creator) {
            json = "{\"desc\":" + (description == null ? "null" : "\"" + description.replaceAll("\"","\\\\\"") + "\"") + ",\"subType\":\"" + subType + "\",\"creator\":\"" + creator + "\"}";
        }
        public String toJSONString() {
            return json;
        }
    }

    static class Parameters {
        public String uri;
        public int depth;

        public Parameters(String uri, int depth) {
            this.uri = uri;
            this.depth = depth;
        }
    }

    protected JSONObject newExtra(String description, String subType, String creator) {
        return new Extra(description, subType, creator);
    }

    /**
     * This is a workaround to preserve the [old] TreeDataProvider interface method signatures.
     * @param executionContext
     * @param parameters
     * @return
     */
    protected ExecutionContext setExecutionContextAndParameters(ExecutionContext executionContext, Parameters parameters) {
        if (executionContext == null) {
            executionContext = new ExecutionContextImpl();
        }
        if (executionContext.getAttributes() == null) {
            executionContext.setAttributes(new ArrayList());
        }
        executionContext.getAttributes().add(parameters);
        return executionContext;
    }

    protected Parameters getParameters(ExecutionContext executionContext) {
        if (executionContext != null) {
            if (executionContext.getAttributes() != null) {
                for (Object attr : executionContext.getAttributes()) {
                    if (attr instanceof Parameters) {
                        return (Parameters) attr;
                    }
                }
            }
        }
        return null;
    }

    protected void clearParameters(ExecutionContext executionContext, Parameters parameters) {
        if (executionContext != null && executionContext.getAttributes() != null) {
            executionContext.getAttributes().remove(parameters);
        }
    }

    public List getChildren(ExecutionContext executionContext,
            String parentUri, int depth) {

        TreeNode node = getNode(executionContext, parentUri, depth + 1);
        return node.getChildren();
    }

    public TreeNode getNode(ExecutionContext executionContext, String uri, int depth) {
        Parameters parameters = new Parameters(uri, depth);
        executionContext = setExecutionContextAndParameters(executionContext, parameters);
        TreeNode node = createRoot(executionContext);
        clearParameters(executionContext, parameters);
        String[] pathParts = uri.split("/");
        StringBuffer curPath = new StringBuffer();

        if ("/".equals(uri)) {
            return cutNodeHierarchyToGivenDepth(node, depth);
        }

        for (int i = 1; i < pathParts.length; i++) {
            curPath.append("/").append(pathParts[i]);
            for (TreeNode ch : (List<TreeNode>) node.getChildren()) {
                if (curPath.toString().equals(ch.getUriString())) {
                    node = ch;
                    break;
                }
            }
        }

        return cutNodeHierarchyToGivenDepth(node, depth);
    }

    protected abstract TreeNode createRoot(ExecutionContext executionContext);

    protected TreeNode cutNodeHierarchyToGivenDepth(TreeNode node, int depth) {
        if (node == null) {
            return null;
        }
        TreeNode retNode = node.clone(false);
        retNode.getChildren().clear();
        if (depth > 0 && node.getChildren() != null && node.getChildren().size() > 0) {
            for (TreeNode chNode : (List<TreeNode>) node.getChildren()) {
                retNode.getChildren().add(cutNodeHierarchyToGivenDepth(chNode, depth - 1));
            }
        }
        return retNode;
    }

    /**
     * Returns a parent folder node for a given URI. It will load and add missing hierarchy nodes to the given parent.
     * @param parent
     * @param uri
     * @return
     */
    protected TreeNode getParentFolderNode(TreeNode parent, String uri) {
        if (uri.contains("/")) {
            return findOrBuildFolderNode(parent, uri.substring(0, uri.lastIndexOf("/")));
        }
        throw new JSException("Cannot resolve parent folder for : " + uri);
    }

    /**
     * The method either finds an existing node for a given folder URI, or loads all the missing nodes,
     * adds them to a given parent, and then returns a corresponding node
     * @param parent one of the [grand*]parent folder nodes which holds the hierarchy, newly loaded nodes will be added to this parent
     * @param uri requested folder URI
     * @return TreeNode which corresponds to the URI and is a part of the hierarchy
     */
    protected TreeNode findOrBuildFolderNode(TreeNode parent, String uri) {
        String[] pathParts = uri.split("/");
        StringBuffer curPath = new StringBuffer();
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext();
        for (int i = 1; i < pathParts.length; i++) {
            boolean found = false;
            curPath.append("/").append(pathParts[i]);
            for (TreeNode ch : (List<TreeNode>) parent.getChildren()) {
                if (curPath.toString().equals(ch.getUriString())) {
                    parent = ch;
                    found = true;
                    break;
                }
            }
            if (!found) {
                try {
                    Folder f = repositoryService.getFolder(context, curPath.toString());
                    TreeNode n = new TreeNodeImpl(this,
                            f.getName(), f.getLabel(), f.getResourceType(), f.getURIString(), 1);
                    parent.getChildren().add(n);
                    parent = n;
                } catch (AccessDeniedException e){
                    // dummy node to be disposed (not  part of the tree)
                    return new TreeNodeImpl(this,   "", "", "", "", 1);
                }
            }
        }
        return parent;
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
}
