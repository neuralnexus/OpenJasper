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

package com.jaspersoft.jasperserver.export.util;

import com.jaspersoft.jasperserver.export.Parameters;

import java.util.Optional;

import static java.lang.String.join;

public class EncryptionParams {
    /**
     * Secret key
     */
    public static final String SECRET_KEY = "secret-key";

    /**
     * Secret key
     */
    public static final String GEN_KEY = "genkey";

    /**
     * Parameter defines an algorithm/transformation of the key used in encryption.
     * A transformation always includes the name of a cryptographic algorithm (e.g., AES), and may be followed by a feedback mode and padding scheme.
     * <p>
     * A transformation is of the form:
     * - "algorithm/mode/padding" or
     * - "algorithm"
     * </p>
     * <p>
     * Default value: "AES/CBC/PKCS5Padding"
     * </p>
     */
    public static final String KEY_ALGORITHM_PARAMETER = "keyalg";

    public static final String KEY_SIZE_PARAMETER = "keysize";

    /**
     * Parameter defines a unique string to identify the key entry in the keystore.
     * <p>
     * Default value: "importExportKey"
     * </p>
     */
    public static final String KEY_ALIAS_PARAMETER = "keyalias";

    public static final String KEY_UUID_PARAMETER = "keyuuid";

    /**
     * The key password for a key entry in the keystore.
     */
    public static final String KEY_PASSWD_PARAMETER = "keypass";

    /**
     * The new key password for a key entry in the keystore.
     */
    public static final String NEW_KEY_PASSWD_PARAMETER = "newkeypass";


    /**
     * Parameter defines defines a keystore location.
     * The keystore is type of database that represents a storage facility for cryptographic keys and certificates.
     * TODO Describe defaults if not specified
     */
    public static final String KEY_STORE_PARAMETER = "keystore";

    /**
     * Parameter defines defines a keystore location.
     * The keystore is type of database that represents a storage facility for cryptographic keys and certificates.
     * TODO Describe defaults if not specified
     */
    public static final String KEY_STORE_PASSWD_PARAMETER = "storepass";

    /**
     * Parameter defines type of the keystore.
     * TODO Describe defaults if not specified
     */
    public static final String KEY_STORE_TYPE_PARAMETER = "storetype";

    /**
     * Parameter defines defines a keystore location.
     * The keystore is type of database that represents a storage facility for cryptographic keys and certificates.
     * TODO Describe defaults if not specified
     */
    public static final String DEST_KEY_STORE_PARAMETER = "destkeystore";

    /**
     * Parameter defines type of the keystore.
     * TODO Describe defaults if not specified
     */
    public static final String DEST_STORE_PASSWD_PARAMETER = "deststorepass";

    /**
     * Parameter defines type of the keystore.
     * TODO Describe defaults if not specified
     */
    public static final String DEST_KEY_PASSWD_PARAMETER = "destkeypass";

    public static final String KEY_LABEL_PARAMETER = "keylabel";
    public static final String KEY_ORGANISATION_PARAMETER = "keyorganisation";
    public static final String KEY_VISIBLE_PARAMETER = "visible";

    public static final String FAIL_ON_WRONG_KEY = "fail-on-wrong-key";

    /**
     * Parameter to specify a properties file to define all of the above in a single file.
     * If this properties file is specified when any of above parameters are specified than they take precedence
     * Encryption properties file may define more properties that just parameters above, but they are not required and have well defined defaults.
     * Encryption Properties that are available only trough properties file are:
     * - block.size (default to 16 if not defined)
     * - key.size (default to 128 if not defined)
     */
    public static final String ENCRYPTION_PROPERTIES_PARAMETER = "encryption-props";

    private Parameters parameters;

    public EncryptionParams(Parameters parameters) {
        this.parameters = parameters;
    }

    private boolean hasParameter(String parameterName) {
        return parameters.hasParameter(parameterName);
    }

    private Optional<String> getValue(String parameterName) {
        return Optional.ofNullable(parameters.getParameterValue(parameterName));
    }

    public Optional<String> getKeyStoreType() {
        return hasParameter(KEY_STORE_TYPE_PARAMETER)
                ? getValue(KEY_STORE_TYPE_PARAMETER) : Optional.empty();
    }

