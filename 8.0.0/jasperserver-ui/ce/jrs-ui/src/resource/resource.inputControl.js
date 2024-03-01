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

var addInputControl = {
    PAGE_ID: 'addResource_inputControl',
    LABEL_ID: 'label',
    RESOURCE_ID_ID: 'name',
    DESCRIPTION_ID: 'description',
    NEXT_BUTTON_ID: 'next',
    NEXT_AND_SUBMIT_BUTTON_ID: 'nextAndSubmit',
    TYPE_ID: 'dataTypeKind',
    _canGenerateId: true,
    initialize: function (options) {
        this._form = jQuery('#' +this.PAGE_ID).find('form')[0];
        this._label = jQuery('#' +this.LABEL_ID)[0];
        this._resourceId = jQuery('#' +this.RESOURCE_ID_ID)[0];
        this._description = jQuery('#' +this.DESCRIPTION_ID)[0];
        this._nextButton = jQuery('#' +this.NEXT_BUTTON_ID);
        this._nextAndSubmitButton = jQuery('#' +this.NEXT_AND_SUBMIT_BUTTON_ID);
        this._typeId = jQuery('#' +this.TYPE_ID)[0];
        this._isEditMode = options.isEditMode;
        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);
        this._initEvents();
    },
    _initEvents: function () {
        var nextHandler = function (e) {
            if (!this._isDataValid()) {
                e.stopPropagation();
            }
        }.bind(this);
        this._nextButton.on('click', nextHandler);
        this._nextAndSubmitButton.on('click', nextHandler);
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
        }.bindAsEventListener(this));
        this._typeId.observe('change', function (e) {
            if (e.target.value == 1) {
                this._nextButton.addClass('hidden');
                this._nextAndSubmitButton.removeClass('hidden');
            } else {
                this._nextButton.removeClass('hidden');
                this._nextAndSubmitButton.addClass('hidden');
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

export default addInputControl;