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

import $ from 'jquery';
import SingleSelect from 'src/components/singleSelect/view/SingleSelectNew';
import DataProvider from 'src/components/singleSelect/dataprovider/DataProviderNew';

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

describe("SingleSelect New", function () {

    var $scalableList,$selectInput,  singleSelect;

    beforeEach(function(){

        var getData = new DataProvider({
            request: listGetDataFactory(),
            //pageSize: 10,
            //maxSearchCacheSize: 3,
            //saveLastCriteria: false,
            serialRequestsDelay: 0,
            controlGetTotal: true,
            saveLastCriteria: true
        }).getData;

        singleSelect = new SingleSelect({
            el: $("<div id=\"singleSelectPlaceHolder\"></div>"),
            label: "List with Search",
            getData: getData,
            value: "8500",
            scrollTimeout: -1,
            keydownTimeout: -1
        }).renderData();

        $("body").append(singleSelect.el);

        $scalableList = $("body").find(".jr-mScalablelist");
        $selectInput = singleSelect.$el.find(".jr-mSingleselect-input");
    });

    afterEach(function(){
        singleSelect.remove();
        $("#singleSelectPlaceHolder").remove();
        $scalableList.remove();
    });

    var _expand = function() {
        //Expand
        var $selectInput = singleSelect.$el.find(".jr-mSingleselect-input");
        var event = $.Event("click");
        $selectInput.trigger(event);
        singleSelect.onFocus();
        $scalableList.trigger("scroll");
    };

    var _setValue = function(value) {
        singleSelect.setValue(value);
        $scalableList.trigger("scroll");
    };

    it("should return initial value in getValue if it was not changed", function(){
        var value = singleSelect.getValue();
        expect(value).toEqual("8500");
    });

    // TODO: Does not working because css file is not applied
    // eslint-disable-next-line no-undef
    xit("should add expanded class to the component, scroll to selectied element, focus on input on click on collapsed component", function(){
        var hasFocus = false;
        singleSelect.$el.find("input").on("focus", function() {
            hasFocus = true;
        });

        singleSelect.setValue("60");

        var event = $.Event("click");
        $selectInput.trigger(event);
        $scalableList.trigger("scroll");

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
        expect($scalableList.scrollTop() > 200).toBeTruthy();
        expect($scalableList.find("li[data-index='59']").hasClass("jr-isSelected")).toBe(true);
        //Uncomment when solution for focus+karma will be found
        //expect(hasFocus).toBeTruthy();
    });

    it("should select item on mousemove", function(){
        var $li = $scalableList.find("li:eq(2)");

        var event = $.Event("mousemove");
        $li.trigger(event);

        expect($scalableList.find("li:eq(2)").hasClass("jr-isSelected")).toBeTruthy();
    });

    it("should select item and trigger selection change on mouseup on element", function(){
        var value = null;
        singleSelect.on("selection:change", function(selection) {
            value = selection;
        });

        var mouseMoveEvt = $.Event("mousemove");
        $scalableList.find("li:eq(2)").trigger(mouseMoveEvt);

        var mouseUpEvent = $.Event("mouseup");
        $scalableList.find("li:eq(2)").trigger(mouseUpEvent);

        expect(singleSelect.getValue()).toEqual("3");
        expect(value).toEqual("3");
    });

    it("should add collapsed class to the component, do not reset selected value on click on expanded element", function(){

        //Expand
        _expand();

        //Mousemove
        var mouseMoveEvt = $.Event("mousemove");
        $scalableList.find("li:eq(2)").trigger(mouseMoveEvt);

        //Collapse
        var clickEvt = $.Event("click");
        $selectInput.trigger(clickEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
        expect(singleSelect.getValue()).toEqual("8500");
    });

    it("should filter out values on keydown in search input", function(){
        _setValue("1");

        //Expand
        _expand();

        //type value in search
        singleSelect.$el.find("input").val("850");
        var keyDownEvt = $.Event("keydown");
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li").length).toEqual(100);
        expect($scalableList.find("li:eq(0)").text().replace(/\s/g, "")).toEqual("1");

    });

    it("should clear filter criteria on selection change", function(){
        _setValue("1");

        //Expand
        _expand();

        //type value in search
        singleSelect.$el.find("input").val("850");
        var keyDownEvt = $.Event("keydown");
        singleSelect.$el.find("input").trigger(keyDownEvt);

        //Select second li item
        var mouseMoveEvt = $.Event("mousemove");
        $scalableList.find("li:eq(2)").trigger(mouseMoveEvt);

        var mouseUpEvt = $.Event("mouseup");
        $scalableList.find("li:eq(2)").trigger(mouseUpEvt);

        expect(singleSelect.$el.find("input").val()).toEqual("");
        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    it("should clear filter criteria on collapse without selection change", function(){
        _setValue("1");

        //Expand
        _expand();

        //type value in search
        singleSelect.$el.find("input").val("850");
        var keyDownEvt = $.Event("keydown");
        singleSelect.$el.find("input").trigger(keyDownEvt);

        //collapse element
        singleSelect.collapse();

        expect(singleSelect.getValue()).toEqual("1");
        expect($scalableList.find("li").length).toEqual(100);
        expect(singleSelect.$el.find("input").val()).toEqual("");
        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    it("should not collapse then mousedown was done on component and mouseup outside component", function(){
        _setValue("1");

        //Expand
        _expand();

        //Mousedown on component
        var mouseDownEvt = $.Event("mousedown");
        $scalableList.find("li:eq(2)").trigger(mouseDownEvt);

        //Mouseup outside component
        var mouseUpEvt = $.Event("mouseup");
        $("body").trigger(mouseUpEvt);
        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    it("should collapse then mousedown was done outside component", function(){
        _setValue("1");

        //Expand
        _expand();

        //Mousedown on component
        var mouseDownEvt = $.Event("mousedown");
        $scalableList.find("li:eq(2)").trigger(mouseDownEvt);

        //Mouseup outside component
        var mouseUpEvt = $.Event("mouseup");
        $("body").trigger(mouseUpEvt);
        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();

        //Emulate blur event
        singleSelect.onBlur();

        expect(singleSelect.getValue()).toEqual("1");
        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
        expect($selectInput.hasClass("jr-isFocused")).toBeFalsy();
    });

    it("should scroll to current component's selected element if it's present in result set after filtering out", function(){
        _setValue("20");

        //Expand
        _expand();

        //type value in search
        singleSelect.$el.find("input").val("2");
        var keyDownEvt = $.Event("keydown");
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li[data-index='19']").hasClass("jr-isSelected")).toBe(true);
        // var selectedItemTop = $scalableList.find("li[data-index='19']").offset().top;
        // var elTop = $scalableList.offset().top;
        // expect(selectedItemTop > elTop).toBeTruthy();
        // expect(selectedItemTop < elTop + $scalableList.height()).toBeFalsy();
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
        var mouseUpEvt = $.Event("mouseup");
        $scalableList.find("li[data-index='19']").trigger(mouseUpEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
        expect(selectionChanged).toBeFalsy();
        expect(singleSelect.getValue()).toEqual("20");
    });

    /* keyboard events */

    /* KEY_UP */
    it("should expand component then keyUp is pressed on collapsed component", function(){
        var event = $.Event("keydown");
        event.which = 38;
        singleSelect.$el.find("input").trigger(event);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    it("should select previous item in component then keyUp is pressed", function(){

        _setValue("5");

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 38;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li[data-index='3']").hasClass("jr-isSelected")).toBe(true);
    });

    it("should collapse component if keyUp is pressed then first element is selected", function(){
        _setValue("1");

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 38;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    it("should collapse component if keyUp is pressed then no element is selected", function(){
        _setValue(undefined);

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 38;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    /* KEY_DOWN */
    it("should expand component then keyDown is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 40;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();

    });

    it("should select next item in component then keyDown is pressed", function(){

        _setValue("5");

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 40;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li[data-index='5']").hasClass("jr-isSelected")).toBe(true);
    });

    it("should select next item in component then keyDown is pressed and value was set through API using object notation", function(){

        _setValue(undefined);
        _setValue({"4": "5"});

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 40;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li[data-index='5']").hasClass("jr-isSelected")).toBe(true);
    });

    it("should do nothing with component if keyDown is pressed then last element is selected", function(){
        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 40;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
        expect($scalableList.find("li[data-index='0']").hasClass("jr-isSelected")).toBe(true);
    });

    it("should select first element in component if keyDown is pressed then no element is selected", function(){
        _setValue(undefined);

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 40;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li[data-index='0']").hasClass("jr-isSelected")).toBe(true);
    });

    /* KEY_ENTER */
    it("should expand component then EnterKey is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 13;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    it("should change selection and collapse element then EnterKey is pressed", function(){
        _setValue("1");

        //Expand
        _expand();

        //Select second li item
        var mouseMoveEvt = $.Event("mousemove");
        $scalableList.find("li:eq(2)").trigger(mouseMoveEvt);

        mouseMoveEvt = $.Event("keydown");
        mouseMoveEvt.which = 13;
        singleSelect.$el.find("input").trigger(mouseMoveEvt);

        expect(singleSelect.getValue()).toEqual("3");
        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();

    });

    it("should collapse component if no activeValue present and enterKey pressed", function(){
        _setValue(undefined);

        //Expand
        _expand();

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 13;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect(singleSelect.getValue()).toBeUndefined();
        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();

    });

    /* KEY_ESC */
    it("should not expand component then ESC is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 27;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    it("should collapse component, clear filter and preserve existing value then ESC is pressed on expanded component", function(){
        _setValue("1");

        //Expand
        _expand();

        //type value in search
        singleSelect.$el.find("input").val("850");
        var keyDownEvt = $.Event("keydown");
        singleSelect.$el.find("input").trigger(keyDownEvt);

        //Select second li item
        var mouseMoveEvt = $.Event("mousemove");
        $scalableList.find("li:eq(2)").trigger(mouseMoveEvt);

        var keyDownEscEvt = $.Event("keydown");
        keyDownEscEvt.which = 27;
        singleSelect.$el.find("input").trigger(keyDownEscEvt);

        expect(singleSelect.getValue()).toEqual("1");
        expect($scalableList.find("li").length).toEqual(100);
        expect(singleSelect.$el.find("input").val()).toEqual("");
        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    /* KEY_HOME */
    it("should expand component then HOME KEY is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 36;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    it("should select first element then HOME key is pressed on expanded component", function(){
        _setValue("20");

        //Expand
        _expand();

        //Press ESC
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 36;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($scalableList.find("li[data-index='0']").hasClass("jr-isSelected")).toBe(true);
    });

    /* KEY_END */
    it("should expand component then END KEY is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 35;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    // TODO: Does not working because css file is not applied
    // eslint-disable-next-line no-undef
    xit("should select first element then END key is pressed on expanded component", function(){
        _setValue("20");

        //Expand
        _expand();

        //Press ESC
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 35;
        singleSelect.$el.find("input").trigger(keyDownEvt);
        $scalableList.trigger("scroll");

        expect($scalableList.find("li[data-index='8499']").hasClass("jr-isSelected")).toBe(true);
    });

    /* KEY_PAGE_UP */
    it("should expand component then PAGEUP KEY is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 33;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    it("should select element on previous page then PAGEUP key is pressed on expanded component", function(){
        var initialSelectedValue = 60;

        _setValue("" + initialSelectedValue);

        //Expand
        _expand();

        //Press ESC
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 33;
        singleSelect.$el.find("input").trigger(keyDownEvt);
        $scalableList.trigger("scroll");

        var height = $scalableList.height();
        var itemHeight = $scalableList.find("li:eq(0)").outerHeight(true);
        var itemsPerPage = Math.floor(height / itemHeight) - 1;
        var itemFromPreviousPage = (initialSelectedValue - 1) - itemsPerPage; // -1 because index of items started from 0 but values from 1
        var selectedIndex = $scalableList.find("li.jr-isSelected").data("index");

        expect(selectedIndex === itemFromPreviousPage).toBe(true);
    });

    /* KEY_PAGE_DOWN */
    it("should expand component then PAGEDOWN KEY is pressed on collapsed component", function(){
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 34;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeTruthy();
    });

    // TODO: Does not working because css file is not applied
    // eslint-disable-next-line no-undef
    xit("should select element on next page then PAGEDOWN key is pressed on expanded component", function(){
        var initialSelectedValue = 60;

        _setValue("" + initialSelectedValue);

        //Expand
        _expand();

        //Press ESC
        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 34;
        singleSelect.$el.find("input").trigger(keyDownEvt);
        $scalableList.trigger("scroll");

        var height = $scalableList.height();
        var itemHeight = $scalableList.find("li:eq(0)").outerHeight(true);
        var itemsPerPage = Math.floor(height / itemHeight) - 1;
        var itemFromNextPage = (initialSelectedValue - 1) + itemsPerPage; // -1 because index of items started from 0 but values from 1
        var selectedIndex = $scalableList.find("li.jr-isSelected").data("index");


        expect(selectedIndex === itemFromNextPage).toBe(true);
    });

    /* Tests for disabled state */

    //TODO: looks like we don't have 'jr-isDisabled' class for 'singleselect'
    it("should has disabled class then component is disabled", function(){
        $scalableList.trigger("scroll");

        expect(singleSelect.listView.$el.hasClass("disabled")).toBeFalsy();
        expect(singleSelect.$el.find("input[type='text']").attr("disabled")).toBeUndefined();

        singleSelect.setDisabled(true);

        expect(singleSelect.listView.$el.hasClass("disabled")).toBeTruthy();
        expect(singleSelect.$el.find("input[type='text']").attr("disabled")).toBeDefined();
    });

    it("should not expand component then keyUp is pressed on collapsed component if it is disabled", function(){
        singleSelect.setDisabled(true);

        var keyDownEvt = $.Event("keydown");
        keyDownEvt.which = 38;
        singleSelect.$el.find("input").trigger(keyDownEvt);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
    });

    it("should not add expanded class to the component, on click on collapsed but disabled component", function(){
        var hasFocus = false;
        singleSelect.$el.find("input").on("focus", function() {
            hasFocus = true;
        });

        singleSelect.setDisabled("false");


        var event = $.Event("click");
        $selectInput.trigger(event);

        expect($selectInput.hasClass("jr-isOpen")).toBeFalsy();
        expect($scalableList.scrollTop() === 0).toBeTruthy();
        expect(hasFocus).toBeFalsy();
    });

});