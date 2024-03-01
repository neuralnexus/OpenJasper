define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var importExportTypesEnum = require('../../export/enum/exportTypesEnum');

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
var attributesByType = {};
var events = {
  includeAccessEvents: true,
  includeAuditEvents: true,
  includeMonitoringEvents: true
};
var themes = {
  skipThemes: true
};
var serverAssets = {
  includeServerSettings: true
};
attributesByType[importExportTypesEnum.ROOT_TENANT] = _.extend({}, events, themes);
attributesByType[importExportTypesEnum.TENANT] = _.extend({}, themes);
attributesByType[importExportTypesEnum.SERVER_PRO] = _.extend({}, events, themes, serverAssets);
attributesByType[importExportTypesEnum.SERVER_CE] = _.extend({}, serverAssets, {
  includeAccessEvents: true
});

module.exports = function (type) {
  return attributesByType[type];
};

});