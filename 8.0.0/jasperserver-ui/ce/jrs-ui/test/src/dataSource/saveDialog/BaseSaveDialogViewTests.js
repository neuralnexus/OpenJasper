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
import BaseSaveDialogView from 'src/dataSource/saveDialog/BaseSaveDialogView';
import ResourceModel from 'bi-repository/src/bi/repository/model/RepositoryResourceModel';

describe('Testing BaseSaveDialogView ', function () {
    var sourceAttributes, sourceModel;
    beforeEach(function () {
        sourceAttributes = {
            dataSourceName: 'textDataSource',
            someAttribute1: 'someValue1',
            someAttribute2: 'someValue2'
        };
        sourceModel = new ResourceModel();
        sourceModel.toJSON = function () {
            return sourceAttributes;
        };
        sourceModel.type = 'testBaseSaveDialogView_type';
        sourceModel.get = function (attribute) {
            return sourceAttributes[attribute];
        };
        sourceModel.set = function (attribute, value) {
            sourceAttributes[attribute] = value;
        };
    });
    it(' extendModel()', function () {
        sinon.stub(BaseSaveDialogView.prototype, 'initializeTree');
        var resultModel = new BaseSaveDialogView({ model: sourceModel }).model;
        expect(resultModel).toBeDefined();
        expect(resultModel).toBe(sourceModel);
        expect(resultModel.validation.label).toBeDefined();
        expect(resultModel.validation.name).toBeDefined();
        expect(resultModel.validation.description).toBeDefined();
        expect(resultModel.validation.parentFolderUri).toBeDefined();
        BaseSaveDialogView.prototype.initializeTree.restore();
    });
    it(' _saveSuccessCallback() _closeDialog and success are called, no domain generation', function () {
        // explicitly disable domain generation
        sourceAttributes.prepareDataForReporting = false;
        sinon.stub(BaseSaveDialogView.prototype, 'initializeTree');
        var success = sinon.spy(), view = new BaseSaveDialogView({
            model: sourceModel,
            success: success
        });
        view._closeDialog = sinon.spy();
        view._saveSuccessCallback();
        expect(view._closeDialog).toHaveBeenCalled();
        expect(success).toHaveBeenCalled();
        BaseSaveDialogView.prototype.initializeTree.restore();
    });
    it('performSave() should accept saveFn param as another save handler', function () {
        var saveFn = sinon.stub();
        sinon.stub(BaseSaveDialogView.prototype, 'initializeTree');
        var view = new BaseSaveDialogView({
            model: sourceModel,
            saveFn: saveFn
        });
        view.performSave();
        expect(saveFn).toHaveBeenCalled();
        BaseSaveDialogView.prototype.initializeTree.restore();
    });
});