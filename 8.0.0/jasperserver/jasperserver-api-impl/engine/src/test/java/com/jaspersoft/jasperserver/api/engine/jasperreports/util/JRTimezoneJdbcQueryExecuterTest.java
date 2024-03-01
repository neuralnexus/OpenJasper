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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import static com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory.SET_LOCAL_TIME_ZONE_IN_SQL;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.sql.ProfileAttributeTimeZoneQueryProviderImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.sql.TimeZoneQueryProviderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.AttributesSearchResultImpl;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillParameter;

public class JRTimezoneJdbcQueryExecuterTest {
    @Mock
    private JasperReportsContext jasperReportsContext;
    @Mock
    private JRDataset jrDataset;
    @Mock
    private JRFillObjectFactory jrFillObjectFactory;
    @Mock
    private JRParameter jrParameter;
    @Mock
    private Connection connection;

    private TimeZone timeZone;
    
    @Mock
    private ProfileAttributeService profileAttributeService;
    @Mock
    private ResourceFactory resourceFactory;

    private JRFakeFillParameter reportParametersMapFake;
    private JRFakeFillParameter reportConnectionFake;
    private JRFakeFillParameter databaseTimezoneFake;

    private JRTimezoneJdbcQueryExecuter jrTimezoneJdbcQueryExecuter;
    private Map params;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        params = new HashMap();
        timeZone = TimeZone.getTimeZone("America/New_York");

        reportParametersMapFake = new JRFakeFillParameter(jrParameter, jrFillObjectFactory);
        reportParametersMapFake.setValue(new HashMap<>());
        params.put(JRParameter.REPORT_PARAMETERS_MAP, reportParametersMapFake);

        reportConnectionFake = new JRFakeFillParameter(jrParameter, jrFillObjectFactory);
        reportConnectionFake.setValue(connection);
        params.put(JRParameter.REPORT_CONNECTION, reportConnectionFake);

        databaseTimezoneFake = new JRFakeFillParameter(jrParameter, jrFillObjectFactory);
        databaseTimezoneFake.setValue(timeZone);
        params.put(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE, databaseTimezoneFake);

        jrTimezoneJdbcQueryExecuter = spy(new JRTimezoneJdbcQueryExecuter(jasperReportsContext, jrDataset, params));
        doReturn(resourceFactory).when(jrTimezoneJdbcQueryExecuter).getObjectMappingFactory();
        doNothing().when(jrTimezoneJdbcQueryExecuter).validateSQL();
    }
    
    @Test
    public void timezoneAdjustValueIsTakenFromParams() {
        JRTimezoneJdbcQueryExecuter.TimezoneAdjustInfo timezoneAdjust = jrTimezoneJdbcQueryExecuter.getTimezoneAdjustInfo();
        assertEquals(timeZone, timezoneAdjust.timezone);
    }

    @Test
    public void timezoneAdjustValueIsTakenFromParentIfNotPresentInParams() {
        databaseTimezoneFake.setValue(null);
        JRTimezoneJdbcQueryExecuter jrTimezoneJdbcQueryExecuterNext = new JRTimezoneJdbcQueryExecuter(jasperReportsContext, jrDataset, params);

        JRTimezoneJdbcQueryExecuter.TimezoneAdjustInfo timezoneAdjust = jrTimezoneJdbcQueryExecuterNext.getTimezoneAdjustInfo();
        assertEquals(timeZone, timezoneAdjust.timezone);
    }

    @Test
    public void timezoneAdjustValueIsTakenFromParamInsteadOfParent() {
        TimeZone timeZoneAlternative = TimeZone.getTimeZone("UTC");
        databaseTimezoneFake.setValue(timeZoneAlternative);
        JRTimezoneJdbcQueryExecuter jrTimezoneJdbcQueryExecuterNext = new JRTimezoneJdbcQueryExecuter(jasperReportsContext, jrDataset, params);

        JRTimezoneJdbcQueryExecuter.TimezoneAdjustInfo timezoneAdjust = jrTimezoneJdbcQueryExecuterNext.getTimezoneAdjustInfo();
        assertEquals(timeZoneAlternative, timezoneAdjust.timezone);
    }

    private PreparedStatement tzQueryProviderSetup(TimeZoneQueryProviderImpl tzqp) throws Exception {
        final String productName = "postgresql";
        Map<String, String> p2q = new HashMap<>();
        p2q.put(productName, "SET LOCAL TIMEZONE='{TimeZone}'");
        tzqp.setProductNameToQuery(p2q);
        tzqp.setTimeZonePlaceholder("{TimeZone}");
        
        final String alterQuery = "SET LOCAL TIMEZONE='America/New_York'";
        final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
        final PreparedStatement alterTimeZoneStatement = mock(PreparedStatement.class);
        doReturn(tzqp).when(jrTimezoneJdbcQueryExecuter).getTimeZoneQueryProvider();
        doReturn(databaseMetaData).when(connection).getMetaData();
        doReturn(productName).when(databaseMetaData).getDatabaseProductName();
        doReturn(alterTimeZoneStatement).when(connection).prepareStatement(eq(alterQuery));
        return alterTimeZoneStatement;
    }

    @Test
    public void createDatasource_default_shouldNotApplyTimeZone() throws Exception {
        TimeZoneQueryProviderImpl timeZoneQueryProvider = new TimeZoneQueryProviderImpl();
        timeZoneQueryProvider.setDefaultSetTimeZoneInSQL(false);
        PreparedStatement alterTimeZoneStatement = tzQueryProviderSetup(timeZoneQueryProvider);

        jrTimezoneJdbcQueryExecuter.createDatasource();

        verify(alterTimeZoneStatement, never()).execute();
    }

    @Test
    public void createDatasource_springconfig_shouldApplyTimeZone() throws Exception {
        TimeZoneQueryProviderImpl timeZoneQueryProvider = new TimeZoneQueryProviderImpl();
        timeZoneQueryProvider.setDefaultSetTimeZoneInSQL(true);
        PreparedStatement alterTimeZoneStatement = tzQueryProviderSetup(timeZoneQueryProvider);

        jrTimezoneJdbcQueryExecuter.createDatasource();

        verify(alterTimeZoneStatement).execute();
    }

    @Test
    public void createDatasource_profileattribute_shouldApplyTimeZone() throws Exception {
        final ProfileAttribute profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName(SET_LOCAL_TIME_ZONE_IN_SQL);
        profileAttribute.setAttrValue(Boolean.toString(true));
        final AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<>();
        searchResult.setList(Collections.singletonList(profileAttribute));
        ProfileAttributeTimeZoneQueryProviderImpl timeZoneQueryProvider = new ProfileAttributeTimeZoneQueryProviderImpl();
        PreparedStatement alterTimeZoneStatement = tzQueryProviderSetup(timeZoneQueryProvider);

        doReturn(searchResult).when(profileAttributeService).getProfileAttributesForPrincipal(any(), any(), any());
        timeZoneQueryProvider.setProfileAttributeService(profileAttributeService);

        jrTimezoneJdbcQueryExecuter.createDatasource();

        verify(alterTimeZoneStatement).execute();
    }

    private static class JRFakeFillParameter extends JRFillParameter {
        protected JRFakeFillParameter(JRParameter parameter, JRFillObjectFactory factory) {
            super(parameter, factory);
        }
    }
}
