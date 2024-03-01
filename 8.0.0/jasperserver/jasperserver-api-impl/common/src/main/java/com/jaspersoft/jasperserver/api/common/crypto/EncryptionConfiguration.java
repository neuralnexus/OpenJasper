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

public abstract class EncryptionConfiguration extends ExternalConfiguration {

    protected String keyAlias;

    protected String keyPass;

    protected String transformation;

    protected String initializationVector;

    protected Integer blockSize;

    protected String secretKey;

    protected String keyAlgorithm;

    protected Integer keySize;

    protected String keyUuid;

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public void setInitializationVector(String initializationVector) {
        this.initializationVector = initializationVector;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
    }

    public void setKeyUuid(String keyUuid) {
        this.keyUuid = keyUuid;
    }

    protected void init(final String confId) throws Exception {
        super.init(confId);
    }

    protected void copyTo(final EncryptionConfiguration other) {
        super.copyTo(other);
        other.setKeyAlias(keyAlias);
        other.setKeyPass(keyPass);
        other.setTransformation(transformation);
        other.setInitializationVector(initializationVector);
        other.setBlockSize(blockSize);
        other.setSecretKey(secretKey);
        other.setKeyAlgorithm(keyAlgorithm);
        other.setKeySize(keySize);
        other.setKeyUuid(keyUuid);
    }
}
