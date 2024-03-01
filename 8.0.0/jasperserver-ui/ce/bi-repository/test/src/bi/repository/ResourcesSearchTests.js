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
import sinon from 'sinon';
import _ from 'underscore';
import ResourcesSearch from 'src/bi/repository/ResourcesSearch';

describe('ResourcesSearch BI component', function () {
    describe('instance creation', function () {
        it('should create instance', function () {
            expect(new ResourcesSearch()).toBeDefined();
        });    //            it("should be a BiComponent", function(){
        //                expect(new ResourcesSearch() instanceof BiComponent).toBeTruthy();
        //            });
        //            it("should be a BiComponent", function(){
        //                expect(new ResourcesSearch() instanceof BiComponent).toBeTruthy();
        //            });
        it('should create simple properties', function () {
            var inst = new ResourcesSearch(), propertyNames = [
                    'server',
                    'q',
                    'folderUri',
                    'types',
                    'offset',
                    'limit',
                    'recursive',
                    'sortBy',
                    'accessType',
                    'showHiddenItems',
                    'forceTotalCount'
                ], instancePropertyNames = _.functions(inst);
            _.each(propertyNames, function (property) {
                expect(_.indexOf(instancePropertyNames, property) < 0).toBeFalsy();
            });
        });
        it('should create common properties', function () {
            var inst = new ResourcesSearch(), propertyNames = [
                    'properties',
                    'data'
                ], instancePropertyNames = _.functions(inst);
            _.each(propertyNames, function (property) {
                expect(_.indexOf(instancePropertyNames, property) > 0).toBeTruthy();
            });
        });
        it('should set values to simple properties', function () {
            var inst = new ResourcesSearch(), propertyNames = [
                    'server',
                    'q',
                    'folderUri',
                    'types',
                    'offset',
                    'limit',
                    'recursive',
                    'sortBy',
                    'accessType',
                    'showHiddenItems',
                    'forceTotalCount'
                ], value = 'sapi';
            _.each(propertyNames, function (property) {
                inst[property](value);
                expect(inst[property]()).toEqual(value);
            });
        });
        it('should set values to simple properties and return instance', function () {
            var inst = new ResourcesSearch(), propertyNames = [
                    'server',
                    'q',
                    'folderUri',
                    'types',
                    'offset',
                    'limit',
                    'recursive',
                    'sortBy',
                    'accessType',
                    'showHiddenItems',
                    'forceTotalCount'
                ], value = 'sapi';
            _.each(propertyNames, function (property) {
                expect(inst[property](value)).toBe(inst);
            });
        });
        it('should set values to common properties', function () {
            var inst = new ResourcesSearch(), propertyNames = ['properties'], value = { q: '9' };
            _.each(propertyNames, function (property) {
                inst[property](value);
                expect(inst[property]()).toEqual(value);
            });
        });
        it('should set values to common properties and return instance', function () {
            var inst = new ResourcesSearch(), propertyNames = ['properties'], value = { q: '9' };
            _.each(propertyNames, function (property) {
                expect(inst[property](value)).toBe(inst);
            });
        });
        it('should set values to proper instance', function () {
            var inst = new ResourcesSearch(), inst2 = new ResourcesSearch(), propertyNames = ['properties'], value = { q: '9' }, value2 = { q: '5' };
            _.each(propertyNames, function (property) {
                inst[property](value);
                inst2[property](value2);
                expect(inst[property]()).toEqual(value);
                expect(inst2[property]()).toEqual(value2);
                expect(inst2[property]()).not.toEqual(inst[property]());
            });
        });
        it('should set simple values to proper instance', function () {
            var inst = new ResourcesSearch(), inst2 = new ResourcesSearch(), propertyNames = [
                    'server',
                    'q',
                    'folderUri',
                    'types',
                    'offset',
                    'limit',
                    'recursive',
                    'sortBy',
                    'accessType',
                    'showHiddenItems',
                    'forceTotalCount'
                ], value = 'sapi', value2 = 'urg';
            _.each(propertyNames, function (property) {
                inst[property](value);
                inst2[property](value2);
                expect(inst[property]()).toEqual(value);
                expect(inst2[property]()).toEqual(value2);
                expect(inst2[property]()).not.toEqual(inst[property]());
            });
        });
        it('should set simple values via properties method', function () {
            var inst = new ResourcesSearch(), propertyNames = [
                    'server',
                    'q',
                    'folderUri',
                    'types',
                    'offset',
                    'limit',
                    'recursive',
                    'sortBy',
                    'accessType',
                    'showHiddenItems',
                    'forceTotalCount'
                ], value = 'sapi', value2 = 'urg';
            _.each(propertyNames, function (property) {
                var options = {};
                options[property] = property;
                inst.properties(options);
                expect(inst.properties()[property]).toEqual(property);
                options[property] = undefined;
                inst.properties(options);
                expect(inst.properties()[property]).not.toBeDefined();
            });
        });
        it('should set simple values and properites together', function () {
            var inst = new ResourcesSearch();
            inst.properties({ q: 'q' });
            inst.folderUri('folderUri');
            expect(inst.properties().q).toEqual('q');
            expect(inst.properties().folderUri).toEqual('folderUri');
        });
        it('should set simple values and properites together 2', function () {
            var inst = new ResourcesSearch();
            inst.properties({ q: 'q' });
            inst.folderUri('folderUri');
            inst.properties({ recursive: true });
            expect(inst.properties().q).toEqual('q');
            expect(inst.properties().folderUri).toEqual('folderUri');
            expect(inst.properties().recursive).toBeTruthy();
        });
        it('should have default data()', function () {
            var rs = new ResourcesSearch();
            expect(rs.data()).toEqual([]);
        });
    });
    describe('action Run', function () {
        var defaultSettings = { server: 'http://localhost:8080/jasperserver-pro' }, defaultData = { resourceLookup: [] }, server;
        beforeEach(function () {
            server = sinon.fakeServer.create();
        });
        afterEach(function () {
            server.restore();
        });
        it('should have run method', function () {
            var inst = new ResourcesSearch(defaultSettings);
            expect(inst.run).toBeDefined();
            expect(_.isFunction(inst.run)).toBeTruthy();
        });
        it('should return deferred', function () {
            var inst = new ResourcesSearch(defaultSettings), res = inst.run();
            expect(_.isFunction(res.done)).toBeTruthy();
            expect(_.isFunction(res.fail)).toBeTruthy();
            expect(_.isFunction(res.always)).toBeTruthy();
        });
        it('should take callback and run it on resolve', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), inst = new ResourcesSearch(defaultSettings), res = inst.run(sucsess, failure);
            res.resolve();
            expect(sucsess.called).toBeTruthy();
            expect(failure.called).toBeFalsy();
        });
        it('should take errback and run it on resolve', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), inst = new ResourcesSearch(defaultSettings), res = inst.run(sucsess, failure);
            res.reject();
            expect(sucsess.called).toBeFalsy();
            expect(failure.called).toBeTruthy();
        });
        it('should take complete handler and run it on resolve', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), complete = sinon.spy(), inst = new ResourcesSearch(defaultSettings), res = inst.run(sucsess, failure, complete);
            res.resolve();
            expect(complete.called).toBeTruthy();
        });
        it('should take complete handler and run it on fail', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), complete = sinon.spy(), inst = new ResourcesSearch(defaultSettings), res = inst.run(sucsess, failure, complete);
            res.reject();
            expect(complete.called).toBeTruthy();
        });
        it('should construct url properly - defaults', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), url = defaultSettings.server + '/rest_v2/resources?', inst = new ResourcesSearch(defaultSettings);
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            server.respond();
            expect(sucsess.called).toBeTruthy();
            expect(failure.called).toBeFalsy();
        });
        it('should construct url properly - error', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), url = defaultSettings.server + '/rest_v2/resources?adfsdffsfdf', inst = new ResourcesSearch(defaultSettings);
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            server.respond();
            expect(sucsess.called).toBeFalsy();
            expect(failure.called).toBeTruthy();
        });
        it('should construct url properly - server ends with slash', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), url = defaultSettings.server + '/rest_v2/resources?', settings = { server: defaultSettings.server + '/' }, inst = new ResourcesSearch(settings);
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            server.respond();
            expect(sucsess.called).toBeTruthy();
            expect(failure.called).toBeFalsy();
        });
        it('should construct url properly - parameter set', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), q = 'adfaugawpuib', url = defaultSettings.server + '/rest_v2/resources?q=' + q, inst = new ResourcesSearch(defaultSettings);
            inst.q(q);
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            server.respond();
            expect(sucsess.called).toBeTruthy();
            expect(failure.called).toBeFalsy();
        });
        it('should construct url properly - multiple parameters set', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), q = 'adfaugawpuib', folderUri = '/saii/fa', recursive = false, url = defaultSettings.server + '/rest_v2/resources?q=' + q + '&folderUri=' + folderUri + '&recursive=' + recursive, inst = new ResourcesSearch(defaultSettings);
            inst.properties(_.extend({}, defaultSettings, {
                q: q,
                folderUri: folderUri,
                recursive: recursive
            }));
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            server.respond();
            expect(sucsess.called).toBeTruthy();
            expect(failure.called).toBeFalsy();
        });
        it('should pass results to callback', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), q = 'adfaugawpuib', folderUri = '/saii/fa', recursive = false, url = defaultSettings.server + '/rest_v2/resources?q=' + q + '&folderUri=' + folderUri + '&recursive=' + recursive, inst = new ResourcesSearch(defaultSettings);
            inst.properties(_.extend({}, defaultSettings, {
                q: q,
                folderUri: folderUri,
                recursive: recursive
            }));
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            server.respond();
            expect(sucsess.called).toBeTruthy();
            expect(sucsess.args[0][0]).toEqual(defaultData.resourceLookup);
        });
        it('should pass results to data()', function () {
            var sucsess = sinon.spy(), failure = sinon.spy(), q = 'adfaugawpuib', folderUri = '/saii/fa', recursive = false, url = defaultSettings.server + '/rest_v2/resources?q=' + q + '&folderUri=' + folderUri + '&recursive=' + recursive, inst = new ResourcesSearch(defaultSettings);
            inst.properties(_.extend({}, defaultSettings, {
                q: q,
                folderUri: folderUri,
                recursive: recursive
            }));
            server.respondWith(url, [
                200,
                {},
                JSON.stringify(defaultData)
            ]);
            inst.run(sucsess, failure);
            expect(inst.data()).toEqual([]);
            server.respond();
            expect(inst.data()).toEqual(defaultData.resourceLookup);
        });
    });
    describe('action Validate', function () {
        it('should have the method', function () {
            var inst = new ResourcesSearch();
            expect(inst.validate).toBeDefined();
            expect(_.isFunction(inst.validate)).toBeTruthy();
        });
        it('should validate properties', function () {
            var inst = new ResourcesSearch();
            expect(inst.validate()).toBeDefined();
        });
        it('should validate valid properties', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).not.toBeDefined();
        });
        it('should validate on run and pass validation error to errback', function () {
            var inst = new ResourcesSearch({
                    server: 'http://localhost:8080/jasperserver-pro',
                    recursive: 'yes'
                }), spy = sinon.spy();
            inst.run(null, spy);
            expect(spy.called).toBeTruthy();
        });
    });
    describe('properties validation', function () {
        it('should require server property', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.properties({ server: undefined });
            expect(inst.validate()).toBeTruthy();
        });
        it('should not allow to set random values to types', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.types('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.types(['folder']);
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to types in array', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.types(['sd,[sdm']);
            expect(inst.validate()).toBeTruthy();
            inst.types(['folder']);
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to sortBy', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.sortBy('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.sortBy('label');
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to accessType', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.accessType('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.accessType('viewed');
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to recursive', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.recursive('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.recursive(true);
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to showHiddenItems', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.showHiddenItems('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.showHiddenItems(true);
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to forceTotalCount', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.forceTotalCount('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.forceTotalCount(true);
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to offset', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.offset('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.offset(4);
            expect(inst.validate()).toBeFalsy();
        });
        it('should not allow to set random values to limit', function () {
            var inst = new ResourcesSearch({ server: 'http://localhost:8080/jasperserver-pro' });
            expect(inst.validate()).toBeFalsy();
            inst.limit('sd,[sdm');
            expect(inst.validate()).toBeTruthy();
            inst.limit(4);
            expect(inst.validate()).toBeFalsy();
        });
    });
});