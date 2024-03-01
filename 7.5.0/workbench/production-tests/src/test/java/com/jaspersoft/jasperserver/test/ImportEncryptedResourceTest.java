package com.jaspersoft.jasperserver.test;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.conf.DeprecatedImportExportEnc;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.junit.Before;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import java.util.UUID;

import java.io.File;

public class ImportEncryptedResourceTest extends BaseServiceSetupTestNG {

    private static final Log m_logger = LogFactory.getLog(ImportEncryptedResourceTest.class);
    private String ZIPFILEPATH = null;
    //SECRECT_KEY by which the zip file is encrypted
    private static final String SECRET_KEY = "0x0a 0xd0 0x7d 0xec 0x3d 0xc1 0xa9 0xd7 0xa9 0x7c 0x48 0xea 0xd0 0x5a 0x78 0xbc";
    //WRONG SECRECT_KEY by which the zip file is encrypted
    private static final String WRONG_SECRET_KEY = "0x0a 0xd0 0x7d 0xec 0x3d 0xc1 0xa9 0xd7 0xa9 0x7c 0x48 0xea 0xd0 0x5a 0x78 0x5a";

    public ImportEncryptedResourceTest(){
        m_logger.info("ImportEncryptedResourceTest => constructor() called");
    }

    @BeforeMethod
    public void getFile() {
        ZIPFILEPATH = TEST_BASE_DIR + FILE_SEPARATOR + "test-classes" + FILE_SEPARATOR + "encryption-import" +
            FILE_SEPARATOR + "encryption-resource.zip";
    }

    @Test
    public void decodeEncryptedValueShouldFailWithoutParameters()   {
        Parameters importParams = createParameters();
        importParams.addParameterValue(PARAM_IMPORT_ZIP, ZIPFILEPATH);
        try {
            performImport(importParams);
        }
        catch(Exception e)  {
            assertNotNull(e);
            assertEquals(e.getMessage(),"Import failed as resources cannot be decoded");
        }

    }

    @Test
    public void decodeEncryptedValueWithCorrectSecretKey()   {
        KeystoreManager instance = KeystoreManager.getInstance();
        Parameters importParams = createParameters();
        importParams.addParameterValue(PARAM_IMPORT_ZIP, ZIPFILEPATH);
        importParams.addParameterValue("secret-key", SECRET_KEY);
        performImport(importParams);
    }

    @Test
    public void decodeEncryptedValueShouldFailWithWrongSecretKey()   {
        KeystoreManager instance = KeystoreManager.getInstance();
        Parameters importParams = createParameters();
        importParams.addParameterValue(PARAM_IMPORT_ZIP, ZIPFILEPATH);
        importParams.addParameterValue("secret-key", WRONG_SECRET_KEY);
        try {
            performImport(importParams);
        }
        catch(Exception e)  {
            assertNotNull(e);
            assertEquals(e.getMessage(),"Import failed as resources cannot be decoded");
        }
    }
}
