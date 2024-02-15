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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.dto.connection.JndiConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: JndiConnectionStrategy.java 48307 2014-08-15 21:38:37Z ichan $
 */
@Service
public class JndiConnectionStrategy implements ConnectionManagementStrategy<JndiConnection> {
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public JndiConnection createConnection(JndiConnection connectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        Connection conn = null;
        boolean passed = false;
        Throwable exception = null;
        try {
            Context ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/" + connectionDescription.getJndiName());
            conn = dataSource.getConnection();
            if (conn != null) {
                passed = true;
            }
        } catch (SQLException vex) {
            if (vex.getMessage().indexOf("[JI_CONNECTION_VALID]") >= 0) passed = true;
            exception = vex;
        } catch(Throwable e) {
            exception = e;
        } finally {
            if(conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Couldn't disconnect JNDI connection", e);
                }
        }
        if(!passed){
            throw new ConnectionFailedException(connectionDescription.getJndiName(), "jndiName", "Invalid JNDI name: "
                    + connectionDescription.getJndiName(), exception);
        }
        return connectionDescription;
    }

    @Override
    public void deleteConnection(JndiConnection connectionDescription, Map<String, Object> data) {
    }

    @Override
    public JndiConnection modifyConnection(JndiConnection newConnectionDescription, JndiConnection oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        // here is nothing to update, just check if it can be connected.
        return createConnection(newConnectionDescription, data);
    }

    @Override
    public JndiConnection secureGetConnection(JndiConnection connectionDescription, Map<String, Object> data) {
        // no hidden attributes
        return connectionDescription;
    }

}
