define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var domReady = require('requirejs-domready');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var _controlsControlsBase = require('../controls/controls.base');

var baseControls = _controlsControlsBase.ControlsBase;

var _prototype = require('prototype');

var Hash = _prototype.Hash;

var _ = require('underscore');

var $ = require('jquery');

var layoutModule = require('../core/core.layout');

var Report = require('./report.view.runtime');

require('../components/components.toolbarButtons.events');

require('../controls/controls.report');

require('jquery-ui-touch-punch');

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
window.printRequest = function () {} // Leave empty. This function is invoked by Flash charts in report for managed print.
// Printing is not available in report view, only in dashboard view.
;

window.FC_Rendered = function (DOMId) {
  $('#' + DOMId).hide().show();
};

domReady(function () {
  if (window.Prototype) {
    delete Object.prototype.toJSON;
    delete Array.prototype.toJSON;
    delete Hash.prototype.toJSON;
    delete String.prototype.toJSON;
  }

  _.extend(baseControls, jrsConfigs.inputControlConstants);

  _.extend(Report, jrsConfigs.reportViewer.ReportSettings);

  if (Report.hasInputControls && (Report.reportControlsLayout === 2 || Report.reportControlsLayout === 4)) {
    layoutModule.resizeOnClient('inputControlsForm', 'reportViewFrame');
  }

  require(['ReportRequireJsConfig'], function () {
    //We can start load report viewer only when additional requirejs config is loaded
    //so check that additional config is for sure loaded
    require(['./viewer'], function (Viewer) {
      window.viewer = new Viewer({
        at: '#reportContainer',
        reporturi: Report.reportUnitURI,
        async: true,
        page: 0,
        contextPath: jrsConfigs.contextPath
      });
      window.jasperreports = {
        reportviewertoolbar: window.viewer
      };
      Report.initialize();
    }); //workaround for deprecated JIVE components
    //it fixes styling of jquery.ui.datepicker
    //workaround for deprecated JIVE components
    //it fixes styling of jquery.ui.datepicker


    var $body = $('body');

    if (!$body.hasClass('jr')) {
      $body.addClass('jr');
    }
  });
});

});