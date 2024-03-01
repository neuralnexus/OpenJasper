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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Volodya Sabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateRepositoryServiceImplTest {
    @Mock
    private HibernateTemplate hibernateTemplate;
    @InjectMocks
    private HibernateRepositoryServiceImpl hibernateRepositoryService;

    @Test
    public void isLocalFolder_PathMatchLocalFolderAndFolderIsHidden_returnTrue() {
        String localFolderPath = "/f1/f2_files";
        List folders = Collections.singletonList(mockFolder(localFolderPath, true));
        when(hibernateTemplate.findByCriteria(any(DetachedCriteria.class))).thenReturn(folders);

        boolean isLocalFolder = hibernateRepositoryService.isLocalFolder(null, "/f1/f2_files");

        assertTrue(isLocalFolder);
        verify(hibernateTemplate, times(1)).findByCriteria(any());

    }

    @Test
    public void isLocalFolder_PathEndsAsLocalFolderButFolderNotHidden_returnFalse() {
        String folderPath = "/f1/f2_files";
        List folders = Collections.singletonList(mockFolder(folderPath, false));
        when(hibernateTemplate.findByCriteria(any(DetachedCriteria.class))).thenReturn(folders);

        boolean isLocalFolder = hibernateRepositoryService.isLocalFolder(null, folderPath);

        assertFalse(isLocalFolder);
        verify(hibernateTemplate, times(1)).findByCriteria(any());
    }

    @Test
    public void isLocalFolder_PathNotMatchLocalFolder_returnFalse() {
        String folderPath = "/f1/f2";

        boolean isLocalFolder = hibernateRepositoryService.isLocalFolder(null, folderPath);

        assertFalse(isLocalFolder);
        verify(hibernateTemplate, times(0)).findByCriteria(any());
    }

    @Test
    public void isLocalFolder_PathIsNull_returnFalse() {
        boolean isLocalFolder = hibernateRepositoryService.isLocalFolder(null, null);

        assertFalse(isLocalFolder);
        verify(hibernateTemplate, times(0)).findByCriteria(any());
    }

    @Test
    public void addLastAccessTimeAttributeToResources() {
        Timestamp curTime = new Timestamp(System.currentTimeMillis());
        List<ResourceLookup> resourceLookupList = new ArrayList<ResourceLookup>(Arrays.asList(new ResourceLookupImpl()));
        List<Timestamp> lastAccessTimeList = new ArrayList<Timestamp>(Arrays.asList(curTime));
        List<ResourceLookup> resources = hibernateRepositoryService.addLastAccessTimeAttributeToResources(resourceLookupList, lastAccessTimeList);
        assertEquals(curTime, resources.get(0).getLastAccessTime());
    }


    private RepoFolder mockFolder(String uri, boolean isHidden) {
        RepoFolder folder = mock(RepoFolder.class);
        when(folder.isHidden()).thenReturn(isHidden);
        when(folder.getURI()).thenReturn(uri);
        return folder;
    }
}
