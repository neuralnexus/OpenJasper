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
 * UserAndRoleManagementServiceImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.axis2.authority;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.remote.common.CallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceWrapperWithCheckedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.ws.authority.WSObjectPermission;
import com.jaspersoft.jasperserver.ws.axis2.util.RemoteServiceFromWsCallTemplate;
import org.apache.axis.AxisFault;
import org.springframework.security.AccessDeniedException;

import java.util.List;

@CallTemplate(RemoteServiceFromWsCallTemplate.class)
public class PermissionsManagementServiceImpl extends RemoteServiceWrapperWithCheckedException<PermissionsService,
        AxisFault> implements PermissionsManagementService {

    public WSObjectPermission[] getPermissionsForObject(final String targetURI) throws AxisFault {
        // checking administrable access

        List objectPermissions = callRemoteService(new ConcreteCaller<List>() {
            public List call(PermissionsService permissionsService) throws RemoteException {
                return permissionsService.getPermissionsForObject(targetURI);
            }
        });

        return PermissionsTranslator.toWSTenantArray(objectPermissions);
    }

    public WSObjectPermission putPermission(WSObjectPermission objectPermission) throws AxisFault {
        final ObjectPermission op = getRemoteService().newObjectPermission();
        PermissionsTranslator.populateObjectPermission(objectPermission, op);

        ObjectPermission targetObjectPermission = callRemoteService(new ConcreteCaller<ObjectPermission>() {
            public ObjectPermission call(PermissionsService permissionsService) throws RemoteException {
                try {
                    return permissionsService.putPermission(op);
                } catch (AccessDeniedException ade) {
                    final String message = "The resource belongs to the organization which is not visible from the recipient's organization.";
                    throw new IllegalParameterValueException(new ErrorDescriptor.Builder().setMessage(message).getErrorDescriptor());
                }

            }
        });

        return PermissionsTranslator.toWSObjectPermission(targetObjectPermission);
    }

    public void deletePermission(WSObjectPermission objectPermission) throws AxisFault {
        final ObjectPermission op = getRemoteService().newObjectPermission();
        PermissionsTranslator.populateObjectPermission(objectPermission, op);

        callRemoteService(new ConcreteCaller<Object>() {
            public Object call(PermissionsService permissionsService) throws RemoteException {
                permissionsService.deletePermission(op);
                return null;
            }
        });
    }

    public void setPermissionsService(PermissionsService permissionsService) {
        this.remoteService = permissionsService;
    }
}
