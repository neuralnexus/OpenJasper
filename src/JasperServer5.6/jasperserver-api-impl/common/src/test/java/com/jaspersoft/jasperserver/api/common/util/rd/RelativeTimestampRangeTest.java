package com.jaspersoft.jasperserver.api.common.util.rd;

import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.RelativeTimestampRange;
import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;

public class RelativeTimestampRangeTest extends BaseRelativeDateTest {

    @Test
    public void shouldReturnDateInPast() throws Exception {
        DateRange rd = new RelativeTimestampRange("DAY-3");
        assertEquals(Timestamp.class, rd.getStart().getClass());
        assertEquals(Timestamp.class, rd.getEnd().getClass());
    }
}
