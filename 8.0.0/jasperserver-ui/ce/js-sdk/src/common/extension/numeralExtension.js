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

import numeral from 'numeral';
import jasperserverConfig from "../../i18n/jasperserver_config.properties";
import loadLocale from './commonLocaleExtension';

const DEFAULT_EMBEDDED_LOCALES = ['en'];

const importNumeralLocale = (locale) => {
    return import(`numeral/locales/${locale}`);
}

export default loadLocale({
    exactImportLocale: importNumeralLocale,
    embeddedLocales: DEFAULT_EMBEDDED_LOCALES
}).then((locale) => {
    numeral.locale(locale);
    numeral.localeData(locale).currency.symbol = jasperserverConfig['client.currency.symbol'];
    numeral.localeData(locale).delimiters.thousands = jasperserverConfig['client.delimiters.thousands'];
    numeral.localeData(locale).delimiters.decimal = jasperserverConfig['client.delimiters.decimal'];
});