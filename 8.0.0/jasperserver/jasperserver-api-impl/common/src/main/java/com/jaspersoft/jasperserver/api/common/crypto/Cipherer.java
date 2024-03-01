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


import com.jaspersoft.jasperserver.api.security.encryption.BaseCipher;
import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.RandomGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import static com.jaspersoft.jasperserver.api.common.crypto.Hexer.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;


/**
 * This class provides the utilities of a cryptographic cipher for encryption and decryption.
 */
public class Cipherer extends BaseCipher {
    private static Log log = LogFactory.getLog(Cipherer.class);

    //Create an 8-byte initialization vector
    //TODO Static IV is bad
    private static final byte[] INIT_VECTOR_8 = {(byte) 0x8E, (byte) 0x12, (byte) 0x39, (byte) 0x9C,
            (byte) 0x07, (byte) 0x72, (byte) 0x6F, (byte) 0x5A};
//	private static final Random NONCE_GEN = new SecureRandom(SecureRandom.getSeed(16));

    //Create an 16-byte initialization vector
    private static final byte[] INIT_VECTOR_16 =
            {(byte) 0x8b, (byte) 0x48, (byte) 0x10, (byte) 0x03, (byte) 0x5d, (byte) 0xdf, (byte) 0xf9, (byte) 0xac,
                    (byte) 0x17, (byte) 0xf2, (byte) 0xbd, (byte) 0x64, (byte) 0xb7, (byte) 0x51, (byte) 0xc0, (byte) 0x29};


    private static final String IV_PREFIX = "JRSIV";
    private static final int IV_PREFIX_SIZE = IV_PREFIX.getBytes().length;

    private Key key;
    private byte[] initializationVector;
    private String cipherTransformation;
    private int blockSize;
    private int keySize;
    private String keyUuid;

    private boolean allowEncoding = true;
    private PlainCipher fallbackCipherer;

    public PlainCipher getFallbackCipherer() {
        return fallbackCipherer;
    }

    public void setFallbackCipherer(PlainCipher fallbackCipherer) {
        this.fallbackCipherer = fallbackCipherer;
    }

    public Cipherer(final EncryptionProperties properties, final Key key, final String keyUuid, final PlainCipher fallbackCipherer) throws NoSuchMethodException {
        super(properties, key, keyUuid, fallbackCipherer);
        this.cipherTransformation = properties.getCipherTransformation();
        this.blockSize = properties.getBlockSize();
        this.keySize = properties.getKeyProperties().getKeySize();
        this.keyUuid = keyUuid;
        this.key = key;

        if (properties.getInitializationVector() != null && !properties.getInitializationVector().isEmpty()) {
            this.initializationVector = parse(properties.getInitializationVector());
        }

        this.fallbackCipherer = fallbackCipherer;
    }

    public Cipherer(final EncryptionProperties properties, final String keyBytes, boolean isPlainText) throws NoSuchMethodException {
        this(properties
                , new SecretKeySpec(isPlainText ? requireNonNull(keyBytes).getBytes() : parse(requireNonNull(keyBytes))
                        , requireNonNull(properties).getKeyProperties().getKeyAlg()), null, null);
    }

    /**
     * Encodes and hexifies the given content
     */
    public String encode(String content) {
        if (!allowEncoding) return content;
        try {
            if (content == null) content = "";
            final String hexify = hexify(encode(content.getBytes(UTF_8)));
            //System.out.println(String.format("Encoding %s -> %s using %s", content, hexify, stringify(keyBytes)) );
            return hexify;
        } catch (Exception ex) {
            log.warn("Unsuccessful password encryption", ex);
            return hexify(encode(content.getBytes()));
        }
    }

    private byte[] encode(byte[] content) {
        byte[] initializationVector = this.initializationVector == null
                ? RandomGenerator.randomIV(blockSize)
                : this.initializationVector;


        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
        } catch (Exception e) {
            log.error("Cipher initialization failed", e);
            throw new RuntimeException(e);
        }

