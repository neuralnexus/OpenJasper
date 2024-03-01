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
import i18n from '../../i18n/all.properties';
import SimpleDomainView from '../fileDataSource/SimpleDomainView';
import BaseSaveDialogView from './BaseSaveDialogView';
import textDataSourceSaveDialogTemplate from './template/textDataSourceSaveDialogTemplate.htm';

export default BaseSaveDialogView.extend({
    saveDialogTemplate: textDataSourceSaveDialogTemplate,
    constructor: function (options) {
        options || (options = {});
        this.options = _.extend({}, options);
        this.options.isEmbedded = !!this.options.saveFn;
        BaseSaveDialogView.prototype.constructor.call(this, options);
    },
    initialize: function () {
        this.preSelectedFolder = this.options.model.options.parentFolderUri;
        BaseSaveDialogView.prototype.initialize.apply(this, arguments);
    },
    extendModel: function (sourceModel) {
        var model = BaseSaveDialogView.prototype.extendModel.call(this, sourceModel);
        model.set('prepareDataForReporting', !(this.options.isEmbedded || this.options.isEditMode));
        return model;
    },
    _getLabelForSaveButton: function (model) {
        return "resource.datasource.saveDialog.save";
    },
    _saveSuccessCallback: function (model, data) {
        if (!!this.model.get('prepareDataForReporting')) {
            var simpleDomainView = new SimpleDomainView({
                cancel: this.options.success,
                dataSource: this.model.toJSON()
            });
            simpleDomainView.startDialog();
        } else {
            if (_.isFunction(this.options.success)) {
                this.options.success();
            }
        }
        this._closeDialog();
    }
});