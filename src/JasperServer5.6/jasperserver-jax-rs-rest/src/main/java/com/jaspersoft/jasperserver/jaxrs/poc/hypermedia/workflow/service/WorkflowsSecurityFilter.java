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
package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.service;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.dto.UserWorkflow;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.afterinvocation.AfterInvocationProvider;

import java.util.Iterator;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: WorkflowsSecurityFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class WorkflowsSecurityFilter implements AfterInvocationProvider {
    private String supportedAttribute;
    private List<String> allowedRoles;
    private List<String> workflowsToSecure;

    public String getSupportedAttribute() {
        return supportedAttribute;
    }

    public void setSupportedAttribute(String supportedAttribute) {
        this.supportedAttribute = supportedAttribute;
    }

    public List<String> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public List<String> getWorkflowsToSecure() {
        return workflowsToSecure;
    }

    public void setWorkflowsToSecure(List<String> workflowsToSecure) {
        this.workflowsToSecure = workflowsToSecure;
    }

    @Override
    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject) throws AccessDeniedException {
        // method supports(Class clazz) assures cast safety
        @SuppressWarnings("unchecked")
        List<UserWorkflow> workflows = (List<UserWorkflow>) returnedObject;
        final GrantedAuthority[] authorities = authentication.getAuthorities();
        boolean hasAuthority = false;
        for(GrantedAuthority authority : authorities){
            if(allowedRoles.contains(authority.getAuthority())){
                hasAuthority = true;
                break;
            }
        }
        if(!hasAuthority){
            final Iterator<UserWorkflow> iterator = workflows.iterator();
            for(;iterator.hasNext();){
                UserWorkflow currentWorkflow = iterator.next();
                if(workflowsToSecure.contains(currentWorkflow.getName()) || workflowsToSecure.contains(currentWorkflow.getParentName())){
                    iterator.remove();
                }
            }
        }
        return workflows;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return supportedAttribute.equals(attribute.getAttribute());
    }

    @Override
    public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz);
    }
}
