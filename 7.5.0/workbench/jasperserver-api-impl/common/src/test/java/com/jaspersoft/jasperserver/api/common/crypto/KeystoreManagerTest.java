package com.jaspersoft.jasperserver.api.common.crypto;

import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.conf.BuildEnc;
import com.jaspersoft.jasperserver.crypto.conf.ImportExportEnc;
import com.jaspersoft.jasperserver.crypto.conf.PasswordEncoderEnc;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static com.jaspersoft.jasperserver.crypto.EncryptionProperties.*;
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
//        keystoreManager.getEncryptionProperty("")
    }

    @Test
    public void keystoreShouldHaveBuildEncryptionProperties() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager.getEncryptionProperties(BuildEnc.ID));
    }


    @Test
    public void keystoreShouldHavePasswordEncoderProperties() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager.getEncryptionProperties(PasswordEncoderEnc.ID));
    }
    @Test
    public void keystoreShouldHaveImportExportProperties() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
        assertNotNull(keystoreManager.getEncryptionProperties(ImportExportEnc.ID));
    }

//    @Test
//    public void keystoreShouldHaveDiagnosticDataProperties() throws Exception {
//        final KeystoreManager keystoreManager = KeystoreManager.getInstance();
//        assertNotNull(keystoreManager.getDiagnosticDataKey());
//    }

    @Test
    public void keystoreShouldGenerateKeyUsingDefaults() throws Exception {
        final KeystoreManager keystoreManager = KeystoreManager.getInstance();

        EncryptionProperties props = keystoreManager.getEncryptionProperties(PasswordEncoderEnc.ID);
        assertNotNull(KeystoreManager.generateSecret(props.getKeyProperties().getKeyAlg(), props.getKeyProperties().getKeySize()));

        props = keystoreManager.getEncryptionProperties(ImportExportEnc.ID);
        assertNotNull(KeystoreManager.generateSecret(props.getKeyProperties().getKeyAlg(), props.getKeyProperties().getKeySize()));
    }
}
