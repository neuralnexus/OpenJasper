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
import $ from 'jquery';
import ExportPageView from 'src/tenantImportExport/export/view/ExportPageView';

describe('Export Page View', function () {
    var view;
    beforeEach(function () {
        view = new ExportPageView();
    });
    afterEach(function () {
        view && view.remove();
    });
    it('should have public methods', function () {
        expect(view.initialize).toBeDefined();
        expect(view.doExport).toBeDefined();
    });
    it('should be defined', function () {
        view && view.remove();
        var initializeSpy = sinon.spy(ExportPageView.prototype, 'initialize');
        view = new ExportPageView();
        expect(initializeSpy).toHaveBeenCalled();
        expect(view.exportView).toBeDefined();
        initializeSpy.restore();
    });
    it('should perform export while click on export button', function () {
        var doExportStub = sinon.stub(view.exportView, 'doExport'), $exportButton = $('<button></button>', { id: 'exportButton' });
        view.$el.append($exportButton);
        $exportButton.click();
        expect(doExportStub).toHaveBeenCalled();
        doExportStub.restore();
    });
    it('should enable export button when export is available', function () {
        var $exportButton = $('<button></button>', { id: 'exportButton' });
        view.$el.append($exportButton);
        view.exportView.model.trigger('validated', true);
        expect($exportButton.attr('disabled')).toEqual(undefined);
        $exportButton.remove();
    });
    it('should disable export button when export is unavailable', function () {
        var $exportButton = $('<button></button>', { id: 'exportButton' });
        view.$el.append($exportButton);
        view.exportView.model.trigger('validated', false);
        expect($exportButton.attr('disabled')).toEqual('disabled');
        $exportButton.remove();
    });
});