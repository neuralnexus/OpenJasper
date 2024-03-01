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

import _ from 'underscore';
import tenantTreeDataUriTemplateUtil from 'src/tenantImportExport/utils/tenantTreeDataUriTemplateUtil';

describe("tenantTreeDataUriTemplateUtil Tests.", function() {
    var template;

    beforeEach(function() {
        template = _.template(tenantTreeDataUriTemplateUtil({
            contextPath: "contextPath"
        }));
    });

    it("should return tenant tree data uri with encoded rootTenantId param", function() {
        var uri = template({
            limit: 100,
            offset: 0,
            id: "/path/@to"
        });

        expect(uri).toEqual("contextPath/rest_v2/organizations?rootTenantId=%2Fpath%2F%40to&offset=0&limit=100&maxDepth=1");
    });

    it("should return tenant tree data uri without rootTenantId param if id equals 'organizations'", function() {
        var uri = template({
            limit: 100,
            offset: 0,
            id: "organizations"
        });

        expect(uri).toEqual("contextPath/rest_v2/organizations?&offset=0&limit=100&maxDepth=1");
    });
});