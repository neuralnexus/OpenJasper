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
 * @version: $Id: export.extendedformview.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "export.formmodel", "export.extendedformview", "text!templates/export.htm"],
function ($, FormModel, ExtendedFormView, exportText) {

    describe("Export's ExtendedFormView", function(){

        var view;

        beforeEach(function(){
            sinon.stub($, "ajax");
            view = new ExtendedFormView({model : new FormModel()});
            setTemplates(exportText, "<div id='exportControls'></div>");
        });

        afterEach(function(){
            $.ajax.restore();
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

                expect($("#exportOptions .leaf").length).toEqual(12);
                expect($("#exportOptions input[type='checkbox']:eq(0)")).toBeChecked();
                expect($("#exportOptions #selectRolesUsers")).toExist();
                expect($("#exportOptions input[type='checkbox']:eq(2)")).not.toBeChecked();
                expect($("#exportOptions input[type='checkbox']:eq(3)")).not.toBeChecked();
                expect($("#exportOptions input[type='checkbox']:eq(4)")).not.toBeChecked();
                expect($("#exportOptions input[type='checkbox']:eq(5)")).not.toBeChecked();

                expect($("#exportOptions input[type='radio']:eq(0)")).toExist();
                expect($("#exportOptions input[type='radio']:eq(1)")).toExist();
                expect($("#exportOptions input[type='radio']:eq(2)")).toExist();
            });

            it("set disabled state", function(){
                view.changeEnabledState = sinon.spy();

                view.render();

                expect(view.changeEnabledState.calledOnce).toBeTruthy();
                expect(view.changeEnabledState.calledWith(view.model.get("everything"))).toBeTruthy();
            });

            it("sets disabled state properly", function(){
                view.changeEnabledState(true);

                expect(view.$el.find(".selectedRoles .disabled")).toExist();
                expect(view.$el.find(".selectedUsers .disabled")).toExist();
                expect(view.$el.find("#roleUsers")).toBeDisabled();
                expect(view.$el.find("#includeAccessEvents")).not.toBeDisabled();

                view.changeEnabledState(false);

                expect(view.$el.find(".selectedRoles")).not.toBeDisabled();
                expect(view.$el.find(".selectedUsers")).not.toBeDisabled();
                expect(view.$el.find("#roleUsers")).not.toBeDisabled();
                expect(view.$el.find("#includeAccessEvents")).toBeDisabled();
            });

            it("render export button", function(){
                expect(view.$el.find((" #exportButton"))).toExist();
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
                fileInput.val("test.zip");
                fileInput.trigger("keyup");
                expect(formModel.get("fileName")).toEqual("test.zip");
            });

            it("can validate filename", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var errorSpan = label.find("span");
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};

                $(fileInput).trigger("keyup");

                expect(label[0]).toHasClass("error");
                expect(errorSpan.text()).toEqual(validationResilt);
            });

            it("can hide error after unsuccessful validation id validation has passed", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};
                $(fileInput).trigger("keyup");

                view.model.validate = function(){};
                $(fileInput).trigger("keyup");

                expect(label[0]).not.toHasClass("error")
            });

            it("should know this it is valid", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){};
                $(fileInput).trigger("keyup");

                expect(view.isValid()).toBeTruthy();
            });

            it("should know this it is invalid", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};
                $(fileInput).trigger("keyup");

                expect(view.isValid()).not.toBeTruthy();
            });

            it("can export everything", function(){
                var exportEverything = $("#exportOptions input[type='checkbox']:eq(0)");
                $(exportEverything).prop("checked", false);
                $(exportEverything).trigger("change");
                expect(formModel.get("everything")).toBeFalsy();
            });

            it("can change disabled state on change of export everything", function(){
                var spy =  sinon.spy(view, "changeEnabledState");

                var exportEverything = $("#exportOptions input[type='checkbox']:eq(0)");
                $(exportEverything).prop("checked", false);
                $(exportEverything).trigger("change");
                $(exportEverything).prop("checked", true);
                $(exportEverything).trigger("change");

                expect(spy.getCall(0).args[0] === false).toBeTruthy();
                expect(spy.getCall(1).args[0] === true).toBeTruthy();

                spy.restore();
            });

            it("can clear pickers if everything selected", function(){
                var users =  sinon.spy(view.usersList, "selectNone");
                var roles =  sinon.spy(view.rolesList, "selectNone");

                var exportEverything = $("#exportOptions input[type='checkbox']:eq(0)");
                $(exportEverything).prop("checked", true);
                $(exportEverything).trigger("change");

                expect(users.calledOnce).toBeTruthy();
                expect(roles.calledOnce).toBeTruthy();

                users.restore();
                roles.restore();
            });

            it("should not clear pickers if everything deselected", function(){
                var users =  sinon.spy(view.usersList, "selectNone");
                var roles =  sinon.spy(view.rolesList, "selectNone");

                var exportEverything = $("#exportOptions input[type='checkbox']:eq(0)");
                $(exportEverything).prop("checked", false);
                $(exportEverything).trigger("change");

                expect(users.called).toBeFalsy();
                expect(roles.called).toBeFalsy();

                users.restore();
                roles.restore();
            });

            it("can include system settings", function(){
                var exportEverything = $("#exportOptions input[type='checkbox']:eq(0)");
                $(exportEverything).prop("checked", false);
                $(exportEverything).trigger("change");

                var includeSysSettings = $("#exportOptions input#includeSystemProperties[type='checkbox']");
                $(includeSysSettings).prop("checked", false);
                $(includeSysSettings).trigger("change");
                expect(formModel.get("includeSystemProperties")).toBeFalsy();

                $(includeSysSettings).prop("checked", true);
                $(includeSysSettings).trigger("change");
                expect(formModel.get("includeSystemProperties")).toBeTruthy();
            });

            it("can include roles by users", function(){
                var rolesByUsers = $("#exportOptions input[type='checkbox']:eq(1)");
                $(rolesByUsers).prop("disabled", false);
                $(rolesByUsers).prop("checked", false);
                $(rolesByUsers).trigger("change");
                expect(formModel.get("userForRoles")).toBeFalsy();
            });

            it("can include access events", function(){
                var includeAccessEvents = $("#includeAccessEvents");
                $(includeAccessEvents).prop("checked", true);
                $(includeAccessEvents).trigger("change");
                expect(formModel.get("includeAccessEvents")).toBeTruthy();
            });

            it("can include audit events", function(){
               var includeAuditEvents = $("#includeAuditEvents");
               $(includeAuditEvents).prop("checked", true);
               $(includeAuditEvents).trigger("change");
               expect(formModel.get("includeAuditEvents")).toBeTruthy();
           });

            it("can include monitor events", function(){
               var includeMonitoringEvents = $("#includeMonitoringEvents");
               $(includeMonitoringEvents).prop("checked", true);
               $(includeMonitoringEvents).trigger("change");
               expect(formModel.get("includeMonitoringEvents")).toBeTruthy();
            });

            it("can include server settings", function(){
                var includeServerSettings = $("#includeSystemProperties");
                $(includeServerSettings).prop("checked", true);
                $(includeServerSettings).trigger("change");
                expect(formModel.get("includeSystemProperties")).toBeTruthy();
            });

            it("can start export by saving form", function(){
               var exportButton = $(" #exportButton");
               $(exportButton).trigger("click");
               expect(formSaveStub).toHaveBeenCalled();
            });

            it("should not start export if form not valid", function(){
                var fileInput = $("#exportDataFile input[type='text']");
                var label = $(fileInput).parent();
                var validationResilt = "invalid";

                view.model.validate = function(){return validationResilt};
                $(fileInput).trigger("keyup");

                var exportButton = $(" #exportButton");
               $(exportButton).trigger("click");
               expect(formSaveStub).not.toHaveBeenCalled();
            });

            it("should set disabled state if nothing selected", function(){
                var button = $("#exportButton").prop("disabled", false);
                spyOn(view.model, "isAcceptable").andReturn(false);
                view.model.trigger("change");
                expect(button).toBeDisabled();
            });

            it("should set enabled state if something was selected", function(){
                var button = $("#exportButton").prop("disabled", true);
                spyOn(view.model, "isAcceptable").andReturn(true);
                view.model.trigger("change");
                expect(button).not.toBeDisabled();
            });

        });

        describe("Authority pickers", function(){
            var users = ["aaa"];
            var roles = ["bbb", "ccc"];

            it("should be", function(){
                expect(view.rolesList).toBeTruthy();
                expect(view.usersList).toBeTruthy();
            });

            it("should be bound with model", function(){

                view.bindWithRoles(roles);
                view.bindWithUsers(users);

                expect(view.model.get("users")).toArrayEquals(users);
                expect(view.model.get("roles")).toArrayEquals(roles);

            });

            it("should be aware of component's events", function(){
                view.bindWithRoles = sinon.spy();
                view.bindWithUsers = sinon.spy();

                view.rolesList.trigger("change:selection", roles);
                view.usersList.trigger("change:selection", users);

                expect(view.bindWithRoles.calledWith(roles));
                expect(view.bindWithUsers.calledWith(users));
            });

        });

    });

});