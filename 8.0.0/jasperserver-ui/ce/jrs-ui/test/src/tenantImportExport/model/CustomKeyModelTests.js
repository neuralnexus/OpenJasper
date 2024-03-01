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
import CustomeKeyModel from 'src/tenantImportExport/model/CustomKeyModel';
import $ from "jquery";
describe('Export Model', function () {
    var server, customeKeyModel;

    beforeEach(function () {
        server = sinon.fakeServer.create();
        customeKeyModel = new CustomeKeyModel();


    });

    afterEach(function () {
        server.restore();
    });

    it('should filter AES alogorithm', function () {
        let dfd = new $.Deferred();
        server.respondWith(
            "GET",
            "rest_v2/keys",
            [200,
                { "ContentType":"application/json" },
                JSON.stringify([{alias : 'key1'}])
            ]);
        dfd = customeKeyModel.getCustomKeys();
        server.respond();
        dfd.then(function (response) {
            expect(response[0].alias).toEqual('key1');
        });
    });
    it('should return status 400', function () {
        let dfd = new $.Deferred();
        server.respondWith(
            "GET",
            "rest_v2/keys",
            [400,
                { "ContentType":"application/json" },
                'error'
            ]);

        dfd = customeKeyModel.getCustomKeys();
        server.respond();
        dfd.fail(function (response) {
            expect(response.status).toEqual(400);
        });
    });

});