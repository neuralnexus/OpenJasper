define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _namespaceNamespace = require("../namespace/namespace");

var JRS = _namespaceNamespace.JRS;

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

/**
 * @author; inesterenko
 * @version: $Id$
 */
JRS.Log = function (args) {
  //module:
  //
  //  controls.logging
  //
  //summary:
  //
  //  Safe using of console object even in IE
  //
  //main object:
  //
  //  console
  //
  if (!window.console) {
    window.console = {
      log: function log() {//stub
      },
      debug: function debug() {//stub
      },
      error: function error() {//stub
      }
    };
  }
}();

module.exports = JRS.Log;

});