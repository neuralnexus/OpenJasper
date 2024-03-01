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
package com.jaspersoft.buildomatic.crypto;

import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.conf.DiagnosticDataEnc;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.Key;
import java.util.Optional;

public class EncryptZipTask extends Task implements DiagnosticAntTask {
    private String inFile;
    private String encProps;
    private boolean genkey;
    private DiagnosticCryptoUtil diagnosticCryptoUtil = new DiagnosticCryptoUtil(this);

    /**
     * Encrypts diagnostic ZIP file
     *
     * @throws BuildException
     */
    @Override
    public void execute() throws BuildException {
        File inF = new File(inFile);

        try {
            EncryptionProperties encryptionProperties = diagnosticCryptoUtil.getEncryptionProperties(encProps);
            Key key = null;
            if (genkey) {
                key = KeystoreManager.generateSecret(encryptionProperties.getKeyProperties());
            } else {
                key = KeystoreManager.getInstance().getKey(DiagnosticDataEnc.ID);
            }

            if (!inF.exists()) {
                throw new IllegalArgumentException("Input file not exists.");
            }
            if (key == null) {
                throw new IllegalArgumentException("Failed to resolve secret key..");
            }

            if (inF.isDirectory()) {
                File[] files = inF.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().contains("log")) {
                            // TODO danger: deleting file from collection over which iterating
                            File outF = diagnosticCryptoUtil.createEncryptedOutputFile(file);
                            EncryptionEngine.encryptFile(file, outF, key, encryptionProperties);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("No files to encrypt found in directory: " + inF.getName());
                }
            } else {
                File outF = diagnosticCryptoUtil.createEncryptedOutputFile(inF);
                EncryptionEngine.encryptFile(inF, outF, key, encryptionProperties);
            }

            if (genkey) {
                logMessage("Secret Key : " + stringify(key.getEncoded()));
            }
        } catch (Exception e) {
            log("Error encrypting the file. " + e);
            e.printStackTrace();
        }
    }
    // TODO Duplicated
    public static String stringify(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("0x%02x ", b & 0xff));
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void logMessage(String message) {
        log(message);
    }

    public void setInFile(String inFile) {
        this.inFile = inFile;
    }

    public void setEncProps(String encProps) {
        this.encProps = encProps;
    }

    public void setGenkey(boolean genkey) {
        this.genkey = genkey;
    }
}