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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.dto.common.ClientTypeUtility;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ContentResourceConverterTest {
    private ContentResourceConverter converter = new ContentResourceConverter();
    private byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
    private String encoded = DatatypeConverter.printBase64Binary(data);

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeUtility.extractClientType(ClientFile.class));
        assertEquals(converter.getServerResourceType(), ContentResource.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception{
        final String expectedFileType = ContentResource.TYPE_ODS;
        final ClientFile clientObject = new ClientFile();
        final ContentResource serverObject = new ContentResourceImpl();
        clientObject.setType(ClientFile.FileType.ods);
        clientObject.setContent(encoded);
        final ContentResource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, new ArrayList<Exception>(), null);
        assertNotNull(result);
        assertEquals(result.getFileType(), expectedFileType);
        assertEquals(result.getData(), data);
    }


    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedFileType = ContentResource.TYPE_ODS;
        final ClientFile clientObject = new ClientFile();
        final ContentResource serverObject = new ContentResourceImpl();
        serverObject.setFileType(expectedFileType);
        serverObject.setData(data);
        final ClientFile result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertNotNull(result);
        assertEquals(result.getType().name(), expectedFileType);
        assertEquals(result.getContent(), null);
    }
}
