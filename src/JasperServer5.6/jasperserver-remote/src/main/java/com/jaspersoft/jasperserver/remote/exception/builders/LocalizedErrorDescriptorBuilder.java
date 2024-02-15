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


package com.jaspersoft.jasperserver.remote.exception.builders;

import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author inesterenko
 * @version $Id: LocalizedErrorDescriptorBuilder.java 47331 2014-07-18 09:13:06Z kklein $
 */

public class LocalizedErrorDescriptorBuilder extends ErrorDescriptor.Builder{
    public static final String BUNDLE_PREFIX = "exception.remote.";

    private MessageSource messageSource;

    public LocalizedErrorDescriptorBuilder(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    public ErrorDescriptor createDescriptor(String errorCode, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource != null ? messageSource.getMessage(errorCode, params, null, locale) : errorCode;
        return this.setMessage(message).setErrorCode(errorCode).setParameters(params).getErrorDescriptor();
    }

    public ErrorDescriptor localizeDescriptor(ErrorDescriptor base){
        Locale locale = LocaleContextHolder.getLocale();
        String message = base.getMessage();

        if (messageSource != null) {
            message = messageSource.getMessage(BUNDLE_PREFIX + base.getErrorCode(),
                    base.getParameters(), message, locale);
        }

        base.setMessage(message);

        return base;
    }

}
