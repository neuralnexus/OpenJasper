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
 * @author: Sergii Kylypko, Kostiantyn Tsaregradskyi
 * @version: $Id: RepositoryResourceModelTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {
    "use strict";

    var BaseModel = require("common/model/BaseModel"),
        _ = require("underscore"),
        Validation = require("backbone.validation"),
        repositoryResourceTypes = require("common/enum/repositoryResourceTypes"),
        ResourceModel = require("common/model/RepositoryResourceModel");

    describe("RepositoryResourceModel", function() {

        it("should be Backbone.Model instance", function(){
            expect(typeof ResourceModel).toBe("function");
            expect(ResourceModel.prototype instanceof Backbone.Model).toBeTruthy();
        });

        it("should have 'uri' as idAttribute", function(){
            expect(ResourceModel.prototype.idAttribute).toBe("uri");
        });

        it("should have type undefined", function(){
            expect(ResourceModel.prototype.type).toBeUndefined();
        });

        it("should have 'urlRoot' method", function(){
            expect(typeof ResourceModel.prototype.urlRoot).toBe("function");

            var model = new ResourceModel();
            expect(model.urlRoot()).toEqual("rest_v2/resources");
        });

        it("should use contextPath passed as an option in urlRoot method", function(){
            var model = new ResourceModel({}, { contextPath: "/jasperserver-pro" });
            expect(model.contextPath).toEqual("/jasperserver-pro");
            expect(model.urlRoot()).toEqual("/jasperserver-pro/rest_v2/resources");
        });

        it("should use 'type' passed as an option", function(){
            var model = new ResourceModel({}, { type: "dashboard" });
            expect(model.type).toEqual("dashboard");
        });

        it("should clone contextPath when cloning model", function() {
            var model = new ResourceModel({}, { contextPath: "/jasperserver-pro" }),
                clone = model.clone();

            expect(clone.contextPath).toBe(model.contextPath);
        });

        it("should have default attributes", function() {
            expect(ResourceModel.prototype.defaults).toBeDefined();
        });

        it("should have backbone-validation bound", function() {
            expect(ResourceModel.prototype.isValid).toEqual(Validation.mixin.isValid);
            expect(ResourceModel.prototype.validate).toEqual(Validation.mixin.validate);
            expect(ResourceModel.prototype.preValidate).toEqual(Validation.mixin.preValidate);
        });

        it("should have validation rules for attributes", function() {
            expect(ResourceModel.prototype.validation).toBeDefined();
            expect(ResourceModel.prototype.validation.name).toBeDefined();
            expect(ResourceModel.prototype.validation.label).toBeDefined();
            expect(ResourceModel.prototype.validation.description).toBeDefined();
            expect(ResourceModel.prototype.validation.parentFolderUri).toBeDefined();

            expect(_.findWhere(ResourceModel.prototype.validation.name, { required: false })).toBeDefined();
            expect(_.findWhere(ResourceModel.prototype.validation.name, { maxLength: ResourceModel.NAME_MAX_LENGTH })).toBeDefined();
            expect(_.findWhere(ResourceModel.prototype.validation.name, { doesNotContainSymbols: ResourceModel.NAME_NOT_SUPPORTED_SYMBOLS })).toBeDefined();

            expect(_.findWhere(ResourceModel.prototype.validation.label, { required: true })).toBeDefined();
            expect(_.findWhere(ResourceModel.prototype.validation.label, { maxLength: ResourceModel.LABEL_MAX_LENGTH })).toBeDefined();
            expect(_.findWhere(ResourceModel.prototype.validation.label, { doesNotContainSymbols: ResourceModel.LABEL_NOT_SUPPORTED_SYMBOLS })).toBeDefined();

            expect(_.findWhere(ResourceModel.prototype.validation.description, { required: false })).toBeDefined();
            expect(_.findWhere(ResourceModel.prototype.validation.description, { maxLength: ResourceModel.DESCRIPTION_MAX_LENGTH })).toBeDefined();
            expect(_.findWhere(ResourceModel.prototype.validation.description, { doesNotContainSymbols: ResourceModel.DESCRIPTION_NOT_SUPPORTED_SYMBOLS })).toBeDefined();

            expect(_.findWhere(ResourceModel.prototype.validation.parentFolderUri, { required: true })).toBeDefined();
        });

        it("should have static constants", function(){
            expect(ResourceModel.LABEL_MAX_LENGTH).toBe(100);
            expect(ResourceModel.NAME_MAX_LENGTH).toBe(100);
            expect(ResourceModel.DESCRIPTION_MAX_LENGTH).toBe(250);
            expect(ResourceModel.LABEL_NOT_SUPPORTED_SYMBOLS).toBe("<>");
            expect(ResourceModel.DESCRIPTION_NOT_SUPPORTED_SYMBOLS).toBe("<>");
            expect(ResourceModel.NAME_NOT_SUPPORTED_SYMBOLS).toBe("~!#\\$%^|\\s`@&*()\\-+={}\\[\\]:;\"\"\\<\\>,?\/\\|\\\\");
        });

        it("should have 'getNameFromUri' static method", function(){
            expect(typeof ResourceModel.getNameFromUri).toBe("function");
            expect(ResourceModel.getNameFromUri("/public/test")).toBe("test");
            expect(ResourceModel.getNameFromUri("/public")).toBe("public");
            expect(ResourceModel.getNameFromUri("/")).toBe("");
        });

        it("should have 'getParentFolderFromUri' static method", function(){
            expect(typeof ResourceModel.getParentFolderFromUri).toBe("function");
            expect(ResourceModel.getParentFolderFromUri("/public/test")).toBe("/public");
            expect(ResourceModel.getParentFolderFromUri("/public")).toBe("/");
            expect(ResourceModel.getParentFolderFromUri("/")).toBe("");
        });

        it("should have 'generateResourceName' static method", function(){
            expect(typeof ResourceModel.generateResourceName).toBe("function");
            expect(ResourceModel.generateResourceName("test")).toBe("test");
            expect(ResourceModel.generateResourceName("???")).toBe("___");
        });

        it("should have 'constructUri' static method", function(){
            expect(typeof ResourceModel.constructUri).toBe("function");
            expect(ResourceModel.constructUri("/public", "test")).toBe("/public/test");
            expect(ResourceModel.constructUri("/", "test")).toBe("/test");
            expect(ResourceModel.constructUri("/public", "")).toBeUndefined();
            expect(ResourceModel.constructUri("", "test")).toBeUndefined();
        });

        it("should set name and parentFolderUri in parse method if uri is defined in response", function(){
            expect(typeof ResourceModel.prototype.parse).toBe("function");

            var model = new ResourceModel();

            var parsedResponse = model.parse({ uri: "/public/test" });
            expect(parsedResponse.name).toBe("test");
            expect(parsedResponse.parentFolderUri).toBe("/public");

            parsedResponse = model.parse({});
            expect(parsedResponse.name).toBeUndefined();
            expect(parsedResponse.parentFolderUri).toBeUndefined();
        });

        it("should set uri in parse method if name and parentFolderUri are defined in response", function(){
            var model = new ResourceModel();

            var parsedResponse = model.parse({ parentFolderUri: "/public", name: "test" });
            expect(parsedResponse.uri).toBe("/public/test");
        });

        it("should allow new resource creation", function(){
            var model = new ResourceModel();
            expect(model.isNew()).toBeTruthy();
        });

        it("should call 'parse' method on init by default", function(){
            var parseSpy = sinon.spy(ResourceModel.prototype, "parse");

            new ResourceModel();

            sinon.assert.calledWith(parseSpy);

            parseSpy.restore();
        });

        it("should set parentFolderUri and name from uri on init", function(){
            var model = new ResourceModel({ uri: "/public/test" });
            expect(model.isNew()).toBeFalsy();
            expect(model.get("parentFolderUri")).toBe("/public");
            expect(model.get("name")).toBe("test");
        });

        it("should set uri from parentFolderUri and name on init", function(){
            var model = new ResourceModel({ parentFolderUri: "/public", name: "test" });
            expect(model.isNew()).toBeFalsy();
            expect(model.get("uri")).toBe("/public/test");
        });

        it("should update parentFolderUri and name when uri changes", function(){
            var model = new ResourceModel();
            model.set({ uri: "/public/test" });
            expect(model.get("parentFolderUri")).toBe("/public");
            expect(model.get("name")).toBe("test");
        });

        it("should update uri when name or parentFolderUri changes", function(){
            var model = new ResourceModel();
            model.set({ parentFolderUri: "/public", name: "test" });
            expect(model.get("uri")).toBe("/public/test");
        });

        it("should have 'serialize' method", function(){
            expect(typeof ResourceModel.prototype.serialize).toBe("function");

            var model = new ResourceModel({
                uri: "/public/My_Resource",
                label: "My Resource"
            });

            var jsonData = model.serialize();
            expect(jsonData.label).toBe("My Resource");
            expect(jsonData.uri).toBe("/public/My_Resource");
        });

        it("should remove name and parentFolderUri in 'toJSON' method", function(){
            var model = new ResourceModel({
                parentFolderUri: "/public",
                name: "test"
            });

            var jsonData = model.toJSON();
            expect(jsonData.name).toBeUndefined();
            expect(jsonData.parentFolderUri).toBeUndefined();
        });

        it("should have 'isWritable' method", function(){
            expect(typeof ResourceModel.prototype.isWritable).toBe("function");

            var model = new ResourceModel();

            expect(model.isWritable()).toBeFalsy();

            model.set({ permissionMask: 1 });

            expect(model.isWritable()).toBeTruthy();

            model.set({ permissionMask: 6 });

            expect(model.isWritable()).toBeTruthy();

            model.set({ permissionMask: 8 });

            expect(model.isWritable()).toBeFalsy();
        });

        it("should call BaseModel.prototype.fetch with 'application/json' Accept header and ?expanded=false by default", function() {
            var model = new ResourceModel({ uri: "/public/test" });

            var fetchSpy = sinon.spy(BaseModel.prototype, "fetch");

            model.fetch();

            sinon.assert.calledWith(fetchSpy, {
                url: "rest_v2/resources/public/test?expanded=false",
                headers: {
                    Accept: "application/json"
                }
            });

            fetchSpy.restore();
        });

        it("should fetch expanded resource when fetch is called with expanded=true option", function() {
            var model = new ResourceModel({ uri: "/public/test" });

            var fetchSpy = sinon.spy(BaseModel.prototype, "fetch");

            model.fetch({ expanded: true });

            sinon.assert.calledWith(fetchSpy, {
                url: "rest_v2/resources/public/test?expanded=true",
                headers: {
                    Accept: "application/json"
                }
            });

            fetchSpy.restore();
        });

        it("should throw error if save is called and type is undefined", function(){
            // create model instance without type specified
            var model = new ResourceModel({ uri: "/public/test" });
            expect(function() { model.save(); }).toThrow("Resource type is unspecified. It's not possible to save " +
                "a resource without it's type specified");
        });

        it("should call BaseModel.prototype.save with 'application/json' Accept header, correct Content-Type, " +
            "createFolders=false and overwrite=false by default", function() {
            var model = new ResourceModel({ uri: "/public/test"}, {type:"testType"});

            var saveSpy = sinon.spy(BaseModel.prototype, "save");

            model.save();

            sinon.assert.calledWith(saveSpy, {}, {
                url: "rest_v2/resources/public/test?createFolders=false&overwrite=false",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/repository.testType+json; charset=UTF-8"
                }
            });

            saveSpy.restore();
        });

        it("should extract resource type from Content-Type header if sync is called with method 'read'", function() {
            var model = new ResourceModel({ uri: "/public/test"}),
                originalSuccess = sinon.stub();

            var fakeServer = sinon.fakeServer.create();

            model.sync('read', model, {success: originalSuccess});

            fakeServer.respondWith([
                200,
                { "Content-Type": "application/repository.testType+json" },
                JSON.stringify({ uri: '/public/test' })
            ]);

            fakeServer.respond();
            expect(model.type).toBe("testType");
            sinon.assert.called(originalSuccess);
            fakeServer.restore();
        });

        it("should throw unsupported content type error if sync is called with method 'read' " +
            "and response doesn't have proper Content-Type header", function() {
            var model = new ResourceModel({ uri: "/public/test"}),
                successWrapper = undefined,
                expectedError = undefined;

            var syncStub = sinon.stub(BaseModel.prototype, "sync", function(method, model, options){
                successWrapper = options.success;
            });
            model.sync('read', model, {});
            expect(successWrapper).toBeFunction();
            expect(function(){successWrapper({}, "someStatus", {
                getResponseHeader: function(name){
                    return name === "Content-Type" ? "application/json" : null;
                }
            });}).toThrow("Unsupported response content type: application/json");
            syncStub.restore();
        });

        it("should be saved with overridden createFolders and overwrite options", function() {
            var model = new ResourceModel({ uri: "/public/test"}, {type: "testType"});

            var saveSpy = sinon.spy(BaseModel.prototype, "save");

            model.save({}, {
                overwrite: true,
                createFolders: false
            });

            sinon.assert.calledWith(saveSpy, {}, {
                url: "rest_v2/resources/public/test?createFolders=false&overwrite=true",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/repository.testType+json; charset=UTF-8"
                }
            });

            saveSpy.restore();
        });

        it("should be invalid without any attributes", function(){
            var a = new ResourceModel();
            expect(a.validate()).toBeDefined();
        });

        it("should be invalid with invalid 'uri' attribute", function(){
            var a = new ResourceModel({ label: "test", uri: "/public/test?test" });
            expect(a.validate()).toBeDefined();
        });

        it("should be invalid without 'label' attribute", function(){
            var a = new ResourceModel({ parentFolderUri: "/public" });
            expect(a.validate()).toBeDefined();
        });

        it("should be invalid with invalid 'label' attribute", function(){
            var a = new ResourceModel({ label: "<test>", parentFolderUri: "/public" });
            expect(a.validate()).toBeDefined();

            var  b = new ResourceModel({ label: Array(200).join("1"), parentFolderUri: "/public" });
            expect(b.validate()).toBeDefined();
        });

        it("should be invalid with invalid 'description' attribute", function(){
            var a = new ResourceModel({ label: "test", parentFolderUri: "/public", description: "<desc>" });
            expect(a.validate()).toBeDefined();

            var b = new ResourceModel({ label: "test", parentFolderUri: "/public", description: Array(500).join("1") });
            expect(b.validate()).toBeDefined();
        });

        it('should allow creating resource', function(){
            var a, fakeServer;

            fakeServer = sinon.fakeServer.create();

            a = new ResourceModel({ label: 'test', parentFolderUri: '/public' }, {type: "testType"});
            a.save();

            fakeServer.respondWith([
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ uri: '/public/test' })
            ]);

            fakeServer.respond();

            expect(a.get('uri')).toBe('/public/test');
            expect(a.get('parentFolderUri')).toBe('/public');
            expect(a.get('name')).toBe('test');

            fakeServer.restore();
        });

        it('should allow resource fetch', function(){
            var a, fakeServer;

            fakeServer = sinon.fakeServer.create();

            a = new ResourceModel({ uri: '/public/test' });
            a.fetch();

            fakeServer.respondWith([
                200,
                { "Content-Type": "application/repository.testType+json" },
                JSON.stringify({ uri: '/public/test', label: 'test', description: '' })
            ]);

            fakeServer.respond();

            expect(a.get('uri')).toBe('/public/test');
            expect(a.get('parentFolderUri')).toBe('/public');
            expect(a.get('name')).toBe('test');
            expect(a.get('label')).toBe('test');
            expect(a.get('description')).toBe('');
            expect(a.type).toBe("testType");

            fakeServer.restore();
        });
    });

});