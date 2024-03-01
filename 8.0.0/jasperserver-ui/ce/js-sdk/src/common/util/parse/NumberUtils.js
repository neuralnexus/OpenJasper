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

import _ from 'underscore';

const NumberUtils = function (options) {
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

    // http://regexlib.com/REDetails.aspx?regexp_id=196 with some changes
    this.DECIMAL_NUMBER_PATTERN = new RegExp('^-?([1-9]{1}[0-9]{0,2}(' + this.groupingSeparator + '[0-9]{3})*(' + this.decimalSeparator + '[0-9]+)?|[1-9]{1}[0-9]{0,}(' + this.decimalSeparator + '[0-9]+)?|0(' + this.decimalSeparator + '[0-9]+)?)$');
    this.INTEGER_NUMBER_PATTERN = new RegExp('^-?([1-9]{1}[0-9]{0,2}(' + this.groupingSeparator + '[0-9]{3})*|[1-9]{1}[0-9]{0,}|0)$');
};
const MIN_INT32 = -2147483648;
const MAX_INT32 = 2147483647;
const MAX_INT = Number.MAX_SAFE_INTEGER ? Number.MAX_SAFE_INTEGER+1 : 9007199254740992;
const MIN_INT = Number.MIN_SAFE_INTEGER ? Number.MIN_SAFE_INTEGER-1 : -9007199254740992;

/*
     Copyright (c) 2013 Kevin van Zonneveld (http://kvz.io)
     and Contributors (http://phpjs.org/authors)

     Permission is hereby granted, free of charge, to any person obtaining a copy of
     this software and associated documentation files (the "Software"), to deal in
     the Software without restriction, including without limitation the rights to
     use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
     of the Software, and to permit persons to whom the Software is furnished to do
     so, subject to the following conditions:

     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.

     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.
     */

NumberUtils.prototype.number_format = function (number, decimals, dec_point, thousands_sep) {
    //  discuss at: http://phpjs.org/functions/number_format/
    // original by: Jonas Raoni Soares Silva (http://www.jsfromhell.com)
    // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // improved by: davook
    // improved by: Brett Zamir (http://brett-zamir.me)
    // improved by: Brett Zamir (http://brett-zamir.me)
    // improved by: Theriault
    // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // bugfixed by: Michael White (http://getsprink.com)
    // bugfixed by: Benjamin Lupton
    // bugfixed by: Allan Jensen (http://www.winternet.no)
    // bugfixed by: Howard Yeend
    // bugfixed by: Diogo Resende
    // bugfixed by: Rival
    // bugfixed by: Brett Zamir (http://brett-zamir.me)
    //  revised by: Jonas Raoni Soares Silva (http://www.jsfromhell.com)
    //  revised by: Luke Smith (http://lucassmith.name)
    //    input by: Kheang Hok Chin (http://www.distantia.ca/)
    //    input by: Jay Klehr
    //    input by: Amir Habibi (http://www.residence-mixte.com/)
    //    input by: Amirouche
    //   example 1: number_format(1234.56);
    //   returns 1: '1,235'
    //   example 2: number_format(1234.56, 2, ',', ' ');
    //   returns 2: '1 234,56'
    //   example 3: number_format(1234.5678, 2, '.', '');
    //   returns 3: '1234.57'
    //   example 4: number_format(67, 2, ',', '.');
    //   returns 4: '67,00'
    //   example 5: number_format(1000);
    //   returns 5: '1,000'
    //   example 6: number_format(67.311, 2);
    //   returns 6: '67.31'
    //   example 7: number_format(1000.55, 1);
    //   returns 7: '1,000.6'
    //   example 8: number_format(67000, 5, ',', '.');
    //   returns 8: '67.000,00000'
    //   example 9: number_format(0.9, 0);
    //   returns 9: '1'
    //  example 10: number_format('1.20', 2);
    //  returns 10: '1.20'
    //  example 11: number_format('1.20', 4);
    //  returns 11: '1.2000'
    //  example 12: number_format('1.2000', 3);
    //  returns 12: '1.200'
    //  example 13: number_format('1 000,50', 2, '.', ' ');
    //  returns 13: '100 050.00'
    //  example 14: number_format(1e-8, 8, '.', '');
    //  returns 14: '0.00000001'

    number = (number + '').replace(/[^0-9+\-Ee.]/g, '');
    const n = !isFinite(+number) ? 0 : +number,
        prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
        sep = typeof thousands_sep === 'undefined' ? ',' : thousands_sep,
        dec = typeof dec_point === 'undefined' ? '.' : dec_point,
        toFixedFix = function (n, prec) {
            const k = Math.pow(10, prec);
            return '' + (Math.round(n * k) / k).toFixed(prec);
        };

    let result = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
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

// http://stackoverflow.com/a/3885844
NumberUtils.prototype.isNumberInt = function (value) {
    return _.isNumber(value) && !isNaN(value) && value !== Number.POSITIVE_INFINITY && value !== Number.NEGATIVE_INFINITY && (value === +value && value === (value | 0) || value % 1 === 0);
};
NumberUtils.prototype.isStringInt = function (value) {
    return _.isString(value) && this.INTEGER_NUMBER_PATTERN.test(value);
};

/**
     * We don't use this validator, since back-end maps Long, bigint fields to Integer filters.
     * So isInt should be used to validate Long and bigint filters
     * @param value
     * @returns {*|boolean}
     */

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
        if (result >= MAX_INT || result <= MIN_INT) {
            if (window.console) {
                window.console.warn(value + ' is out of the [' + (MIN_INT+1)   + ', ' + (MAX_INT-1) + '] bounds. ' + 'Parsing results may be corrupted. Use string representation instead. ' + 'For more details see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number.');
            }
            return false;
        }
        return result;
    }
    return undefined;
};
NumberUtils.prototype.formatNumber = function (val, options) {
    options = options || {};
    const decimalNumStr = val.toString().split('.')[1];

    let decimalPlacesAmount = decimalNumStr ? decimalNumStr.length : 0;
    if (!_.isUndefined(options.decimalPlacesAmount)) {
        decimalPlacesAmount = options.decimalPlacesAmount;
    }

    try {
        return this.number_format(
            val,
            decimalPlacesAmount,
            options.decimalSeparator || this.DECIMAL_SEPARATOR,
            options.groupingSeparator || this.GROUPING_SEPARATOR
        );
    } catch (ex) {
        return val.toString();
    }
};
NumberUtils.prototype.MaxRange = Number.MAX_SAFE_INTEGER ? Number.MAX_SAFE_INTEGER+1 : 9007199254740992;
NumberUtils.prototype.MinRange = Number.MIN_SAFE_INTEGER ? Number.MIN_SAFE_INTEGER-1 : -9007199254740992;

export default NumberUtils;
