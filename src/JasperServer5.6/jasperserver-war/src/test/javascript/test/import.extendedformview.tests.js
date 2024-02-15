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
 * @version: $Id: import.extendedformview.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "underscore",
        "import.extendedformview",
        "import.formmodel",
        "text!templates/import.htm",
        "text!templates/components.htm"],
    function ($, _, ExtendedFormView, FormModel, importText, componentsText) {

    describe("Import ExtendedFormView", function(){

        var view;

        beforeEach(function(){
            setTemplates(importText,componentsText, "<div id='importControls'></div>");
            view = new ExtendedFormView({model : new FormModel()});
        });

        it("chainable render", function(){
           expect(view.render()).toEqual(view);
        });


        describe("Rendering", function(){

            beforeEach(function(){
                $("#importControls").append(view.render().el);
            });

            it("render filename group", function(){
                expect($("#importDataFile input")).toExist();

            });

            it("render import options", function(){
                expect($("#importOptions.group")).toExist();

                expect($("#importOptions .leaf").length).toEqual(6);
                expect($("#importOptions input[type='checkbox']:eq(0)")).toBeChecked();
                expect($("#importOptions input[type='checkbox']:eq(1)")).not.toBeChecked();
                expect($("#importOptions input[type='checkbox']:eq(2)")).toBeChecked();
                expect($("#importOptions input[type='checkbox']:eq(3)")).toBeChecked();
                expect($("#importOptions input[type='checkbox']:eq(4)")).toBeChecked();
                expect($("#importOptions input[type='checkbox']:eq(5)")).not.toBeChecked();
            });

            it("render import button", function(){
                expect($("#controlButtons #importButton")).toExist();
            });

        });

        describe("Wiring with model by dom events", function(){

            var formModel;

            beforeEach(function(){
                formModel = view.model;
                $("#importControls").append(view.render().el);
                formModel.get("state").reset();
            });

            xit("can change model by clicking on checkbox", function(){
                var checkbox = $("#importOptions input[type='checkbox']:eq(0)");
                $(checkbox).trigger("click");
                expect(formModel.get("update")).toBeFalsy();
            });

            //it works in real app, but in test environment it won't work
            //TODO:fix
            xit("can check checkbox by clicking on its label ", function(){
                var checkbox = $("#importOptions input[type='checkbox']:eq(0)");
                var label  = $("#importOptions .checkBox label:eq(0)");
                var value = checkbox[0].checked;

                label.trigger("click");
                expect(checkbox[0].checked).not.toEqual(value);
            });

            xit("can change state on import", function(){
                view.valid = true;
                var exportButton = $("#controlButtons #importButton");
                $(exportButton).trigger("click");
                delete view.valid;

                expect(formModel.get("state").get("phase")).toEqual("inprogress");
            });

            it("should not change state if invalid", function(){
                var exportButton = $("#controlButtons #importButton");
                $(exportButton).trigger("click");

                expect(formModel.get("state").get("phase")).not.toEqual("inprogress");
            });

            it("can validate filename", function(){
                var input = $('<input type="text" value="zzz.zip">');
                var container = $('<div></div>').append(input);

                view.validateFile({target:input});

                expect(container).not.toHasClass("error");
                expect($("#importButton")).not.toBeDisabled();
            });

            it("can validate wrong filename", function(){
                var input = $('<input type="text" value="zzz.png">');
                var container = $('<div></div>').append(input);

                view.validateFile({target:input});

                expect(container).toHasClass("error");
                expect($("#importButton")).toBeDisabled();
            });

            it("can recover from last error", function(){
                var input = $('<input type="text" value="zzz.png">');
                var container = $('<div></div>').append(input);

                view.validateFile({target:input});
                input.val("ll.zip");
                view.validateFile({target:input});

                expect(container).not.toHasClass("error");
                expect($("#importButton")).not.toBeDisabled();
            });

            it("sets disabled state to sub inputs", function(){
                var input = $("#update");
                var dependentInput = $("#skipUserUpdate");

                view.changeEnabledState(input, true);
                expect(dependentInput).toBeDisabled();

                view.changeEnabledState(input, false);
                expect(dependentInput).not.toBeDisabled();
            });

            it("sets disabled state to sub inputs on change of value", function(){
                spyOn(view, "changeEnabledState").andCallThrough();
                var input = $("#update");

                input.trigger("change");
                expect(view.changeEnabledState.wasCalled).toBeTruthy();
            });
        });
    });


});