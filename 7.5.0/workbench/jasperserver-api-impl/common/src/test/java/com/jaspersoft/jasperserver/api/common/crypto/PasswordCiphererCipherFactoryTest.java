package com.jaspersoft.jasperserver.api.common.crypto;

import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
public class PasswordCiphererCipherFactoryTest {
    final static String ksLocation = getenv("ks");
    final static String kspLocation = getenv("ksp");

    @Configuration
    @ComponentScan("com.jaspersoft.jasperserver.api.common.crypto")
    static class ContextConfiguration {

        public static final String PASSWORD_ENC_SECRET = "passwordEncSecret";

        @Bean(name = "passwordEncoder") // enables factory reference `&httpParameterCipher`
        public CipherFactory passwordEncoderFactory() throws Exception {
            CipherFactory factory = new CipherFactory();
            factory.setCipherClass(PasswordCipherer.class);
            factory.setConfId(PASSWORD_ENC_SECRET);
            return factory;
        }
        @Bean(name = "passwordEncoder")
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

    final static String SOME_JASPERADMIN = "4A5253495600000010A64C3395DE200BD9C8D8B491DCD28F4730C87D07D59D83A95FA975EE96693C28";
    final static String SOME_JOEUSER = "4A5253495600000010A64C3395DE200BD9C8D8B491DCD28F47A4B2114749F53F76C3E34A29714F4696";

    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(PasswordCiphererCipherFactoryTest.class.getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Test
    public void shouldBeInitialized() throws Exception {
        assertNotNull(cipher);
    }

    @Test
    public void shouldEncodeAndDecode() {
        final String text = "text";
        String encodedText = cipher.encode(text);
        assertEquals(text, cipher.decode(encodedText));
    }

}
