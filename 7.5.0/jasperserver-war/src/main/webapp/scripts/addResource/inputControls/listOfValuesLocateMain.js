define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var _ = require('underscore');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var resource = require('../../resource/resource.base');

var _utilUtilsCommon = require('../../util/utils.common');

var isIPad = _utilUtilsCommon.isIPad;

var resourceListOfValuesLocate = require('../../resource/resource.listOfValues.locate');

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
domReady(function () {
  _.extend(resource.messages, jrsConfigs.resourceListOfValuesLocate.messages);

  resourceListOfValuesLocate.initialize();
  isIPad() && resource.initSwipeScroll();
});

});