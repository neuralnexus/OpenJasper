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
