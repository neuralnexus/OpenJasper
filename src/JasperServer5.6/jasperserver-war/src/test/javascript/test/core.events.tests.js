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
 * @version: $Id: core.events.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["core.events.bis",
    "core.layout",
    "actionModel.primaryNavigation",
    "text!templates/mainNavigation.htm",
    "text!templates/layout.htm",
    "text!templates/events.htm"],
    function(buttonManager, layoutModule, primaryNavModule, mainNavigationText, layoutText, eventsText) {

    // TODO
    xdescribe("Core events", function() {

        beforeEach(function() {
            setTemplates(eventsText, layoutText, mainNavigationText);
        });

        describe("events", function() {
            beforeEach(function() {
                sinon.stub(actionModel, "resetMenu");
            });

            afterEach(function(){
                actionModel.resetMenu.restore();
            });

            describe("mousedown", function() {

                it("should initialize global mousedown events(layoutModule.MINIMIZER_PATTERN)", function() {
                    spyOn(layoutModule, "maximize");

                    var element = jQuery("#minimized").simulate("mouseup")[0];

                    expect(layoutModule.maximize).toHaveBeenCalledWith(element);
                });

                it("should initialize global mousedown events(layoutModule.MINIMIZED_PATTERN)", function() {
                    spyOn(layoutModule, "minimize");

                    var element = jQuery("#minimized").parent().addClass("maximized").find("#minimized").simulate("mouseup")[0];

                    expect(layoutModule.minimize).toHaveBeenCalledWith(element);
                });

                it("should initialize global mousedown events(layoutModule.META_LINKS_PATTERN)", function() {
                    spyOn(primaryNavModule, "navigationOption");

                    jQuery("#main_logOut_link").simulate("mousedown");

                    expect(primaryNavModule.navigationOption).toHaveBeenCalledWith("logOut");
                });

                it("should initialize global mousedown events(layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN )", function() {
                    spyOn(buttonManager, "down");
                    spyOn(window, "isSupportsTouch").andReturn(false);

                    var element = jQuery("#run").simulate("mousedown")[0];

                    expect(buttonManager.down).toHaveBeenCalledWith(element);
                });

                it("should initialize global mousedown events(pressed layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN)", function() {
                    spyOn(buttonManager, "down");
                    spyOn(window, "isSupportsTouch").andReturn(false);

                    jQuery("#run").attr("disabled", true).simulate("mousedown");

                    expect(buttonManager.down).not.toHaveBeenCalled();
                });

                it("should initialize global mousedown events(not support touch, layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN)", function() {
                    spyOn(buttonManager, "down");
                    spyOn(window, "isSupportsTouch").andReturn(true);

                    jQuery("#run").attr("disabled", true).simulate("mousedown");

                    expect(buttonManager.down).not.toHaveBeenCalled();
                });

            });

            describe("mouseover", function() {
                it("should initialize global mouseover events(layoutModule.NAVIGATION_MUTTON_PATTERN)", function() {
                    spyOn(primaryNavModule, "showNavButtonMenu");

                    var element = jQuery("#main_manage").simulate("mouseover")[0];

                    expect(primaryNavModule.showNavButtonMenu).toHaveBeenCalled();
                    expect(primaryNavModule.showNavButtonMenu.mostRecentCall.args[1]).toEqual(element);
                });
            });

            describe("mouseup", function() {
                it("should initialize global mouseup events(home, layoutModule.NAVIGATION_PATTERN)", function() {
                    spyOn(primaryNavModule, "navigationOption");

                    jQuery("#" + layoutModule.MAIN_NAVIGATION_HOME_ITEM_ID).find(layoutModule.BUTTON_PATTERN).simulate("mouseup");

                    expect(primaryNavModule.navigationOption).toHaveBeenCalledWith("home");
                });

                it("should initialize global mouseup events(library, layoutModule.NAVIGATION_PATTERN)", function() {
                    spyOn(primaryNavModule, "navigationOption");

                    jQuery("#" + layoutModule.MAIN_NAVIGATION_LIBRARY_ITEM_ID).find(layoutModule.BUTTON_PATTERN).simulate("mouseup");

                    expect(primaryNavModule.navigationOption).toHaveBeenCalledWith("library");
                });

                it("should initialize global mouseup events(other, layoutModule.NAVIGATION_PATTERN)", function() {
                    spyOn(primaryNavModule, "navigationOption");

                    jQuery("#main_view").find(layoutModule.BUTTON_PATTERN).simulate("mouseup");

                    expect(primaryNavModule.navigationOption).not.toHaveBeenCalled();
                });

                it("should initialize global mouseup events(not selected, layoutModule.TABSET_TAB_PATTERN)", function() {
                    jQuery("[tabId=#attributesTab]").simulate("mouseup");

                    expect(jQuery("[tabId=#attributesTab]")).toHasClass(layoutModule.SELECTED_CLASS);
                    expect(jQuery("#attributesTab")).toBeVisible();
                    expect(jQuery("#propertiesTab")).toBeHidden();

                });

                it("should initialize global mouseup events(not selected, disabled, layoutModule.TABSET_TAB_PATTERN)", function() {
                    jQuery("[tabId=#attributesTab]").attr("disabled", true).simulate("mouseup");

                    expect(jQuery("[tabId=#attributesTab]")).not.toHasClass(layoutModule.SELECTED_CLASS);
                    expect(jQuery("#attributesTab")).toBeHidden();
                    expect(jQuery("#propertiesTab")).toBeVisible();

                });

                it("should initialize global mouseup events(not capsule, layoutModule.BUTTON_PATTERN)", function() {
                    spyOn(buttonManager, "up");

                    var element = jQuery("#run").removeClass("capsule").simulate("mouseup")[0];

                    expect(buttonManager.up).toHaveBeenCalledWith(element);
                });
            });
        });

        describe("button manager", function() {

            it("should know, if item is selected(selected)", function() {
                expect(buttonManager.isSelected($(jQuery("#anchor")[0]))).toBeTruthy();
            });

            it("should know, if item is selected(not selected)", function() {
                expect(buttonManager.isSelected($(jQuery("#anchor").parent().removeClass("selected")[0]))).toBeFalsy();
            });

            it("should know, if item is selected(not item)", function() {
                expect(buttonManager.isSelected($(jQuery("#minimized")[0]))).toBeFalsy();
            });

            it("should know, if item is selected(select by function)", function() {
                expect(buttonManager.isSelected(true, function() {
                    return $(jQuery("#anchor")[0])
                })).toBeTruthy();
            });

            it("should know, if item is selected(nothing selected)", function() {
                expect(buttonManager.isSelected()).toBeFalsy();
            });

            it("should set layoutModule.HOVERED_CLASS", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);

                buttonManager.over(jQuery("#menuMutton")[0]);

                expect(jQuery("#menuMutton")).toHasClass(layoutModule.HOVERED_CLASS);
            });

            it("should not set layoutModule.HOVERED_CLASS if item is selected", function() {
                spyOn(buttonManager, "isSelected").andReturn(true);

                buttonManager.over(jQuery("#menuMutton")[0]);

                expect(jQuery("#menuMutton")).not.toHasClass(layoutModule.HOVERED_CLASS);
            });

            it("should set layoutModule.HOVERED_CLASS for element, got by function", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);

                buttonManager.over(true, function() {
                    return $(jQuery("#menuMutton")[0])
                });

                expect(jQuery("#menuMutton")).toHasClass(layoutModule.HOVERED_CLASS);
            });

            it("should remove classes on out", function() {
                var element = jQuery("#menuMutton").addClass(layoutModule.HOVERED_CLASS).addClass(layoutModule.PRESSED_CLASS);
                buttonManager.out(element[0]);

                expect(element).not.toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).not.toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should remove classes on out for element, got by function", function() {
                var element = jQuery("#menuMutton").addClass(layoutModule.HOVERED_CLASS).addClass(layoutModule.PRESSED_CLASS);
                buttonManager.out(true, function() {
                    return element[0]
                });

                expect(element).not.toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).not.toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should set layoutModule.PRESSED_CLASS", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);
                var element = jQuery("#menuMutton").addClass(layoutModule.HOVERED_CLASS);
                buttonManager.down(element[0]);

                expect(element).not.toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should not set layoutModule.PRESSED_CLASS if item is selected", function() {
                spyOn(buttonManager, "isSelected").andReturn(true);
                var element = jQuery("#menuMutton").addClass(layoutModule.HOVERED_CLASS);
                buttonManager.down(element[0]);

                expect(element).toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).not.toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should set layoutModule.PRESSED_CLASS for element, got by function", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);
                var element = jQuery("#menuMutton").addClass(layoutModule.HOVERED_CLASS);
                buttonManager.down(true, function() {
                    return element[0]
                });

                expect(element).not.toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);
                var element = jQuery("#menuMutton").addClass(layoutModule.PRESSED_CLASS).removeClass("selected");
                buttonManager.up(element[0]);

                expect(element).toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).not.toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should not set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS if is ipad", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);
                var element = jQuery("#menuMutton").addClass(layoutModule.PRESSED_CLASS);
                spyOn(window, "isIPad").andReturn(true);

                buttonManager.up(element[0]);

                expect(element).not.toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).not.toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should not set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS if item is selected", function() {
                spyOn(buttonManager, "isSelected").andReturn(true);
                var element = jQuery("#menuMutton").addClass(layoutModule.PRESSED_CLASS);

                buttonManager.up(element[0]);

                expect(element).not.toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should set layoutModule.layoutModule.HOVERED_CLASS and remove layoutModule.PRESSED_CLASS for element, got by function", function() {
                spyOn(buttonManager, "isSelected").andReturn(false);
                var element = jQuery("#menuMutton").removeClass("selected").addClass(layoutModule.PRESSED_CLASS);
                buttonManager.up(true, function() {
                    return element[0]
                });

                expect(element).toHasClass(layoutModule.HOVERED_CLASS);
                expect(element).not.toHasClass(layoutModule.PRESSED_CLASS);
            });

            it("should enable element", function() {
                spyOn(buttonManager, "out");
                var element = jQuery("#run").attr("disabled", true)[0];

                buttonManager.enable(element);

                expect(element).not.toBeDisabled();
                expect(buttonManager.out).toHaveBeenCalledWith(element);
            });

            it("should disable element", function() {
                spyOn(buttonManager, "out");
                var element = jQuery("#run")[0];

                buttonManager.disable(element);

                expect(element).toBeDisabled();
                expect(buttonManager.out).toHaveBeenCalledWith(element);
            });

            it("should select element", function() {
                var element = jQuery("#run");

                buttonManager.select(element[0]);

                expect(element).toHasClass(layoutModule.SELECTED_CLASS)
            });

            it("should deselect element", function() {
                var element = jQuery("#run").addClass(layoutModule.SELECTED_CLASS);

                buttonManager.unSelect(element[0]);

                expect(element).not.toHasClass(layoutModule.SELECTED_CLASS);
            });

        });

    });
});