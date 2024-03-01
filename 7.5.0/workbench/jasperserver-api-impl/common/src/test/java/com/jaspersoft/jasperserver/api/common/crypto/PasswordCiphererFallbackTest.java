package com.jaspersoft.jasperserver.api.common.crypto;

import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
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

import static java.lang.System.getenv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class PasswordCiphererFallbackTest {
    final static String ksLocation = getenv("ks");
    final static String kspLocation = getenv("ksp");

    @Configuration
    @ComponentScan("com.jaspersoft.jasperserver.api.common.crypto")
    static class ContextConfiguration {

        public static final String PASSWORD_ENC_SECRET = "passwordEncSecret";
        public static final String DEPRICATED_PASSWORD_ENC_SECRET = "deprecatedPasswordEncSecret";

        @Bean(name = "passwordEncoder")
        public CipherFactory passwordEncoderFactory() throws Exception {
            CipherFactory factory = new CipherFactory();
            factory.setCipherClass(PasswordCipherer.class);
            factory.setConfId(PASSWORD_ENC_SECRET);
            factory.setFallbackFactory(getPasswordEncoderFallbackFactory());
            return factory;
        }

        @Bean(name = "passwordEncoder_7_2")
        public CipherFactory getPasswordEncoderFallbackFactory() throws  Exception {
            CipherFactory factory = new CipherFactory();
            factory.setCipherClass(PasswordCipherer.class);
            factory.setConfId(DEPRICATED_PASSWORD_ENC_SECRET);
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

    }

    @Qualifier("passwordEncoder")
    @Autowired
    private PlainCipher cipher;

    final static String SUPER_USER_PWD = "41309C5003C52BA327F3D2FF2CDDA4AC";

    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(PasswordCiphererFallbackTest.class.getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Test
    public void shouldDecodeWithPasswordFallbackCipherer() {
        assertEquals("superuser", cipher.decode(SUPER_USER_PWD));
    }

}
