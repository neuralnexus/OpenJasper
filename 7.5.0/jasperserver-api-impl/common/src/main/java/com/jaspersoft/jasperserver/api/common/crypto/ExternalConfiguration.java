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
