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
 * @version: $Id: actionModel.primaryNavigation.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["actionModel.modelGenerator",
        "actionModel.primaryNavigation",
        "text!templates/menu.htm",
        "text!templates/mainNavigation.htm"],
        function(actionModel, primaryNavModule, menuText, mainNavigationText) {

        describe("Primary navigation", function() {
            beforeEach(function() {
                setTemplates(menuText, mainNavigationText);
            });

            var navActionModel = {"main_home_mutton": [], "main_view_mutton": [
                {"type": "selectAction", "text": "View", "children": [
                    {"type": "optionAction", "text": "Search Results", "action": "primaryNavModule.navigationOption", "actionArgs": ["search"]},
                    {"type": "separator"},
                    {"type": "optionAction", "clientTest": "!isIPad", "text": "Samples", "action": "primaryNavModule.navigationOption", "actionArgs": ["samples"]}
                ]}
            ]};

            it("should create muttons on initialization", function() {
                spyOn(window, "$").andReturn({
                    text: {
                        evalJSON: function() {
                            return navActionModel;
                        }
                    }
                });

                spyOn(primaryNavModule, "createMutton");

                primaryNavModule.initializeNavigation();

                var args = primaryNavModule.createMutton.argsForCall;
                var expectedArgs = [
                    ["main_view", "View"]
                ];

                expect(args).toArrayEquals(expectedArgs);
            });

            it("should create mutton elements", function() {
                jQuery("#navigationOptions .mutton").remove();

                primaryNavModule.createMutton("test_id", "test");

                var mutton = jQuery("#navigationOptions #test_id");

                expect(mutton.length).toEqual(1);
                expect(mutton).toHasClass("mutton");
                expect(mutton.text().strip()).toEqual("test");
            });

            it("should open menu on mutton", function() {
                jQuery("#navigationOptions .mutton").remove();
                spyOn(actionModel, "showDynamicMenu");

                var prototype = $;
                $ = function() {
                    return {
                        text: {
                            evalJSON: function() {
                                return navActionModel;
                            }
                        }
                    }
                }

                var old = primaryNavModule.createMutton;
                spyOn(primaryNavModule, "createMutton").andCallFake(function() {
                    $ = prototype;
                    old.apply(primaryNavModule, arguments);
                });

                primaryNavModule.initializeNavigation();

                var left = 0, top = 0, height = 100;
                primaryNavModule.showNavButtonMenu({}, $(jQuery("#main_view")[0]));

                expect(actionModel.showDynamicMenu).toHaveBeenCalled();

            });

            it("should make test before navigation (positive)", function() {
                spyOn(primaryNavModule, "setNewLocation");
                spyOn(window, "$").andReturn({
                    readAttribute: function() {
                        return "designer";
                    }
                });

                var stub;
                if (window.designerBase) {
                    stub = sinon.stub(designerBase, "confirmAndLeave", function(){return true;})
                } else {
                    window.designerBase = {
                        confirmAndLeave: function() {
                            return true;
                        }
                    }
                }

                primaryNavModule.navigationOption();

                expect(primaryNavModule.setNewLocation).toHaveBeenCalled();

                stub && stub.restore();
            });

            it("should make test before navigation (negative)", function() {
                spyOn(primaryNavModule, "setNewLocation");
                spyOn(window, "$").andReturn({
                    readAttribute: function() {
                        return "designer";
                    }
                });

                var stub;
                if (window.designerBase) {
                    stub = sinon.stub(designerBase, "confirmAndLeave", function(){return false;})
                } else {
                    window.designerBase = {
                        confirmAndLeave: function() {
                            return false;
                        }
                    }
                }

                primaryNavModule.navigationOption();

                expect(primaryNavModule.setNewLocation).not.toHaveBeenCalled();

                stub && stub.restore();
            });

        });
    });