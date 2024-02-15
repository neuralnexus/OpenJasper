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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.vote.AccessDecisionVoter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ExecutionOwnerVoter.java 27042 2012-12-19 10:18:08Z ykovalchyk $
 */
@Component
public class ExecutionOwnerVoter implements AccessDecisionVoter {
    public static final String ATTRIBUTE_EXECUTION_OWNER = "EXECUTION_OWNER";

    @Resource
    private Map<String, EngineServiceImpl.ReportExecutionStatus> engineExecutions;
    @Resource(name = "concreteSecurityContextProvider")
    private SecurityContextProvider securityContextProvider;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return ATTRIBUTE_EXECUTION_OWNER.equals(attribute.getAttribute());
    }

    @Override
    public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
        int result = ACCESS_ABSTAIN;
        if (ATTRIBUTE_EXECUTION_OWNER.equals(((ConfigAttribute) config.getConfigAttributes().iterator().next()).getAttribute())) {
            MethodInvocation invocation = (MethodInvocation) object;
            final String executionId = (String) invocation.getArguments()[0];
            EngineServiceImpl.ReportExecutionStatus status = engineExecutions.get(executionId);
            if (status != null) {
                User currentUser = securityContextProvider.getContextUser();
                result = status.getOwner().equals(currentUser) ? ACCESS_GRANTED : ACCESS_DENIED;
            }
        }
        return result;
    }
}
