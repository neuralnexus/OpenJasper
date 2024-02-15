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
 * @version: $Id: SingleSelectTests.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

define([
    "jquery",
    "underscore",
    "core.layout",
    "common/component/singleSelect/view/SingleSelectNew",
    "common/component/singleSelect/dataprovider/DataProviderNew",
    "text!templates/single.select.htm",
    "text!templates/single.select.list.htm",
    "text!templates/single.select.list.items.htm"
    ],

    function ($, _, layoutModule, SingleSelect, DataProvider, singleSelectTemplate, singleSelectListTemplate, singleSelectListItemsTemplate) {

        var sList;

        var listGetDataFactory = function(options) {
            var MAX_TOTAL = 8500;

            var total = MAX_TOTAL;

            var getDataSegment = function(first, last, criteria) {
                last = Math.min(last, total - 1);

                var result = [];

                for (var i = first; i <= last; i++) {
                    var val = "" + (i + 1);
                    result.push({label: val + (criteria ? criteria : ""), value: val + (criteria ? criteria : "")})
                }

                return result;
            };

            var getData = function(options) {
                var offset = options ? options.offset || 0 : 0;
                var limit = options ? options.limit || total : total;

                var data = getDataSegment(offset, offset + limit - 1, options.criteria);

                var deferred = new $.Deferred();
                deferred.resolve({data: data, total: total});

                return deferred.promise();
            };

            total = (options && options.total) || MAX_TOTAL;

            return getData;
        };

        describe("SingleSelect New", function () {

            beforeEach(function(){
                var getData = new DataProvider({
                    request: listGetDataFactory(),
                    //pageSize: 10,
                    //maxSearchCacheSize: 3,
                    //saveLastCriteria: false,
                    serialRequestsDelay: 0
                }).getData;

                singleSelect = new SingleSelect({
                    el: $("<div id=\"singleSelectPlaceHolder\"></div>"),
                    template: singleSelectTemplate,
                    listElement: $(singleSelectListTemplate),
                    //chunksTemplate: singleSelectListChunksTemplate,
                    itemsTemplate: singleSelectListItemsTemplate,
                    label: "List with Search",
                    getData: getData,
                    value: "8500",
                    scrollTimeout: -1,
                    keydownTimeout: -1
                }).renderData();

                sList = $("body").find(".sList");
                $("body").append(singleSelect.el);
            });

            afterEach(function(){
                singleSelect.remove();
                $("#singleSelectPlaceHolder").remove();
                sList.remove();
            });

            var _expand = function() {
                //Expand
                var sSelectInput = singleSelect.$el.find(".sSelect-input");
                var event = jQuery.Event("click");
                sSelectInput.trigger(event);
                singleSelect.onFocus();
                sList.trigger("scroll");
            };

            var _setValue = function(value) {
                singleSelect.setValue(value);
                sList.trigger("scroll");
            };

            it("should return initial value in getValue if it was not changed", function(){
                var value = singleSelect.getValue();
                expect(value).toEqual("8500");
            });

            it("should add expanded class to the component, scroll to selected element if it's already in list model, focus on input on click on collapsed component", function(){
                var hasFocus = false;
                singleSelect.$el.find("input").on("focus", function() {
                    hasFocus = true;
                });

                singleSelect.setValue("30");

                var sSelectInput = singleSelect.$el.find(".sSelect-input");

                var event = jQuery.Event("click");
                sSelectInput.trigger(event);
                sList.trigger("scroll");

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeFalsy();
                expect(sList.scrollTop() > 200).toBeTruthy();
                expect(sList.find("li[data-index='29']")).toHasClass("selected");
                expect(hasFocus).toBeTruthy();
            });

            it("should select item on mousemove", function(){
                var li = sList.find("li:eq(2)");

                var event = jQuery.Event("mousemove");
                li.trigger(event);

                expect(sList.find("li:eq(2)").hasClass("selected")).toBeTruthy();
            });

            it("should select item and trigger selection change on mouseup on element", function(){
                var value = null;
                singleSelect.on("selection:change", function(selection) {
                    value = selection;
                });

                var event = jQuery.Event("mousemove");
                sList.find("li:eq(2)").trigger(event);

                var event = jQuery.Event("mouseup");
                sList.find("li:eq(2)").trigger(event);

                expect(singleSelect.getValue()).toEqual("3");
                expect(value).toEqual("3");
            });

            it("should add collapsed class to the component, do not reset selected value on click on expanded element", function(){

                //Expand
                _expand();

                //Mousemove
                var event = jQuery.Event("mousemove");
                sList.find("li:eq(2)").trigger(event);

                //Collapse
                var sSelectInput = singleSelect.$el.find(".sSelect-input");
                var event = jQuery.Event("click");
                sSelectInput.trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeFalsy();
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
                expect(singleSelect.getValue()).toEqual("8500");
            });

            it("should filter out values on keydown in search input", function(){
                _setValue("1");

                //Expand
                _expand();

                //type value in search
                singleSelect.$el.find("input").val("850");
                var event = jQuery.Event("keydown");
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li").length).toEqual(100);
                expect(sList.find("li:eq(0)").text().replace(/\s/g, "")).toEqual("1850");
            });

            it("should clear filter criteria on selection change", function(){
                _setValue("1");

                //Expand
                _expand();

                //type value in search
                singleSelect.$el.find("input").val("850");
                event = jQuery.Event("keydown");
                singleSelect.$el.find("input").trigger(event);

                //Select second li item
                var event = jQuery.Event("mousemove");
                sList.find("li:eq(2)").trigger(event);

                event = jQuery.Event("mouseup");
                sList.find("li:eq(2)").trigger(event);

                expect(singleSelect.$el.find("input").val()).toEqual("");
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            it("should clear filter criteria on collapse without selection change", function(){
                _setValue("1");

                //Expand
                _expand();

                //type value in search
                singleSelect.$el.find("input").val("850");
                var event = jQuery.Event("keydown");
                singleSelect.$el.find("input").trigger(event);

                //collapse element
                singleSelect.collapse();

                expect(singleSelect.getValue()).toEqual("1");
                expect(sList.find("li").length).toEqual(100);
                expect(singleSelect.$el.find("input").val()).toEqual("");
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            it("should not collapse then mousedown was done on component and mouseup outside component", function(){
                _setValue("1");

                //Expand
                _expand();

                //Mousedown on component
                var event = jQuery.Event("mousedown");
                sList.find("li:eq(2)").trigger(event);

                //Mouseup outside component
                event = jQuery.Event("mouseup");
                $("body").trigger(event);
                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should collapse then mousedown was done outside component", function(){
                _setValue("1");

                //Expand
                _expand();

                //Mousedown on component
                var event = jQuery.Event("mousedown");
                sList.find("li:eq(2)").trigger(event);

                //Mouseup outside component
                event = jQuery.Event("mouseup");
                $("body").trigger(event);
                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();

                //Emulate blur event
                singleSelect.onBlur();

                expect(singleSelect.getValue()).toEqual("1");
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
                expect(singleSelect.$el.find(".sSelect").hasClass("focused")).toBeFalsy();
            });

            it("should scroll to current component's selected element if it's present in result set after filtering out", function(){
                _setValue("32");

                //Expand
                _expand();

                //type value in search
                singleSelect.$el.find("input").val("2");
                var event = jQuery.Event("keydown");
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li[data-index='2']")).toHasClass("selected");
                /*var selectedItemTop = sList.find("li[data-index='19']").offset().top;
                var elTop = sList.offset().top;
                expect(selectedItemTop > elTop).toBeTruthy();
                expect(selectedItemTop < elTop + sList.height()).toBeFalsy();*/
            });

            it("should not fire selection change on mouseup on already selected element but component should collapse", function(){
                _setValue("20");

                //Expand
                _expand();

                var selectionChanged = false;
                singleSelect.once("selection:change", function() {
                    selectionChanged = true;
                });

                //Click on already selected element
                var event = jQuery.Event("mouseup");
                sList.find("li[data-index='19']").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
                expect(selectionChanged).toBeFalsy();
                expect(singleSelect.getValue()).toEqual("20");
            });

            /* keyboard events */

            /* KEY_UP */
            it("should expand component then keyUp is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 38;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should select previous item in component then keyUp is pressed", function(){

                _setValue("5");

                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 38;
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li[data-index='3']")).toHasClass("selected");
            });

            it("should collapse component if keyUp is pressed then first element is selected", function(){
                _setValue("1");

                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 38;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            it("should collapse component if keyUp is pressed then no element is selected", function(){
                _setValue(undefined);

                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 38;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            /* KEY_DOWN */
            it("should expand component then keyDown is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 40;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should select next item in component then keyDown is pressed", function(){

                _setValue("5");

                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 40;
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li[data-index='5']")).toHasClass("selected");
            });

            it("should select next item in component then keyDown is pressed and value was set through API using object notation", function(){

                _setValue(undefined);
                _setValue({"4": "5"});

                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 40;
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li[data-index='5']")).toHasClass("selected");
            });

            xit("should do nothing with component if keyDown is pressed then last element is selected", function(){
                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 35;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
                expect(sList.find("li[data-index='8499']")).toHasClass("selected");
            });

            it("should select first element in component if keyDown is pressed then no element is selected", function(){
                _setValue(undefined);

                //Expand
                _expand();

                var event = jQuery.Event("keydown");
                event.which = 40;
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li[data-index='0']")).toHasClass("selected");
            });

            /* KEY_ENTER */
            it("should expand component then EnterKey is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 13;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should change selection and collapse element then EnterKey is pressed", function(){
                _setValue("1");

                //Expand
                _expand();

                //Select second li item
                var event = jQuery.Event("mousemove");
                sList.find("li:eq(2)").trigger(event);

                event = jQuery.Event("keydown");
                event.which = 13;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.getValue()).toEqual("3");
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            it("should collapse component if no activeValue present and enterKey pressed", function(){
                _setValue(undefined);

                //Expand
                _expand();

                event = jQuery.Event("keydown");
                event.which = 13;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.getValue()).toBeUndefined();
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            /* KEY_ESC */
            it("should not expand component then ESC is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 27;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            it("should collapse component, clear filter and preserve existing value then ESC is pressed on expanded component", function(){
                _setValue("1");

                //Expand
                _expand();

                //type value in search
                singleSelect.$el.find("input").val("850");
                var event = jQuery.Event("keydown");
                singleSelect.$el.find("input").trigger(event);

                //Select second li item
                var event = jQuery.Event("mousemove");
                sList.find("li:eq(2)").trigger(event);

                //Press ESC
                var event = jQuery.Event("keydown");
                event.which = 27;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.getValue()).toEqual("1");
                expect(sList.find("li").length).toEqual(100);
                expect(singleSelect.$el.find("input").val()).toEqual("");
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
            });

            /* KEY_HOME */
            it("should expand component then HOME KEY is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 36;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should select first element then HOME key is pressed on expanded component", function(){
                _setValue("20");

                //Expand
                _expand();

                //Press ESC
                var event = jQuery.Event("keydown");
                event.which = 36;
                singleSelect.$el.find("input").trigger(event);

                expect(sList.find("li[data-index='0']")).toHasClass("selected");
            });

            /* KEY_END */
            it("should expand component then END KEY is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 35;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should select first element then END key is pressed on expanded component", function(){
                _setValue("20");

                //Expand
                _expand();

                //Press ESC
                var event = jQuery.Event("keydown");
                event.which = 35;
                singleSelect.$el.find("input").trigger(event);
                sList.trigger("scroll");

                expect(sList.find("li[data-index='8499']")).toHasClass("selected");
            });

            /* KEY_PAGE_UP */
            it("should expand component then PAGEUP KEY is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 33;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should select element on previous page then PAGEUP key is pressed on expanded component", function(){
                var initialSelectedValue = 20;

                _setValue("" + initialSelectedValue);

                //Expand
                _expand();

                //Press ESC
                var event = jQuery.Event("keydown");
                event.which = 33;
                singleSelect.$el.find("input").trigger(event);
                sList.trigger("scroll");

                var height = sList.height();
                var itemHeight = sList.find("li:eq(0)").height();
                var itemsPerPage = Math.floor(height / itemHeight) - 1;
                var itemFromPreviousPage = initialSelectedValue - itemsPerPage;

                expect(sList.find("li[data-index='" + (itemFromPreviousPage - 1) + "']")).toHasClass("selected");
            });

            /* KEY_PAGE_DOWN */
            it("should expand component then PAGEDOWN KEY is pressed on collapsed component", function(){
                var event = jQuery.Event("keydown");
                event.which = 34;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeTruthy();
            });

            it("should select element on next page then PAGEDOWN key is pressed on expanded component", function(){
                var initialSelectedValue = 20;

                _setValue("" + initialSelectedValue);

                //Expand
                _expand();

                //Press ESC
                var event = jQuery.Event("keydown");
                event.which = 34;
                singleSelect.$el.find("input").trigger(event);
                sList.trigger("scroll");

                var height = sList.height();
                var itemHeight = sList.find("li:eq(0)").height();
                var itemsPerPage = Math.floor(height / itemHeight) - 1;
                var itemFromNextPage = initialSelectedValue + itemsPerPage;

                expect(sList.find("li[data-index='" + (itemFromNextPage - 1) + "']")).toHasClass("selected");
            });

            /* Tests for disabled state */

            it("should has disabled class then component is disabled", function(){
                sList.trigger("scroll");

                expect(singleSelect.$el.hasClass("disabled")).toBeFalsy();
                expect(singleSelect.$el.find("input[type='text']").attr("disabled")).toBeUndefined();

                singleSelect.setDisabled(true);

                expect(singleSelect.$el.hasClass("disabled")).toBeTruthy();
                expect(singleSelect.$el.find("input[type='text']").attr("disabled")).toBeDefined();
            });

            it("should not expand component then keyUp is pressed on collapsed component if it is disabled", function(){
                singleSelect.setDisabled(true);

                var event = jQuery.Event("keydown");
                event.which = 38;
                singleSelect.$el.find("input").trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeFalsy();
            });

            it("should not add expanded class to the component, on click on collapsed but disabled component", function(){
                var hasFocus = false;
                singleSelect.$el.find("input").on("focus", function() {
                    hasFocus = true;
                });

                singleSelect.setDisabled("false");

                var sSelectInput = singleSelect.$el.find(".sSelect-input");

                var event = jQuery.Event("click");
                sSelectInput.trigger(event);

                expect(singleSelect.$el.find(".sSelect").hasClass("expanded")).toBeFalsy();
                expect(singleSelect.$el.find(".sSelect").hasClass("collapsed")).toBeTruthy();
                expect(sList.scrollTop() == 0).toBeTruthy();
                expect(hasFocus).toBeFalsy();
            });

        });
});
