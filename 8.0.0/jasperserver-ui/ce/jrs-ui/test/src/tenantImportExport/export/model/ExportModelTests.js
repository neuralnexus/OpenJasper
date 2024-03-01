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
import _ from 'underscore';
import ExportModel from 'src/tenantImportExport/export/model/ExportModel';
import importExportTypesEnum from 'src/tenantImportExport/export/enum/exportTypesEnum';
import exportModelAttributesFactory from 'src/tenantImportExport/export/factory/exportModelAttributesFactory';

describe('Export Model', function () {
    var exportModel, savedJSON, savedToJSON;
    beforeEach(function () {
        exportModel = new ExportModel();
        if (window.Prototype) {
            savedJSON = {
                array: Array.prototype.toJSON,
                string: String.prototype.toJSON
            };
            delete Array.prototype.toJSON;
            delete String.prototype.toJSON;
            savedToJSON = Object.toJSON;
            Object.toJSON = function (object) {
                return JSON.stringify(object);
            };
        }
    });
    afterEach(function () {
        if (window.Prototype) {
            Array.prototype.toJSON = savedJSON.array;
            String.prototype.toJSON = savedJSON.string;
            Object.toJSON = savedToJSON;
        }
    });
    it('has defaults', function () {
        expect(exportModel.get('uris')).toEqual(['/']);
        expect(exportModel.get('fileName')).toEqual('export.zip');
        expect(exportModel.get('encryptFile')).toEqual(false);
        expect(exportModel.get('includeRepositoryPermissions')).toEqual(true);
        expect(exportModel.get('includeScheduledReportJobs')).toEqual(true);
    });
    it('should reset defaults', function () {
        var setStub = sinon.spy(exportModel, 'set'), clearStub = sinon.spy(exportModel, 'clear'), serverPro = importExportTypesEnum.SERVER_PRO, serverCe = importExportTypesEnum.SERVER_CE, rootTenant = importExportTypesEnum.ROOT_TENANT, tenant = importExportTypesEnum.TENANT;
        exportModel.reset(serverPro);
        expect(exportModel.defaults).toEqual(_.extend(exportModel.defaults, exportModelAttributesFactory(serverPro)));
        exportModel.reset(serverCe);
        expect(exportModel.defaults).toEqual(_.extend(exportModel.defaults, exportModelAttributesFactory(serverCe)));
        exportModel.reset(rootTenant);
        expect(exportModel.defaults).toEqual(_.extend(exportModel.defaults, exportModelAttributesFactory(rootTenant)));
        exportModel.reset(tenant);
        expect(exportModel.defaults).toEqual(_.extend(exportModel.defaults, exportModelAttributesFactory(tenant)));
        expect(setStub.callCount).toEqual(8);
        expect(clearStub.callCount).toEqual(4);
        setStub.restore();
        clearStub.restore();
    });
    describe('Properties transformation', function () {
        it('can convert export parameters without uris', function () {
            var serverObj = exportModel.toExportTask();
            expect(serverObj.parameters).toEqual(['everything']);
            exportModel.set({
                includeAccessEvents: true,
                includeAuditEvents: true,
                includeSystemProperties: true,
                includeMonitoringEvents: true
            });
            serverObj = exportModel.toExportTask();
            expect(serverObj.parameters).toEqual([
                'everything',
                'include-access-events',
                'include-audit-events',
                'include-monitoring-events'
            ]);
            exportModel.set({
                everything: false,
                userForRoles: false,
                includeAccessEvents: false,
                includeAuditEvents: false,
                includeSystemProperties: false,
                includeMonitoringEvents: false
            });
            serverObj = exportModel.toExportTask();
            expect(serverObj.parameters).toEqual([
                'repository-permissions',
                'include-attributes',
                'include-server-settings'
            ]);
        });
        it('can convert export parameters with uris', function () {
            exportModel.set({
                uris: [
                    'a',
                    'b',
                    'c'
                ]
            });
            var serverObj = exportModel.toExportTask();
            expect(serverObj.parameters).toEqual(['everything']);
            exportModel.set({
                includeAccessEvents: true,
                includeAuditEvents: true,
                includeMonitoringEvents: true
            });
            serverObj = exportModel.toExportTask();
            expect(serverObj.parameters).toEqual([
                'everything',
                'include-access-events',
                'include-audit-events',
                'include-monitoring-events'
            ]);
            exportModel.set({
                everything: false,
                userForRoles: false,
                includeAccessEvents: false,
                includeAuditEvents: false
            });
            serverObj = exportModel.toExportTask();
            expect(serverObj.parameters).toEqual([
                'repository-permissions',
                'include-attributes',
                'include-server-settings',
                'include-monitoring-events'
            ]);
        });
        it('exports all if no URI specified', function () {
            var serverObj = exportModel.toExportTask();
            expect(serverObj.uris).toEqual(['/']);
        });
    });
    describe('Properties validation', function () {
        it('validates params', function () {
            exportModel.set('fileName', '');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.empty');
            exportModel.set('fileName', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.too.long');
            exportModel.set('fileName', '/');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '\\');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '?');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '%');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '*');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', ':');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '|');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '"');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '<');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '>');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.contains.not.supported.characters');
            exportModel.set('fileName', '.');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.not.valid');
            exportModel.set('fileName', '..');
            expect(exportModel.validate().fileName.code).toEqual('export.file.name.not.valid');
            exportModel.set('fileName', 'some.zip');
            expect(!exportModel.validate()).toBeTruthy();
        });
        it('is not valid when empty', function () {
            for (var key in exportModel.attributes) {
                exportModel.attributes[key] === true && (exportModel.attributes[key] = false);
            }
            expect(exportModel.validate()).toBeFalsy();
        });
        it('is valid when set some roles', function () {
            for (var key in exportModel.attributes) {
                exportModel.attributes[key] === true && (exportModel.attributes[key] = false);
            }
            exportModel.set('roles', ['aaa']);
            expect(!exportModel.validate()).toBeTruthy();
        });
        it('is valid when set some users', function () {
            for (var key in exportModel.attributes) {
                exportModel.attributes[key] === true && (exportModel.attributes[key] = false);
            }
            exportModel.set('users', ['aaa']);
            expect(!exportModel.validate()).toBeTruthy();
        });
        it('is acceptable when set some uris', function () {
            for (var key in exportModel.attributes) {
                exportModel.attributes[key] === true && (exportModel.attributes[key] = false);
            }
            exportModel.set('uris', ['aaa']);
            expect(!exportModel.validate()).toBeTruthy();
        });
        it('is acceptable when set some params', function () {
            for (var key in exportModel.attributes) {
                exportModel.attributes[key] === true && (exportModel.attributes[key] = false);
            }
            exportModel.set('everything', true);
            expect(!exportModel.validate()).toBeTruthy();
        });
    });
    describe('Interaction with server', function () {
        var server;
        beforeEach(function () {
            server = sinon.fakeServer.create();
            exportModel.set({
                users: [
                    'testUser1',
                    'testUser2',
                    'testUser3'
                ],
                roles: [
                    'testRole1',
                    'testRole2'
                ]
            });
        });
        afterEach(function () {
            server.restore();
        });
        it('should sent request for getting resources', function () {
            exportModel.set({
                includeReports: true,
                everything: false
            });
            exportModel.save();
            expect(server.requests.length).toEqual(1);
            expect(server.requests[0].method).toEqual('GET');
            expect(server.requests[0].url).toEqual('rest_v2/settings/exportResourceOptions');
        });
        it('makes valid request', function () {
            exportModel.set({
                uris: [
                    'a',
                    'b',
                    'c'
                ],
                includeReports: true,
                everything: true
            });
            exportModel.save();
            expect(server.requests.length).toEqual(1);
            expect(server.requests[0].method).toEqual('POST');
            expect(server.requests[0].url).toEqual('rest_v2/export');
            var resData = {
                uris: [
                    'a',
                    'b',
                    'c'
                ],
                roles: [
                    'testRole1',
                    'testRole2'
                ],
                users: [
                    'testUser1',
                    'testUser2',
                    'testUser3'
                ],
                scheduledJobs: [
                    'a',
                    'b',
                    'c'
                ],
                parameters: ['everything']

            };
            resData["keyAlias"] =  'importExportEncSecret';
            expect(server.requests[0].requestBody).toEqual(JSON.stringify(resData));
        });
        it('should handle error if session finished', function () {
            var triggerSpy = sinon.spy(exportModel, 'trigger'), response;
            server.respondWith('POST', 'rest_v2/export', [
                403,
                { 'Content-Type': 'text/html' },
                '<html><body>bla</body></html>'
            ]);
            exportModel.save().fail(function (xhr) {
                response = xhr;
            });
            server.respond();
            expect(triggerSpy).toHaveBeenCalledWith('error', exportModel, response);
            triggerSpy.restore();
        });
    });
});