package com.jaspersoft.jasperserver.export.modules.common;

import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;

import java.util.function.Supplier;

import static com.jaspersoft.jasperserver.api.common.crypto.Hexer.parse;
import static com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext.getApplicationContext;
import static com.jaspersoft.jasperserver.export.modules.common.Encryptable.IMPORT_EXPORT_CIPHER;
import static java.lang.String.format;

public enum CipherLookup implements Supplier<PlainCipher> {
    INSTANCE;

    private final ThreadLocal<PlainCipher> threadCipher = new ThreadLocal<>();

    public void set(PlainCipher cipher) {
        threadCipher.set(cipher);
    }

    @Override
    public PlainCipher get() {
        final PlainCipher plainCipher = threadCipher.get();

        if (plainCipher != null) {
            return plainCipher;

        } else { // fallback to the configured by default
            return (PlainCipher) getApplicationContext().getBean(IMPORT_EXPORT_CIPHER);
        }

    }
}
