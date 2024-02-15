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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl.CHILDREN_FOLDER_SUFFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author Volodya Sabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalFolderAndResourceAclServiceTest {
    @Mock
    private RepositoryService repositoryService;

    @Mock
    private AclService aclLookupStrategy;

    @InjectMocks
    LocalFolderAndResourceAclService localHiddenFolderAclService;

    @Before
    public void before() {
        reset(repositoryService, aclLookupStrategy);
    }

    @Test
    public void readAclById_objectIdentityNotLocalResourceOrFolder_returnNull() {
        InternalURI objectIdentity = new InternalURIDefinition("repo:/folder1/notLocal");
        when(repositoryService.isLocalFolder(null, objectIdentity.getPath())).thenReturn(false);
        when(repositoryService.isLocalFolder(null, objectIdentity.getParentPath())).thenReturn(false);

        Acl result =  localHiddenFolderAclService.readAclById(objectIdentity);

        assertNull(result);
    }

    @Test
    public void readAclById_objectIdentityIsLocalFolder_returnAcl() {
        String masterResourceUri = "repo:/folder1/Report1";
        Acl masterResourceAcl = mockAclFor(masterResourceUri);

        InternalURI localFolder = createLocalFolderURI(masterResourceUri);

        Acl result = localHiddenFolderAclService.readAclById(localFolder);

        checkAcl(result, localFolder, new ArrayList<>(), masterResourceAcl);
    }

    @Test
    public void readAclById_objectIdentityIsLocalResource_returnAcl() {
        String masterResourceUri = "repo:/folder1/Report1";

        InternalURI localResource = createLocalResourceURI(masterResourceUri);
        Acl parentLocalFolderAcl = mockAclFor(localResource.getParentURI());

        Acl result = localHiddenFolderAclService.readAclById(localResource);

        checkAcl(result, localResource, new ArrayList<>(), parentLocalFolderAcl);
    }

    @Test
    public void readAclById_withSids_returnAcl() {
        List<Sid> sids = Arrays.asList(mock(Sid.class), mock(Sid.class));
        InternalURI objectIdentity = new InternalURIDefinition("repo:/f1/Resource");
        Acl mockAcl = mock(Acl.class);
        when(aclLookupStrategy.readAclById(objectIdentity)).thenReturn(mockAcl);

        Acl result = localHiddenFolderAclService.readAclById(objectIdentity, sids);

        assertSame(mockAcl, result);
    }

    @Test
    public void readAclsById_returnAcl() {
        InternalURI objectIdentity1 = new InternalURIDefinition("repo:/f1/Resource1");
        InternalURI objectIdentity2 = new InternalURIDefinition("repo:/f1/Resource2");
        Acl mockAcl1 = mock(Acl.class);
        Acl mockAcl2 = mock(Acl.class);
        when(aclLookupStrategy.readAclById(objectIdentity1)).thenReturn(mockAcl1);
        when(aclLookupStrategy.readAclById(objectIdentity2)).thenReturn(mockAcl2);

        Map<ObjectIdentity, Acl> result = localHiddenFolderAclService.readAclsById(Arrays.asList(objectIdentity1, objectIdentity2));

        assertEquals(2, result.keySet().size());
        assertSame(mockAcl1, result.get(objectIdentity1));
        assertSame(mockAcl2, result.get(objectIdentity2));
    }

    @Test
    public void readAclsById_withSids_returnAcl() {
        List<Sid> sids = Arrays.asList(mock(Sid.class), mock(Sid.class));
        InternalURI objectIdentity1 = new InternalURIDefinition("repo:/f1/Resource1");
        InternalURI objectIdentity2 = new InternalURIDefinition("repo:/f1/Resource2");
        Acl mockAcl1 = mock(Acl.class);
        Acl mockAcl2 = mock(Acl.class);
        when(aclLookupStrategy.readAclById(objectIdentity1, sids)).thenReturn(mockAcl1);
        when(aclLookupStrategy.readAclById(objectIdentity2, sids)).thenReturn(mockAcl2);

        Map<ObjectIdentity, Acl> result = localHiddenFolderAclService.readAclsById(Arrays.asList(objectIdentity1, objectIdentity2), sids);

        assertEquals(2, result.keySet().size());
        assertSame(mockAcl1, result.get(objectIdentity1));
        assertSame(mockAcl2, result.get(objectIdentity2));
    }

    private void checkAcl(Acl aclToCheck, ObjectIdentity expectedObjIdn, List<AccessControlEntry> expectedEntries, Acl expectedParentAcl) {
        assertEquals(expectedEntries, aclToCheck.getEntries());
        assertEquals(expectedParentAcl, aclToCheck.getParentAcl());
        assertEquals(expectedObjIdn, aclToCheck.getObjectIdentity());
    }

    private InternalURI createLocalFolderURI(String masterResourceURI) {
        InternalURI masterResource = new InternalURIDefinition(masterResourceURI);
        String localFolderPath = masterResource.getPath() + CHILDREN_FOLDER_SUFFIX;
        when(repositoryService.isLocalFolder(null, localFolderPath)).thenReturn(true);

        return new InternalURIDefinition(localFolderPath, masterResource.getProtocol());
    }

    private InternalURI createLocalResourceURI(String masterResourceURI) {
        InternalURI masterResource = new InternalURIDefinition(masterResourceURI);
        String localResourcePath = masterResource.getPath() + CHILDREN_FOLDER_SUFFIX + "/localResource";
        InternalURI localResource = new InternalURIDefinition(localResourcePath);

        when(repositoryService.isLocalFolder(null, localResource.getParentPath())).thenReturn(true);

        return new InternalURIDefinition(localResourcePath, masterResource.getProtocol());
    }

    private Acl mockAclFor(String resourceUri) {
        InternalURI resource = new InternalURIDefinition(resourceUri);
        Acl masterResourceAcl = mock(Acl.class);

        when(aclLookupStrategy.readAclById(eq(resource))).thenReturn(masterResourceAcl);

        return masterResourceAcl;
    }

}
