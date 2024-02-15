package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class RoleConverterTest {
    private final Role server = new RoleImpl();
    private final ClientRole client = new ClientRole();
    private final RoleConverter converter  = new RoleConverter();

    @BeforeMethod
    public void setUp() throws Exception {
        server.setExternallyDefined(true);
        server.setRoleName("server");
        server.setTenantId("st");

        client.setName("client");
        client.setExternallyDefined(true);
        client.setTenantId("seva");
    }

    @Test
    public void testToClient() throws Exception {
        ClientRole converted = converter.toClient(server, null);

        assertEquals(converted.getName(), server.getRoleName());
        assertEquals(converted.getTenantId(), server.getTenantId());
        assertEquals(converted.isExternallyDefined(), server.isExternallyDefined());
    }

    @Test
    public void testToServer() throws Exception {
        Role converted = converter.toServer(client, null);

        assertEquals(converted.getRoleName(), client.getName());
        assertEquals(converted.getTenantId(), client.getTenantId());
        assertEquals(converted.isExternallyDefined(), client.isExternallyDefined());
    }

    @Test
    public void testToServer_update() throws Exception {
        Role converted = converter.toServer(client, server, null);

        assertEquals(converted, server);
        assertEquals(converted.getRoleName(), client.getName());
        assertEquals(converted.getTenantId(), client.getTenantId());
        assertEquals(converted.isExternallyDefined(), client.isExternallyDefined());
    }
}
