package com.jaspersoft.jasperserver.export.io;

import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.crypto.KeyProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.KeystoreProperties;
import com.jaspersoft.jasperserver.crypto.properties.KeyAlgorithm;
import com.jaspersoft.jasperserver.export.ImportInputMetadata;
import com.jaspersoft.jasperserver.export.ImportTask;
import com.jaspersoft.jasperserver.export.Importer;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

public class SecretKeyImporter implements Importer {
    protected static final CommandOut commandOut = CommandOut.getInstance();
    public static final String OK = "Key is successfully stored.";

    protected ImportTask task;

    @Override
    public void setTask(ImportTask task) {
        this.task = task;

        ImportInputMetadata metadata = new ImportInputMetadata();
        metadata.setProperty(ImportExportService.ROOT_TENANT_ID, TenantService.ORGANIZATIONS);
        this.task.setInputMetadata(metadata);
    }

    @Override
    public void performImport() throws ImportFailedException {
        final EncryptionParams params = new EncryptionParams(task.getParameters());

        KeystoreManager keystoreMng = KeystoreManager.getInstance();

        byte[] bytes;

        try (InputStream in = task.getInput().getFileInputStream(null)) {
            int n = in.available();
            bytes = new byte[n];
            in.read(bytes, 0, n);
        } catch (Exception e) {
            throw new ImportFailedException(e.getMessage());
        }

        Optional<String> keyAlias = params.getKeyAlias();
        if (!keyAlias.isPresent() || keyAlias.get().isEmpty()) {
            final String message = "Key alias is required.";
//            commandOut.error(message);

            throw new ImportFailedException(message);
        }

        if (!params.getKeyAlg().isPresent()) {
            final String message = "Key algorithm is required.";
//            commandOut.error(message);

            throw new ImportFailedException(message);
        }

        int keySize = 0;
        if (params.getKeySize().isPresent()) {
            try {
                keySize = parseInt(params.getKeySize().get());
            } catch (NumberFormatException e) {
                final String message = "Key size is invalid.";
//                commandOut.error(message);

                throw new ImportFailedException(message);
            }
        }

        if (params.hasGenKey() && keySize <= 0) {
            final String message = "Key size is required for key generation.";
//            commandOut.error(message);

            throw new ImportFailedException(message);
        }

        String keyPasswd = params.getKeyPasswd().orElseGet(() -> {
            final KeystoreProperties keystoreProperties = keystoreMng.getKeystoreProperties(keyAlias.get());
            return keystoreProperties == null || keystoreProperties.getKeyPasswd() == null ? "" : keystoreProperties.getKeyPasswd();
        });
        String keyAlg = params.getKeyAlg().get();

        final KeystoreProperties keystoreProperties = new KeystoreProperties(keyAlias.get(), keyPasswd);
        if (bytes.length > 0 && (keyAlg.equals(KeyAlgorithm.AES) || keyAlg.equals(KeyAlgorithm.RSA) || keyAlg.equals(KeyAlgorithm.DES_EDE))) {
            Key key = new SecretKeySpec(bytes, keyAlg);
            keystoreMng.setKey(key, keystoreProperties);

            commandOut.info(OK);
        } else if (params.hasGenKey() && keyAlg.equals(KeyAlgorithm.AES)) {
            try {
                keystoreMng.generateSecret(new KeyProperties(keyAlg, keySize), keystoreProperties, false);

            } catch (Exception e) {
                throw new ImportFailedException(e.getMessage());
            }

            commandOut.info(OK);
        } else if (params.hasGenKey() && keyAlg.equals(KeyAlgorithm.RSA)) {
            try {
                keystoreMng.generateKeyPair(new KeyProperties(keyAlg, keySize), keystoreProperties, false);

            } catch (Exception e) {
                throw new ImportFailedException(e.getMessage());
            }

            commandOut.info(OK);
        } else {
            final String message = format("Key algorithm '%s' is not supported.", keyAlg);
            throw new ImportFailedException(message);
        }
    }
}
