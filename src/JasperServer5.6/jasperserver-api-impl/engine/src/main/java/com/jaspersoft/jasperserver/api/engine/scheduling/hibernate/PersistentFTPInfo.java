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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.FTPInfo;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.FTPInfoModel;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.util.NullValue;

import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
/**
 * @author Ivan Chan (lucianc@users.sourceforge.net)
 * @version $Id: PersistentFTPInfo.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class PersistentFTPInfo {
	private static final Logger logger = LogManager.getLogger(PersistentFTPInfo.class);

    String userName;
    String password;
    String folderPath;
    String serverName;
    Map<String, String> PropertiesMap;

	public PersistentFTPInfo() {
	}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Map<String, String> getPropertiesMap() {
        return PropertiesMap;
    }

    public void setPropertiesMap(Map<String, String> propertiesMap) {
        PropertiesMap = propertiesMap;
    }

    public void copyFrom(FTPInfo ftpInfo) {
        setUserName(ftpInfo.getUserName());
        setPassword(PasswordCipherer.getInstance().encodePassword(ftpInfo.getPassword()));
        setFolderPath(ftpInfo.getFolderPath());
        setServerName(ftpInfo.getServerName());
        setPropertiesMap(NullValue.replaceWithNullValues(ftpInfo.getPropertiesMap()));
	}

	public void copyFrom(FTPInfoModel ftpInfoModel) {
        if (ftpInfoModel.isUserNameModified()) setUserName(ftpInfoModel.getUserName());
        if (ftpInfoModel.isPasswordModified()) setPassword(PasswordCipherer.getInstance().encodePassword(ftpInfoModel.getPassword()));
        if (ftpInfoModel.isFolderPathModified()) setFolderPath(ftpInfoModel.getFolderPath());
        if (ftpInfoModel.isServerNameModified()) setServerName(ftpInfoModel.getServerName());
        if (ftpInfoModel.isPropertiesMapModified()) setPropertiesMap(NullValue.replaceWithNullValues(ftpInfoModel.getPropertiesMap()));

	}

	public FTPInfo toClient() {
	    FTPInfo ftpInfo = new FTPInfo();
        ftpInfo.setUserName(getUserName());
        ftpInfo.setFolderPath(getFolderPath());
        ftpInfo.setServerName(getServerName());
        ftpInfo.setPropertiesMap(NullValue.restoreNulls(getPropertiesMap()));

		try {
			ftpInfo.setPassword(PasswordCipherer.getInstance().decodePassword(getPassword()));
		} catch (Exception e) {
			logger.warn("FTP Info for " + ftpInfo.getServerName() + ": failed to decrypt password. Most likely reason is unencrypted legacy entries in db.");
			ftpInfo.setPassword(getPassword());
		}

		return ftpInfo;
	}

}
