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

package com.jaspersoft.jasperserver.war.themes;

import org.springframework.context.support.AbstractMessageSource;
import org.springframework.ui.context.Theme;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Implements MessageSource interface for themes.
 * This class disregards the locale, only code matters for the message look up.
 * @author asokolnikov
 */
public class ThemeMessageSource extends AbstractMessageSource {

    private Map<String, MessageFormat> messages;

    public ThemeMessageSource() {
        messages = new HashMap<String, MessageFormat>();
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return messages.get(code);
    }

    public void addMessage(String code, String message) {
        messages.put(code, new MessageFormat(message));
    }

}