        try {
            final byte[] bytes = cipher.doFinal(content);

            ByteBuffer byteBuffer = ByteBuffer.allocate(
                    IV_PREFIX_SIZE + (Integer.SIZE / 8) + initializationVector.length + bytes.length);
            byteBuffer.put(IV_PREFIX.getBytes());
            byteBuffer.putInt(initializationVector.length);
            byteBuffer.put(initializationVector);
            byteBuffer.put(bytes);

            return byteBuffer.array();
        } catch (Exception ex) {
            log.warn("Encryption failed", ex);
            return content;
        }
    }

    /**
     * Dehexifies and decodes the given content
     * * @param content string to be decoded
     *
     * @return the decoded content.
     */
    public String decode(String content) {
        if (!allowEncoding) return content;
        if (content == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(dehexify(content));
        try {
            AlgorithmParameterSpec ivSpec = getIV(byteBuffer);
            if ( ivSpec == null && fallbackCipherer != null) {
                return fallbackCipherer.decode(content);
            }
            final String res = new String(decode(byteBuffer, ivSpec), UTF_8);
            return res;
        } catch (Exception ex) {
            log.warn("Password decrypted unsuccessfully.", ex);
            return new String(decode(byteBuffer, null));
        }
    }

    private AlgorithmParameterSpec getIV(ByteBuffer byteBuffer) {
        AlgorithmParameterSpec ivSpec = null;
        byte[] ivPrefixData = new byte[IV_PREFIX_SIZE];
        try {
            int ivLength = byteBuffer.get(ivPrefixData).getInt();
            String ivPrefix = new String(ivPrefixData);
            if (IV_PREFIX.equals(ivPrefix) && (ivLength == 8 || ivLength == 16)) {
                byte[] contentIv = new byte[ivLength];
                byteBuffer.get(contentIv);
                ivSpec = new IvParameterSpec(contentIv);
            }
        } catch (BufferUnderflowException e) {
            ivSpec = null;
        }
        return ivSpec;
    }

    private byte[] decode(ByteBuffer byteBuffer, AlgorithmParameterSpec ivSpec ) {
        byte[] cipherContent;
        final boolean isAes = cipherTransformation.toUpperCase().startsWith("AES");
        try {
            if (ivSpec !=  null) {
                cipherContent = new byte[byteBuffer.remaining()];
                byteBuffer.get(cipherContent);
            } else {
                ivSpec = new IvParameterSpec(isAes ? INIT_VECTOR_16 : INIT_VECTOR_8);
                cipherContent = byteBuffer.array();
            }
        } catch (BufferUnderflowException e) {
            cipherContent = byteBuffer.array();
            ivSpec = new IvParameterSpec(isAes ? INIT_VECTOR_16 : INIT_VECTOR_8);
        }

        byte[] data;
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } catch (Exception e) {
            log.error("Cipher initialization failed", e);
            throw new RuntimeException(e);
        }

        try {
            data = cipher.doFinal(cipherContent);
        } catch (Exception ex) {
            log.warn("Decryption failed", ex);
            data = byteBuffer.array();
        }
        return data;
    }

    /**
     * Decodes the given content if the booleanValue is "true" (case insensitive).
     * (Utility Function)
     *
     * @param content      string to be decoded
     * @param booleanValue specifies whether content needs to be decoded
     * @return the decoded content if the booleanValue is "true" (case insensitive). Otherwise it just returns content.
     */
    public String decode(String content, Object booleanValue) {
        if (!(booleanValue instanceof String) || (content == null))
            return content;
        boolean isEncrypted = Boolean.parseBoolean((String) booleanValue);
        if (isEncrypted) return decode(content);
        return content;
    }
//
//    /**
//     * @param inKeyBytes  The KEY_BYTES to set.
//     * @param isPlainText Whether key_bytes is plain text or a represantation of byte sequence
//     */
//    public void setKeyBytes(String inKeyBytes, boolean isPlainText) {
//        if (isPlainText) {
//            keyBytes = inKeyBytes.getBytes();
//        } else {
//            setKeyBytes(inKeyBytes);
//        }
//
//    }
//
//    public void setKeyBytes(String inKeyBytes) {
//        keyBytes = parse(inKeyBytes);
//    }

    @Override
    public void setAllowEncryption(boolean flag) {
        allowEncoding = flag;
    }

    @Override
    public String getCipherTransformation() {
        return cipherTransformation;
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    public String getKeyAlgorithm() {
        return this.key.getAlgorithm();
    }

    @Override
    public int getKeySize() {
        return keySize;
    }

    @Override
    public String getKeyUuid() {
        return keyUuid;
    }

    @Override
    public Key getKey() {
        return key;
    }
}
