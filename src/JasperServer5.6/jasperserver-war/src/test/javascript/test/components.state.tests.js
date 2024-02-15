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
 * @version: $Id: components.state.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "components.state"],
function ($, _, State) {

    describe("ExportState", function () {

        var state;

        describe("Phases", function () {
            it("defined", function () {
                expect(State.NOT_STARTED).toEqual("not started");
                expect(State.INPROGRESS).toEqual("inprogress");
                expect(State.READY).toEqual("finished");
                expect(State.FAILED).toEqual("failed");
            });
        });

        describe("Initialization", function() {
            beforeEach(function() {
                sinon.stub($, "ajax");
                state = State.instance({urlTemplate: "rest_v2/export/{id}/state"});
                state.set({id: "uuid"})
            });

            afterEach(function() {
                $.ajax.restore();
            });

            it("can't request without 'id'", function() {
                state = new State();
                expect(function() {
                    state.url();
                }).toThrow("Can't initialize export state without 'id'");
            });

            it("has defaults", function() {
                expect(state.get('phase')).toEqual(State.NOT_STARTED);
                expect(state.name).toEqual("export.zip");
            });

            it("can reset to defaults", function() {
                state.name = "blblblblb";
                state.set({id: "testId"});
                state.set({phase: "testPhase"});
                state.set({message: "testMessage"});
                state.reset();

                expect(state.name).toEqual("export.zip");
                expect(state.get("id")).toEqual(null);
                expect(state.get("phase")).toEqual(State.NOT_STARTED);
                expect(state.get("message")).toEqual("");
            });

            it("can respond on externally set failure", function() {
                var callback = jasmine.createSpy("server error callback");
                state.on("error:server", callback);

                state.set("phase", State.FAILED);

                expect(callback.wasCalled).toBeTruthy();
            });

        });

        describe("Interaction with server", function () {

            var server;

            beforeEach(function() {
                state = State.instance({urlTemplate: "rest_v2/export/{id}/state"});
                state.set({id: "uuid"})
                server = sinon.fakeServer.create();
            });

            afterEach(function () {
                server.restore();
            });

            it("makes valid request", function () {
                state.fetch();
                expect(server.requests.length).toEqual(1);
                expect(server.requests[0].method).toEqual("GET");
                expect(server.requests[0].url).toEqual("rest_v2/export/uuid/state");
            });

            it("can retrieve export task id", function () {

                server.respondWith(
                    "GET",
                    "rest_v2/export/uuid/state",
                    [200,
                        { "ContentType":"application/json" },
                        JSON.stringify({
                            id: "uuid",
                            phase : "inprogress",
                            message : "Test message here"
                        })
                    ]);

                state.fetch();

                server.respond();

                expect(state.get("id")).toEqual("uuid");
                expect(state.get("phase")).toEqual("inprogress");
                expect(state.get("message")).toEqual("Test message here");

            });
        });

    });

});