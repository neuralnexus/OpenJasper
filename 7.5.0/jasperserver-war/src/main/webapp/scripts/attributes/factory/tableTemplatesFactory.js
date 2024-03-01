define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var attributesDesignerTemplate = require("text!../../attributes/templates/attributesDesignerTemplate.htm");

var attributesViewerTemplate = require("text!../../attributes/templates/attributesViewerTemplate.htm");

var emptyViewerTemplate = require("text!../../attributes/templates/emptyViewerTemplate.htm");

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
module.exports = function (options) {
  options = options || {};
  return options.readOnly ? !options.empty ? attributesViewerTemplate : emptyViewerTemplate : attributesDesignerTemplate;
};

});