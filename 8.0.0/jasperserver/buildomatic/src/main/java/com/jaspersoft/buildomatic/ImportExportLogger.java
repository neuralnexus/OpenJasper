package com.jaspersoft.buildomatic;

public class ImportExportLogger extends org.apache.tools.ant.DefaultLogger {
    @Override
    protected String getBuildSuccessfulMessage() {
        return "VALIDATION COMPLETED";
    }
}
