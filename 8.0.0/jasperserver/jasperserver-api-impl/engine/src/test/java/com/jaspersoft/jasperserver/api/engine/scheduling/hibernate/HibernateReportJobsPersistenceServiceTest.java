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

package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.BaseUnitTest;
import com.jaspersoft.jasperserver.api.engine.common.user.UserPersistenceHandler;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Taras Matyashovsky
 */

public class HibernateReportJobsPersistenceServiceTest extends BaseUnitTest {

    @InjectMocks
    private HibernateReportJobsPersistenceService hibernateReportJobsPersistenceService;

    @Mock
    private UserPersistenceHandler userHandler;

    @Mock
    private HibernateTemplate hibernateTemplate;

    @After
    public void tearDown() {
        verifyNoMoreInteractions(userHandler, hibernateTemplate);
    }

    @Test
    public void listJobs() {
        ExecutionContext executionContext = getExecutionContext();

        /* Creating stub for output persistent report jobs. */
        List<PersistentReportJob> persistentReportJobs = new ArrayList<PersistentReportJob>();
        PersistentReportJob persistentReportJob = new PersistentReportJob();
        persistentReportJob.setSource(new PersistentReportJobSource());
        persistentReportJobs.add(persistentReportJob);

        /* Hibernate template should once return persistent report jobs. */
        when(hibernateTemplate.loadAll(PersistentReportJob.class)).thenReturn(persistentReportJobs);

        /* Target method invocation. */
        List jobs = hibernateReportJobsPersistenceService.listJobs(executionContext);

        /* Assertions. */
        assertNotNull("Jobs should not be null", jobs);
        assertEquals(1, jobs.size());

        /* Hibernate template have been invoked from tested object with request to load all persistent report jobs. */
        verify(hibernateTemplate, times(1)).loadAll(PersistentReportJob.class);
        verify(userHandler, times(1)).getClientUsername(any());
    }
	
}
