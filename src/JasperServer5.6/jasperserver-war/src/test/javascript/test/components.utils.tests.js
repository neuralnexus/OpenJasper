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
 * @version: $Id: components.utils.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

;
(function (jQuery, utils, templateEngine) {


    var mocks = {

        generateObjects:function (count, pattern) {
            var results = [];
            while (count > 0) {
                results.push(this.generateObject(pattern));
                --count;
            }
            return results;
        },

        generateObject:function (pattern) {
            var result = _.clone(pattern);
            _.each(result, function (value, key) {
                result[key] = Math.random() * 1000 + "_test_" + Math.random() * 1000;
            });
            return result;
        }

    };


    describe("Component's Utils", function () {
        var elem, templateText, templateScriptContent,
            OBJECTS_COUNT = 10000;

        beforeEach(function () {
            elem = jQuery("<select id='orphan' multiple='multiple' >orphan</select>");
            templateText = "{{#items}}<option value='{{value}}'>{{label}}</option>{{/items}}";
            templateScriptContent =
                "<script id='sandbox' type='mustache'>" +
                    templateText +
                    "</script>";
        });

        it("check is element in a DOM", function () {
            expect(utils.isElementInDom(elem[0])).toBeFalsy();
            jasmine.getFixtures().set(elem[0].outerHTML);
            expect(jQuery('#orphan')).toExist();
        });

        it("fast insert on big html content in a DOM", function () {
            jasmine.getFixtures().set(templateScriptContent + elem[0].outerHTML);
            var orphan = jQuery('#orphan')[0];
            var template = templateEngine.createTemplate('sandbox');
            var mockObjects = mocks.generateObjects(OBJECTS_COUNT, {value:"", label:""});
            var start = (new Date()).getTime();
            utils.setInnerHtml(orphan, template, {
                items:mockObjects
            });
            var end = (new Date()).getTime();

            var optTime = end - start;

            jasmine.getFixtures().cleanUp();

            jasmine.getFixtures().set(templateScriptContent + elem[0].outerHTML);

            orphan = jQuery("#orphan");

            start = (new Date()).getTime();

            orphan.append(jQuery(template({
                items:mockObjects
            })));

            end = (new Date()).getTime();

            var noneOptTime = end - start;

            console.log("Optimal time: %s", optTime);
            console.log("Non optimal time: %s", noneOptTime);
            expect(optTime).toBeLessThan(noneOptTime);

        });

        xit("create deferred for wait certain amount of milliseconds ", function () {
            var doSome = jasmine.createSpy('doSome');

            utils.wait(1000).done(doSome);

            waitsFor(function () {
                return doSome.callCount === 1;
            }, "Timeout deferred doesn't work", 1100);
        });
//TODO: refactor with using time mocking
//        it("show loading dialog after " +utils.LOADING_DIALOG_DELAY, function () {
//
//            ajax = {
//                LOADING_ID : "test"
//            };
//            $ = function(){};
//
//            spyOn(dialogs.popup, "show");
//            spyOn(dialogs.popup, "hide");
//
//            var deferred = new jQuery.Deferred();
//
//            utils.wait(utils.LOADING_DIALOG_DELAY + 10).then(function(){
//                deferred.resolve();
//                expect(dialogs.popup.show).toHaveBeenCalled();
//                deferred.reject();
//                waitsFor(function(){
//                    return dialogs.popup.hide.callCount > 0;
//                }, "shuld hide dialog", 500);
//            });
//
//            utils.showLoadingDialogOn(deferred);
//        });

    });


})(
    jQuery,
    jaspersoft.components.utils,
    jaspersoft.components.templateEngine
);
