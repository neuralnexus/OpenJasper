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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.BuiltInParameterProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Parameters for reports and queries from the user profile are injected.
 *
 * @author Sherman Wood (sgwood@users.sourceforge.net)
 * @version $Id: UserProfileBuiltInParameterProvider.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class UserProfileBuiltInParameterProvider implements BuiltInParameterProvider, Serializable {

    public static final String LOGGED_IN_USER_PARAMETER_PREFIX = "LoggedInUser";

    /*
     * Automatically added by JasperServer when running a report
     *
     * $P{LoggedInUser}
     * $P{LoggedInUsername}
     *
     * This will be available if explictly included in the query or report
     *
     * $P{LoggedInUserFullName}
     * $P{LoggedInUserEmailAddress}
     * $P{LoggedInUserEnabled}
     * $P{LoggedInUserExternallyDefined}
     * $P{LoggedInUserTenantId}
     * $P{LoggedInUserRoles} -> return collection of role names
     * $P{LoggedInUserAttributes} -> return Map of attributes
     * $P{LoggedInUserAttributeNames} -> return collection of attribute names
     * $P{LoggedInUserAttributeValues} -> return collection of attribute values
     * $P{LoggedInUserAttribute_att1} -> return attribute value for matched attribute name
     */

    public static final String LOGGED_IN_USER_NAME_PARAMETER = LOGGED_IN_USER_PARAMETER_PREFIX + "name";

    public static final String[] LOGGED_IN_USER_STANDARD_PARAMETERS = {
        LOGGED_IN_USER_PARAMETER_PREFIX,
        LOGGED_IN_USER_NAME_PARAMETER
    };

    /**
     * Each element is a JRParameter, value
     *
     * @param context
     * @param jrParameters
     * @param parameters
     * @return List<Object[]> [JRParameter, value]
     */
    public List<Object[]> getParameters(ExecutionContext context, List jrParameters, Map parameters) {

        List<Object[]> userProfileParameters = new ArrayList<Object[]>();

        for (String parameterName : LOGGED_IN_USER_STANDARD_PARAMETERS) {
            Object[] result = getParameter(context, jrParameters, parameters, parameterName);
            if (result != null) {
                userProfileParameters.add(result);
            }
        }

        return userProfileParameters;
    }

    public Object[] getParameter(ExecutionContext context, List jrParameters, Map parameters, String name) {

        if (name == null || !name.toLowerCase().startsWith(LOGGED_IN_USER_PARAMETER_PREFIX.toLowerCase())) {
            return null;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof MetadataUserDetails)) {
            return null;
        }
        MetadataUserDetails userDetails = (MetadataUserDetails) auth.getPrincipal();

        // return a copy, so we are not handing out passwords
        if (name.equalsIgnoreCase(LOGGED_IN_USER_PARAMETER_PREFIX)) {
            MetadataUserDetails returnUserDetails = null;
            try {
                // Write the object out to a byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(userDetails);
                out.flush();
                out.close();

                // Make an input stream from the byte array and read
                // a copy of the object back in.
                ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
                returnUserDetails = (MetadataUserDetails) in.readObject();
            } catch(IOException e) {
                e.printStackTrace();
                return null;
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                return null;
            }
            returnUserDetails.setPassword(null);
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, User.class), returnUserDetails};
        }

        String variableName = name.substring(LOGGED_IN_USER_PARAMETER_PREFIX.length());

        if (variableName.equalsIgnoreCase("name")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, String.class),
                userDetails.getUsername() != null ? userDetails.getUsername() : ""};
        } else if (variableName.equalsIgnoreCase("Fullname")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, String.class),
                userDetails.getFullName() != null ? userDetails.getFullName() : ""};
        } else if (variableName.equalsIgnoreCase("EmailAddress")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, String.class),
                userDetails.getEmailAddress() != null ? userDetails.getEmailAddress() : ""};
        } else if (variableName.equalsIgnoreCase("Enabled")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, Boolean.class), Boolean.valueOf(userDetails.isEnabled())};
        } else if (variableName.equalsIgnoreCase("ExternallyDefined")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, Boolean.class), Boolean.valueOf(userDetails.isExternallyDefined())};
        } else if (variableName.equalsIgnoreCase("TenantId")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, String.class),
                userDetails.getTenantId() != null ? userDetails.getTenantId() : ""};
        } else if (variableName.startsWith("Roles")) {
            Collection<String> roleNames = new ArrayList<String>(userDetails.getRoles().size());
            for (Object o : userDetails.getRoles()) {
                Role r = (Role) o;
                roleNames.add(r.getRoleName());
            }
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, Collection.class), roleNames};
        } else if (variableName.equalsIgnoreCase("Attributes")) {
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, Collection.class), userDetails.getAttributes()};
        } else if (variableName.equalsIgnoreCase("AttributeNames")) {
            Collection<String> keys;
            if (userDetails!=null && userDetails.getAttributes()!=null){
                keys= new ArrayList<String>(userDetails.getAttributes().size());
                for (Object o : userDetails.getAttributes()) {
                    ProfileAttribute att = (ProfileAttribute) o;
                    keys.add(att.getAttrName());
                }
            }
            else {
                // we can get to here in cases like external authentication or where we have created the user without attributes
                keys = new ArrayList<String>(0);
            }
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, Collection.class), keys};
        } else if (variableName.equalsIgnoreCase("AttributeValues")) {
            Collection<String> values = new ArrayList<String>(userDetails.getAttributes().size());
            for (Object o : userDetails.getAttributes()) {
                ProfileAttribute att = (ProfileAttribute) o;
                values.add(att.getAttrValue());
            }
            return new Object[] {JRQueryExecuterAdapter.makeParameter(name, Collection.class), values};
        } else if (variableName.startsWith("Attribute_")) {
            String attrName = variableName.substring("Attribute_".length());
            for (Object o : userDetails.getAttributes()) {
                ProfileAttribute att = (ProfileAttribute) o;

                if (att.getAttrName().equalsIgnoreCase(attrName)) {
                    return new Object[] {JRQueryExecuterAdapter.makeParameter(name, String.class), att.getAttrValue()};
                }
            }
        }
        return null;
    }

    public String getLoggedInUserParameterPrefix() {
        return LOGGED_IN_USER_PARAMETER_PREFIX;
    }
}
