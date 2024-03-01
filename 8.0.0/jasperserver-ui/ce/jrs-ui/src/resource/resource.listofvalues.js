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
import {matchAny} from "../util/utils.common";
import {ValidationModule} from "../util/utils.common";

var resourceListOfValues = {
    LABEL_ID: 'labelID',
    RESOURCE_ID_ID: 'resourceID',
    DESCRIPTION_ID: 'description',
    ITEM_NAME_ID: 'name',
    ITEM_VALUE_ID: 'value',
    SUBMIT_EVENT_ID: 'submitEvent',
    ITEM_TO_DELETE_ID: 'itemToDelete',
    SAVE_BUTTON_PATTERN: '#save',
    CANCEL_BUTTON_PATTERN: '#cancel',
    ADD_LINK_ID: 'add',
    LINK_PATTERN: 'a.launcher',
    _canGenerateId: true,
    initialize: function (options) {
        this._form = jQuery(document.body).find('form')[0];
        this._label = jQuery('#' +this.LABEL_ID)[0];
        this._resourceId = jQuery('#' +this.RESOURCE_ID_ID)[0];
        this._description = jQuery('#' +this.DESCRIPTION_ID)[0];
        this._itemName = jQuery('#' +this.ITEM_NAME_ID)[0];
        this._itemValue = jQuery('#' +this.ITEM_VALUE_ID)[0];
        this._submitEvent = jQuery('#' +this.SUBMIT_EVENT_ID)[0];
        this._itemToDelete = jQuery('#' +this.ITEM_TO_DELETE_ID)[0];
        this._addLink = jQuery('#' +this.ADD_LINK_ID)[0];
        this._isEditMode = options.isEditMode;
        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);
        this._itemName.validator = this._itemNameValidator.bind(this);
        this._itemValue.validator = this._itemValueValidator.bind(this);
        this._initEvents();
    },
    _initEvents: function () {
        jQuery(this._form).on('click', function (e) {
            var element = e.target;
            e.stopPropagation();
            if (matchAny(element, [this.SAVE_BUTTON_PATTERN], true)) {
                if (this._isDataValid()) {
                    jQuery(this._submitEvent).attr('name', '_eventId_save');
                    this._form.submit();
                }
            } else if (element == this._addLink) {
                if (this._isValueDataValid() && this._isDataValid()) {
                    this._submitEvent.writeAttribute('name', '_eventId_addItem');
                    this._form.submit();
                }
            } else if (matchAny(element, [this.LINK_PATTERN], true) && element != this._addLink) {
                var id = element.identify();
                this._itemToDelete.setValue(id);
                jQuery(this._submitEvent).attr('name', '_eventId_removeItem');
                this._form.submit();
            } else if (matchAny(element, [this.CANCEL_BUTTON_PATTERN], true)) {
                jQuery(this._submitEvent).attr('name', '_eventId_cancel');
                this._form.submit();
            }
        }.bind(this));
        jQuery(this._form).on('keyup', function (e) {
            var element = e.target;
            var targetElements = [
                this._label,
                this._resourceId,
                this._description,
                this._itemName,
                this._itemValue
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
    },
    _isValueDataValid: function () {
        var elementsToValidate = [
            this._itemName,
            this._itemValue
        ];
        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    },
    _itemNameValidator: function (value) {
        var isValid = true;
        var errorMessage = '';
        if (value.blank()) {
            errorMessage = resource.messages['itemNameIsEmpty'];
            isValid = false;
        }
        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },
    _itemValueValidator: function (value) {
        var isValid = true;
        var errorMessage = '';
        if (value.blank()) {
            errorMessage = resource.messages['itemValueIsEmpty'];
            isValid = false;
        }
        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    }
};

export default resourceListOfValues;