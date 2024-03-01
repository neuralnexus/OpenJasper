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

import static com.jaspersoft.jasperserver.crypto.conf.Defaults.PasswordEncoderEnc;
import static java.lang.System.getenv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"default","engine","jrs"})
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class PasswordCiphererCipherFactorySpecificKeyTest {
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
            factory.setConfId(PasswordEncoderEnc.value().getConfId());
            factory.setSecretKey("0x5d 0x03 0x23 0x9f 0xb3 0x7a 0x9b 0x3f 0x8d 0x00 0xb1 0xb9 0x19 0x36 0xe0 0xea");
//            factory.setSecretKey("0xC8 0x43 0x29 0x49 0xAE 0x25 0x2F 0xA1 0xC1 0xF2 0xC8 0xD9 0x31 0x01 0x2C 0x52 0x54 0x0B 0x5E 0xEA 0x9E 0x37 0xA8 0x61");
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

//    final static String SOME_JASPERADMIN = "4A5253495600000010E0AD17E54ACA8CB48D2EC17376B1133E57C891CAA224491230A83E001E044391";
//    final static String SOME_JOEUSER = "4A5253495600000010E0AD17E54ACA8CB48D2EC17376B1133E4FB521A2ED03916DAE26689822FAC172";
//
    //    final static String SOME_JASPERADMIN = "4A525349560000001027FA8A304AD038D92A73795558E18103FACAAD080B850E2F29F7E47E3B2626C6";
//    final static String SOME_JOEUSER = "4A525349560000001027FA8A304AD038D92A73795558E18103833D2D3175C070296751054DD00446F7";
//
    final static String JASPERADMIN_72 = "349AFAADD5C5A2BD477309618DCD58B9";
    final static String JOEUSER_72 = "4DD8128D07A12649";


    @BeforeClass
    public static void setUp() throws Exception {
        final File file = getFile(PasswordCiphererCipherFactorySpecificKeyTest.class.getResource("/enc.properties"));
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

}
