define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!AttributesBundle");

var confirmDialogTypesEnum = require('../../serverSettingsCommon/enum/confirmDialogTypesEnum');

var confirmationDialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/ConfirmationDialog");

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
var typeToConfirmDialogOptionsMap = {};
var defaultConfirm = {
  title: i18n['attributes.confirm.dialog.title']
};
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.DELETE_CONFIRM] = defaultConfirm;
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.NAME_CONFIRM] = {
  title: i18n['attributes.confirm.dialog.title'],
  text: i18n['attributes.confirm.name.dialog.text']
};
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.CANCEL_CONFIRM] = {
  title: i18n['attributes.confirm.dialog.title'],
  text: i18n['attributes.confirm.cancel.dialog.text.custom']
};
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.PERMISSION_CONFIRM] = defaultConfirm;
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.EDIT_CONFIRM] = _.extend({}, {
  text: i18n['attributes.confirm.edit.dialog.text']
}, defaultConfirm);

module.exports = function (type) {
  return typeToConfirmDialogOptionsMap[type] && new confirmationDialog(typeToConfirmDialogOptionsMap[type]);
};

});