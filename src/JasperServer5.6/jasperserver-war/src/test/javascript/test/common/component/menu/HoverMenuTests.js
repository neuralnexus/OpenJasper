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


define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        $ = require("jquery"),
        HoverMenu = require("common/component/menu/HoverMenu"),
        Menu = require("common/component/menu/Menu");

    describe('HoverMenu component', function(){
        var menu;

        beforeEach(function() {
            menu = new HoverMenu([ { label: "Save Dashboard", action: "save" } ], $("<button></button>"));
        });

        afterEach(function() {
            menu && menu.remove();
            $(".menu.vertical.dropDown.fitable").remove();
        });

        it('should be Backbone.View instance', function(){
            expect(typeof HoverMenu).toBe('function');
            expect(HoverMenu.prototype instanceof Backbone.View).toBeTruthy();
        });

        it('should be Menu instance', function(){
            expect(HoverMenu.prototype instanceof Menu).toBeTruthy();
        });

        it('should throw exception if attachTo setting is not defined', function(){
            expect(function() { new HoverMenu(); }).toThrow("HoverMenu should be attached to an element");
            expect(function() { new HoverMenu({}); }).toThrow("HoverMenu should be attached to an element");
            expect(function() { new HoverMenu([ { label: "Save Dashboard", action: "save" } ], $("#someNotExistingElement")); }).toThrow("HoverMenu should be attached to an element");
        });

        it('should attach mouseover/mouseout event handlers to element', function() {
            var $btn = menu.$attachTo;

            var events = $._data($btn[0], "events");
            expect(events).toBeDefined();
            expect(events.mouseover).toBeDefined();
            expect(events.mouseout).toBeDefined();
        });

        it("should detach mouseover/mouseout events from element when remove() is called", function() {
            var $btn = menu.$attachTo;

            menu.remove();

            var events = $._data($btn[0], "events");
            expect(events).toBeUndefined();
        });

        it("should show menu on element mouseover", function() {
            var $btn = menu.$attachTo,
                showSpy = sinon.spy(menu, "show");

            $btn.trigger("mouseover");

            sinon.assert.calledWith(showSpy);

            showSpy.restore();
        });

        it("should hide menu on element mouseout", function() {
            var $btn = menu.$attachTo,
                hideSpy = sinon.spy(menu, "hide");

            jasmine.Clock.useMock();

            $btn.trigger("mouseout");

            jasmine.Clock.tick(20);

            sinon.assert.calledWith(hideSpy);

            hideSpy.restore();
        });
    });
});