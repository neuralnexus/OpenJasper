/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl.CHILDREN_FOLDER_SUFFIX;
import static com.jaspersoft.jasperserver.api.metadata.user.service.impl.InternalURIUtil.getInternalUriFor;

/**
 * ACL service for local hidden folder or local resource. Local Hidden folder was introduced into product in order to
 * keep all local resources for target, lets say, master resource(sometimes we call it as complex resource. Examples of
 * such Repository resources are: Report, Domain, Ad Hoc View, Dashboard, Input Control).
 * This folder is always invisible for end user, though there is some case when it become visible(like when user GET
 * target resource via REST API with expanded=false). We did assumption that local hidden folder should have the same
 * permission, as master resource, that owns this local folder and there are no ability to assign permissions to local
 * hidden folder and local resources.
 *
 * @author Volodya Sabadosh
 */
public class LocalFolderAndResourceAclService implements AclService {
    private RepositoryService repositoryService;
    private AclService aclLookupStrategy;

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        return null;
    }

    /**
     * Reads ACL by object identity if it's local hidden folder or local resource or return null.
     * If object identity represents local hidden folder, then create ACl with
     * empty aces and as parent ACL put master resource ACL. If object identity represents local resource,
     * then create ACl with empty aces and as parent ACL put local folder ACL, that keeps this local resource.
     *
     * @param object object identity
     *
     * @return ACL for object identity if it is local folder or resource or return null.
     * @throws NotFoundException
     */
    @Override
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        InternalURI targetUri = getInternalUriFor(object);
        if (repositoryService.isLocalFolder(null, targetUri.getPath())) {
            InternalURI masterResourceUri = getMasterResourceUriFor(targetUri);
            Acl masterResourceAcl = aclLookupStrategy.readAclById(masterResourceUri);
            return new JasperServerAclImpl(targetUri, new ArrayList<>(), masterResourceAcl);
        } else if (isLocalResource(targetUri)) {
            Acl parentLocalFolderAcl = aclLookupStrategy.readAclById(new InternalURIDefinition(targetUri.getParentPath(),
                    targetUri.getProtocol()));
            //local resource shouldn't have its own permissions, that's why aces is empty list.
            return new JasperServerAclImpl(targetUri, new ArrayList<>(), parentLocalFolderAcl);
        }
        return null;
    }

    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        return aclLookupStrategy.readAclById(object);
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        Map<ObjectIdentity, Acl> result = new HashMap<>();
        for(ObjectIdentity oid : objects) {
            result.put(oid, aclLookupStrategy.readAclById(oid));
        }
        return result;
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        Map<ObjectIdentity, Acl> result = new HashMap<>();
        for(ObjectIdentity oid : objects) {
            result.put(oid, aclLookupStrategy.readAclById(oid, sids));
        }
        return result;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setAclLookupStrategy(AclService aclLookupStrategy) {
        this.aclLookupStrategy = aclLookupStrategy;
    }

    private InternalURI getMasterResourceUriFor(InternalURI localFolderUri) {
        String path = localFolderUri.getPath();
        String masterResourcePath = path.substring(0, path.length() - CHILDREN_FOLDER_SUFFIX.length());
        return new InternalURIDefinition(masterResourcePath, localFolderUri.getProtocol());
    }

    /**
     * Checks if target Uri is local resource.
     * We make assumption that local folder can keep either local resource or local folder. That's why we should use this method after
     * we have checked that it is not local folder.
     *
     * @param targetUri resource internal URI
     *
     * @return <code>true</code> if local resource.
     */
    private boolean isLocalResource(InternalURI targetUri) {
        return targetUri.getParentPath() != null &&
                repositoryService.isLocalFolder(null, targetUri.getParentPath());
    }

}
