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
 * @version: $Id: controls.basecontrol.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "controls.basecontrol"], function (jQuery, Controls){
    describe("BaseControl", function(){

        describe("creation", function () {

            beforeEach(function () {
                this.baseRender = Controls.BaseControl.prototype.baseRender;
                this.bindEvents = Controls.BaseControl.prototype.bindCustomEventListeners;
                this.init = Controls.BaseControl.prototype.initialize;
            });
            afterEach(function () {
                Controls.BaseControl.prototype.baseRender = this.baseRender;
                Controls.BaseControl.prototype.bindCustomEventListeners = this.bindEvents;
                Controls.BaseControl.prototype.initialize = this.init;
            });

            it("can initialize", function () {
                spyOn(Controls, "BaseControl");
                new Controls.BaseControl({test:"test"});
                expect(Controls.BaseControl).toHaveBeenCalledWith({test:"test"});
            });

            it("should invoke base rendering and bind custom events", function () {
                var baseRenderSpy = jasmine.createSpy("baseRender");
                var bindCustomEventsSpy = jasmine.createSpy("bindCustomEventListeners");

                Controls.BaseControl.prototype.baseRender = baseRenderSpy;
                Controls.BaseControl.prototype.bindCustomEventListeners = bindCustomEventsSpy;
                var args = {test:"test", visible: true};
                new Controls.BaseControl(args);

                expect(baseRenderSpy).toHaveBeenCalledWith(args);
                expect(bindCustomEventsSpy).toHaveBeenCalled();
            });

            it("should not invoke binding custom events on invisible control", function () {
                var bindCustomEventsSpy = jasmine.createSpy("bindCustomEventListeners");

                Controls.BaseControl.prototype.bindCustomEventListeners = bindCustomEventsSpy;
                var args = {test:"test", visible: false};
                new Controls.BaseControl(args);

                expect(bindCustomEventsSpy).not.toHaveBeenCalled();
            });

            it("has base render", function () {
                Controls.BaseControl.prototype.initialize = function () {
                };
                spyOn(Controls.TemplateEngine, "createTemplate").andCallFake(function () {
                    return function () {
                        return "<div id='test'>aaa</div>"
                    };
                });
                var baseControl = new Controls.BaseControl();
                spyOn(baseControl, "setElem");

                baseControl.baseRender({type:"baseType"});

                expect(Controls.TemplateEngine.createTemplate).toHaveBeenCalledWith("baseType");
                expect(baseControl.setElem.mostRecentCall.args[0][0].outerHTML.toLowerCase().strip().replace("\"test\"", "test")).toEqual('<div id=test>aaa</div>');

            });
        });

        describe("useful functions", function(){
            var baseControl, testValues;

            beforeEach(function(){
                baseControl = new Controls.BaseControl({type:"baseType", visible: true});
                testValues = [{
                    value:1, label:1, selected : true
                },{
                    value:2, label:2
                },{
                    value:3, label:3, selected : true
                }];
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("can set or get elem property", function(){
               baseControl.setElem("test");
               expect(baseControl.getElem()).toEqual("test");
            });

            it("can fire change control event", function(){
                var spyListener = jasmine.createSpy("spyListener");
                Controls.listen({
                    "changed:control" : spyListener
                });
                baseControl.fireControlSelectionChangeEvent();
                expect(spyListener).toHaveBeenCalled();
                expect(spyListener.mostRecentCall.args[1]).toEqual(baseControl);

            });

            it("can be disabled, enabled", function(){
                jasmine.getFixtures().set("<div id='test1'><input value='test'/><select></select></div>");
                baseControl.elem = jQuery("#test1");

                baseControl.disable();

                expect(jQuery("#test1 input")).toBeDisabled();
                expect(jQuery("#test1 select")).toBeDisabled();

                baseControl.enable();

                expect(jQuery("#test1 input")).not.toBeDisabled();
                expect(jQuery("#test1 select")).not.toBeDisabled();
            });

            it("can update warning message", function(){
                var message = "bad news";
                jasmine.getFixtures().set("<div id='test1'><span class='warning'></span></div>");

                baseControl.elem = jQuery("#test1");
                baseControl.error = message;
                baseControl.updateWarningMessage();

                expect(baseControl.elem.find(".warning")).toHaveText(message);

                baseControl.error = null;
                baseControl.updateWarningMessage();

                expect(baseControl.elem.find(".warning")).toHaveText("");
            });

            it("can get template section", function(){
                spyOn(Controls.TemplateEngine, "createTemplateSection").andCallFake(function(section){
                    return "test";
                });
                var resultSection = baseControl.getTemplateSection("data");
                expect(Controls.TemplateEngine.createTemplateSection).toHaveBeenCalledWith("data", "baseType");
                expect(resultSection).toEqual("test");

                resultSection = baseControl.getTemplateSection("data");

                expect(Controls.TemplateEngine.createTemplateSection).toHaveBeenCalledNTimes(1);
                expect(resultSection).toEqual("test");
            });

            it("can return 'selection' or any other property", function(){
                baseControl.selection = [1,3];
                expect(baseControl.get('selection')).toEqual([1,3]);

                baseControl.value = 1 ;

                expect(baseControl.get('value')).toEqual(1);

                baseControl.test = "palundra!";

                expect(baseControl.get('test')).toEqual("palundra!");
            });

            it("check validness",function(){
                expect(baseControl.isValid()).toBeTruthy();
                baseControl.error = "palundra!";
                expect(baseControl.isValid()).toBeFalsy();
            });

            it("can refresh",function(){

                spyOn(baseControl, "get").andCallFake(function(){
                    return [1,2,3];
                });
                spyOn(baseControl, "initialize");
                spyOn(baseControl, "update");

                baseControl.refresh();

                expect(baseControl.get).toHaveBeenCalledWith('values');
                expect(baseControl.initialize).toHaveBeenCalledWith(baseControl);
                expect(baseControl.update).toHaveBeenCalledWith([1,2,3]);
            });

            it("should not call update on refresh if control is invisible",function(){

                spyOn(baseControl, "get").andCallFake(function(){
                    return [1,2,3];
                });
                spyOn(baseControl, "initialize");
                spyOn(baseControl, "update");

                //Force control to be invisible
                baseControl.visible = false;

                baseControl.refresh();

                expect(baseControl.get).toHaveBeenCalledWith('values');
                expect(baseControl.initialize).toHaveBeenCalledWith(baseControl);
                expect(baseControl.update).not.toHaveBeenCalled();
            });

            it("can refresh with single value",function(){

                spyOn(baseControl, "get").andCallFake(function(){
                    return 4;
                });

                baseControl.refresh();

                expect(baseControl.values).toEqual(4);
            });

            it("can find different attributes in values property",function(){
                baseControl.values = testValues;
                expect(baseControl.find({label: 3})).toEqual({
                    value:3, label:3, selected : true
                });

                //check for null safety
                baseControl.find();
                baseControl.find(null);
            });

            it("can update values", function () {
                spyOn(baseControl, "update");
                baseControl.set({values:[{value:1}, {value:2}, {value:3}]});
                expect(baseControl.update).toHaveBeenCalled();
            });

            it("should not call update on set if control is invisible", function () {
                baseControl.visible = false;

                spyOn(baseControl, "update");
                baseControl.set({values:[{value:1}, {value:2}, {value:3}]});
                expect(baseControl.update).not.toHaveBeenCalled();
            });

            it("can update selection while updating values", function () {
                baseControl.set({values:[{value:1}, {value:2}, {value:3}]});
                expect(baseControl.selection).toEqual(undefined);

                baseControl.selection = [];
                baseControl.set({values:[{value:1, selected:true}, {value:2, selected:true}, {value:3}]});
                expect(baseControl.selection).toEqual([1,2]);

                baseControl.selection = undefined;
                baseControl.set({values:[{value:1, selected:true}, {value:2}, {value:3}]});
                expect(baseControl.selection).toEqual(1);

                baseControl.set({values:"blablabalaaa"});
                expect(baseControl.selection).toEqual("blablabalaaa");
            });

            describe("setting selection ", function(){

                beforeEach(function(){
                    spyOn(baseControl, "fireControlSelectionChangeEvent");
                });

                it("can set selection silent or fire event", function(){
                    baseControl.set({selection:"blabla"});
                    expect(baseControl.fireControlSelectionChangeEvent).toHaveBeenCalled();

                    baseControl.fireControlSelectionChangeEvent.reset();

                    baseControl.set({selection:"albalb"}, true);
                    expect(baseControl.fireControlSelectionChangeEvent).not.toHaveBeenCalled();
                });

                it("can update single value ", function () {
                    baseControl.values = "test";
                    baseControl.set({selection:"blabla"});
                    expect(baseControl.selection).toEqual("blabla");
                    expect(baseControl.values).toEqual("blabla");
                });

                it("can update multi values with single selection ", function () {
                    baseControl.values = [1,2,3];
                    baseControl.set({selection:"blabla"});
                    expect(baseControl.selection).toEqual("blabla");
                    expect(baseControl.values).toEqual([1,2,3]);
                });

                it("can update multi values with multi selection ", function () {

                    baseControl.values = [2,3,4,5,6];
                    baseControl.set({selection:[1,2,3]});
                    expect(baseControl.selection).toEqual([1,2,3]);
                    expect(baseControl.values).toEqual([2,3,4,5,6]);

                });

            });

            it("can be disabled or enabled",function(){
                spyOn(baseControl, "disable");
                spyOn(baseControl, "enable");
                baseControl.set({disabled:true});

                expect(baseControl.disable).toHaveBeenCalled();
                expect(baseControl.enable).not.toHaveBeenCalled();

                baseControl.disable.reset();
                baseControl.enable.reset();
                baseControl.set({disabled:false});

                expect(baseControl.enable).toHaveBeenCalled();
                expect(baseControl.disable).not.toHaveBeenCalled();

                baseControl.disable.reset();
                baseControl.enable.reset();
                baseControl.readOnly = true;
                baseControl.set({disabled:true});

                expect(baseControl.disable).not.toHaveBeenCalled();
                expect(baseControl.enable).not.toHaveBeenCalled();

                baseControl.disable.reset();
                baseControl.enable.reset();
                baseControl.set({disabled:false});

                expect(baseControl.enable).not.toHaveBeenCalled();
                expect(baseControl.disable).not.toHaveBeenCalled();
            });

            it("can update error message",function(){
                spyOn(baseControl, "updateWarningMessage");
                baseControl.set({error: "palundra!"});

                expect(baseControl.updateWarningMessage).toHaveBeenCalled();
            });
        });

        describe(" merge values with selection", function(){

            var values, selection;

            beforeEach(function(){
                values = [
                   {value:1, selected:true},
                   {value:2},
                   {value:3}
                ];
                selection = [2,3];
            });

            it("merges array with array", function () {
                expect(Controls.BaseControl.merge(values, selection)).toEqual([
                    {value:1},
                    {value:2, selected:true},
                    {value:3, selected:true}
                ]);
            });

            it("merges array with single value", function () {
                selection = 3;
                expect(Controls.BaseControl.merge(values, selection)).toEqual([
                    {value:1},
                    {value:2},
                    {value:3, selected:true}
                ]);
            });

            it("handle undefined", function(){
                expect(Controls.BaseControl.merge(values, null)).toEqual(values);
                expect(Controls.BaseControl.merge(values, undefined)).toEqual(values);
                expect(Controls.BaseControl.merge(null, selection)).toEqual(selection);
                expect(Controls.BaseControl.merge(undefined, selection)).toEqual(selection);
            });
        });
    });
});
