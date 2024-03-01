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
package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class NotLocalFolderAndResourceArgumentVoterTest {
    @Mock
    private RepositoryService repositoryService;
    @InjectMocks
    private NotLocalFolderAndResourceArgumentVoter notLocalFolderAndResourceArgumentVoter;

    @Test
    public void isPermitted_localFolder_returnFalse() {
        String localFolderUri = "/f1/f2_files";
        when(repositoryService.isLocalFolder(null, localFolderUri)).thenReturn(true);
        ObjectPermission objectPermission = mock(ObjectPermission.class);
        when(objectPermission.getURI()).thenReturn("repo:" + localFolderUri);

        boolean result = notLocalFolderAndResourceArgumentVoter.
                isPermitted(mock(Authentication.class), objectPermission, mock(Object.class));

        assertFalse(result);
    }

    @Test
    public void isPermitted_localResource_returnFalse() {
        String parentPath = "/f1/f2_files";
        String localResource = "/f1/f2_files/Res";
        when(repositoryService.isLocalFolder(null, parentPath)).thenReturn(true);
        ObjectPermission objectPermission = mock(ObjectPermission.class);
        when(objectPermission.getURI()).thenReturn("repo:"+ localResource);

        boolean result = notLocalFolderAndResourceArgumentVoter.
                isPermitted(mock(Authentication.class), objectPermission, mock(Object.class));

        assertFalse(result);
    }

    @Test
    public void isPermitted_notLocalResourceNorFolder_returnTrue() {
        String notLocalResourceNorFolder = "/f1/f2";
        when(repositoryService.isLocalFolder(null, notLocalResourceNorFolder)).thenReturn(false);
        ObjectPermission objectPermission = mock(ObjectPermission.class);
        when(objectPermission.getURI()).thenReturn("repo:" + notLocalResourceNorFolder);

        boolean result = notLocalFolderAndResourceArgumentVoter.
                isPermitted(mock(Authentication.class), objectPermission, mock(Object.class));

        assertTrue(result);
    }

}
