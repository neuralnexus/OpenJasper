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
 * @version: $Id: components.notificationview.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

;
(function ($, _, Backbone, Components) {

    describe("Notification View", function () {

        beforeEach(function () {
            jasmine.getFixtures().set("<div id='controls'></div>");
        });


        describe("Initialization", function () {

            var handleServerErrorStub, view, firstModel, secondModel, firstModelMock, secondModelMock, bindWithServerErrorStub, tempId;

            beforeEach(function () {
                handleServerErrorStub = sinon.stub(Components.NotificationView.prototype, "handleServerError");
                bindWithServerErrorStub = sinon.stub(Components.NotificationView.prototype, "addListeners");
                firstModel = new Backbone.Model();
                firstModelMock = sinon.mock(firstModel);
                secondModel = new Backbone.Model();
                secondModelMock = sinon.mock(secondModel);
                tempId = Components.NotificationView.DEFAULT_TEMPLATE_ID;
            });

            afterEach(function () {
                handleServerErrorStub.restore();
                firstModelMock.restore();
                secondModelMock.restore();
                bindWithServerErrorStub.restore();
                Components.NotificationView.DEFAULT_TEMPLATE_ID = tempId;
            });

            it("should bind server error handler with each model", function () {
                view = new Components.NotificationView({collection:new Backbone.Collection([firstModel, secondModel])});
                expect(bindWithServerErrorStub).toHaveBeenCalledTwice();
            });

            it("should throw exception if no template was found", function () {
                Components.NotificationView.DEFAULT_TEMPLATE_ID = "dasdasdasd";
                expect(function () {
                    view = new Components.NotificationView({collection:new Backbone.Collection([firstModel, secondModel])});
                }).toThrow("Not found template by id 'dasdasdasd'")
            });
        });


        describe("Base functions", function () {

            var view;

            beforeEach(function () {
                view = new Components.NotificationView({collection:new Backbone.Collection()});
            });

            it("chainable render", function () {
                expect(view.render()).toEqual(view);
            });

            xit("should handle error from server", function () {
                var model = new Backbone.Model();
                view.bindWithServerError(model)
            });
        });

       describe("Rendering", function () {

            var view, clock, timeoutSpy;

            beforeEach(function () {
                view = new Components.NotificationView({collection:new Backbone.Collection()});
                $("#controls").append(view.render().el);
                clock = sinon.useFakeTimers();
                timeoutSpy = sinon.spy(window, "setTimeout");
            });

            afterEach(function () {
                clock.restore();
                timeoutSpy.restore();
            });

            it("should render correctly", function () {
                expect($(".notification .message")).toExist();
            });

            describe("Show and hide functionality", function(){

                var hideSpy;

                beforeEach(function(){
                    hideSpy = sinon.spy(view, "hideNotification");

                });

                it("can hide message", function () {
                    view.$el.addClass("error").addClass("success");
                    view.hideNotification();
                    expect(view.$el).not.toHaveClass("error");
                    expect(view.$el).not.toHaveClass("success");
                });

                it("should hide previous notification before", function(){
                    view.showNotification("test message", "test type");
                    expect(hideSpy).toHaveBeenCalled();
                    hideSpy.restore();
                });

                it("should set message and type while displaying notification", function(){
                    view.showNotification("test message", "test type");
                    expect(view.$el).toHaveClass("test type");
                    expect(view.$el.find(".message")).toHaveText("test message");
                });

                it("shouldn't hide message if delay wasn't set", function(){
                    view.showNotification("test message", "test type");
                    clock.tick(10000000000000);
                    expect(hideSpy).not.toHaveBeenCalledTwice();
                });

            });


            it("can show error message", function () {
                var message = "balbaba";
                view.showErrorNotification(message);
                expect($(".message")).toHaveText(message);
                expect($(".notification")).toHaveClass("error");
            });

            it("can show success message", function () {
                var message = "balbaba";
                view.showSuccessNotification(message);
                expect($(".message")).toHaveText(message);
                expect($(".notification")).toHaveClass("success");
            });

            it("can hide error message by default timeout", function () {
                view.showErrorNotification("test", 10000);
                expect(view.$el).toHaveClass("error");
                clock.tick(10000);
                expect(view.$el).not.toHaveClass("error");
            });

            it("can set custom timeout to hiding notification", function () {
                view.showErrorNotification("test", 3000);
                expect(timeoutSpy.args[0][1]).toEqual(3000);
            });

        });

    });


})(
    jQuery,
    _,
    Backbone,
    jaspersoft.components
);
