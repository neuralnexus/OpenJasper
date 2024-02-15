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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.providers.encoding.PasswordEncoder;



/**
 * This class is used to encrypt/decrypt passwords both in acegi domain and also throughout the applicaton.
 */
// TODO: class needs revision.  Double-check is anti-pattern: it doesn't work
// TODO This class can be discarded, given that StaticApplicationContext has access to Spring config
public class PasswordCipherer implements PasswordEncoder {
	
	private static Log log = LogFactory.getLog(PasswordCipherer.class);
	
    //singleton self
    private static PasswordCipherer instance = null;
    
    //single Cipherer instance 
    private static Cipherer cipherer = null;
    
    //checks whether password encoding is required. It is configured in Spring environment.
    private boolean allowEncoding = false;
    
    /* Checks whether the submitted key for SecretKeySpec in plain text or a Integer represantation of byte sequence. 
     * It is configured in Spring environment.
     */
    private boolean keyInPlainText = false;
    
    /*
     * the value to be set in Cipherer.keyBytes. It is configured in Spring environment.
     */
    private String secretKey = null;
    
    /*
     * the name of the secret-key algorithm to be associated with the given key.
     * the value to be set in Cipherer.keyAlgorithm. It is configured in Spring environment.
     */
   
    private String secretKeyAlgorithm = null;
    
    /*
     * the name of the transformation, e.g., DES/CBC/PKCS5Padding.
     * the value to be set in Cipherer.keyAlgorithm. It is configured in Spring environment.
     */
    private String cipherTransformation = null;
    
	/**
     * Constuctor to be called only from Spring framework
     *
     */
	public PasswordCipherer() {
		instance = this;
	}
	
	/**
     * singleton accessor
     * @return LicenseBean
     */
    public static PasswordCipherer getInstance() {
        if (instance == null) {
            synchronized (PasswordCipherer.class) {
                //DOUBLE CHECK pattern is used
                if (instance == null) {
                    instance = new PasswordCipherer();
                }
            }
        }

        if ((cipherer == null) && (instance.isAllowEncoding()))  {
        	//only init if allowEncoding=true
        	instance.initCipherer();
        }

        return instance;
    }
    
    /**
     * Initialzies the cipherer.
     */
    private void initCipherer() {
        synchronized (PasswordCipherer.class) {
            //DOUBLE CHECK pattern is used
            if ((cipherer == null))  {
                cipherer = new Cipherer();
                if (secretKey != null) cipherer.setKeyBytes(secretKey, keyInPlainText);
                if (cipherTransformation != null) cipherer.setCipherTransformation(cipherTransformation);
                if (secretKeyAlgorithm != null) cipherer.setKeyAlgorithm(secretKeyAlgorithm);
                cipherer.init();                            
            }
        }
    }


    /**
     * <p>Decodes the specified raw password with an implementation specific algorithm if allowEncoding is TRUE.</p>
     * Otherwise it returns encPass.
     * @param encPass
     * @return
     * @throws DataAccessException
     */
    public String decodePassword(String encPass) {
        synchronized (PasswordCipherer.class) {
            try{
                log.debug("Decode password: " + allowEncoding);
                if(!allowEncoding) return encPass;
                return cipherer.decode(encPass);
            } catch (Exception ex) {
                log.warn("Password decryption failed", ex);
                throw new DataAccessResourceFailureException(ex.getMessage(), ex.getCause());
            }
        }
	}
    
    /**
     * <p>Encodes the specified raw password with an implementation specific algorithm if allowEncoding is TRUE.</p>
     * Otherwise it returns rawPass.
     * @param rawPass
     * @return
     * @throws DataAccessException
     */
    public String encodePassword(String rawPass) throws DataAccessException {
        synchronized (PasswordCipherer.class) {
            try {
                log.debug("Encode password: " + allowEncoding);
                if(!allowEncoding) return rawPass;
                return cipherer.encode(rawPass);
            } catch (Exception ex) {
                log.warn("Password decryption failed", ex);
                throw new DataAccessResourceFailureException(ex.getMessage(), ex.getCause());
            }            
        }
	}
    
    
    /********** PasswordEncoder METHODS ****************/
	/* (non-Javadoc)
	 * @see org.springframework.security.providers.encoding.PasswordEncoder#encodePassword(java.lang.String, java.lang.Object)
	 * NOTE: salt will be ignored since we will use the "secretket" defined in Spring configuration
	 */
	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		//log.debug("Encode password: " + rawPass);
		return encodePassword(rawPass);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.providers.encoding.PasswordEncoder#isPasswordValid(java.lang.String, java.lang.String, java.lang.Object)
	 * NOTE: salt will be ignored since we will use the "secretket" defined in Spring configuration
	 */
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
		//by this time the encPass should already be decrypted in RepoUser
		//log.debug("isPasswordValid: " + encPass+ " " + rawPass);
		return rawPass.equals(encPass);
	}
	
	
    /********** SPRING BEAN CALLBACKS ****************/
    
	/**
	 * @return Returns the allowEncoding.
	 */
	public boolean isAllowEncoding() {
		return allowEncoding;
	}

	/**
	 * @param allowEncoding The allowEncoding to set.
	 */
	public void setAllowEncoding(boolean allowEncoding) {
		this.allowEncoding = allowEncoding;
	}

	/**
	 * @return Returns the cipherTransformation.
	 */
	public String getCipherTransformation() {
		return cipherTransformation;
	}

	/**
	 * @param cipherTransformation The cipherTransformation to set.
	 */
	public void setCipherTransformation(String cipherTransformation) {
		this.cipherTransformation = cipherTransformation;
	}

	/**
	 * @return Returns the keyInPlainText.
	 */
	public boolean isKeyInPlainText() {
		return keyInPlainText;
	}

	/**
	 * @param keyInPlainText The keyInPlainText to set.
	 */
	public void setKeyInPlainText(boolean keyInPlainText) {
		this.keyInPlainText = keyInPlainText;
	}

	/**
	 * @return Returns the secretKey.
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey The secretKey to set.
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return Returns the secretKeyAlgorithm.
	 */
	public String getSecretKeyAlgorithm() {
		return secretKeyAlgorithm;
	}

	/**
	 * @param secretKeyAlgorithm The secretKeyAlgorithm to set.
	 */
	public void setSecretKeyAlgorithm(String secretKeyAlgorithm) {
		this.secretKeyAlgorithm = secretKeyAlgorithm;
	}


}

