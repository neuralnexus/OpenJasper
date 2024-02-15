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

package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ErrorDescriptorTest extends BaseDTOPresentableTest<ErrorDescriptor> {

    @Override
    protected List<ErrorDescriptor> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setMessage("message2"),
                createFullyConfiguredInstance().setErrorCode("402"),
                createFullyConfiguredInstance().setErrorUid("122"),
                createFullyConfiguredInstance().setParameters("3", "4"),
                createFullyConfiguredInstance().setDetails(Arrays.asList(new ErrorDescriptor().setErrorUid("uid2"), new ErrorDescriptor().setErrorCode("405"))),
                createFullyConfiguredInstance().setProperties(Arrays.asList(new ClientProperty().setValue("value"), new ClientProperty().setKey("key2"))),
                // with null values
                createFullyConfiguredInstance().setMessage(null),
                createFullyConfiguredInstance().setErrorCode(null),
                createFullyConfiguredInstance().setErrorUid(null),
                createFullyConfiguredInstance().setParameters((String) null),
                createFullyConfiguredInstance().setDetails(null),
                createFullyConfiguredInstance().setProperties(null)
        );
    }

    @Override
    protected ErrorDescriptor createFullyConfiguredInstance() {
        ErrorDescriptor errorDescriptor = new ErrorDescriptor();
        errorDescriptor.setMessage("message");
        errorDescriptor.setErrorCode("401");
        errorDescriptor.setErrorUid("121");
        errorDescriptor.setParameters("1", "2");
        errorDescriptor.setDetails(Arrays.asList(new ErrorDescriptor(), new ErrorDescriptor().setErrorCode("403")));
        errorDescriptor.setProperties(Arrays.asList(new ClientProperty(), new ClientProperty().setKey("key")));
        return errorDescriptor;
    }

    @Override
    protected ErrorDescriptor createInstanceWithDefaultParameters() {
        return new ErrorDescriptor();
    }

    @Override
    protected ErrorDescriptor createInstanceFromOther(ErrorDescriptor other) {
        return new ErrorDescriptor(other);
    }

    @Test
    public void addDetailsToErrorDescriptorWorksForSampleList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor().setDetails(new ArrayList<ErrorDescriptor>() {{
            add(new ErrorDescriptor().setErrorCode("403"));
        }});

        ErrorDescriptor[] errorDescriptors = {new ErrorDescriptor().setErrorUid("2323"), new ErrorDescriptor().setErrorCode("404")};

        List<ErrorDescriptor> list = new ArrayList<ErrorDescriptor>(errorDescriptor1.getDetails());
        list.addAll(Arrays.asList(errorDescriptors));

        errorDescriptor1.addDetails(errorDescriptors);

        assertArrayEquals(list.toArray(), errorDescriptor1.getDetails().toArray());
    }

    @Test
    public void addDetailsToErrorDescriptorWorksForNullList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor();

        ErrorDescriptor[] errorDescriptors = {new ErrorDescriptor().setErrorUid("2323"), new ErrorDescriptor().setErrorCode("404")};
        errorDescriptor1.addDetails(errorDescriptors);

        assertArrayEquals(errorDescriptors, errorDescriptor1.getDetails().toArray());
    }

    @Test
    public void addPropertiesToErrorDescriptorWorksForSampleList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor().setProperties(new ArrayList<ClientProperty>() {{
            add(new ClientProperty().setKey("key2"));
        }});

        ClientProperty[] clientProperties = {new ClientProperty().setKey("key1"), new ClientProperty().setValue("value1")};

        List<ClientProperty> list = new ArrayList<ClientProperty>(errorDescriptor1.getProperties());
        list.addAll(Arrays.asList(clientProperties));

        errorDescriptor1.addProperties(clientProperties);

        assertArrayEquals(list.toArray(), errorDescriptor1.getProperties().toArray());
    }

    @Test
    public void addPropertiesToErrorDescriptorWorksForNullList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor();

        ClientProperty[] clientProperty = {new ClientProperty().setKey("key1"), new ClientProperty().setValue("value1")};
        errorDescriptor1.addProperties(clientProperty);

        assertArrayEquals(clientProperty, errorDescriptor1.getProperties().toArray());
    }

    @Test
    public void addParametersToErrorDescriptorWorksForSampleList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor().setParameters("test");

        errorDescriptor1.addParameters("test2", "test3");

        assertArrayEquals(new String[]{"test2", "test3"}, errorDescriptor1.getParameters());
    }

    @Test
    public void addNullParameterToErrorDescriptorWorksForSampleList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor().setParameters("test");

        errorDescriptor1.addParameters("test2", null);

        assertArrayEquals(new String[]{"test2", null}, errorDescriptor1.getParameters());
    }

    @Test
    public void addParametersToErrorDescriptorWorksForNullList() {
        ErrorDescriptor errorDescriptor1 = new ErrorDescriptor();

        errorDescriptor1.addParameters("test2", "test3");

        assertArrayEquals(new String[]{"test2", "test3"}, errorDescriptor1.getParameters());
    }

    @Test
    public void setExceptionForErrorDescriptorWorksForRuntimeException() {
        ErrorDescriptor errorDescriptor = new ErrorDescriptor();
        Throwable exception = new RuntimeException("test");
        errorDescriptor.setException(exception);

        assertEquals(exception, errorDescriptor.getException());
    }
}
