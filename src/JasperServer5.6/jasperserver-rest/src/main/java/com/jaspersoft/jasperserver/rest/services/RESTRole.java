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

import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.rest.RESTAbstractService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSRoleSearchCriteria;
import com.jaspersoft.jasperserver.ws.authority.WSUser;
import com.jaspersoft.jasperserver.ws.axis2.authority.UserAndRoleManagementService;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author carbiv
 * @version $Id: RESTRole.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service("restRoleService")
public class RESTRole extends RESTAbstractService {

    private final static Log log = LogFactory.getLog(RESTRole.class);
    private final static String SERVICE_NAME = "role";
    @Resource(name = "concreteUserAndRoleManagementService")
    private UserAndRoleManagementService userAndRoleManagementService;
    @Resource
    private ConfigurationBean configurationBean;

    public ConfigurationBean getConfigurationBean() {
        return configurationBean;
    }

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    public UserAndRoleManagementService getUserAndRoleManagementService() {
        return userAndRoleManagementService;
    }

    public void setUserAndRoleManagementService(UserAndRoleManagementService userAndRoleManagementService) {
        this.userAndRoleManagementService = userAndRoleManagementService;
    }
    @Transactional
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        super.execute(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        WSRoleSearchCriteria criteria = restUtils.populateServiceObject(req.getPathInfo(), req.getParameterMap(), WSRoleSearchCriteria.class);

        WSRole[] roles = null;

        try {
            // get the resources....
            roles = userAndRoleManagementService.findRoles(criteria);
        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, "could not locate roles in uri: " + criteria.getRoleName() + axisFault.getLocalizedMessage());
        }

        if (log.isDebugEnabled()) {
            log.debug(roles.length + " roles were found");
        }

        String marshal = generateSummeryReport(roles);
        if (log.isDebugEnabled()) {
            log.debug("Marshaling OK");
        }
        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, marshal);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        try {
            WSRole role = restUtils.unmarshal(WSRole.class, req.getInputStream());
            role = restUtils.populateServiceObject(role);

            if (userAndRoleManagementService.findRoles(wsRoleToWSRoleSearchCriteria(role)).length == 0) {
                userAndRoleManagementService.putRole(role);
                restUtils.setStatusAndBody(HttpServletResponse.SC_CREATED, resp, "");
            } else {
                throw new IllegalArgumentException("can not create new role: " + role.getRoleName() + ". it already exists");
            }
        } catch (Exception e) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    private WSRoleSearchCriteria wsRoleToWSRoleSearchCriteria(WSRole role) {
        WSRoleSearchCriteria criteria = new WSRoleSearchCriteria();
        criteria.setRoleName(role.getRoleName());
        criteria.setTenantId(role.getTenantId());
        return criteria;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        WSRole role = restUtils.populateServiceObject(req.getPathInfo(), req.getParameterMap(), WSRole.class);
        WSRoleSearchCriteria criteria = restUtils.populateServiceObject(req.getPathInfo(), req.getParameterMap(), WSRoleSearchCriteria.class);

        WSRole[] roles = null;
        try {
            roles = userAndRoleManagementService.findRoles(criteria);
            if (roles != null && roles.length != 0) {
                userAndRoleManagementService.deleteRole(role);
                if (log.isDebugEnabled()) {
                    log.debug(role + " role were deleted");
                }
            } else {
                throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, "role: " + role + " was not found");
            }
        } catch (AxisFault axisFault) {
            throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, axisFault.getLocalizedMessage());
        }
        restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServiceException {
        try {

            String searchCriteria = restUtils.extractResourceName(SERVICE_NAME, req.getPathInfo());

            WSRole role = restUtils.unmarshal(WSRole.class, req.getInputStream());

            role = restUtils.populateServiceObject(role);

            updateRole(role, restUtils.getWSRoleSearchCriteria(searchCriteria));

            restUtils.setStatusAndBody(HttpServletResponse.SC_OK, resp, "");
        } catch (IOException e) {
            throw new ServiceException(e.getLocalizedMessage());
        } catch (JAXBException e) {
            throw new ServiceException(e.getLocalizedMessage());
        }
    }


     private void updateRole(WSRole role, WSRoleSearchCriteria wsRoleSearchCriteria) {
        try {

            WSRole[] roles = userAndRoleManagementService.findRoles(wsRoleSearchCriteria);
            if (roles.length != 1) {
                throw new IllegalStateException("found " + roles.length + " roles to be update");
            } else {
                String newName = role.getRoleName();
                if (newName == null || "".equals(newName)) {
                    throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "New name is null.");
                }
                String nameWithoutNotSupportedSymbols = newName.replaceAll(configurationBean.getRoleNameNotSupportedSymbols(), "");

                if (nameWithoutNotSupportedSymbols.length() != newName.length()) {
                    throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, "Role name contains not supported symbols");
                }
                userAndRoleManagementService.deleteRole(roles[0]); // we know that there can be only one
                if (log.isDebugEnabled()) {
                    log.debug("role: " + roles[0].getRoleName() + " was deleted");
                }
                userAndRoleManagementService.putRole(role);
                if (log.isDebugEnabled()) {
                    log.debug("role: " + role.getRoleName() + " was added");
                }
            }
        }
        catch (AxisFault axisFault) {
            throw new ServiceException( axisFault.getLocalizedMessage());
        }
    }

    private String generateSummeryReport(WSRole[] roles) throws ServiceException {
        try {
            StringWriter sw = new StringWriter();

            sw.append("<roles>");
            for (int i = 0; i < roles.length; i++) {
                restUtils.getMarshaller(WSUser.class, WSRole.class).marshal(roles[i], sw);
                if (log.isDebugEnabled()) {
                    log.debug("finished marshaling role: " + roles[i].getTenantId());
                }
            }

            sw.append("</roles>");
            return sw.toString();
        } catch (JAXBException e) {
            throw new ServiceException(e.getLocalizedMessage());
        }
    }
}
