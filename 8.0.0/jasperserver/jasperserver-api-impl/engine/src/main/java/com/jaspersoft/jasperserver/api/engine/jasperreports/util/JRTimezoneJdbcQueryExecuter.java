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

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.sql.TimeZoneQueryProvider;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.security.validators.Validator;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory.SET_LOCAL_TIME_ZONE_IN_SQL;
import static net.sf.jasperreports.engine.JRParameter.REPORT_TIME_ZONE;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class JRTimezoneJdbcQueryExecuter extends JRJdbcQueryExecuter {
	private Logger log = Logger.getLogger(JRTimezoneJdbcQueryExecuter.class);

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
			Date date = DateUtil.translateTime(initialDate,
					timezoneAdjust.timezone, TimeZone.getDefault());
            //Date date = TriggerUtils.translateTime(initialDate,
			//		timezoneAdjust.timezone, TimeZone.getTimeZone("GMT"));
			initialDate.setTime(date.getTime());
		}
		return param;
	}

	public JRDataSource createDatasource() throws JRException {
		/* Checking that query is valid, throws a runtime exception if not */
		validateSQL();
		JRDataSource dataSource;
		// Figure out whether we will add SQL to set the TZ
		// it can be set with a param, or by asking the TimeZoneQueryProvider
		Optional<PreparedStatement> tzStatement = Optional.empty();
        boolean setTZParam = getBooleanParameterOrProperty(SET_LOCAL_TIME_ZONE_IN_SQL, false);
        if (! setTZParam) {
        	setTZParam = getTimeZoneQueryProvider().shouldSetLocalTimeZoneInSQL();
        }
		try {
	        if (setTZParam) {
	        	tzStatement = createSetTimeZoneStatement();
			} 
	        // if there is a statement to run, run it with the datasource query in a txn
	        // otherwise, just call super
			if (!tzStatement.isPresent()) {
				dataSource = super.createDatasource();
			} else {
				connection.setAutoCommit(false);
				tzStatement.get().execute();
				dataSource = super.createDatasource();
				connection.commit();
			}
		} catch (SQLException e) {
			throw new JRException(e);
		}
		return new JRTimezoneResultSetDataSource(dataSource, timezoneAdjust.timezone);
	}

	public synchronized void close() {
		if (timezoneSet) {
			parentTimezone.remove();
		}

		super.close();
	}

	private Optional<PreparedStatement> createSetTimeZoneStatement() throws SQLException {
		TimeZone reportTimeZone = parameterHasValue(REPORT_TIME_ZONE)
				? (TimeZone) getParameterValue(REPORT_TIME_ZONE)
				: getDatabaseTimezoneFromParams();
		TimeZoneQueryProvider tzQueryProvider = getTimeZoneQueryProvider();
		String productName = connection.getMetaData().getDatabaseProductName().toLowerCase();
		String alterQuery = tzQueryProvider.getAlterQuery(productName, reportTimeZone);
		if (alterQuery == null) {
			log.warn("Didn't find alter time zone SQL query for \"" + productName + "\" database. " +
					"TimeZone will not be provided to the database. You can configure \"TimeZoneQueryProviderImpl\" bean to support it");
			return Optional.empty();
		} else {
			return Optional.of(connection.prepareStatement(alterQuery));
		}
	}

	void validateSQL() {
		Validator.validateSQL(getQueryString(), connection);
	}

    TimeZoneQueryProvider getTimeZoneQueryProvider() {
        return StaticApplicationContext.getApplicationContext().getBean(TimeZoneQueryProvider.class);
    }

    ResourceFactory getObjectMappingFactory() {
        return StaticApplicationContext.getApplicationContext().getBean("mappingResourceFactory", ResourceFactory.class);
    }

}
