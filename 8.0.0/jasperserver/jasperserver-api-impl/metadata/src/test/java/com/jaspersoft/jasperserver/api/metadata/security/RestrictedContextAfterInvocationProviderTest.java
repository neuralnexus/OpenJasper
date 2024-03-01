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
package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.VirtualReportDataSourceImpl;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.getRestrictedRuntimeExecutionContext;
import static com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl.getRuntimeExecutionContext;
import static com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission.ADMINISTRATION;
import static com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission.EXECUTE;
import static com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission.READ;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


/**
 * @author Volodya Sabadosh
 */
@RunWith(MockitoJUnitRunner.class)
public class RestrictedContextAfterInvocationProviderTest {
    private String SUPPORTED_ATTR = "supportedAttribute";
    private String NOT_SUPPORTED_ATTR = "notSupportedAttribute";
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = mock(ObjectIdentityRetrievalStrategy.class);
    private SidRetrievalStrategy sidRetrievalStrategy = mock(SidRetrievalStrategy.class);
    private AclService aclService = mock(AclService.class);
    private Set<Class<? extends Resource>> inAccessibleResourceTypes = Stream.of(ReportDataSource.class).collect(toSet());
    private Set<Class<? extends Resource>> skipResourceTypes =  Stream.of(VirtualReportDataSource.class).collect(toSet());
    private List<Permission> requiredPermission = singletonList(EXECUTE);
    private List<Permission> ignoreRestrictedContextForPermission = asList(ADMINISTRATION, READ);
    private Authentication auth = mock(Authentication.class);

    private RestrictedContextAfterInvocationProvider provider;

    @Before
    public void before() {
        provider = new RestrictedContextAfterInvocationProvider(aclService, SUPPORTED_ATTR, requiredPermission,
                        ignoreRestrictedContextForPermission, inAccessibleResourceTypes, skipResourceTypes);
        provider.setObjectIdentityRetrievalStrategy(objectIdentityRetrievalStrategy);
        provider.setSidRetrievalStrategy(sidRetrievalStrategy);
    }

    @Test(expected = AccessDeniedException.class)
    public void decide_contextIsRestrictedAndResourceIsRestrictedAndOnlyMinimalPermissionGranted_throwException() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Object returnObject = mock(JdbcReportDataSource.class);
        mockAcl(true, false, returnObject, false);

        provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);
    }

    @Test
    public void decide_contextIsRestrictedAndResourceIsSkippedAndOnlyMinimalPermissionGranted_returnSame() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Object returnObject = mock(VirtualReportDataSourceImpl.class);
        mockAcl(true, false, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);
        assertSame(returnObject, actualObj);
    }

    @Test
    public void decide_contextIsRestrictedAndResourceIsRestrictedButOtherPermissionGranted_returnSame() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Object returnObject = mock(ReportDataSource.class);
        mockAcl(false, false, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);

        assertSame(returnObject, actualObj);
    }

    @Test
    public void decide_contextIsNotRestrictedAndResourceIsRestrictedAndOnlyMinimalPermissionGranted_returnSame() {
        Object securedObject = mockSecuredMethodInvocation(false);
        Object returnObject = mock(ReportDataSource.class);
        mockAcl(true, false, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);
        assertSame(returnObject, actualObj);
    }

    @Test
    public void decide_restrictedContextAppliedButResourceIsNotRestrictedAndNotContainRestrictedLocalResource_returnSame() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Resource returnObject = mock(Resource.class);
        mockAcl(true, false, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);
        assertSame(returnObject, actualObj);
    }

    @Test
    public void decide_restrictedContextAppliedButResourceIsNotRestrictedAndContainsRestrictedResource_returnResourceWithHiddenLocalRestrictedRes() {
        Object securedObject = mockSecuredMethodInvocation(true);
        String restrictedLocalResourceUri = "/restrictedResourceUri";

        ReportDataSource restrictedLocalResource = mock(ReportDataSource.class);
        when(restrictedLocalResource.getURIString()).thenReturn(restrictedLocalResourceUri);

        QueryImpl returnObject = new QueryImpl();
        returnObject.setDataSource(restrictedLocalResource);

        mockAcl(true, false, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);

        assertSame(returnObject, actualObj);
        assertFalse(returnObject.getDataSource().isLocal());
        assertEquals(restrictedLocalResourceUri, returnObject.getDataSource().getTargetURI());
    }

    @Test
    public void decide_contextIsRestrictedAndResourceIsRestrictedAndMinimalWithOtherPermissionGranted_returnSame() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Object returnObject = mock(ReportDataSource.class);
        mockAcl(true, true, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);
        assertSame(returnObject, actualObj);
    }

    @Test
    public void decide_attributeIsNotSupported_success() {
        Object returnObject = mock(ReportDataSource.class);
        Object securedObject = mockSecuredMethodInvocation(true);

        assertEquals(returnObject, provider.decide(auth, securedObject,
                mockConfigAttributes(NOT_SUPPORTED_ATTR), returnObject));
    }

    @Test
    public void decide_contextIsRestrictedAndResourceIsRestrictedAndOnlyMinimalPermissionGrantedButAclThrowException_returnSame() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Object returnObject = mock(ReportDataSource.class);
        mockAcl(true, false, returnObject, true);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), returnObject);
        assertSame(returnObject, actualObj);
    }

    @Test
    public void decide_resourceIsNull_returnNull() {
        Object securedObject = mockSecuredMethodInvocation(true);
        Object returnObject = null;
        mockAcl(false, false, returnObject, false);

        Object actualObj = provider.decide(auth, securedObject, mockConfigAttributes(SUPPORTED_ATTR), null);

        assertSame(null, actualObj);
    }

    private Object mockSecuredMethodInvocation(boolean isRestrictedContext) {
        MethodInvocation securedObject = mock(MethodInvocation.class);
        ExecutionContext context;
        if (isRestrictedContext) {
            context = getRestrictedRuntimeExecutionContext();
        } else {
            context = getRuntimeExecutionContext();
        }
        when(securedObject.getArguments()).thenReturn(new Object[]{context, "otherAttr"});
        return securedObject;
    }

    private List<ConfigAttribute> mockConfigAttributes(String... attributeNames) {
        List<ConfigAttribute> result = new ArrayList<>();
        for (String attributeName : attributeNames) {
            ConfigAttribute configAttribute = mock(ConfigAttribute.class);
            when(configAttribute.getAttribute()).thenReturn(attributeName);
            result.add(configAttribute);
        }
        return result;
    }

    private void mockAcl(boolean isRequiredPermissionGranted, boolean isIgnoreRestrictedContextForPermissionGranted,
                        Object returnObject, boolean aclServiceThrowsException) {
        reset(objectIdentityRetrievalStrategy, sidRetrievalStrategy, aclService);
        Acl acl = mock(Acl.class);

        ObjectIdentity objectIdentity = mock(ObjectIdentity.class);
        List<Sid> sids = mock(List.class);

        when(objectIdentityRetrievalStrategy.getObjectIdentity(returnObject)).thenReturn(objectIdentity);
        when(sidRetrievalStrategy.getSids(auth)).thenReturn(sids);
        if (!aclServiceThrowsException) {
            when(aclService.readAclById(objectIdentity, sids)).thenReturn(acl);
        } else {
            when(aclService.readAclById(objectIdentity, sids)).thenThrow(NotFoundException.class);
        }

        when(acl.isGranted(requiredPermission, sids, false)).thenReturn(isRequiredPermissionGranted);
        when(acl.isGranted(ignoreRestrictedContextForPermission, sids, false))
                .thenReturn(isIgnoreRestrictedContextForPermissionGranted);
    }

}
