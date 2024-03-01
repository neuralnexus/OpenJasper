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
package com.jaspersoft.jasperserver.api.security.encryption;

import com.jaspersoft.jasperserver.crypto.JrsKeystore;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyPair;

public interface PlainCipher {
    void setAllowEncryption(boolean flag);

    String encode(String content);
    String decode(String content);

    String getCipherTransformation();
    int getBlockSize();
    String getKeyAlgorithm();
    int getKeySize();
    String getKeyUuid();
    Key getKey();

    default SecretKey generateSecret(String keyAlg, int keySize) {
        return JrsKeystore.generateSecret(keyAlg, keySize);
    }

    default SecretKey generateSecret(String keyAlg) {
        if (keyAlg == null || keyAlg.isEmpty()) {
            return generateSecret(getKeyAlgorithm(), getKeySize());
        } else {
            return generateSecret(keyAlg, getKeySize());
        }

    }

    default SecretKey generateSecret(final int keySize) {
        if (keySize > 0) {
            return generateSecret(getKeyAlgorithm(), keySize);
        } else {
            return generateSecret(getKeyAlgorithm(), getKeySize());
        }
    }

    default SecretKey generateSecret() {
        return JrsKeystore.generateSecret(getKeyAlgorithm(), getKeySize());
    }

    default KeyPair generateKeyPair(String keyAlg, int keySize) {
        return JrsKeystore.generateKeyPair(keyAlg, keySize);
    }

    default KeyPair generateKeyPair() {
        return generateKeyPair(getKeyAlgorithm(), getKeySize());
    }
}
