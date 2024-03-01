/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.remote.common;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;

/**
 * This class can be used to wrap any remote service and share it's logic via REST, SOAP or other.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 * @param <RemoteServiceType> concrete type of remote service to wrap.
 */
public class RemoteServiceWrapper<RemoteServiceType> extends AbstractRemoteServiceWrapper<RemoteServiceType, RemoteServiceCallTemplate<RemoteServiceType>>{


    /**
     * This method is used to call remote service within the same template.
     * Default template can be changed via @CallTemplate annotation.
     *
     * @param caller contain concrete remote service call logic
     * @param <ResponseType> - concrete type of caller response
     * @return caller response
     */
    protected final <ResponseType> ResponseType callRemoteService(ConcreteCaller<ResponseType> caller){
        return getTemplate().callRemoteService(caller, getRemoteService());
    }

    @Override
    protected AbstractCallTemplate getDefaultTemplate() {
        return new RemoteServiceCallTemplate<RemoteServiceType>() {
            public <ResponseType> ResponseType callRemoteService(RemoteServiceInTemplateCaller<ResponseType, RemoteServiceType> responseTypeRemoteServiceTypeRemoteServiceInTemplateCaller, RemoteServiceType service) {
                try {
                    return responseTypeRemoteServiceTypeRemoteServiceInTemplateCaller.call(service);
                } catch (ErrorDescriptorException e) {
                    throw new RuntimeException("Unexpected error occurs", e);
                }
            }
        };
    }
}
