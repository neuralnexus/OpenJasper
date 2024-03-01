package com.jaspersoft.jasperserver.test.ks;

import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.System.getenv;

public class KeystoreUtils {

    public static void createIfNotExists(Class<?> clazz) throws FileNotFoundException {
        if (!Files.exists(Paths.get(getenv("ksp"), KeystoreManager.KS_PROP_NAME))) {
            final File file = ResourceUtils.getFile(clazz.getResource("/enc.properties"));
            KeystoreManager.init(getenv("ks"), getenv("ksp"), file);
        }
    }

}
