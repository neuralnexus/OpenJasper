/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.api.security.encryption;

import mondrian.tui.MockHttpServletRequest;
import mondrian.tui.MockHttpServletResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.security.KeyPair;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 3/8/12
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
//TODO more tests
public class EncryptionManagerTest {
    //fix test to find keystore.jks
    @Ignore
    @Test
    public void testGettingKeysFromKeystore() throws Exception {
        final Boolean isDynamicKeygenPerRequest = false;

        KeyPair keyPair = EncryptionManager.generateKeys(isDynamicKeygenPerRequest);
        Assert.assertNotNull("Unable to read keys from keystore jks file.  Jaspersoft should ship with a default file.", keyPair);
    }

    @Ignore
    @Test
    public void testDecrypt() throws Exception {

    }
}
