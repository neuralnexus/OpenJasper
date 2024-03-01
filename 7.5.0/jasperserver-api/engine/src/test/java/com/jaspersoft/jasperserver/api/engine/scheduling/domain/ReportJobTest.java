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

package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import net.sf.jasperreports.engine.JRParameter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author schubar
 * @author Anton Fomin
 * @version $Id$
 */
public class ReportJobTest {


    ReportJob reportJob = new ReportJob();

    ReportJobSource reportJobSource = new ReportJobSource();

    Map<String, Object> paramValues;

    @Before
    public void fixtures() {
        reportJob.setSource(reportJobSource);

        this.paramValues = new HashMap<>();

        this.paramValues.put("test1", "value1");
        this.paramValues.put("test2", 2);
    }

    @Test
    public void shouldSetReportParameters() {
        this.reportJob.setReportParameters(this.paramValues);
        assertEquals(2, this.reportJob.getSource().getParameters().size());
    }

    @Test
    public void shouldSetReportTimeZone() {
        this.reportJob.setOutputTimeZone("America/Chicago");

        assertEquals(TimeZone.getTimeZone("America/Chicago"),
                this.reportJob.getSource().getParameters().get(JRParameter.REPORT_TIME_ZONE));
    }

    @Test
    public void shouldReturnNullIfTimeZoneNotSet() {
        assertNull(this.reportJob.getOutputTimeZone());
    }

    @Test
    public void shouldReturnTimeZoneIDIfSet() {
        this.reportJob.setOutputTimeZone("America/Chicago");
        assertEquals("America/Chicago", this.reportJob.getOutputTimeZone());
    }
}
