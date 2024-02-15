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
package com.jaspersoft.jasperserver.ws.axis2.util;

import com.jaspersoft.jasperserver.remote.common.RemoteServiceCheckedExceptionCallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceInTemplateCaller;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import org.apache.axis.AxisFault;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class RemoteServiceFromWsCallTemplate<ServiceType> implements RemoteServiceCheckedExceptionCallTemplate<ServiceType, AxisFault>{
    public <ResponseType> ResponseType callRemoteService(RemoteServiceInTemplateCaller<ResponseType, ServiceType> responseTypeServiceTypeRemoteServiceInTemplateCaller, ServiceType service) throws AxisFault{
        ResponseType result;
        try{
            result = responseTypeServiceTypeRemoteServiceInTemplateCaller.call(service);
        }catch (RemoteException e){
            throw new AxisFault(e.getMessage());
        }
        return result;
    }
}
