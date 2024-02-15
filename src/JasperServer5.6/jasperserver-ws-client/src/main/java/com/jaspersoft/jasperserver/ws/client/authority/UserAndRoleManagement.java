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
 * UserAndRoleManagement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.client.authority;

public interface UserAndRoleManagement extends java.rmi.Remote {
    public com.jaspersoft.jasperserver.ws.authority.WSUser[] findUsers(com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria criteria) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSUser putUser(com.jaspersoft.jasperserver.ws.authority.WSUser user) throws java.rmi.RemoteException;
    public void deleteUser(com.jaspersoft.jasperserver.ws.authority.WSUser user) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSRole[] findRoles(com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria criteria) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSRole putRole(com.jaspersoft.jasperserver.ws.authority.WSRole role) throws java.rmi.RemoteException;
    public com.jaspersoft.jasperserver.ws.authority.WSRole updateRoleName(com.jaspersoft.jasperserver.ws.authority.WSRole oldRole, java.lang.String newName) throws java.rmi.RemoteException;
    public void deleteRole(com.jaspersoft.jasperserver.ws.authority.WSRole role) throws java.rmi.RemoteException;
}
