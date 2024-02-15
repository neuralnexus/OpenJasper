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

import com.jaspersoft.jasperserver.export.service.ImportExportService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.util.Locale;
import java.util.Map;

/**
 * @author  inesterenko
 */

abstract public class BaseImportExportTaskRunnable<T> implements TaskRunnable {

    protected Map<String, Boolean> parameters;
    protected File file;
    protected StateDto state;

    private MessageSource messageSource;
    protected ImportExportService service;
    protected Locale locale;

    public File getFile(){
            return file;
        }

    public StateDto getState(){
        return state;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ImportExportService getService() {
        return service;
    }

    public void setService(ImportExportService service) {
        this.service = service;
    }

    protected String localize(String key){
        return messageSource.getMessage(key, null, key, locale);
    }

    abstract public void prepare();
}
