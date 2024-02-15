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
 * @version: $Id: export.statecontroller.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "underscore",
        "backbone",
        "components.ajaxdownloader",
        "components.state",
        "components.statecontrollertrait",
        "export.statecontroller"],
        function($, _, Backbone, AjaxDownloader, State, StateControllerTrait, StateController) {

        describe("StateController", function() {

            var controller;

            describe("Initialization", function() {

                it("should initialize downloader", function() {
                    sinon.stub(StateControllerTrait, "initialize");

                    var controller = new StateController();
                    expect(controller.downloader).toBeDefined();
                    expect(StateControllerTrait.initialize).toHaveBeenCalled();

                    StateControllerTrait.initialize.restore();
                });

            });

            describe("Base functionality", function() {

                var model, modelMock, downloaderMock, controllerMock, state;

                beforeEach(function() {
                    controller = new StateController();
                    state = new State({message: "message", phase: "phase"});
                    model = state;
                    model.reset = function() {
                    };
                    modelMock = sinon.mock(model);
                    downloaderMock = sinon.mock(controller.downloader);
                    controllerMock = sinon.mock(controller);
                });

                afterEach(function() {
                    downloaderMock.restore();
                    modelMock.restore();
                    controllerMock.restore();
                });

                it("can generate file url", function() {
                    model.set({id: "uuid"});
                    model.name = "testFileName.zip";
                    expect(controller.getFileUrl(model)).toEqual("rest_v2/export/uuid/testFileName.zip");

                    model.name = "testFileName";
                    expect(controller.getFileUrl(model)).toEqual("rest_v2/export/uuid/testFileName.zip");
                });

                it("can handle ready phase", function() {
                    sinon.stub(controller, "getFileUrl", function() {
                        return "test"
                    });
                    downloaderMock.expects("start").once().withArgs("test");
                    controller.handleReadyPhase(model);
                    downloaderMock.verify();
                });

            });

        });

    });