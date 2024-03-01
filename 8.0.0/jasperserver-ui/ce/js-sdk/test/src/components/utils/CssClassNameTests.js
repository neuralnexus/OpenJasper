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

import CssClassName from 'src/components/utils/CssClassName';
describe('CssClassName', function () {
    describe('Setters & Getters', function () {
        it('should throw an error if name non-string', function () {
            expect(function () {
                new CssClassName({
                    type: 'module',
                    name: { test: 'dfdfdfdf' }
                });
            }).toThrow(new TypeError('\'name\' should be a string'));
        });
        it('should throw an error if name isn\'t specified', function () {
            expect(function () {
                new CssClassName({
                    type: 'module',
                    name: ''
                });
            }).toThrow(new Error('\'name\' shouldn\'t be an empty string'));
        });
        it('should throw an error if type non-string', function () {
            expect(function () {
                new CssClassName({ name: 'balbalb' });
            }).toThrow(new TypeError('\'type\' should be a string'));
        });
        it('should throw an error if type isn\'t specified', function () {
            expect(function () {
                new CssClassName({
                    name: 'balbalb',
                    type: ''
                });
            }).toThrow(new Error('\'type\' shouldn\'t be an empty string'));
        });
        it('should throw an error if type isn\'t one of available types', function () {
            expect(function () {
                new CssClassName({
                    name: 'balbalb',
                    type: 'aaaaa'
                });
            }).toThrow(new Error('\'type\' should be one of available types'));
        });
    });
    describe('Generate CSS class names by default', function () {
        it('should generate module name', function () {
            var cssClass = new CssClassName({
                type: 'module',
                name: 'dialog'
            });
            expect(cssClass.toString()).toEqual('jr-mDialog');
        });
        it('should generate layout name', function () {
            var cssClass = new CssClassName({
                type: 'layout',
                name: 'wrap'
            });
            expect(cssClass.toString()).toEqual('jr-lWrap');
        });
        it('should generate state name', function () {
            var cssClass = new CssClassName({
                type: 'state',
                name: 'hidden'
            });
            expect(cssClass.toString()).toEqual('jr-isHidden');
        });
        it('should generate util name', function () {
            var cssClass = new CssClassName({
                type: 'util',
                name: 'width-400pc'
            });
            expect(cssClass.toString()).toEqual('jr-uWidth-400pc');
        });
        it('should generate `js-hook` name', function () {
            var cssClass = new CssClassName({
                type: 'jshook',
                name: 'BlaBlaBla'
            });
            expect(cssClass.toString()).toEqual('jr-jBlaBlaBla');
        });
    });
    describe('Generate CSS class names by customized main prefix', function () {
        beforeEach(function () {
            this.prefix = CssClassName.MAIN_PREFIX;
            CssClassName.MAIN_PREFIX = 'foo';
        });
        afterEach(function () {
            CssClassName.MAIN_PREFIX = this.prefix;
        });
        it('should generate module name', function () {
            var cssClass = new CssClassName({
                type: 'module',
                name: 'dialog'
            });
            expect(cssClass.toString()).toEqual('foo-mDialog');
        });
        it('should generate layout name', function () {
            var cssClass = new CssClassName({
                type: 'layout',
                name: 'wrap'
            });
            expect(cssClass.toString()).toEqual('foo-lWrap');
        });
        it('should generate state name', function () {
            var cssClass = new CssClassName({
                type: 'state',
                name: 'hidden'
            });
            expect(cssClass.toString()).toEqual('foo-isHidden');
        });
        it('should generate util name', function () {
            var cssClass = new CssClassName({
                type: 'util',
                name: 'width-400pc'
            });
            expect(cssClass.toString()).toEqual('foo-uWidth-400pc');
        });
        it('should generate jshook name name', function () {
            var cssClass = new CssClassName({
                type: 'jshook',
                name: 'test'
            });
            expect(cssClass.toString()).toEqual('foo-jTest');
        });
    });
});