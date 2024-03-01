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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class JRTimezoneJdbcQueryExecuterTest {

    @Mock
    JasperReportsContext jasperReportsContext;
    @Mock
    JRDataset jrDataset;
    @Mock
    JRFillObjectFactory jrFillObjectFactory;
    @Mock
    JRParameter jrParameter;
    @Mock
    Connection connection;
    @Mock
    TimeZone timeZone;

    private JRFakeFillParameter reportParametersMapFake;
    private JRFakeFillParameter reportConnectionFake;
    private JRFakeFillParameter databaseTimezoneFake;

    private JRTimezoneJdbcQueryExecuter jrTimezoneJdbcQueryExecuter;
    private Map params;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        params = new HashMap();

        reportParametersMapFake = new JRFakeFillParameter(jrParameter, jrFillObjectFactory);
        reportParametersMapFake.setValue(new HashMap<>());
        params.put(JRParameter.REPORT_PARAMETERS_MAP, reportParametersMapFake);

        reportConnectionFake = new JRFakeFillParameter(jrParameter, jrFillObjectFactory);
        reportConnectionFake.setValue(connection);
        params.put(JRParameter.REPORT_CONNECTION, reportConnectionFake);

        databaseTimezoneFake = new JRFakeFillParameter(jrParameter, jrFillObjectFactory);
        databaseTimezoneFake.setValue(timeZone);
        params.put(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE, databaseTimezoneFake);

        jrTimezoneJdbcQueryExecuter = new JRTimezoneJdbcQueryExecuter(jasperReportsContext, jrDataset, params);
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

    private static class JRFakeFillParameter extends JRFillParameter{
        protected JRFakeFillParameter(JRParameter parameter, JRFillObjectFactory factory) {
            super(parameter, factory);
        }
    }
}
