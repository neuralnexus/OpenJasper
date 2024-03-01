define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var toolbarButtonModule = require('../../components/components.toolbarButtons');

var layoutModule = require('../../core/core.layout');

var _utilUtilsCommon = require("../../util/utils.common");

var matchAny = _utilUtilsCommon.matchAny;

var xssUtil = require("runtime_dependencies/js-sdk/src/common/util/xssUtil");

var _prototype = require('prototype');

var $ = _prototype.$;

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
 * @version: $Id$
 */
var messageDetailModule = {
  _flowExecutionKey: null,
  _message: null,
  toolbar: {
    _buttons: null,
    _id: 'toolbar',
    initialize: function initialize() {
      toolbarButtonModule.initialize({});
      this._buttons = document.body.select(layoutModule.TOOLBAR_CAPSULE_PATTERN);

      this._initEventHandlers();
    },
    refresh: function refresh() {
      this._buttons.each(function (button) {
        toolbarButtonModule.setButtonState(button, true);
      }.bind(this));
    },
    _initEventHandlers: function _initEventHandlers() {
      $(this._id).observe('click', function (e) {
        var button = matchAny(e.element(), [layoutModule.BUTTON_PATTERN], true);
        document.location = 'flow.html?_flowExecutionKey=' + messageDetailModule._flowExecutionKey + '&_eventId=' + button.identify();
      }.bindAsEventListener(this));
    }
  },
  initialize: function initialize(options) {
    this._flowExecutionKey = options.flowExecutionKey;
    this._message = options.message;

    this._process();

    this.toolbar.initialize();
  },
  _process: function _process() {
    $('subject').update(xssUtil.hardEscape(this._message.subject));
    $('date').update(xssUtil.hardEscape(this._message.date));
    $('component').update(xssUtil.hardEscape(this._message.component));
    $('message').update(xssUtil.hardEscape(this._message.message));
  }
};

if (typeof require === 'undefined') {
  document.observe('dom:loaded', function () {
    messageDetailModule.initialize(window.localContext.initOptions);
  });
}

module.exports = messageDetailModule;

});