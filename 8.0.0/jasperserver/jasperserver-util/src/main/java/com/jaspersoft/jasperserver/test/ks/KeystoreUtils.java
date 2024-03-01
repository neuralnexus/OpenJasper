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

package com.jaspersoft.jasperserver.test.ks;

import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.System.getenv;

public class KeystoreUtils {

    public static void createIfNotExists(Class<?> clazz) throws FileNotFoundException {
        if (!Files.exists(Paths.get(getenv("ksp"), KeystoreManager.KS_PROP_NAME))) {
            final File file = ResourceUtils.getFile(clazz.getResource("/enc.properties"));
            KeystoreManager.init(getenv("ks"), getenv("ksp"), file);
        }
    }
}
