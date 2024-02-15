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
 * @version: $Id: components.statecontrollertrait.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "components.statecontrollertrait", "components.state"],
function ($, _, StateControllerTrait, State) {

    describe("Components.StateControllerTrait", function(){

        var controller,
            timeConfigs = {timeout: 10000, delay: 10000},
            StateControllerType = Backbone.View.extend(StateControllerTrait);

        describe("Initialization", function(){

            var model, modelMock, mockHandler, handleStateChangeStub;

            beforeEach(function(){
                model = new Backbone.Model();
                model.test = function(){};
                modelMock = sinon.mock(model);
                mockHandler = function(){};
                handleStateChangeStub = sinon.stub(StateControllerType.prototype, "handleStateChanges");
            });

            afterEach(function(){
                modelMock.restore();
                handleStateChangeStub.restore();
            });

            it("should bind default handler with state", function(){
                modelMock.expects("on").twice();
                new StateControllerType({model: model});
                modelMock.verify();
                modelMock.restore();
                modelMock.expects("on").never();
                new StateControllerType({model: model});
                modelMock.verify();
            });

        });

        describe("Base functionality", function(){

            var model, modelMock, downloaderMock, controllerMock, state;

            beforeEach(function(){
                controller = new StateControllerType(timeConfigs);
                state = new State({message: "message", phase:"phase"});
                model = state;
                model.reset = function(){};
                modelMock = sinon.mock(model);
                controllerMock = sinon.mock(controller);
            });

            afterEach(function(){
                modelMock.restore();
                controllerMock.restore();
            });

            xit("can handle failed phase", function(){
                modelMock.expects("trigger").once().withArgs("error:server", "message", State.FAILED);
                controllerMock.expects("reset").once();
                controller.handleFailedPhase(model);
                modelMock.verify();
                controllerMock.verify();
            });

            it("can handle state's phase", function(){
                controllerMock.expects("handleReadyPhase").once().withArgs(model);
                controller.handleStateChanges(model, State.READY);
                controllerMock.verify();
                controllerMock.restore();
                controllerMock.expects("handleFailedPhase").once().withArgs(model);
                controller.handleStateChanges(model, State.FAILED);
                controllerMock.verify();
            });

            it("can handle server errors", function(){
                modelMock.expects("defaultErrorDelegator").once().withArgs(model, 1,2,3);
                controllerMock.expects("reset").once();
                controller.handleServerError(model, 1,2,3);
                controllerMock.verify();
            });

            it("can handle inprogress phase", function () {
                var observePhaseStub = sinon.stub(controller, "handleInprogressPhase");
                controller.handleInprogressPhase(state);
                expect(observePhaseStub).toHaveBeenCalled();
            });

        });

        describe("Incremental state update", function(){

            var clock, clearIntervalSpy, state;

            beforeEach(function(){
                 controller = new StateControllerType(timeConfigs);
                 state = new State();
                 //remove default phase observer
                 state.off("change:phase");
                 clock = sinon.useFakeTimers();
                 clearIntervalSpy = sinon.spy(window, "clearInterval");
            });

            afterEach(function(){
                clock.restore();
                clearIntervalSpy.restore();
            });

            it("can reset state and clear interval", function(){
                var stateMock = sinon.mock(state);
                controller.model = state;
                var intervalId = 123;
                controller.intervalId = intervalId;
                stateMock.expects("reset").once();
                controller.reset();
                expect(clearIntervalSpy).toHaveBeenCalledWith(intervalId);
                expect(controller.intervalId ).not.toBeDefined();
                stateMock.verify();
                stateMock.restore();
            });

            it("should set in FAILURE phase after timeout (20sec.)", function(){

               var fetchStub = sinon.stub(state, "fetch", function () {
                   state.set({phase: State.INPROGRESS});
               });

               //emulate setting initial phase
               clock.tick(timeConfigs.delay);
               state.set({phase: State.INPROGRESS});

               var timeout = timeConfigs.delay * 10;
               var observerId = controller.observePhase(state, timeout);
               expect(observerId).toBeDefined();

                while(timeout > 0){
                    clock.tick(timeConfigs.delay);
                    timeout -= timeConfigs.delay;
                    expect(fetchStub).toHaveBeenCalledWith({error: controller.handleServerError});
                }

               expect(clearIntervalSpy).toHaveBeenCalledWith(observerId);
               expect(fetchStub.callCount).toBeGreaterThan(8);
               expect(state.get("phase")).toEqual(State.FAILED);
           });

            it("should stop observing phase after setting not INPROGRESS phase", function(){

                   var fetchStub = sinon.stub(state, "fetch", function () {
                       var now = (new Date()).getTime();
                       if (now < timeConfigs.delay * 6){
                           state.set({phase: State.INPROGRESS});
                       }else{
                           state.set({
                               phase: State.READY,
                               message: "Ready"
                           });
                       }
                   });

                   //emulate setting initial phase
                   clock.tick(timeConfigs.delay);
                   state.set({phase: State.INPROGRESS});

                   var timeout = timeConfigs.delay * 10;
                   var observerId = controller.observePhase(state, timeout);
                   expect(observerId).toBeDefined();

                    while(timeout > timeConfigs.delay * 3){
                        clock.tick(timeConfigs.delay);
                        timeout -= timeConfigs.delay;
                        expect(fetchStub).toHaveBeenCalledWith({error: controller.handleServerError});
                    }

                   expect(clearIntervalSpy).toHaveBeenCalledWith(observerId);
                   expect(fetchStub.callCount).toBeLessThan(7);
                   expect(state.get("phase")).toEqual(State.READY);
                   expect(state.get("message")).toEqual("Ready");
            });

        });
    });
});