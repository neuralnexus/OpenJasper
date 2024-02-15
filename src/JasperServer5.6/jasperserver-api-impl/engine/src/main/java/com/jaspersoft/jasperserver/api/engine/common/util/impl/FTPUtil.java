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

package com.jaspersoft.jasperserver.api.engine.common.util.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.FTPService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import java.io.InputStream;


public class FTPUtil implements FTPService {

    private static final Log log = LogFactory.getLog(FTPUtil.class);


    public FTPServiceClient connect(String host, String userName, String password) throws Exception {
        return new FTPSServiceClientImpl(host, 990, "TLS", true, 0, "P", userName, password);
    }

    public FTPServiceClient connectFTP(String host, int port, String userName, String password) throws Exception {
        return new FTPServiceClientImpl(host, port, userName, password);
    }

    public FTPServiceClient connectFTPS(String host, int port, String userName, String password) throws Exception {
        return new FTPSServiceClientImpl(host, port, "TLS", true, 0, "P", userName, password);
    }

    public FTPServiceClient connectFTPS(String host, int port, String protocol, boolean isImplicit, long pbsz, String prot, String userName, String password) throws Exception {
        return new FTPSServiceClientImpl(host, port, protocol, isImplicit, pbsz, prot, userName, password);
    }

    public class FTPSServiceClientImpl implements FTPServiceClient {

        private FTPSClient ftpClient = null;

        /*
        * Establishes a data connection with the FTPS server
        *
        * @param host ftp server host name
        * @param port ftp server port number
        * @param protocol ftp protocol
        * @param isImplicit the security mode for FTPS (Implicit/ Explicit)
        * @param pbsz pbsz value: 0 to (2^32)-1 decimal integer.
        * @param prot PROT command
        * @param userName login user name
        * @param password login password
        * @return FTPServiceClient interface to access ftp server
        */
        public FTPSServiceClientImpl(String host, int port, String protocol, boolean isImplicit, long pbsz, String prot, String userName, String password) throws Exception {
            if (protocol == null) protocol = "TLS";
            if (pbsz < 0) pbsz = 0;
            if (prot == null) prot = "P";

            ftpClient = new FTPSClient(protocol, isImplicit);
            ftpClient.connect(host, port);

            if (!ftpClient.login(userName, password)) {
                ftpClient.logout();
                throw new JSException("FTPS:  Invalid user name/ password.");
            }
            if (log.isDebugEnabled()) log.debug("FTPS:  connected to " + host + " LOGIN OK.");

            // After connection attempt, you should check the reply code to verify
            // success.
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new JSException("FTPS:  unable to connect to " + host);
            }
            if (log.isDebugEnabled()) log.debug("Connected to " + host + " REPLY OK.");
            ftpClient.execPBSZ(pbsz);
            ftpClient.execPROT(prot);
            ftpClient.enterLocalPassiveMode();
        }

        public void disconnect() throws Exception {
            FTPUtil.disconnect(ftpClient);
        }

        public void changeDirectory(String directoryPath) throws Exception {
            FTPUtil.changeDirectory(ftpClient, directoryPath);
        }

        public InputStream getFile(String fileName) throws Exception {
            return FTPUtil.getFile(ftpClient, fileName);
        }

        public void putFile(String fileName, InputStream inputData) throws Exception {
            FTPUtil.putFile(ftpClient, fileName, inputData);
        }

        public boolean exists(String fileName) throws Exception {
            return FTPUtil.exists(ftpClient, fileName);
        }

    }

    public class FTPServiceClientImpl implements FTPServiceClient {

        private FTPClient ftpClient = null;

        /*
        * Establishes a data connection with the FTP server
        *
        * @param host ftp server host name
        * @param port ftp server port number
        * @param userName login user name
        * @param password login password
        * @return FTPServiceClient interface to access ftp server
        */
        public FTPServiceClientImpl(String host, int port, String userName, String password) throws Exception {
            ftpClient = new FTPClient();
            ftpClient.connect(host, port);
            if (!ftpClient.login(userName, password)) {
                ftpClient.logout();
                throw new JSException("FTP:  Fail to login:  may due to invalid username/ password.");
            }
            if (log.isDebugEnabled()) log.debug("FTP:  connected to " + host + " LOGIN OK.");

            // After connection attempt, you should check the reply code to verify
            // success.
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new JSException("FTP:  unable to connect to " + host);
            }
            if (log.isDebugEnabled()) log.debug("Connected to " + host + " REPLY OK.");
            ftpClient.enterLocalPassiveMode();
        }

        public void disconnect() throws Exception {
            FTPUtil.disconnect(ftpClient);
        }

        public void changeDirectory(String directoryPath) throws Exception {
            FTPUtil.changeDirectory(ftpClient, directoryPath);
        }

        public InputStream getFile(String fileName) throws Exception {
            return FTPUtil.getFile(ftpClient, fileName);
        }

        public void putFile(String fileName, InputStream inputData) throws Exception {
            FTPUtil.putFile(ftpClient, fileName, inputData);
        }

        public boolean exists(String fileName) throws Exception {
            return FTPUtil.exists(ftpClient, fileName);
        }
    }

        private static void disconnect(FTPClient ftpClient) throws Exception {
            try {
                if ((ftpClient != null) && (ftpClient.isConnected())) ftpClient.disconnect();
            } catch (Exception ex) {
                throw ex;
            } finally {
                ftpClient = null;
            }
        }

        private static void changeDirectory(FTPClient ftpClient, String directoryPath) throws Exception {
            if (ftpClient == null) throw new JSException("Please connect to FTP server first before changing directory!");
            if (log.isDebugEnabled()) log.debug("Original Working directory = " + ftpClient.printWorkingDirectory());
            ftpClient.changeWorkingDirectory(directoryPath);
            if (log.isDebugEnabled()) log.debug("NEW Working directory = " + ftpClient.printWorkingDirectory());
        }


        private static InputStream getFile(FTPClient ftpClient, String fileName) throws Exception {
            if (ftpClient == null) throw new JSException("Please connect to FTP server first before changing directory!");
            return ftpClient.retrieveFileStream(fileName);
        }

        private static void putFile(FTPClient ftpClient, String fileName, InputStream inputData) throws Exception {
            if (ftpClient == null) throw new JSException("Please connect to FTP server first before changing directory!");
            if (log.isDebugEnabled()) log.debug("START:  FUT FILE = " + fileName);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean state = ftpClient.storeFile(ftpClient.printWorkingDirectory() + "/" + fileName, inputData);
            if (log.isDebugEnabled()) log.debug("END:  FUT FILE = " + fileName + " STATE = " + state);
            if (!state) {
               throw new JSException("Fail to upload file " + fileName);
            }
        }

        private static  boolean exists(FTPClient ftpClient, String fileName) throws Exception {
            if (ftpClient == null) throw new JSException("Please connect to FTP server first before changing directory!");
            try {
                if (log.isDebugEnabled()) log.debug("FTP Working directory = " + ftpClient.printWorkingDirectory());
                FTPFile[] files = ftpClient.listFiles(ftpClient.printWorkingDirectory());

                if (log.isDebugEnabled()) log.debug("FTP:  number of files - " + (files == null ? "NULL" : files.length));

                if (files == null) return false;
                for (FTPFile ftpFile : files) {
                    if (ftpFile.getName().equalsIgnoreCase(fileName)) return true;
                }
                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
}
