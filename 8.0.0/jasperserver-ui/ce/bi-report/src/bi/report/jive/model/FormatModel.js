/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
var POSITIVE_NUMBER_FORMAT = '###0';
var DURATION_PATTERN = '[h]:mm:ss';
var currencyMap = {
    'LOCALE_SPECIFIC': '\xA4',
    'USD': '$',
    'GBP': '\xA3',
    'EUR': '\u20AC',
    'YEN': '\xA5'
};
export default Backbone.Model.extend({
    defaults: function () {
        return {
            'backgroundColor': undefined,
            'align': undefined,
            'font': {
                'name': undefined,
                'size': 11,
                'bold': false,
                'italic': false,
                'underline': false,
                'color': '000000'
            }
        };
    },
    initialize: function (attrs, options) {
        options || (options = {});
        this.dataType = options.dataType;
    },
    parse: function (jiveObj) {
        var res = {
            align: jiveObj.fontHAlign.toLowerCase(),
            backgroundColor: jiveObj.mode === 'Opaque' ? jiveObj.fontBackColor : 'transparent',
            font: {
                'name': jiveObj.fontName,
                'size': parseFloat(jiveObj.fontSize),
                'italic': jiveObj.fontItalic,
                'bold': jiveObj.fontBold,
                'underline': jiveObj.fontUnderline,
                'color': jiveObj.fontColor
            }
        };
        if (jiveObj.formatPattern) {
            if (this.dataType === 'numeric' && DURATION_PATTERN === jiveObj.formatPattern) {
                res.pattern = jiveObj.formatPattern;
            } else if (this.dataType === 'numeric') {
                var pattern = jiveObj.formatPattern, precisionRegexp = new RegExp('0\\.(0+)'), grouping = false, percentage = false, precision = 0, negativeFormat = '-' + POSITIVE_NUMBER_FORMAT, currency = null;
                if (pattern.indexOf(',') > -1) {
                    grouping = true;
                    pattern = pattern.replace(/,/g, '');
                }
                if (pattern.indexOf('%') > -1) {
                    percentage = true;
                    pattern = pattern.replace(/%/g, '');
                }
                if (pattern.indexOf('\xA4') > -1) {
                    currency = 'LOCALE_SPECIFIC';
                    pattern = pattern.replace(/\u00A4/g, '');
                } else if (pattern.indexOf('$') > -1) {
                    currency = 'USD';
                    pattern = pattern.replace(/\u0024/g, '');
                } else if (pattern.indexOf('\xA3') > -1) {
                    currency = 'GBP';
                    pattern = pattern.replace(/\u00A3/g, '');
                } else if (pattern.indexOf('\u20AC') > -1) {
                    currency = 'EUR';
                    pattern = pattern.replace(/\u20AC/g, '');
                } else if (pattern.indexOf('\xA5') > -1) {
                    currency = 'YEN';
                    pattern = pattern.replace(/\u00A5/g, '');
                }
                pattern = pattern.replace(/\s/g, '');
                var precisionTestResult = precisionRegexp.exec(pattern);
                if (precisionTestResult && precisionTestResult.length == 2) {
                    precision = precisionTestResult[1].length;
                    pattern = pattern.replace(new RegExp('\\.' + Array(precision + 1).join('0'), 'g'), '');
                }
                var parts = pattern.split(';');
                if (parts && parts.length === 2) {
                    negativeFormat = parts[1];
                }
                res.pattern = {
                    negativeFormat: negativeFormat,
                    currency: currency,
                    precision: precision,
                    grouping: grouping,
                    percentage: percentage
                };
            } else {
                res.pattern = jiveObj.formatPattern;
            }
        }
        return res;
    },
    toJiveFormat: function () {
        if (!this.has('pattern')) {
            return '';
        } else {
            if (this.dataType !== 'numeric' || this.dataType === 'numeric' && DURATION_PATTERN === this.get('pattern')) {
                return this.get('pattern');
            } else {
                var patternObj = this.get('pattern'), positivePart = POSITIVE_NUMBER_FORMAT, negativePart = patternObj.negativeFormat || '-' + POSITIVE_NUMBER_FORMAT;
                if (patternObj.percentage) {
                    positivePart += ' %';
                    negativePart += ' %';
                }
                if (patternObj.currency && currencyMap[patternObj.currency]) {
                    positivePart = currencyMap[patternObj.currency] + ' ' + positivePart;
                    negativePart = currencyMap[patternObj.currency] + ' ' + negativePart;
                }
                if (patternObj.grouping) {
                    var index = positivePart.indexOf('#');
                    positivePart = positivePart.slice(0, index) + '#,' + positivePart.slice(index + 1);
                    index = negativePart.indexOf('#');
                    negativePart = negativePart.slice(0, index) + '#,' + negativePart.slice(index + 1);
                }
                if (patternObj.precision) {
                    var index = positivePart.indexOf('##0');
                    positivePart = positivePart.slice(0, index) + '##0' + ('.' + Array(patternObj.precision + 1).join('0')) + positivePart.slice(index + 3);
                    index = negativePart.indexOf('##0');
                    negativePart = negativePart.slice(0, index) + '##0' + ('.' + Array(patternObj.precision + 1).join('0')) + negativePart.slice(index + 3);
                }
                return positivePart + ';' + negativePart;
            }
        }
    }
});