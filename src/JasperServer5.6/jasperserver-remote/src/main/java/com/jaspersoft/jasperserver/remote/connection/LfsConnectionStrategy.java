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

import com.jaspersoft.jasperserver.dto.connection.LfsConnection;
import org.springframework.security.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: LfsConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class LfsConnectionStrategy implements ConnectionManagementStrategy<LfsConnection> {
    @Override
    public LfsConnection createConnection(LfsConnection connectionDescription, Map<String, Object> data) {
        final String path = connectionDescription.getPath();
        File file = new File(path);
        if (!file.exists() || !file.isDirectory() || !canWriteToDirectory(path)) {
            throw new AccessDeniedException(path);
        }
        return connectionDescription;
    }

    protected boolean canWriteToDirectory(String path) {
        boolean canWrite = false;
        final File testFile = new File(path + File.separator + "JRS_canWriteFile.tmp");
        try {
            if (testFile.exists()) {
                testFile.delete();
            }
            canWrite = testFile.createNewFile();
        } catch (IOException e) {
            // do nothing. Directory isn't writable
        } catch (SecurityException e) {
            // do nothing. Directory isn't writable
        } finally {
            if (testFile.exists()) {
                try {
                    testFile.delete();
                } catch (SecurityException e) {
                    // no delete permissions. Let it be...
                }
            }
        }
        return canWrite;
    }

    @Override
    public void deleteConnection(LfsConnection connectionDescription, Map<String, Object> data) {

    }

    @Override
    public LfsConnection modifyConnection(LfsConnection newConnectionDescription, LfsConnection oldConnectionDescription, Map<String, Object> data) {
        return createConnection(newConnectionDescription, data);
    }

    @Override
    public LfsConnection secureGetConnection(LfsConnection connectionDescription, Map<String, Object> data) {
        return connectionDescription;
    }
}
