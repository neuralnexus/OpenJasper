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
import ExportDialogView from 'src/tenantImportExport/export/view/ExportDialogView';
import Dialog from 'js-sdk/src/common/component/dialog/Dialog';

describe('Export Dialog View', function () {
    var view;
    beforeEach(function () {
        view = new ExportDialogView();
    });
    afterEach(function () {
        view && view.remove();
    });
    it('should have public methods', function () {
        expect(view.constructor).toBeDefined();
        expect(view.initialize).toBeDefined();
        expect(view.openRepoDialog).toBeDefined();
        expect(view.openTenantDialog).toBeDefined();
    });
    it('should be defined', function () {
        view && view.remove();
        var initializeSpy = sinon.spy(ExportDialogView.prototype, 'initialize');
        view = new ExportDialogView();
        expect(initializeSpy).toHaveBeenCalled();
        expect(view.exportView).toBeDefined();
        expect(view.modal).toEqual(true);
        initializeSpy.restore();
    });
    it('should open repository dialog', function () {
        var openStub = sinon.stub(Dialog.prototype, 'open');
        view.openRepoDialog({ URIString: '/organizations' });
        expect(view.$el.hasClass('repository-export-dialog')).toBe(true);    //            expect(view.$(".header > .title").text()).toBe(i18n["export.dialog.title"] + " " + i18n["export.dialog.repository.title"]);
        //            expect(view.$(".header > .title").text()).toBe(i18n["export.dialog.title"] + " " + i18n["export.dialog.repository.title"]);
        expect(openStub).toHaveBeenCalled();
        openStub.restore();
    });
    it('should open/close tenant dialog', function () {
        var openStub = sinon.stub(Dialog.prototype, 'open'), tenant = {
            name: 'test',
            isRoot: function () {
                return false;
            }
        };
        view.openTenantDialog(tenant);
        expect(view.$el.hasClass('tenant-export-dialog')).toBe(true);    //            expect(view.$(".header > .title").text()).toBe(i18n["export.dialog.title"] + " " + tenant.name);
        //            expect(view.$(".header > .title").text()).toBe(i18n["export.dialog.title"] + " " + tenant.name);
        expect(openStub).toHaveBeenCalled();
        expect(view.$el.find('.export-view').length).toBe(1);
        openStub.restore();
    });
    it('should perform export while click on export button and then be closed', function () {
        var closeStub = sinon.stub(view, 'close'), doExportStub = sinon.stub(view.exportView, 'doExport'), openStub = sinon.stub(Dialog.prototype, 'open');
        view.openRepoDialog({ URIString: '/organizations' });
        view.trigger('button:export');
        expect(doExportStub).toHaveBeenCalled();
        expect(closeStub).toHaveBeenCalled();
        doExportStub.restore();
        closeStub.restore();
        openStub.restore();
    });
    it('should be closed while click on cancel button', function () {
        view && view.remove();
        var closeStub = sinon.stub(ExportDialogView.prototype, 'close');
        view = new ExportDialogView();
        view.trigger('button:cancel');
        expect(closeStub).toHaveBeenCalled();
        closeStub.restore();
    });
    it('should enable export button when export is available', function () {
        var enableButtonStub = sinon.stub(view.buttons, 'enable');
        view.exportView.model.trigger('validated', true);
        expect(enableButtonStub).toHaveBeenCalledWith('export');
        enableButtonStub.restore();
    });
    it('should disable export button when export is unavailable', function () {
        var disableButtonStub = sinon.stub(view.buttons, 'disable');
        view.exportView.model.trigger('validated', false);
        expect(disableButtonStub).toHaveBeenCalledWith('export');
        disableButtonStub.restore();
    });
});