/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.export.io;

import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.crypto.JrsKeystore;
import com.jaspersoft.jasperserver.crypto.KeyProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.properties.KeyAlgorithm;
import com.jaspersoft.jasperserver.export.ImportInputMetadata;
import com.jaspersoft.jasperserver.export.ImportTask;
import com.jaspersoft.jasperserver.export.Importer;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

public class SecretKeyImporter implements Importer {
    protected static final CommandOut commandOut = CommandOut.getInstance();
    public static final String OK = "Key is successfully stored";

    protected ImportTask task;

    @Override
    public void setTask(ImportTask task) {
        this.task = task;

        ImportInputMetadata metadata = new ImportInputMetadata();
        metadata.setProperty(ImportExportService.ROOT_TENANT_ID, TenantService.ORGANIZATIONS);
        this.task.setInputMetadata(metadata);
    }

    @Override
    public void performImport() throws ImportFailedException {
        final EncryptionParams params = new EncryptionParams(task.getParameters());

        KeystoreManager keystoreMng = KeystoreManager.getInstance();
        JrsKeystore keystore = keystoreMng.getKeystore(null);

        byte[] bytes;

        try (InputStream in = task.getInput().getFileInputStream(null)) {
            int n = in.available();
            bytes = new byte[n];
            in.read(bytes, 0, n);
        } catch (Exception e) {
            throw new ImportFailedException(e.getMessage());
        }

        Optional<String> keyAlias = params.getKeyAlias();
        if (!keyAlias.isPresent() || keyAlias.get().isEmpty()) {
            final String message = "Key alias is required.";
//            commandOut.error(message);

            throw new ImportFailedException(message);
        }

        if (!params.getKeyAlg().isPresent()) {
            final String message = "Key algorithm is required.";
//            commandOut.error(message);

            throw new ImportFailedException(message);
        }

        int keySize = 0;
        if (params.getKeySize().isPresent()) {
            try {
                keySize = parseInt(params.getKeySize().get());
            } catch (NumberFormatException e) {
                final String message = "Key size is invalid.";
//                commandOut.error(message);

                throw new ImportFailedException(message);
            }
        }

        if (params.hasGenKey() && keySize <= 0) {
            final String message = "Key size is required for key generation.";
//            commandOut.error(message);

            throw new ImportFailedException(message);
        }

        String keyPasswd = params.getKeyPasswd().orElseGet(() -> {
            try {
                final KeyProperties keystoreProperties = keystore.getKeyProperties(keyAlias.get());
                return keystoreProperties.getKeyPasswd() == null ? params.getKeyStorePasswd().orElse("") : keystoreProperties.getKeyPasswd();
            } catch (Exception e) {
                return params.getKeyStorePasswd().orElse("") ;
            }
        });
        String keyAlg = params.getKeyAlg().get();

        final KeyProperties keyProperties = new KeyProperties(keyAlias.get(), keyPasswd);
        keyProperties.setKeyAlg(keyAlg);
        params.getKeyVisible().ifPresent(keyProperties::setKeyVisible);
        params.getKeyLabel().ifPresent(keyProperties::setKeyLabel);
        params.getKeyOrganisation().ifPresent(keyProperties::setKeyOrganization);

        if (bytes.length > 0 && (keyAlg.equals(KeyAlgorithm.AES) || keyAlg.equals(KeyAlgorithm.RSA) || keyAlg.equals(KeyAlgorithm.DES_EDE))) {
            Key key = new SecretKeySpec(bytes, keyAlg);
            keystore.setKey(key, keyProperties);

            commandOut.info(OK);
        } else if (params.hasGenKey() && keyAlg.equals(KeyAlgorithm.AES)) {
            try {
                keyProperties.setKeyAlg(keyAlg);
                keyProperties.setKeySize(keySize);
                keystore.generateSecret(keyProperties, false);

            } catch (Exception e) {
                throw new ImportFailedException(e.getMessage());
            }

            commandOut.info(OK);
        } else if (params.hasGenKey() && keyAlg.equals(KeyAlgorithm.RSA)) {
            try {
                keyProperties.setKeyAlg(keyAlg);
                keyProperties.setKeySize(keySize);
                keystore.generateKeyPair(keyProperties, false);

            } catch (Exception e) {
                throw new ImportFailedException(e.getMessage());
            }

            commandOut.info(OK);
        } else {
            final String message = format("Key algorithm '%s' is not supported.", keyAlg);
            throw new ImportFailedException(message);
        }
    }
}
