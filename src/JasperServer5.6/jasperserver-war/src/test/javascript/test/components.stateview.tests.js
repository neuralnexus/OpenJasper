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
 * @version: $Id: components.stateview.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "components.stateview", "components.utils", "components.state"],
function ($, _, StateView, utils, State) {

    describe("StateView", function(){

        var view;

        describe("Initialization", function(){

            var model, modelMock, mockHandler, handleStateChangeStub;

            beforeEach(function(){
                model = new Backbone.Model();
                model.test = function(){};
                modelMock = sinon.mock(model);
                mockHandler = function(){};
                handleStateChangeStub = sinon.stub(StateView.prototype, "handleStateChanges");
            });

            afterEach(function(){
                modelMock.restore();
                handleStateChangeStub.restore();
            });

            it("should bind default handler with state view", function(){
                modelMock.expects("on").once().withArgs("change:phase");
                modelMock.expects("on").once().withArgs("error:server");
                new StateView({model: model});
                modelMock.verify();
            });

        });

        describe("Base functionality", function(){

            var model, modelMock, viewMock, createDeferredDialogStub, dialogDfd, dialogDfdMock;

            beforeEach(function(){
                dialogDfd = new $.Deferred();
                dialogDfdMock = sinon.mock(dialogDfd);
                view = new StateView();
                model = new Backbone.Model({message: "message", phase: "phase"});
                model.reset = function(){};
                modelMock = sinon.mock(model);
                createDeferredDialogStub = sinon.stub(utils, "showLoadingDialogOn");
                viewMock = sinon.mock(view);
                view.dialogDfd = dialogDfd;
            });

            afterEach(function(){
                dialogDfdMock.restore();
                modelMock.restore();
                viewMock.restore();
                createDeferredDialogStub.restore();
            });

            it("can handle state's phase", function(){
                viewMock.expects("handleInprogressPhase").once().withArgs(model);
                view.handleStateChanges(model, State.INPROGRESS);
                viewMock.verify();
            });

            it("can show dialog while inprogress state", function () {
                var dfd = $.Deferred();
                var createDfd = sinon.stub(view, "createDeferredDialog").returns(dfd);
                model.set({phase:State.NOT_STARTED});
                model.set({phase:State.INPROGRESS});

                view.handleInprogressPhase(model);

                expect(createDfd).toHaveBeenCalled();
                expect(view.dialogDfd).toEqual(dfd)
            });

            it("can hide dialog on ready phase", function(){
                dialogDfdMock.expects("resolve").once();
                view.handleStateChanges(model, State.READY);
                viewMock.verify();
            });

            it("can hide dialog on failure phase", function(){
                dialogDfdMock.expects("resolve").once();
                view.handleStateChanges(model, State.FAILED);
                dialogDfdMock.verify();
            });

            it("should hide dialog on error", function(){
                dialogDfdMock.expects("resolve").once();
                view.handleError();
                dialogDfdMock.verify();
            });

            it("can create progress dialog(deferred)", function(){
                var deferedDialog = view.createDeferredDialog();
                expect(deferedDialog).toBeDefined();
                expect(createDeferredDialogStub).toHaveBeenCalled()
            });



        });

    });

});
