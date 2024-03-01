/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

var regex = {
    numberPart: /([\d|#]+(?!,))/,
    decimalPart: /(\.[\d|#]+)/,
    numericChar: /[\d|#]/
};
var POSITIVE_NUMBER_FORMAT = '###0';
var DURATION_PATTERN = '[h]:mm:ss';
function addDecimalPlaceToToken(token) {
    var dotIndex = token.indexOf('.');
    if (dotIndex != -1) {
        // already have decimals
        var decimalPart = regex.decimalPart.exec(token)[1];
        return token.replace(decimalPart, decimalPart + '0');
    } else {
        // no decimals
        var numberPart = regex.numberPart.exec(token)[1];
        return token.replace(numberPart, numberPart + '.0');
    }
}
function removeDecimalPlaceFromToken(token) {
    var result = token, dotIndex = result.indexOf('.');
    if (dotIndex != -1) {
        var decimalPart = regex.decimalPart.exec(result)[1];
        if (decimalPart.length > 2) {
            // remove last decimal place
            result = result.replace(decimalPart, decimalPart.substring(0, decimalPart.length - 1));
        } else {
            // remove all (dot and decimal place)
            result = result.replace(decimalPart, '');
        }
    }
    return result;
}
function addThousandsSeparatorToToken(token) {
    var indexOfNumericChar = token.indexOf(regex.numericChar.exec(token)), firstPart = token.substring(0, indexOfNumericChar + 1);
    return firstPart + ',' + token.substring(firstPart.length);
}
function removeThousandsSeparatorFromToken(token) {
    return token.replace(',', '');
}
function addPercentageToToken(token) {
    return token + ' %';
}
function removePercentageFromToken(token) {
    return token.substring(0, token.length - 2);
}
var NumberFormatUtil = {
    addRemoveDecimalPlace: function (exp, booleanAdd) {
        var pozToken = exp.split(';')[0], negToken = exp.split(';')[1];
        if (booleanAdd) {
            exp = addDecimalPlaceToToken(pozToken);
            if (negToken) {
                exp = exp + ';' + addDecimalPlaceToToken(negToken);
            }
            return exp;
        } else {
            exp = removeDecimalPlaceFromToken(pozToken);
            if (negToken) {
                exp = exp + ';' + removeDecimalPlaceFromToken(negToken);
            }
            return exp;
        }
    },
    addRemoveThousandsSeparator: function (exp, booleanAdd) {
        var indexOfComma = exp.indexOf(','), pozToken = exp.split(';')[0], negToken = exp.split(';')[1];
        if (booleanAdd) {
            if (indexOfComma == -1) {
                // add
                exp = addThousandsSeparatorToToken(pozToken);
                if (negToken) {
                    exp = exp + ';' + addThousandsSeparatorToToken(negToken);
                }
            }
        } else {
            if (indexOfComma != -1) {
                // remove
                exp = removeThousandsSeparatorFromToken(pozToken);
                if (negToken) {
                    exp = exp + ';' + removeThousandsSeparatorFromToken(negToken);
                }
            }
        }
        return exp;
    },
    addRemovePercentage: function (exp, booleanAdd) {
        var indexOfPercent = exp.indexOf('%'), pozToken = exp.split(';')[0], negToken = exp.split(';')[1];
        if (booleanAdd) {
            // add
            if (indexOfPercent == -1) {
                exp = addPercentageToToken(pozToken);
                if (negToken) {
                    exp = exp + ';' + addPercentageToToken(negToken);
                }
            }
        } else {
            // remove
            if (indexOfPercent != -1) {
                exp = removePercentageFromToken(pozToken);
                if (negToken) {
                    exp = exp + ';' + removePercentageFromToken(negToken);
                }
            }
        }
        return exp;
    },
    addRemovePercentageForNumber: function (numberExp, booleanAdd) {
        var numberPart = regex.numberPart.exec(numberExp)[1];
        if (booleanAdd) {
            if (numberExp.indexOf('%') == -1 && numberPart.indexOf('00') == -1) {
                numberExp = numberExp.replace(numberPart, numberPart + '00');
                numberExp = numberExp + ' %';
            }
        } else {
            if (numberExp.indexOf('%') != -1 && numberPart.indexOf('00') != -1) {
                numberExp = numberExp.replace(numberPart, numberPart.substring(0, numberPart.length - 2));
                numberExp = numberExp.substring(0, numberExp.length - 2);
            }
        }
        return numberExp;
    },
    addRemoveCurrencySymbol: function (exp, booleanAdd, currencySymbol) {
        var indexOfCS = exp.indexOf(currencySymbol), pozToken = exp.split(';')[0], negToken = exp.split(';')[1];
        if (booleanAdd) {
            if (indexOfCS == -1) {
                exp = currencySymbol + ' ' + pozToken;
                if (negToken) {
                    exp = exp + ';' + currencySymbol + ' ' + negToken;
                }
            }
        } else {
            if (indexOfCS != -1) {
                exp = pozToken.substring(2);
                if (negToken) {
                    exp = exp + ';' + negToken.substring(2);
                }
            }
        }
        return exp;
    },
    jivePatternToSchemaPattern: function (jivePattern, jiveDataType) {
        var result;
        if (jiveDataType === 'numeric' && jivePattern === DURATION_PATTERN) {
            result = jivePattern;
        } else if (jiveDataType === 'numeric') {
            var pattern = jivePattern, precisionRegexp = new RegExp('0\\.(0+)'), grouping = false, percentage = false, precision = 0, negativeFormat = '-' + POSITIVE_NUMBER_FORMAT, currency = null;
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
            result = {
                negativeFormat: negativeFormat,
                currency: currency,
                precision: precision,
                grouping: grouping,
                percentage: percentage
            };
        } else {
            result = jivePattern;
        }
        return result;
    }
};
export default NumberFormatUtil;