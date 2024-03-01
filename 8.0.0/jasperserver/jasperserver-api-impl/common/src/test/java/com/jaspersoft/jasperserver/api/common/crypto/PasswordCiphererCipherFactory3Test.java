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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;

import static com.jaspersoft.jasperserver.crypto.conf.Defaults.BuildEnc;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.ImportExportEnc;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.PasswordEncoderEnc;
import static com.jaspersoft.jasperserver.crypto.conf.Defaults.DeprecatedPasswordEncoderEnc;
import static java.lang.System.getenv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"default","engine","jrs"})
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class PasswordCiphererCipherFactory3Test {
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
            factory.setConfId(DeprecatedPasswordEncoderEnc.value().getConfId());
            factory.setSecretKey("0xC8 0x43 0x29 0x49 0xAE 0x25 0x2F 0xA1 0xC1 0xF2 0xC8 0xD9 0x31 0x01 0x2C 0x52 0x54 0x0B 0x5E 0xEA 0x9E 0x37 0xA8 0x61");
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


    final static String SOME_JASPERADMIN = "4A52534956000000088E12399C07726F5A349AFAADD5C5A2BD477309618DCD58B9";
    final static String SOME_JOEUSER = "4A52534956000000088E12399C07726F5A4DD8128D07A12649";

    final static String JASPERADMIN_72 = "349AFAADD5C5A2BD477309618DCD58B9";
    final static String JOEUSER_72 = "4DD8128D07A12649";


    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(PasswordCiphererCipherFactory3Test.class.getResource("/enc.properties"));
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
    public void shouldDecodePasswordWithUnknownIV() {
        assertEquals("jasperadmin", cipher.decode(SOME_JASPERADMIN));
        assertEquals("joeuser", cipher.decode(SOME_JOEUSER));
    }

    @Test
    public void shouldDecodePasswordWithOldSetConfig() {
        assertEquals("jasperadmin", cipher.decode(JASPERADMIN_72));
        assertEquals("joeuser", cipher.decode(JOEUSER_72));
    }
}
