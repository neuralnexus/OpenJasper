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
 * @version: $Id: actionModel.modelGenerator.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["actionModel.modelGenerator",
        "tests/data/actionModel.data",
        "text!templates/menu.htm"],
        function(actionModel,actionModelScriptTag, menuText) {

        describe("Action Model", function() {
            var menuContext = 'folder_mutton';
            var x = 100, y = 100;

            beforeEach(function() {
                setTemplates(menuText);
                sinon.stub(window, "fitObjectIntoScreen");
                sinon.stub(window, "isSupportsTouch").returns(false);

                actionModel.hideMenu();
            });

            afterEach(function(){
                window.fitObjectIntoScreen.restore();
                window.isSupportsTouch.restore();
                actionModel.closeHandler({relatedTarget: document.body});
            });

            it("should be defined", function() {
                expect(actionModel).toBeDefined();
            });

            it("should show menu", function() {
                expect(jQuery("#menu")).toHasClass('hidden');

                actionModel.showDynamicMenu(menuContext, {}, "", {menuTop: y * 2, menuLeft: x * 2}, actionModelScriptTag);

                expect(jQuery("#menu")).not.toHasClass('hidden');
            });

            it("should show menu with predefined class", function() {
                expect(jQuery("#menu")).not.toHasClass('testtest');

                actionModel.showDynamicMenu(menuContext, {}, "testtest", {menuTop: y * 2, menuLeft: x * 2}, actionModelScriptTag);

                expect(jQuery("#menu")).toHasClass('testtest');
            });

            it("should show menu in position, where event was fired", function() {
                spyOn(Event, 'pointer').andReturn({x: x, y: y});
                actionModel.showDynamicMenu(menuContext, {}, "testtest", null, actionModelScriptTag);

                expect((jQuery("#menu")[0]).style.top).toEqual(y + "px");
                expect((jQuery("#menu")[0]).style.left).toEqual(x + "px");
            });

            it("should show menu in position,set by event and explicitly(left)", function() {
                spyOn(Event, 'pointer').andReturn({x: x, y: y});
                actionModel.showDynamicMenu(menuContext, {}, "testtest", {menuLeft: x * 2}, actionModelScriptTag);

                expect((jQuery("#menu")[0]).style.top).toEqual(y + "px");
                expect((jQuery("#menu")[0]).style.left).toEqual((x * 2) + "px");
            });

            it("should show menu in position,set by event and explicitly(left)", function() {
                spyOn(Event, 'pointer').andReturn({x: x, y: y});
                actionModel.showDynamicMenu(menuContext, {}, "testtest", {menuTop: y * 2}, actionModelScriptTag);

                expect((jQuery("#menu")[0]).style.top).toEqual((y * 2) + "px");
                expect((jQuery("#menu")[0]).style.left).toEqual(x + "px");
            });

            it("should show menu in position,set explicitly(both)", function() {
                spyOn(Event, 'pointer').andReturn({x: x, y: y});
                actionModel.showDynamicMenu(menuContext, {}, "testtest", {menuTop: y * 2, menuLeft: x * 2}, actionModelScriptTag);

                expect((jQuery("#menu")[0]).style.top).toEqual((y * 2) + "px");
                expect((jQuery("#menu")[0]).style.left).toEqual((x * 2) + "px");
            });

            it("should be able to make menu visible", function() {
                expect(jQuery("#menu")).toHasClass('hidden');

                actionModel.makeMenuVisible($(jQuery('#menu')[0]));

                expect(jQuery("#menu")).not.toHasClass('hidden');
                expect(+(jQuery("#menu")[0]).style.zIndex).toEqual(actionModel.HIGH_Z_INDEX);
            });

            it("should be to adjust menu position if if out of screen and fitable dropDown", function() {
                var menu = $(jQuery('#menu').addClass("fitable").addClass("dropDown")[0])

                actionModel.adjustMenuPosition(menu, 1, 2, 3, 4);

                expect(window.fitObjectIntoScreen.calledWithExactly(menu, null, 2, null, 4)).toBeTruthy();
            });

            it("should be to adjust menu position if if out of screen", function() {
                var menu = $(jQuery('#menu')[0]);

                actionModel.adjustMenuPosition(menu, 1, 2, 3, 4);

                expect(window.fitObjectIntoScreen.calledWithExactly(menu, null, 2, null, 4)).toBeTruthy();
            });

            it("should focus menu after opening", function() {
                actionModel.menuDom = $(jQuery('#menu').removeClass(layoutModule.HIDDEN_CLASS)[0]);
                var button = $(jQuery('#menu').find('li').find('.button')[0]);
                spyOn(actionModel.menuDom, "focus");
                spyOn(buttonManager, "over");

                actionModel.focusMenu();

                expect(actionModel.menuDom.focus).toHaveBeenCalled();
                expect(buttonManager.over).toHaveBeenCalledWith(button);

            });

            it("should not focus menu if it not open", function() {
                actionModel.menuDom = $(jQuery('#menu')[0]);
                var button = $(jQuery('#menu').find('li').find('.button')[0]);
                spyOn(actionModel.menuDom, "focus");
                spyOn(buttonManager, "over");

                actionModel.focusMenu();

                expect(actionModel.menuDom.focus).not.toHaveBeenCalled();
                expect(buttonManager.over).toHaveBeenCalledWith(button);// maybe it  should not call to?

            });

            xit("should not select first item for main menu", function() {
                actionModel.menuDom = $(jQuery('#menu').addClass(actionModel.DROP_DOWN_MENU).removeClass(layoutModule.HIDDEN_CLASS)[0]);

                var button = $(jQuery('#menu').find('li').find('.button')[0]);
                spyOn(actionModel.menuDom, "focus");
                spyOn(buttonManager, "over");
                window.isSupportsTouch.returns(true);

                actionModel.focusMenu();

                expect(actionModel.menuDom.focus).toHaveBeenCalled();
                expect(buttonManager.over).not.toHaveBeenCalled();

            });

            it("should not select first item for main menu", function() {
                actionModel.menuDom = $(jQuery('#menu').addClass(actionModel.DROP_DOWN_MENU).removeClass(layoutModule.HIDDEN_CLASS)[0]);

                var button = $(jQuery('#menu').find('li').find('.button')[0]);
                spyOn(actionModel.menuDom, "focus");
                spyOn(buttonManager, "over");
                window.isSupportsTouch.returns(true);

                actionModel.focusMenu();

                expect(actionModel.menuDom.focus).toHaveBeenCalled();
                expect(buttonManager.over).not.toHaveBeenCalled();
            });

            it("should append to menu selected context", function() {
                spyOn(actionModel, "appendToMenu");

                actionModel.initActionModelData(actionModelScriptTag);
                actionModel.assembleMenuFromActionModel(menuContext);

                expect(actionModel.appendToMenu).toHaveBeenCalledNTimes(actionModelScriptTag[menuContext].length)
                for (var i = 0, l = actionModelScriptTag[menuContext].length; i < l; i++) {
                    if (actionModel.appendToMenu.argsForCall[i][0] != actionModelScriptTag[menuContext][i]) {
                        this.fail();
                    }
                }
            });

            it("should run function which updates context if specified", function() {
                spyOn(actionModel, "appendToMenu");

                var changedContext = [1, 2, 3];
                var spy = function() {
                    return [1, 2, 3]
                };

                actionModel.initActionModelData(actionModelScriptTag);
                actionModel.assembleMenuFromActionModel(menuContext, 1, 1, spy);

                expect(actionModel.appendToMenu).toHaveBeenCalledNTimes(changedContext.length)
                for (var i = 0, l = changedContext.length; i < l; i++) {
                    if (actionModel.appendToMenu.argsForCall[i][0] != changedContext[i]) {
                        this.fail();
                    }
                }
            });

            it("should test before append to menu", function() {
                spyOn(actionModel, "appendDesiredRowType");
                spyOn(window, "getMenuMouseupFunction");
                spyOn(actionModel, "passesClientTest").andReturn(true);

                actionModel.appendToMenu(actionModelScriptTag.folder_mutton[0]);

                expect(actionModel.appendDesiredRowType).toHaveBeenCalled();

            });

            it("should test before append to menu and skip if test failed", function() {
                spyOn(actionModel, "appendDesiredRowType");
                spyOn(window, "getMenuMouseupFunction");
                spyOn(actionModel, "passesClientTest").andReturn(false);

                actionModel.appendToMenu(actionModelScriptTag.folder_mutton[0]);

                expect(actionModel.appendDesiredRowType).not.toHaveBeenCalled();

            });

            it("should evaluate positive client tests (success)", function() {
                var action = {"type": "simpleAction", "clientTest": "clientTest", "className": "up", "text": "text", "action": "action", "actionArgs": ["actionArgs"], "clientTestArgs": ["clientTestArgs"]};

                window.clientTest = sinon.stub().returns(true);

                var result = actionModel.passesClientTest(action);

                expect(window.clientTest.calledWithExactly('clientTestArgs')).toBeTruthy();
                expect(result).toBeTruthy();

            });

            it("should evaluate positive client tests (fail)", function() {
                var action = {"type": "simpleAction", "clientTest": "!clientTest", "className": "up", "text": "text", "action": "action", "actionArgs": ["actionArgs"], "clientTestArgs": ["clientTestArgs"]};

                window.clientTest = sinon.stub().returns(true);

                var result = actionModel.passesClientTest(action);

                expect(window.clientTest.calledWithExactly('clientTestArgs')).toBeTruthy();
                expect(result).toBeFalsy();

            });

            it("should return true if test not specified", function() {
                var action = {"type": "simpleAction", "className": "up", "text": "text", "action": "action", "actionArgs": ["actionArgs"]};

                window.clientTest = sinon.stub().returns(true);

                var result = actionModel.passesClientTest(action);

                expect(window.clientTest.called).toBeFalsy();
                expect(result).toBeTruthy();

            });

            it("should append simpleActionRow", function() {
                sinon.stub(actionModel, "addSimpleActionRow");

                var action = {type: actionModel.SIMPLE_ACTION};
                actionModel.appendDesiredRowType(action);

                expect(actionModel.addSimpleActionRow.called).toBeTruthy();
                actionModel.addSimpleActionRow.restore();
            });

            it("should append separatorRows", function() {
                sinon.stub(actionModel, "addSeparatorRows");

                var action = {type: actionModel.SEPARATOR};
                actionModel.appendDesiredRowType(action);

                expect(actionModel.addSeparatorRows.called).toBeTruthy();
                actionModel.addSeparatorRows.restore();
            });

            it("should append selector", function() {
                sinon.stub(actionModel, "addSelector");

                var action = {type: actionModel.SELECT_ACTION};
                actionModel.appendDesiredRowType(action);

                expect(actionModel.addSelector.called).toBeTruthy();
                actionModel.addSelector.restore();
            });

            it("should append option", function() {
                sinon.stub(actionModel, "addOption");

                var action = {type: actionModel.OPTION_ACTION};
                actionModel.appendDesiredRowType(action);

                expect(actionModel.addOption.called).toBeTruthy();
                actionModel.addOption.restore();
            });

            it("should create submenu", function() {
                var actionModelData = {context: [
                    {"type": "selectAction", "className": "flyout", "text": "Parent", "children": [
                        {"type": "optionAction", "className": "up", "text": "Child", "action": "invokeCreate", "actionArgs": ["ReportDataSource"]}
                    ]}
                ]};

                actionModel.showDynamicMenu("context", {}, "", {menuTop: y * 2, menuLeft: x * 2}, actionModelData);

                var menu = jQuery("#menu").find("ul");

                expect(menu.find("li").length).toEqual(2);
                expect(menu.find("li ul li").length).toEqual(1);
                expect(menu.find("li ul li").text().strip()).toEqual("Child");

            });

            it("should hide submenu and if not supports touch get back focus to last focused element", function() {
                actionModel.lastFocused = {
                    focus: jasmine.createSpy("focus")
                }

                spyOn(actionModel, "isMenuShowing").andReturn(true);
                spyOn(actionModel, "makeMenuInVisible");
                actionModel.hideMenu();

                expect(actionModel.makeMenuInVisible).toHaveBeenCalled();
            });

            it("should meke menu invisible", function() {
                jQuery("#menu").removeClass("hidden");

                actionModel.hideMenu();

                expect(jQuery("#menu")).toHasClass("hidden");
            });

            it("should be able to determine, if menu opened", function() {
                expect(actionModel.isMenuShowing()).toBeFalsy();

                actionModel.showDynamicMenu(menuContext, {}, "", {menuTop: y * 2, menuLeft: x * 2}, actionModelScriptTag);

                expect(actionModel.isMenuShowing()).toBeTruthy();

                actionModel.hideMenu();

                expect(actionModel.isMenuShowing()).toBeFalsy();
            });

            it("should open submenu", function() {
                var actionModelData = {context: [
                    {"type": "selectAction", "className": "flyout", "text": "Parent", "children": [
                        {"type": "optionAction", "className": "up", "text": "Child", "action": "invokeCreate", "actionArgs": ["ReportDataSource"]}
                    ]}
                ]};

                actionModel.showDynamicMenu("context", {}, "", {menuTop: y * 2, menuLeft: x * 2}, actionModelData);

                var menu = jQuery("#menu").find("ul").find("li");
                actionModel.showChildSubmenu(menu[0]);

                expect((menu.find("div")[0]).style.display).toEqual(actionModel.DISPLAY_STYLE);
                expect(actionModel.openedSubMenus.length).toEqual(1);
            });

            it("should close submenu", function() {
                var actionModelData = {context: [
                    {"type": "selectAction", "className": "flyout", "text": "Parent", "children": [
                        {"type": "optionAction", "className": "up", "text": "Child", "action": "invokeCreate", "actionArgs": ["ReportDataSource"]}
                    ]}
                ]};

                actionModel.showDynamicMenu("context", {}, "", {menuTop: y * 2, menuLeft: x * 2}, actionModelData);

                var menu = jQuery("#menu").find("ul").find("li");
                actionModel.showChildSubmenu(menu[0]);
                actionModel.hideChildSubmenu(menu[0]);

                expect((menu.find("div")[0]).style.display).toEqual("none");
            });
        });
    });