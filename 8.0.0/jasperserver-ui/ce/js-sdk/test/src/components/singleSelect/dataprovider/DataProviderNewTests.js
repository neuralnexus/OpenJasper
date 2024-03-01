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
import $ from 'jquery';
import DataProvider from 'src/components/singleSelect/dataprovider/DataProviderNew';
var listGetDataFactory = function (options) {
    options = options || {};
    var MAX_TOTAL = 8500;
    var total = MAX_TOTAL, dfd = options.deferred;
    var getDataSegment = function (first, last, criteria) {
        last = Math.min(last, total - 1);
        var result = [];
        for (var i = first; i <= last; i++) {
            var val = '' + (i + 1);
            result.push({
                label: val + (criteria ? criteria : ''),
                value: val
            });
        }
        return result;
    };
    var getData = function (options) {
        var offset = options ? options.offset || 0 : 0;
        var limit = options ? options.limit || total : total;
        var data = getDataSegment(offset, offset + limit - 1, options.criteria);
        var deferred = dfd || new $.Deferred();
        deferred.resolve({
            data: data,
            total: total
        });
        return deferred.promise();
    };
    total = options && options.total || MAX_TOTAL;
    return getData;
};
var request;
var dataProvider;
var data;
describe('New DataProvider', function () {
    beforeEach(function () {
        request = sinon.spy(listGetDataFactory());
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            saveLastCriteria: false,
            serialRequestsDelay: 0
        });
    });
    it('should query one page then inner request fits to one page', function () {
        dataProvider.getData({
            offset: 0,
            limit: 5
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        sinon.assert.calledWith(request, {
            offset: 0,
            limit: 10
        });
        expect(data.total).toEqual(8500);
        expect(data.data.length).toEqual(5);
        expect(data.data[0].label).toEqual('1');
        expect(data.data[4].label).toEqual('5');
    });
    it('should not query if data is already cached', function () {
        dataProvider.getData({
            offset: 0,
            limit: 5
        });
        var data;
        dataProvider.getData({
            offset: 5,
            limit: 5
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        sinon.assert.calledWith(request, {
            offset: 0,
            limit: 10
        });
        expect(data.total).toEqual(8500);
        expect(data.data.length).toEqual(5);
        expect(data.data[0].label).toEqual('6');
        expect(data.data[4].label).toEqual('10');
    });
    it('should query if request was rejected', function () {
        var deferred = new $.Deferred();
        request = sinon.spy(listGetDataFactory({ deferred: deferred }));
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            saveLastCriteria: false,
            serialRequestsDelay: 0
        });
        deferred.reject();
        dataProvider.getData({
            offset: 0,
            limit: 5
        });
        dataProvider.getData({
            offset: 0,
            limit: 5
        });
        expect(request).toHaveBeenCalledTwice();
    });
    it('should query all data if no limit and offset params are specified', function () {
        request = sinon.spy(listGetDataFactory({ total: 10 }));
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            saveLastCriteria: false,
            serialRequestsDelay: 0
        });
        dataProvider.getData({});
        var data;
        dataProvider.getData({}).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        expect(data.total).toEqual(10);
        expect(data.data.length).toEqual(10);
        expect(data.data[0].label).toEqual('1');
        expect(data.data[9].label).toEqual('10');
    });
    it('should query data if clear was called', function () {
        dataProvider.getData({
            offset: 0,
            limit: 5
        });
        dataProvider.clear();
        var data;
        dataProvider.getData({
            offset: 0,
            limit: 5
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledTwice(request);
    });
    it('should call only one page if start is on one page and end is on second and one of pages are cached', function () {
        dataProvider.getData({
            offset: 0,
            limit: 5
        });
        var data;
        dataProvider.getData({
            offset: 5,
            limit: 10
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledTwice(request);
        request.secondCall.calledWith({
            offset: 10,
            limit: 10
        });
        expect(data.total).toEqual(8500);
        expect(data.data.length).toEqual(10);
        expect(data.data[0].label).toEqual('6');
        expect(data.data[9].label).toEqual('15');
    });
    it('should call all pages if start is in one page and end is in second and none of pages are cached', function () {
        dataProvider.getData({
            offset: 5,
            limit: 10
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        request.calledWith({
            offset: 0,
            limit: 20
        });
        expect(data.total).toEqual(8500);
        expect(data.data.length).toEqual(10);
        expect(data.data[0].label).toEqual('6');
        expect(data.data[9].label).toEqual('15');
    });
    it('should search through criteria', function () {
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test'
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        request.calledWith({
            offset: 0,
            limit: 20,
            criteria: 'test'
        });
        expect(data.total).toEqual(8500);
        expect(data.data.length).toEqual(10);
        expect(data.data[0].label).toEqual('6test');
        expect(data.data[9].label).toEqual('15test');
    });
    it('should be able to change search criteria key', function () {
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            saveLastCriteria: false,
            serialRequestsDelay: 0,
            searchKey: 'customSearchKey'
        });
        dataProvider.getData({
            offset: 5,
            limit: 10,
            customSearchKey: 'test'
        });
        sinon.assert.calledOnce(request);
        request.calledWith({
            offset: 0,
            limit: 20,
            customSearchKey: 'test'
        });
    });
    it('should not request cached data when search through criteria', function () {
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 0,
            limit: 5,
            criteria: 'test'
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        expect(data.data.length).toEqual(5);
        expect(data.data[0].label).toEqual('1test');
        expect(data.data[4].label).toEqual('5test');
    });
    it('should cache up to 3 search terms', function () {
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test1'
        });
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test2'
        });
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test3'
        });
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test2'
        });
        expect(request.callCount).toEqual(5);
    });
    it('should use same pageSize when search through criteria', function () {
        dataProvider.getData({
            offset: 5,
            limit: 10,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 0,
            limit: 20,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 20,
            limit: 1,
            criteria: 'test'
        });
        sinon.assert.calledTwice(request);
        request.secondCall.calledWith({
            offset: 20,
            limit: 10,
            criteria: 'test'
        });
    });
    it('should use same dataconverter when search through criteria', function () {
        var dataConverter = sinon.spy(function (item) {
            return item;
        });
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            dataConverter: dataConverter,
            serialRequestsDelay: 0
        });
        dataProvider.getData({
            offset: 0,
            limit: 5,
            criteria: 'test'
        });
        expect(dataConverter.callCount).toEqual(10);
    });
    it('should reuse last search term when search through criteria and saveLastCriteria = true', function () {
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            saveLastCriteria: true,
            serialRequestsDelay: 0
        });
        dataProvider.getData({
            offset: 0,
            limit: 5,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 5,
            limit: 5
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledOnce(request);
        expect(data.data.length).toEqual(5);
        expect(data.data[0].label).toEqual('6test');
        expect(data.data[4].label).toEqual('10test');
    });
    it('should clear last search term when search through criteria = \'\' and saveLastCriteria = true', function () {
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            saveLastCriteria: true,
            serialRequestsDelay: 0
        });
        dataProvider.getData({
            offset: 0,
            limit: 5,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 5,
            limit: 5,
            criteria: ''
        });
        dataProvider.getData({
            offset: 5,
            limit: 5
        }).done(function (result) {
            data = result;
        });
        sinon.assert.calledTwice(request);
        expect(data.data.length).toEqual(5);
        expect(data.data[0].label).toEqual('6');
        expect(data.data[4].label).toEqual('10');
    });
    it('should add skipGetTotal param when controlGetTotal set to true', function () {
        dataProvider = new DataProvider({
            request: request,
            pageSize: 10,
            maxSearchCacheSize: 3,
            serialRequestsDelay: 0,
            controlGetTotal: true
        });
        dataProvider.getData({
            offset: 0,
            limit: 5,
            criteria: 'test'
        });
        dataProvider.getData({
            offset: 10,
            limit: 5,
            criteria: 'test'
        });
        expect(request.secondCall.args[0].skipGetTotal).toBe(true);
    });
    it('should add custom params when call original request', function () {
        dataProvider.getData({
            offset: 0,
            limit: 5,
            someParam: 'value'
        });
        dataProvider.getData({
            offset: 10,
            limit: 5,
            criteria: 'test',
            someParam2: 'value2'
        });
        expect(request.firstCall.args[0].someParam).toEqual('value');
        expect(request.secondCall.args[0].someParam2).toEqual('value2');
    });
});