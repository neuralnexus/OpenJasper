package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class UserAttributesConverterTest {
    private final ProfileAttribute server = new ProfileAttributeImpl();
    private final ClientUserAttribute client = new ClientUserAttribute();
    private final UserAttributesConverter converter  = new UserAttributesConverter();

    @BeforeMethod
    public void setUp() throws Exception {
        server.setAttrName("sname");
        server.setAttrValue("scalue");

        client.setName("cname");
        client.setValue("cvallue");
    }

    @Test
    public void testToClient() throws Exception {
        ClientUserAttribute converted = converter.toClient(server, null);

        assertEquals(converted.getName(), server.getAttrName());
        assertEquals(converted.getValue(), server.getAttrValue());
    }

    @Test
    public void testToServer() throws Exception {
        ProfileAttribute converted = converter.toServer(client, null);

        assertEquals(converted.getAttrName(), client.getName());
        assertEquals(converted.getAttrValue(), client.getValue());
    }

    @Test
    public void testToServer_update() throws Exception {
        ProfileAttribute converted = converter.toServer(client, server, null);

        assertEquals(converted.getAttrName(), client.getName());
        assertEquals(converted.getAttrValue(), client.getValue());
    }

}
