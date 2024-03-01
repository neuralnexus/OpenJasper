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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link com.jaspersoft.jasperserver.api.engine.jasperreports.util.InputControlLabelResolver}
 *
 * @author Sergey Prilukin
 * @version $Id: MessageSourceLoaderTest.java 23845 2012-05-22 09:13:54Z afomin $
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageSourceLoaderTest {

    @Before
    public void before() throws Exception {
        RepositoryUtil.clearThreadRepositoryContext();
    }

    @Test
    public void ensureBundleUseLocalizedMessageIfPresent() throws Exception {

        final FileResource resource1 = createResource(FileResource.class.getName(), "bundle.properties", "prop", "prop1=prop1.base");
        final FileResource resource2 = createResource(FileResource.class.getName(), "bundle_yy.properties", "prop", "prop1=prop1.yy\r\nprop2=prop2.yy");
        final FileResource resource3 = createResource(FileResource.class.getName(), "bundle_xx.properties", "prop", "prop1=prop1.xx\r\nprop3=prop3.xx");
        final ResourceContainer resourceContainer = createResourceContainer(resource1, resource2, resource3);
        final RepositoryService repositoryService = createRepositoryService(resource1, resource2, resource3);

        MessageSource messageSource = MessageSourceLoader.loadMessageSource(null, resourceContainer, repositoryService);
        assertEquals("prop1.base", messageSource.getMessage("prop1", null, new Locale("zz")));
        assertEquals("prop1.yy", messageSource.getMessage("prop1", null, new Locale("yy")));
        assertEquals("prop2.yy", messageSource.getMessage("prop2", null, new Locale("yy")));
        assertEquals("prop1.xx", messageSource.getMessage("prop1", null, new Locale("xx")));
        assertEquals("prop3.xx", messageSource.getMessage("prop3", null, new Locale("xx")));
    }

    @Test
    public void ensureExceptionIsThrownIfNoBundles() throws Exception {

        final ResourceContainer resourceContainer = createResourceContainer();
        final RepositoryService repositoryService = createRepositoryService();

        MessageSource messageSource = MessageSourceLoader.loadMessageSource(null, resourceContainer, repositoryService);
        assertNull(messageSource);
    }

    private ResourceContainer createResourceContainer(FileResource... references) {
        ResourceContainer resourceContainerMock = mock(ResourceContainer.class);
        when(resourceContainerMock.getResources()).thenReturn(createRefs(references));
        return resourceContainerMock;
    }

    private FileResource createResource(String resourceType, String name, String fileType, String propertyStrings) {
        FileResource fileResourceMock = mock(FileResource.class);
        when(fileResourceMock.getResourceType()).thenReturn(resourceType);
        when(fileResourceMock.getName()).thenReturn(name);
        when(fileResourceMock.getFileType()).thenReturn(fileType);
        when(fileResourceMock.getURIString()).thenReturn("/" + name);
        when(fileResourceMock.getDataStream()).thenReturn(new ByteArrayInputStream(propertyStrings.getBytes()));

        return fileResourceMock;
    }

    private List<ResourceReference> createRefs(FileResource... fileResources) {
        List<ResourceReference> list = new ArrayList<>(fileResources.length);
        for (FileResource res: fileResources) {
            list.add(new ResourceReference(res));
        }
        return list;
    }

    private RepositoryService createRepositoryService(final FileResource... resources) {
        final Map<String, FileResource> resourcesMap = new HashMap<>();
        for (FileResource resource: resources) {
            resourcesMap.put(resource.getURIString(), resource);
        }

        RepositoryService repositoryServiceMock = mock(RepositoryService.class);
        when(repositoryServiceMock.getResource(any(), any(), eq(FileResource.class)))
                .thenAnswer(invocation -> resourcesMap.get(invocation.getArgument(1)));

        when(repositoryServiceMock.getResource(any(), any()))
                .thenAnswer(invocation -> resourcesMap.get(invocation.getArgument(1)));

        when(repositoryServiceMock.getResourceData(any(), any()))
                .thenAnswer(invocation -> {
                    FileResourceData fileResourceDataMock = mock(FileResourceData.class);
                    FileResource fileResource = resourcesMap.get(invocation.getArgument(1));

                    final InputStream dataStream = fileResource.getDataStream();
                    when(fileResourceDataMock.getDataStream()).thenReturn(dataStream);

                    return fileResourceDataMock;
                });

        return repositoryServiceMock;
    }
}
