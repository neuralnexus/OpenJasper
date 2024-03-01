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
import BaseModel from 'js-sdk/src/common/model/BaseModel';
import _ from 'underscore';
import Backbone from 'backbone';
import i18nMessage from 'js-sdk/src/common/util/i18nMessage';
import Validation from 'backbone-validation';
import ResourceModel from 'src/bi/repository/model/RepositoryResourceModel';

describe("RepositoryResourceModel", function() {
    var sandbox;

    beforeEach(function() {
        sandbox = sinon.createSandbox();
    });

    afterEach(function() {
        sandbox.restore();
    });

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

    it("urlRoot should return absolute path if contextPath isn't specified", function(){
        expect(typeof ResourceModel.prototype.urlRoot).toBe("function");

        var model = new ResourceModel();
        expect(model.urlRoot()).toEqual("/rest_v2/resources");
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

    it("should have static constants", function(){
        expect(ResourceModel.settings.LABEL_MAX_LENGTH).toBe(100);
        expect(ResourceModel.settings.NAME_MAX_LENGTH).toBe(100);
        expect(ResourceModel.settings.DESCRIPTION_MAX_LENGTH).toBe(250);
        expect(ResourceModel.settings.NAME_NOT_SUPPORTED_SYMBOLS).toBe("~!#\\$%^|\\s`@&*()\\-+={}\\[\\]:;\"'\\<\\>,?/\\|\\\\");

    });

    it("should have validation rules for attributes", function() {

        expect(ResourceModel.prototype.validation).toBeDefined();
        expect(ResourceModel.prototype.validation.name).toBeDefined();
        expect(ResourceModel.prototype.validation.label).toBeDefined();
        expect(ResourceModel.prototype.validation.description).toBeDefined();
        expect(ResourceModel.prototype.validation.parentFolderUri).toBeDefined();
    });

    it("should have validation rule for name attribute", function(){

        expect(ResourceModel.prototype.validation.name() instanceof i18nMessage).toBeFalsy();
        expect(ResourceModel.prototype.validation.name("") instanceof i18nMessage).toBeFalsy();

        expect(ResourceModel.prototype.validation.name((new Array(ResourceModel.settings.NAME_MAX_LENGTH+1).join("a"))) instanceof i18nMessage).toBeFalsy();
        expect(ResourceModel.prototype.validation.name((new Array(ResourceModel.settings.NAME_MAX_LENGTH+2).join("a"))) instanceof i18nMessage).toBeTruthy();

        var notSupportedSymbolsArray = ResourceModel.settings.NAME_NOT_SUPPORTED_SYMBOLS.replace("\\s", " ").split("");
        for (var i = 0, l = notSupportedSymbolsArray.length; i < l; i++) {
            expect(ResourceModel.prototype.validation.name(notSupportedSymbolsArray[i]) instanceof i18nMessage).toBeTruthy();
        }
    });

    it("should have validation rule for label attribute", function(){

        expect(ResourceModel.prototype.validation.label() instanceof i18nMessage).toBeTruthy();
        expect(ResourceModel.prototype.validation.label("") instanceof i18nMessage).toBeTruthy();

        expect(ResourceModel.prototype.validation.label((new Array(ResourceModel.settings.LABEL_MAX_LENGTH+1).join("a"))) instanceof i18nMessage).toBeFalsy();
        expect(ResourceModel.prototype.validation.label((new Array(ResourceModel.settings.LABEL_MAX_LENGTH+2).join("a"))) instanceof i18nMessage).toBeTruthy();

    });

    it("should have validation rule for description attribute", function(){

        expect(ResourceModel.prototype.validation.description() instanceof i18nMessage).toBeFalsy();
        expect(ResourceModel.prototype.validation.description("") instanceof i18nMessage).toBeFalsy();

        expect(ResourceModel.prototype.validation.description((new Array(ResourceModel.settings.DESCRIPTION_MAX_LENGTH+1).join("a"))) instanceof i18nMessage).toBeFalsy();
        expect(ResourceModel.prototype.validation.description((new Array(ResourceModel.settings.DESCRIPTION_MAX_LENGTH+2).join("a"))) instanceof i18nMessage).toBeTruthy();

    });

    it("should have validation rule for parentFolderUri attribute", function(){
        expect(_.findWhere(ResourceModel.prototype.validation.parentFolderUri, { required: true })).toBeDefined();
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
        var parseSpy = sandbox.spy(ResourceModel.prototype, "parse");

        new ResourceModel();

        expect(parseSpy).toHaveBeenCalledWith();
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

        var fetchSpy = sandbox.spy(BaseModel.prototype, "fetch");

        model.fetch();

        expect(fetchSpy).toHaveBeenCalledWith({
            url: "/rest_v2/resources/public/test?expanded=false",
            headers: {
                Accept: "application/json"
            }
        });
    });

    it("should use relative path in fetch when contextPath is specified", function() {
        var model = new ResourceModel({ uri: "/public/test" }, {contextPath: "/test"});

        var fetchSpy = sandbox.spy(BaseModel.prototype, "fetch");

        model.fetch();

        expect(fetchSpy).toHaveBeenCalledWith({
            url: "/test/rest_v2/resources/public/test?expanded=false",
            headers: {
                Accept: "application/json"
            }
        });
    });

    it("should fetch expanded resource when fetch is called with expanded=true option", function() {
        var model = new ResourceModel({ uri: "/public/test" });

        var fetchSpy = sandbox.spy(BaseModel.prototype, "fetch");

        model.fetch({ expanded: true });

        expect(fetchSpy).toHaveBeenCalledWith({
            url: "/rest_v2/resources/public/test?expanded=true",
            headers: {
                Accept: "application/json"
            }
        });
    });

    it("should throw error if save is called and type is undefined", function(){
        // create model instance without type specified
        var model = new ResourceModel({ uri: "/public/test" });
        expect(function() { model.save(); }).toThrow(new Error("Resource type is unspecified. It's not possible to save " +
            "a resource without it's type specified"));
    });

    it("should call BaseModel.prototype.save with 'application/json' Accept header, correct Content-Type, " +
        "createFolders=false, overwrite=false and expanded=false by default", function() {
        var model = new ResourceModel({ uri: "/public/test"}, {type:"testType"});

        var saveSpy = sandbox.spy(BaseModel.prototype, "save");

        model.save();

        expect(saveSpy).toHaveBeenCalledWith({}, {
            url: "/rest_v2/resources/public/test?createFolders=false&overwrite=false&expanded=false&dry-run=false",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/repository.testType+json; charset=UTF-8"
            }
        });
    });

    it("should extract resource type from Content-Type header if sync is called with method 'read'", function() {
        var model = new ResourceModel({ uri: "/public/test"}),
            originalSuccess = sandbox.stub();

        var fakeServer = sandbox.useFakeServer();

        model.sync('read', model, {success: originalSuccess});

        fakeServer.respondWith([
            200,
            { "Content-Type": "application/repository.testType+json" },
            JSON.stringify({ uri: '/public/test' })
        ]);

        fakeServer.respond();
        expect(model.type).toBe("testType");
        expect(originalSuccess).toHaveBeenCalled();
    });

    it("should throw unsupported content type error if sync is called with method 'read' " +
        "and response doesn't have proper Content-Type header", function() {
        var model = new ResourceModel({ uri: "/public/test"});

        sandbox.stub(BaseModel.prototype, "sync").callsFake(function(method, model, options){
            var successWrapper = options.success;

            expect(function() {
                successWrapper({}, "someStatus", {
                    getResponseHeader: function(name) {
                        return name === "Content-Type" ? "application/json" : null;
                    }
                });
            }).toThrow(new Error("Unsupported response content type: application/json"));
        });

        model.sync('read', model, {});
    });

    it("should be saved with overridden createFolders, overwrite, dry-run and expanded options", function() {
        var model = new ResourceModel({ uri: "/public/test"}, {type: "testType"});

        var saveSpy = sandbox.spy(BaseModel.prototype, "save");

        model.save({}, {
            overwrite: true,
            createFolders: false,
            expanded: true,
            dryRun:true
        });

        expect(saveSpy).toHaveBeenCalledWith({}, {
            url: "/rest_v2/resources/public/test?createFolders=false&overwrite=true&expanded=true&dry-run=true",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/repository.testType+json; charset=UTF-8"
            }
        });
    });

    it("should use context path in save method", function() {
        var model = new ResourceModel({ uri: "/public/test"}, {type: "testType", contextPath: "/test"});

        var saveSpy = sandbox.spy(BaseModel.prototype, "save");

        model.save({}, {
            overwrite: true,
            createFolders: false,
            expanded: true,
            dryRun:true
        });

        expect(saveSpy).toHaveBeenCalledWith({}, {
            url: "/test/rest_v2/resources/public/test?createFolders=false&overwrite=true&expanded=true&dry-run=true",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/repository.testType+json; charset=UTF-8"
            }
        });
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

    it('should allow creating resource', function(){
        var a, fakeServer;

        fakeServer = sandbox.useFakeServer();

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
    });

    it('should allow resource fetch', function(){
        var a, fakeServer;

        fakeServer = sandbox.useFakeServer();

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
    });

    describe("check label existence on server", function() {
        var resourceModel,
            fakeServer;

        beforeEach(function() {
            resourceModel = new ResourceModel({
                label: "{^label}",
                parentFolderUri: "/public"
            }, {
                contextPath: "/contextPath"
            });

            fakeServer = sandbox.useFakeServer();
        });

        it("label exists", function(done) {
            fakeServer.respondWith("GET", "/contextPath/rest_v2/resources?folderUri=%2Fpublic&q=%7B%5Elabel%7D&recursive=false", [
                200,
                {
                    "Content-Type": "application/json"
                },
                JSON.stringify({
                    resourceLookup: ["resource"]
                })
            ]);

            resourceModel.checkLabelExistenceOnServer().then(function(response) {
                expect(response).toEqual({
                    foundResources: ["resource"]
                });

                done();
            });

            fakeServer.respond();
        });

        it("label doesn't exist", function(done) {
            fakeServer.respondWith("GET", "/contextPath/rest_v2/resources?folderUri=%2Fpublic&q=%7B%5Elabel%7D", [
                404,
                {
                    "Content-Type": "application/json"
                },
                JSON.stringify({
                    errorMessage: "notFound"
                })
            ]);

            resourceModel.checkLabelExistenceOnServer().then(function(response) {
                expect(response).toEqual({
                    foundResources: []
                });

                done();
            });

            fakeServer.respond();
        });

        it("error during label existence check", function(done) {
            fakeServer.respondWith("GET", "/contextPath/rest_v2/resources?folderUri=%2Fpublic&q=%7B%5Elabel%7D&recursive=false", [
                403,
                {
                    "Content-Type": "application/json"
                },
                JSON.stringify({
                    errorMessage: "forbidden"
                })
            ]);

            resourceModel.checkLabelExistenceOnServer().fail(function(xhr) {
                expect(xhr.status).toEqual(403);
                expect(xhr.responseJSON).toEqual({
                    errorMessage: "forbidden"
                });

                done();
            });

            fakeServer.respond();
        });
    });
});