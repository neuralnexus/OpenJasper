/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

define(function(require) {

    var _ = require("underscore"),
        numeral = require("numeral"),
        jrsConfigs = require("jrs.configs"),
        jasperserverConfig = require("bundle!jasperserver_config");

    require("numeralPackage/locales");

    var defaultLocale = "en";

    var locale = jrsConfigs.userLocale,
        numeralLocale = locale.toLowerCase().replace("_", "-");

    try {
        numeral.locale(numeralLocale);
        numeral.localeData(numeralLocale);
    } catch(e) {
        numeralLocale = defaultLocale;
        numeral.locale(numeralLocale);
    }

    numeral.localeData(numeralLocale).currency.symbol = jasperserverConfig["client.currency.symbol"];
    numeral.localeData(numeralLocale).delimiters.thousands = jasperserverConfig["client.delimiters.thousands"];
    numeral.localeData(numeralLocale).delimiters.decimal = jasperserverConfig["client.delimiters.decimal"];

    return numeral;
});