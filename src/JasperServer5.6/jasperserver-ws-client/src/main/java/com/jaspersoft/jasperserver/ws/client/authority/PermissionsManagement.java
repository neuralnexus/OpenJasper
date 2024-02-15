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

/**
 * PermissionsManagement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.client.authority;

public interface PermissionsManagement extends java.rmi.Remote {
    public com.jaspersoft.jasperserver.ws.authority.WSObjectPermission[] getPermissionsForObject(java.lang.String targetURI) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSObjectPermission putPermission(com.jaspersoft.jasperserver.ws.authority.WSObjectPermission objectPermission) throws java.rmi.RemoteException;
    public void deletePermission(com.jaspersoft.jasperserver.ws.authority.WSObjectPermission objectPermission) throws java.rmi.RemoteException;
}
