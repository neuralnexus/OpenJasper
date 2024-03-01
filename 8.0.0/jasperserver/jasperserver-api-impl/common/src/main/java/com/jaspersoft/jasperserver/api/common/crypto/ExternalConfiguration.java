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

import java.util.Objects;

public abstract class ExternalConfiguration {
    protected String keyAliasProp;

    protected String keyPassProp;

    protected String transformationProp;

    protected String initializationVectorProp;

    protected String blockSizeProp;

    protected String secretKeyProp;

    protected String keyAlgorithmProp;

    protected String keySizeProp;

    protected String keystoreProp;

    protected String storePasswdProp;

    protected String keyUuidProp;

    public void setKeyAliasProp(String keyAliasProp) {
        this.keyAliasProp = keyAliasProp;
    }

    public void setKeyPassProp(String keyPassProp) {
        this.keyPassProp = keyPassProp;
    }

    public void setTransformationProp(String transformationProp) {
        this.transformationProp = transformationProp;
    }

    public void setInitializationVectorProp(String initializationVectorProp) {
        this.initializationVectorProp = initializationVectorProp;
    }

    public void setBlockSizeProp(String blockSizeProp) {
        this.blockSizeProp = blockSizeProp;
    }

    public void setSecretKeyProp(String secretKeyProp) {
        this.secretKeyProp = secretKeyProp;
    }

    public void setKeyAlgorithmProp(String keyAlgorithmProp) {
        this.keyAlgorithmProp = keyAlgorithmProp;
    }

    public void setKeySizeProp(String keySizeProp) {
        this.keySizeProp = keySizeProp;
    }

    public void setKeystoreProp(String keystoreProp) {
        this.keystoreProp = keystoreProp;
    }

    public void setStorePasswdProp(String storePasswdProp) {
        this.storePasswdProp = storePasswdProp;
    }

    public void setKeyUuidProp(String keyUuidProp) {
        this.keyUuidProp = keyUuidProp;
    }

    protected void init(final String confId) throws Exception {
        Objects.requireNonNull(confId);

        if (keyAliasProp == null) keyAliasProp = confId + ".keyalias";
        if (keyPassProp == null) keyPassProp = confId + ".keypass";
        if (transformationProp == null) transformationProp = confId + ".enc.transformation";
        if (initializationVectorProp == null) initializationVectorProp = confId + ".enc.iv";
        if (blockSizeProp == null) blockSizeProp = confId + ".block.size";
        if (secretKeyProp == null) secretKeyProp = confId + ".secret.key";
        if (keyAlgorithmProp == null) keyAlgorithmProp = confId + ".keyalg";
        if (keySizeProp == null) keySizeProp = confId + ".keysize";
        if (keystoreProp == null) keystoreProp = confId + ".keystore";
        if (storePasswdProp == null) storePasswdProp = confId + ".storepass";
        if (keyUuidProp == null) keyUuidProp = confId + ".uuid";
    }

    protected void copyTo(final ExternalConfiguration conf) {
        conf.setKeyAliasProp(keyAliasProp);
        conf.setKeyPassProp(keyPassProp);
        conf.setTransformationProp(transformationProp);
        conf.setInitializationVectorProp(initializationVectorProp);
        conf.setBlockSizeProp(blockSizeProp);
        conf.setSecretKeyProp(secretKeyProp);
        conf.setKeyAlgorithmProp(keyAlgorithmProp);
        conf.setKeySizeProp(keySizeProp);
        conf.setKeystoreProp(keystoreProp);
        conf.setStorePasswdProp(storePasswdProp);
        conf.setKeyUuidProp(keyUuidProp);
    }
}
