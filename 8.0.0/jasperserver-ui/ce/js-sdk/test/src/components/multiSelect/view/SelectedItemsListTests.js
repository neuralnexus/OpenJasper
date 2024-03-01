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
import Backbone from 'backbone';
import SelectedItemsList from 'src/components/multiSelect/view/SelectedItemsList';

var selectedItemsList;

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

describe("SelectedItemsList", function () {
    var sandbox,
        listView,
        listViewModel;

    beforeEach(function(){
        sandbox = sinon.createSandbox();

        selectedItemsList = new SelectedItemsList({
            el: $("<div id=\"selectedItemsListPlaceHolder\"></div>"),
            getData: listGetDataFactory(),
            scrollTimeout: -1
        }).renderData();

        $("body").append(selectedItemsList.el);

        listView = _.extend({
            el: $("<div></div>"),
            undelegateEvents: sandbox.stub(),
            delegateEvents: sandbox.stub(),
            render: sandbox.stub(),
            resize: sandbox.stub(),
            remove: sandbox.stub(),
            scrollTo: sandbox.stub()
        }, Backbone.Events);

        listViewModel = _.extend({
            clearSelection: sandbox.stub()
        }, Backbone.Events);
    });

    afterEach(function(){
        sandbox.restore();
        selectedItemsList.remove();
        $("#selectedItemsListPlaceHolder").remove();
    });

    it("should trigger selection:remove on click on cross sign", function(){
        var selection = null;
        selectedItemsList.once("selection:remove", function(val) {
            selection = val;
        });

        //first of all select some element
        //in real browser selection should be triggered by mousedown event,
        //but we do not care here about how selection was made.
        selectedItemsList.listView.setValue("3");

        //Mouseup on cross sign
        var event = $.Event("mouseup");
        event.which = 1;
        selectedItemsList.$el.find("li:eq(2) .jr-mSelectlist-item-delete").trigger(event);

        expect(selection[2]).toEqual("3");
    });

    it("should trigger selection:remove on del key", function(){
        var selection = null;
        selectedItemsList.once("selection:remove", function(val) {
            selection = val;
        });

        //Mousedown on component
        var event = $.Event("mousedown");
        event.which = 1;
        selectedItemsList.$el.find("li:eq(2)").trigger(event);

        event = $.Event("keydown");
        event.which = 8;
        selectedItemsList.$el.find("input").trigger(event);

        expect(selection[2]).toEqual("3");
    });

    it("should scroll to item on its activation", function() {
        selectedItemsList = new SelectedItemsList({
            listView: listView,
            el: $("<div id=\"selectedItemsListPlaceHolder\"></div>"),
            getData: listGetDataFactory(),
            scrollTimeout: -1
        });

        listView.trigger("active:changed", {
            index: 5
        });

        expect(listView.scrollTo).toHaveBeenCalledWith(5);
    });

    it("should clear selection if there's no item on activation", function() {
        selectedItemsList = new SelectedItemsList({
            listView: listView,
            listViewModel: listViewModel,
            el: $("<div id=\"selectedItemsListPlaceHolder\"></div>"),
            getData: listGetDataFactory(),
            scrollTimeout: -1
        });

        listView.trigger("active:changed");

        expect(listViewModel.clearSelection).toHaveBeenCalled();
    });
});