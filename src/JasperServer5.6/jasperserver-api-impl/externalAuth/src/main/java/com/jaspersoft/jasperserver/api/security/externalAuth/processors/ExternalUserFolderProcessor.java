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

package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.util.RepositoryLabelIDHelper;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclEntry;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chaim Arbiv
 * @version $id$
 * Creates
 */
public class ExternalUserFolderProcessor extends AbstractExternalUserProcessor {
    private static final Logger log = LogManager.getLogger(ExternalUserFolderProcessor.class);

    // the parent folder to create user directories under. default value is root.
    private String userFoldersParentDirectory = "";

    // checks if user has a folder on his name in the configured location, if not creates one
    @Override
    public void process() {
//        multiTenancyRepositoryContextManager.create
        if (getRepositoryService().getFolder(new ExecutionContextImpl(), getUserFolderPathUri())==null){
            createUserFolder();
        }
    }

    private void createUserFolder() {
        User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // this operation is an administration operation so we need to be in priviliged mode
        // adding a privileged attribute to the context to avoid the access denied.
        ExecutionContext context = new ExecutionContextImpl();
        List<String> contextAttributes = new ArrayList<String>();
        contextAttributes.add(ObjectPermissionService.PRIVILEGED_OPERATION);
        context.setAttributes(contextAttributes);

        // preparing the folder
        String folderName = currentUser.getUsername();
        Folder folder = new FolderImpl();
        String generatedId = RepositoryLabelIDHelper.generateIdBasedOnLabel(getRepositoryService(), userFoldersParentDirectory, folderName);
        folder.setParentFolder(userFoldersParentDirectory);
        folder.setName(generatedId);
        folder.setLabel(folderName);
        folder.setDescription("Default user folder");// not I18N ? 2014-03-08 rfaber
        getRepositoryService().saveFolder(context, folder);
        log.debug("folder "+folder.getName()+" was created for.");

        // setting the permission only for the user
        ObjectPermission userFolderPermission = getObjectPermissionService().newObjectPermission(context);
        userFolderPermission.setURI(folder.getURI());
        userFolderPermission.setPermissionRecipient(currentUser);
        userFolderPermission.setPermissionMask(JasperServerAclEntry.READ_WRITE_CREATE_DELETE);

        getObjectPermissionService().putObjectPermission(context, userFolderPermission);
    }
    private String getUserFolderPathUri() {
        return getUserFoldersParentDirectory() + "/" + ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
    public String getUserFoldersParentDirectory() {
        return userFoldersParentDirectory;
    }
    public void setUserFoldersParentDirectory(String userFoldersParentDirectory) {
        this.userFoldersParentDirectory = userFoldersParentDirectory;
    }
}
