package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import net.sf.jasperreports.engine.JRParameter;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author schubar
 * @author Anton Fomin
 * @version $Id: ReportJobTest.java 24781 2012-09-06 16:28:04Z afomin $
 */
public class ReportJobTest extends UnitilsJUnit4 {


    @TestedObject
    ReportJob reportJob;

    @InjectInto(property = "source")
    ReportJobSource reportJobSource;

    Map<String, Object> paramValues;

    @Before
    public void setUp() {
        reportJob = new ReportJob();
        reportJobSource = new ReportJobSource();
    }

    @Before
    public void fixtures() {
        this.paramValues = new HashMap<String, Object>();

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
