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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: InputControlCollectionTests.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        json3 = require("json3"),
        InputControlModel = require("inputControl/model/InputControlModel"),
        InputControlCollection = require("inputControl/collection/InputControlCollection");

    describe("InputControlCollection tests", function() {
        it("should be Backbone.Collection instance", function(){
            expect(typeof InputControlCollection).toBe("function");
            expect(InputControlCollection.prototype instanceof Backbone.Collection).toBeTruthy();
        });

        it("should have InputControlModel as model", function() {
            expect(InputControlCollection.prototype.model).toBe(InputControlModel);
        });

        it("should init 'contextPath' and 'resourceUri' from options", function() {
            var collection = new InputControlCollection();

            expect(collection.contextPath).toBeUndefined();
            expect(collection.resourceUri).toBeUndefined();

            collection = new InputControlCollection([], {
                contextPath: "/jasperserver-pro",
                resourceUri: "/test/report"
            });

            expect(collection.contextPath).toBe("/jasperserver-pro");
            expect(collection.resourceUri).toBe("/test/report");
        });

        it("should throw exception when calling 'url' method if 'resourceUri' is not defined", function() {
            var collection = new InputControlCollection();

            expect(function() { collection.url(); }).toThrow("Resource URI is not specified.");
        });

        it("should use 'contextPath' if it is specified in forming 'url'", function() {
            var collection = new InputControlCollection([], {
                contextPath: "/jasperserver-pro",
                resourceUri: "/test/report"
            });

            expect(collection.url()).toBe("/jasperserver-pro/rest_v2/reports/test/report/inputControls");

            collection.contextPath = "/jasperserver-pro/";

            expect(collection.url()).toBe("/jasperserver-pro/rest_v2/reports/test/report/inputControls");
        });

        it("should skip 'contextPath' if it is not specified in forming 'url'", function() {
            var collection = new InputControlCollection([], {
                resourceUri: "/test/report"
            });

            expect(collection.url()).toBe("rest_v2/reports/test/report/inputControls");
        });

        it("should throw error in 'parse' method if response is invalid", function() {
            var collection = new InputControlCollection();

            expect(function() { collection.parse({}) }).toThrow("Unable to parse response from server.");
            expect(function() { collection.parse({ inputControl: "aaa" }) }).toThrow("Unable to parse response from server.");
        });

        it("should extract 'inputControl' array from response in 'parse' method", function() {
            var collection = new InputControlCollection(),
                parseResult = collection.parse({ inputControl: [ { id: "My_Input_Control" } ] });

            expect(parseResult).toEqual([ { id: "My_Input_Control" } ]);
        });

        it("should call base 'fetch' method with 'reset=true' option", function() {
            var fetchSpy = sinon.spy(Backbone.Collection.prototype, "fetch"),
                collection = new InputControlCollection([], {
                    contextPath: "/jasperserver-pro",
                    resourceUri: "/test/report"
                });

            collection.fetch();

            expect(fetchSpy).toHaveBeenCalledWith({
                url: collection.url(),
                reset: true
            });

            fetchSpy.restore();
        });

        it("should be able to add 'excludeState' option when calling 'fetch' method", function() {
            var fetchSpy = sinon.spy(Backbone.Collection.prototype, "fetch"),
                collection = new InputControlCollection([], {
                    contextPath: "/jasperserver-pro",
                    resourceUri: "/test/report"
                });

            collection.fetch({ excludeState: true });

            expect(fetchSpy).toHaveBeenCalledWith({
                url: collection.url() + "?exclude=state",
                reset: true,
                excludeState: true
            });

            fetchSpy.restore();
        });

        it("should have 'update' method", function() {
            expect(typeof InputControlCollection.prototype.update).toBe("function");
        });

        it("should throw exception when calling 'update' method without params", function() {
            var collection = new InputControlCollection([], {
                contextPath: "/jasperserver-pro",
                resourceUri: "/test/report"
            });

            expect(function() { collection.update(); }).toThrow("Cannot update input controls without passed params");
        });

        it("should call base 'fetch' method with overridden options when calling 'update' method", function() {
            var fetchSpy = sinon.spy(Backbone.Collection.prototype, "fetch"),
                collection = new InputControlCollection([], {
                    contextPath: "/jasperserver-pro",
                    resourceUri: "/test/report"
                }),
                params = {
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                };

            collection.update({ params: params });

            expect(fetchSpy).toHaveBeenCalledWith({
                params: params,
                type: "POST",
                contentType: "application/json",
                data: json3.stringify(params),
                reset: true
            });

            fetchSpy.restore();
        });
    });
});
