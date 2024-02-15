/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.remote.services.async;

import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

/*
*  @author inesterenko
*/
public class ImportRunnable extends BaseImportExportTaskRunnable<StateDto> {

    protected final static Log log = LogFactory.getLog(ImportRunnable.class);

    public ImportRunnable(Map<String, Boolean> exportParams, InputStream stream) throws Exception{
        this(exportParams, stream, LocaleContextHolder.getLocale());
    }

    public ImportRunnable(Map<String, Boolean> exportParams, InputStream stream, Locale locale) throws Exception{
        this.parameters = exportParams;
        this.file = copyToTempFile(stream);
        this.state = new StateDto();
        this.locale = locale;
    }

    @Override
    public void run(){
        try {
            service.doImport(file, parameters, locale);
            state.setPhase(Task.FINISHED);
            state.setMessage(localize("import.finished"));
        } catch (ImportFailedException e) {
            state.setPhase(Task.FAILED);
            state.setMessage(e.getMessage());
            log.error("Import failed: ", e);
        } finally {
            if(!file.delete()){
                log.error("Can't delete temp file "+file.getAbsolutePath());
            }
        }
    }

    protected File copyToTempFile(InputStream input) throws IOException {
        File tmp = File.createTempFile("import_", null);

        FileOutputStream fileStream = new FileOutputStream(tmp);

        byte[] buff = new byte[512];
        int read = input.read(buff);
        while (read > 0){
            fileStream.write(buff, 0, read);
            read = input.read(buff);
        }
        fileStream.flush();
        fileStream.close();

        return tmp;
    }

    public StateDto getResult() {
        return state;
    }

    @Override
    public void prepare() {
        state.setMessage(localize("import.in.progress"));
    }
}
