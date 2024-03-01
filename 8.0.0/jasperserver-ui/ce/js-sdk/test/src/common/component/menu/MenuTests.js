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
import Backbone from 'backbone';
import $ from 'jquery';
import Menu from 'src/common/component/menu/Menu';
import OptionView from 'src/common/component/base/OptionView';

describe('Menu component', function(){
    var menu;

    beforeEach(function() {
        menu = new Menu([ { label: "Save Dashboard", action: "save" } ]);
    });

    afterEach(function() {
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof Menu).toBe('function');
        expect(Menu.prototype instanceof Backbone.View).toBeTruthy();
    });

    it('should throw exception if required options are not defined', function(){
        expect(function() { new Menu(); }).toThrow(new Error("Menu should have options"));
        expect(function() { new Menu({}); }).toThrow(new Error("Menu should have options"));
        expect(function() { new Menu([]); }).toThrow(new Error("Menu should have options"));
    });

    it('should have public functions', function() {
        expect(menu.render).toBeDefined();
        expect(menu.show).toBeDefined();
        expect(menu.hide).toBeDefined();
        expect(menu.remove).toBeDefined();
    });

    it("should pass additional settings to OptionContainer", function(){
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();

        menu = new Menu([ { label: "Save Dashboard", action: "save" } ], {
            toggle: true,
            toggleClass: "selected",
            menuContainerTemplate: "<div></div>",
            menuOptionTemplate: "<span></span>"
        });

        expect(menu.template).toEqual("<div></div>");
        expect(menu.optionTemplate).toEqual("<span></span>");
        expect(menu.toggle).toEqual(true);
        expect(menu.toggleClass).toEqual("selected");
    });

    it('should have element with complex structure', function(){
        expect(menu.$el.hasClass("menu")).toBe(true);
        expect(menu.$el.hasClass("vertical")).toBe(true);
        expect(menu.$el.hasClass("dropDown")).toBe(true);
        expect(menu.$el.hasClass("fitable")).toBe(true);
        expect(menu.$("ul").length).toBe(1);
    });

    it('should transform options into Backbone.Collection', function() {
        expect(menu.collection instanceof Backbone.Collection).toBe(true);
        expect(menu.collection.length).toEqual(1);
        expect(menu.collection.at(0).get("label")).toEqual("Save Dashboard");
        expect(menu.collection.at(0).get("action")).toEqual("save");
    });

    it("should render options and append menu to body", function() {
        expect(menu.$el.parent()[0]).toEqual($("body")[0]);
        expect(menu.$("li").length).toEqual(1);
        expect(menu.$("li > p").text()).toEqual("Save Dashboard");
        expect(menu.options.length).toEqual(1);
        expect(menu.options[0] instanceof OptionView).toBe(true);
        expect(menu.options[0].model).toEqual(menu.collection.at(0));
    });

    it("should show menu", function() {
        menu.show();

        expect(menu.$el.is(":visible")).toBe(true);
    });

    it("should trigger 'show' event on show", function() {
        var triggerSpy = sinon.spy(menu, "trigger");

        menu.show();

        sinon.assert.calledWith(triggerSpy, "show", menu);

        triggerSpy.restore();
    });

    it("should hide menu", function() {
        menu.show();

        menu.hide();

        expect(menu.$el.is(":visible")).toBe(false);
    });

    it("should trigger 'hide' event on hide", function() {
        menu.show();

        var triggerSpy = sinon.spy(menu, "trigger");

        menu.hide();

        sinon.assert.calledWith(triggerSpy, "hide", menu);

        triggerSpy.restore();
    });

    it("should trigger 'option' event when menu option is clicked", function() {
        menu.show();

        var menuTriggerSpy = sinon.spy(menu, "trigger");

        menu.$("li:eq(0)").trigger("click");

        sinon.assert.calledWith(menuTriggerSpy, "option:save");

        menuTriggerSpy.restore();
    });

    it("should remove subviews first when remove() is called", function() {
        var optionRemoveSpy = sinon.spy(menu.options[0], "remove");

        menu.remove();

        sinon.assert.calledWith(optionRemoveSpy);

        optionRemoveSpy.restore();
    });

    it("should call base Backbone.View 'remove' method", function() {
        var removeSpy = sinon.spy(Backbone.View.prototype, "remove");

        menu.remove();

        sinon.assert.calledWith(removeSpy);

        removeSpy.restore();
    });
});