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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class MongoDbJDBCReportDataSourceService extends JdbcDataSourceService {


	public MongoDbJDBCReportDataSourceService(DataSource dataSource, TimeZone timezone) {
        super(dataSource, timezone);
	}

    @Override
    public boolean testConnection() throws SQLException {
	    Connection conn = getDataSource().getConnection();
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final Future future = executor.submit(new TestConnectionThread(conn));
            executor.shutdown(); // This does not cancel the already-scheduled task.

            try {
                if (!future.isDone()) future.get(5, TimeUnit.MINUTES);
            } catch (InterruptedException ie) {
                /* Handle the interruption. Or ignore it. */
                return false;
            } catch (ExecutionException ee) {
                /* Handle the error. Or ignore it. */
                return false;
            } catch (TimeoutException te) {
                /* Handle the timeout. Or ignore it. */
                return false;
            }
            return true;
        } finally {
            if (!executor.isTerminated()) executor.shutdownNow(); // If you want to stop the code that hasn't finished.
        }
    }


    public class TestConnectionThread extends Thread {

	    Connection connection;

	    TestConnectionThread(Connection connection) {
	        this.connection = connection;
        }

        public void run() {
	        try {
	            // test connection by obtaining schema
                // JRS-11418 Mongo JDBC: unable to establish connection to DB after network re-connection
                ResultSet resultSet = connection.getMetaData().getSchemas();
            } catch (SQLException sqlException) {
	            throw new RuntimeException(sqlException);
            }
        }
    }
}
