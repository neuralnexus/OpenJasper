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

package com.jaspersoft.jasperserver.export.io;

import com.jaspersoft.jasperserver.api.common.crypto.Hexer;
import com.jaspersoft.jasperserver.export.ParametersImpl;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static com.jaspersoft.jasperserver.export.util.EncryptionParams.*;
import static org.junit.Assert.*;

public class SecretKeyInputTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldGetEmptyStreamIfGenkey() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(GEN_KEY, null);

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals( "", IOUtils.toString(stream));
    }

    @Test
    public void shouldGetByteStreamIfSecretKeyProvided() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(GEN_KEY, null);

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[]{"0x5d", "0x03", "0x23", "0x9f", "0xb3", "0x7a", "0x9b", "0x3f", "0x8d", "0x00", "0xb1", "0xb9", "0x19", "0x36", "0xe0", "0xea"}, new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x5d 0x03 0x23 0x9f 0xb3 0x7a 0x9b 0x3f 0x8d 0x00 0xb1 0xb9 0x19 0x36 0xe0 0xea", Hexer.stringify(IOUtils.toByteArray(stream)));
    }

    @Test
    public void shouldGetByteStreamFromKeystore() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore3");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "superuser");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0xa6 0xd1 0x1d 0x9b 0x88 0x97 0x8b 0x9d 0x11 0x3a 0x96 0x81 0xf7 0x00 0x29 0x0c", Hexer.stringify(IOUtils.toByteArray(stream)));
    }

    @Test
    public void shouldGetByteStreamFromKeystoreByAlias() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore3");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "superuser");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "e05f06fd-ba89-4930-87e5-73d12b82456b");
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "superuser");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x92 0x0a 0xb8 0xfe 0xaa 0xa8 0xe2 0x68 0x6e 0x74 0xf2 0x2c 0x2a 0xde 0x38 0xbd", Hexer.stringify(IOUtils.toByteArray(stream)));
    }

    @Test
    public void shouldGetByteStreamFromKeystoreByAliasAndPass() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore3");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "superuser");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "e05f06fd-ba89-4930-87e5-73d12b82456b");
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "superuser");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x92 0x0a 0xb8 0xfe 0xaa 0xa8 0xe2 0x68 0x6e 0x74 0xf2 0x2c 0x2a 0xde 0x38 0xbd", Hexer.stringify(IOUtils.toByteArray(stream)));
    }
    @Test
    public void shouldGetByteStreamFromKeystoreByAliasAndPassAndAlg() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore3");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "superuser");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "e05f06fd-ba89-4930-87e5-73d12b82456b");
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "superuser");
        parameters.setParameterValue(KEY_ALGORITHM_PARAMETER, "AES");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x92 0x0a 0xb8 0xfe 0xaa 0xa8 0xe2 0x68 0x6e 0x74 0xf2 0x2c 0x2a 0xde 0x38 0xbd", Hexer.stringify(IOUtils.toByteArray(stream)));
    }

    @Test
    public void shouldGetByteStreamFromKeystoreWithPass() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore4");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "testpass");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x46 0xf5 0x0e 0xa3 0x02 0xd7 0x89 0x0d 0x03 0xf3 0x94 0x23 0x04 0x0b 0x64 0x00",
                Hexer.stringify(IOUtils.toByteArray(stream)));
    }

    @Test
    public void shouldGetByteStreamFromKeystoreWithPassByAlias() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore4");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "testpass");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "e05f06fd-ba89-4930-87e5-73d12b82456b");
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "testpass");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x92 0x0a 0xb8 0xfe 0xaa 0xa8 0xe2 0x68 0x6e 0x74 0xf2 0x2c 0x2a 0xde 0x38 0xbd", Hexer.stringify(IOUtils.toByteArray(stream)));
    }

    @Test
    public void shouldGetByteStreamFromKeystoreWithPassByAliasAndPass() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore4");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "testpass");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "4d6a9d84-f562-41ef-a506-0238562962fa");
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "testpass");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x5b 0x1c 0xc2 0x2f 0x11 0x4a 0x83 0x8c 0xec 0x35 0x34 0xc9 0x1e 0x36 0xb7 0x35",
                Hexer.stringify(IOUtils.toByteArray(stream)));
    }
    @Test
    public void shouldGetByteStreamFromKeystoreWithPassByAliasAndPassAndAlg() throws Exception {
        final ParametersImpl parameters = new ParametersImpl();
        parameters.setParameterValue(KEY_STORE_PARAMETER, "target/test-classes/mystore4");
        parameters.setParameterValue(KEY_STORE_PASSWD_PARAMETER, "testpass");
        parameters.setParameterValue(KEY_ALIAS_PARAMETER, "e05f06fd-ba89-4930-87e5-73d12b82456b");
        parameters.setParameterValue(KEY_PASSWD_PARAMETER, "testpass");
        parameters.setParameterValue(KEY_ALGORITHM_PARAMETER, "AES");

        final SecretKeyInput secretKeyInput =
                new SecretKeyInput(new String[0], new EncryptionParams(parameters));

        final ByteArrayInputStream stream = secretKeyInput.getFileInputStream("");

        assertNotNull(stream);
        assertEquals("0x92 0x0a 0xb8 0xfe 0xaa 0xa8 0xe2 0x68 0x6e 0x74 0xf2 0x2c 0x2a 0xde 0x38 0xbd", Hexer.stringify(IOUtils.toByteArray(stream)));
    }
}