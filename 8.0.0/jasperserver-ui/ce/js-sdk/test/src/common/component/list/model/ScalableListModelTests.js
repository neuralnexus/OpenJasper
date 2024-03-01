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
import Model from 'src/common/component/list/model/ScalableListModel';
import $ from 'jquery';

var listGetDataFactory = function(options) {
    var MAX_TOTAL = 8500;

    var total = MAX_TOTAL;

    var getDataSegment = function(first, last) {
        last = Math.min(last + 1, total);

        var result = [];

        for (var i = first + 1; i <= last; i++) {
            var val = "" + i;
            result.push({label: val, value: val})
        }

        return result;
    };

    var getData = function(options) {
        var offset = options ? options.offset || 0 : 0;
        var limit = options ? options.limit || total : total;

        var data = getDataSegment(offset, offset + limit);

        var deferred = new $.Deferred();
        deferred.resolve({data: data, total: total});

        return deferred.promise();
    };

    total = (options && options.total) || MAX_TOTAL;

    return getData;
};

describe("ScalableListModel", function () {

    var model, getDataSpy;

    beforeEach(function(){
        getDataSpy = sinon.spy(listGetDataFactory());
        model = new Model({
            getData: getDataSpy
        });
    });

    afterEach(function(){
    });

    it("should call get data with correct limit and offset in case if top and bottom equals to buffer size", function(){
        model.fetch({
            top: 0,
            bottom: 99
        });

        sinon.assert.calledWith(getDataSpy, {offset: 0, limit: 100});
    });

    it("should call get data with correct limit and offset in case if fetch close to the beginning", function(){
        model.fetch({
            top: 5,
            bottom: 15
        });

        sinon.assert.calledWith(getDataSpy, {offset: 0, limit: 100});
    });

    it("should call get data with correct limit and offset in case if fetch in the middle of the list", function(){
        model.fetch({
            top: 695,
            bottom: 705
        });

        //offset = (695 + (705 - 695)/2) - 50 = 650
        //limit = 100
        sinon.assert.calledWith(getDataSpy, {offset: 650, limit: 100});
    });

    it("should call normalized get data when fetch at the end of the list and total is present", function(){
        model.fetch(); //to get total

        model.fetch({
            top: 8450,
            bottom: 8500
        });

        //offset = (8450 + (8500 - 8450)/2) - 50 = 8425
        //limit = min(100, 8499-8425+1) = 75
        sinon.assert.calledWith(getDataSpy, {offset: 8425, limit: 75});
    });

    it("should call not normalized get data when fetch out of the bounds and total is not present", function(){
        model.fetch({
            top: 8450,
            bottom: 8550
        });

        sinon.assert.calledWith(getDataSpy, {offset: 8450, limit: 100});
    });

    it("should normalize bufferEndIndex when fetch out of the bounds and total is not present", function(){
        model.fetch({
            top: 8450,
            bottom: 8550
        });

        expect(model.get("bufferEndIndex")).toEqual(8499);
    });

    it("should not call get data when fetch called without any params", function(){
        model.fetch({
            top: 8450,
            bottom: 8550
        });

        model.fetch({});

        sinon.assert.calledOnce(getDataSpy);
        expect(model.get("bufferStartIndex")).toEqual(8450);
        expect(model.get("bufferEndIndex")).toEqual(8499);
    });

    it("should recalc buffer boundaries when they are already set and fetch called with different top and bottom than calculated in previous fetch", function(){
        model.fetch({
            top: 8450,
            bottom: 8550
        });

        model.fetch({
            top: 8450,
            bottom: 8550
        });

        sinon.assert.calledWith(getDataSpy, {offset: 8450, limit: 50});
        expect(model.get("bufferStartIndex")).toEqual(8450);
        expect(model.get("bufferEndIndex")).toEqual(8499);
    });

    it("should recalc buffer boundaries when they are already set and fetch called with force flag", function(){
        model.fetch({
            top: 8450,
            bottom: 8550
        });

        model.fetch({
            force: true
        });

        //offset = (8450 + (8499 - 8450)/2) - 50 = 8424
        //limit = (8424+100-1) - 8424 + 1 = 100
        sinon.assert.calledWith(getDataSpy, {offset: 8424, limit: 100});
        expect(model.get("bufferStartIndex")).toEqual(8424);
        expect(model.get("bufferEndIndex")).toEqual(8499);
    });

    it("should not trust getData's total value", function(){
        var getData = function(options) {
            var deferred = new $.Deferred();
            deferred.resolve({
                data: [{value: "test"}],
                total: 2
            });

            return deferred.promise();
        };

        model = new Model({
            getData: getData
        });

        model.fetch();

        expect(model.get("bufferStartIndex")).toEqual(0);
        expect(model.get("bufferEndIndex")).toEqual(0);
        expect(model.get("total")).toEqual(1);
    });

    it("should recover bufferEndIndex during fetch if it was less than bufferSize because of previous requests", function(){
        var defaultGetData = listGetDataFactory(),
            returnZero = true,
            getData = function(options) {

                if (returnZero) {
                    var deferred = new $.Deferred();
                    deferred.resolve({
                        data: [],
                        total: 0
                    });

                    return deferred.promise();
                } else {
                    return defaultGetData(options);
                }
            };

        model = new Model({
            getData: getData
        });

        //First fetch will retrieve empty data, thus bufferEndIndex should be 0
        model.fetch();

        expect(model.get("bufferStartIndex")).toEqual(0);
        expect(model.get("bufferEndIndex")).toEqual(0);
        expect(model.get("total")).toEqual(0);

        //second fetch will retrieve normal data amout thus bufferendIndex should be recovered
        returnZero = false;
        model.fetch({force: true});

        expect(model.get("bufferStartIndex")).toEqual(0);
        expect(model.get("bufferEndIndex")).toEqual(99);
        expect(model.get("total")).toEqual(8500);
    });
});