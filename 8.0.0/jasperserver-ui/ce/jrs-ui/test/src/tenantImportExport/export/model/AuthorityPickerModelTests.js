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
import AuthorityModel from 'src/tenantImportExport/export/model/AuthorityPickerModel';
import mocks from 'src/tenantImportExport/export/model/data/authority.data';

describe("Authority model", function () {
    var server;

    beforeEach(function () {
        server = sinon.fakeServer.create();
    });

    afterEach(function () {
        server.restore();
    });

    it("should use proper url", function () {
        var roles = AuthorityModel.instance("rest_v2/roles");

        roles.fetch();

        expect(server.requests.length).toEqual(1);
        expect(server.requests[0].method).toEqual("GET");
        expect(server.requests[0].url).toEqual("rest_v2/roles");
    });

    it("should be able to search roles", function () {
        var searchString = "aaa";
        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");

        roles.setContext({searchString:searchString});

        expect(server.requests.length).toEqual(1);
        expect(server.requests[0].method).toEqual("GET");
        expect(server.requests[0].url).toEqual("rest_v2/roles?search=" + searchString);
    });

    it("should load all entities on empty search", function () {
        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");

        roles.setContext();

        expect(server.requests.length).toEqual(1);
        expect(server.requests[0].method).toEqual("GET");
        expect(server.requests[0].url).toEqual("rest_v2/roles");
    });

    it("should fetch items array", function () {

        server.respondWith(
            "GET",
            "rest_v2/roles",
            [200,
                { "ContentType":"application/json" },
                JSON.stringify(mocks.rolesRest)
            ]);

        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");

        roles.setContext();
        server.respond();

        var rolesArray = roles.get("items");

        expect(rolesArray.length).toBeTruthy();
    });

    it("should fetch empty array of no content returned ", function () {

        server.respondWith(
            "GET",
            "rest_v2/roles",
            [204,
                { "ContentType":"application/json" },
                ""
            ]);

        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");

        roles.setContext();
        server.respond();

        var rolesArray = roles.get("items");

        expect(rolesArray.length).toBeDefined();
        expect(rolesArray.length).toBe(0);
    });

    it("should distinguish roles and users properly", function () {
        var model = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");

        var roles = model.parse(mocks.rolesRest);
        var users = model.parse(mocks.usersRest);

        expect(roles.items).toEqual(mocks.rolesRest.role);
        expect(users.items).toEqual(mocks.usersRest.user);
    });

    it("should notify, when fetch is finished", function () {
        var callback = sinon.spy();

        server.respondWith(
            "GET",
            "rest_v2/roles",
            [200,
                { "ContentType":"application/json" },
                JSON.stringify(mocks.rolesRest)
            ]);

        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");
        roles.on("change", callback);

        roles.setContext();
        server.respond();

        expect(callback.calledOnce).toBeTruthy();
    });

    it("should be aware is session was invalidated", function () {
        var callback = sinon.spy();

        server.respondWith(
            "GET",
            "rest_v2/roles",
            [403,
                { "ContentType":"text/html" },
                '<html><body><h1>HTTP Status 403 - Full authentication is required</h1></body></html>'
            ]);

        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");
        roles.on("error:server", callback);

        roles.setContext();
        server.respond();

        expect(callback.calledOnce).toBeTruthy();
        expect(callback.calledWith(403, null, roles)).toBeTruthy();
    });

    it("should obtain serialized exceptions", function () {
        var callback = sinon.spy();

        server.respondWith(
            "GET",
            "rest_v2/roles",
            [400,
                { "ContentType":"application/json" },
                '{"error":"resource.not.found"}'
            ]);

        var roles = AuthorityModel.instance("rest_v2/roles{{ if (searchString) { }}?search={{-searchString}}{{ } }}");
        roles.on("error:server", callback);

        roles.setContext();
        server.respond();

        expect(callback.calledOnce).toBeTruthy();
        expect(callback.calledWith(400, {"error":"resource.not.found"}, roles)).toBeTruthy();
    });

});