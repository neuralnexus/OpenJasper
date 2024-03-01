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

import NumberUtils from 'src/common/util/parse/NumberUtils';
describe('Number Formatter', function () {
    var numberUtils = new NumberUtils();
    it('should be defined', function () {
        expect(NumberUtils).toBeDefined();
        expect(typeof NumberUtils).toEqual('function');
    });
    it('should create instance with default params', function () {
        expect(numberUtils).toBeDefined();
        expect(typeof numberUtils).toEqual('object');
    });
    it('should have methods', function () {
        expect(numberUtils).toBeDefined();
        expect(numberUtils.isInt).toBeDefined();
        expect(typeof numberUtils.isInt).toEqual('function');
        expect(numberUtils.isNumberInt).toBeDefined();
        expect(typeof numberUtils.isNumberInt).toEqual('function');
        expect(numberUtils.isStringInt).toBeDefined();
        expect(typeof numberUtils.isStringInt).toEqual('function');
        expect(numberUtils.isInt32).toBeDefined();
        expect(typeof numberUtils.isInt32).toEqual('function');
        expect(numberUtils.isDecimal).toBeDefined();
        expect(typeof numberUtils.isDecimal).toEqual('function');
        expect(numberUtils.parseNumber).toBeDefined();
        expect(typeof numberUtils.parseNumber).toEqual('function');
        expect(numberUtils.formatNumber).toBeDefined();
        expect(typeof numberUtils.formatNumber).toEqual('function');
    });
    describe('isInt', function () {
        it('should return true for integer Number', function () {
            expect(numberUtils.isInt(42)).toEqual(true);
            expect(numberUtils.isInt(-1123456)).toEqual(true);
        });
        it('should return true for 0', function () {
            expect(numberUtils.isInt(0)).toEqual(true);
        });
        it('should return false for NaN', function () {
            expect(numberUtils.isInt(Number.NaN)).toEqual(false);
        });
        it('should return false for Infinity', function () {
            expect(numberUtils.isInt(Number.POSITIVE_INFINITY)).toEqual(false);
            expect(numberUtils.isInt(Number.NEGATIVE_INFINITY)).toEqual(false);
        });
        it('should return false for float Number', function () {
            expect(numberUtils.isInt(42.13)).toEqual(false);
            expect(numberUtils.isInt(-1142.13)).toEqual(false);
        });
        it('should return true for numbers bigger then Int32', function () {
            expect(numberUtils.isInt(2147483649)).toEqual(true);
        });
        it('should return true for numbers bigger then Int64', function () {
            expect(numberUtils.isInt(9223372036854776000)).toEqual(true);
        });
        it('should return true for integer number string', function () {
            expect(numberUtils.isInt('1234')).toEqual(true);
            expect(numberUtils.isInt('0')).toEqual(true);
            expect(numberUtils.isInt('-32425234')).toEqual(true);
        });
        it('should return false for integer number string with leading 0', function () {
            expect(numberUtils.isInt('01234')).toEqual(false);
        });
        it('should return false for decimal number string', function () {
            expect(numberUtils.isInt('1234.0123')).toEqual(false);
            expect(numberUtils.isInt('0.00')).toEqual(false);
            expect(numberUtils.isInt('-32425234.00')).toEqual(false);
        });
        it('should return true for integer number string with grouping', function () {
            expect(numberUtils.isInt('1,234')).toEqual(true);
            expect(numberUtils.isInt('-32,425,234')).toEqual(true);
        });
        it('should return false for decimal number string with grouping', function () {
            expect(numberUtils.isInt('1,234.00')).toEqual(false);
            expect(numberUtils.isInt('-32,425,234.123')).toEqual(false);
        });
        it('should return false for integer number string with incorrect grouping', function () {
            expect(numberUtils.isInt('12,34')).toEqual(false);
            expect(numberUtils.isInt('-324,25,234')).toEqual(false);
        });
        it('should return true for big integer number string', function () {
            expect(numberUtils.isInt('9223372036854775809')).toEqual(true);
            expect(numberUtils.isInt('2,147,483,649')).toEqual(true);
        });
    });
    describe('isNumberInt', function () {
        it('should return true for integer Number', function () {
            expect(numberUtils.isNumberInt(42)).toEqual(true);
            expect(numberUtils.isNumberInt(-1123456)).toEqual(true);
        });
        it('should return true for 0', function () {
            expect(numberUtils.isNumberInt(0)).toEqual(true);
        });
        it('should return false for NaN', function () {
            expect(numberUtils.isNumberInt(Number.NaN)).toEqual(false);
        });
        it('should return false for Infinity', function () {
            expect(numberUtils.isNumberInt(Number.POSITIVE_INFINITY)).toEqual(false);
            expect(numberUtils.isNumberInt(Number.NEGATIVE_INFINITY)).toEqual(false);
        });
        it('should return false for float Number', function () {
            expect(numberUtils.isNumberInt(42.13)).toEqual(false);
            expect(numberUtils.isNumberInt(-1142.13)).toEqual(false);
        });
        it('should return true for numbers bigger then Int32', function () {
            expect(numberUtils.isNumberInt(2147483649)).toEqual(true);
        });
        it('should return true for numbers bigger then Int64', function () {
            expect(numberUtils.isNumberInt(9223372036854776000)).toEqual(true);
        });
    });
    describe('isStringInt', function () {
        it('should return true for integer number string', function () {
            expect(numberUtils.isStringInt('1234')).toEqual(true);
            expect(numberUtils.isStringInt('0')).toEqual(true);
            expect(numberUtils.isStringInt('-32425234')).toEqual(true);
        });
        it('should return false for integer number string with leading 0', function () {
            expect(numberUtils.isStringInt('01234')).toEqual(false);
        });
        it('should return false for decimal number string', function () {
            expect(numberUtils.isStringInt('1234.0123')).toEqual(false);
            expect(numberUtils.isStringInt('0.00')).toEqual(false);
            expect(numberUtils.isStringInt('-32425234.00')).toEqual(false);
        });
        it('should return true for integer number string with grouping', function () {
            expect(numberUtils.isStringInt('1,234')).toEqual(true);
            expect(numberUtils.isStringInt('-32,425,234')).toEqual(true);
        });
        it('should return false for decimal number string with grouping', function () {
            expect(numberUtils.isStringInt('1,234.00')).toEqual(false);
            expect(numberUtils.isStringInt('-32,425,234.123')).toEqual(false);
        });
        it('should return false for integer number string with incorrect grouping', function () {
            expect(numberUtils.isStringInt('12,34')).toEqual(false);
            expect(numberUtils.isStringInt('-324,25,234')).toEqual(false);
        });
        it('should return true for big integer number string', function () {
            expect(numberUtils.isStringInt('9223372036854775809')).toEqual(true);
            expect(numberUtils.isStringInt('2,147,483,649')).toEqual(true);
        });
    });
    describe('isDecimal', function () {
        it('should return true for any Number', function () {
            expect(numberUtils.isDecimal(42)).toEqual(true);
            expect(numberUtils.isDecimal(-1123456)).toEqual(true);
            expect(numberUtils.isDecimal(42.13)).toEqual(true);
            expect(numberUtils.isDecimal(-1142.13)).toEqual(true);
            expect(numberUtils.isDecimal(0)).toEqual(true);
        });
        it('should return true for decimal number string', function () {
            expect(numberUtils.isDecimal('1234.120')).toEqual(true);
            expect(numberUtils.isDecimal('0.00')).toEqual(true);
            expect(numberUtils.isDecimal('-32425234.234')).toEqual(true);
        });
        it('should return true for decimal number string with grouping separators', function () {
            expect(numberUtils.isDecimal('1,234.123')).toEqual(true);
            expect(numberUtils.isDecimal('1,234')).toEqual(true);
            expect(numberUtils.isDecimal('-32,425,234.012')).toEqual(true);
        });
        it('should return false for decimal number string with incorrect grouping', function () {
            expect(numberUtils.isDecimal('12,34')).toEqual(false);
            expect(numberUtils.isDecimal('12,34.123')).toEqual(false);
            expect(numberUtils.isDecimal('1234.123.00')).toEqual(false);
            expect(numberUtils.isDecimal('-324,25234.1')).toEqual(false);
        });
        it('should return false for NaN', function () {
            expect(numberUtils.isDecimal(Number.NaN)).toEqual(false);
        });
        it('should return false for numbers without full part', function () {
            expect(numberUtils.isDecimal('.75')).toEqual(false);
        });
        it('should return false for Infinity', function () {
            expect(numberUtils.isDecimal(Number.POSITIVE_INFINITY)).toEqual(false);
            expect(numberUtils.isDecimal(Number.NEGATIVE_INFINITY)).toEqual(false);
        });
        it('should return false for decimal number string with leading 0', function () {
            expect(numberUtils.isInt('01234.01')).toEqual(false);
        });
    });
    describe('parseNumber', function () {
        it('should correctly parse Number', function () {
            expect(numberUtils.parseNumber(123)).toEqual(123);
        });
        it('should return undefined if unable to parse value', function () {
            expect(numberUtils.parseNumber([])).toEqual(undefined);
            expect(numberUtils.parseNumber('2323,233.0000.00')).toEqual(undefined);
        });
        it('should return false for values that exceed precise numbers', function () {
            expect(numberUtils.parseNumber('9007199254740994')).toEqual(false);
        });
        it('should correctly parse integer string', function () {
            expect(numberUtils.parseNumber('922337203685')).toEqual(922337203685);
            expect(numberUtils.parseNumber('-22123')).toEqual(-22123);
        });
        it('should correctly parse integer string with grouping', function () {
            expect(numberUtils.parseNumber('2,123')).toEqual(2123);
            expect(numberUtils.parseNumber('-2,211,123')).toEqual(-2211123);
        });
        it('should correctly parse decimal string', function () {
            expect(numberUtils.parseNumber('123.123')).toEqual(123.123);
            expect(numberUtils.parseNumber('-22123.00')).toEqual(-22123);
        });
        it('should correctly parse decimal string with grouping', function () {
            expect(numberUtils.parseNumber('2,123.123')).toEqual(2123.123);
            expect(numberUtils.parseNumber('-2,211,123.00')).toEqual(-2211123);
        });
    });
    describe('isInt32', function () {
        it('should return true for Int32 Number', function () {
            expect(numberUtils.isInt32(2147483647)).toEqual(true);
            expect(numberUtils.isInt32(-2147483648)).toEqual(true);
        });
        it('should return true for Int32 number string', function () {
            expect(numberUtils.isInt32('2147483647')).toEqual(true);
            expect(numberUtils.isInt32('-2,147,483,648')).toEqual(true);
        });
        it('should return false for non-Int32 Number', function () {
            expect(numberUtils.isInt32(2147483648)).toEqual(false);
            expect(numberUtils.isInt32(-2147483649)).toEqual(false);
        });
        it('should return false for non-Int32 number string', function () {
            expect(numberUtils.isInt32('2,147,483,648')).toEqual(false);
            expect(numberUtils.isInt32('-2147483649')).toEqual(false);
        });
    });
    describe('formatNumber', function () {
        it('should correctly format Number according to current locale', function () {
            expect(numberUtils.formatNumber(1234.567)).toEqual('1,234.567');
        });
    });
});