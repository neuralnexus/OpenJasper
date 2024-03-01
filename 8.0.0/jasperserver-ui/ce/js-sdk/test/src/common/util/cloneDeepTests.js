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

import cloneDeep from 'src/common/util/cloneDeep';

describe("cloneDeep Tests.", function() {

    it("should clone deep object", function() {
        var source = {
            a: "prop",
            b: 1,
            c: [1, 2, 3, {
                a: 1
            }],
            d: {
                e: 1
            }
        };

        var result = cloneDeep(source);

        expect(result).not.toBe(source);
        expect(result.c).not.toBe(source.c);
        expect(result.c[3]).not.toBe(source.c[3]);
        expect(result.d).not.toBe(source.d);
        expect(result).toEqual(source);
    });

    it("should clone string", function () {
        expect(cloneDeep("string")).toEqual("string");
    });

    it("should clone number", function () {
        expect(cloneDeep(1)).toEqual(1);
    });

    it("should clone boolean", function () {
        expect(cloneDeep(true)).toEqual(true);
        expect(cloneDeep(false)).toEqual(false);
    });

    it("should clone falsy values", function () {
        expect(cloneDeep(null)).toEqual(null);
        expect(cloneDeep(undefined)).toEqual(undefined);
    });

    it("should clone deep array", function() {
        var source = [1, 2, {
            a: 1
        }];

        var result = cloneDeep(source);

        expect(result).not.toBe(source);
        expect(result[2]).not.toBe(source[2]);
        expect(result).toEqual(source);
    });

    it("should clone deep object with customizer (root)", function() {
        var source = {
            a: "prop",
            b: 1,
            c: [1, 2, 3, {
                a: 1
            }],
            d: {
                e: 1
            }
        };

        var result = cloneDeep(source, function(value) {
            if (value === source) {
                return value;
            }
        });

        expect(result).toBe(source);
    });

    it("should clone deep object with customizer (child property)", function() {
        var source = {
            a: "prop",
            b: 1,
            c: [1, 2, 3, {
                a: 1
            }],
            d: {
                e: 1
            }
        };

        var result = cloneDeep(source, function(value, key) {
            if (key === "c") {
                return value;
            }
        });

        expect(result).not.toBe(source);
        expect(result).toEqual(source);

        expect(result.c).toBe(source.c);
    });

    it("should clone deep object if customizer is not a function", function() {
        var source = {
            a: "prop",
            b: 1,
            c: [1, 2, 3, {
                a: 1
            }],
            d: {
                e: 1
            }
        };

        var result = cloneDeep(source, 1);

        expect(result).not.toBe(source);
        expect(result.c).not.toBe(source.c);
        expect(result.d).not.toBe(source.d);

        expect(result).toEqual(source);
    });
});