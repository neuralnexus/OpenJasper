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

package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.ReferencedResourceAccessDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class ResourceLookupContextStrategyTest {

    @InjectMocks
    private ResourceLookupContextStrategy strategy = new ResourceLookupContextStrategy();
    private ResourceLookupContextStrategy strategySpy;

    @Mock
    private RepositoryService repository;

    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        strategySpy = spy(strategy);
    }

    @BeforeMethod
    public void refresh() {
        reset(repository, strategySpy);
    }

    @Test(expectedExceptions = ReferencedResourceAccessDeniedException.class)
    public void getFullClientResource_AccessDenied() throws ReferencedResourceAccessDeniedException {
        ClientResourceLookup clientResourceLookup = new ClientResourceLookup();
        clientResourceLookup.setUri("/Domains/Simple_Domain");
        when(repository.getResource(any(ExecutionContext.class), any(String.class))).thenThrow(new AccessDeniedException("NO"));
            strategy.getFullClientResource(clientResourceLookup);

    }

    @Test
    public void getToClientConversionOptionsForFullResource() throws ReferencedResourceAccessDeniedException {
        ToClientConversionOptions toClientConversionOptions = strategy.getToClientConversionOptionsForFullResource();
        assertEquals(toClientConversionOptions.isAllowSecureDataConversation(), true);
    }


}
