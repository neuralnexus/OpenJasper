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

import java.security.KeyPair;
import java.security.PrivateKey;

/**
 * The latest version of jcryption.js uses AES encryption, but JCryption for Java
 * has not kept up with the newer encryption techniques.  This is a placeholder in
 * case Twofish encryption is used.
 * <p/>
 * DO NOT USE at this time!
 *
 * @author norm
 * @since 2/9/2012
 */
public class EncryptionTwofish implements Encryption {
    /**
     * {@inheritDoc}
     */
    public KeyPair generateKeypair(int keyLength) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public String decrypt(String encrypted, PrivateKey privateKey) {
        throw new RuntimeException("Not implemented");
    }

}
