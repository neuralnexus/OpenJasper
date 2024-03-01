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
package com.jaspersoft.jasperserver.api.security;


import com.jaspersoft.jasperserver.api.JSSecurityException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Static class which represents security configuration,
 * accessed by security validators and filters.
 *
 * @author Anton Fomin
 * @version $Id$
 */
public final class SecurityConfiguration {
    /* Logger */
    private static final Logger log = LogManager.getLogger(SecurityConfiguration.class);

    /* Security configuration properties */
    private static Properties securityConfig = null;

    /* Security configuration property file */
    private static final String SECURITY_CONFIG = "esapi/security-config.properties";

    /* Property names */
    private static final String INPUT_FILTER_SWITCH = "security.validation.input.on";
    private static final String SQL_FILTER_SWITCH = "security.validation.sql.on";
    public static final String SQL_COMMENTS_REGEXP = "security.validation.sql.comments.regexp";
    private static final String VALIDATE_SQL_VIA_META_QUERY_EXEC = "validate.sql.via.metadata.query.execution";
    private static final String ENCRYPTION_FILTER_SWITCH = "encryption.on";

    private static final String MSG_SECURITY_OFF = "log.msg.security.off";

    private static final String LOG_MSG_CANNOT_LOAD = "msg.cannot.load";

    private static final String DYNAMIC_KEY_GENERATION = "encryption.dynamic.key";
    private static final String ENCRYPTION_TYPE = "encryption.type";
    private static final String KEYPAIR_SESSION_KEY = "encryption.keypairSessionKey";
    private static final String ENCRYPTION_PARAM_PREFIX = "encryption.param.";

    private static final String INPUT_VALIDATION = "INPUT-VALIDATION";

    private static final String SQL_INJECTION = "SQL-INJECTION";
    private static final String ENCRYPTION = "ENCRYPTION";

    private static final Set<String> encryptedParameters = new HashSet<String>();

    /**
     * Instantiation is not allowed
     */
    private SecurityConfiguration() {
        /* Do nothing */
    }

    /**
     * Initialize it
     */
    static {
        loadSecurityConfiguration();
        logSettings();
        initEncryptedParameters();
    }

    private static void initEncryptedParameters() {
        for (String propName : securityConfig.stringPropertyNames()) {
            if (propName.startsWith(ENCRYPTION_PARAM_PREFIX)) {
                encryptedParameters.add(getProperty(propName));
            }
        }

    }

    /**
     * Load security configuration file only once
     */
    private static synchronized void loadSecurityConfiguration() {
        if (securityConfig == null) {
            securityConfig = new Properties();
            try {
                InputStream is = SecurityConfiguration.class.getClassLoader().getResourceAsStream(SECURITY_CONFIG);
                securityConfig.load(is);
                is.close();
            }
            catch (Exception e) {
				final String errMsg = String.format(getProperty(LOG_MSG_CANNOT_LOAD), SECURITY_CONFIG);
				log.error(errMsg, e);
				throw new JSSecurityException(errMsg, e);
            }
        }
    }

    /*
     * Log security settings if they are off only
     * because security is turned on by default.
     */
    private static void logSettings() {
        if (!isInputValidationOn()) {
            logSecuritySetting(INPUT_VALIDATION);
        }

        if (!isSQLValidationOn()) {
            logSecuritySetting(SQL_INJECTION);
        }

        if (!isEncryptionOn()) {
            logSecuritySetting(ENCRYPTION);
        }

    }

    /**
     * @return false only if config is 'false'
     */
    public static boolean isEncryptionOn() {
        final String encryptioOn = getProperty(ENCRYPTION_FILTER_SWITCH);
        return encryptioOn != null && encryptioOn.equalsIgnoreCase("false") ? false : true;
    }

    /**
     * @return true only if config is true
     */
    public static boolean isInputValidationOn() {
        final String inputValidationOn = getProperty(INPUT_FILTER_SWITCH);
        return inputValidationOn != null && inputValidationOn.equalsIgnoreCase("false") ? false : true;
    }

    /**
     * @return true only if config is true
     */
    public static boolean isSQLValidationOn() {
        final String sqlValidationOn = getProperty(SQL_FILTER_SWITCH);
        return sqlValidationOn != null && sqlValidationOn.equalsIgnoreCase("false") ? false : true;
    }

    public static String getMsgSecurityOff() {
        return getProperty(MSG_SECURITY_OFF);
    }

    public static String getEncryptionKeyPairSession() {
        return getProperty(KEYPAIR_SESSION_KEY);
    }

    public static String getEncryptionType() {
        return getProperty(ENCRYPTION_TYPE);
    }

    public static Set<String> getEncryptionParameters() {
        return encryptedParameters;
    }

    /**
     * @return true only if config is true
     */
    public static boolean isEncryptionDynamicKeyGeneration() {
        return Boolean.parseBoolean(getProperty(DYNAMIC_KEY_GENERATION));
    }

    /* Get property string from config properties */
    public static String getProperty(String propertyName) {
        return securityConfig.getProperty(propertyName,"");
    }

    /**
     * Log security setting.
     *
     * @param typeOfValidation Type of validation: Input, CSRF, SQL, or Encryption
     */
    private static void logSecuritySetting(String typeOfValidation) {
        log.warn(String.format(getProperty(MSG_SECURITY_OFF), typeOfValidation));
    }

    public static boolean isSqlValidationViaMetadataOn() {
        return Boolean.parseBoolean(getProperty(VALIDATE_SQL_VIA_META_QUERY_EXEC));
    }
}


