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

package com.jaspersoft.jasperserver.test;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.JrsKeystore;
import com.jaspersoft.jasperserver.crypto.KeyProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class ImportEncryptionKeyTest extends BaseExportTestCaseTestNG {

    private static final Log m_logger = LogFactory.getLog(ImportEncryptionKeyTest.class);
    public ImportEncryptionKeyTest(){
        m_logger.info("ImportEncryptionKeyTest => constructor() called");
    }

    @BeforeClass()
    public void onSetUp() throws Exception {
        super.onSetUp();
    }

    @AfterClass()
    public void onTearDown() throws Exception {
        super.onTearDown();
    }
    @Test
    public void shouldGenerateAndImportNewKey()   {
        Parameters importParams = createParameters();
        importParams.addParameter(PARAM_INPUT_KEY);
        importParams.addParameter("genkey");
        importParams.addParameterValue("keyalias", "itSecret");
        importParams.addParameterValue("keypass", "password123");
        importParams.addParameterValue("keyalg", "AES");
        importParams.addParameterValue("keysize", "128");
        importParams.addParameter("visible");
        importParams.addParameterValue("keylabel", "IT Key");
        importParams.addParameterValue("keyorganisation", "org1");

        performImport(importParams);

        final JrsKeystore keystore = keystoreManager.getKeystore(null);
        assertTrue(keystore.containsAlias("itSecret"));

        final KeyProperties itSecret =
                keystore.getKeyProperties("itSecret");
        assertNotNull(itSecret);

        assertTrue(itSecret.isKeyVisible());
        assertEquals("IT Key", itSecret.getKeyLabel());
        assertEquals("org1", itSecret.getKeyOrganization());
    }

    @Test
    public void shouldGenerateAndImportNewRSAKey()   {
        Parameters importParams = createParameters();
        importParams.addParameter(PARAM_INPUT_KEY);
        importParams.addParameter("genkey");
        importParams.addParameterValue("keyalias", "itSecretRSA");
        importParams.addParameterValue("keypass", "password123");
        importParams.addParameterValue("keyalg", "RSA");
        importParams.addParameterValue("keysize", "1024");
        importParams.addParameterValue("visible", "false");
        importParams.addParameterValue("keylabel", "IT Key 2");
        importParams.addParameterValue("keyorganisation", "org1");

        performImport(importParams);

        final JrsKeystore keystore = keystoreManager.getKeystore(null);
        assertTrue(keystore.containsAlias("itSecretRSA"));

        final KeyProperties itSecret =
                keystore.getKeyProperties("itSecretRSA");
        assertNotNull(itSecret);

        assertFalse(itSecret.isKeyVisible());
        assertEquals("IT Key 2", itSecret.getKeyLabel());
        assertEquals("org1", itSecret.getKeyOrganization());
    }
}
