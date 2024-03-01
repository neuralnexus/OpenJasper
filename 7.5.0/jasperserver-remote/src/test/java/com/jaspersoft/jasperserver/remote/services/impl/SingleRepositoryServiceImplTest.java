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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.RepositoryConfiguration;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.FolderAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.FolderNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.NotAFileException;
import com.jaspersoft.jasperserver.remote.exception.ResourceAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.VersionNotMatchException;
import com.jaspersoft.jasperserver.remote.resources.converters.DataTypeResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.remote.resources.operation.CopyMoveOperationStrategy;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.springframework.security.authentication.BadCredentialsException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class SingleRepositoryServiceImplTest {
    @InjectMocks
    private SingleRepositoryServiceImpl service;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private Set<String> fileResourceTypes;
    @Mock
    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;
    @Mock
    private RepositoryConfiguration configurationBean;
    @Mock
    private ResourceConverterProvider resourceConverterProvider;
    @Mock
    private ToServerConverter toServerConverter;
    @Mock
    private ToClientConverter toClientConverter;
    @Mock
    private SearchCriteriaFactory searchCriteriaFactory;
    @Spy
    protected Map<String, CopyMoveOperationStrategy> copyMoveStrategies = new HashMap<String, CopyMoveOperationStrategy>();


    private ClientInputControl clientObject;

    final private String uri = "/test";
    final private String uri2 = "/test/r";
    final private String orgUri = "/organizations";
    final private Folder folder = new FolderImpl();
    final private Folder rootFolder = new FolderImpl();
    final private Resource resource = new QueryImpl();
    final private Resource resource2 = new QueryImpl();

    @BeforeClass
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = Mockito.spy(service);
        when(configurationBean.getResourceIdNotSupportedSymbols()).thenReturn("\\s");
        when(configurationBean.getOrganizationsFolderUri()).thenReturn(orgUri);
    }

    @BeforeMethod
    public void cleanUp(){
        Mockito.reset(service, repositoryService, uriHardModifyProtectionChecker, toClientConverter, toServerConverter);
        folder.setURIString("/folder");
        folder.setLabel("l");

        rootFolder.setURIString("/");

        resource.setURIString("/test/folder");
        resource.setLabel("Folder");
        resource2.setURIString(uri2 + "/" + resource.getName());
        if(clientObject != null) {
            clientObject.setVersion(Resource.VERSION_NEW);
        }
    }

    @BeforeGroups(groups = "POST")
    public void makePartialPost() throws Exception{
        doReturn(resource).when(service).getResource(nullable(String.class));
    }

    @BeforeGroups(groups = "SAVE_OR_UPDATE")
    public void prepareSaveOrUpdate() throws Exception{
        clientObject = new ClientInputControl().setUri(uri);

        when(resourceConverterProvider.getToServerConverter(clientObject)).thenReturn(toServerConverter);
        when(resourceConverterProvider.getToClientConverter(any(DataType.class))).thenReturn((ToClientConverter)new DataTypeResourceConverter());
        when(toServerConverter.getServerResourceType()).thenReturn(InputControl.class.getName());
    }

    @BeforeGroups(groups = "CREATE_FILE")
    public void makePartialCreateFile() throws Exception{
        doAnswer(new ReturnsArgumentAt(0)).when(service).createResource(nullable(Resource.class), nullable(String.class), anyBoolean(), eq(false));
    }

    @BeforeGroups(groups = "DRY_RUN")
    public void prepareDryRun(){
        clientObject = new ClientInputControl().setUri(uri);
    }

    @Test(groups = "DRY_RUN")
    public void createResource_dryRunTrue_noSaveMethodsCall() throws Exception {
        final Resource result = service.createResource(this.resource, "/some/folder", true, true);
        assertSame(result, this.resource);
        verify(service, never()).ensureFolderUri(anyString());
        verify(repositoryService, never()).saveResource(nullable(ExecutionContext.class), any(Resource.class));
        verify(service, never()).getResource(anyString());
    }

    @Test(groups = "DRY_RUN")
    public void saveOrUpdate_dryRunIsTrue_anotherResourceType_owerwriteIsTrue_noDelete(){
        doReturn(resource).when(service).getResource(anyString());
        doReturn(null).when(resourceConverterProvider).getToClientConverter(anyString(), anyString());
        doReturn(toServerConverter).when(resourceConverterProvider).getToServerConverter(clientObject);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(resource);
        final ClientJndiJdbcDataSource expectedResult = new ClientJndiJdbcDataSource();
        final ArgumentCaptor<ToClientConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToClientConversionOptions.class);
        doReturn(expectedResult).when(toClientConverter).toClient(same(resource), optionsArgumentCaptor.capture());
        doReturn(resource).when(toServerConverter).toServer(same(clientObject), isNull(Resource.class), any(ToServerConversionOptions.class));
        final HashMap<String, String[]> additionalProperties = new HashMap<String, String[]>();
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, true, additionalProperties);
        assertSame(result, expectedResult);
        verify(service, never()).deleteResource(anyString());
        final ToClientConversionOptions options = optionsArgumentCaptor.getValue();
        assertNotNull(options);
        assertSame(options.getAdditionalProperties(), additionalProperties);
    }

    @Test(groups = "DRY_RUN")
    public void saveOrUpdate_dryRunIsTrue_versionNotMutch_owerwriteIsTrue_noDelete(){
        doReturn(resource).when(service).getResource(anyString());
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(anyString(), anyString());
        doReturn(toServerConverter).when(resourceConverterProvider).getToServerConverter(clientObject);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(resource);
        resource.setVersion(10);
        clientObject.setVersion(9);
        final ClientJndiJdbcDataSource expectedResult = new ClientJndiJdbcDataSource();
        doReturn(expectedResult).when(toClientConverter).toClient(same(resource), any(ToClientConversionOptions.class));
        doReturn(resource).when(toServerConverter).toServer(same(clientObject), isNull(Resource.class), any(ToServerConversionOptions.class));
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, true, null);
        assertSame(result, expectedResult);
        verify(service, never()).deleteResource(anyString());
    }

    @Test(groups = "DELETE", expectedExceptions = {AccessDeniedException.class})
    public void testDeleteResource_modifyProtected() throws Exception{
        final String testUri = "testUri";
        when(uriHardModifyProtectionChecker.isHardModifyProtected(testUri)).thenReturn(true);

        service.deleteResource(testUri);

        Mockito.verify(repositoryService).deleteResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "SAVE_OR_UPDATE", expectedExceptions = ResourceAlreadyExistsException.class)
    public void saveOrUpdate_anotherResource_noOverwrite_exception() throws Exception {
        reset(service);
        final DataTypeImpl serverObject = new DataTypeImpl();
        doReturn(serverObject).when(service).getResource(uri);
        service.saveOrUpdate(clientObject, false, true, null, false, null);
    }

    @Test(groups = "SAVE_OR_UPDATE", expectedExceptions = VersionNotMatchException.class)
    public void saveOrUpdate_anotherResource_noOverwrite_exception_version() throws Exception {
        reset(service);

        final InputControl serverObject = new InputControlImpl();
        serverObject.setVersion(5);

        doReturn(serverObject).when(service).getResource(uri);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(serverObject.getResourceType(),
                ClientTypeUtility.extractClientType(clientObject.getClass()));

        service.saveOrUpdate(clientObject, false, true, null, false, null);
    }

    @Test(groups = "SAVE_OR_UPDATE")
    public void saveOrUpdate_anotherResource_overwrite() throws Exception {
        reset(service);
        final DataTypeImpl serverObject = new DataTypeImpl();
        serverObject.setURIString(uri);
        doReturn(serverObject).when(service).getResource(uri);
        doNothing().when(service).deleteResource(uri);
        ArgumentCaptor<ToServerConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(same(clientObject), isNull(), optionsArgumentCaptor.capture())).thenReturn(serverObject);
        doReturn(serverObject).when(service).createResource(serverObject, serverObject.getParentPath(), true, false);
        final ToClientConverter toClientConverter = mock(ToClientConverter.class);
        when(toClientConverter.getClientResourceType()).thenReturn(ResourceMediaType.DATA_TYPE_CLIENT_TYPE);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn(toClientConverter);
        when(toClientConverter.toClient(same(serverObject), any(ToClientConversionOptions.class))).thenReturn(clientObject);
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, false, null);
        verify(service).deleteResource(uri);
        assertSame(result, clientObject);
        final ToServerConversionOptions usedOptions = optionsArgumentCaptor.getValue();
        assertNotNull(usedOptions);
        assertEquals(usedOptions.getOwnersUri(), uri);
    }

    @Test(groups = "SAVE_OR_UPDATE")
    public void saveOrUpdate_existingResource_updated() throws Exception {
        reset(service);
        final InputControl serverObject = new InputControlImpl();
        serverObject.setURIString(uri);
        serverObject.setVersion(1);
        clientObject.setVersion(1);
        doReturn(serverObject).when(service).getResource(uri);
        ArgumentCaptor<ToServerConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(same(clientObject), same(serverObject), optionsArgumentCaptor.capture())).thenReturn(serverObject);
        doReturn(serverObject).when(service).updateResource(serverObject, false);
        final ToClientConverter toClientConverter = mock(ToClientConverter.class);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn(toClientConverter);
        when(toClientConverter.toClient(same(serverObject), any(ToClientConversionOptions.class))).thenReturn(clientObject);
        when(toClientConverter.getClientResourceType()).thenReturn(ResourceMediaType.INPUT_CONTROL_CLIENT_TYPE);
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, false, null);
        verify(service, never()).deleteResource(any(String.class));
        assertSame(result, clientObject);
        final ToServerConversionOptions usedOptions = optionsArgumentCaptor.getValue();
        assertNotNull(usedOptions);
        assertEquals(usedOptions.getOwnersUri(), uri);
    }

    @Test(groups = "SAVE_OR_UPDATE")
    public void saveOrUpdate_doesntExist_created() throws Exception {
        reset(service);
        final InputControl serverObject = new InputControlImpl();
        serverObject.setURIString(uri);
        doReturn(null).when(service).getResource(uri);
        ArgumentCaptor<ToServerConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(same(clientObject), isNull(), optionsArgumentCaptor.capture())).thenReturn(serverObject);
        doReturn(serverObject).when(service).createResource(serverObject, serverObject.getParentPath(), true, false);
        final ToClientConverter toClientConverter = mock(ToClientConverter.class);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn(toClientConverter);
        when(toClientConverter.toClient(same(serverObject), any(ToClientConversionOptions.class))).thenReturn(clientObject);
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, false, null);
        verify(service, never()).deleteResource(any(String.class));
        verify(service, never()).updateResource(any(Resource.class), eq(true));
        assertSame(result, clientObject);
        final ToServerConversionOptions usedOptions = optionsArgumentCaptor.getValue();
        assertNotNull(usedOptions);
        assertEquals(usedOptions.getOwnersUri(), uri);
    }

    @Test(groups = "DELETE", expectedExceptions = {IllegalParameterValueException.class})
    public void testDeleteResource_Null() throws Exception{
        service.deleteResource(null);
    }

    @Test(groups = "DELETE", expectedExceptions = {IllegalParameterValueException.class})
    public void testDeleteResource_Empty() throws Exception{
        service.deleteResource("");
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_Resource() throws Exception{
        Mockito.when(repositoryService.getResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(resource);

        service.deleteResource(uri);

        Mockito.verify(repositoryService).deleteResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_Folder() throws Exception{
        Mockito.when(repositoryService.getResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(folder);

        service.deleteResource(uri);

        Mockito.verify(repositoryService).deleteFolder(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "DELETE", expectedExceptions = {AccessDeniedException.class})
    public void testDeleteResource_Forbidden() throws Exception{
        Mockito.when(repositoryService.getResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(folder);
        Mockito.doThrow(new org.springframework.security.access.AccessDeniedException("NO")).when(repositoryService).deleteFolder(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri));

        service.deleteResource(uri);
    }

    @Test(groups = "DELETE", expectedExceptions = {AccessDeniedException.class})
    public void testDeleteResource_HasDependentResources() throws Exception{
        List<ResourceLookup> dependencies = new LinkedList<ResourceLookup>();
        dependencies.add(new ResourceLookupImpl());
        dependencies.get(0).setURIString(uri);

        Mockito.when(repositoryService.getResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(resource);
        Mockito.when(repositoryService.getDependentResources(Mockito.nullable(ExecutionContext.class), Mockito.anyString(), Mockito.same(searchCriteriaFactory), anyInt(), anyInt())).thenReturn(dependencies);

        service.deleteResource(uri);
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_HasDependentTempResources() throws Exception{
        List<ResourceLookup> dependencies = new LinkedList<ResourceLookup>();
        dependencies.add(new ResourceLookupImpl());
        dependencies.get(0).setURIString("/temp/uri");

        Mockito.when(repositoryService.getResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri))).thenReturn(resource);
        Mockito.when(repositoryService.getDependentResources(Mockito.nullable(ExecutionContext.class), Mockito.anyString(), Mockito.same(searchCriteriaFactory), anyInt(), anyInt())).thenReturn(dependencies);

        service.deleteResource(uri);

        Mockito.verify(repositoryService).deleteResource(Mockito.nullable(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "POST")
    public void testCreateResource_created() throws Exception{
        Mockito.doReturn(true).when(repositoryService).folderExists(nullable(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(Mockito.nullable(ExecutionContext.class), Mockito.eq(resource.getURIString()))).thenReturn(folder);

        Resource response = service.createResource(resource, uri, false, false);

        Mockito.verify(repositoryService).saveResource(nullable(ExecutionContext.class), any(Resource.class));
    }

    @Test(groups = "POST", expectedExceptions = {ResourceAlreadyExistsException.class})
    public void testCreateResource_oneOfGeneratedIsResource() throws Exception {
        Mockito.when(repositoryService.getResource(Mockito.nullable(ExecutionContext.class), anyString())).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenReturn(folder);

        Resource response = service.createResource(resource, uri, true, false);

        assertEquals(response.getName(), resource.getLabel().concat("_1"));
    }

    @Test(groups = "POST")
    public void testCreateResource_generate() throws Exception{
        resource.setName("");
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);
        doReturn(resource).when(service).getResource(anyString());

        Resource response = service.createResource(resource, uri, true, false);

        assertEquals(response.getName(), resource.getLabel());
    }

    @Test(groups = "POST")
    public void testCreateResource_generate_stripSpaces() throws Exception{
        resource.setName("");
        resource.setLabel(" test label ");
        doReturn(resource).when(service).getResource(anyString());
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);

        Resource response = service.createResource(resource, uri, true, false);

        assertEquals(response.getName(), "_test_label_");
    }

    @Test(groups = "POST")
    public void testCreateResource_generateNameIfResourceExists() throws Exception{
        resource.setName("");
        doReturn(resource).when(service).getResource(anyString());
        doReturn("generatedName").when(service).generateName(uri, resource.getLabel());

        Resource response = service.createResource(resource, uri, true, true);

        assertEquals(response.getName(), "generatedName");
    }

    @Test(groups = "POST")
    public void generateName_resourceExistsTwice() throws Exception{
        doReturn(new InputControlImpl()).when(repositoryService).getResource(null, "/test/label");
        doReturn(new InputControlImpl()).when(repositoryService).getResource(null, "/test/label_1");

        final String result = service.generateName("/test", "label");

        assertEquals(result, "label_2");
    }

    @Test(groups = "POST", expectedExceptions = {FolderNotFoundException.class})
    public void testCreateResource_failIfFolderNotExists() throws Exception{
        Mockito.doReturn(true).when(repositoryService).resourceExists(nullable(ExecutionContext.class), anyString());
        Mockito.doReturn(false).when(repositoryService).folderExists(nullable(ExecutionContext.class), anyString());

        Resource response = service.createResource(resource, uri, false, false);
    }

    @Test(groups = "POST")
    public void testCreateResource_createNotExistingFolder() throws Exception{
        Mockito.doReturn(false).when(repositoryService).resourceExists(nullable(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);

        Resource response = service.createResource(resource, uri, true, false);

        Mockito.verify(repositoryService).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST", expectedExceptions = {FolderAlreadyExistsException.class})
    public void testCreateResource_createNotExistingFolderUriExist() throws Exception{
        folder.setURIString(uri.toUpperCase());

        Mockito.doReturn(false).when(repositoryService).resourceExists(nullable(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(uri))).thenReturn(folder);

        Resource response = service.createResource(resource, uri, true, false);

        Mockito.verify(repositoryService).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST")
    public void testCreateResource_createNotExistingFolders() throws Exception{
        Mockito.doReturn(false).when(repositoryService).resourceExists(nullable(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);

        resource.setURIString(resource.getURIString() + "/gfyu");
        Resource response = service.createResource(resource, resource.getURIString(), true, false);

        Mockito.verify(repositoryService, times(3)).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST", expectedExceptions = {FolderAlreadyExistsException.class})
    public void testCreateResource_createNotExistingFoldersUriExist() throws Exception {
        folder.setURIString(uri.toUpperCase());

        Mockito.doReturn(false).when(repositoryService).resourceExists(nullable(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(uri))).thenReturn(folder);

        resource.setURIString(resource.getURIString() + "/gfyu");
        Resource response = service.createResource(resource, resource.getURIString(), true, false);

        Mockito.verify(repositoryService, times(3)).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    // TODO: SSU
    @Test(groups = "POST", expectedExceptions = {AccessDeniedException.class},enabled = false)
    public void testCreateResource_createNotExistingFoldersWithoutPermission() throws Exception{
        Mockito.doReturn(false).when(repositoryService).resourceExists(nullable(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq("/"))).thenThrow(BadCredentialsException.class);

        resource.setURIString(resource.getURIString() + "/gfyu");
        Resource response = service.createResource(resource, resource.getURIString(), true, false);

        Mockito.verify(repositoryService, times(2)).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST")
    public void testCreateResource_createsFolder() throws Exception {
        Mockito.when(repositoryService.folderExists(nullable(ExecutionContext.class), anyString())).thenReturn(true);

        Resource response = service.createResource(folder, uri, false, false);

        Mockito.verify(repositoryService).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST")
    public void testCreateResource_forbiddenFolder_folder() throws Exception {
        Mockito.when(repositoryService.folderExists(nullable(ExecutionContext.class), anyString())).thenReturn(true);
        Mockito.when(uriHardModifyProtectionChecker.isHardModifyProtected(uri)).thenReturn(true);

        Resource response = service.createResource(folder, uri, false, false);

        Mockito.verify(repositoryService).saveFolder(nullable(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "PUT")
    public void testUpdateResource() throws Exception{
        service.updateResource(resource, false);

        verify(repositoryService).saveResource(nullable(ExecutionContext.class), eq(resource));
    }

    @Test(groups = "PUT")
    public void testUpdateResource_folder() throws Exception{
        service.updateResource(folder, false);

        verify(repositoryService).saveFolder(nullable(ExecutionContext.class), eq(folder));
    }

    @Test(groups = "PUT")
    public void testUpdateResource_composite_references() throws Exception{
        InputControl inputControl = new InputControlImpl();
        inputControl.setQuery(new ResourceReference(resource.getURIString()));
        inputControl.setURIString(uri);

        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri))).thenReturn(inputControl);

        service.updateResource(inputControl, false);

        assertEquals(inputControl.getQuery().getTargetURI(), resource.getURIString());
        assertFalse(inputControl.getQuery().isLocal());
    }

    @Test(groups = "COPY")
    public void testCopyResource_copy() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.copyResource(resource.getURIString(), folder.getURIString(), false, false, null);

        verify(repositoryService).copyRenameResource(nullable(ExecutionContext.class), eq(resource.getURIString()),
                eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()), isNull(String.class));
    }

    @Test(groups = "COPY")
    public void testCopyResource_copy_rename() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);
        final String newName = "newName";
        service.copyResource(resource.getURIString(), folder.getURIString(), false, false, newName);

        verify(repositoryService).copyRenameResource(nullable(ExecutionContext.class), eq(resource.getURIString()),
                eq(folder.getURIString() + Folder.SEPARATOR + newName), eq(newName));
    }

    @Test(groups = "COPY", expectedExceptions = {IllegalParameterValueException.class})
    public void testCopyResource_sourceNotSet() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri2))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), anyString())).thenReturn(folder);

        service.copyResource(null, uri2, false, false, null);
    }

    @Test(groups = "COPY", expectedExceptions = {ResourceNotFoundException.class})
    public void testCopyResource_copyNotExists() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri2))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), anyString())).thenReturn(null);

        service.copyResource(uri, uri2, false, false, null);
    }

    @Test(groups = "COPY", expectedExceptions = {ResourceAlreadyExistsException.class})
    public void testCopyResource_copyDestinationAlreadyExists() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()))).thenReturn(resource2);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.copyResource(resource.getURIString(), folder.getURIString(), false, false, null);
    }

    @Test(groups = "COPY")
    public void testCopyResource_copyDestinationAlreadyExistsOverwrite() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()))).thenReturn(resource2);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.copyResource(resource.getURIString(), folder.getURIString(), false, true, null);

        verify(repositoryService).deleteResource(nullable(ExecutionContext.class), eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()));
    }

    @Test(groups = "COPY")
    public void testCopyResource_copyFolder() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(uri))).thenReturn(folder);

        final String newName = "newName";
        service.copyResource(folder.getURIString(), uri, false, false, newName);

        Mockito.verify(repositoryService).copyRenameFolder(nullable(ExecutionContext.class), eq(folder.getURIString()),
                eq(folder.getURIString() + Folder.SEPARATOR + newName), eq(newName));
    }

    @Test(groups = "COPY", expectedExceptions = {AccessDeniedException.class})
    public void testCopyResource_copyForbiddenFolder() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri2))).thenReturn(folder);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), anyString())).thenReturn(folder);
        Mockito.when(uriHardModifyProtectionChecker.isHardModifyProtected(uri2)).thenReturn(true);

        service.copyResource(uri2, uri, false, false, null);
    }

    @Test(groups = "MOVE", dependsOnGroups = {"COPY"})
    public void testMoveResource_move() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.moveResource(resource.getURIString(), folder.getURIString(), false, false, null);

        verify(repositoryService).moveResource(nullable(ExecutionContext.class), eq(resource.getURIString()), eq(folder.getURIString()));
    }

    @Test(groups = "MOVE", dependsOnGroups = {"COPY"})
    public void testMoveResource_moveFolder() throws Exception{
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);
        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(nullable(ExecutionContext.class), eq(uri))).thenReturn(folder);

        service.moveResource(folder.getURIString(), uri, false, false, null);

        Mockito.verify(repositoryService).moveFolder(nullable(ExecutionContext.class), eq(folder.getURIString()), eq(folder.getURIString()));
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_file() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        FileResource file = new FileResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file), data);
        verify(repositoryService, only()).getResourceData(nullable(ExecutionContext.class), eq(file.getURIString()));
        verify(repositoryService, never()).getContentResourceData(nullable(ExecutionContext.class), anyString());
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_contentResource() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        ContentResource file = new ContentResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file), data);
        verify(repositoryService, never()).getResourceData(nullable(ExecutionContext.class), anyString());
        verify(repositoryService, only()).getContentResourceData(nullable(ExecutionContext.class), eq(file.getURIString()));
    }

    @Test(groups = "GET", expectedExceptions = {IllegalStateException.class})
    public void testGetFileResourceData_not_a_file() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        Resource file = new ResourceLookupImpl();
        file.setURIString("/test/index.html");

        service.getFileResourceData(file);
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_file_uri() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        FileResource file = new FileResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), anyString())).thenReturn(file);
        Mockito.when(repositoryService.getResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file.getURIString()), data);
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_contentResource_uri() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        ContentResource file = new ContentResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), anyString())).thenReturn(file);
        Mockito.when(repositoryService.getResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(nullable(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file.getURIString()), data);
    }

    @Test(groups = "GET", expectedExceptions = {IllegalStateException.class})
    public void testGetFileResourceData_not_a_file_uri() throws Exception {
         Resource file = new ResourceLookupImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(file.getURIString()))).thenReturn(file);

        service.getFileResourceData(file.getURIString());
    }

    @Test(groups = "GET", expectedExceptions = {IllegalStateException.class})
    public void testGetFileResourceData_illegal_uri() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        Resource file = new ResourceLookupImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(nullable(ExecutionContext.class), eq(file.getURIString()))).thenReturn(null);

        service.getFileResourceData(file.getURIString());
    }

    @Test(groups = "CREATE_FILE", dependsOnGroups = {"POST"})
    public void testCreateFileResource_file() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String description = "description";

        when(fileResourceTypes.contains(nullable(String.class))).thenReturn(true);
        doAnswer(new ReturnsArgumentAt(0)).when(service).createResource(nullable(Resource.class), nullable(String.class), anyBoolean(), anyBoolean());

        Resource response = service.createFileResource(baos, uri, name, name, description, null, false, false);
        if (response instanceof FileResource){
            assertEquals(response.getLabel(), name);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((FileResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected FileResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "CREATE_FILE", dependsOnGroups = {"POST"})
    public void testCreateFileResource_contentResource() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String description = "description";

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        doAnswer(new ReturnsArgumentAt(0)).when(service).createResource(any(Resource.class), anyString(), anyBoolean(), anyBoolean());

        Resource response = service.createFileResource(baos, uri, name, name, description, null, false, false);
        if (response instanceof ContentResource){
            assertEquals(response.getLabel(), name);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((ContentResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"})
    public void testUpdateFileResource_file() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String label = "label";
        String description = "description";
        FileResource contentResource = new FileResourceImpl();

        contentResource.setName(name);
        contentResource.setParentFolder(uri);

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(contentResource);

        Resource response = service.updateFileResource(baos, uri, name, label, description, null, false);
        if (response instanceof FileResource){
            assertEquals(response.getLabel(), label);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((FileResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"})
    public void testUpdateFileResource_file_no_label() throws Exception {
         ByteArrayInputStream baos = new ByteArrayInputStream(new byte[10]);
        String name = "name";
        String description = "description";
        FileResource contentResource = new FileResourceImpl();

        contentResource.setName(name);
        contentResource.setParentFolder(uri);

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(contentResource);

        Resource response = service.updateFileResource(baos, uri, name, name, description, null, false);
        if (response instanceof FileResource){
            assertEquals(response.getLabel(), name);
            assertEquals(response.getDescription(), description);
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"})
    public void testUpdateFileResource_contentResource() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String label = "label";
        String description = "description";
        ContentResource contentResource = new ContentResourceImpl();

        contentResource.setName(name);
        contentResource.setParentFolder(uri);

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(contentResource);

        Resource response = service.updateFileResource(baos, uri, name, label, description, null, false);
        if (response instanceof ContentResource){
            assertEquals(response.getLabel(), label);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((ContentResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"}, expectedExceptions = NotAFileException.class)
    public void testUpdateFileResource_not_a_file() throws Exception {
        ByteArrayInputStream baos = new ByteArrayInputStream(new byte[5]);
        String name = "name";
        String label = "label";
        String description = "description";

        when(repositoryService.getResource(nullable(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(resource);

        Resource response = service.updateFileResource(baos, uri, name, label, description, null, false);

    }
}
