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

import javax.crypto.spec.SecretKeySpec;
import java.io.File;

import static com.jaspersoft.jasperserver.crypto.conf.PasswordEncoderEnc.*;
import static java.lang.System.getenv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class PasswordCiphererCipherFactory4Test {
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

            factory.setTransformation((String) ENC_TRANSFORMATION.value());
            factory.setBlockSize((Integer) ENC_BLOCK_SIZE.value());
            factory.setKeyAlgorithm((String) KEYALG.value());
            factory.setKeySize((Integer) KEYSIZE.value());
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

    @Qualifier("&passwordEncoder")
    @Autowired
    private CipherFactory cipherFactory;


    final static String SOME_JASPERADMIN = "4A52534956000000108A4019B005077091A69DC3050560807E200FE5DCB7DBD063308FF186F8EFD638";
    final static String SOME_JOEUSER = "4A52534956000000108A4019B005077091A69DC3050560807ED8F3FDE813EC7D30B828F78DB2401818";

    final static String KEY = "0x06 0x67 0x2b 0x6c 0x24 0x88 0x84 0x87 0x7b 0x36 0x93 0x0b 0x8c 0x08 0x32 0x92";

    final static String JASPERADMIN_72 = "349AFAADD5C5A2BD477309618DCD58B9";
    final static String JOEUSER_72 = "4DD8128D07A12649";


    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(PasswordCiphererCipherFactory4Test.class.getResource("/enc.properties"));
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

    @Test
    public void shouldDecodePasswordWithUnknownIV() throws Exception {
        final CipherFactory factory = cipherFactory.fork(new SecretKeySpec(Hexer.parse(KEY), (String) KEYALG.value()));
        final PlainCipher cipher = factory.getObject();

        assertEquals("jasperadmin", cipher.decode(SOME_JASPERADMIN));
        assertEquals("joeuser", cipher.decode(SOME_JOEUSER));
    }
}
