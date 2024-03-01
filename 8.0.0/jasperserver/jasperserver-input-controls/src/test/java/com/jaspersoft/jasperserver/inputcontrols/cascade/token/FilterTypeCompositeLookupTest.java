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
package com.jaspersoft.jasperserver.inputcontrols.cascade.token;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterTypeCompositeLookupTest {
    @InjectMocks
    private ParameterTypeCompositeLookup lookup;

    private ResourceReference dataSource = mock(ResourceReference.class);

    private ExecutionContext context = mock(ExecutionContext.class);

    private Set<String> parameterNames = new HashSet<String>() {{
        add("ORACLE_REF_CURSOR");
        add("CUSTOM_PARAM");
    }};

    @Before
    public void setUp() {
        final ResourceLookup resourceLookup = mock(ResourceLookup.class);

        doReturn(false).when(dataSource).isLocal();
        doReturn(resourceLookup).when(dataSource).getReferenceLookup();
        doReturn(ReportUnit.class.getName()).when(resourceLookup).getResourceType();
    }

    @Test
    public void getType_dataSourceWithReference_referenceType() {
        assertEquals(ReportUnit.class.getName(), lookup.getResourceType(dataSource));
    }

    @Test
    public void getType_dataSourceWithoutReference_null() {
        doReturn(false).when(dataSource).isLocal();
        doReturn(null).when(dataSource).getReferenceLookup();

        assertNull(lookup.getResourceType(dataSource));
    }

    @Test
    public void getType_dataSourceWithLocalResource_resourceType() {
        final Resource resource = mock(ReportUnit.class);

        doReturn(true).when(dataSource).isLocal();
        doReturn(resource).when(dataSource).getLocalResource();

        assertEquals(resource.getClass().getName(), lookup.getResourceType(dataSource));
    }

    @Test
    public void getType_dataSourceWithoutLocalResource_null() {
        doReturn(true).when(dataSource).isLocal();

        assertNull(lookup.getResourceType(dataSource));
    }

    @Test
    public void getParameterTypes_emptyType_emptyParameters() throws Exception {
        doReturn(true).when(dataSource).isLocal();
        doReturn(null).when(dataSource).getLocalResource();

        Map<String, Class<?>> parameterTypes = lookup.getParameterTypes(context, dataSource, parameterNames);
        assertTrue(parameterTypes.isEmpty());
    }

    @Test
    public void getParameterTypes_missingLookup_emptyParameters() throws Exception {
        Map<String, Class<?>> result = lookup.getParameterTypes(context, dataSource, parameterNames);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getParameterTypes_foundLookup_parameters() throws Exception {
        final ParameterTypeLookup lookup = mock(ParameterTypeLookup.class);
        final Map<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>() {{
            put("ORACLE_REF_CURSOR", java.lang.String.class);
        }};

        doReturn(parameterTypes).when(lookup).getParameterTypes(eq(context), eq(dataSource), eq(parameterNames));
        this.lookup.registerLookup(ReportUnit.class.getName(), lookup);

        Map<String, Class<?>> result = this.lookup.getParameterTypes(context, dataSource, parameterNames);
        assertEquals(parameterTypes, result);
    }
}
