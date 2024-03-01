package com.jaspersoft.jasperserver.export.io;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.crypto.Hexer;
import com.jaspersoft.jasperserver.api.common.crypto.KeystoreManagerFactory;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.export.ParametersImpl;
import org.apache.commons.io.IOUtils;
import org.junit.*;


import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.jaspersoft.jasperserver.export.util.EncryptionParams.*;
import static java.lang.System.getenv;
import static org.junit.Assert.*;
import static org.springframework.util.ResourceUtils.getFile;

public class SecretKeyInputFactoryTest {
    final static String ksLocation = getenv("ks");
    final static String kspLocation = getenv("ksp");
    public static final String INPUT_KEY = "input-key";

    private Console console;

    private KeystoreManagerFactory kmFactory;
    private SecretKeyInputFactory factory;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        final File file = getFile(SecretKeyInputFactoryTest.class.getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Before
    public void setUp() throws Exception {
        console = System.console();

        kmFactory = new KeystoreManagerFactory();
        kmFactory.init();

        factory = new SecretKeyInputFactory(console);
        factory.setImporterBeanName("secretKeyImporter");
        factory.setInputKeyParameter("input-key");
        factory.setKeystoreManager(kmFactory.getObject());
    }

    @AfterClass
    public static void cleanUp() throws IOException {
        Files.delete(Paths.get(ksLocation, ".jrsks"));
        Files.delete(Paths.get(ksLocation, ".jrsksp"));
    }

    @Test(expected = JSException.class)
    public void shouldFailToCreateInputWithoutParams() {
        final ParametersImpl parameters = new ParametersImpl();

        final ImportInput input = factory.createInput(parameters);

        assertNotNull(input);
    }

    @Test(expected = JSException.class)
    public void shouldFailToCreateInputWithoutKeyAlias() {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(INPUT_KEY, "");

        final ImportInput input = factory.createInput(parameters);

        assertNotNull(input);
    }

    @Test
    public void shouldCreateInputFromKey() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        final String key = "0x46 0xf5 0x0e 0xa3 0x02 0xd7 0x89 0x0d 0x03 0xf3 0x94 0x23 0x04 0x0b 0x64 0x00";
        Arrays.stream(key.split(" "))
                .forEach(b -> parameters.addParameterValue("input-key", b));
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "importExportEncSecret");

        final ImportInput input = factory.createInput(parameters);

        assertNotNull(input);
        assertEquals(key, Hexer.stringify(IOUtils.toByteArray(input.getFileInputStream(""))));
    }

    @Test(expected = Exception.class)
    public void shouldCreateInputFromKeyButWillFailBecauseOfExpectedConsoleInput() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        final String key = "0x46 0xf5 0x0e 0xa3 0x02 0xd7 0x89 0x0d 0x03 0xf3 0x94 0x23 0x04 0x0b 0x64 0x00";
        Arrays.stream(key.split(" "))
                .forEach(b -> parameters.addParameterValue("input-key", b));
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "test-key");

        final ImportInput input = factory.createInput(parameters);

        assertNotNull(input);
        assertEquals(key, Hexer.stringify(IOUtils.toByteArray(input.getFileInputStream(""))));
    }

    @Test
    @Ignore
    public void shouldCreateInputFromKeystore() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        final String key = "0x5b 0x1c 0xc2 0x2f 0x11 0x4a 0x83 0x8c 0xec 0x35 0x34 0xc9 0x1e 0x36 0xb7 0x35";

//        parameters.setParameterValue(INPUT_KEY, "");
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore4");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "superuser");
//        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "4d6a9d84-f562-41ef-a506-0238562962fa");
//        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "testpass");

        final ImportInput input = factory.createInput(parameters);

        assertNotNull(input);
        assertEquals(key, Hexer.stringify(IOUtils.toByteArray(input.getFileInputStream(""))));

    }

    @Test(expected = JSException.class)
    public void shouldFailCreateInputFromKeystoreWithoutPass() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        final String key = "0x5b 0x1c 0xc2 0x2f 0x11 0x4a 0x83 0x8c 0xec 0x35 0x34 0xc9 0x1e 0x36 0xb7 0x35";

        parameters.setParameterValue(INPUT_KEY, "");
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore4");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "test-key");

        final ImportInput input = factory.createInput(parameters);

        assertNotNull(input);
        assertEquals(key, Hexer.stringify(IOUtils.toByteArray(input.getFileInputStream(""))));

    }
}