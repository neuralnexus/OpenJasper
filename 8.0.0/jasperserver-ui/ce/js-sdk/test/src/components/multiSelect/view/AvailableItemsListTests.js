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
import _ from 'underscore';
import AvailableItemsList from 'src/components/multiSelect/view/AvailableItemsList';

var availableItemsList;

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

describe("AvailableItemsList", function () {
    var sandbox;

    beforeEach(function(){
        availableItemsList = new AvailableItemsList({
            el: $("<div id=\"availableItemsListPlaceHolder\"></div>"),
            label: "List with Search",
            getData: listGetDataFactory(),
            value: {2: "3"},
            scrollTimeout: -1
        }).renderData();

        sandbox = sinon.createSandbox();

        $("body").append(availableItemsList.el);
    });

    afterEach(function(){
        sandbox.restore();
        availableItemsList.remove();
        $("#availableItemsListPlaceHolder").remove();
    });

    it("should return initial value in getValue if it was not changed", function(){
        var value = _.compact(availableItemsList.getValue());
        expect(value[0]).toEqual("3");
    });

    it("should select all items on click selectAll", function(){
        var selectAllSpy = sandbox.spy(availableItemsList.listView, "selectAll");

        //dropdown part
        var event = $.Event("click");
        event.which = 1;
        availableItemsList.$el.find(".jr-jSelectAll").trigger(event);

        expect(selectAllSpy.calledOnce).toBeTruthy();
    });

    it("should select none on click selectNone", function(){
        var spy = sandbox.spy(availableItemsList.listView, "selectNone");

        //dropdown part
        var event = $.Event("click");
        event.which = 1;
        availableItemsList.$el.find(".jr-jSelectNone").trigger(event);

        expect(spy.calledOnce).toBeTruthy();
    });

    it("should invert selection on click invertSelection", function(){
        var spy = sandbox.spy(availableItemsList.listView, "invertSelection");

        //dropdown part
        var event = $.Event("click");
        event.which = 1;
        availableItemsList.$el.find(".jr-jInvert").trigger(event);

        expect(spy.calledOnce).toBeTruthy();
    });

    it("should activate previous item on up key press", function() {
        var event = {};

        var getActiveValueStub = sandbox.stub(availableItemsList.listView, "getActiveValue"),
            activatePreviousStub = sandbox.stub(availableItemsList.listView, "activatePrevious");

        getActiveValueStub.onCall(0).returns({index: 1});
        getActiveValueStub.onCall(1).returns({index: 0});

        availableItemsList.onUpKey(event);

        expect(availableItemsList.activeChangedWithShift).toBeFalsy();

        expect(activatePreviousStub).toHaveBeenCalled();
        expect(getActiveValueStub).toHaveBeenCalledTwice();
    });

    it("should activate last item on up key press if first item is active", function() {
        var event = {
            shiftKey: true
        };

        var getActiveValueStub = sandbox.stub(availableItemsList.listView, "getActiveValue"),
            activatePreviousStub = sandbox.stub(availableItemsList.listView, "activatePrevious"),
            activateLastStub = sandbox.stub(availableItemsList.listView, "activateLast");

        getActiveValueStub.onCall(0).returns({index: 0});
        getActiveValueStub.onCall(1).returns({index: 0});

        availableItemsList.onUpKey(event);

        expect(availableItemsList.activeChangedWithShift).toBeTruthy();

        expect(activatePreviousStub).toHaveBeenCalled();
        expect(activateLastStub).toHaveBeenCalled();
        expect(getActiveValueStub).toHaveBeenCalledTwice();
    });

    it("should activate last item on up key press if no item is active", function() {
        var event = {};

        var getActiveValueStub = sandbox.stub(availableItemsList.listView, "getActiveValue"),
            activatePreviousStub = sandbox.stub(availableItemsList.listView, "activatePrevious"),
            activateLastStub = sandbox.stub(availableItemsList.listView, "activateLast");

        getActiveValueStub.onCall(0).returns();

        availableItemsList.onUpKey(event);

        expect(activatePreviousStub).not.toHaveBeenCalled();
        expect(activateLastStub).toHaveBeenCalled();
        expect(getActiveValueStub).toHaveBeenCalled();
    });

    it("should activate next item on down key press", function() {
        var event = {};

        var getActiveValueStub = sandbox.stub(availableItemsList.listView, "getActiveValue"),
            activateNextStub = sandbox.stub(availableItemsList.listView, "activateNext");

        getActiveValueStub.onCall(0).returns({index: 0});
        getActiveValueStub.onCall(1).returns({index: 1});

        availableItemsList.onDownKey(event);

        expect(availableItemsList.activeChangedWithShift).toBeFalsy();


        expect(activateNextStub).toHaveBeenCalled();
        expect(getActiveValueStub).toHaveBeenCalled();
    });

    it("should activate first item on down key press if active item is last", function() {
        var event = {
            shiftKey: true
        };

        var getActiveValueStub = sandbox.stub(availableItemsList.listView, "getActiveValue"),
            activateNextStub = sandbox.stub(availableItemsList.listView, "activateNext"),
            activateFirstStub = sandbox.stub(availableItemsList.listView, "activateFirst");

        getActiveValueStub.onCall(0).returns({index: 1});
        getActiveValueStub.onCall(1).returns({index: 1});

        availableItemsList.onDownKey(event);

        expect(availableItemsList.activeChangedWithShift).toBeTruthy();

        expect(activateNextStub).toHaveBeenCalled();
        expect(activateFirstStub).toHaveBeenCalled();
        expect(getActiveValueStub).toHaveBeenCalled();
    });

    it("should activate first item on down key press if no item is active", function() {
        var event = {};

        var getActiveValueStub = sandbox.stub(availableItemsList.listView, "getActiveValue"),
            activateNextStub = sandbox.stub(availableItemsList.listView, "activateNext"),
            activateFirstStub = sandbox.stub(availableItemsList.listView, "activateFirst");

        getActiveValueStub.onCall(0).returns();

        availableItemsList.onDownKey(event);

        expect(activateNextStub).not.toHaveBeenCalled();
        expect(activateFirstStub).toHaveBeenCalled();
        expect(getActiveValueStub).toHaveBeenCalled();
    });

    it("should trigger listRenderError", function() {
        var triggerSpy = sandbox.spy(availableItemsList, "trigger");

        availableItemsList.listView.trigger("listRenderError", "200", "error");

        expect(triggerSpy).toHaveBeenCalledWith("listRenderError", "200", "error");
    });

    it("should call unset totalValues on fetch if search criteria is undefined", function () {
        var fetchSpy = sandbox.spy(availableItemsList.listView, "fetch");

        availableItemsList.model.set("criteria", undefined);

        var modelUnsetStub = sandbox.stub(availableItemsList.model, "unset");

        availableItemsList.fetch();

        expect(fetchSpy).toHaveBeenCalled();
        expect(modelUnsetStub).toHaveBeenCalledWith("totalValues", {
            silent: true
        });
    });

    it("should call unset totalValues on fetch if search criteria is empty", function () {
        var fetchSpy = sandbox.spy(availableItemsList.listView, "fetch");

        availableItemsList.model.set("criteria", "");

        var modelUnsetStub = sandbox.stub(availableItemsList.model, "unset");

        availableItemsList.fetch();

        expect(fetchSpy).toHaveBeenCalled();
        expect(modelUnsetStub).toHaveBeenCalledWith("totalValues", {
            silent: true
        });
    });

    it("should not call unset totalValues on fetch if search criteria is present", function () {
        var fetchSpy = sandbox.spy(availableItemsList.listView, "fetch");

        availableItemsList.model.set("criteria", "searchQuery");

        var modelUnsetStub = sandbox.stub(availableItemsList.model, "unset");

        availableItemsList.fetch();

        expect(fetchSpy).toHaveBeenCalled();
        expect(modelUnsetStub).not.toHaveBeenCalled();
    });
    it("should select none on click selectNone and set criteria to empty", function(){
        var spy = sandbox.spy(availableItemsList.listView, "selectNone");
        availableItemsList.model.set("value", ["param1"])
        availableItemsList.model.set("criteria", "searchQuery");
        var clearFilterSpy = sandbox.spy(availableItemsList,'clearFilter')

        //dropdown part
        var event = $.Event("click");
        event.which = 1;
        availableItemsList.$el.find(".jr-jSelectNone").trigger(event);

        expect(spy.calledOnce).toBeTruthy();
        expect(clearFilterSpy).toHaveBeenCalled();
        expect(availableItemsList.model.get('criteria')).toEqual('')
        expect(availableItemsList.model.get('value')).toEqual({})
    });
});
