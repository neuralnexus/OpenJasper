package com.jaspersoft.jasperserver.test.ks;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.FileNotFoundException;

public class JrsAnnotationConfigContextLoader extends AnnotationConfigContextLoader {
    @Override
    protected void prepareContext(GenericApplicationContext context) {
        try {
            KeystoreUtils.createIfNotExists(getClass());
        } catch (FileNotFoundException e) {

        }
        super.prepareContext(context);
    }
}
