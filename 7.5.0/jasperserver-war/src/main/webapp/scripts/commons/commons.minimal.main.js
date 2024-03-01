define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var heartbeat = require('../components/components.heartbeat');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var aboutDialog = require('../components/components.about');

var webHelp = require('../components/components.webHelp');

var $ = require('jquery');

var stdnav = require("runtime_dependencies/js-sdk/src/common/stdnav/stdnav");

var stdnavPluginAnchor = require("runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginAnchor");

var stdnavPluginButton = require("runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginButton");

var stdnavPluginGrid = require("runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginGrid");

var stdnavPluginList = require("runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginList");

var stdnavPluginTable = require("runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginTable");

var stdnavPluginActionMenu = require('../stdnav/plugins/stdnavPluginActionMenu');

var stdnavPluginDynamicList = require('../stdnav/plugins/stdnavPluginDynamicList');

var stdnavPluginForms = require('../stdnav/plugins/stdnavPluginForms');

var stdnavPluginToolbar = require('../stdnav/plugins/stdnavPluginToolbar');

var stdnavPluginWorkflowCard = require("runtime_dependencies/js-sdk/src/common/stdnav/plugins/stdnavPluginWorkflowCard");

require('./commons.bare.main');

require('../config/dateAndTimeSettings');

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
// Basic stdnav plugins from js-sdk
domReady(function () {
  //Heartbeat
  heartbeat.initialize(jrsConfigs.heartbeatInitOptions);
  heartbeat.start();
  jrsConfigs.initAdditionalUIComponents && aboutDialog.initialize(); //Web help
  //Web help

  var helpLink = $('#helpLink');

  if (helpLink) {
    helpLink.on('click', function (e) {
      e.preventDefault();
      webHelp.displayWebHelp();
    });
  }

  if (jrsConfigs.enableAccessibility === 'true') {
    // Basic stdnav plugins from js-sdk
    stdnav.activate();
    stdnavPluginAnchor.activate(stdnav);
    stdnavPluginButton.activate(stdnav);
    stdnavPluginForms.activate(stdnav);
    stdnavPluginGrid.activate(stdnav);
    stdnavPluginList.activate(stdnav);
    stdnavPluginTable.activate(stdnav); // JasperServer-specific stdnav plugins from jrs-ui
    // JasperServer-specific stdnav plugins from jrs-ui

    stdnavPluginActionMenu.activate(stdnav);
    stdnavPluginDynamicList.activate(stdnav);
    stdnavPluginToolbar.activate(stdnav);
    stdnavPluginWorkflowCard.activate(stdnav);
    stdnav.start();
  }
});

});