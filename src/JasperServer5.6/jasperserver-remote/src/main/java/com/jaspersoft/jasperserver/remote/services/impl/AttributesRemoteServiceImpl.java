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
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.services.AttributesRemoteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: AttributesRemoteService.java 21826 2012-01-04 09:43:55Z ykovalchyk $
 */
@Component("attributesRemoteService")
public class AttributesRemoteServiceImpl implements AttributesRemoteService {

    private final static Log log = LogFactory.getLog(AttributesRemoteServiceImpl.class);
    @Resource(name = "concreteUserAuthorityService")
    private UserAuthorityService userAuthorityService;
    @Resource
    private ProfileAttributeService profileAttributeService;

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }


    public void deleteAttribute(String userName, ProfileAttribute pa) {
        User user = userAuthorityService.getUser(null, userName);
        if (user==null){
            throw new ServiceException(ServiceException.RESOURCE_NOT_FOUND, "user: "+userName+" not found ");
        }

        if (log.isDebugEnabled()) {
            log.debug("user "+user.getUsername()+" was found");
        }

        List<ProfileAttribute> l = profileAttributeService.getProfileAttributesForPrincipal(null, user);

        for (ProfileAttribute att:l ){
            if (att.getAttrName().equals(pa.getAttrName())){
                att.setPrincipal(user);
                profileAttributeService.deleteProfileAttribute(null, att);
                if (log.isDebugEnabled()) {
                    log.debug("attribute "+pa.getAttrName()+" was deleted successfully from "+ userName);
                }
                return;
            }
        }

        throw new ServiceException(ServiceException.RESOURCE_NOT_FOUND, "attribute with key="+pa.getAttrName()+" does not exist");
    }


    public String getAttribute(String attName) throws ServiceException{
        return profileAttributeService.getCurrentUserPreferenceValue(attName);
    }

    @SuppressWarnings("unchecked")
    public List<ProfileAttribute> getAttributesOfUser(String userName) throws ServiceException {
        User user = userAuthorityService.getUser(null, userName);
        if (user == null){
            throw new ServiceException(404, ResourceNotFoundException.ERROR_CODE_RESOURCE_NOT_FOUND);
        }
        return profileAttributeService.getProfileAttributesForPrincipal(null, user);
    }


    public void putAttribute(String userName, ProfileAttribute pa) {
        User user = userAuthorityService.getUser(null, userName);
        if ( user==null){
            throw new IllegalArgumentException("could not find user: "+ userName);

        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("user "+user.getUsername()+" was found");
            }
            pa.setPrincipal(user);
            profileAttributeService.putProfileAttribute(null, pa);
            if (log.isDebugEnabled()) {
                log.debug("attribute was set successfully");
            }
        }

    }
}
