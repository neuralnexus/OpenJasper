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

import com.jaspersoft.jasperserver.api.common.util.FTPService;
import com.jaspersoft.jasperserver.api.engine.common.util.impl.FTPUtil;
import com.jaspersoft.jasperserver.dto.connection.FtpConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: FtpConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class FtpConnectionStrategy implements ConnectionManagementStrategy<FtpConnection> {
    private final static Log log = LogFactory.getLog(FtpConnectionStrategy.class);
    private FTPService ftpService = new FTPUtil();

    @Override
    public FtpConnection createConnection(FtpConnection connectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        FTPService.FTPServiceClient client = null;
        try {
            if (connectionDescription.getType() == FtpConnection.FtpType.ftp) {
                client = ftpService.connectFTP(connectionDescription.getHost(), connectionDescription.getPort(),
                        connectionDescription.getUserName(), connectionDescription.getPassword());
            } else {
                client = ftpService.connectFTPS(connectionDescription.getHost(), connectionDescription.getPort(),
                        connectionDescription.getProtocol(), connectionDescription.getImplicit(),
                        connectionDescription.getPbsz(), connectionDescription.getProt(),
                        connectionDescription.getUserName(), connectionDescription.getPassword());
            }
            client.changeDirectory(connectionDescription.getFolderPath());
        } catch (UnknownHostException e) {
            throw new ConnectionFailedException(connectionDescription.getHost(), "host", null, e);
        } catch (Exception e) {
            throw new ConnectionFailedException(connectionDescription, e);
        } finally {
            if (client != null) try {
                client.disconnect();
            } catch (Exception e) {
                log.error("Couldn't disconnect FTP connection", e);
            }
        }
        return connectionDescription;
    }

    @Override
    public void deleteConnection(FtpConnection connectionDescription, Map<String, Object> data) {
        // do nothing
    }

    @Override
    public FtpConnection modifyConnection(FtpConnection newConnectionDescription, FtpConnection oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        // here is nothing to update, just check if it can be connected.
        return createConnection(newConnectionDescription, data);
    }

    @Override
    public FtpConnection secureGetConnection(FtpConnection connectionDescription, Map<String, Object> data) {
        return new FtpConnection(connectionDescription).setPassword(null);
    }
}
