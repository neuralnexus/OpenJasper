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
