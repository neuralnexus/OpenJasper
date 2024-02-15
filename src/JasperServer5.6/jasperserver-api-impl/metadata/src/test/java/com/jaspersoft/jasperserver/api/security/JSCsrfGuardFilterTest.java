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

package com.jaspersoft.jasperserver.api.security;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.Unitils;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Anton Fomin
 */
public class JSCsrfGuardFilterTest extends UnitilsJUnit4 {

    @Test
    public void testFiltering() throws Exception {
        JSCsrfGuardFilter filter = new JSCsrfGuardFilter();

        Mock<HttpServletRequest> requestMock = MockUnitils.createMock(HttpServletRequest.class);
        Map<String, String[]> paramMap = new LinkedHashMap<String, String[]>();
        requestMock.returns(paramMap).getParameterMap();

        Method filterRequest = JSCsrfGuardFilter.class.getDeclaredMethod("filterRequest", HttpServletRequest.class);
        filterRequest.setAccessible(true);

        /* Request with empty parameter map */
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Test Create */

        /* One parameter, when two are required */
        paramMap.put("_eventId", new String[]{"create"});
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Two required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"create"});
        paramMap.put("entity", new String[]{"{\"userName\": \"test\", \"fullName\": \"test\", \"password\": \"111\", \"email\": \"test@test.com\", \"enabled\": true, \"roles\": [{\"roleName\": \"ROLE_USER\", \"FLOW_ID\": \"roleListFlow\"}], \"attributes\": [], \"FLOW_ID\": \"userListFlow\"}"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Wrong parameter value */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"view"});
        paramMap.put("entity", new String[]{"{\"userName\": \"test\", \"fullName\": \"test\", \"password\": \"111\", \"email\": \"test@test.com\", \"enabled\": true, \"roles\": [{\"roleName\": \"ROLE_USER\", \"FLOW_ID\": \"roleListFlow\"}], \"attributes\": [], \"FLOW_ID\": \"userListFlow\"}"});
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Whatever parameter value */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"create"});
        paramMap.put("entity", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* More than two required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"create"});
        paramMap.put("entity", new String[]{"{\"userName\": \"test\", \"fullName\": \"test\", \"password\": \"111\", \"email\": \"test@test.com\", \"enabled\": true, \"roles\": [{\"roleName\": \"ROLE_USER\", \"FLOW_ID\": \"roleListFlow\"}], \"attributes\": [], \"FLOW_ID\": \"userListFlow\"}"});
        paramMap.put("whatever", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Test Update */

        /* Three required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"update"});
        paramMap.put("entityName", new String[]{"whatever"});
        paramMap.put("entity", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Two of three required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"update"});
        paramMap.put("entityName", new String[]{"whatever"});
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Test Delete */

        /* Two required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"deleteAll"});
        paramMap.put("entities", new String[]{"[\"test\"]"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Parameter "entities" is missing */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"deleteAll"});
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Delete Role */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"delete"});
        paramMap.put("entity", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Delete Role missing entity parameter */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"delete"});
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Delete Role, more parameters then needed (two)*/
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"delete"});
        paramMap.put("entity", new String[]{"whatever"});
        paramMap.put("entityName", new String[]{"whatever"});
        paramMap.put("whatever", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Delete Role, wrong parameter value */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"delet"});
        paramMap.put("entity", new String[]{"whatever"});
        assertFalse((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Test Permission Update */

        /* Three required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"permissionsUpdate"});
        paramMap.put("uri", new String[]{"whatever"});
        paramMap.put("entitiesWithPermission", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

        /* Three required parameters */
        paramMap.clear();
        paramMap.put("_eventId", new String[]{"permissionsUpdate"});
        paramMap.put("uri", new String[]{"whatever"});
        paramMap.put("entitiesWithPermission", new String[]{"whatever"});
        assertTrue((Boolean) filterRequest.invoke(filter, requestMock.getMock()));

    }

}
