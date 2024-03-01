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
import DataLayer from 'src/common/component/tree/TreeDataLayer';

describe('Tree component: TreeDataLayer', function () {
    describe('initialization', function () {
        it('should be', function () {
            expect(typeof DataLayer).toBe('function');
        });
        it('should be able to extend', function () {
            expect(typeof DataLayer.extend).toBe('function');
        });
        it('should run initialize function, even if it passed as child', function () {
            var TestLayer = DataLayer.extend({ initialize: sinon.spy() });
            new TestLayer({});
            expect(TestLayer.prototype.initialize.called).toBeTruthy();
        });
        it('should be able to pass getDataUri function', function () {
            var getDataUri = function () {
            };
            var layer = new DataLayer({ getDataUri: getDataUri });
            expect(layer.getDataUri).toBe(getDataUri);
        });
        it('should be able to pass dataUriTemplate', function () {
            var template = 'dataUriTemplate';
            var layer = new DataLayer({ dataUriTemplate: template });
            expect(layer.getDataUri).toBeTruthy();
            expect(layer.getDataUri()).toEqual(template);
        });
        it('should have higher priority for function', function () {
            var getDataUri = function () {
                return 'some';
            };
            var template = 'dataUriTemplate';
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataUri: getDataUri
            });
            expect(layer.getDataUri).toBe(getDataUri);
            expect(layer.getDataUri()).not.toEqual(template);
        });
        it('should define getDataArray and getDataSize functions', function () {
            var template = 'dataUriTemplate';
            var layer = new DataLayer({ dataUriTemplate: template });
            expect(typeof layer.getDataArray).toBe('function');
            expect(typeof layer.getDataSize).toBe('function');
        });
        it('should be able to pass getDataArray and getDataSize functions', function () {
            var template = 'dataUriTemplate', getDataArray = function () {
                }, getDataSize = function () {
                };
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray,
                getDataSize: getDataSize
            });
            expect(layer.getDataArray).toBe(getDataArray);
            expect(layer.getDataSize).toBe(getDataSize);
        });
        it('should return result of getDataArray in getDataSize by default', function () {
            var template = 'dataUriTemplate', getDataArray = function () {
                return [
                    1,
                    2,
                    3,
                    4,
                    5
                ];
            };
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray
            });
            expect(layer.getDataSize()).toBe(5);
        });
        it('should be able to pass external extractId function', function () {
            var template = 'dataUriTemplate', spy = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                extractId: spy
            });
            layer.extractId();
            expect(spy.called).toBeTruthy();
        });
        it('should be able to pass levelDataId parameter', function () {
            var template = 'dataUriTemplate', id = 'test';
            var layer = new DataLayer({
                dataUriTemplate: template,
                levelDataId: id
            });
            expect(layer.levelDataId).toEqual(id);
        });
        it('should be set default value if nothing was specified', function () {
            var template = 'dataUriTemplate';
            var layer = new DataLayer({ dataUriTemplate: template });
            expect(layer.levelDataId).toEqual('id');
        });
        it('should extract id properly', function () {
            var template = 'dataUriTemplate';
            var layer = new DataLayer({ dataUriTemplate: template });
            expect(layer.extractId({ id: template })).toEqual(template);
        });
        it('should define processors', function () {
            var template = 'dataUriTemplate';
            var layer = new DataLayer({ dataUriTemplate: template });
            expect(layer.processors).toBeDefined();
            expect(layer.processors.length).toBe(0);
        });
        it('should be able to pass processors', function () {
            var template = 'dataUriTemplate', processor = {};
            var layer = new DataLayer({
                dataUriTemplate: template,
                processors: [processor]
            });
            expect(layer.processors).toBeDefined();
            expect(layer.processors.length).toBe(1);
            expect(layer.processors[0]).toBe(processor);
        });
    });
    describe('obtainData functionality', function () {
        var server;
        beforeEach(function () {
            server = sinon.fakeServer.create();
        });
        afterEach(function () {
            server.restore();
        });
        it('should define obtainData', function () {
            var template = 'dataUriTemplate';
            var layer = new DataLayer({ dataUriTemplate: template });
            expect(typeof layer.obtainData).toBe('function');
        });
        it('should return promise', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                return data;
            };
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray
            });
            server.respondWith('[1,2,3,4,5]');
            var result = layer.obtainData({});
            server.respond();
            expect(result).toBeDefined();
            expect(typeof result.done).toEqual('function');
            expect(typeof result.fail).toEqual('function');
        });
        it('should return data', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, data = [
                    { id: 'a' },
                    { id: 'b' },
                    { id: 'c' }
                ], dataSize = data.length, dataConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray
            });
            server.respondWith(JSON.stringify(data));
            var result = layer.obtainData({}).done(dataConsumer);
            server.respond();
            expect(dataConsumer.called).toBeTruthy();
            expect(dataConsumer.args[0][0].total).toEqual(dataSize);
        });
        it('should call error handler on error', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, dataConsumer = sinon.spy(), errorConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray
            });
            layer.obtainData({}).done(dataConsumer).fail(errorConsumer);
            server.respond();
            expect(dataConsumer.called).not.toBeTruthy();
            expect(errorConsumer.called).toBeTruthy();
        });
        it('should define data in proper format', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, data = [
                    { id: 'a' },
                    { id: 'b' }
                ], dataSize = data.length, dataConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray
            });
            server.respondWith(JSON.stringify(data));
            layer.obtainData({}).done(dataConsumer);
            server.respond();
            var processedObject = dataConsumer.args[0][0];
            expect(processedObject.total).toEqual(dataSize);
            expect(processedObject.data.length).toEqual(dataSize);
            for (var i = 0; i < processedObject.data.length; i++) {
                expect(processedObject.data[i].id).toEqual(data[i].id);
                expect(processedObject.data[i].value).toEqual(data[i]);
            }
        });
        it('should invoke processors', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, data = [
                    { id: 'a' },
                    { id: 'b' }
                ], processor = {
                    processItem: function (item) {
                        item.processed = true;
                        return item;
                    }
                }, dataConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray,
                processors: [processor]
            });
            server.respondWith(JSON.stringify(data));
            layer.obtainData({}).done(dataConsumer);
            server.respond();
            var processedObject = dataConsumer.args[0][0];
            for (var i = 0; i < processedObject.data.length; i++) {
                expect(processedObject.data[i].processed).toBeTruthy();
            }
        });
        it('should invoke processors and be able to filter not required items', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, data = [
                    { id: 'a' },
                    { id: 'b' }
                ], dataSize = data.length, processor = {
                    processItem: function (item) {
                        if (item.id !== 'b') {
                            return item;
                        }
                    }
                }, dataConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray,
                processors: [processor]
            });
            server.respondWith(JSON.stringify(data));
            layer.obtainData({}).done(dataConsumer);
            server.respond();
            var processedObject = dataConsumer.args[0][0];
            expect(processedObject.total).toEqual(dataSize);
            expect(processedObject.data.length).toEqual(dataSize - 1);
            for (var i = 0; i < processedObject.data.length; i++) {
                expect(processedObject.data[i].id).not.toEqual('b');
            }
        });
        it('should return predefined data', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, data = [
                    { id: 'a' },
                    { id: 'b' },
                    { id: 'c' }
                ], dataSize = data.length, dataConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray
            });
            layer.predefinedData = { '/': data };
            layer.obtainData({ id: '/' }).done(dataConsumer);
            expect(dataConsumer.called).toBeTruthy();
            expect(dataConsumer.args[0][0].total).toEqual(dataSize);
            var processedObject = dataConsumer.args[0][0];
            expect(processedObject.total).toEqual(dataSize);
            expect(processedObject.data.length).toEqual(dataSize);
            for (var i = 0; i < processedObject.data.length; i++) {
                expect(processedObject.data[i]).toEqual(data[i]);
            }
        });
        it('should not process predefined data', function () {
            var template = 'dataUriTemplate', getDataArray = function (data) {
                    return data;
                }, data = [
                    { id: 'a' },
                    { id: 'b' },
                    { id: 'c' }
                ], processor = { processItem: sinon.spy() }, dataConsumer = sinon.spy();
            var layer = new DataLayer({
                dataUriTemplate: template,
                getDataArray: getDataArray,
                processors: [processor]
            });
            layer.predefinedData = { '/': data };
            layer.obtainData({ id: '/' }).done(dataConsumer);
            expect(processor.processItem.called).not.toBeTruthy();
        });
    });
});