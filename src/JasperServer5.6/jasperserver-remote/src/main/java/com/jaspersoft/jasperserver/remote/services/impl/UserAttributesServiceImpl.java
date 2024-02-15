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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.services.AttributesRemoteService;
import com.jaspersoft.jasperserver.remote.services.GenericAttributesService;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: Zakhar.Tomchenco
 */
@Component("userAttributesService")
public class UserAttributesServiceImpl implements GenericAttributesService<User> {

    @Resource(name = "attributesRemoteService")
    private AttributesRemoteService service;

    public void deleteAttribute(User principal, ProfileAttribute pa) {
        service.deleteAttribute(principal.getUsername(), pa);
    }

    public List<ProfileAttribute> getAttributes(User principal) throws ServiceException {
        return service.getAttributesOfUser(principal.getUsername());
    }

    public ProfileAttribute getAttribute(User principal, String name) throws ServiceException {
        List<ProfileAttribute> attributes = getAttributes(principal);
        for (ProfileAttribute p : attributes){
            if (p.getAttrName().equals(name)) return p;
        }
        return null;
    }

    public void putAttribute(User principal, ProfileAttribute pa) throws IllegalParameterValueException{
        try{
            service.putAttribute(principal.getUsername(), pa);
        } catch (DataIntegrityViolationException dive){
            throw new IllegalParameterValueException("value", pa.getAttrValue());
        }
    }
}
