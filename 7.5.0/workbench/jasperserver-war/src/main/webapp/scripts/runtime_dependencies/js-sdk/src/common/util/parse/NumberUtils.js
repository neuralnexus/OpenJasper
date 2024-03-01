define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

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
var NumberUtils = function NumberUtils(options) {
  options = options || {};
  this.DECIMAL_SEPARATOR = options.decimalSeparator || '.';
  this.GROUPING_SEPARATOR = options.groupingSeparator || ',';

  function separatorSymbolToRegExpPattern(separator) {
    if (separator === '.') {
      separator = '\\.';
    } else if (separator === ' ') {
      separator = '\\s';
    }

    return separator;
  }

  this.decimalSeparator = separatorSymbolToRegExpPattern(this.DECIMAL_SEPARATOR);
  this.groupingSeparator = separatorSymbolToRegExpPattern(this.GROUPING_SEPARATOR);
  this.DECIMAL_NUMBER_PATTERN = new RegExp('^-?([1-9]{1}[0-9]{0,2}(' + this.groupingSeparator + '[0-9]{3})*(' + this.decimalSeparator + '[0-9]+)?|[1-9]{1}[0-9]{0,}(' + this.decimalSeparator + '[0-9]+)?|0(' + this.decimalSeparator + '[0-9]+)?)$');
  this.INTEGER_NUMBER_PATTERN = new RegExp('^-?([1-9]{1}[0-9]{0,2}(' + this.groupingSeparator + '[0-9]{3})*|[1-9]{1}[0-9]{0,}|0)$');
};

var MIN_INT32 = -2147483648;
var MAX_INT32 = 2147483647;
var MAX_INT = 9007199254740992;
var MIN_INT = -9007199254740992;

NumberUtils.prototype.number_format = function (number, decimals, dec_point, thousands_sep) {
  number = (number + '').replace(/[^0-9+\-Ee.]/g, '');

  var n = !isFinite(+number) ? 0 : +number,
      prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
      sep = typeof thousands_sep === 'undefined' ? ',' : thousands_sep,
      dec = typeof dec_point === 'undefined' ? '.' : dec_point,
      toFixedFix = function toFixedFix(n, prec) {
    var k = Math.pow(10, prec);
    return '' + (Math.round(n * k) / k).toFixed(prec);
  };

  var result = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');

  if (result[0].length > 3) {
    result[0] = result[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
  }

  if ((result[1] || '').length < prec) {
    result[1] = result[1] || '';
    result[1] += new Array(prec - result[1].length + 1).join('0');
  }

  return result.join(dec);
};

NumberUtils.prototype.isInt = function (value) {
  return this.isNumberInt(value) || this.isStringInt(value);
};

NumberUtils.prototype.isNumberInt = function (value) {
  return _.isNumber(value) && !isNaN(value) && value !== Number.POSITIVE_INFINITY && value !== Number.NEGATIVE_INFINITY && (value === +value && value === (value | 0) || value % 1 === 0);
};

NumberUtils.prototype.isStringInt = function (value) {
  return _.isString(value) && this.INTEGER_NUMBER_PATTERN.test(value);
};

NumberUtils.prototype.isInt32 = function (value) {
  return this.isInt(value) && this.parseNumber(value) >= MIN_INT32 && this.parseNumber(value) <= MAX_INT32;
};

NumberUtils.prototype.isDecimal = function (value) {
  return _.isNumber(value) && !isNaN(value) && value !== Number.POSITIVE_INFINITY && value !== Number.NEGATIVE_INFINITY || _.isString(value) && this.DECIMAL_NUMBER_PATTERN.test(value);
};

NumberUtils.prototype.parseNumber = function (value) {
  if (_.isNumber(value)) {
    return value;
  } else if (_.isString(value) && this.DECIMAL_NUMBER_PATTERN.test(value)) {
    value = value.replace(new RegExp(this.groupingSeparator, 'g'), '').replace(new RegExp(this.decimalSeparator, 'g'), '.');
    var result = value * 1;

    if (result > MAX_INT || result < MIN_INT) {
      if (window.console) {
        window.console.warn(value + ' is out of the [' + MIN_INT + ', ' + MAX_INT + '] bounds. ' + 'Parsing results may be corrupted. Use string representation instead. ' + 'For more details see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number.');
      }

      return false;
    }

    return result;
  }

  return undefined;
};

NumberUtils.prototype.formatNumber = function (val, options) {
  options = options || {};
  var decimalNumStr = val.toString().split('.')[1];
  var decimalPlacesAmount = decimalNumStr ? decimalNumStr.length : 0;

  if (!_.isUndefined(options.decimalPlacesAmount)) {
    decimalPlacesAmount = options.decimalPlacesAmount;
  }

  try {
    return this.number_format(val, decimalPlacesAmount, options.decimalSeparator || this.DECIMAL_SEPARATOR, options.groupingSeparator || this.GROUPING_SEPARATOR);
  } catch (ex) {
    return val.toString();
  }
};

module.exports = NumberUtils;

});