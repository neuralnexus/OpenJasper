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
import sinon from 'sinon';
import ImportView from 'src/tenantImportExport/import/view/ImportView';
import importRestErrorCodesEnum from 'src/tenantImportExport/import/enum/importRestErrorCodesEnum';
import secureKeyTypeEnum from 'src/tenantImportExport/import/enum/secureKeyTypeEnum';

import ImportStateModel from 'src/tenantImportExport/import/model/ImportStateModel';
import importErrorMessageFactory from 'src/tenantImportExport/import/factory/importErrorMessageFactory';

describe('Import View', function () {
    let view;
    let sandbox;
    let keyFileDialog, getCustomKeysStub;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        keyFileDialog = {
            events: {},
            open: sandbox.stub(),
            on(event, callback) {
                this.events[event] = callback;
            },
            trigger(event) {
                this.events[event] && this.events[event]();
            }
        };

        view = new ImportView({
            keyFileDialog: keyFileDialog
        });
        getCustomKeysStub = sinon.stub(view.customKeyStateModel , 'getCustomKeys').callsFake(function () {
            return $.Deferred().resolve([]);
        });
    });

    afterEach(() => {
        getCustomKeysStub.restore();
        sandbox.restore();
        view && view.remove();
    });

    it('should have public methods', function () {
        expect(view.initialize).toBeDefined();
        expect(view.render).toBeDefined();
        expect(view.validateFile).toBeDefined();
        expect(view.doImport).toBeDefined();
    });

    it('should be defined', function () {
        view && view.remove();
        var initializeSpy = sinon.spy(ImportView.prototype, 'initialize');
        view = new ImportView();
        expect(initializeSpy).toHaveBeenCalled();
        expect(view.stateModel).toBeDefined();
        expect(view.model).toBeDefined();
        expect(view.loadingDialog).toBeDefined();
        expect(view.mergeTenantDialogView).toBeDefined();
        expect(view.dependentResourcesDialogView).toBeDefined();
        expect(view.notification).toBeDefined();
        initializeSpy.restore();
    });

    it('should have chainable render', function () {
        expect(view.render({
            type: 'tenant',
            tenantId: 'test'
        })).toEqual(view);
    });

    describe('import phase', () => {

        it('should show notification with error on failed import', () => {
            sandbox.stub(importErrorMessageFactory, 'create').withArgs('errorCode').returns('error');

            view.stateModel.set({
                error: {
                    errorCode: 'errorCode'
                }
            }, {
                silent: true
            });

            sandbox.stub(view.notification, 'show');

            view.stateModel.set('phase', ImportStateModel.STATE.FAILED);

            expect(view.notification.show).toHaveBeenCalledWith({
                delay: false,
                message: 'error',
                type: 'warning'
            });
        });
    });

    it('should set keyType to key value and clean errors', () => {
        $('body').append(view.$el);

        view.render({
            type: 'tenant',
            tenantId: 'test'
        });

        sandbox.stub(view.model, 'set');

        $('input.jr-jKeyValue').trigger('change');

        expect(view.model.set).toHaveBeenCalledWith('keyType', secureKeyTypeEnum.VALUE);
        expect(view.model.set).toHaveBeenCalledWith({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    });

    it('should set keyType to default and clean errors', () => {
        $('body').append(view.$el);

        view.render({
            type: 'tenant',
            tenantId: 'test'
        });

        sandbox.stub(view.model, 'set');

        $('input.jr-jKeyValue').trigger('change');
        $('input.jr-jDefaultKey').trigger('change');

        expect(view.model.set).toHaveBeenCalledWith('keyType', '');
        expect(view.model.set).toHaveBeenCalledWith({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    });

    it('should set keyType to key file and clean errors', () => {
        $('body').append(view.$el);

        view.render({
            type: 'tenant',
            tenantId: 'test'
        });

        sandbox.stub(view.model, 'set');

        $('input.jr-jKeyFile').trigger('change');

        expect(view.model.set).toHaveBeenCalledWith('keyType', secureKeyTypeEnum.FILE);
        expect(view.model.set).toHaveBeenCalledWith({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    });

    it('should open dialog on open repository dialog button click', () => {
        $('body').append(view.$el);

        view.render({
            type: 'tenant',
            tenantId: 'test'
        });

        view.model.set('keyType', secureKeyTypeEnum.FILE);

        $('button.jr-jRepositoryBrowserButton').trigger('click');

        expect(keyFileDialog.open).toHaveBeenCalled();
    });

    it('should set secret key value on key input and clean errors', () => {
        $('body').append(view.$el);

        view.render({
            type: 'tenant',
            tenantId: 'test'
        });

        view.model.set('keyType', secureKeyTypeEnum.VALUE);

        sandbox.stub(view.model, 'set');

        const input = $('input.jr-jSecretKey');

        input.val('abc');

        input.trigger($.Event('input'));

        expect(view.model.set).toHaveBeenCalledWith('secretKey', 'abc', {silent: true});

        expect(view.model.set).toHaveBeenCalledWith({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    });

    it('should set secretUri value on secretUri input and clean errors', () => {
        $('body').append(view.$el);

        view.render({
            type: 'tenant',
            tenantId: 'test'
        });

        view.model.set('keyType', secureKeyTypeEnum.FILE);

        sandbox.stub(view.model, 'set');

        const input = $('input.jr-jSecretUri');

        input.val('abc');

        input.trigger($.Event('input'));

        expect(view.model.set).toHaveBeenCalledWith('secretUri', 'abc', {silent: true});

        expect(view.model.set).toHaveBeenCalledWith({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    });

    it('should set secretUri and clear any secret key related errors on keyFileDialog close', () => {
        sandbox.stub(view.model, 'set');

        keyFileDialog.selectedResource = {
            resourceUri: '/resource/uri'
        };

        keyFileDialog.trigger('close');

        expect(view.model.set).toHaveBeenCalledWith('secretUri', '/resource/uri');
        expect(view.model.set).toHaveBeenCalledWith({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    });

    it('should set invalid key error on failed import', (done) => {
        const dfd = new $.Deferred();

        sandbox.stub(view.model, 'isValid').withArgs(true).returns(true);
        sandbox.stub(view.model, 'save').returns(dfd);
        sandbox.stub(view.model, 'set');
        sandbox.stub(view.stateModel, 'set');

        view.doImport().fail(() => {
            expect(view.model.set).toHaveBeenCalledWith('invalidKeyError', "import.invalid.secretKey");

            done();
        });

        dfd.reject({
            errorCode: importRestErrorCodesEnum.INVALID_SECRET_KEY
        });
    });

    it('should set invalid secure content error on failed import', (done) => {
        const dfd = new $.Deferred();

        sandbox.stub(view.model, 'isValid').withArgs(true).returns(true);
        sandbox.stub(view.model, 'save').returns(dfd);
        sandbox.stub(view.model, 'set');
        sandbox.stub(view.stateModel, 'set');

        view.doImport().fail(() => {
            expect(view.model.set).toHaveBeenCalledWith('invalidSecureFileContentError', "import.invalid.secretUri.secretFile");
        }).always(() => {
            expect(view.stateModel.set).toHaveBeenCalledWith({
                errorCode: importRestErrorCodesEnum.INVALID_SECRET_FILE_CONTENT
            });

            done();
        });

        dfd.reject({
            errorCode: importRestErrorCodesEnum.INVALID_SECRET_FILE_CONTENT
        });
    });

    it('should set invalid secure file error on failed import', (done) => {
        const dfd = new $.Deferred();

        sandbox.stub(view.model, 'isValid').withArgs(true).returns(true);
        sandbox.stub(view.model, 'save').returns(dfd);
        sandbox.stub(view.model, 'set');
        sandbox.stub(view.stateModel, 'set');

        view.doImport().fail(() => {
            expect(view.model.set).toHaveBeenCalledWith('invalidSecureFileContentError', "import.invalid.secretUri");
        }).always(() => {
            expect(view.stateModel.set).toHaveBeenCalledWith({
                errorCode: importRestErrorCodesEnum.INVALID_SECRET_FILE
            });

            done();
        });

        dfd.reject({
            errorCode: importRestErrorCodesEnum.INVALID_SECRET_FILE
        });
    });

    it('should set invalid secret key length error on failed import', (done) => {
        const dfd = new $.Deferred();

        sandbox.stub(view.model, 'isValid').withArgs(true).returns(true);
        sandbox.stub(view.model, 'save').returns(dfd);
        sandbox.stub(view.model, 'set');
        sandbox.stub(view.stateModel, 'set');

        view.doImport().fail(() => {
            expect(view.model.set).toHaveBeenCalledWith('invalidKeyError', "import.invalid.secretKey.length");
        }).always(() => {
            expect(view.stateModel.set).toHaveBeenCalledWith({
                errorCode: importRestErrorCodesEnum.INVALID_SECRET_KEY_LENGTH
            });

            done();
        });

        dfd.reject({
            errorCode: importRestErrorCodesEnum.INVALID_SECRET_KEY_LENGTH
        });
    });
});