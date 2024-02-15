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
 * @version: $Id: controls.core.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define([
    "jquery",
    "underscore",
    "mustache",
    "controls.core",
    "core.ajax"
],function (jQuery, _, Mustache, Controls){

    var mocks = {

        generateObjects : function(count, pattern){
            var results = [];
            while(count > 0){
                results.push(this.generateObject(pattern));
                --count;
            }
            return results;
        },

        generateObject : function(pattern){
            var result = _.clone(pattern);
            _.each(result, function(value, key){
                result[key] = Math.random()*1000 + "_test_" + Math.random()*1000;
            });
            return result;
        }

    };


    describe("Controls", function(){

        describe("Core", function(){

            it("basic object should be defined",function(){
                expect(Controls.Base).toBeDefined();
                expect(Controls.TemplateEngine).toBeDefined();
                expect(Controls.Utils).toBeDefined();
            });

            describe("Base", function(){

                it("provide basic inheritance", function(){
                    var A = Controls.Base.extend({
                        say : function(){
                            console.log('hello');
                        }
                    });

                    expect(A.prototype.say).toBeDefined();
                    var a = (new A);
                    expect(a.say).toBeFunction();

                    spyOn(a, "say");

                    a.say();

                    expect(a.say).toHaveBeenCalled();
                });

                it("provide advances inheritance", function(){
                    var A = Controls.Base.extend({
                        say : function(){
                            console.log('hello');
                        }
                    });

                    var B = A.extend({
                        print : function(){
                            this.say();
                        }
                    });

                    var b = new B();

                    expect(B.prototype.say).toBeDefined();

                    spyOn(B.prototype, "say");
                    var printStub = sinon.spy(b, "print");

                    b.print();

                    expect(b.print.callCount).toEqual(1);
                    expect(B.prototype.say.callCount).toEqual(1);

                    printStub.restore();
                });
            });

            describe("Utils", function () {
                var elem, templateText, templateScriptContent,
                    OBJECTS_COUNT = 10000;

                beforeEach(function(){
                    elem = jQuery("<select id='orphan' multiple='multiple' >orphan</select>");
                    templateText = "{{#items}}<option value='{{value}}'>{{label}}</option>{{/items}}";
                    templateScriptContent =
                        "<script id='sandbox' type='mustache'>"+
                            templateText +
                            "</script>";
                });

                it("check is element in a DOM", function () {
                    expect(Controls.Utils.isElementInDom(elem[0])).toBeFalsy();
                    jasmine.getFixtures().set(elem[0].outerHTML);
                    expect(jQuery('#orphan')).toExist();
                });

                xit("fast insert on big html content in a DOM", function () {
                    //TODO: it has shown not quite good stability, also it's not unit test (philosophy)
                    //Performance test
                    jasmine.getFixtures().set(templateScriptContent +  elem[0].outerHTML);
                    var orphan = jQuery('#orphan')[0];
                    var template = Controls.TemplateEngine.createTemplate('sandbox');
                    var mockObjects = mocks.generateObjects(OBJECTS_COUNT, {value:"", label:""});
                    var start = (new Date()).getTime();
                    Controls.Utils.setInnerHtml(orphan, template, {
                        items : mockObjects
                    });
                    var end = (new Date()).getTime();
                    var optTime = end - start;

                    jasmine.getFixtures().cleanUp();

                    jasmine.getFixtures().set(templateScriptContent +  elem[0].outerHTML);

                    orphan = jQuery("#orphan");

                    start = (new Date()).getTime();
                    orphan.append(jQuery(template({
                        items : mockObjects
                    })));
                    end = (new Date()).getTime();

                    var noneOptTime = end - start;

                    console.log("Optimal time: %s", optTime);
                    console.log("Non optimal time: %s", noneOptTime);
                    expect(optTime).toBeLessThan(noneOptTime);
                });

                it("create deferred for wait certain amount of milliseconds ", function () {
                    var doSome  = sinon.stub();

                    // create a fake clock which does not tick itself
                    var clock = sinon.useFakeTimers();

                    // run our effective code
                    Controls.Utils.wait(5).done(doSome);

                    // increase clock for some amount
                    clock.tick(10);

                    // launch waitFor function to check the amount of calls
                    waitsFor(function(){
                        return doSome.callCount === 1;
                    },"Timeout deferred doesn't work", 10);

                    // increase clock for some more time
                    clock.tick(20);
                    // restore original clock
                    clock.restore();
                });

                it("show loading dialog after " +Controls.Utils.LOADING_DIALOG_DELAY, function () {

                    // create a fake clock which does not tick itself
                    var clock = sinon.useFakeTimers();
                    console.log("Clock1: " + clock.Date());

                    var showStub = sinon.stub(dialogs.popup, "show");
                    var hideStub = sinon.stub(dialogs.popup, "hide");

                    var deferred = new jQuery.Deferred();

                    Controls.Utils.showLoadingDialogOn(deferred);

                    clock.tick(Controls.Utils.LOADING_DIALOG_DELAY + 10);

                    expect(showStub).toHaveBeenCalled();

                    deferred.reject();

                    Controls.Utils.wait(500).then(function(){
                        expect(hideStub).toHaveBeenCalled();
                    });

                    clock.tick(500 + 10);

                    // restore original clock
                    clock.restore();
                    showStub.restore();
                    hideStub.restore();
                });

            });

            describe("listen", function () {

                it("attach listeners for global custom events", function () {

                    var eventHandler = function(){};

                    var params  = {};
                    var customEventName = "firstTestEvent";
                    params[customEventName] = eventHandler;

                    var bindStub = sinon.spy(jQuery.fn, "bind");

                    Controls.listen(params);

                    expect(bindStub).toHaveBeenCalled();

                    bindStub.restore();
                });
            });
        });
    });
});
