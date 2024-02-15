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

import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ConnectionManagementStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface ConnectionManagementStrategy<ConnectionDescriptionType> {
    /**
     * Try to establish the connection by the given description.
     * @param connectionDescription - description of the connection to establish
     * @return the connection description after successful establishment
     * @throws IllegalParameterValueException in case if any description parameter is incorrect.
     */
    ConnectionDescriptionType createConnection(ConnectionDescriptionType connectionDescription, Map<String, Object> data) throws IllegalParameterValueException;

    /**
     * Do clean actions (if needed) before connection description is removed from the cache.
     * @param connectionDescription
     */
    void deleteConnection(ConnectionDescriptionType connectionDescription, Map<String, Object> data);

    /**
     * Try to connect with modified description.
     *
     * @param newConnectionDescription - modified connection description
     * @param oldConnectionDescription - old connection description
     * @return modified connection description
     * @throws IllegalParameterValueException in case if any description parameter is incorrect.
     */
    ConnectionDescriptionType modifyConnection(ConnectionDescriptionType newConnectionDescription,
            ConnectionDescriptionType oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException;

    /**
     * Prepare connection description object to be returned to the client. E.g. here is the place to return description copy without password.
     * @param connectionDescription - the original connection description to process.
     * @return connection description without authentication data.
     */
    ConnectionDescriptionType secureGetConnection(ConnectionDescriptionType connectionDescription, Map<String, Object> data);
}
