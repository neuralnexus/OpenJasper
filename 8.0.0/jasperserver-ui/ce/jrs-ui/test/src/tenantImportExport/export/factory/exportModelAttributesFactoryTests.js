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
import exportModelAttributesFactory from 'src/tenantImportExport/export/factory/exportModelAttributesFactory';
import exportTypesEnum from 'src/tenantImportExport/export/enum/exportTypesEnum';

describe('Export Model Attributes Factory', function () {
    var commonAttributes = {
            roles: null,
            users: null,
            everything: true,
            userForRoles: false,
            rolesForUser: false,
            includeReports: true,
            includeOtherResourceFiles: true,
            includeDataSources: true,
            includeDependentObjects: true,
            includeAttributes: true,
            includeAttributeValues: true
        }, events = {
            includeAccessEvents: false,
            includeAuditEvents: false,
            includeMonitoringEvents: false
        }, proAssets = {
            includeDomains: true,
            includeAdHocViews: true,
            includeDashboards: true
        }, tenantAssets = { includeSubOrganizations: true }, serverAssets = { includeServerSettings: true };
    it('should return proper attributes for root tenant model', function () {
        expect(exportModelAttributesFactory(exportTypesEnum.ROOT_TENANT)).toEqual(_.extend({}, commonAttributes, events, proAssets, tenantAssets, serverAssets));
    });
    it('should return proper attributes for tenant model', function () {
        expect(exportModelAttributesFactory(exportTypesEnum.TENANT)).toEqual(_.extend({}, commonAttributes, proAssets, tenantAssets));
    });
    it('should return proper attributes for server pro model', function () {
        expect(exportModelAttributesFactory(exportTypesEnum.SERVER_PRO)).toEqual(_.extend({}, commonAttributes, events, proAssets, tenantAssets, serverAssets));
    });
    it('should return proper attributes for server ce model', function () {
        expect(exportModelAttributesFactory(exportTypesEnum.SERVER_CE)).toEqual(_.extend({}, commonAttributes, serverAssets, { includeAccessEvents: true }));
    });
});