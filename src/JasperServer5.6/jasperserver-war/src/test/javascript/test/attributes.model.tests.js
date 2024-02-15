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
 * @version: $Id: attributes.model.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["backbone", "attributes.model"], function(Backbone, attributes){
describe("attributes", function(){
    var server;

    beforeEach(function(){
        server = sinon.fakeServer.create();
    });

    afterEach(function(){
        server.restore();
    });

    describe("attributes model", function(){

        it("should create instance", function(){
            var model = attributes.Attribute.instance();

            expect(model).toBeDefined();
            expect(model instanceof attributes.Attribute).toBeTruthy();
        });

        it("should create instance", function(){
            var model = attributes.Attribute.instance();

            expect(model).toBeDefined();
            expect(model instanceof attributes.Attribute).toBeTruthy();
        });

        it("should validate", function(){
            var errors = ["attribute.name.empty", "attribute.name.empty", "attribute.value.empty", "attribute.value.empty", "attribute.name.too.long", "attribute.value.too.long"];
            var expected = [];

            var model = attributes.Attribute.instance();

            model.on("invalid", function(model, error){
                expected.push(error);
            });

            model.set({name:"              "}, {validate: true});
            model.set({name:""}, {validate: true});
            model.set({value:"         "}, {validate: true});
            model.set({value:""}, {validate: true});
            model.set({name:"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}, {validate: true});
            model.set({value:"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}, {validate: true});

            expect(expected).toArrayEquals(errors);
        });

        it("should call synchronously", function(){
            var a = {}, b = [], model = attributes.Attribute.instance();

            spyOn(Backbone,"sync");

            model.url = "zgg";
            model.sync(a, b);

            expect(Backbone.sync).toHaveBeenCalledWith(a, b, {async:false});

            Backbone.sync = Backbone.sync.originalValue;

        });


    });

    describe("attributes collection", function(){

        it("should create instance", function(){
            var collection = attributes.Attributes.instance({});

            expect(collection).toBeDefined();
            expect(collection instanceof attributes.Attributes).toBeTruthy();
        });

        it("should have proper url", function(){
            var userName = "admin";

            var collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:userName};

            expect(collection.url()).toEqual("/rest_v2/users/"+userName+"/attributes/");
        });

        it("should have proper url with tenant", function(){
            var userName = "admin";
            var tenant = "aaa";

            var collection = attributes.Attributes.instance({urlTemplate:"/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:userName, tenantId:tenant};

            expect(collection.url()).toEqual("/rest_v2/users/"+userName+"/attributes/?tenantId=" + tenant);
        });

        it("should be able to create model", function(){
            var userName = "admin";

            var collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:userName};

            var model = collection.create({name:"name", value:"value"});

            expect(model).toBeDefined();
            expect(model instanceof attributes.Attribute).toBeTruthy();
            expect(model.isNew()).toBeTruthy();
        });

        it("should respond on adding of new models", function(){
            var callback = jasmine.createSpy("callback");
            var errorCallback = jasmine.createSpy("callback");
            var attr = {name:"name", value:"value"};

            var collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:"ucuas"};
            collection.on("sync", callback);

            server.respondWith([200, {"Content-Type":"application/json"},  JSON.stringify(attr)]);

            var model = collection.create(attr,{error:errorCallback});
            server.respond();

            expect(callback).toHaveBeenCalled();
            expect(errorCallback).not.toHaveBeenCalled();
        });

        it("should respond on failure of adding of new model", function(){
            var callback = jasmine.createSpy("callback");
            var errorCallback = jasmine.createSpy("callback");
            var attr = {name:"name", value:"value"};

            var collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:"ucuas"};
            collection.on("sync", callback);

            server.respondWith([400, {"Content-Type":"application/json"},  JSON.stringify(attr)]);

            var model = collection.create(attr, {error:errorCallback});
            server.respond();

            expect(callback).not.toHaveBeenCalled();
            expect(errorCallback).toHaveBeenCalled();
        });


        it("should call callback after fetching", function(){
            var callback = jasmine.createSpy("callback");
            var errorCallback = jasmine.createSpy("callback");
            var attr = [{name:"name", value:"value"},{name:"name2", value:"value2"}];


            var collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:"ucuas"};
            collection.on("sync", callback);

            server.respondWith([200, {"Content-Type":"application/json"},  JSON.stringify(attr)]);

            collection.fetch({success:callback});

            server.respond();

            expect(callback).toHaveBeenCalled();
            expect(errorCallback).not.toHaveBeenCalled();
        });

        it("should be aware, if models are valid", function(){
            var name = "me";

            var collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});
            collection.context = {userName:"ucuas"};

            collection.add(new attributes.Attribute({name:name, value:"iia"}));
            collection.add(new attributes.Attribute({name:name, value:"iia"}));

            expect(collection.isValid()).toBeFalsy();
        });

    });
});
});


