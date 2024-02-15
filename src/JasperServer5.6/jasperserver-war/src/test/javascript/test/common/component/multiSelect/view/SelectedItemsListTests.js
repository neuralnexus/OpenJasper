/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: SelectedItemsListTests.js 47864 2014-08-06 13:47:07Z sergey.prilukin $
 */

define([
    "jquery",
    "underscore",
    "common/component/multiSelect/view/SelectedItemsList",
    "text!templates/availableItemsTemplate.htm",
    "text!templates/single.select.list.htm"
    ],

function ($, _, SelectedItemsList, selectedItemsTemplate, singleSelectListTemplate) {
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

        beforeEach(function(){
            selectedItemsList = new SelectedItemsList({
                el: $("<div id=\"selectedItemsListPlaceHolder\"></div>"),
                listElement: $(singleSelectListTemplate),
                itemsTemplate: selectedItemsTemplate,
                getData: listGetDataFactory(),
                scrollTimeout: -1
            }).renderData();

            $("body").append(selectedItemsList.el);
        });

        afterEach(function(){
            selectedItemsList.remove();
            $("#selectedItemsListPlaceHolder").remove();
        });

        it("should trigger selection:remove on del key", function(){
            var selection = null;
            selectedItemsList.once("selection:remove", function(val) {
                selection = val;
            });

            //Mousedown on component
            event = jQuery.Event("mousedown");
            event.which = 1;
            selectedItemsList.$el.find("li:eq(2)").trigger(event);

            var event = jQuery.Event("keydown");
            event.which = 8;
            selectedItemsList.$el.find("input").trigger(event);

            expect(selection[2]).toEqual("3");
        });

        it("should select all items on CTRL+A", function(){
            var selection = null;
            selectedItemsList.once("selection:remove", function(val) {
                selection = val;
            });

            //Press CTRL+A
            event = jQuery.Event("keydown");
            event.which = 65;
            event.ctrlKey = true;
            selectedItemsList.$el.find("input").trigger(event);

            event = jQuery.Event("keydown");
            event.which = 8;
            selectedItemsList.$el.find("input").trigger(event);

            expect(selection.length).toEqual(8500);
            expect(selection[0]).toEqual("1");
            expect(selection[8499]).toEqual("8500");
        });
    });
});