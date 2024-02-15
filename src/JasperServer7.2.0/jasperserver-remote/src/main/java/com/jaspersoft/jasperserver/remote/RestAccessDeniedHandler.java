/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.exception.SpringSecurityAccessDeniedErrorDescriptorBuilder;
import com.jaspersoft.jasperserver.remote.helpers.JacksonMapperProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Resource
    private SpringSecurityAccessDeniedErrorDescriptorBuilder springSecurityAccessDeniedErrorDescriptorBuilder;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final ErrorDescriptor errorDescriptor = springSecurityAccessDeniedErrorDescriptorBuilder
                .build(accessDeniedException);
        final ObjectMapper objectMapper = JacksonMapperProvider.getObjectMapper();
        response.setStatus(403);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), errorDescriptor);
    }
}
