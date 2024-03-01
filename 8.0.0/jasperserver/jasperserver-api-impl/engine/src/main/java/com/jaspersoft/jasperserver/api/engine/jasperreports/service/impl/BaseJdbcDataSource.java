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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ConnectionTestingDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseJdbcDataSource implements ReportDataSourceService, ConnectionTestingDataSourceService {
	private static final Log log = LogFactory.getLog(BaseJdbcDataSource.class);
	
	private Connection conn;
	private Optional<Tracer> tracer = Optional.empty();
	private Optional<Span> connSpan;
	private Optional<Tracer.SpanInScope> connScope;

	public void setReportParameterValues(Map parameterValues) {
		connSpan = this.tracer.map(t -> t.nextSpan().name("dbConnection"));
		try {
			connScope = this.tracer.flatMap(t -> connSpan.map(span -> t.withSpan(span.start())));;

			conn = createConnection();
			parameterValues.put(JRParameter.REPORT_CONNECTION, conn);

			connSpan.ifPresent(span -> {
				try {
					span.tag("url", conn.getMetaData().getURL());
				} catch (Exception ignore) {}
			});
			connSpan.ifPresent(span -> span.event("connectionCreated"));
		} catch (Exception e){
			// This will allow collecting the span to send it to a distributed tracing system e.g. Zipkin
			connSpan.ifPresent(span -> {
				span.error(e);
				connScope.ifPresent(Tracer.SpanInScope::close);
				span.end();
			});
			throw e;
		}
	}

	public void closeConnection() {
		if (conn != null)
		{
			try {
				conn.close();

				connSpan.ifPresent(span -> span.event("connectionClosed"));
				if (log.isDebugEnabled()) {
                    log.debug("Connection successfully closed");
                }
            } catch (SQLException e) {
				log.error("Error closing connection.", e);
				connSpan.ifPresent(span -> span.error(e));
				throw new JSExceptionWrapper(e);
			} finally {
				// Once done remember to end the span. This will allow collecting
				// the span to send it to a distributed tracing system e.g. Zipkin

				connScope.ifPresent(Tracer.SpanInScope::close);
				connSpan.ifPresent(Span::end);
			}

			conn = null;
		}
	}
	
	protected abstract Connection createConnection();

	public BaseJdbcDataSource withTracer(Tracer tracer) {
		this.tracer = Optional.ofNullable(tracer);
		return this;
	}
}
