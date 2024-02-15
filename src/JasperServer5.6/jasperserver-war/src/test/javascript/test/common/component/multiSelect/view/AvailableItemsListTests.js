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
 * @version: $Id: AvailableItemsListTests.js 47864 2014-08-06 13:47:07Z sergey.prilukin $
 */

define([
    "jquery",
    "underscore",
    "core.layout",
    "common/component/multiSelect/view/AvailableItemsList",
    "text!templates/availableItemsListTemplate.htm",
    "text!templates/availableItemsTemplate.htm",
    "text!templates/single.select.list.htm"
    ],

    function ($, _, layoutModule, AvailableItemsList, availableItemsListTemplate, availableItemsTemplate, singleSelectListTemplate) {

        var availableItemsList, dropDownPart;

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

            beforeEach(function(){
                availableItemsList = new AvailableItemsList({
                    el: $("<div id=\"availableItemsListPlaceHolder\"></div>"),
                    template: availableItemsListTemplate,
                    listElement: $(singleSelectListTemplate),
                    //chunksTemplate: singleSelectListChunksTemplate,
                    itemsTemplate: availableItemsTemplate,
                    label: "List with Search",
                    getData: listGetDataFactory(),
                    value: {2: "3"},
                    scrollTimeout: -1
                }).renderData();

                dropDownPart = availableItemsList.$dropDownEl;
                $("body").append(availableItemsList.el);
            });

            afterEach(function(){
                availableItemsList.remove();
                $("#availableItemsListPlaceHolder").remove();
            });

            var _expand = function() {
                //Expand
                var input = availableItemsList.$el.find(".mSelect-input");
                var event = jQuery.Event("click");
                input.trigger(event);
                availableItemsList.onFocus();
                availableItemsList.$el.find(".sList").trigger("scroll");
            };

            it("should return initial value in getValue if it was not changed", function(){
                var value = _.compact(availableItemsList.getValue());
                expect(value[0]).toEqual("3");
            });

            it("should expand component on focus", function(){
                var input = availableItemsList.$el.find(".mSelect-input");
                var event = jQuery.Event("focus");
                input.trigger(event);

                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).toHasClass("expanded");
                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).not.toHasClass("collapsed");
                expect(dropDownPart.css("display")).toEqual("block");
            });

            it("should expand component on click on textbox", function(){
                _expand();

                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).toHasClass("expanded");
                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).not.toHasClass("collapsed");
                expect(dropDownPart.css("display")).toEqual("block");
            });

            xit("should change selection on mousedown", function(){});

            xit("should deselect item and trigger selection change on mousedown on selected element", function(){});

            xit("should toggle selection on mousedown with ctrl", function(){});

            xit("should toggle selection on mousedown with command", function(){});

            xit("should select range on mousedown with shift", function(){});

            it("should collapse element on focus lost", function(){
                _expand();

                var input = availableItemsList.$el.find(".mSelect-input");
                var event = jQuery.Event("blur");
                input.trigger(event);

                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).toHasClass("collapsed");
                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).not.toHasClass("expanded");
                expect(dropDownPart.css("display")).toEqual("none");
            });

            it("should collapse element on clock on close button", function(){
                _expand();

                var button = dropDownPart.find(".mSelect-footer button");
                var event = jQuery.Event("click");
                button.trigger(event);

                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).toHasClass("collapsed");
                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).not.toHasClass("expanded");
                expect(dropDownPart.css("display")).toEqual("none");
            });

            it("should not collapse element on click on items", function(){
                _expand();

                //regular part
                var input = availableItemsList.$el;
                var event = jQuery.Event("mousedown");
                event.which = 1;
                input.trigger(event);

                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).toHasClass("expanded");
                expect(dropDownPart.css("display")).toEqual("block");

                //dropdown part
                var event = jQuery.Event("mousedown");
                event.which = 1;
                dropDownPart.trigger(event);

                expect(availableItemsList.$el.find(".mSelect-avListPlaceholder")).toHasClass("expanded");
                expect(dropDownPart.css("display")).toEqual("block");
            });

            it("should select all items on click selectAll", function(){
                _expand();

                var selectAllSpy = sinon.spy(availableItemsList.listView, "selectAll");

                //dropdown part
                var event = jQuery.Event("click");
                event.which = 1;
                dropDownPart.find("a.all").trigger(event);

                expect(selectAllSpy.calledOnce).toBeTruthy();

                selectAllSpy.restore();
            });

            it("should select none on click selectNone", function(){
                _expand();

                var spy = sinon.spy(availableItemsList.listView, "selectNone");

                //dropdown part
                var event = jQuery.Event("click");
                event.which = 1;
                dropDownPart.find("a.none").trigger(event);

                expect(spy.calledOnce).toBeTruthy();

                spy.restore();
            });

            it("should invert selection on click invertSelection", function(){
                _expand();

                var spy = sinon.spy(availableItemsList.listView, "invertSelection");

                //dropdown part
                var event = jQuery.Event("click");
                event.which = 1;
                dropDownPart.find("a.invert").trigger(event);

                expect(spy.calledOnce).toBeTruthy();

                spy.restore();
            });

            /* Keyboard events */

            xit("should select range on KEYDOWN+SHIFT", function(){});

            xit("should select range on KEYUP+SHIFT", function(){});

            xit("should select range on PAGEDOWN+SHIFT", function(){});

            xit("should select range on PAGEUP+SHIFT", function(){});

            xit("should select item on ENTER", function(){});

            xit("should toggle item on CTRL+ENTER", function(){});

            xit("should toggle item on COMMAND+ENTER", function(){});

            xit("should select range SHIFT+ENTER", function(){});
        });
});
