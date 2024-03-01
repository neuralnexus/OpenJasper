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
package com.jaspersoft.jasperserver.remote.services.async;

import com.google.common.base.Charsets;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/*
*  @author inesterenko
*/
public class ImportRunnable extends BaseImportExportTaskRunnable<State> {

    protected final static Log log = LogFactory.getLog(ImportRunnable.class);
    public static final String JS_ENCRYPTED = "jsEncrypted";

    public ImportRunnable(Map<String, String> exportParams, InputStream stream) throws Exception{
        this(exportParams, stream, LocaleContextHolder.getLocale());
    }

    public ImportRunnable(Map<String, String> exportParams, InputStream stream, Locale locale) throws Exception{
        super(new State());

        this.parameters = exportParams;
        this.file = copyToTempFile(stream);
        this.locale = locale;
    }

    @Override
    public void run(){
        try {
            List<WarningDescriptor> warningDescriptors = new ArrayList<WarningDescriptor>();
            state.setWarnings(warningDescriptors);
            service.doImport(file, parameters, organizationId, brokenDependenciesStrategy, locale, warningDescriptors);
            synchronized (state) {
                state.setPhase(Task.FINISHED);
                state.setMessage(localize("import.finished"));
            }
        } catch (ImportFailedException e) {
            synchronized (state) {
                if (ImportExportService.ERROR_CODE_IMPORT_TENANTS_NOT_MATCH.equals(e.getErrorCode())
                        || ImportExportService.ERROR_CODE_IMPORT_BROKEN_DEPENDENCIES.equals(e.getErrorCode())) {
                    state.setPhase(Task.PENDING);
                    state.setMessage("Import is pending");
                    log.info(String.format("Import task %s is in pending mode.", state.getId()));
                } else {
                    state.setPhase(Task.FAILED);
                    state.setMessage(e.getMessage());
                    log.error("Import failed: ", e);
                }

                ErrorDescriptor errorDescriptor = new ErrorDescriptor();
                errorDescriptor.setErrorCode(e.getErrorCode());
                errorDescriptor.setParameters(e.getParameters());
                state.setError(errorDescriptor);
            }
        } catch (Exception e) {
            synchronized (state) {
                state.setPhase(Task.FAILED);
                state.setMessage(e.getMessage());
            }
            log.error("Import failed: ", e);
        } finally {
            if (!state.getPhase().equals(Task.PENDING)) {
                if(!file.delete()){
                    log.error("Can't delete temp file "+file.getAbsolutePath());
                }
            }
            taskCompletionDate = new Date();
        }
    }

    protected File copyToTempFile(InputStream input) throws IOException {
        File tmp = File.createTempFile("import_", null);

        try(FileOutputStream fileStream = new FileOutputStream(tmp)) {
            byte[] buff = new byte[512];

            final int jsEncryptedLength = JS_ENCRYPTED.getBytes().length;
            int read = input.read(buff, 0, jsEncryptedLength);

            final String jsEncryptedHeader = new String(buff, Charsets.UTF_8);
            if (jsEncryptedHeader.startsWith(JS_ENCRYPTED)) {
                parameters.put("isEncrypted", Boolean.TRUE.toString());

                byte[] keyUUIDBytes = new byte[36];
                int keyAliasRead = input.read(keyUUIDBytes, 0, keyUUIDBytes.length);
                final String keyUUIDHeader = new String(keyUUIDBytes, Charsets.UTF_8);
                parameters.put(EncryptionParams.KEY_ALIAS_PARAMETER, UUID.fromString(keyUUIDHeader).toString());

                read = input.read(buff);
            }
            while (read > 0){
                fileStream.write(buff, 0, read);
                read = input.read(buff);
            }
            fileStream.flush();
        }

        return tmp;
    }

    public State getResult() {
        return state;
    }

    @Override
    public void prepare() {
        state.setMessage(localize("import.in.progress"));
    }
}
