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
package com.jaspersoft.jasperserver.api.security.encryption;

import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.conf.HttpParameterEnc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.getEncryptionParameters;
import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.isEncryptionOn;

/**
 * EncryptionManager is in charge of keeping track of encryption configuration
 * such as on/off, defaults, etc.  It will also have a handle to the type of
 * encryption being employed based on the configuration.
 *
 * @author nmacaraeg
 *         Date Time: 2/8/12 11:13 AM
 */
public class EncryptionManager {
    private static final Logger logger = LogManager.getLogger(EncryptionManager.class);
    public static final String KEYPAIR_SESSION_KEY = "KEYPAIR_SESSION_KEY";

    @Autowired
    private KeystoreManager keystoreManager;
    private Encryption encryption;

    private enum EncryptionTypes {
        RSA
    }

    public void setKeystoreManager(KeystoreManager keystoreManager) {
        this.keystoreManager = keystoreManager;
    }

    @Autowired
    public void setEncryption(PlainCipher encryption) {
        this.encryption = (Encryption) encryption;
    }

    public boolean isEncryptionProcessOn()
    {
        return isEncryptionOn();
    }

    /**
     * Generates public and private keys. Public key gets stored into the session - public is sent over the wire
     * to the browser.
     *
     * @param isDynamicKeygenPerRequest - if true, generate the keys per every request.  If false, read keys from keystore
     * @throws java.io.IOException
     */
    public KeyPair generateKeys(boolean isDynamicKeygenPerRequest) throws IOException, KeyStoreException {
        final KeyPair keyPair;
         if (isDynamicKeygenPerRequest) {
             logger.debug("Generated keys on the fly successfully.");
             keyPair = this.encryption.generateKeyPair();
            //httpRequest.getSession().setAttribute(KEYPAIR_SESSION_KEY, keys);
        } else {
             keyPair = keystoreManager.getKeyPair(HttpParameterEnc.ID);
        }
        return keyPair;
    }

    /*
    * Decrypt an encrypted string which is tied to the session's KeyPair.
    * The encryption used the KeyPair's public key.
    * The decryption will use the KeyPair's private key.
    *
    * @param httpRequest a HttpServletRequest.
    * @param attribute the attribute key to set in the request.
    * @param encryptedValue an encrypted string value tied to the session's keypair.
    *
    * @return list of decrypted params.  If any of the params fail decryption, RunTimeException is thrown.
    */
    public List<String> decrypt(PrivateKey privateKey, String... encryptedValues) {
        List<String> retList = new ArrayList<String>(encryptedValues.length);
        try {
            if (privateKey != null) {
                for (String encryptedValue : encryptedValues) {
                    if (encryptedValue.trim().length() == 0) {
                        retList.add(encryptedValue);
                        continue;
                    }

                    String decryptedValue = encryption.decrypt(encryptedValue, privateKey);

                    if (decryptedValue != null && decryptedValue.length() > 0) {
                        try {
                            decryptedValue = URLDecoder.decode(decryptedValue, StandardCharsets.UTF_8.name());

                        } catch (UnsupportedEncodingException e) {
                            logger.error(StandardCharsets.UTF_8.name() + " encoding is not supported");
                        }
                    } else {
                        decryptedValue = encryptedValue;
                        logger.warn("Cannot decrypt encryptedValue (hidden for security reasons).");
                    }

                    retList.add(decryptedValue);
                }

                return retList;
            } else {
                throw new RuntimeException("privateKey is NULL when attempting to decrypt!");
            }
        } catch (Exception e) {
            logger.warn("failed to decrypt. assuming plain text ");
            retList.addAll(Arrays.asList(encryptedValues));
            return retList;
        }
    }

    /**
     * @return true only if the param is in the set of enc properties in SecurityConfiguration.
     */
    public static boolean isEncryptedParam(String param) {
        return getEncryptionParameters().contains(param);
    }

    /**
     * @return true only if the param is in the map.
     */
    public static boolean maybeEncryptedJSONParam(String param) {
        Set<String> encParamSet = getEncryptionParameters();
        for (String encParam : encParamSet) {
            if (encParam.startsWith(param))
                return true;
        }

        return false;
    }


    /*
    * Creates the JSONObject response which is expected by the encrypt method
    * in jcryption.js.  This is the public key.
    */
    protected JSONObject buildPublicKeyJSON(PublicKey pubKey) throws IOException {
        try {
            if (encryption instanceof EncryptionRSA) {
                String exponent  = EncryptionRSA.getPublicKeyExponent(pubKey);
                String modulus   = EncryptionRSA.getPublicKeyModulus(pubKey);
                String maxDigits = String.valueOf(encryption.getMaxDigits());

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("e", exponent);
                jsonObj.put("n", modulus);
                jsonObj.put("maxdigits", maxDigits);

                return jsonObj;
            }
            else
                throw new RuntimeException("Encryption algorithm " + encryption.getKeyAlgorithm() + " is not implemented.");
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to respond with a key pair.", e);
        }
    }

}
