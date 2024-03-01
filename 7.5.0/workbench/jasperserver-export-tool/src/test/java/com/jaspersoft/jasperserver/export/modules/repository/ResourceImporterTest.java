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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.ImportTask;
import com.jaspersoft.jasperserver.export.ParametersImpl;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.MapAttributes;
import com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode;
import com.jaspersoft.jasperserver.export.modules.repository.beans.LegacyResourceBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceImporterTest {
    private class LocalResourceImporter extends ResourceImporter {
        // Override to change method's visibility
        @Override
        protected void logWarning(ExportImportWarningCode warningCode, String[] parameters, String message) {
            super.logWarning(warningCode, parameters, message);
        }
    }

    @InjectMocks
    @Spy
    private LocalResourceImporter resourceImporter = new LocalResourceImporter();
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private ResourceModuleConfiguration moduleConfiguration;
    @Mock
    private TenantService tenantService;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void importResource_shouldSkipNonSupportedResources_success() {
        final String IMPORT_URI = "/resource_name";

        // Required to initialize ResourceBean entity
        StaticApplicationContext.setApplicationContext(mock(ApplicationContext.class));
        ImporterModuleContext moduleContext = mock(ImporterModuleContext.class);
        ImportTask importTask = mock(ImportTask.class);
        ExecutionContext context = mock(ExecutionContext.class);

        doReturn(importTask).when(moduleContext).getImportTask();
        doReturn(new MapAttributes()).when(moduleContext).getAttributes();
        doReturn(new ParametersImpl()).when(importTask).getParameters();
        doReturn(context).when(importTask).getExecutionContext();
//        doReturn(true).when(resourceImporter).hasResourceBeanData(eq(IMPORT_URI));
        doReturn(false).when(resourceImporter).skipResource(eq(IMPORT_URI));
        doReturn(true).when(resourceImporter).hasResourceBeanData(eq(IMPORT_URI));
        doReturn(new LegacyResourceBean()).when(resourceImporter).readResourceBean(eq(IMPORT_URI));
        when(repositoryService.getResource(any(ExecutionContext.class), eq(IMPORT_URI))).thenReturn(null);

        resourceImporter.init(moduleContext);
        resourceImporter.initProcess();

        final String message = "Resource \"/resource_name\" is deprecated, not importing.";

        String resultUri = resourceImporter.importResource(IMPORT_URI, true);
        assertEquals(IMPORT_URI, resultUri);
        verify(resourceImporter).logWarning(
                ExportImportWarningCode.IMPORT_SKIP_RESOURCE,
                new String[]{IMPORT_URI},
                message
        );

        doReturn(true).when(resourceImporter).getUpdateFlag();
        when(repositoryService.getResource(any(ExecutionContext.class), eq(IMPORT_URI))).thenReturn(mock(Resource.class));
        resourceImporter.init(moduleContext);

        resultUri = resourceImporter.importResource(IMPORT_URI, true);
        assertTrue(resourceImporter.isUpdate());
        assertEquals(IMPORT_URI, resultUri);
        verify(resourceImporter, times(2)).logWarning(
                ExportImportWarningCode.IMPORT_SKIP_RESOURCE,
                new String[]{IMPORT_URI},
                message
        );
    }
}
