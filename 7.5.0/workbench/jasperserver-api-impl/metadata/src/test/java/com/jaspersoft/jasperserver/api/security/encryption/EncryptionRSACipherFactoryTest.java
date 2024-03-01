package com.jaspersoft.jasperserver.api.security.encryption;

import com.jaspersoft.jasperserver.api.common.crypto.CipherFactory;
import com.jaspersoft.jasperserver.api.common.crypto.KeystoreManagerFactory;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.conf.DeprecatedHttpParameterEnc;
import com.jaspersoft.jasperserver.crypto.conf.HttpParameterEnc;
import org.apache.commons.lang3.CharEncoding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

import javax.crypto.Cipher;
import java.io.File;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import static java.lang.System.getenv;
import static org.junit.Assert.*;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class EncryptionRSACipherFactoryTest {

    final static String ksLocation = getenv("ks");
    final static String kspLocation = getenv("ksp");

    @Configuration
    @ComponentScan("com.jaspersoft.jasperserver.api.common.crypto")
    static class ContextConfiguration {
        @Bean(name = "httpParameterCipher") // enables factory reference `&httpParameterCipher`
        public CipherFactory httpParameterCipherFactory() throws Exception {
            CipherFactory factory = new CipherFactory();
            factory.setCipherClass(EncryptionRSA.class);
            factory.setConfId(DeprecatedHttpParameterEnc.ID);
            return factory;
        }
        @Bean
        public PlainCipher httpParameterCipher() throws Exception {
            return httpParameterCipherFactory().getObject();
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

    @Qualifier("&httpParameterCipher")
    @Autowired
    private CipherFactory cipherFactory;

    @Qualifier("httpParameterCipher")
    @Autowired
    private PlainCipher cipher;

    @Qualifier("keystoreManager")
    @Autowired
    private KeystoreManager keystoreManager;

    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(EncryptionRSACipherFactoryTest.class.getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Test
    public void shouldBeInitialized() throws Exception {
        assertNotNull(cipher);
        assertTrue(cipher instanceof Encryption);
        assertEquals("RSA/NONE/NoPadding", cipher.getCipherTransformation());
        assertEquals(131, cipher.getBlockSize());
        assertEquals("RSA", cipher.getKeyAlgorithm());
        assertEquals(1024, cipher.getKeySize());

        final String publicKeyModulus = ((EncryptionRSA) cipher).getPublicKeyModulus();
        final String publicKeyExponent = ((EncryptionRSA) cipher).getPublicKeyExponent();

        final KeyPair keyPair = keystoreManager.getKeyPair(DeprecatedHttpParameterEnc.ID);

        assertEquals(EncryptionRSA.getPublicKeyExponent(keyPair.getPublic()), publicKeyExponent);
        assertEquals(EncryptionRSA.getPublicKeyModulus(keyPair.getPublic()), publicKeyModulus);
    }

    @Test
    public void shouldDecryptWithSpecifiedKey() throws Exception {
        assertNotNull(cipher);

        final KeyPair kp = cipher.generateKeyPair();

        final String publicKeyModulus = EncryptionRSA.getPublicKeyModulus(kp.getPublic());
        final String publicKeyExponent = EncryptionRSA.getPublicKeyExponent(kp.getPublic());

        final PublicKey clientKey =
                getPublicKey(publicKeyModulus, publicKeyExponent);
        final String encryptedText = encrypt(clientKey, "superuser");

        final String decryptedText = ((Encryption) cipher).decrypt(encryptedText, kp.getPrivate());
        assertEquals("superuser", decryptedText);
    }

    @Test
    public void shouldDecrypt() throws Exception {
        assertNotNull(cipher);

        final String publicKeyModulus = ((EncryptionRSA) cipher).getPublicKeyModulus();
        final String publicKeyExponent = ((EncryptionRSA) cipher).getPublicKeyExponent();

        final PublicKey clientKey =
                getPublicKey(publicKeyModulus, publicKeyExponent);
        final String encryptedText = encrypt(clientKey, "superuser");

        final String decryptedText = cipher.decode(encryptedText);
        assertEquals("superuser", decryptedText);
    }

    @Test
    public void shouldGenerateKeyPair() throws Exception {
        assertNotNull(cipher);
        final KeyPair kp = cipher.generateKeyPair();
        assertNotNull(kp);
        assertEquals("RSA", kp.getPrivate().getAlgorithm());
        assertEquals("10001", EncryptionRSA.getPublicKeyExponent(kp.getPublic()));

        final String publicKeyModulus = EncryptionRSA.getPublicKeyModulus(kp.getPublic());
        assertEquals(256, publicKeyModulus.length());
//        cipher.decode();
    }


    private static String byteArrayToHexString(byte[] byteArr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteArr.length; i++) {
            byte b = byteArr[i];
            int high = (b & 0xF0) >> 4;
            int low = b & 0x0F;
            sb.append(Character.forDigit(high, 16));
            sb.append(Character.forDigit(low, 16));
        }
        return sb.toString();
    }

    private static String encrypt(PublicKey publicKey, String text) throws Exception {
        byte[] encryptedUtfPass;
        Cipher enc = Cipher.getInstance("RSA/NONE/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
        enc.init(Cipher.ENCRYPT_MODE, publicKey);
        String utfPass = URLEncoder.encode(text, CharEncoding.UTF_8);
        encryptedUtfPass = enc.doFinal(utfPass.getBytes());

        return byteArrayToHexString(encryptedUtfPass);
    }

    /**
     * Recovering public key from
     * @param n modulus
     * @param e exponent
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(String n, String e) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        int radix = 16;
        BigInteger modulus = new BigInteger(n, radix);
        BigInteger publicExponent = new BigInteger(e, radix);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
        return keyFactory.generatePublic(publicKeySpec);
    }
}
