define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var i18n = require("bundle!EditSettingsBundle");

var confirmDialogTypesEnum = require('../../serverSettingsCommon/enum/confirmDialogTypesEnum');

var ConfirmationDialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/ConfirmationDialog");

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
  title: i18n['editSettings.confirm.dialog.title']
};
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.DELETE_CONFIRM] = defaultConfirm;
typeToConfirmDialogOptionsMap[confirmDialogTypesEnum.CANCEL_CONFIRM] = {
  title: i18n['editSettings.confirm.dialog.title'],
  text: i18n['editSettings.confirm.cancel.dialog.text.custom']
};

module.exports = function (type) {
  return typeToConfirmDialogOptionsMap[type] && new ConfirmationDialog(typeToConfirmDialogOptionsMap[type]);
};

});