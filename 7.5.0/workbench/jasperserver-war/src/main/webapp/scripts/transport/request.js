define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var configs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var requestSettings = require("requestSettings");

var xdm = require("transport/xdmRequest");

var xhr = require("transport/xhrRequest");

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
var requestFunc;

module.exports = function () {
  var args = Array.prototype.slice.call(arguments, 0);

  if (!requestFunc) {
    requestFunc = configs.isXdm ? xdm : xhr;
  }

  var mergedOptions = _.extend({}, requestSettings, arguments[0]);

  if (requestSettings.headers && arguments[0].headers) {
    mergedOptions.headers = _.extend({}, requestSettings.headers, arguments[0].headers);
  }

  args[0] = mergedOptions;
  return requestFunc.apply($, args);
};

});