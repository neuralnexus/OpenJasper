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

import jrsConfigs from '../../jrs.configs';

const DEFAULT_LOCALE_SEPARATOR = '-';
const DEFAULT_LOCALE_FALLBACK = 'en';
const DEFAULT_EMBEDDED_LOCALES = [DEFAULT_LOCALE_FALLBACK];

const importLocale = (locale, exactImportLocale, embeddedLocales) => {
    if (!embeddedLocales.includes(locale)) {
        return exactImportLocale(locale);
    } else {
        // en and en-us locales are bundled into the moment library, no need to load them
        return Promise.resolve();
    }
}

// if locale is zh-cn we will load zh-cn first
// and if load will fail we will try to load zh locale as a fallback
// if load still will fail we will use default fallback
const resolveLocale = (localesToCheck, localeFallback, exactImportLocale, embeddedLocales) => {
    const localeToImport = localesToCheck.pop();
    return importLocale(localeToImport, exactImportLocale, embeddedLocales)
        .then(() => localeToImport)
        .catch(() => {
            if (localesToCheck.length > 0) {
                return resolveLocale(localesToCheck, localeFallback, exactImportLocale, embeddedLocales);
            } else {
                return localeFallback;
            }
        })
}

const defaultLocaleConverter = (locale) => {
    const locales = locale.toLowerCase().split('_');

    return locales.reduce((acc, nextLocalePart, index) => {
        const previousLocalePart = index > 0 ? `${acc[index - 1]}${DEFAULT_LOCALE_SEPARATOR}` : ''
        const nextLocale = `${previousLocalePart}${nextLocalePart}`;
        return acc.concat(nextLocale);
    }, []);
}

export default ({
    exactImportLocale,
    locale = jrsConfigs.userLocale,
    localeFallback = DEFAULT_LOCALE_FALLBACK,
    localeConverter = defaultLocaleConverter,
    embeddedLocales = DEFAULT_EMBEDDED_LOCALES
}) => {
    const localesToCheck = localeConverter(locale);

    return resolveLocale(localesToCheck, localeFallback, exactImportLocale, embeddedLocales);
}