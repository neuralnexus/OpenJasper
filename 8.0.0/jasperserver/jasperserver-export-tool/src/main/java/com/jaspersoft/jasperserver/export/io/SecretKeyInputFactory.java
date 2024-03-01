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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.crypto.JrsKeystore;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.export.DefaultKeystorePasswdProvider;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;

import java.io.Console;
import java.util.Optional;
import java.util.function.Supplier;

import static com.jaspersoft.jasperserver.export.util.CommandUtils.requestConfirmedPassword;
import static com.jaspersoft.jasperserver.export.util.EncryptionParams.KEY_PASSWD_PARAMETER;
import static com.jaspersoft.jasperserver.export.util.EncryptionParams.KEY_STORE_PASSWD_PARAMETER;

/**
 * @author schubar
 * @version $Id$
 */
public class SecretKeyInputFactory implements ImportInputFactory {

    private String inputKeyParameter;
    private String importerBeanName;
    protected KeystoreManager keystoreManager;
    private JrsKeystore keystore;

    protected DefaultKeystorePasswdProvider defaultKeystorePasswdProvider;

    private final Console console;

    public SecretKeyInputFactory() {
        this.console = System.console();
    }

    /** For testing */
    SecretKeyInputFactory(Console console) {
        this.console = console;
    }

    public boolean matches(Parameters parameters) {
        return parameters.hasParameter(inputKeyParameter);
    }

    public ImportInput createInput(Parameters parameters) {
        EncryptionParams cryptoParameters = new EncryptionParams(parameters);
        Optional<String> keyAlias = cryptoParameters.getKeyAlias();
        Optional<String> keyStore = cryptoParameters.getKeyStore();
        final Supplier<String> passwdSupplier = () -> {
            String passwd = requestConfirmedPassword(console,
                    "Please enter key password: ",
                    "Please confirm key password: ");
            parameters.setParameterValue(KEY_PASSWD_PARAMETER, passwd);
            return passwd;
        };


        String[] key = parameters.getParameterValues(inputKeyParameter);
        if (key == null && !keyStore.isPresent() && !cryptoParameters.hasGenKey()) {
            // Adding non localized message cause import-export tool does not support localization.
            throw new JSException("No import secret key was specified.");
        }

        if (keyStore.isPresent()) {
            String keyStorePasswd;
            if (cryptoParameters.getKeyStorePasswd().isPresent() ) {
                keyStorePasswd = cryptoParameters.getKeyStorePasswd().get();
                if (keyStorePasswd.isEmpty()) {
                    keyStorePasswd = requestConfirmedPassword(console,
                            "Please enter keystore password: ",
                            "Please confirm keystore password: ");

                    parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, keyStorePasswd);
                }
            } else {
                if (defaultKeystorePasswdProvider != null) {
                    keyStorePasswd = defaultKeystorePasswdProvider.getDefaultPassword();
                    parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, keyStorePasswd);
                } else {
                    throw new JSException("--storepass must be specified.");
                }
            }

            if (keyAlias.isPresent()) {
                String keyPasswd = cryptoParameters.getKeyPasswd().orElseGet(passwdSupplier);
                if (keyPasswd.isEmpty()) {
                    passwdSupplier.get();
                }
            }

        } else {
            if ((!keyAlias.isPresent() || keyAlias.get().isEmpty())) {
                throw new JSException("--keyalias must be specified.");
            }

            if (!keystore.containsAlias(keyAlias.get())) {
                String keyPasswd = cryptoParameters.getKeyPasswd().orElseGet(passwdSupplier);
                if (keyPasswd.isEmpty()) {
                    passwdSupplier.get();
                }
            }
        }

        parameters.setParameterValue("importerBeanName", this.importerBeanName);

        return new SecretKeyInput(key, cryptoParameters);
    }

    public String getInputKeyParameter() {
        return inputKeyParameter;
    }

    public void setInputKeyParameter(String inputKeyParameter) {
        this.inputKeyParameter = inputKeyParameter;
    }

    public String getImporterBeanName() {
        return importerBeanName;
    }

    public void setImporterBeanName(String importerBeanName) {
        this.importerBeanName = importerBeanName;
    }

    public void setKeystoreManager(KeystoreManager keystoreManager) {
        this.keystoreManager = keystoreManager;
        keystore = keystoreManager.getKeystore(null);
    }

    public void setDefaultKeystorePasswdProvider(DefaultKeystorePasswdProvider defaultKeystorePasswdProvider) {
        this.defaultKeystorePasswdProvider = defaultKeystorePasswdProvider;
    }
}
