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

package com.jaspersoft.jasperserver.ws.client.controller;

import com.jaspersoft.jasperserver.ws.client.authority.PermissionsManagement;
import com.jaspersoft.jasperserver.ws.client.authority.PermissionsManagementServiceLocator;
import com.jaspersoft.jasperserver.ws.client.authority.UserAndRoleManagement;
import com.jaspersoft.jasperserver.ws.client.authority.UserAndRoleManagementServiceLocator;

import javax.xml.rpc.ServiceException;

/**
 * Web services client manager.
 *
 * @author Yuriy Plakosh
 * @author Vladimir Sabadosh
 */
public class WSClientManager {
    public static final String PERMISSIONS_SERVICE_NAME = "PermissionsManagementService";
    public static final String USER_AND_ROLE_SERVICE_NAME = "UserAndRoleManagementService";

    public static PermissionsManagement getPermissionsManagement(String baseAddress) throws ServiceException {
        return new PermissionsManagementServiceLocator(getServiceAddress(baseAddress, PERMISSIONS_SERVICE_NAME))
                .getPermissionsManagementServicePort();
    }

    public static UserAndRoleManagement getUserAndRoleManagement(String baseAddress) throws ServiceException {
        return new UserAndRoleManagementServiceLocator(getServiceAddress(baseAddress, USER_AND_ROLE_SERVICE_NAME))
                .getUserAndRoleManagementServicePort();
    }

    private static String getServiceAddress(String baseAddress, String serviceName) {
        return new StringBuilder(baseAddress).append("/").append(serviceName).toString();
    }    
}
