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

/**
 * @version: $Id: $
 */

/* global spyOn */
import $ from 'jquery';
import toolbarTemplate from './test/templates/toolbar.htm';
import stdnav from 'js-sdk/src/common/stdnav/stdnav';
import stdnavPluginToolbar from 'src/stdnav/plugins/stdnavPluginToolbar';
import stdnavPluginButton from 'js-sdk/src/common/stdnav/plugins/stdnavPluginButton';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe("Stdnav Toolbar Plugin", function() {
    var key = $.simulate.keyCode,
        toolbarListItem;

    beforeEach(function() {
        $("body").attr("js-stdnav", "true");
        setTemplates(toolbarTemplate);
        toolbarListItem = $(".j-toolbar li");

        stdnav.activate();
        stdnavPluginToolbar.activate();
        stdnavPluginButton.activate();

        $(".subfocus").removeClass("subfocus");
        $(".superfocus").removeClass("superfocus");

    });

    afterEach(function() {
        stdnavPluginToolbar.deactivate();
        stdnavPluginButton.deactivate();
        stdnav.deactivate();
        $("body").removeAttr("js-stdnav");
    });


    describe("left-right navigation inside menus", function (){

        it("should move class 'subfocus' to prev li if we press left arrow key", function(){
            toolbarListItem.eq(9).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.left[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe(toolbarListItem.eq(3)[0]);
        });

        it("should move class 'subfocus' to next li if we press right arrow key", function(){
            toolbarListItem.eq(0).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.right[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe(toolbarListItem.eq(2)[0]);
        });

        it("should not remove class 'subfocus' if we press left arrow key", function(){
            toolbarListItem.eq(0).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.left[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe(toolbarListItem.eq(0)[0]);
        });

        it("should not remove class 'subfocus' if we press right arrow key", function(){
            toolbarListItem.eq(9).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.right[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe(toolbarListItem.eq(9)[0]);
        });

    });

    describe("up-down navigation inside menus", function(){

        it("should move class 'subfocus' to next li if we press down arrow key", function(){
            $("#menuList li").eq(0).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.down[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#menuList li").eq(1)[0]);
        });

        it("should move class 'subfocus' to prev li if we press up arrow key", function(){
            $("#menuList li").eq(1).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.up[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#menuList li").eq(0)[0]);
        });

        it("should not remove class 'subfocus' from last li if we press down arrow key", function(){
            $("#menuList li").eq(1).addClass("subfocus");
            expect(stdnavPluginToolbar.behavior.down[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#menuList li").eq(1)[0]);
        });
    });

    describe("navigation between menus", function (){
        it("should close dropdown and move class 'subfocus' to prev li if we press up arrow key", function () {
            stdnavPluginToolbar.parent = $("#viewerToolbar ul>li.leaf").eq(3)[0]; //ToDo

            $("#menu ul li").first().addClass("subfocus");

            expect(stdnavPluginToolbar.behavior.left[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($(".j-toolbar li").eq(2)[0]);
        }
        );

        describe("on right moving", function(){

            it("should close dropdown and move class 'subfocus' to next li if we press right arrow key", function () {
                stdnavPluginToolbar.parent = $("#viewerToolbar ul>li.leaf").eq(2)[0]; //ToDo

                $("#menu ul li").first().addClass("subfocus");

                expect(stdnavPluginToolbar.behavior.right[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($(".j-toolbar li").eq(3)[0]);
            });
        });

        describe("on moving down", function (){

            it("should move class 'subfocus' to main menu li if we press down arrow key on first elem in dropdown #1", function(){
                $("#viewerToolbar ul>li.leaf").eq(2).addClass("subfocus");

                expect(stdnavPluginToolbar.behavior.down[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#menu ul>li.leaf").eq(0)[0]);
            });

            it("should move class 'subfocus' to main menu li if we press down arrow key on first elem in dropdown #2", function(){
                $(".toolsRight li").eq(3).addClass("subfocus");
                $("#vwroptions .menu").show();

                expect(stdnavPluginToolbar.behavior.down[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#vwroptions ul li").first()[0]);
            });
        });


        describe("on moving up", function (){

            it("should move class 'subfocus' to main menu li if we press up arrow key on first elem in dropdown", function(){
                $("#menuList li").eq(0).addClass("subfocus");
                stdnavPluginToolbar.parent = $("#viewerToolbar ul>li.leaf").eq(2)[0];

                expect(stdnavPluginToolbar.behavior.up[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#viewerToolbar ul>li.leaf").eq(2)[0]);
            });
        });

        describe("on exit", function () {
            it("should move class 'subfocus' to main menu li if we press esc key on first elem in dropdown", function(){
                $("#menuList li").eq(0).addClass("subfocus");
                stdnavPluginToolbar.parent = $("#viewerToolbar ul>li.leaf").eq(2)[0];

                expect(stdnavPluginToolbar.behavior.exit[1].call(stdnavPluginToolbar, $("li.subfocus")[0])).toBe($("#viewerToolbar ul>li.leaf").eq(2)[0]);
            });

            it("should return element if it doesn't have 'P' child element", function () {
                var returnEl = stdnavPluginToolbar.behavior.exit[1].call(stdnavPluginToolbar, $("<div></div>")[0]);

                expect(returnEl.id).toEqual("searchInput");
            });

            it("should set element to this.parent", function () {
                var someEl = $("<div><p></p></div>")[0],
                    parentEl = $("div")[0];
                stdnavPluginToolbar.parent = parentEl;

                expect(stdnavPluginToolbar.behavior.exit[1].call(stdnavPluginToolbar, someEl)).toBe(parentEl);
            });
        });

        describe("on enter", function () {

            it("should set this.parent to argument element", function () {
                var someEl = $("<div class='j-dropdown'><input class='someInput'></div>")[0];

                stdnavPluginToolbar.behavior.enter[1].call(stdnavPluginToolbar, someEl);

                expect(stdnavPluginToolbar.parent).toBe(someEl);
            });

            it("should set this.parent to argument element", function () { //TODO: fix description!
                var someEl = $("<div class='j-dropdown'><input class='someInput'></div>")[0];

                expect($(stdnavPluginToolbar.behavior.enter[1].call(stdnavPluginToolbar, someEl)).hasClass("someInput")).toBeTruthy();
            });

            it("should set element to this.parent", function () {
                var someEl = $("<div><p></p></div>")[0],
                    parentEl = $("div")[0];
                stdnavPluginToolbar.parent = parentEl;

                expect(stdnavPluginToolbar.behavior.enter[1].call(stdnavPluginToolbar, someEl)).toBe(parentEl);
            });
        });
    });


    describe("firing actions", function () {

        it("should call ToolbarPlugin 'enter' handler when Enter key pressed on BUTTON", function () {
            var enterSpy = spyOn(stdnavPluginToolbar.behavior.enter, 1);

            $('#zoom_in').focus().closest("li").focusin();
            $('#zoom_in').simulate("keydown", {keyCode: key.ENTER});

            expect(enterSpy).toHaveBeenCalledTimes(1);
        });

        it("should call ButtonPlugin 'toggle' handler when Space key pressed on BUTTON", function () {
            var toggleSpy = spyOn(stdnavPluginButton.behavior.toggle, 1);

            $('#zoom_in').focus().closest("li").focusin();
            $('#zoom_in').simulate("keydown", {keyCode: key.SPACE});

            expect(toggleSpy).toHaveBeenCalledTimes(1);
        });

        it("should call private '_onEnterOrEntered' handler when Enter or Space key pressed on BUTTON", function () {
            var toolbarSpy = spyOn(stdnavPluginButton, "_onEnterOrEntered");
            var buttonSpy = spyOn(stdnavPluginToolbar, "_onEnterOrEntered");

            stdnavPluginButton.deactivate();
            stdnavPluginToolbar.deactivate();
            stdnavPluginButton.activate();
            stdnavPluginToolbar.activate();

            $('#zoom_in').focus().closest("li").focusin();
            $('#zoom_in').simulate("keydown", {keyCode: key.ENTER});
            $('#zoom_in').simulate("keydown", {keyCode: key.SPACE});

            expect(toolbarSpy).toHaveBeenCalledTimes(1);
            expect(buttonSpy).toHaveBeenCalledTimes(1);
        });

        it("should trigger 'click' event when Enter or Space key pressed on BUTTON", function () {
            var testButton = $('#zoom_in')[0],
                testButtonLi = $(testButton).closest("li");

            spyOn(testButton, "onclick");

            testButtonLi.focus().focusin();
            testButtonLi.simulate("keydown", {keyCode: key.SPACE});
            testButtonLi.simulate("keydown", {keyCode: key.ENTER});

            expect(testButton.onclick).toHaveBeenCalledTimes(2);
        });
    });
});