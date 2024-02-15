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
package com.jaspersoft.jasperserver.rest.services;

import com.jaspersoft.jasperserver.core.util.validators.InputValidator;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.authority.WSUserSearchCriteria;
import com.jaspersoft.jasperserver.ws.axis2.authority.UserAndRoleManagementService;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

/**
 * @author carbiv
 * @version $Id: RESTUser.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service("restUserService")
public class RESTUser extends RESTAbstractService {
    private final static Log log = LogFactory.getLog(RESTUser.class);
    @Resource(name = "concreteUserAndRoleManagementService")
    private UserAndRoleManagementService userAndRoleManagementService;

    @javax.annotation.Resource(name = "emailInputValidator")
    private InputValidator emailValidator;

    public UserAndRoleManagementService getUserAndRoleManagementService() {
        return userAndRoleManagementService;
    }

    public void setUserAndRoleManagementService(UserAndRoleManagementService userAndRoleManagementService) {
        this.userAndRoleManagementService = userAndRoleManagementService;
    }

    public InputValidator getEmailValidator() {
        return emailValidator;
    }

    public void setEmailValidator(InputValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    @Transactional
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        super.execute(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        String searchCriteria = getUserSearchInformation(req.getPathInfo());
        WSUser[] users = null;
        WSUserSearchCriteria wsUserSearchCriteria = restUtils.getWSUserSearchCriteria(searchCriteria);

        try {
            // get the resources....
            users = userAndRoleManagementService.findUsers(wsUserSearchCriteria);
        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, "could not locate users in uri: " + wsUserSearchCriteria.getName() + axisFault.getLocalizedMessage());
        }
        if (log.isDebugEnabled()) {
            log.debug("" + users.length + " users were found");
        }

        String marshal = generateSummeryReport(users);
        if (log.isDebugEnabled()) {
            log.debug("Marshaling OK");
        }
        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, marshal);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        try {
            String userName = restUtils.extractResourceName(req.getPathInfo());


            WSUser user = restUtils.populateServiceObject(restUtils.unmarshal(WSUser.class, req.getInputStream()));
            if (log.isDebugEnabled()) {
                log.debug("un Marshaling OK");
            }

            if (validateUserForPutOrUpdate(user)) {
                if (!isAlreadyAUser(user)) {
                    userAndRoleManagementService.putUser(user);
                    restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, "");
                } else {
                    throw new ServiceException(HttpServletResponse.SC_FORBIDDEN, "user " + user.getUsername() + "already exists");
                }
            } else
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "check request parameters");

        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, axisFault.getLocalizedMessage());
        } catch (IOException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
        } catch (JAXBException e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
        }

    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        try{
            WSUserSearchCriteria c = restUtils.getWSUserSearchCriteria(getUserSearchInformation(req.getPathInfo()));

            WSUser userToDelete = new WSUser();
            userToDelete.setUsername(c.getName());
            userToDelete.setTenantId(c.getTenantId());

            if (isLoggedInUser(restUtils.getCurrentlyLoggedUser(), userToDelete)) {
                throw new ServiceException(HttpServletResponse.SC_FORBIDDEN, "user: "+userToDelete.getUsername() +" can not to delete himself");
            } else if (validateUserForGetOrDelete(userToDelete)){
                if (isAlreadyAUser(userToDelete)){
                    userAndRoleManagementService.deleteUser(userToDelete);
                } else {
                    throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, "user: "+userToDelete.getUsername() +" was not found");
                }
            } else {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "check request parameters");
            }
            if (log.isDebugEnabled()) {
                log.debug(userToDelete.getUsername()+" was deleted");
            }

            restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
        }
        catch (AxisFault axisFault) {
            throw new ServiceException( HttpServletResponse.SC_BAD_REQUEST, axisFault.getLocalizedMessage());
        } catch (IOException e) {
            throw new ServiceException( HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    protected boolean isLoggedInUser(UserDetails currentlyLoggedUser, WSUser userToDelete) {
        return currentlyLoggedUser.getUsername().equals(userToDelete.getUsername());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException
    {
        try{
            String userName = restUtils.extractResourceName(req.getPathInfo());

            WSUser user = restUtils.unmarshal(WSUser.class, req.getInputStream());
            if (isUserNameValid(userName, user))
                this.updateUser(user);
            else {
                throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "check your request parameters");
            }
            restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
        }
        catch (AxisFault axisFault) {
            throw new ServiceException( axisFault.getLocalizedMessage());
        } catch (IOException e) {
            throw new ServiceException( e.getLocalizedMessage());
        } catch (JAXBException e) {
            throw new ServiceException( HttpServletResponse.SC_BAD_REQUEST, "could not marshal the user descriptor");
        }
    }

    protected Boolean isUserNameValid(String userName, WSUser user){
        return userName != null && user != null && userName.equals(user.getUsername());
    }


     private void updateUser(WSUser user) throws ServiceException{
        try {
            WSUserSearchCriteria criteria = new WSUserSearchCriteria();

            criteria.setName((user.getUsername()));
            criteria.setTenantId(user.getTenantId());

            if (log.isDebugEnabled()) {
                log.debug("criteria was created: user: "+criteria.getName()+" tenant: "+criteria.getTenantId());
            }

            WSUser[] users = userAndRoleManagementService.findUsers(criteria);
            if (users.length != 0){
                WSUser existing = users[0];
                doUpdateUser(existing, user);

                userAndRoleManagementService.putUser(existing);
                if (log.isDebugEnabled()) {
                    log.debug("user: "+user.getUsername()+" was updated");
                }
            }
            else{
                if (log.isDebugEnabled()) {
                    log.debug("could not find user: "+user.getUsername()+" to update");
                }
                throw new ServiceException(ServiceException.RESOURCE_NOT_FOUND, "User "+ user.getUsername()+ " not found");
            }
        } catch (AxisFault axisFault) {
            throw new ServiceException(axisFault.getLocalizedMessage());
        }
    }

    private String generateSummeryReport(WSUser[] users) throws ServiceException{
        try{
            StringWriter sw = new StringWriter();

            sw.append("<users>");


            for (int i=0 ; i<users.length ; i++){
                // user password shouldn't be in a response
                users[i].setPassword(null);
                restUtils.getMarshaller(WSUser.class, WSRole.class).marshal(users[i], sw);
                if (log.isDebugEnabled()) {
                    log.debug("finished marshaling user: "+users[i].getTenantId());
                }
            }
            sw.append("</users>");
            return sw.toString();
        }
        catch (JAXBException e) {
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private String getUserSearchInformation(String path){
        String criteria = path.substring(path.indexOf("/user/")+"/user/".length(), path.length());
        if (criteria.endsWith("/")){
            criteria = criteria.substring(0,criteria.length() - 2);
        }
        return criteria;
    }

    // this method is just for i will be able to override it in pro
    protected WSUser fillUserDetails(WSUser user){
        return user;
    }

    private boolean isAlreadyAUser(WSUser user) throws AxisFault {
        WSUserSearchCriteria criteria = new WSUserSearchCriteria();
        criteria.setName(user.getUsername());
        criteria.setTenantId(user.getTenantId());

        return userAndRoleManagementService.findUsers(criteria).length>0;

    }

    // all the information for the user is set for user creation
    protected boolean validateUserForPutOrUpdate(WSUser user) {
        user = fillUserDetails(user);

        boolean ret =   (user.getEmailAddress()==null || "".equals(user.getEmailAddress()) || emailValidator.isValid(user.getEmailAddress())) &&
                validateUserForGetOrDelete(user) &&
                (user.getExternallyDefined() ? true : (user.getPassword() != null && !user.getPassword().equals("")));
        return ret;
    }

    // can execute any GET/DELETE
    protected boolean validateUserForGetOrDelete(WSUser user){
        user = fillUserDetails(user);
        return user.getUsername()!=null && !user.getUsername().equals("");

    }

    private void doUpdateUser(WSUser oldUser, WSUser newUser){
        // username and tenant omitted
        if (newUser.getEmailAddress() != null) oldUser.setEmailAddress(newUser.getEmailAddress());
        if (newUser.getFullName() != null) oldUser.setFullName(newUser.getFullName());
        if (newUser.getEnabled() != null) oldUser.setEnabled(newUser.getEnabled());
        if (newUser.getExternallyDefined() != null) oldUser.setExternallyDefined(newUser.getExternallyDefined());
        if (newUser.getRoles() != null) oldUser.setRoles(newUser.getRoles());
        if (newUser.getPassword() != null) {
            if (!newUser.getPassword().equals(oldUser.getPassword())){
                oldUser.setPreviousPasswordChangeTime(new Date());
            }
            oldUser.setPassword(newUser.getPassword());
        }
    }
}
