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
