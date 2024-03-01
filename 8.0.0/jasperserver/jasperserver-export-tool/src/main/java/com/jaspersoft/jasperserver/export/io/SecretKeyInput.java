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

import com.jaspersoft.jasperserver.api.common.crypto.Hexer;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * @author schubar
 * @version $Id$
 */
public class SecretKeyInput extends BaseImportInput {

    private final String[] secretKey;
    private EncryptionParams parameters;

    public SecretKeyInput(String[] secretKey, EncryptionParams parameters) {
        this.secretKey = secretKey;
        this.parameters = parameters;
    }

    @Override
    public void open() throws IOException {
    }

    @Override
    public void close() throws IOException {
        // noop
    }

    @Override
    public boolean fileExists(String path) {
        return path != null && path.length() > 0;
    }

    @Override
    public boolean folderExists(String path) {
        return fileExists(path);
    }

    @Override
    public ByteArrayInputStream getFileInputStream(String path) throws IOException {
        if (secretKey != null && secretKey.length > 0) {
            try {
                final byte[] key = Hexer.parse(secretKey);
                return new ByteArrayInputStream(key);
            } catch (Exception e) {
                throw new IOException(format("Key import failed due to error: can't parse hex \"%s\"",
                        String.join(" ", secretKey)), e);
            }

        } else if (parameters.hasGenKey()) {
            return new ByteArrayInputStream(new byte[0]);

        } else {
            Optional<String> keyStoreParam = parameters.getKeyStore();
            final KeyStore keystore;

            if (keyStoreParam.isPresent() && !keyStoreParam.get().isEmpty()) {
                String keyStorePasswd = parameters.getKeyStorePasswd().orElse("");

                final Enumeration<String> aliases;
                try(InputStream keyStoreFile = new FileInputStream(keyStoreParam.get())) {
                    keystore = KeyStore.getInstance(parameters.getKeyStoreType().orElse(KeystoreManager.KS_TYPE));
                    keystore.load(keyStoreFile, keyStorePasswd.toCharArray());
                    aliases = keystore.aliases();
                } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
                    throw new IOException("Failed to load provided keystore", e);
                }

                if(!aliases.hasMoreElements()){
                    throw new IOException("Failed to import a key. Keystore is empty.");
                }

                final String keyAlias;
                String keyPasswd;
                if (parameters.getKeyAlias().isPresent()) {
                    keyAlias = parameters.getKeyAlias().get();
                    keyPasswd = parameters.getKeyPasswd().orElseThrow(() -> new RuntimeException("Key password was not specified"));
                } else {
                    keyAlias = pickUuidOrVeryFirst(aliases);
                    parameters.setKeyAlias(keyAlias);
                    keyPasswd = parameters.getKeyPasswd().orElse(keyStorePasswd);
                    parameters.getKeyPasswd().ifPresent(parameters::setKeyPasswd);
                }

                try {
                    Key key = keystore.getKey(keyAlias, keyPasswd.toCharArray());
                    if (key == null) throw new RuntimeException("ERROR: Specified key couldn't be recovered from the keystore.");
                    if (!parameters.getKeyAlg().isPresent()) {
                        parameters.setKeyAlg(key.getAlgorithm());
                    } else {
                        if (!key.getAlgorithm().equalsIgnoreCase(parameters.getKeyAlg().get())) {
                           throw new RuntimeException("ERROR: Specified key algorithm doesn't match the recovered key algorithm from the keystore.");
                        }
                    }

                    return new ByteArrayInputStream(key.getEncoded());
                } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                    throw new IOException("Failed to import specified key", e);
                }
            }
        }

        throw new IOException("No key or keystore was specified.");
    }

    @Override
    public void propertiesRead(Properties properties) {

    }

    private String pickUuidOrVeryFirst(final Enumeration<String> aliases) {
        String first = null;
        String alias = null;
        while (aliases.hasMoreElements()) {
            try {
                UUID.fromString(alias = aliases.nextElement());
                return alias;
            } catch (Exception e) {
                if (first == null && alias != null) first = alias;
                // keep looking for uuid
            }
        }

        return first;
    }
}
