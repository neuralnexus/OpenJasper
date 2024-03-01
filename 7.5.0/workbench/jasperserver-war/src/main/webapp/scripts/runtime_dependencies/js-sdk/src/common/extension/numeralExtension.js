define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var numeral = require('numeral/numeral');

var jrsConfigs = require('../../jrs.configs');

var jasperserverConfig = require("bundle!jasperserver_config");

require('numeral/locales');

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
var defaultLocale = 'en';
var locale = jrsConfigs.userLocale;
var numeralLocale = locale.toLowerCase().replace('_', '-');

try {
  numeral.locale(numeralLocale);
  numeral.localeData(numeralLocale);
} catch (e) {
  numeralLocale = defaultLocale;
  numeral.locale(numeralLocale);
}

numeral.localeData(numeralLocale).currency.symbol = jasperserverConfig['client.currency.symbol'];
numeral.localeData(numeralLocale).delimiters.thousands = jasperserverConfig['client.delimiters.thousands'];
numeral.localeData(numeralLocale).delimiters.decimal = jasperserverConfig['client.delimiters.decimal'];
module.exports = numeral;

});