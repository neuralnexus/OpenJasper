/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

import java.security.Key;

/**
 * Javadoc and signatures originally from the JCryption source, with some minor modifications.
 * Uses RSA for now.
 *
 * @author norm macaraeg
 * @see "https://github.com/novoj/jCryption4J" and "jcryption.org"
 */
public interface Encryption extends PlainCipher
{
    /**
     * Encrypts a given string with the RSA keys
     *
     * @param text full text.
     * @param privateKey RSA private key.
     * @return full encrypted text.
     * @throws RuntimeException if the RSA algorithm not supported or decrypt operation failed.
     */
    String encrypt(String text, Key privateKey);


    /**
     * Decrypts a given string with the RSA keys
     *
     * @param encrypted full encrypted text.
     * @param privateKey RSA private key.
     * @return decrypted text.
     * @throws RuntimeException if the RSA algorithm not supported or decrypt operation failed.
     */
    String decrypt(String encrypted, Key privateKey);


    /**
     * Max block size with given key length
     *
     * @return number of digits
     */
    int getMaxDigits();
}
