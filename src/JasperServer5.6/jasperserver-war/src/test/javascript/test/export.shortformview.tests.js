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
 * @version: $Id: export.shortformview.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "export.formmodel", "export.shortformview", "text!templates/export.htm"],
function ($, _, FormModel, ShortFormView, exportText) {

    describe("ShortFormView", function(){

        var view;
        jasmine.getFixtures().set(exportText);

        beforeEach(function(){
            view = new ShortFormView({model: new FormModel()});
            jasmine.getFixtures().set("<div id='exportControls'></div>");
        });

        it("chainable render", function(){
           expect(view.render()).toEqual(view);
        });

        describe("Rendering", function(){

            beforeEach(function(){
                $("#exportControls").append(view.render().el);
            });

            it("render export filename group", function(){
                expect($("#exportDataFile input")).toExist();
                expect($("#exportDataFile input[type='text']")).toHaveValue("export.zip");

            });

            it("render export options", function(){
                expect($("#exportOptions.group")).toExist();

                expect($("#exportOptions .leaf").length).toEqual(2);
                expect($("#exportOptions input[type='checkbox']:eq(0)")).toBeChecked();
                expect($("#exportOptions input[type='checkbox']:eq(1)")).toBeChecked();
            });

            it("render export button", function(){
                expect($("#exportButton")).toExist();
            });

            it("resets it's state before show", function(){
                $("#exportDataFile input[type='text']").val("sss");

                view.prepareToShow();

                expect($("#exportDataFile input[type='text']")).toHaveValue("export.zip");
            });
        });

        describe("Wiring with model by dom events", function(){

            var formModel, formSaveStub;

            beforeEach(function(){
                formModel = view.model;
                formSaveStub = sinon.stub(formModel, "save");
                $("#exportControls").append(view.render().el);
            });

            afterEach(function(){
                formSaveStub.restore();
            });

            it("can edit filename", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                $(fileInput).val("test.zip");
                $(fileInput).trigger("input");
                expect(formModel.get("fileName")).toEqual("test.zip");
            });

            it("can validate filename", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var errorSpan = label.find("span");
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};

                $(fileInput).trigger("input");

                expect(label[0]).toHasClass("error");
                expect(errorSpan.text()).toEqual(validationResilt);
            });

            it("can hide error after unsuccessful validation id validation has passed", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};
                $(fileInput).trigger("input");

                view.model.validate = function(){};
                $(fileInput).trigger("input");

                expect(label[0]).not.toHasClass("error")
            });

            it("should know this it is valid", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){};
                $(fileInput).trigger("input");

                expect(view.isValid()).toBeTruthy();
            });

            it("should know this it is invalid", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};
                $(fileInput).trigger("input");

                expect(view.isValid()).not.toBeTruthy();
            });

            it("can set value for checkbox", function(){
                var checkbox = $("#exportOptions input[type='checkbox']:eq(0)");
                $(checkbox).prop("checked", false);
                $(checkbox).trigger("change");
                expect(formModel.get(checkbox.attr("id"))).toBeFalsy();
            });

            it("can start export by saving form", function(){
               var exportButton = $("#exportButton");
               $(exportButton).trigger("click");
               expect(formSaveStub).toHaveBeenCalled();
            });

            it("should not start export if form not valid", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};
                $(fileInput).trigger("change");

                var exportButton = $("#exportButton");
                $(exportButton).trigger("click");
                expect(formSaveStub).not.toHaveBeenCalled();
            });
        });

        describe("Wiring with model", function(){

            it("can show error message on failed export", function(){
                //TODO:tests before implementation
            })

        });

    });


});