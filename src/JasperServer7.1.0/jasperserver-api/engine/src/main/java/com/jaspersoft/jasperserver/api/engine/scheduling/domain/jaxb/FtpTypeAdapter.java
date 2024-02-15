/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.FTPInfo;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class FtpTypeAdapter extends XmlAdapter<String, String> {
    private static final String CLIENT_TYPE_FTP = "ftp";
    private static final String CLIENT_TYPE_FTPS = "ftps";
    private static final String CLIENT_TYPE_SFTP = "sftp";
    @Override
    public String unmarshal(String v) throws Exception {
        return CLIENT_TYPE_FTPS.equals(v) ? FTPInfo.TYPE_FTPS : CLIENT_TYPE_SFTP.equals(v) ? FTPInfo.TYPE_SFTP : FTPInfo.TYPE_FTP;
    }

    @Override
    public String marshal(String v) throws Exception {
        return FTPInfo.TYPE_FTPS.equals(v) ? CLIENT_TYPE_FTPS : FTPInfo.TYPE_SFTP.equals(v) ? CLIENT_TYPE_SFTP  : CLIENT_TYPE_FTP;
    }
}
