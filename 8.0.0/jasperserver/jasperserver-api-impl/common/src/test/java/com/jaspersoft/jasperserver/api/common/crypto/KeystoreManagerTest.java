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

package com.jaspersoft.jasperserver.api.common.crypto;

import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.JrsKeystore;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static com.jaspersoft.jasperserver.crypto.EncryptionProperties.*;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.BuildEnc;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.ImportExportEnc;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.PasswordEncoderEnc;
import static java.lang.System.getenv;
import static java.nio.file.Files.exists;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.ResourceUtils.getFile;

public class KeystoreManagerTest {
    final String ksLocation = getenv("ks");
    final String kspLocation = getenv("ksp");

    @Before
    public void setUp() throws Exception {
        final File file = getFile(this.getClass().getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Test
    public void keystoreShouldBeInitialized() throws Exception {
        assertTrue(exists(Paths.get(ksLocation, ".jrsks")));
        assertTrue(exists(Paths.get(kspLocation, ".jrsksp")));
    }

    @Test
    public void keystoreManagerShouldBeInitialized() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager);
        assertNotNull(keystoreManager.getKeystore(null));
//        keystoreManager.getEncryptionProperty("")
    }

    @Test
    public void keystoreShouldHaveBuildEncryptionProperties() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager.getKeystore(null).getEncryptionProperties(BuildEnc.getConfId()));
    }


    @Test
    public void keystoreShouldHavePasswordEncoderProperties() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager.getKeystore(null).getEncryptionProperties(PasswordEncoderEnc.getConfId()));
    }
    @Test
    public void keystoreShouldHaveImportExportProperties() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager.getKeystore(null).getEncryptionProperties(ImportExportEnc.getConfId()));
    }

//    @Test
//    public void keystoreShouldHaveDiagnosticDataProperties() throws Exception {
//        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
//        assertNotNull(keystoreManager.getDiagnosticDataKey());
//    }

    @Test
    public void keystoreShouldGenerateKeyUsingDefaults() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();

        final JrsKeystore keystore = keystoreManager.getKeystore(null);
        EncryptionProperties props = keystore.getEncryptionProperties(PasswordEncoderEnc.getConfId());
        assertNotNull(JrsKeystore.generateSecret(props.getKeyProperties().getKeyAlg(), props.getKeyProperties().getKeySize()));

        props = keystore.getEncryptionProperties(ImportExportEnc.getConfId());
        assertNotNull(JrsKeystore.generateSecret(props.getKeyProperties().getKeyAlg(), props.getKeyProperties().getKeySize()));
    }
}
