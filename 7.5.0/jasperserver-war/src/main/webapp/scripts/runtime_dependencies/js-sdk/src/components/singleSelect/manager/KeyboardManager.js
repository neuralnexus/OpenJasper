define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

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
var DEFAULT_KEY_DOWN_TIMEOUT = 200;

var KeyboardManager = function KeyboardManager(options) {
  this.initialize(options);
  return this;
};

_.extend(KeyboardManager.prototype, {
  systemKeyCodes: [16, 17, 18, 91],
  keydownHandlers: {
    '38': 'onUpKey',
    '40': 'onDownKey',
    '13': 'onEnterKey',
    '27': 'onEscKey',
    '36': 'onHomeKey',
    '35': 'onEndKey',
    '9': 'onTabKey',
    '33': 'onPageUpKey',
    '34': 'onPageDownKey'
  },
  initialize: function initialize(options) {
    _.bindAll(this, 'onKeydown');

    if (options.keydownHandlers) {
      this.keydownHandlers = options.keydownHandlers;
    }

    this.stopPropagation = options.stopPropagation;
    this.keydownTimeout = options.keydownTimeout || DEFAULT_KEY_DOWN_TIMEOUT;
    this.context = options.context;
    this.deferredKeydownHandler = options.deferredKeydownHandler;
    this.immediateHandleCondition = options.immediateHandleCondition;
    this.immediateKeydownHandler = options.immediateKeydownHandler;
  },
  onKeydown: function onKeydown(event) {
    if (this._isSystemKeyPressed(event)) {
      return;
    } else if (this._canImmediatelyHandleKeyboardEvent(event)) {
      this._immediatelyHandleKeyboardEvent(event);
    } else {
      this.deferredHandleKeyboardEvent(event);
    }

    if (this.stopPropagation) {
      event.stopPropagation();
    }
  },
  deferredHandleKeyboardEvent: function deferredHandleKeyboardEvent(event) {
    if (this.deferredKeydownHandler) {
      if (this.keydownTimeout > 0) {
        clearTimeout(this.deferredTimeout);
        this.deferredTimeout = setTimeout(_.bind(this.deferredKeydownHandler, this.context, event), this.keydownTimeout);
      } else {
        this.deferredKeydownHandler.call(this.context, event);
      }
    }
  },
  _isSystemKeyPressed: function _isSystemKeyPressed(event) {
    return _.indexOf(this.systemKeyCodes, event.which) > -1;
  },
  _immediateHandleCondition: function _immediateHandleCondition(event) {
    return this.immediateHandleCondition && this.immediateHandleCondition.call(this.context, event);
  },
  _canImmediatelyHandleKeyboardEvent: function _canImmediatelyHandleKeyboardEvent(event) {
    var keydownHandler = this.keydownHandlers['' + event.which];
    return keydownHandler && typeof this.context[keydownHandler] === 'function' || this._immediateHandleCondition(event);
  },
  _immediatelyHandleKeyboardEvent: function _immediatelyHandleKeyboardEvent(event) {
    var keydownHandler = this.keydownHandlers['' + event.which];

    if (keydownHandler && typeof this.context[keydownHandler] === 'function') {
      this.context[keydownHandler].call(this.context, event);
    } else if (this._immediateHandleCondition(event)) {
      this.immediateKeydownHandler && this.immediateKeydownHandler.call(this.context, event);
    }
  }
});

module.exports = KeyboardManager;

});