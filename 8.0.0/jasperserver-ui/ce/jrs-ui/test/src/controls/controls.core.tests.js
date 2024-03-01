/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import sinon from 'sinon';
import jQuery from 'jquery';
import _ from 'underscore';
import Controls from 'src/controls/controls.core';
import 'src/core/core.ajax';
import dialogs from 'src/components/components.dialogs';

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
                        console.log('hello'); // eslint-disable-line no-console
                    }
                });

                expect(A.prototype.say).toBeDefined();
                var a = (new A());
                expect(typeof a.say).toEqual('function');

                spyOn(a, "say");

                a.say();

                expect(a.say).toHaveBeenCalled();
            });

            it("provide advances inheritance", function(){
                var A = Controls.Base.extend({
                    say : function(){
                        console.log('hello'); // eslint-disable-line no-console
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
                expect(B.prototype.say.calls.count()).toEqual(1);

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

            it("create deferred for wait certain amount of milliseconds ", function (done) {
                var doSome  = sinon.stub();

                // create a fake clock which does not tick itself
                var clock = sinon.useFakeTimers();

                // run our effective code
                Controls.Utils.wait(5).done(doSome);

                // increase clock for some amount
                clock.tick(10);

                // launch waitFor function to check the amount of calls
                setTimeout(function(){
                    expect(doSome.callCount).toEqual(1);
                    done();
                },"Timeout deferred doesn't work", 10);

                // increase clock for some more time
                clock.tick(20);
                // restore original clock
                clock.restore();
            });

            it("show loading dialog after " +Controls.Utils.LOADING_DIALOG_DELAY, function () {
                var loading = jQuery("<div id='loading' class='hidden'></div>");

                jQuery("body").append(loading);
                // create a fake clock which does not tick itself
                var clock = sinon.useFakeTimers();

                var showStub = sinon.stub(dialogs.popup, "show");
                var hideStub = sinon.stub(dialogs.popup, "hide");

                var deferred = new jQuery.Deferred();

                Controls.Utils.showLoadingDialogOn(deferred);

                clock.tick(Controls.Utils.LOADING_DIALOG_DELAY + 10);

                expect(showStub).toHaveBeenCalled();

                deferred.reject();

                clock.tick(1);

                Controls.Utils.wait(500).then(function(){
                    expect(hideStub).toHaveBeenCalled();
                });

                clock.tick(500 + 10);

                // restore original clock
                clock.restore();
                showStub.restore();
                hideStub.restore();

                loading.remove();
            });

        });

        describe("listen", function () {

            it("attach listeners for global custom events", function () {

                var eventHandler = function(){};

                var params  = {};
                var customEventName = "firstTestEvent";
                params[customEventName] = eventHandler;

                var bindStub = sinon.spy(jQuery.fn, "on");

                Controls.listen(params);

                expect(bindStub).toHaveBeenCalled();

                bindStub.restore();
            });
        });
    });
});