/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

package com.jaspersoft.jasperserver.export;

public class AuditAccessTransferCommand extends BaseExportImportCommand {
    public static final String DEFAULT_COMMAND_BEAN_NAME = "auditAccessTransferCommand";
    public static final String METADATA_BEAN_NAME = "exportCommandMetadata";

    protected AuditAccessTransferCommand() {
        super(DEFAULT_COMMAND_BEAN_NAME, METADATA_BEAN_NAME);
    }

    public static void main(String[] args) {
        debugArgs(args);

        boolean success = false;
        try {
            success = new AuditAccessTransferCommand().process(args);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace(System.err);
        }
        System.exit(success ? 0 : -1);
    }
}