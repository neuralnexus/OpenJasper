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

import ScopeChecker from 'src/common/util/ScopeChecker';
describe('ScopeChecker', function () {
    it('should return 0 properties count for empty object', function () {
        expect(new ScopeChecker({}).getPropertiesCount()).toBe(0);
    });
    it('should return empty properties array for empty object', function () {
        var propertiesNames = new ScopeChecker({}).getPropertiesNames();
        expect(propertiesNames).toBeDefined();
        expect(propertiesNames.length).toBe(0);
    });
    it('should return 0 added and 0 removed properties for empty object', function () {
        var comparisonResult = new ScopeChecker({}).compareProperties({});
        expect(comparisonResult).toBeDefined();
        expect(comparisonResult.added).toBeDefined();
        expect(comparisonResult.removed).toBeDefined();
        expect(comparisonResult.added.length).toBe(0);
        expect(comparisonResult.removed.length).toBe(0);
    });
    it('should return 2 properties count for object with 2 properties', function () {
        var obj = {
            prop1: 'A',
            prop2: 'B'
        };
        expect(new ScopeChecker(obj).getPropertiesCount()).toBe(2);
    });
    it('should return properties array with 2 properties names for object with 2 properties', function () {
        var obj = {
            prop1: 'A',
            prop2: 'B'
        };
        var propertiesNames = new ScopeChecker(obj).getPropertiesNames();
        expect(propertiesNames).toBeDefined();
        expect(propertiesNames.length).toBe(2);
        expect(propertiesNames).toEqual([
            'prop1',
            'prop2'
        ]);
    });
    it('should compare 2 objects and return 1 removed and 2 added as comparison result', function () {
        var obj = {
            prop1: 'A',
            prop2: 'B'
        };
        var scopeChecker = new ScopeChecker(obj);
        var originalPropertiesNames = scopeChecker.getPropertiesNames();
        delete obj.prop1;
        obj.prop3 = 'C';
        obj.prop4 = 'D';
        var comparisonResult = scopeChecker.compareProperties(originalPropertiesNames);
        expect(comparisonResult).toBeDefined();
        expect(comparisonResult.added).toBeDefined();
        expect(comparisonResult.removed).toBeDefined();
        expect(comparisonResult.added.length).toBe(2);
        expect(comparisonResult.removed.length).toBe(1);
        expect(comparisonResult).toEqual({
            added: [
                'prop3',
                'prop4'
            ],
            removed: ['prop1'],
            madeUndefined: [],
            pollution: [
                'prop3',
                'prop4'
            ]
        });
    });
    it('should compare 2 objects and return 1 removed (made undefined) and 0 added as comparison result', function () {
        var obj = {
            prop1: 'A',
            prop2: 'B'
        };
        var scopeChecker = new ScopeChecker(obj);
        var originalPropertiesNames = scopeChecker.getPropertiesNames();
        delete obj.prop1;
        var comparisonResult = scopeChecker.compareProperties(originalPropertiesNames);
        expect(comparisonResult).toBeDefined();
        expect(comparisonResult.added).toBeDefined();
        expect(comparisonResult.removed).toBeDefined();
        expect(comparisonResult.added.length).toBe(0);
        expect(comparisonResult.removed.length).toBe(1);
        expect(comparisonResult).toEqual({
            added: [],
            removed: ['prop1'],
            madeUndefined: [],
            pollution: []
        });
    });
    it('should remove all 4 properties and return 4 removed and 0 added as comparison result', function () {
        var obj = {
            prop1: 'A',
            prop2: 'B',
            prop3: 'C',
            prop4: 'D'
        };
        var scopeChecker = new ScopeChecker(obj);
        var originalPropertiesNames = scopeChecker.getPropertiesNames();
        delete obj.prop1;
        delete obj.prop2;
        delete obj.prop3;
        delete obj.prop4;
        var comparisonResult = scopeChecker.compareProperties(originalPropertiesNames);
        expect(comparisonResult).toBeDefined();
        expect(comparisonResult.added).toBeDefined();
        expect(comparisonResult.removed).toBeDefined();
        expect(comparisonResult.added.length).toBe(0);
        expect(comparisonResult.removed.length).toBe(4);
        expect(comparisonResult).toEqual({
            added: [],
            removed: [
                'prop1',
                'prop2',
                'prop3',
                'prop4'
            ],
            madeUndefined: [],
            pollution: []
        });
    });
    it('should add 4 properties to empty object and return 0 removed and 4 added as comparison result', function () {
        var obj = {};
        var scopeChecker = new ScopeChecker(obj);
        var originalPropertiesNames = scopeChecker.getPropertiesNames();
        obj.prop1 = 'A';
        obj.prop2 = 'B';
        obj.prop3 = 'C';
        obj.prop4 = 'D';
        var comparisonResult = scopeChecker.compareProperties(originalPropertiesNames);
        expect(comparisonResult).toBeDefined();
        expect(comparisonResult.added).toBeDefined();
        expect(comparisonResult.removed).toBeDefined();
        expect(comparisonResult.added.length).toBe(4);
        expect(comparisonResult.removed.length).toBe(0);
        expect(comparisonResult).toEqual({
            added: [
                'prop1',
                'prop2',
                'prop3',
                'prop4'
            ],
            removed: [],
            madeUndefined: [],
            pollution: [
                'prop1',
                'prop2',
                'prop3',
                'prop4'
            ]
        });
    });
    it('should add 4 properties to empty object and return 0 removed, 4 added, 1 madeUndefined as comparison result', function () {
        var obj = {};
        var scopeChecker = new ScopeChecker(obj);
        var originalPropertiesNames = scopeChecker.getPropertiesNames();
        obj.prop1 = 'A';
        obj.prop2 = 'B';
        obj.prop3 = 'C';
        obj.prop4 = 'D';
        obj.prop3 = undefined;
        var comparisonResult = scopeChecker.compareProperties(originalPropertiesNames);
        expect(comparisonResult).toBeDefined();
        expect(comparisonResult.added).toBeDefined();
        expect(comparisonResult.removed).toBeDefined();
        expect(comparisonResult.added.length).toBe(4);
        expect(comparisonResult.removed.length).toBe(0);
        expect(comparisonResult).toEqual({
            added: [
                'prop1',
                'prop2',
                'prop3',
                'prop4'
            ],
            removed: [],
            madeUndefined: ['prop3'],
            pollution: [
                'prop1',
                'prop2',
                'prop4'
            ]
        });
    });
});