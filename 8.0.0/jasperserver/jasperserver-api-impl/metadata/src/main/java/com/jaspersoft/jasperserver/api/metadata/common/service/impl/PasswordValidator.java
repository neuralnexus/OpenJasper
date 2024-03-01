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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.crypto.Cipherer;
import com.jaspersoft.jasperserver.api.security.encryption.BaseCipher;
import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import java.security.Key;


/**
 * PasswordValidator is used to compare the password presented by a user during auth to a password stored in the database.
 * During instantiation, a Cipherer must be configured via Spring application context.
 * PasswordValidator uses the Cipherer to encrypt the raw password and compare it to the database one.
 */

public class PasswordValidator extends BaseCipher implements PasswordEncoder {

	private static final Logger log = LogManager.getLogger(PasswordValidator.class);

    private PlainCipher cipher;

	public PasswordValidator(EncryptionProperties properties, Key key, String keyUuid, PlainCipher fallbackCipherer) throws NoSuchMethodException {
		super(properties, key, keyUuid, fallbackCipherer);
		cipher = new Cipherer(properties, key, keyUuid, fallbackCipherer);
	}

	/**
	 * @see org.springframework.security.authentication.encoding.PasswordEncoder#encodePassword(java.lang.String, java.lang.Object)
	 * NOTE: salt will be ignored since we will use the "secretkey" defined in Spring configuration
	 */
	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		try {
			return encode(rawPass);
		} catch (Exception ex) {
			log.debug(ex);
			throw new PasswordEncryptionException(ex.getMessage(), ex.getCause());
		}
	}

	/**
	 * @see org.springframework.security.authentication.encoding.PasswordEncoder#isPasswordValid(java.lang.String, java.lang.String, java.lang.Object)
	 * NOTE: salt will be ignored since we will use the "secretkey" defined in Spring configuration
	 */
	public boolean isPasswordValid(String encPass, String otherPass, Object salt) throws DataAccessException {
		String rawPass = decode(encPass);
		return rawPass.equals(otherPass);
	}

	@Override
	public void setAllowEncryption(boolean flag) {

	}

	@Override
	public String encode(String content) {
		return cipher.encode(content);
	}

	@Override
	public String decode(String content) {
		return cipher.decode(content);
	}

	/**
	 * @return Returns the cipherTransformation.
	 */
	public String getCipherTransformation() {
		return cipher.getCipherTransformation();
	}

	@Override
	public int getBlockSize() {
		return cipher.getBlockSize();
	}

	@Override
	public String getKeyAlgorithm() {
		return cipher.getKeyAlgorithm();
	}

	@Override
	public int getKeySize() {
		return cipher.getKeySize();
	}

	@Override
	public String getKeyUuid() {
		return cipher.getKeyUuid();
	}

	@Override
	public Key getKey() {
		return cipher.getKey();
	}
}

