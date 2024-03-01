package com.jaspersoft.jasperserver.api.common.crypto;

import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.conf.DeprecatedPasswordEncoderEnc;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;

import static com.jaspersoft.jasperserver.crypto.conf.PasswordEncoderEnc.*;
import static java.lang.System.getenv;
import static org.junit.Assert.*;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class DepricatedPasswordCiphererCipherFactoryTest {
    final static String ksLocation = getenv("ks");
    final static String kspLocation = getenv("ksp");

    final static String SOME_JASPERADMIN = "4A52534956000000088E12399C07726F5A349AFAADD5C5A2BD477309618DCD58B9";
    final static String SOME_JOEUSER = "4A52534956000000088E12399C07726F5A4DD8128D07A12649";

    final static String JASPERADMIN_72 = "349AFAADD5C5A2BD477309618DCD58B9";
    final static String JOEUSER_72 = "4DD8128D07A12649";

    @Configuration
    @ComponentScan("com.jaspersoft.jasperserver.api.common.crypto")
    static class ContextConfiguration {

        @Bean(name = "passwordEncoder") // enables factory reference `&httpParameterCipher`
        public CipherFactory passwordEncoderFactory() throws Exception {
            CipherFactory factory = new CipherFactory();
            factory.setConfId(DeprecatedPasswordEncoderEnc.ID);
            factory.setCipherClass(PasswordCipherer.class);
            return factory;
        }

        @Bean
        public PlainCipher passwordEncoder() throws Exception {
            return passwordEncoderFactory().getObject();
        }

        @Bean(name = "keystoreManager")
        public KeystoreManagerFactory keystoreManagerFactory() throws Exception {
            return new KeystoreManagerFactory();
        }
        @Bean
        public KeystoreManager keystoreManager() throws Exception {
            return keystoreManagerFactory().getObject();
        }
    }

    @Qualifier("passwordEncoder")
    @Autowired
    private PlainCipher cipher;

    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(DepricatedPasswordCiphererCipherFactoryTest.class.getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Test
    public void shouldBeInitialized() throws Exception {
        assertNotNull(cipher);
    }

    @Test
    public void shouldEncodeAndDecode() {
        final String text = "jasperadmin";
        String encodedText = cipher.encode(text);
        assertEquals(text, cipher.decode(encodedText));
    }

    @Test
    public void shouldDecodePasswordWithUnknownIV() {
        assertEquals("jasperadmin", cipher.decode(SOME_JASPERADMIN));
        assertEquals("joeuser", cipher.decode(SOME_JOEUSER));
    }

    @Test
    public void shouldDecode72Password() {
        assertEquals("jasperadmin", cipher.decode(JASPERADMIN_72));
        assertEquals("joeuser", cipher.decode(JOEUSER_72));
    }
}
