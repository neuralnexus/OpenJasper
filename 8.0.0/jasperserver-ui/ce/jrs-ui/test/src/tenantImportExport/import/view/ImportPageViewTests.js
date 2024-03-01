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
import ImportPageView from 'src/tenantImportExport/import/view/ImportPageView';

describe('Import Page View', function () {
    var view;
    beforeEach(function () {
        view = new ImportPageView();
    });
    afterEach(function () {
        view && view.remove();
    });
    it('should have public methods', function () {
        expect(view.initialize).toBeDefined();
        expect(view.doImport).toBeDefined();
    });
    it('should be defined', function () {
        view && view.remove();
        var initializeSpy = sinon.spy(ImportPageView.prototype, 'initialize');
        view = new ImportPageView();
        expect(initializeSpy).toHaveBeenCalled();
        expect(view.importView).toBeDefined();
        initializeSpy.restore();
    });
    it('should perform import while click on import button', function () {
        var doImportStub = sinon.stub(view.importView, 'doImport'), $importButton = $('<button></button>', { id: 'importButton' });
        view.$el.append($importButton);
        $importButton.click();
        expect(doImportStub).toHaveBeenCalled();
        doImportStub.restore();
    });
});