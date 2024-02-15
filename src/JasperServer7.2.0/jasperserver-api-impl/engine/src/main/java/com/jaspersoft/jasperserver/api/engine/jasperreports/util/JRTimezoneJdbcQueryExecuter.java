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

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;
import org.quartz.DateBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.api.security.validators.Validator.validateSQL;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class JRTimezoneJdbcQueryExecuter extends JRJdbcQueryExecuter {

	protected static class TimezoneAdjustInfo {
		protected final TimeZone timezone;
		protected final Set adjustedDates;
		protected final Object reportConnection;

		public TimezoneAdjustInfo(TimeZone timezone, Object reportConnection) {
			this.timezone = timezone;
            this.reportConnection = reportConnection;
            this.adjustedDates = new HashSet();
		}
    }

	protected static class IdentityObjectWrapper {
		protected final Object object;

		public IdentityObjectWrapper(Object object) {
			this.object = object;
		}

		public int hashCode() {
			return System.identityHashCode(object);
		}

		public boolean equals(Object o) {
			if (!(o instanceof IdentityObjectWrapper)) {
				return false;
			}

			return object == ((IdentityObjectWrapper) o).object;
		}
	}

	private static InheritableThreadLocal<TimezoneAdjustInfo> parentTimezone = new InheritableThreadLocal<>();

	private final TimezoneAdjustInfo timezoneAdjust;
	private final boolean timezoneSet;

	public JRTimezoneJdbcQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset, Map map) {
		super(jasperReportsContext, dataset, map);

		Object reportConnection = getReportConnectionFromParams();
        TimeZone timezoneParam = getDatabaseTimezoneFromParams();
        TimezoneAdjustInfo parentTimezoneAdjust = getTimezoneAdjustFromParent(reportConnection);

		if (shouldTakeTimezoneAdjustFromParent(timezoneParam, parentTimezoneAdjust)) {
			timezoneSet = false;
			timezoneAdjust = parentTimezoneAdjust;
		} else {
			timezoneSet = true;
			timezoneAdjust = new TimezoneAdjustInfo(timezoneParam, reportConnection);
			parentTimezone.set(timezoneAdjust);
		}
	}

    TimezoneAdjustInfo getTimezoneAdjustInfo() {
	    return timezoneAdjust;
    }

    private Object getReportConnectionFromParams() {
        JRValueParameter reportConnectionParam = getValueParameter(JRParameter.REPORT_CONNECTION);
        return reportConnectionParam.getValue();
    }

    private TimeZone getDatabaseTimezoneFromParams() {
        JRValueParameter databaseTimezoneParam = getValueParameter(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE);
        return (TimeZone) databaseTimezoneParam.getValue();
    }

    private TimezoneAdjustInfo getTimezoneAdjustFromParent(Object reportConnection) {
        TimezoneAdjustInfo value = parentTimezone.get();
        if (value != null && value.reportConnection.equals(reportConnection)) {
            return value;
        }
        return null;
    }

    private boolean shouldTakeTimezoneAdjustFromParent(TimeZone timeZone, TimezoneAdjustInfo parentTimezoneAdjust) {
        return parentTimezoneAdjust != null &&
                (timeZone == null ||
                        (parentTimezoneAdjust.timezone != null && parentTimezoneAdjust.timezone.equals(timeZone)));
    }

	protected JRValueParameter getValueParameter(String parameterName) {
		JRValueParameter param = super.getValueParameter(parameterName);

		Object value = param.getValue();
		if (value instanceof Date
				&& timezoneAdjust != null
				&& timezoneAdjust.timezone != null
				&& timezoneAdjust.adjustedDates.add(new IdentityObjectWrapper(value))) {
			Date initialDate = (Date) value;
			Date date = DateBuilder.translateTime(initialDate,
					timezoneAdjust.timezone, TimeZone.getDefault());
            //Date date = TriggerUtils.translateTime(initialDate,
			//		timezoneAdjust.timezone, TimeZone.getTimeZone("GMT"));
			initialDate.setTime(date.getTime());
		}
		return param;
	}

	public JRDataSource createDatasource() throws JRException {
        /* Checking that query is valid, throw a runtime exception */
        validateSQL(getQueryString(), connection);
		JRDataSource dataSource = super.createDatasource();
		return new JRTimezoneResultSetDataSource(dataSource,
				timezoneAdjust.timezone);
	}

	public synchronized void close() {
		if (timezoneSet) {
			parentTimezone.remove();
		}

		super.close();
	}

}
