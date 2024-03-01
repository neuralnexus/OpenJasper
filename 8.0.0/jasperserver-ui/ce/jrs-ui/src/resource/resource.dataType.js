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
/**
 * @version: $Id$
 */
import jQuery from 'jquery';
import resource from './resource.base';
import {ValidationModule} from "../util/utils.common";

var resourceDataType = {
    PAGE_ID: 'addResource_dataType',
    DATA_TYPE_ID: 'dataType\\.dataTypeType',
    LABEL_ID: 'dataType\\.label',
    RESOURCE_ID_ID: 'dataType\\.name',
    DESCRIPTION_ID: 'dataType\\.description',
    SAVE_BUTTON_ID: 'done',
    CHANGE_TYPE_BUTTON_ID: 'changeCombo',
    _canGenerateId: true,
    initialize: function (options) {
        this._form = jQuery('#' +this.PAGE_ID).find('form')[0];
        this._dataType = jQuery('#' +this.DATA_TYPE_ID)[0];
        this._label = jQuery('#' +this.LABEL_ID)[0];
        this._resourceId = jQuery('#' +this.RESOURCE_ID_ID)[0];
        this._description = jQuery('#' +this.DESCRIPTION_ID)[0];
        this._saveButton = jQuery('#' +this.SAVE_BUTTON_ID);
        this._changeTypeButton = jQuery('#' +this.CHANGE_TYPE_BUTTON_ID)[0];
        this._isEditMode = options.isEditMode;
        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);
        this._initEvents();
    },
    _initEvents: function () {
        this._dataType.observe('change', function () {
            this._changeTypeButton.click();
        }.bindAsEventListener(this));
        this._saveButton.on('click', function (e) {
            if (!this._isDataValid()) {
                e.stopPropagation();
            }
        }.bindAsEventListener(this));
        jQuery(this._form).on('keyup', function (e) {
            var element = e.target;
            var targetElements = [
                this._label,
                this._resourceId,
                this._description
            ];
            if (targetElements.include(element)) {
                ValidationModule.validate(resource.getValidationEntries([element]));
                if (element == this._resourceId && this._resourceId.getValue() != resource.generateResourceId(this._label.getValue())) {
                    this._canGenerateId = false;
                }
                if (element == this._label && !this._isEditMode && this._canGenerateId) {
                    this._resourceId.setValue(resource.generateResourceId(this._label.getValue()));
                    ValidationModule.validate(resource.getValidationEntries([this._resourceId]));
                }
            }
        }.bind(this));
    },
    _isDataValid: function () {
        var elementsToValidate = [
            this._label,
            this._resourceId,
            this._description
        ];
        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    }
};

export default resourceDataType;