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
 * @version: $Id: components.layout.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "underscore",
        "components.dialog",
        "text!templates/export.htm",
        "text!templates/components.htm"],
function ($, _, Dialog, exportText, componentsText){

    var components = jaspersoft.components;

    describe("Layout", function(){

        var layout;

        beforeEach(function(){
            setTemplates(exportText, componentsText, "<div id='test_container'><div class='body'></div></div>") ;
        });

        describe("Initialization", function(){

            var createUIComponetsStub;

            beforeEach(function(){
                createUIComponetsStub = sinon.stub(components.Layout.prototype, "createUIComponents");
            });

            it("should create ui component", function(){
                var args = {};
                new components.Layout(args);
                expect(createUIComponetsStub).toHaveBeenCalledWith(args);
            });

            afterEach(function(){
                createUIComponetsStub.restore();
            });

        });

        describe("Create UI components", function(){

            var extendedViewConstructorStub,
                shortViewConstructorStub,
                modelStub,
                dialogViewConstructorStub,
                stateViewConstructorStub,
                stateStub,
                notificationViewConstructorStub,
                namespace;

            beforeEach(function(){
                modelStub = new Backbone.Model();
                stateStub = new Backbone.Model();
                namespace = {
                    ShortFormView:function(){},
                    ExtendedFormView : function(){}
                };
                modelStub.set({state: stateStub});
                layout = new components.Layout({model: modelStub});
                extendedViewConstructorStub = sinon.stub(namespace, "ExtendedFormView");
                shortViewConstructorStub = sinon.stub(namespace, "ShortFormView");
                dialogViewConstructorStub = sinon.stub(components, "Dialog");
                stateViewConstructorStub = sinon.stub(components, "StateView");
                notificationViewConstructorStub = sinon.stub(components, "SystemNotificationView");
            });

            afterEach(function(){
                extendedViewConstructorStub.restore();
                shortViewConstructorStub.restore();
                dialogViewConstructorStub.restore();
                stateViewConstructorStub.restore();
                notificationViewConstructorStub.restore();
            });

            it("should create short view", function(){
                layout.createUIComponents({type: "ShortFormView", namespace: namespace});
                expect(shortViewConstructorStub).toHaveBeenCalled({model:modelStub});
            });

            it("should create extended view", function(){
                layout.createUIComponents({type: "ExtendedFormView", namespace: namespace});
                expect(extendedViewConstructorStub).toHaveBeenCalled({model:modelStub});
            });

            it("should't create dialog if container specified", function(){
                layout.createUIComponents({container: "#test"});
                expect(dialogViewConstructorStub).not.toHaveBeenCalled();
            });

            it("should create dialog if no container specified", function(){
                layout.createUIComponents({type: "ExtendedFormView", namespace: namespace});
                expect(dialogViewConstructorStub).toHaveBeenCalled({model:modelStub});
            });

            it("should create state view", function(){
                layout.createUIComponents();
                expect(stateViewConstructorStub).toHaveBeenCalledWith({model:stateStub});
            });

            it("should create notification view", function(){
               layout.createUIComponents();
               expect(notificationViewConstructorStub.args[0][0].collection.length).toEqual(2);
            });

        });

        describe("Layout to standard container", function(){

            var formView,formViewMock,dialogView, dialogViewMock, layoutMock,notificationView, notificationViewMock;

             beforeEach(function(){
                 formView = new Backbone.View();
                 dialogView = new Backbone.View();
                 notificationView = new Backbone.View();
                 notificationView.el = $("<div class='notification'></div>");
                 layout = new components.Layout();
                 layoutMock = sinon.mock(layout);
                 formViewMock = sinon.mock(formView);
                 dialogViewMock = sinon.mock(dialogView);
                 notificationViewMock = sinon.mock(notificationView);
             });

             afterEach(function(){
                 formViewMock.restore();
                 dialogViewMock.restore();
                 layoutMock.restore();
                 notificationViewMock.restore();
             });

            it("can layout view if std container exist", function () {
                formViewMock.expects("render").once().withArgs({container:"#testSelector"});
                layout.defaultRender(formView, {container:"#testSelector"});
                formViewMock.verify();
                formViewMock.restore();
                formViewMock.expects("render").never();
                layout.defaultRender(formView);
                formViewMock.verify();
            });

            it("can layout view in dialog", function(){
                dialogViewMock.expects("render").once().withArgs($("body"));
                formViewMock.expects("render").once().withArgs({container: dialogView.$el});
                layout.dialogRender(formView, dialogView);
                dialogViewMock.verify();
                formViewMock.verify();
                //check null safety
                layout.dialogRender();
            });

            it("use dialog if no container was been set", function(){
                layout.formView = formView;
                layout.dialog = dialogView;
                dialogView.$el = $("#test_container");dialogView.el = dialogView.el[0];
                layout.notificationView  = notificationView;
                layoutMock.expects("defaultRender").never();
                layoutMock.expects("dialogRender").once().withArgs(formView,layout.dialog);
                notificationViewMock.expects("render").once();
                layout.render();
                layoutMock.verify();
                notificationViewMock.verify();
                expect($("#test_container .body .notification")).toExist();
            });

            it("delegate render to container if container was been set", function(){
                layout.formView = formView;
                layout.dialog = dialogView;
                layout.notificationView  = notificationView;
                layoutMock.expects("dialogRender").never();
                layoutMock.expects("defaultRender").once().withArgs(formView, {container: $("#test_container")});
                notificationViewMock.expects("render").once();
                layout.render({container: "#test_container"});
                layoutMock.verify();
                notificationViewMock.verify();
                expect($("#test_container .body .notification")).toExist();
            });

        });

        describe("Show dialog", function(){
            beforeEach(function(){
                layout = new components.Layout();
                layout.notificationView = { hideNotification : function(){}};
                layout.dialog = {show: function(){}};
                layout.formView = {prepareToShow: function(){}};

                sinon.spy(layout.notificationView, "hideNotification");
                sinon.spy(layout.dialog, "show");
                sinon.spy(layout.formView, "prepareToShow");
            });

            it("should show dialog", function(){
                layout.showDialog();

                expect(layout.dialog.show.calledOnce).toBeTruthy();
            });

            it("should not fail if dialog is undefined", function(){
                var temp = layout.dialog;
                layout.dialog = undefined;
                layout.showDialog();
                layout.dialog = temp;

                expect(layout.dialog.show.calledOnce).not.toBeTruthy();
            });

            it("should run prepare to show method before show if any", function(){
                layout.showDialog();

                expect(layout.formView.prepareToShow.called).toBeTruthy();
                expect(layout.formView.prepareToShow.calledBefore(layout.dialog.show)).toBeTruthy();

            });

            it("should not fail if prepare to show method absent", function(){
                var temp = layout.formView;
                layout.formView.prepareToShow = undefined;
                layout.showDialog();
                layout.formView.prepareToShow = temp;

                expect(layout.formView.prepareToShow.called).not.toBeTruthy();
            });

            it("should hide notification", function(){
                layout.showDialog();

                expect(layout.notificationView.hideNotification.called).toBeTruthy();
            });
        });
    });

});