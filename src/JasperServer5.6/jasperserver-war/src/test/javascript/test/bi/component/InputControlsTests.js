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
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var  _ = require("underscore"),
        json3 = require("json3"),
        BiComponent = require("bi/component/BiComponent"),
        InputControlCollection = require("inputControl/collection/InputControlCollection"),
        InputControls = require("bi/component/InputControls"),
        JavaScriptExceptionBiComponentError = require("bi/error/JavaScriptExceptionBiComponentError"),
        SchemaValidationBiComponentError = require("bi/error/SchemaValidationBiComponentError"),
        RequestBiComponentError = require("bi/error/RequestBiComponentError"),
        biComponentErrorFactory = require("bi/error/biComponentErrorFactory");

    describe('InputControls BI component', function(){

        describe('instance creation', function(){

            it("should create instance", function(){
                expect(new InputControls()).toBeDefined();
            });

            it("should be a BiComponent", function(){
                expect(new InputControls() instanceof BiComponent).toBeTruthy();
            });

            it("should create simple properties", function(){
                var inst = new InputControls(),

                propertyNames = ['server', 'resource', 'params'],
                instancePropertyNames = _.functions(inst);

                _.each(propertyNames, function(property){
                    expect(_.indexOf(instancePropertyNames, property) < 0).toBeFalsy();
                });
            });

            it("should create common properties", function(){
                var inst = new InputControls(),
                    propertyNames = ['properties', 'data'],
                    instancePropertyNames = _.functions(inst);

                _.each(propertyNames, function(property){
                    expect(_.indexOf(instancePropertyNames, property) > -1).toBeTruthy();
                });
            });

            it("should set values to simple properties", function(){
                var inst = new InputControls(),
                    propertyNames = ['server', 'resource', 'params'],
                    value = "sapi";

                _.each(propertyNames, function(property){
                    inst[property](value);
                    expect(inst[property]()).toEqual(value);
                });
            });

            it("should set values to simple properties and return instance", function(){
                var inst = new InputControls(),
                    propertyNames = ['server', 'resource', 'params'],
                    value = "sapi";

                _.each(propertyNames, function(property){
                    expect(inst[property](value)).toBe(inst);
                });
            });

            it("should set values to common properties", function(){
                var inst = new InputControls(),
                    propertyNames = ['properties'],
                    value = {resource: "9"};

                _.each(propertyNames, function(property){
                    inst[property](value);
                    expect(inst[property]()).toEqual(value);
                });
            });

            it("should set values to common properties and return instance", function(){
                var inst = new InputControls(),
                    propertyNames = ['properties'],
                    value = {resource: "9"};

                _.each(propertyNames, function(property){
                    expect(inst[property](value)).toBe(inst);
                });
            });

            it("should set values to proper instance", function(){
                var inst = new InputControls(),
                    inst2 = new InputControls(),
                    propertyNames = ['properties'],
                    value = {resource: "9"},
                    value2 = {resource: "5"};

                _.each(propertyNames, function(property){
                    inst[property](value);
                    inst2[property](value2);

                    expect(inst[property]()).toEqual(value);
                    expect(inst2[property]()).toEqual(value2);
                    expect(inst2[property]()).not.toEqual(inst[property]());
                });
            });

            it("should set simple values to proper instance", function(){
                var inst = new InputControls(),
                    inst2 = new InputControls(),
                    propertyNames = ['server', 'resource', 'params'],
                    value = "sapi",
                    value2 = "urg";

                _.each(propertyNames, function(property){
                    inst[property](value);
                    inst2[property](value2);

                    expect(inst[property]()).toEqual(value);
                    expect(inst2[property]()).toEqual(value2);
                    expect(inst2[property]()).not.toEqual(inst[property]());
                });
            });

            it("should set simple values via properties method", function() {
                var inst = new InputControls(),
                    propertyNames = ['server', 'resource', 'params'];

                _.each(propertyNames, function(property) {
                    var options = {};
                    options[property] = property;

                    inst.properties(options);

                    expect(inst.properties()[property]).toEqual(property);

                    options[property] = undefined;
                    inst.properties(options);

                    expect(inst.properties()[property]).not.toBeDefined();
                });
            });

            it("should set simple values and properties together", function() {
                var inst = new InputControls();

                inst.properties({resource:"q"});
                inst.server("server");
                inst.params({
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                });

                expect(inst.properties().resource).toEqual("q");
                expect(inst.properties().server).toEqual("server");

            });

            it("should have default data()", function() {
                var ic = new InputControls();

                expect(ic.data()).toEqual([]);
            });
        });

        describe("action Run", function(){
            var defaultSettings = { server: "http://localhost:8080/jasperserver-pro", resource: "/public/resource" },
                defaultData = {inputControl: []},
                server;

            beforeEach(function(){
                server = sinon.fakeServer.create();
            });

            afterEach(function(){
                server.restore();
            });

            it("should have run method", function(){
                var inst = new InputControls(defaultSettings);

                expect(inst.run).toBeDefined();
                expect(_.isFunction(inst.run)).toBeTruthy();

            });

            it("should return deferred", function(){
                var inst = new InputControls(defaultSettings),
                    res = inst.run();

                expect(_.isFunction(res.done)).toBeTruthy();
                expect(_.isFunction(res.fail)).toBeTruthy();
                expect(_.isFunction(res.always)).toBeTruthy();
            });

            it("should take callback and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new InputControls(defaultSettings),
                    res = inst.run(sucsess, failure);

                res.resolve();

                expect(sucsess.called).toBeTruthy();
                expect(failure.called).toBeFalsy();
            });

            it("should take errback and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new InputControls(defaultSettings),
                    res = inst.run(sucsess, failure);

                res.reject();

                expect(sucsess.called).toBeFalsy();
                expect(failure.called).toBeTruthy();
            });

            it("should take complete handler and run it on resolve", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    complete = sinon.spy(),
                    inst = new InputControls(defaultSettings),
                    res = inst.run(sucsess, failure, complete);

                res.resolve();

                expect(complete.called).toBeTruthy();
            });

            it("should take complete handler and run it on fail", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    complete = sinon.spy(),
                    inst = new InputControls(defaultSettings),
                    res = inst.run(sucsess, failure, complete);

                res.reject();

                expect(complete.called).toBeTruthy();
            });

            it("should pass SchemaValidationBiComponentError instance to error callback if validation fails in 'run' method", function() {
                var failure = sinon.spy(),
                    validationErrorSpy = sinon.spy(biComponentErrorFactory, "validationError"),
                    inst = new InputControls({});

                inst.run(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof SchemaValidationBiComponentError).toBe(true);
                expect(validationErrorSpy).toHaveBeenCalled();

                validationErrorSpy.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback if validation fails in 'run' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(InputControlCollection.prototype, "fetch").throws(new Error("stub exception")),
                    inst = new InputControls(defaultSettings);

                inst.run(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should pass JavaScriptExceptionBiComponentError instance to error callback if validation fails in 'run' method", function() {
                var failure = sinon.spy(),
                    javaScriptExceptionSpy = sinon.spy(biComponentErrorFactory, "javaScriptException"),
                    fetchStub = sinon.stub(InputControlCollection.prototype, "fetch").throws(new Error("stub exception")),
                    inst = new InputControls(defaultSettings);

                inst.run(function() {}, failure);

                var error = failure.getCall(0).args[0];

                expect(error instanceof JavaScriptExceptionBiComponentError).toBe(true);
                expect(javaScriptExceptionSpy).toHaveBeenCalled();

                javaScriptExceptionSpy.restore();
                fetchStub.restore();
            });

            it("should pass RequestBiComponentError instance to error callback if validation fails in 'run' method", function() {
                var failure = sinon.spy(),
                    server = sinon.fakeServer.create(),
                    requestErrorSpy = sinon.spy(biComponentErrorFactory, "requestError"),
                    inst = new InputControls(defaultSettings);

                server.respondWith([400, {}, json3.stringify({ message: "error" })]);

                inst.run(function() {}, failure);

                server.respond();

                var error = failure.getCall(0).args[0];

                expect(error instanceof RequestBiComponentError).toBe(true);
                expect(requestErrorSpy).toHaveBeenCalled();

                requestErrorSpy.restore();
                server.restore();
            });

            it("should pass results to callback", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new InputControls(defaultSettings);

                server.respondWith([200, {}, JSON.stringify(defaultData)]);

                inst.run(sucsess, failure);

                server.respond();

                expect(sucsess.called).toBeTruthy();
                expect(sucsess.args[0][0]).toEqual(defaultData.inputControl);
            });

            it("should pass results to data()", function(){
                var sucsess = sinon.spy(),
                    failure = sinon.spy(),
                    inst = new InputControls(defaultSettings);

                server.respondWith([200, {}, JSON.stringify(defaultData)]);

                inst.run(sucsess, failure);

                expect(inst.data()).toEqual([]);

                server.respond();

                expect(inst.data()).toEqual(defaultData.inputControl);
            });

            it("should call 'fetch' method of InputControlCollection if no 'params' were passed", function() {
                var fetchSpy = sinon.spy(InputControlCollection.prototype, "fetch"),
                    inst = new InputControls(defaultSettings);

                inst.run();

                expect(fetchSpy).toHaveBeenCalled();

                fetchSpy.restore();
            });

            it("should call 'update' method of InputControlCollection if 'params' were passed", function() {
                var updateSpy = sinon.spy(InputControlCollection.prototype, "update"),
                    inst = new InputControls(defaultSettings);

                inst.params({
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                });

                inst.run();

                expect(updateSpy).toHaveBeenCalled();

                updateSpy.restore();
            });
        });

        describe("action Validate", function(){
            it ("should have the method", function(){
                var inst = new InputControls();

                expect(inst.validate).toBeDefined();
                expect(_.isFunction(inst.validate)).toBeTruthy();
            });

            it ("should validate properties", function(){
                var inst = new InputControls();

                expect(inst.validate()).toBeDefined();
            });

            it ("should validate valid properties", function(){
                var inst = new InputControls({
                    server: "http://localhost:8080/jasperserver-pro",
                    resource: "/public/report",
                    params: {
                        "Country_multi_select":["Mexico"],
                        "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                    }
                });

                expect(inst.validate()).not.toBeDefined();
            });

            it ("should validate on run and pass validation error to errback", function(){
                var inst = new InputControls({ server: "http://localhost:8080/jasperserver-pro" }),
                    spy = sinon.spy();

                inst.run(null, spy);

                expect(spy.called).toBeTruthy();
            });
        });

        describe("properties validation", function(){
            it("should require 'server' property", function(){
                var inst = new InputControls({ resource: "/public/report", server: "http://localhost:8080/jasperserver-pro" });

                expect(inst.validate()).toBeFalsy();

                inst.properties({ resource: "/public/report" , server : undefined});

                expect(inst.validate()).toBeTruthy();
            });

            it("should require 'resource' property", function(){
                var inst = new InputControls({ resource: "/public/report", server: "http://localhost:8080/jasperserver-pro" });

                expect(inst.validate()).toBeFalsy();

                inst.properties({ resource : undefined,  server: "http://localhost:8080/jasperserver-pro" });

                expect(inst.validate()).toBeTruthy();
            });

            it("should correctly validate 'params' property", function() {
                var inst = new InputControls({
                    resource: "/public/report",
                    server: "http://localhost:8080/jasperserver-pro",
                    params: {}
                });

                expect(inst.validate()).toBeFalsy();

                inst.params({
                    "Country_multi_select":["Mexico"],
                    "Cascading_state_multi_select":["Guerrero", "Sinaloa"]
                });

                expect(inst.validate()).toBeFalsy();

                inst.params({
                    "Country_multi_select": []
                });

                expect(inst.validate()).toBeFalsy();

                inst.params({
                    "Country_multi_select": "Mexico"
                });

                expect(inst.validate()).toBeTruthy();
            });
        });
    });
});
