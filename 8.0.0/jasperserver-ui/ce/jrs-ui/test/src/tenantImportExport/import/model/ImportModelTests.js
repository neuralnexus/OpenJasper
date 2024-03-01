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

import $ from 'jquery';
import _ from 'underscore';
import sinon from 'sinon';
import ImportModel from 'src/tenantImportExport/import/model/ImportModel';
import importExportTypesEnum from 'src/tenantImportExport/export/enum/exportTypesEnum';
import importModelAttributesFactory from 'src/tenantImportExport/import/factory/importModelAttributesFactory';
import BaseModel from 'js-sdk/src/common/model/BaseModel';
import secureKeyTypeEnum from 'src/tenantImportExport/import/enum/secureKeyTypeEnum';

describe('Import Model', function () {
    let model;
    let sandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        model = new ImportModel(null, { form: $('<form></form>') });
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should have public methods', function () {
        expect(model.initialize).toBeDefined();
        expect(model.parse).toBeDefined();
        expect(model.save).toBeDefined();
        expect(model.cancel).toBeDefined();
        expect(model.reset).toBeDefined();
    });

    it('should be defined', function () {
        var initializeSpy = sinon.spy(ImportModel.prototype, 'initialize');
        model = new ImportModel(null, { form: $('<form></form>') });
        expect(initializeSpy).toHaveBeenCalled();
        expect(model.form).toBeDefined();
        initializeSpy.restore();
    });

    it('should parse attributes', function () {
        expect(model.parse()).toEqual(model.attributes);
    });

    it('should be saved', function () {
        var submitStub = sinon.spy(model.form, 'submit');
        model.save();
        expect(submitStub).toHaveBeenCalled();
        submitStub.restore();
    });

    it('should save existing model with default properties', () => {
        sandbox.stub(BaseModel.prototype, 'save').returns({
            fail() {}
        });

        model.set({
            id: 'id'
        });

        model.save();

        expect(BaseModel.prototype.save).toHaveBeenCalledWith({
            parameters: [
                'skip-themes',
                'update'
            ]
        }, {
            url: 'rest_v2/import/id'
        });
    });

    it('should save existing model when keyType is a secret value', () => {
        sandbox.stub(BaseModel.prototype, 'save').returns({
            fail() {}
        });

        model.set({
            id: 'id',
            skipUserUpdate: true,
            includeAccessEvents: true,
            includeAuditEvents: true,
            includeMonitoringEvents: true,
            includeServerSettings: true,
            mergeOrganization: true,
            skipThemes: true,
            update: true,
            keyType: secureKeyTypeEnum.VALUE
        });

        model.save();

        expect(BaseModel.prototype.save).toHaveBeenCalledWith({
            parameters: [
                'skip-user-update',
                'include-access-events',
                'include-audit-events',
                'include-monitoring-events',
                'include-server-setting',
                'merge-organization',
                'skip-themes',
                'update',
                'keyType',
                'secret-key'
            ]
        }, {
            url: 'rest_v2/import/id'
        });
    });

    it('should save existing model when keyType is a secret uri', () => {
        sandbox.stub(BaseModel.prototype, 'save').returns({
            fail() {}
        });

        model.set({
            id: 'id',
            skipUserUpdate: true,
            includeAccessEvents: true,
            includeAuditEvents: true,
            includeMonitoringEvents: true,
            includeServerSettings: true,
            mergeOrganization: true,
            skipThemes: true,
            update: true,
            keyType: secureKeyTypeEnum.FILE
        });

        model.save();

        expect(BaseModel.prototype.save).toHaveBeenCalledWith({
            parameters: [
                'skip-user-update',
                'include-access-events',
                'include-audit-events',
                'include-monitoring-events',
                'include-server-setting',
                'merge-organization',
                'skip-themes',
                'update',
                'keyType',
                'secret-uri'
            ]
        }, {
            url: 'rest_v2/import/id'
        });
    });

    it('should trigger error event on save fail', (done) => {
        const dfd = new $.Deferred();

        sandbox.stub(BaseModel.prototype, 'save').returns(dfd);

        model.set({
            id: 'id'
        });

        sandbox.stub(model, 'trigger');

        model.save();

        dfd.fail(() => {
            expect(model.trigger).toHaveBeenCalledWith('error');
            done();
        });

        dfd.reject();
    });

    it('should be cancelled', function () {
        var destroyStub = sinon.stub(model, 'destroy');
        model.cancel();
        expect(destroyStub).toHaveBeenCalled();
        destroyStub.restore();
    });

    it('should reset defaults', function () {
        var setStub = sinon.spy(model, 'set'), clearStub = sinon.spy(model, 'clear'), serverPro = importExportTypesEnum.SERVER_PRO, serverCe = importExportTypesEnum.SERVER_CE, rootTenant = importExportTypesEnum.ROOT_TENANT, tenant = importExportTypesEnum.TENANT;
        model.reset(serverPro);
        expect(model.defaults).toEqual(_.extend(model.defaults, importModelAttributesFactory(serverPro)));
        model.reset(serverCe);
        expect(model.defaults).toEqual(_.extend(model.defaults, importModelAttributesFactory(serverCe)));
        model.reset(rootTenant);
        expect(model.defaults).toEqual(_.extend(model.defaults, importModelAttributesFactory(rootTenant)));
        model.reset(tenant);
        expect(model.defaults).toEqual(_.extend(model.defaults, importModelAttributesFactory(tenant)));
        expect(setStub.callCount).toEqual(8);
        expect(clearStub.callCount).toEqual(4);
        setStub.restore();
        clearStub.restore();
    });

    it('should validate fileName', function () {
        model.set('fileName', '');
        expect(model.validate()).toBeTruthy();
        model.set('fileName', 'test.zip');
        expect(!model.validate()).toBeTruthy();
        model.set('fileName', 'test.html');
        expect(model.validate()).toBeTruthy();
    });
});