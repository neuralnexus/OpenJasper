package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static Date translateTime(Date date, TimeZone fromTz, TimeZone toTZ) {
        Date result = new Date();
        int offset = toTZ.getOffset(date.getTime()) - fromTz.getOffset(date.getTime());
        result.setTime(date.getTime() - (long) offset);
        return result;
    }
}
