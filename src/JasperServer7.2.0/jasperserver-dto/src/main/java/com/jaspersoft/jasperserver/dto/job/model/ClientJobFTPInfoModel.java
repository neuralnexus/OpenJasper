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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.job.ClientJobFtpInfo;

import java.util.Map;

/**
 * Holder for FTP information
 *
 * @author tetiana.iefimenko
 * @version $Id$
 */
public class ClientJobFTPInfoModel extends ClientJobFtpInfo {

    private boolean isUserNameModified = false;
    private boolean isPasswordModified = false;
    private boolean isFolderPathModified = false;
    private boolean isServerNameModified = false;
    private boolean isPropertiesMapModified = false;

    public ClientJobFTPInfoModel() {}

    public ClientJobFTPInfoModel(ClientJobFTPInfoModel other) {
        super(other);
        this.isUserNameModified = other.isUserNameModified;
        this.isPasswordModified = other.isPasswordModified;
        this.isFolderPathModified = other.isFolderPathModified;
        this.isServerNameModified = other.isServerNameModified;
        this.isPropertiesMapModified = other.isPropertiesMapModified;
    }


    public boolean isFolderPathModified() {
        return isFolderPathModified;
    }

    public boolean isPasswordModified() {
        return isPasswordModified;
    }

    public boolean isPropertiesMapModified() {
        return isPropertiesMapModified;
    }

    public boolean isServerNameModified() {
        return isServerNameModified;
    }

    public boolean isUserNameModified() {
        return isUserNameModified;
    }

    @Override
    public ClientJobFTPInfoModel setUserName(String userName) {
        super.setUserName(userName);
        isUserNameModified = true;
        return this;
    }

    @Override
    public ClientJobFTPInfoModel setPassword(String password) {
        super.setPassword(password);
        isPasswordModified = true;
        return this;
    }

    @Override
    public ClientJobFTPInfoModel setFolderPath(String folderPath) {
        super.setFolderPath(folderPath);
        isFolderPathModified = true;
        return this;
    }

    @Override
    public ClientJobFTPInfoModel setServerName(String serverName) {
        super.setServerName(serverName);
        isServerNameModified = true;
        return this;
    }

    @Override
    public ClientJobFTPInfoModel setPropertiesMap(Map<String, String> propertiesMap) {
        super.setPropertiesMap(propertiesMap);
        isPropertiesMapModified = true;
        return this;
    }

    @Override
    public ClientJobFTPInfoModel deepClone() {
        return new ClientJobFTPInfoModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobFTPInfoModel)) return false;
        if (!super.equals(o)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isUserNameModified() ? 1 : 0);
        result = 31 * result + (isPasswordModified() ? 1 : 0);
        result = 31 * result + (isFolderPathModified() ? 1 : 0);
        result = 31 * result + (isServerNameModified() ? 1 : 0);
        result = 31 * result + (isPropertiesMapModified() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobFTPInfoModel{" +
                "isFolderPathModified=" + isFolderPathModified +
                ", isUserNameModified=" + isUserNameModified +
                ", isPasswordModified=" + isPasswordModified +
                ", isServerNameModified=" + isServerNameModified +
                ", isPropertiesMapModified=" + isPropertiesMapModified +
                '}';
    }
}
