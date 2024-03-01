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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReportThumbnailServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoReportThumbnail;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.ReportUnitImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.RepoReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl;
import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportThumbnailServiceImplTest {

    @Spy
    @InjectMocks
    ReportThumbnailServiceImpl thumbnailService;

    @Mock
    HibernateRepositoryServiceImpl repositoryService;
    @Mock
    SessionFactory sessionFactory;
    @Mock
    UserAuthorityServiceImpl userAuthorityService;
    @Mock
    HibernateTemplate hibernateTemplate;

    User jasperadminUser = new UserImpl();
    ReportUnit reportUnit = new ReportUnitImpl();

    RepoUser repoJasperAdmin = new RepoUser();
    RepoReportUnit repoReportUnit = new RepoReportUnit();

    RepoReportThumbnail repoThumbnail = new RepoReportThumbnail();

    byte[] thumbnailBytes = new byte[] { (byte) 0xDE,
                                         (byte) 0xAD,
                                         (byte) 0xBE,
                                         (byte) 0xEF  };

    ByteArrayOutputStream thumbnail = new ByteArrayOutputStream();

    @Before
    public void before() throws IOException {
        IOUtils.write(thumbnailBytes, thumbnail);

        // Configure UserAuthorityService
        when(userAuthorityService.getPersistentObject(jasperadminUser)).thenReturn(repoJasperAdmin);

        // Configure Hibernate search
        when(hibernateTemplate.findByCriteria(null)).thenReturn((List)Collections.singletonList(repoThumbnail));

        // Configure ThumbnailService
        when(thumbnailService.getPersistentObject(jasperadminUser)).thenReturn(repoJasperAdmin);

        // Configure RepositoryService
        when(repositoryService.getRepoResource(reportUnit)).thenReturn(repoReportUnit);

        repoThumbnail.setResource(repoReportUnit);
        try {
            repoThumbnail.setThumbnail(new SerialBlob(thumbnailBytes));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        repoThumbnail.setUser(repoJasperAdmin);
    }

    @Test(expected = JSException.class)
    public void saveReportThumbnail_NullResource_Test() {
        thumbnailService.saveReportThumbnail(thumbnail, jasperadminUser, null);
        verify(thumbnailService, times(1)).getPersistentObject(jasperadminUser);
        verify(repositoryService, times(1)).getRepoResource(null);

    }

    @Test
    public void getReportThumbnail_ValidData_Test() {
        thumbnailService.getReportThumbnail(jasperadminUser, reportUnit);
        verify(thumbnailService, times(1)).getPersistentObject(reportUnit);
        verify(userAuthorityService, times(1)).getPersistentObject(jasperadminUser);
        verify(hibernateTemplate).findByCriteria(any());
    }

    @Test
    public void saveReportThumbnail_ValidData_Test() {
        thumbnailService.saveReportThumbnail(thumbnail, jasperadminUser, reportUnit);
        verify(thumbnailService, times(2)).getPersistentObject(jasperadminUser);
        verify(repositoryService, times(1)).getRepoResource(reportUnit);
        verify(hibernateTemplate, times(1)).save(repoThumbnail);
    }
}
