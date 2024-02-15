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
 * @version: $Id: controls.viewmodel.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "controls.viewmodel"], function (jQuery, _, Controls) {

    describe("ViewModel", function(){

        afterEach(function(){
            // remove all listeners which were set by Controller initializer
            jQuery(document).unbind();
        });

        it("use default container if it's not in an arguments",function(){
            var viewModel = new Controls.ViewModel();
            expect(viewModel.containerSelector).toEqual(Controls.ViewModel.DEFAULT_CONTAINER);
        });

        it("use container selector from  arguments",function(){
            var viewModel = new Controls.ViewModel({containerSelector : "blabla"});
            expect(viewModel.containerSelector).toEqual("blabla");
        });

        it("put themselves to 'Controls' object on creation of new instance", function(){
            var viewModel = new Controls.ViewModel();
            expect(Controls.getViewModel()).toEqual(viewModel);
        });

        // TODO: fix failing test: need to spy on prototype method and restore it after usage. Problem happen because onControlChange is got bound (aka wrapped)
        xit("listen for 'changed:control' after object creation", function(){
            spyOn(Controls.ViewModel.prototype, "onControlChange");

            var viewModel = new Controls.ViewModel();
            spyOn(viewModel, "onControlChange");

            jQuery(document).trigger(Controls.CHANGE_CONTROL, "test");

            expect(viewModel.onControlChange).toHaveBeenCalled();
            expect(viewModel.onControlChange.mostRecentCall.args[1]).toEqual("test");
        });

        describe(" behaves as Model", function(){

            var model;

            beforeEach(function(){
                model = new Controls.ViewModel();

                Controls["TestControlType_1"] = Controls.BaseControl;
                Controls["TestControlType_2"] = Controls.BaseControl;
                Controls["TestControlType_3"] = Controls.BaseControl;
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("can create control", function(){

                spyOn(Controls, "TestControlType_1").andCallFake(function(){
                    this.markedControls = true;
                });

                var jsonStructure = {
                    type:"testControlType_1",
                    id: "testId"
                };
                model.createControl(jsonStructure);

                expect(Controls.TestControlType_1).toHaveBeenCalledWith(jsonStructure);
                expect(model.getControls()["testId"].markedControls).toBeTruthy();
            });

            it("can remove control", function(){

                model.controls = {
                    test1 : "1",
                    test2 : "2"
                };

                model.removeControl("test2");
                expect(model.getControls()).toEqual({test1:"1"});

            });

            it("can instantiate", function(){
                spyOn(model, "createControl");

                model.instantiate([{
                    id :"test1",
                    type : "testControlType_1"
                },{
                    id :"test2",
                    type : "testControlType_2"
                },{
                    id :"test3",
                    type : "testControlType_3"
                }]);

                expect(model.createControl.callCount).toEqual(3);
            });

            it("can get all controls", function(){
                model.controls = "test";
                expect(model.getControls()).toEqual("test");
            });

            it("can get 'selection' from all controls except controls with undefined values", function(){

                var testControl1 = {
                    id: "test1",
                    get:jasmine.createSpy('get').andCallFake(function(){
                        return "1";
                    })
                };

                var testControl2 = {
                    id:"test2",
                    get:jasmine.createSpy('get').andCallFake(function(){
                        return ["1","2","3"];
                    })
                };

                var testControl3 = {
                    id:"test3",
                    get:jasmine.createSpy('get').andCallFake(function(){
                        return undefined;
                    })
                };

                model.controls = {
                    "test1" : testControl1,
                    "test2" : testControl2,
                    "test3" : testControl3
                };

                expect(model.get('selection')).toEqual({
                    "test1" : ["1"],
                    "test2" : ["1","2","3"]
                });
                expect(testControl1.get).toHaveBeenCalledWith('selection');
                expect(testControl2.get).toHaveBeenCalledWith('selection');
                expect(testControl3.get).toHaveBeenCalledWith('selection');

            });

            it("can get any attribute thought 'get'", function(){
                model.blabla = "test";
                expect(model.get('blabla')).toEqual("test");
            });

            it("can find control by any attribute",function(){

                var testControl1 = {
                    id: "test1",
                    blabal: "2"
                };

                var testControl2 = {
                    id:"test2",
                    blabal: "22"
                };

                model.controls= {
                     "test1" : testControl1,
                     "test2" :testControl2
                };

                spyOn(_, "find").andCallThrough();
                spyOn(model, "getControls").andCallThrough();

                expect(model.findControl({id:"test1"})).toEqual(testControl1);

                expect(model.getControls).toHaveBeenCalled();
                expect(_.find).toHaveBeenCalled();

                expect(model.findControl({blabal:"2"})).toEqual(testControl1);
                expect(model.findControl({id:"test2"})).toEqual(testControl2);
                expect(model.findControl({blabal:"22"})).toEqual(testControl2);
            });

            it("can 'pluck' any property from all controls", function(){
                 spyOn(model,"getControls");
                 spyOn(_, "pluck").andCallThrough();
                 model.pluck("blabla");

                 expect(model.getControls).toHaveBeenCalled();
                 expect(_.pluck.mostRecentCall.args[1]).toEqual("blabla");
            });

            it("can set 'structure' and trigger instantiation",function(){
                spyOn(model, "instantiate");
                spyOn(model, "draw");

                var testStructure = {
                    control1:"test1",
                    control2:"test2"
                };
                model.set({structure : testStructure});

                expect(model.instantiate).toHaveBeenCalledWith(testStructure);
                expect(model.draw).toHaveBeenCalledWith(testStructure);
            });

            it("can set 'state' and trigger update",function(){
                spyOn(model, "update");
                spyOnEvent(jQuery(document), Controls.ViewModel.CHANGE_VALUES);

                var testState = {
                    control1: "test1",
                    control2: "test2"
                };
                model.set({state: testState});

                expect(model.update).toHaveBeenCalledWith(testState);
                expect(Controls.ViewModel.CHANGE_VALUES).toHaveBeenTriggeredOn(jQuery(document));
            });

            it("can set any attribute",function(){
                var testAttribute = {
                    blabla:{a:"1", b:"2"}
                };
                model.set(testAttribute);
                expect(model.blabla).toEqual({a:"1", b:"2"});
            });

            describe("reorders structure", function(){
                it("length: 1, order ->",function(){
                    model.structure = ["a"];
                    var reordered = ["a"];

                    model.reorderControl(0, 1);

                    expect(model.structure).toArrayEquals(reordered);
                });

                it("length: 2, order ->",function(){
                    model.structure = ["a", "b"];
                    var reordered = ["b", "a"];

                    model.reorderControl(0, 1);

                    expect(model.structure).toArrayEquals(reordered);
                });

                it("length: 2, order <-",function(){
                    model.structure =  ["a", "b"];
                    var reordered = ["b", "a"];

                    model.reorderControl(1, 0);

                    expect(model.structure).toArrayEquals(reordered);
                });

                it("length: 3, order <-, neighbours",function(){
                    model.structure =  ["a", "b", "c"];
                    var reordered = ["b", "a" , "c"];

                    model.reorderControl(1, 0);

                    expect(model.structure).toArrayEquals(reordered);
                });

                it("length: 3, order ->, neighbours",function(){
                    model.structure =  ["a", "b", "c"];
                    var reordered = ["b", "a" , "c"];

                    model.reorderControl(0, 1);

                    expect(model.structure).toArrayEquals(reordered);
                });

                it("length: 3, order <-, not neighbours",function(){
                    model.structure =  ["a", "b", "c"];
                    var reordered = ["c", "a", "b"];

                    model.reorderControl(2, 0);

                    expect(model.structure).toEqual(reordered);
                });

                it("length: 3, order ->, not neighbours",function(){
                    model.structure =  ["a", "b", "c"];
                    var reordered = ["b", "c", "a"];

                    model.reorderControl(0, 2);

                    expect(model.structure).toEqual(reordered);
                });
            });

        });

        describe(" behaves as View", function(){
            var view , testControl1, testControl2, testControl3, setSpy, state;

            beforeEach(function () {
                jasmine.getFixtures().set("<div id='inputControlsContainer'></div>");
                view = new Controls.ViewModel();
                setSpy = jasmine.createSpy('set');

                testControl1 = {
                    id:"test1",
                    visible:true,
                    getElem:jasmine.createSpy('getElem').andCallFake(function () {
                        return "elem1";
                    }),
                    set: setSpy,
                    isValid: function(){return true;}
                };
                testControl2 = {
                    id:"test2",
                    visible:true,
                    getElem:jasmine.createSpy('getElem').andCallFake(function () {
                        return "elem2";
                    }),
                    set: setSpy,
                    isValid: function(){return false;}
                };
                testControl3 = {
                    id:"test3",
                    getElem:jasmine.createSpy('getElem'),
                    set : setSpy,
                    isValid: function(){return true;}
                };

                view.controls = {
                    "test1":testControl1,
                    "test2":testControl2,
                    "test3":testControl3
                };

                state = {
                    "test1":{
                        values:"1"
                    },
                    "test2":{
                        values:["1", "2", "3"],
                        error:"palundra!!!"
                    },
                    "test3":{
                        values:"3"
                    }
                }
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("can get container",function(){
                expect(view.getContainer()).toHaveHtml(jQuery(Controls.ViewModel.DEFAULT_CONTAINER).html());
            });

            it("can remove all validation messages", function(){
                  view.removeValidationMessages();
                  expect(setSpy.callCount).toEqual(3);
                  var expectedArgs = {error:null};
                  expect(setSpy.argsForCall[0][0]).toEqual(expectedArgs);
                  expect(setSpy.argsForCall[1][0]).toEqual(expectedArgs);
                  expect(setSpy.argsForCall[2][0]).toEqual(expectedArgs);
            });

            it("can put controls in specific container",function(){
                var container = view.getContainer();
                spyOn(container, "empty").andCallThrough();
                spyOn(container, "append");
                spyOn(view, "getContainer").andCallThrough();

                view.draw([testControl1, testControl2, testControl3]);

                expect(view.getContainer).toHaveBeenCalled();
                expect(container.empty).toHaveBeenCalled();

                expect(container.append.argsForCall[0][0]).toEqual("elem1");
                expect(container.append.argsForCall[1][0]).toEqual("elem2");

            });

            it("can update state of all controls",function(){
                spyOn(view, "removeValidationMessages");

                view.update(state);

                expect(setSpy.argsForCall[0][0]).toEqual({
                    "values":state.test1.values
                });
                expect(setSpy.argsForCall[1][0]).toEqual({
                    "values":state.test2.values,
                    "error":state.test2.error
                });
                expect(setSpy.argsForCall[2][0]).toEqual({
                    "values":state.test3.values
                });
            });

            it("can check for validness",function(){
                expect(view.areAllControlsValid()).toBeFalsy();
            });

            it("can set container",function(){
                jasmine.getFixtures().set("<div id='otherContainer'>test</div>");
                view.setContainer("#otherContainer");
                expect(view.getContainer()).toHaveId("otherContainer");
            });

            it("can reload container",function(){
                view.container = null;
                view.reloadContainer();
                expect(view.getContainer()).toHaveId("inputControlsContainer");
            });

            it("can disable/enable all controls", function(){
                view.disable();

                var expectedArg = {disabled:true};
                expect(setSpy.argsForCall[0][0]).toEqual(expectedArg);
                expect(setSpy.argsForCall[1][0]).toEqual(expectedArg);
                expect(setSpy.argsForCall[2][0]).toEqual(expectedArg);
                setSpy.reset();

                view.enable();

                expectedArg.disabled = false;
                expect(setSpy.argsForCall[0][0]).toEqual(expectedArg);
                expect(setSpy.argsForCall[1][0]).toEqual(expectedArg);
                expect(setSpy.argsForCall[2][0]).toEqual(expectedArg);
            });
        });

        describe(" behaves as Graph", function(){

            var graph;

            beforeEach(function () {
                graph = new Controls.ViewModel();

                graph.controls = {
                    "test1" :{
                        id : "test1",
                        slaveDependencies: ["test2"],
                        masterDependencies:[],
                        get:jasmine.createSpy("get").andCallFake(function(){
                            return "1";
                        })
                    },
                    "test2":{
                        id : "test2",
                        slaveDependencies: ["test3","test4"],
                        masterDependencies: ["test1"],
                        get:jasmine.createSpy("get").andCallFake(function(){
                            return ["1","2","3"];
                        })
                    },
                    "test3":{
                        id : "test3",
                        masterDependencies: ["test2"],
                        get:jasmine.createSpy("get").andCallFake(function(){
                            return ["a", "abc"];
                        })
                    },
                    "test4": {
                        id:"test4",
                        masterDependencies: ["test2"],
                        get:jasmine.createSpy("get").andCallFake(function(){
                            return "test";
                        })
                    },
                    "test5": {
                        id:"test5",
                        masterDependencies: [],
                        get:jasmine.createSpy("get").andCallFake(function(){
                            return "alone";
                        })
                    }
                };

                //lock in onControlChange
                graph.state = {
                    "test1":"1",
                    "test2":["1", "2", "3"],
                    "test3":["a", "abc"],
                    "test4":"test",
                    "test5":"alone"
                }
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("can get all immediate and transitive slave dependencies",function(){
               expect(graph._getAllSlaveControlIds("test1")).toEqual(["test2","test3","test4"]);
               expect(graph._getAllSlaveControlIds("test2")).toEqual(["test3","test4"]);
               expect(graph._getAllSlaveControlIds("test3")).toEqual([]);
               expect(graph._getAllSlaveControlIds("test4")).toEqual([]);
               expect(graph._getAllSlaveControlIds("test5")).toEqual([]);
            });

            it("can get all parent control ids",function(){
                expect(graph._getParentControlIds(["test4"])).toEqual(["test2"]);
                expect(graph._getParentControlIds(["test3","test4"])).toEqual(["test2"]);
                expect(graph._getParentControlIds(["test2"])).toEqual(["test1"]);
                expect(graph._getParentControlIds(["test1"])).toEqual([]);
                expect(graph._getParentControlIds(["test5"])).toEqual([]);
            });

            it("cut selection on change in cascade",function(){

                Controls.listen({
                    "viewmodel:selection:changed" : function(event, selection, controlInCascade){
                        expect(selection).toEqual({
                              "test2" : ["1","2","3"],
                              "test3" : ["a","abc"],
                              "test4" : ["test"]
                        });
                        expect(controlInCascade).toBeTruthy();
                    }
                });
                graph.onControlChange({}, graph.controls.test2);

                Controls.ignore("viewmodel:selection:changed");
            });

            it("cut selection on change out of cascade triggers change event",function(){

                Controls.listen({
                    "viewmodel:values:changed" : function(event, selection, controlInCascade){
                        expect(selection).toEqual({
                            "test1" : "1",
                            "test2" : ["1","2","3"],
                            "test3" : ["a","abc"],
                            "test4" : "test",
                            "test5" : "alone"
                        });
                        expect(controlInCascade).toBeFalsy();
                    }
                });

                graph.onControlChange({}, graph.controls.test3);

                Controls.ignore("viewmodel:values:changed");
            });
        });

        describe("Detect changes in selection", function(){

            var isSelectionChanged;

            beforeEach(function(){
                isSelectionChanged = Controls.ViewModel.isSelectionChanged;
            });

            it("compare to selections",function(){

                var previous = {
                    test1: ["a"],
                    test2 : [1,2,3],
                    test3 : ["c"]
                };

                var next =  {
                    test1: ["a"],
                    test2 : [1,2,3],
                    test3 : ["c"]
                };
                expect(isSelectionChanged(previous, next)).toBeFalsy();

                previous = {
                    test1:["a"],
                    test2:[1, 2, 3],
                    test3:["c"]
                };

                next = {
                    test1:["a"],
                    test2:[1, 2, 4],
                    test3:["c"]
                };
                expect(isSelectionChanged(previous, next)).toBeTruthy();

                previous = {
                    test1:["a"],
                    test2:[1, 5, 3],
                    test3:["c"]
                };

                next = {
                    test1:["a"],
                    test2:[1, 2, 3],
                    test3:["c"]
                };

                expect(isSelectionChanged(previous, next)).toBeTruthy();

                previous = {
                    test1:["a"],
                    test2:[1],
                    test3:["c"]
                };

                next = {
                    test1:["a", "b", "c"],
                    test2:[1, 5, 3],
                    test3:["c"]
                };
                expect(isSelectionChanged(previous, next)).toBeTruthy();


                previous = {
                    test1:["a", "b", "c"],
                    test2:[1, 5, 3],
                    test3:["c"]
                };

                next = {
                    test1:["a"],
                    test2:[1],
                    test3:["c"]
                };
                expect(isSelectionChanged(previous, next)).toBeTruthy();
            });

            it("compare with empty",function(){

                var previous = {
                    test1:["a"],
                    test2:[1, 2, 3],
                    test3:["c"]
                };
                var next = {};
                expect(isSelectionChanged(previous, next)).toBeTruthy();

                next = {
                    test1:["a"],
                    test2:[1, 2, 3],
                    test3:["c"]
                };
                previous = {};

                expect(isSelectionChanged(previous, next)).toBeTruthy();

                next = {};
                previous = {};

                expect(isSelectionChanged(previous, next)).toBeFalsy();
            });

            it("compare with null/undefined", function () {
                var previous = {
                    test1:["a"],
                    test2:[1, 2, 3],
                    test3:["c"]
                };
                var next;
                expect(isSelectionChanged(previous, next)).toBeTruthy();

                next = {
                    test1:["a"],
                    test2:[1, 2, 3],
                    test3:["c"]
                };
                previous = undefined;

                expect(isSelectionChanged(previous, next)).toBeTruthy();

            });
        });
    });
});
