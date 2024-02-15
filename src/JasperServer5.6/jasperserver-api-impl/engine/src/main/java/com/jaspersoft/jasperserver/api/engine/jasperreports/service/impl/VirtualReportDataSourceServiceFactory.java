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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.VirtualDataSourceHandler;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceAcessDeniedException;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.util.TimeZone;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: VirtualReportDataSourceServiceFactory.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class VirtualReportDataSourceServiceFactory extends JdbcReportDataSourceServiceFactory {

	private static final Log log = LogFactory.getLog(VirtualReportDataSourceServiceFactory.class);
    VirtualDataSourceHandler virtualDataSourceHandler;
    AwsReportDataSourceServiceFactory awsDataSourceServiceFactory;
	public VirtualReportDataSourceServiceFactory() {
	}

    public VirtualDataSourceHandler getVirtualDataSourceHandler() {
        return virtualDataSourceHandler;
    }

    public void setAwsDataSourceServiceFactory(AwsReportDataSourceServiceFactory awsDataSourceServiceFactory) {
        this.awsDataSourceServiceFactory = awsDataSourceServiceFactory;
    }

    public void setVirtualDataSourceHandler(VirtualDataSourceHandler virtualDataSourceHandler) {
        this.virtualDataSourceHandler = virtualDataSourceHandler;
    }

    public ReportDataSourceService createService(ReportDataSource dataSource) {
        if (dataSource instanceof VirtualReportDataSource) {
            // master data source - virtual data source
            DataSource ds;
            VirtualReportDataSource virtualDataSource = (VirtualReportDataSource) dataSource;
            TimeZone timeZone = getTimeZoneByDataSourceTimeZone(virtualDataSource.getTimezone());
            try {
                // generate JDBC data source from virtual data source
                ds = virtualDataSourceHandler.getSqlDataSource(ExecutionContextImpl.getRuntimeExecutionContext(), virtualDataSource);
                return new JdbcDataSourceService(ds, timeZone);
            } catch (JSResourceAcessDeniedException accessDeniedEx) {
                if (log.isDebugEnabled()) log.debug(accessDeniedEx, accessDeniedEx);
                throw accessDeniedEx;
            } catch (Exception e) {
                if (log.isDebugEnabled())
                    log.debug(e, e);
                throw new JSExceptionWrapper(e);
            }
        } else {
            // sub data source - JDBC data source
            // DO NOT use the JDBC data source pooling in JdbcReportDataSourceServiceFactory for JDBC sub data source
            if (dataSource instanceof AwsReportDataSource) {
                return awsDataSourceServiceFactory.createService(dataSource);
            } else {
                return super.createService(dataSource);
            }
        }
    }

    protected DataSource getPoolDataSource(String driverClass, String url, String username, String password) {
        PooledDataSource dataSource;
        // DO NOT use the JDBC data source pooling in JdbcReportDataSourceServiceFactory for JDBC sub data source
        dataSource = getPooledJdbcDataSourceFactory().createPooledDataSource(
                driverClass, url, username, password, getDefaultReadOnly(), getDefaultAutoCommit());
        return dataSource.getDataSource();
    }

}
