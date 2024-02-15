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
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientAdhocDataView;
import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.resources.attachments.AttachmentsProcessor;
import com.jaspersoft.jasperserver.remote.resources.validation.ResourceValidator;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.Authentication;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceConverterImplTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ResourceConverterImplTest {
    private static final String TEST_CLIENT_OBJECT_NAME = "testClientObjectName";
    @InjectMocks
    private ResourceConverterImpl converter;
    @Mock
    private PermissionsService permissionsService;
    @Mock
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    private final ObjectPermission permission = new ObjectPermissionImpl();
    private ToServerConversionOptions options = ToServerConversionOptions.getDefault();

    @BeforeMethod
    public void resetConverter() {
        converter = mock(ResourceConverterImpl.class);
        permission.setPermissionMask(1);
        MockitoAnnotations.initMocks(this);
        when(permissionsService.getEffectivePermission(any(Resource.class), any(Authentication.class))).thenReturn(permission);
    }

    @Test(expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void toClient_EmptyLabel() throws Exception {
        when(converter.genericFieldsToServer(any(ClientResource.class), any(Resource.class), eq(options))).thenCallRealMethod();
        converter.genericFieldsToServer(new ClientAdhocDataView(), null, options);
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validateResource() {
        final Resource resource = mock(Resource.class);
        final String testResourceType = "testResourceType";
        when(resource.getResourceType()).thenReturn(testResourceType);
        final ResourceValidator validator = mock(ResourceValidator.class);
        doThrow(JSValidationException.class).when(validator).validate(resource);
        when(genericTypeProcessorRegistry.getTypeProcessor(testResourceType, ResourceValidator.class, false)).thenReturn(validator);
        doCallRealMethod().when(converter).validateResource(resource);
        converter.validateResource(resource);
    }

    @Test
    public void toServer_withValidation() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        ResourceLookup serverObject = new ResourceLookupImpl();
        ResourceLookup anotherServerObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        serverObject.setURIString("/test/uri");
        when(converter.toServer(clientObject, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, options)).thenReturn(anotherServerObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, anotherServerObject, options)).thenReturn(anotherServerObject);
        converter.toServer(clientObject, serverObject, options);
        verify(converter).validateResource(anotherServerObject);
    }

    @Test
    public void internalToClient() throws ParseException {
        ResourceLookup serverObject = new ResourceLookupImpl();
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final int testVersion = 123;
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";

        permission.setPermissionMask(1);
        serverObject.setResourceType("ignoredResourceType");
        serverObject.setURIString(testResourceUri);
        serverObject.setCreationDate(creationDate);
        serverObject.setUpdateDate(updateDate);
        serverObject.setDescription(testDescription);
        serverObject.setLabel(testLabel);
        serverObject.setVersion(testVersion);
        final ClientFolder expectedClientObject = new ClientFolder();
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.format(eq(creationDate), any(StringBuffer.class), any(FieldPosition.class))).thenReturn(new StringBuffer(creationDateString));
        when(dateFormatMock.format(eq(updateDate), any(StringBuffer.class), any(FieldPosition.class))).thenReturn(new StringBuffer(updateDateString));
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.genericFieldsToClient(expectedClientObject, serverObject, null)).thenCallRealMethod();
        final ClientResource clientObject = converter.genericFieldsToClient(expectedClientObject, serverObject, null);
        assertSame(clientObject, expectedClientObject);
        assertEquals(clientObject.getCreationDate(), creationDateString);
        assertEquals(clientObject.getUpdateDate(), updateDateString);
        assertEquals(clientObject.getDescription(), testDescription);
        assertEquals(clientObject.getLabel(), testLabel);
        assertEquals(clientObject.getUri(), testResourceUri);
        assertEquals(clientObject.getVersion().intValue(), testVersion);
        assertEquals(clientObject.getPermissionMask().intValue(), permission.getPermissionMask());
    }

    @Test
    public void toServer_withAttachments() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        final HashMap<String, InputStream> attachments = new HashMap<String, InputStream>();
        final ToServerConversionOptions toServerConversionOptions = ToServerConversionOptions.getDefault().setAttachments(attachments);
        final ResourceLookupImpl serverObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        when(converter.toServer(clientObject, serverObject, toServerConversionOptions)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, toServerConversionOptions)).thenReturn(serverObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, serverObject, toServerConversionOptions)).thenReturn(serverObject);
        when(converter.getClientTypeClass()).thenReturn(ClientResource.class);
        final AttachmentsProcessor attachmentsProcessor = mock(AttachmentsProcessor.class);
        when(genericTypeProcessorRegistry.getTypeProcessor(ClientResource.class, AttachmentsProcessor.class, false)).thenReturn(attachmentsProcessor);
        when(attachmentsProcessor.processAttachments(serverObject, attachments)).thenReturn(serverObject);
        final Resource result = converter.toServer(clientObject, serverObject, toServerConversionOptions);
        assertSame(result, serverObject);
    }

    @Test
    public void toServer_updateMode() throws Exception {
        final String testResourceType = "testResourceType";
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final int testVersion = 123;
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        ClientFolder clientResource = new ClientFolder();
        clientResource.setDescription(testDescription);
        clientResource.setLabel(testLabel);
        clientResource.setUpdateDate(updateDateString);
        clientResource.setUri(testResourceUri);
        clientResource.setVersion(testVersion);
        clientResource.setCreationDate(creationDateString);
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString(testResourceUri);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(creationDateString)).thenReturn(creationDate);
        when(dateFormatMock.parse(updateDateString)).thenReturn(updateDate);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.getServerResourceType()).thenReturn(testResourceType);
        when(converter.toServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(clientResource, serverObject, options)).thenReturn(serverObject);
        final Resource resource = converter.toServer(clientResource, serverObject, options);
        assertSame(resource, serverObject);
        assertEquals(resource.getURIString(), clientResource.getUri());
        assertEquals(resource.getDescription(), clientResource.getDescription());
        assertEquals(resource.getLabel(), clientResource.getLabel());
        assertNull(resource.getResourceType());
        assertEquals(resource.getUpdateDate(), updateDate);
        assertEquals(resource.getCreationDate(), creationDate);
        assertEquals(resource.getVersion(), clientResource.getVersion().intValue());
    }

    @Test
    public void toServer_noVersion() throws Exception {
        final String testResourceType = "testResourceType";
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        ClientFolder clientResource = new ClientFolder();
        clientResource.setDescription(testDescription);
        clientResource.setLabel(testLabel);
        clientResource.setUpdateDate(updateDateString);
        clientResource.setUri(testResourceUri);
        clientResource.setCreationDate(creationDateString);
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString(testResourceUri);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(creationDateString)).thenReturn(creationDate);
        when(dateFormatMock.parse(updateDateString)).thenReturn(updateDate);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.getServerResourceType()).thenReturn(testResourceType);
        when(converter.toServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(clientResource, serverObject, options)).thenReturn(serverObject);
        final Resource resource = converter.toServer(clientResource, serverObject, options);

        assertEquals(resource.getVersion(), Resource.VERSION_NEW);
    }

    @Test
    public void toServer_resetVersion() throws Exception {
        final String testResourceType = "testResourceType";
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final int testVersion = 123;
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        ClientFolder clientResource = new ClientFolder();
        clientResource.setDescription(testDescription);
        clientResource.setLabel(testLabel);
        clientResource.setUpdateDate(updateDateString);
        clientResource.setUri(testResourceUri);
        clientResource.setVersion(testVersion);
        clientResource.setCreationDate(creationDateString);
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString(testResourceUri);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(creationDateString)).thenReturn(creationDate);
        when(dateFormatMock.parse(updateDateString)).thenReturn(updateDate);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.getServerResourceType()).thenReturn(testResourceType);
        when(converter.toServer(eq(clientResource), eq(serverObject), any(ToServerConversionOptions.class))).thenCallRealMethod();
        when(converter.genericFieldsToServer(eq(clientResource), eq(serverObject), any(ToServerConversionOptions.class))).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(eq(clientResource), eq(serverObject), any(ToServerConversionOptions.class))).thenReturn(serverObject);
        final Resource resource = converter.toServer(clientResource, serverObject, ToServerConversionOptions.getDefault().setResetVersion(true));

        assertEquals(resource.getVersion(), Resource.VERSION_NEW);
    }

    @Test
    public void genericFieldsToServer_resourceToUpdateIsNew() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        ClientResource expectedClientObject = new ClientFolder().setLabel("testLabel");
        Resource expectedNewObject = new FolderImpl();
        when(converter.getNewResourceInstance()).thenReturn(expectedNewObject);
        when(converter.genericFieldsToServer(expectedClientObject, null, null)).thenCallRealMethod();
        when(converter.getDateTimeFormat()).thenReturn(mock(DateFormat.class));
        final Resource result = converter.genericFieldsToServer(expectedClientObject, null, null);
        assertSame(result, expectedNewObject);
        assertEquals(result.getVersion(), Resource.VERSION_NEW);
    }

    @Test
    public void toServer_invalidDate() throws Exception {
        ClientResource clientObject = new ClientFolder();
        final String invalidCreationDate = "invalidCreationDate";
        clientObject.setCreationDate(invalidCreationDate);
        clientObject.setLabel(invalidCreationDate);
        final String invalidUpdateDate = "invalidUpdateDate";
        final ResourceLookupImpl resultToUpdate = new ResourceLookupImpl();
        when(converter.toServer(clientObject, resultToUpdate, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, resultToUpdate, options)).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(clientObject, resultToUpdate, options)).thenReturn(resultToUpdate);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(invalidCreationDate)).thenThrow(ParseException.class);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        // check invalid creation date
        IllegalParameterValueException exception = null;
        try {
            converter.toServer(clientObject, resultToUpdate, options);
        } catch (IllegalParameterValueException e) {
            exception = e;
        }
        assertNotNull(exception);
        ErrorDescriptor errorDescriptor = exception.getErrorDescriptor();
        assertNotNull(errorDescriptor);
        String[] parameters = errorDescriptor.getParameters();
        assertNotNull(parameters);
        assertEquals(parameters.length, 2);
        assertEquals(parameters[0], "creationDate");
        assertEquals(parameters[1], invalidCreationDate);
        // reset mock and check invalid update date
        exception = null;
        clientObject.setCreationDate(null);
        clientObject.setUpdateDate(invalidUpdateDate);
        when(dateFormatMock.parse(invalidUpdateDate)).thenThrow(ParseException.class);
        try {
            converter.toServer(clientObject, resultToUpdate, options);
        } catch (IllegalParameterValueException e) {
            exception = e;
        }
        assertNotNull(exception);
        errorDescriptor = exception.getErrorDescriptor();
        assertNotNull(errorDescriptor);
        parameters = errorDescriptor.getParameters();
        assertNotNull(parameters);
        assertEquals(parameters.length, 2);
        assertEquals(parameters[0], "updateDate");
        assertEquals(parameters[1], invalidUpdateDate);
    }

    @Test
    public void toClient() {
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString("/test/uri");
        ClientResource expectedClientObject = new ClientFolder();
        when(converter.getNewClientObjectInstance()).thenReturn(expectedClientObject);
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(converter.toClient(serverObject, options)).thenCallRealMethod();
        converter.toClient(serverObject, options);
        verify(converter).genericFieldsToClient(expectedClientObject, serverObject, options);
    }

    @Test
    public void getNewClientObjectInstance() {
        ResourceConverterImpl<Folder, ClientFolder> converter = new ResourceConverterImpl<Folder, ClientFolder>() {
            @Override
            protected Folder resourceSpecificFieldsToServer(ClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientFolder resourceSpecificFieldsToClient(ClientFolder client, Folder serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        final ClientFolder newClientObjectInstance = converter.getNewClientObjectInstance();
        assertNotNull(newClientObjectInstance);
    }

    @Test
    public void getClientResourceType_defaultClassName() {
        when(converter.getClientTypeClass()).thenReturn(TestClientObjectWithDefaultName.class);
        when(converter.getClientResourceType()).thenCallRealMethod();
        final String classSimpleName = TestClientObjectWithDefaultName.class.getSimpleName();
        final String defaultClientType = classSimpleName.replaceFirst("^.", classSimpleName.substring(0, 1).toLowerCase());
        assertEquals(converter.getClientResourceType(), defaultClientType);
    }

    @Test
    public void getClientResourceType_specifiedName() {
        when(converter.getClientTypeClass()).thenReturn(TestClientObjectWithSpecifiedName.class);
        when(converter.getClientResourceType()).thenCallRealMethod();
        assertEquals(converter.getClientResourceType(), TEST_CLIENT_OBJECT_NAME);
    }

    @Test
    public void getClientResourceType_clientWithXmlTypeAnnotation() {
        when(converter.getClientTypeClass()).thenReturn(TestClientObjectWithXmlTypeAnnotation.class);
        when(converter.getClientResourceType()).thenCallRealMethod();
        assertEquals(converter.getClientResourceType(), TEST_CLIENT_OBJECT_NAME);
    }

    @Test
    public void getClientTypeClass_rawConverter() {

        final ResourceConverterImpl resourceConverter = new ResourceConverterImpl() {
            @Override
            protected Resource resourceSpecificFieldsToServer(ClientResource clientObject, Resource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientResource resourceSpecificFieldsToClient(ClientResource client, Resource serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        IllegalStateException exception = null;
        try {
            resourceConverter.getClientTypeClass();
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void getClientTypeClass() {
        final ResourceConverterImpl resourceConverter = new ResourceConverterImpl<Folder, ClientFolder>() {
            @Override
            protected Folder resourceSpecificFieldsToServer(ClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientFolder resourceSpecificFieldsToClient(ClientFolder client, Folder serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        final Class<?> clientTypeClass = resourceConverter.getClientTypeClass();
        assertTrue(clientTypeClass == ClientFolder.class);
    }

    @Test
    public void toServer() throws Exception {
        ClientResource expectedClientObject = new ClientFolder();
        Resource expectedNewObject = new FolderImpl();
        when(converter.toServer(expectedClientObject, null, null)).thenReturn(expectedNewObject);
        when(converter.toServer(expectedClientObject, null)).thenCallRealMethod();
        final Resource resource = converter.toServer(expectedClientObject, null);
        assertSame(resource, expectedNewObject);
    }

    @Test
    public void getNewResourceInstance() {
        when(converter.getNewResourceInstance()).thenCallRealMethod();
        final String expectedResourceTypeName = "testResourceTypeName";
        when(converter.getServerResourceType()).thenReturn(expectedResourceTypeName);
        converter.objectFactory = mock(ResourceFactory.class);
        final FolderImpl expectedFolder = new FolderImpl();
        when(converter.objectFactory.newResource(null, expectedResourceTypeName)).thenReturn(expectedFolder);
        final Object folder = converter.getNewResourceInstance();
        assertSame(folder, expectedFolder);
    }

    @Test
    public void getServerResourceType() {
        // correct resource converter for folder resources
        ResourceConverterImpl<Folder, ClientFolder> folderConverter = new ResourceConverterImpl<Folder, ClientFolder>() {
            @Override
            protected Folder resourceSpecificFieldsToServer(ClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientFolder resourceSpecificFieldsToClient(ClientFolder client, Folder serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        final String extractedResourceName = folderConverter.getServerResourceType();
        assertEquals(extractedResourceName, Folder.class.getName());

        // incorrect resource converter for unknown resource type
        ResourceConverterImpl<?, ?> rawConverter = new ResourceConverterImpl() {
            @Override
            protected Resource resourceSpecificFieldsToServer(ClientResource clientObject, Resource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientResource resourceSpecificFieldsToClient(ClientResource client, Resource serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        IllegalStateException exception = null;
        try {
            rawConverter.getServerResourceType();
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }


    @XmlRootElement(name = TEST_CLIENT_OBJECT_NAME)
    private class TestClientObjectWithSpecifiedName extends ClientResource {
    }

    @XmlRootElement
    private class TestClientObjectWithDefaultName extends ClientResource {
    }

    @XmlType(name = TEST_CLIENT_OBJECT_NAME)
    private class TestClientObjectWithXmlTypeAnnotation extends ClientResource {
    }
}
