define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var _ = require('underscore');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var resource = require('../../resource/resource.base');

var resourceQuery = require('../../resource/resource.query');

var jQuery = require('jquery');

var _utilUtilsCommon = require("../../util/utils.common");

var isIPad = _utilUtilsCommon.isIPad;

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
  var options = jrsConfigs.addQuery.localContext.initOptions;

  _.extend(window.localContext, jrsConfigs.addQuery.localContext);

  _.extend(resource.messages, jrsConfigs.addQuery.resource.messages);

  resourceQuery.initialize(options);

  if (jrsConfigs.addQuery.buttonFlowControls) {
    jQuery('#steps1_2').on('click', function () {
      return resourceQuery.jumpTo('reportNaming');
    });
    jQuery('#step3').on('click', function () {
      return resourceQuery.jumpTo('resources');
    });
    jQuery('#step4').on('click', function () {
      return resourceQuery.jumpTo('dataSource');
    });
    jQuery('#step5').on('click', function () {
      return resourceQuery.jumpTo('query');
    });
    jQuery('#step6').on('click', function () {
      return resourceQuery.jumpTo('customization');
    });
  }

  isIPad() && resource.initSwipeScroll();
});

});