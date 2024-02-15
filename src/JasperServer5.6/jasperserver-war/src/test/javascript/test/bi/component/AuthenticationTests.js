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
 * @author yaroslav.kovalchyk
 * @version $Id: AuthenticationTests.js 47331 2014-07-18 09:13:06Z kklein $
 */
define(function (require) {
    "use strict";
    var  _ = require("underscore"),
        BiComponent = require("bi/component/BiComponent"),
        json3 = require("json3"),
        schema = json3.parse(require("text!bi/component/schema/Authentication.json")),
        schemaPropertyNames = _.keys(schema.properties),
        testProperties  = {
            url: "http://some.host.com",
            name: "someName",
            password: "somePassword",
            organization: "someOrganization",
            locale: "someLocale",
            timezone: "someTimezone",
            token: "someToken",
            loginFn: function(){},
            logoutFn: function(){}
        },
        Authentication = require("bi/component/Authentication");
    describe('Authentication component', function(){

        describe('instance creation', function(){

            it("should create instance", function(){
                expect(new Authentication()).toBeDefined();
            });

            it("should be a BiComponent", function(){
                expect(new Authentication() instanceof BiComponent).toBeTruthy();
            });

            it("should create simple properties", function(){
                var inst = new Authentication(),

                    instancePropertyNames = _.functions(inst);

                _.each(schemaPropertyNames, function(property){
                    expect(_.indexOf(instancePropertyNames, property) < 0).toBeFalsy();
                });
            });

            it("should create common properties", function(){
                var inst = new Authentication(),
                    instancePropertyNames = _.functions(inst);

                _.each(['properties', 'data'], function(property){
                    expect(_.indexOf(instancePropertyNames, property) > -1).toBeTruthy();
                });
            });

            it("should set values to simple properties", function(){
                var inst = new Authentication(),
                    value = "sapi";

                _.each(schemaPropertyNames, function(property){
                    inst[property](value);
                    expect(inst[property]()).toEqual(value);
                });
            });

            it("should set values to simple properties and return instance", function(){
                var inst = new Authentication(),
                    value = "sapi";

                _.each(schemaPropertyNames, function(property){
                    expect(inst[property](value)).toBe(inst);
                });
            });

            it("should set values to common properties", function(){
                var inst = new Authentication(),
                    propertyNames = ['properties'],
                    value = _.extend({}, testProperties);

                _.each(propertyNames, function(property){
                    inst[property](value);
                    expect(inst[property]()).toEqual(value);
                });
            });

            it("should set values to common properties and return instance", function(){
                var inst = new Authentication(),
                    propertyNames = ['properties'],
                    value = _.extend({}, testProperties);

                _.each(propertyNames, function(property){
                    expect(inst[property](value)).toBe(inst);
                });
            });

            it("should set values to proper instance", function(){
                var inst = new Authentication(),
                    inst2 = new Authentication(),
                    propertyNames = ['properties'],
                    value = {url: "url1"},
                    value2 = {url: "url2"};

                _.each(propertyNames, function(property){
                    inst[property](value);
                    inst2[property](value2);

                    expect(inst[property]()).toEqual(value);
                    expect(inst2[property]()).toEqual(value2);
                    expect(inst2[property]()).not.toEqual(inst[property]());
                });
            });

            it("should set simple values to proper instance", function(){
                var inst = new Authentication(),
                    inst2 = new Authentication(),
                    value = "value1",
                    value2 = "value2";

                _.each(schemaPropertyNames, function(property){
                    inst[property](value);
                    inst2[property](value2);

                    expect(inst[property]()).toEqual(value);
                    expect(inst2[property]()).toEqual(value2);
                    expect(inst2[property]()).not.toEqual(inst[property]());
                });
            });

            it("should set simple values via properties method", function() {
                var inst = new Authentication();

                _.each(schemaPropertyNames, function(property) {
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
                var inst = new Authentication();

                inst.properties({url:"someUrl"});
                inst.name("someName");
                inst.password("somePassword");

                expect(inst.properties().url).toEqual("someUrl");
                expect(inst.properties().name).toEqual("someName");
                expect(inst.properties().password).toEqual("somePassword");

            });
        });

    });
});