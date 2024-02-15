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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jaxrs.jdbcdrivers;

import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.dto.jdbcdrivers.JdbcDriverInfo;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.Link;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: JdbcDriversJaxrsService.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
@Path("/jdbcDrivers")
public class JdbcDriversJaxrsService {
    @Resource
    private JdbcDriverService jdbcDriverService;
    @Resource
    protected Map<String, Map<String, Object>> jdbcConnectionMap;
    @Resource
    protected List<String> configurationAllowedRoles;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<JdbcDriverInfo> getJdbcDrivers() {
        List<JdbcDriverInfo> jdbcDriverInfos = new ArrayList<JdbcDriverInfo>(jdbcConnectionMap.size());
        final Set<String> registeredDriverClassNames = new HashSet<String>(jdbcDriverService.getRegisteredDriverClassNames());

        for (Map.Entry<String, Map<String, Object>> entry : jdbcConnectionMap.entrySet()) {
            JdbcDriverInfo info = new JdbcDriverInfo().setName(entry.getKey());
            jdbcDriverInfos.add(info);
            final Map<String, Object> value = entry.getValue();
            final String jdbcDriverClass = (String) value.get("jdbcDriverClass");
            final String isDefault = (String) value.get("default");
            info.setAvailable(jdbcDriverService.isRegistered(jdbcDriverClass))
                    .setJdbcDriverClass(jdbcDriverClass)
                    .setDefault(isDefault != null ? Boolean.valueOf(isDefault) : null)
                    .setJdbcUrl((String) value.get("jdbcUrl")).setLabel((String) value.get("label"));
            // XML configuration assures cast safety
            @SuppressWarnings("unchecked")
            final Map<String, String> defaultValues = (Map<String, String>) value.get("defaultValues");
            if (defaultValues != null) {
                List<ClientProperty> defaultProperties = new ArrayList<ClientProperty>(defaultValues.size());
                info.setDefaultValues(defaultProperties);
                for (String currentProperty : defaultValues.keySet()) {
                    defaultProperties.add(new ClientProperty(currentProperty, defaultValues.get(currentProperty)));
                }
            }
            registeredDriverClassNames.remove(jdbcDriverClass);
        }
        if (!registeredDriverClassNames.isEmpty()) {
            for (String currentClass : registeredDriverClassNames) {
                jdbcDriverInfos.add(new JdbcDriverInfo().setName(currentClass).setJdbcDriverClass(currentClass).setLabel(currentClass));
            }
        }
        return jdbcDriverInfos;
    }

    @GET
    @Produces("application/hal+json")
    public HypermediaRepresentation getJdbcDriversHipermediaRepresentation() {
        final List<JdbcDriverInfo> jdbcDrivers = getJdbcDrivers();
        final HypermediaRepresentation hypermediaRepresentation = new HypermediaRepresentation() {
            public List<JdbcDriverInfo> getJdbcDrivers() {
                return jdbcDrivers;
            }
        };
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        Set<Role> roles = ((User) existingAuth.getPrincipal()).getRoles();
        for (Role currentRole : roles) {
            if (configurationAllowedRoles.contains(currentRole.getRoleName())) {
                hypermediaRepresentation.addLink(new Link()
                        .setRelation(Relation.create)
                        .setProfile("POST"));
                hypermediaRepresentation.addLink(new Link()
                        .setRelation(Relation.edit)
                        .setProfile("PUT"));
            }
        }
        return hypermediaRepresentation;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JdbcDriverInfo uploadDriver(@FormDataParam("className") String className, FormDataMultiPart multiPart) {
        Map<String, byte[]> driverFilesData = new HashMap<String, byte[]>();
        for (String currentKey : multiPart.getFields().keySet()) {
            if ("className".equals(currentKey)) continue;
            List<FormDataBodyPart> currentPartsList = multiPart.getFields(currentKey);
            if (currentPartsList != null && !currentPartsList.isEmpty()) {
                final FormDataBodyPart formDataBodyPart = currentPartsList.get(0);
                final String fileName = formDataBodyPart.getContentDisposition().getFileName();
                final byte[] jarContent = formDataBodyPart.getEntityAs(byte[].class);
                if (fileName != null && !"".equals(fileName) && jarContent != null && jarContent.length > 0) {
                    driverFilesData.put(fileName, jarContent);
                }
            }
        }

        try {
            jdbcDriverService.setDriver(className, driverFilesData);
        } catch (NoClassDefFoundError e) {
            throw new IllegalParameterValueException("className", className);
        } catch (ClassNotFoundException e) {
            throw new IllegalParameterValueException("className", className);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage(), e);
        }
        List<JdbcDriverInfo> infos = getJdbcDrivers();
        JdbcDriverInfo result = null;
        for (JdbcDriverInfo info : infos) {
            if (className.equals(info.getJdbcDriverClass())) {
                result = info;
                break;
            }
        }

        if (result == null) {
            throw new IllegalStateException("JdbcDriverInfo for class " + className + " isn't found");
        }

        return result;
    }
}
