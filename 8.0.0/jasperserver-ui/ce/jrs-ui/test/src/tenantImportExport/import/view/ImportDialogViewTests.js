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
import ImportDialogView from 'src/tenantImportExport/import/view/ImportDialogView';
import Dialog from 'js-sdk/src/common/component/dialog/Dialog';
import $ from 'jquery';

describe('Import Dialog View', function () {
    var view;
    beforeEach(function () {
        view = new ImportDialogView();
    });
    afterEach(function () {
        view && view.remove();
    });
    it('should have public methods', function () {
        expect(view.constructor).toBeDefined();
        expect(view.initialize).toBeDefined();
        expect(view.openDialog).toBeDefined();
    });
    it('should be defined', function () {
        view && view.remove();
        var initializeSpy = sinon.spy(ImportDialogView.prototype, 'initialize');
        view = new ImportDialogView();
        expect(initializeSpy).toHaveBeenCalled();
        expect(view.importView).toBeDefined();
        expect(view.modal).toEqual(true);
        initializeSpy.restore();
    });
    it('should be able to open', function () {
        var openStub = sinon.stub(Dialog.prototype, 'open'), tenant = {
            name: 'test',
            isRoot: function () {
                return false;
            }
        };

        view.openDialog(tenant);

        expect(view.$el.hasClass('tenant-import-dialog')).toBe(true);
        expect(openStub).toHaveBeenCalled();
        expect(view.$el.find('#importDataFile').length).toBe(1);

        openStub.restore();
    });
    it('should perform import while click on import button and then be closed', function () {
        var closeStub = sinon.stub(view, 'close'), doImportStub = sinon.stub(view.importView, 'doImport').callsFake(function () { return $.Deferred().resolve([]); }), openStub = sinon.stub(Dialog.prototype, 'open'), tenant = {
            name: 'test',
            isRoot: function () {
                return false;
            }
        };
        view.openDialog(tenant);
        view.trigger('button:import');
        expect(doImportStub).toHaveBeenCalled();
        expect(closeStub).toHaveBeenCalled();
        doImportStub.restore();
        closeStub.restore();
        openStub.restore();
    });
    it('should be closed while click on cancel button', function () {
        view && view.remove();
        var closeStub = sinon.stub(ImportDialogView.prototype, 'close');
        view = new ImportDialogView();
        view.trigger('button:cancel');
        expect(closeStub).toHaveBeenCalled();
        closeStub.restore();
    });
    it('should enable import button when import is available', function () {
        var enableButtonStub = sinon.stub(view.buttons, 'enable');
        view.importView.model.trigger('validated', true);
        expect(enableButtonStub).toHaveBeenCalledWith('import');
        enableButtonStub.restore();
    });
    it('should disable import button when import is unavailable', function () {
        var disableButtonStub = sinon.stub(view.buttons, 'disable');
        view.importView.model.trigger('validated', false);
        expect(disableButtonStub).toHaveBeenCalledWith('import');
        disableButtonStub.restore();
    });
});