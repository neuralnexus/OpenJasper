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
 * @version: $Id: export.formmodel.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "export.formmodel", "components.state"],
function ($, _, FormModel, State) {

    describe("ExportFormModel", function () {

        var exportFormModel, savedJSON, savedToJSON;

        beforeEach(function () {
            exportFormModel = new FormModel();

            if (window.Prototype) {
                savedJSON = {
                    array: Array.prototype.toJSON,
                    hash: Hash.prototype.toJSON,
                    string: String.prototype.toJSON
                };

                delete Array.prototype.toJSON;
                delete Hash.prototype.toJSON;
                delete String.prototype.toJSON;

                savedToJSON = Object.toJSON;
                Object.toJSON = function(object){
                    return JSON.stringify(object);
                };
            }
        });

        afterEach(function(){
            if (window.Prototype) {
                Array.prototype.toJSON = savedJSON.array;
                Hash.prototype.toJSON = savedJSON.hash;
                String.prototype.toJSON = savedJSON.string;

                Object.toJSON = savedToJSON;
            }
        });

        it("has defaults", function () {
            expect(exportFormModel.get("users")).toEqual(null);
            expect(exportFormModel.get("roles")).toEqual(null);
            expect(exportFormModel.get("fileName")).toEqual("export.zip");
            expect(exportFormModel.get("everything")).toEqual(true);
            expect(exportFormModel.get("includeSystemProperties")).toEqual(false);
            expect(exportFormModel.get("userForRoles")).toEqual(false);
            expect(exportFormModel.get("rolesForUser")).toEqual(false);
            expect(exportFormModel.get("includeAccessEvents")).toEqual(false);
            expect(exportFormModel.get("includeAuditEvents")).toEqual(false);
            expect(exportFormModel.get("state")).toBeDefined();
        });

        describe("Updating state", function () {

            var state, stateMock;

            beforeEach(function () {
                state = new State();
                stateMock = sinon.mock(state);

            });

            it("can update state with 'id' only if state is in NOT_STARTED phase(by default)", function () {

                exportFormModel.set({fileName: "testFileName.zip"});

                var dto = new Backbone.Model({
                    id:"testUUID",
                    phase:"phase",
                    message:"message"
                });
                var formSetSpy = sinon.spy(exportFormModel, "get");

                stateMock.expects("set").once().withArgs({id:"testUUID"});
                stateMock.expects("set").once().withArgs({phase:"phase", message:"message"});
                exportFormModel.set({state:state});
                exportFormModel.updateState(dto);
                stateMock.verify();

                expect(formSetSpy).toHaveBeenCalled();
                expect(state.name).toEqual("testFileName.zip");

                formSetSpy.restore();
            });

            it("can update state without 'id' if state in other than NOT_STARTED phase", function () {

                state.set({phase:"balbababa"});

                var dto = new Backbone.Model({
                    id:"fdsdfsdf",
                    phase:"phase",
                    message:"message"
                });
                stateMock.expects("set").once().withExactArgs({
                    phase:"phase",
                    message:"message"
                });
                exportFormModel.set({state:state});
                exportFormModel.updateState(dto);
                stateMock.verify();
            });

        });

        describe("Properties transformation", function(){
            it("can convert export parameters without uris", function () {

                expect(exportFormModel.getConvertedParameters()).toEqual(["everything"]);

                exportFormModel.set({
                    includeAccessEvents:true,
                    includeAuditEvents:true,
                    includeSystemProperties:true,
                    includeMonitoringEvents:true
                });
                expect(exportFormModel.getConvertedParameters()).toEqual([
                    "everything", "include-server-settings",
                    "include-access-events", "include-audit-events", "include-monitoring-events"]);

                exportFormModel.set({
                    everything:false,
                    userForRoles:false,
                    includeAccessEvents:false,
                    includeAuditEvents:false,
                    includeSystemProperties:false,
                    includeMonitoringEvents:false
                });
                expect(exportFormModel.getConvertedParameters()).toEqual([]);

            });

            it("can convert export parameters with uris", function () {

                exportFormModel.set({uris: ["a", "b", "c"]});

                expect(exportFormModel.getConvertedParameters()).toEqual(["everything", "repository-permissions", "report-jobs" ]);

                exportFormModel.set({
                    includeAccessEvents:true,
                    includeAuditEvents:true,
                    includeMonitoringEvents:true
                });

                expect(exportFormModel.getConvertedParameters()).toEqual([
                    "everything", "include-access-events", "include-audit-events", "include-monitoring-events", "repository-permissions", "report-jobs"
                ]);

                exportFormModel.set({
                    everything:false,
                    userForRoles:false,
                    includeAccessEvents:false,
                    includeAuditEvents:false
                });
                expect(exportFormModel.getConvertedParameters()).toEqual(["include-monitoring-events", "repository-permissions", "report-jobs"]);
            });

            it("doesn't add URI if system properties export not needed", function() {
                var serverObj = exportFormModel.prepareServerObject();
                expect(serverObj.uris).toBeNull();
            });

            it("adds URI if system properties export needed", function() {
                exportFormModel.set("includeSystemProperties");
                var serverObj = exportFormModel.prepareServerObject();
                expect(serverObj.uris).toBeNull();
            });

        });

        describe("Properties validation", function(){

            it("triggers events on error", function () {
                var callback = sinon.spy();

                exportFormModel.on("error", callback);

                exportFormModel.set({fileName:"<>"});

                expect(callback.calledWith(exportFormModel, "export.file.name.not.valid"));
            });

            it("validates params", function () {
                expect(exportFormModel.validate({fileName:""})).toEqual("export.file.name.empty");
                expect(exportFormModel.validate({fileName:"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})).toEqual("export.file.name.too.long");
                expect(exportFormModel.validate({fileName:"/"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"\\"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"?"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"%"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"*"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:":"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"|"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"\""})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:"<"})).toEqual("export.file.name.not.valid");
                expect(exportFormModel.validate({fileName:">"})).toEqual("export.file.name.not.valid");
                expect(!exportFormModel.validate({fileName:"some.zip"})).toBeTruthy();
            });

            it("is not acceptable by default", function(){
                expect(exportFormModel.isAcceptable()).toBeTruthy();
            });

            it("is not acceptable when empty", function(){
                for (var key in exportFormModel.attributes){
                    exportFormModel.attributes[key] === true && (exportFormModel.attributes[key] = false);
                }
                expect(exportFormModel.isAcceptable()).toBeFalsy();
            });

            it("is acceptable when set some roles", function(){
                for (var key in exportFormModel.attributes){
                    exportFormModel.attributes[key] === true && (exportFormModel.attributes[key] = false);
                }
                exportFormModel.set("roles", ["aaa"]);
                expect(exportFormModel.isAcceptable()).toBeTruthy();
            });

            it("is acceptable when set some users", function(){
                for (var key in exportFormModel.attributes){
                    exportFormModel.attributes[key] === true && (exportFormModel.attributes[key] = false);
                }
                exportFormModel.set("users", ["aaa"]);
                expect(exportFormModel.isAcceptable()).toBeTruthy();
            });

            it("is acceptable when set some uris", function(){
                for (var key in exportFormModel.attributes){
                    exportFormModel.attributes[key] === true && (exportFormModel.attributes[key] = false);
                }
                exportFormModel.set("uris", ["aaa"]);
                expect(exportFormModel.isAcceptable()).toBeTruthy();
            });

            it("is acceptable when set some params", function(){
                for (var key in exportFormModel.attributes){
                    exportFormModel.attributes[key] === true && (exportFormModel.attributes[key] = false);
                }
                exportFormModel.set("everything", true);
                expect(exportFormModel.isAcceptable()).toBeTruthy();
            });
        });


        describe("Interaction with server", function () {

            var server;

            beforeEach(function () {
                server = sinon.fakeServer.create();
                exportFormModel.set({
                    users:["testUser1", "testUser2", "testUser3"],
                    roles:["testRole1", "testRole2"]
                });
            });

            afterEach(function () {
                server.restore();
            });

            it("customize unserializable error", function(){

               exportFormModel.statuses[403] = "export.session.expired";
               expect(exportFormModel.mapUnserializableErrors({status: 403, statusText: "balalbabl"})).toEqual({
                   message: "export.session.expired",
                   errorCode: "unserializable.error"
               });
               exportFormModel.statuses[503] = "export.server.not.avaliable";
               expect(exportFormModel.mapUnserializableErrors({status: 503, statusText: "balalbabl"})).toEqual({
                   message: "export.server.not.avaliable",
                   errorCode: "unserializable.error"
               });
               exportFormModel.statuses[404] = "export.server.not.avaliable";
               expect(exportFormModel.mapUnserializableErrors({status: 404, statusText: "balalbabl"})).toEqual({
                    message: "export.server.not.avaliable",
                    errorCode: "unserializable.error"
               });

                exportFormModel.statuses[0] = "export.server.not.avaliable";
               expect(exportFormModel.mapUnserializableErrors({status: 404, statusText: "balalbabl"})).toEqual({
                    message: "export.server.not.avaliable",
                    errorCode: "unserializable.error"
               });
            });

            it("makes valid request", function () {
                exportFormModel.set({uris: ["a","b", "c"]});
                exportFormModel.save();
                expect(server.requests.length).toEqual(1);
                expect(server.requests[0].method).toEqual("POST");
                expect(server.requests[0].url).toEqual("rest_v2/export");
                expect(server.requests[0].requestBody).toEqual(JSON.stringify({
                        roles:["testRole1", "testRole2"],
                        users:["testUser1", "testUser2", "testUser3"],
                        uris:["a","b", "c"],
                        parameters:["everything", "repository-permissions", "report-jobs"]}
                ));
            });

            it("can retrieve export state", function () {

                server.respondWith(
                    "POST",
                    "rest_v2/export",
                    [200,
                        { "Content-Type":"application/json" },
                        JSON.stringify({
                                id:"testUUID",
                                phase: State.INPROGRESS,
                                message:"Progress..."
                            }
                        )

                    ]);

                exportFormModel.save();

                server.respond();

                expect(exportFormModel.get("id")).not.toBeDefined();
                expect(exportFormModel.get("state").id).toEqual("testUUID");
                expect(exportFormModel.get("state").get("phase")).toEqual("inprogress");
                expect(exportFormModel.get("state").get("message")).toEqual("Progress...");
            });

            it("should handle error if session finished", function () {

                var defaultErrorDelegatorStub = sinon.stub(exportFormModel, "defaultErrorDelegator");

                server.respondWith(
                    "POST",
                    "rest_v2/export",
                    [403,
                        { "Content-Type":"text/html" },
                        "<html><body>bla</body></html>"
                    ]);

                exportFormModel.save();

                server.respond();

                expect(defaultErrorDelegatorStub).toHaveBeenCalled();
                defaultErrorDelegatorStub.restore();
            });

        });
    });
});