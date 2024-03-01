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
import MergeTenantDialogView from 'src/tenantImportExport/import/view/MergeTenantDialogView';
import BaseWarningDialogView from 'src/tenantImportExport/view/BaseWarningDialogView';

describe('Merge tenant dialog view', function () {
    var view;
    beforeEach(function () {
        view = new MergeTenantDialogView();
    });
    afterEach(function () {
        view && view.remove();
    });
    it('should have public methods', function () {
        expect(view.constructor).toBeDefined();
        expect(view.open).toBeDefined();
    });
    it('should be defined', function () {
        view && view.remove();
        var constructor = BaseWarningDialogView.prototype.constructor
        var constructorStub = sinon.stub(BaseWarningDialogView.prototype, 'constructor').callsFake(constructor);
        view = new MergeTenantDialogView();
        expect(constructorStub).toHaveBeenCalled();
        expect(view.modal).toBe(true);
        constructorStub.restore();
    });
    it('should opens', function () {
        var openStub = sinon.stub(BaseWarningDialogView.prototype, 'open');
        view.open();
        expect(view.$el.hasClass('merge-tenant-dialog')).toBe(true);    //            expect(view.$(".header > .title").text()).toBe(i18n["import.dialog.merge.tenant.title"]);
        //            expect(view.$(".header > .title").text()).toBe(i18n["import.dialog.merge.tenant.title"]);
        expect(openStub).toHaveBeenCalled();
        openStub.restore();
    });
    it('should be closed after click on import/cancel button', function () {
        view && view.remove();
        var closeStub = sinon.stub(MergeTenantDialogView.prototype, 'close');
        view = new MergeTenantDialogView();
        view.trigger('button:import');
        view.trigger('button:cancel');
        expect(closeStub.callCount).toEqual(2);
        closeStub.restore();
    });
});