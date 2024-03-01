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

package com.jaspersoft.jasperserver.export.modules.common;

import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;

import java.util.Objects;

public interface Encryptable {

    String ENCRYPTION_PREFIX = "ENC<";
    String ENCRYPTION_SUFFIX = ">";

    String IMPORT_EXPORT_CIPHER = "importExportCipher";

    default PlainCipher cipher() {
        return CipherLookup.INSTANCE.get();
    }

    default boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENCRYPTION_PREFIX) && value.endsWith(ENCRYPTION_SUFFIX);
    }

    default String encrypt(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        return ENCRYPTION_PREFIX + cipher().encode(rawValue) + ENCRYPTION_SUFFIX;
    }

    default String decrypt(String encryptedValue) {
        Objects.requireNonNull(encryptedValue);
        return cipher().decode(
                encryptedValue
                        .replaceFirst(ENCRYPTION_PREFIX, "")
                        .replaceAll(ENCRYPTION_SUFFIX + "$", ""));
    }
}
