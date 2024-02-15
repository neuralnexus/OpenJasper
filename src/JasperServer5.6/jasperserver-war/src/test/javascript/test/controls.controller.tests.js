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
 * @author: inesterenko
 * @version: $Id: controls.controller.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "controls.controller"],
    function (jQuery, _, Controls) {

    describe("Controller", function(){

        var controller;

        beforeEach(function () {
            controller = new Controls.Controller({
                reportUri: "testUri",
                viewModel : new Controls.ViewModel(),
                dataTransfer : new Controls.DataTransfer({
                    dataConverter : new Controls.DataConverter()
                })
            });

            controller.viewModel.controls = {
                "test1": {id:"test1", value:1},
                "test2": {id:"test2", value:2},
                "test3": {id:"test3", value:3}
            };
        });

        afterEach(function(){
            // remove all listeners which were set by Controller initializer
            jQuery(document).unbind();
        });

        describe("Initialization", function(){

            it("can bind listeners and put themselves in 'Controls' namespace on instantiation",function(){

                expect(controller.getDataTransfer()).toBeDefined();
                expect(controller.getViewModel()).toBeDefined();

                spyOn(controller, "updateControlsValues");
                spyOn(controller, "reset");

                jQuery(document).trigger(Controls.ViewModel.CHANGE_SELECTION, ["selectedData","controlsIds", true]);
                jQuery(document).trigger("reportoptions:selection:changed", {uri : "reportOptionUri"});

                expect(controller.updateControlsValues).toHaveBeenCalledWith("selectedData","controlsIds");
                expect(controller.reset).toHaveBeenCalledWith("reportOptionUri");

                expect(Controls.getController()).toEqual(controller);
            });

            it("can request controls structure",function(){
                var viewModel = controller.getViewModel();
                var dataTransfer = controller.getDataTransfer();
                var expectedResponse = {
                    structure : "testStructure",
                    state : "testState"
                };
                spyOn(viewModel, "set");
                spyOn(dataTransfer, "fetchControlsStructure").andReturn((new jQuery.Deferred()).resolve(expectedResponse));

                controller.fetchControlsStructure({});

                expect(dataTransfer.fetchControlsStructure).toHaveBeenCalledWith([],{});
                expect(viewModel.set).toHaveBeenCalledWith(expectedResponse);
            });

            it("can fetch preselected data, merged with url params",function(){

                var dataTransfer = controller.getDataTransfer();
                spyOn(dataTransfer, "fetchControlsStructure").andReturn((new jQuery.Deferred()).reject());

                var preSelectedData = {
                        "test1":"a",
                        "test2":[1, 2, 3]
                };
                controller.fetchControlsStructure(preSelectedData);

                expect(dataTransfer.fetchControlsStructure).toHaveBeenCalledWith([], preSelectedData);
            });
        });

        it("can get view model",function(){
            expect(controller.getViewModel()).toBeDefined();
        });

        it("can get data transfer",function(){
            expect(controller.getDataTransfer()).toBeDefined();
        });

        describe("Usage of report uri and report options uri ", function(){
            it("can store report uri and reportOption uri", function(){

                controller.detectReportUri({
                    reportUri : "testReportUri",
                    reportOptionUri: "testReportOptionUri"
                });

                expect(controller.reportUri).toEqual("testReportUri");
                expect(controller.reportOptionUri).toEqual("testReportOptionUri");
            });

            it("should throw error if no report uri",function(){
                expect(function(){
                    controller.detectReportUri();
                }).toThrow("Can't initialize without reportUri")
            });

            it("can find report uri and report option uri in url if no in arguments",function(){
                sinon.stub(jQuery.url, "parse").returns({
                    params : {
                        reportUnitURI : "reportUriFromUrl",
                        reportOptionsURI : "reportOptionUriFromUrl"
                    }
                });

                controller.detectReportUri();

                expect(controller.reportUri).toEqual("reportUriFromUrl");
                expect(controller.reportOptionUri).toEqual("reportOptionUriFromUrl");

                jQuery.url.parse.restore();
            });
        });

        describe("Update/Reset controls values by uri and selection", function(){

            var dataTransfer, viewModel;

            beforeEach(function () {

                dataTransfer = controller.getDataTransfer();
                viewModel = controller.getViewModel();
                spyOn(dataTransfer, "fetchControlsUpdatedValues").andReturn((new jQuery.Deferred()).resolve("testResponse"));
                spyOn(dataTransfer, "fetchInitialControlValues").andReturn((new jQuery.Deferred()).resolve("testResponse"));
                spyOn(viewModel, "set");
            });

            it("update by given selectedData and controlIds ",function(){
                controller.updateControlsValues(
                    {"test1":"aaaaa", "test2":"bbbbb"},
                    ["test1", "test2"]
                );
                expect(dataTransfer.fetchControlsUpdatedValues).toHaveBeenCalledWith(
                    ["test1", "test2"],
                    {"test1":"aaaaa", "test2":"bbbbb"}
                );
                expect(viewModel.set).toHaveBeenCalledWith("testResponse");
            });

            it("update only by selectedData",function(){
                controller.updateControlsValues({
                        "test1":"aaaaa", "test2":"bbbbb"
                });
                expect(
                    dataTransfer.fetchControlsUpdatedValues
                ).toHaveBeenCalledWith(
                    ["test1", "test2", "test3"],
                    {"test1":"aaaaa", "test2":"bbbbb"}
                );
                expect(viewModel.set).toHaveBeenCalledWith("testResponse");
            });

            it("reset by given uri",function(){
                controller.reset("test");
                expect(dataTransfer.fetchInitialControlValues).toHaveBeenCalledWith("test");
                expect(viewModel.set).toHaveBeenCalledWith("testResponse");
            });

            it("reset by report option uri",function(){
                controller.reportUri = "testReportUri";
                controller.reportOptionUri = "reportOptionUri";
                controller.reset();
                expect(dataTransfer.fetchInitialControlValues).toHaveBeenCalledWith("reportOptionUri");
                expect(viewModel.set).toHaveBeenCalledWith("testResponse");
            });

            it("reset by report uri",function(){
                controller.reportUri = "testReportUri";
                controller.reset();
                expect(dataTransfer.fetchInitialControlValues).toHaveBeenCalledWith("testReportUri");
                expect(viewModel.set).toHaveBeenCalledWith("testResponse");
            });

        });

        describe("General update", function(){
            beforeEach(function () {
                spyOn(controller, "updateControlsValues");
                spyOn(controller.getViewModel(), "get").andReturn("testSelection");
            });

            it("selection was changed",function(){
                controller.update();
                expect(controller.updateControlsValues).toHaveBeenCalledWith("testSelection");
            });

        });

        describe("General validation", function(){
            
            var dataTransfer, viewModel;
            
            beforeEach(function () {
                dataTransfer = controller.getDataTransfer();
                viewModel = controller.getViewModel();
                
                spyOn(dataTransfer, "fetchControlsUpdatedValues").andReturn((new jQuery.Deferred()).resolve({
                    state:{
                        "test1":{
                            error:null,
                            values:[1, 2, 3]
                        },
                        "test2":{
                            error:"palundra!!!",
                            values:["a", "b", "c"]
                        },
                        "test3":{
                            error:null,
                            values:"123"
                        },
                        "test4":{
                            error:"ones more error",
                            values:"123"
                        }
                    }
                }));
                spyOn(viewModel, "set");
                spyOn(viewModel, "get");
            });

            it("allow to check validness of controls, not single value control", function(){
                spyOn(viewModel, "areAllControlsValid").andReturn(false);

               controller.validate().then(function(isControlsValid){
                    expect(isControlsValid).toBeFalsy();
               });
               expect(viewModel.set).toHaveBeenCalledWith({
                   state : {
                       "test1":{
                           error:null
                       },
                       "test2":{
                            error:"palundra!!!"
                       },
                       "test3":{
                           error:null
                       },
                       "test4":{
                           error:"ones more error"
                       }

                   }
               });
            });

            it("allow to check validness", function () {
                spyOn(viewModel, "areAllControlsValid").andReturn(true);

                controller.validate().then(function (isControlsValid) {
                    expect(isControlsValid).toBeTruthy();
                });
            });
        });

    });
});
