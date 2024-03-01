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

package com.jaspersoft.jasperserver.api.security.encryption;

import com.jaspersoft.jasperserver.api.common.crypto.CipherFactory;
import com.jaspersoft.jasperserver.api.common.crypto.KeystoreManagerFactory;
import com.jaspersoft.jasperserver.core.util.StringUtil;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.DeprecatedHttpParameterEnc;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.crypto.Cipher;
import java.io.File;
import java.security.KeyPair;
import java.util.List;

import static java.lang.System.getenv;
import static org.springframework.util.ResourceUtils.getFile;

/**
 * User: dlitvak
 * Date: 3/8/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"default","engine","jrs"})
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class EncryptionManagerTest {

    final static String ksLocation = getenv("ks");
    final static String kspLocation = getenv("ksp");

    @Configuration
    @ComponentScan("com.jaspersoft.jasperserver.api.common.crypto")
    static class ContextConfiguration {
        @Bean//(name = "httpParameterCipher") // enables factory reference `&httpParameterCipher`
        public CipherFactory httpParameterCipherFactory() throws Exception {
            CipherFactory factory = new CipherFactory();
            factory.setCipherClass(EncryptionRSA.class);
            factory.setConfId(DeprecatedHttpParameterEnc.value().getConfId());
            return factory;
        }
//        @Bean
//        public PlainCipher httpParameterCipher() throws Exception {
//            return httpParameterCipherFactory().getObject();
//        }
        @Bean//(name = "keystoreManager")
        public KeystoreManagerFactory keystoreManagerFactory() throws Exception {
            return new KeystoreManagerFactory();
        }
        @Bean
        public KeystoreManager keystoreManager() throws Exception {
            return keystoreManagerFactory().getObject();
        }
        @Bean
        public EncryptionManager encryptionManager() throws Exception {
            final EncryptionManager manager = new EncryptionManager();
            manager.setKeystoreManager(keystoreManager());
            manager.setEncryption(httpParameterCipherFactory().getObject());
            return manager;
        }
    }

    @Autowired
//    @Qualifier("httpParameterCipher")
    private PlainCipher cipher;

//    private EncryptionRSA encryption = new EncryptionRSA();

    @Autowired
    private EncryptionManager encMgr;

    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(EncryptionRSACipherFactoryTest.class.getResource("/enc.properties"));
        KeystoreManager.init(ksLocation, kspLocation, file);
    }

    @Test
    public void testBouncyCastleProviderIsRegistered() throws Exception {
        KeyPair keyPair = encMgr.generateKeys(true);
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        String testStr = "test";
        byte[] encArr = cipher.doFinal(testStr.getBytes());
        String encTestStr = StringUtil.byteArrayToHexString(encArr);

        List<String> resStrList = encMgr.decrypt(keyPair.getPrivate(), encTestStr);
        Assert.assertTrue("", resStrList.size() == 1 && resStrList.get(0).equals(testStr));
    }
    @Test
    public void testDecryptDynamicKey() throws Exception {
        KeyPair keyPair = encMgr.generateKeys(true);
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        String testStr = "test";
        byte[] encArr = cipher.doFinal(testStr.getBytes());
        String encTestStr = StringUtil.byteArrayToHexString(encArr);

        List<String> resStrList = encMgr.decrypt(keyPair.getPrivate(), encTestStr);
        Assert.assertTrue("", resStrList.size() == 1 && resStrList.get(0).equals(testStr));
    }

    @Test
    public void testDecryptStaticKey() throws Exception {
        KeyPair keyPair = encMgr.generateKeys(false);
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        String testStr = "test2";
        byte[] encArr = cipher.doFinal(testStr.getBytes());
        String encTestStr = StringUtil.byteArrayToHexString(encArr);

        List<String> resStrList = encMgr.decrypt(keyPair.getPrivate(), encTestStr);
        Assert.assertTrue("", resStrList.size() == 1 && resStrList.get(0).equals(testStr));
    }
}