    public Optional<String> getKeyStore() {
        return hasParameter(KEY_STORE_PARAMETER)
                ? getValue(KEY_STORE_PARAMETER) : Optional.empty();
    }

    public Optional<String> getKeyStorePasswd() {
        return hasParameter(KEY_STORE_PASSWD_PARAMETER)
                ? getValue(KEY_STORE_PASSWD_PARAMETER) : Optional.empty();
    }

    public Optional<String> getDestKeyStore() {
        return hasParameter(DEST_KEY_STORE_PARAMETER)
                ? getValue(DEST_KEY_STORE_PARAMETER) : Optional.empty();
    }

    public Optional<String> getDestStorePasswd() {
        return hasParameter(DEST_STORE_PASSWD_PARAMETER)
                ? getValue(DEST_STORE_PASSWD_PARAMETER) : Optional.empty();
    }

    public Optional<String> getDestKeyPasswd() {
        return hasParameter(DEST_KEY_PASSWD_PARAMETER)
                ? getValue(DEST_KEY_PASSWD_PARAMETER) : Optional.empty();
    }

    public Optional<String> getKeyAlias() {
        return hasParameter(KEY_ALIAS_PARAMETER)
                ? getValue(KEY_ALIAS_PARAMETER) : Optional.empty();
    }

    public void setKeyAlias(String a) {
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, a);
    }

    public Optional<String> getKeyUuid() {
        return hasParameter(KEY_UUID_PARAMETER)
                ? getValue(KEY_UUID_PARAMETER) : Optional.empty();
    }

    public Optional<String> getKeyPasswd() {
        return hasParameter(KEY_PASSWD_PARAMETER)
                ? getValue(KEY_PASSWD_PARAMETER) : Optional.empty();
    }

    public void setKeyPasswd(String passwd) {
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, passwd);
    }

    public Optional<String> getSecretKey() {
        if (hasParameter(SECRET_KEY)) {
            final String[] values = parameters.getParameterValues(SECRET_KEY);

            return values == null ? Optional.empty() : Optional.of(join(" ", values).trim());
        } else return Optional.empty();
    }

    public boolean hasGenKey() {
        return hasParameter(GEN_KEY);
    }

    public Optional<String> getKeySize() {
        if (hasParameter(KEY_SIZE_PARAMETER)) {
            return getValue(KEY_SIZE_PARAMETER);
        } else return Optional.empty();
    }

    public Optional<String> getKeyAlg() {
        return hasParameter(KEY_ALGORITHM_PARAMETER)
                ? getValue(KEY_ALGORITHM_PARAMETER) : Optional.empty();
    }

    public void setKeyAlg(String alg) {
        parameters.setParameterValue(KEY_ALGORITHM_PARAMETER, alg);
    }

    public Optional<String> getKeyLabel() {
        return hasParameter(KEY_LABEL_PARAMETER)
                ? getValue(KEY_LABEL_PARAMETER) : Optional.empty();
    }

    public void setKeyLabel(String label) {
        parameters.setParameterValue(KEY_LABEL_PARAMETER, label);
    }

    public Optional<Boolean> getKeyVisible() {
        return hasParameter(KEY_VISIBLE_PARAMETER)
                ? getValue(KEY_VISIBLE_PARAMETER).map(Boolean::parseBoolean).map(Optional::of).orElseGet(() -> Optional.of(Boolean.TRUE))
                : Optional.empty();
    }

    public void setKeyVisible(Boolean visible) {
        parameters.setParameterValue(KEY_VISIBLE_PARAMETER, visible.toString());
    }

    public Optional<String> getKeyOrganisation() {
        return hasParameter(KEY_ORGANISATION_PARAMETER)
                ? getValue(KEY_ORGANISATION_PARAMETER) : Optional.empty();
    }

    public void setKeyOrganisation(String organisationId) {
        parameters.setParameterValue(KEY_ORGANISATION_PARAMETER, organisationId);
    }

    public Optional<Boolean> getFailOnWrongKey() {
        return hasParameter(FAIL_ON_WRONG_KEY) ?
                getValue(FAIL_ON_WRONG_KEY)
                        .map(Boolean::parseBoolean)
                : Optional.empty();
    }

    public void setFailOnWrongKey(Boolean fail) {
        parameters.setParameterValue(FAIL_ON_WRONG_KEY, fail.toString());
    }

}
