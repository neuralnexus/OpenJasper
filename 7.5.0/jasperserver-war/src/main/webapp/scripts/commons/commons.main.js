define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var stdnav = require("runtime_dependencies/js-sdk/src/common/stdnav/stdnav");

var actionModel = require('../actionModel/actionModel.modelGenerator');

var primaryNavigation = require('../actionModel/actionModel.primaryNavigation');

var globalSearch = require('../repository/repository.search.globalSearchBoxInit');

var layoutModule = require('../core/core.layout');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var $ = require('jquery');

var stdnavPluginActionMenu = require('../stdnav/plugins/stdnavPluginActionMenu');

var stdnavPluginDynamicList = require('../stdnav/plugins/stdnavPluginDynamicList');

var stdnavPluginToolbar = require('../stdnav/plugins/stdnavPluginToolbar');

require('./commons.minimal.main');

require('../namespace/namespace');

require('../core/core.accessibility');

require('../core/core.events.bis');

require('../core/core.key.events');

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
  // add information about locale into body's class
  $('body').addClass('locale-' + jrsConfigs.userLocale);
  layoutModule.initialize();
  primaryNavigation.initializeNavigation(); //navigation setup
  //navigation setup

  actionModel.initializeOneTimeMenuHandlers(); //menu setup
  // JRS-specific stdnav plugins from jrs-ui
  //menu setup
  // JRS-specific stdnav plugins from jrs-ui

  stdnavPluginActionMenu.activate(stdnav);
  stdnavPluginDynamicList.activate(stdnav);
  stdnavPluginToolbar.activate(stdnav);
  jrsConfigs.initAdditionalUIComponents && globalSearch.initialize(); //isNotNullORUndefined(window.accessibilityModule) && accessibilityModule.initialize();
  //trigger protorype's dom onload manualy
  //isNotNullORUndefined(window.accessibilityModule) && accessibilityModule.initialize();
  //trigger protorype's dom onload manualy

  document.fire('dom:loaded');
});

});