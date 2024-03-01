define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var browserDetection = require('./browserDetection');

var $ = require('jquery');

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
module.exports = {
  mouseEventOptions: {
    bubbles: true,
    cancelable: true,
    view: document.defaultView,
    detail: 0,
    screenX: 0,
    screenY: 0,
    clientX: 0,
    clientY: 0,
    ctrlKey: false,
    altKey: false,
    shiftKey: false,
    metaKey: false,
    button: 0,
    relatedTarget: null,
    srcElement: null
  },
  triggerNativeEvent: function triggerNativeEvent(eventName, target, options) {
    var ie = browserDetection.isIE(),
        ev;
    target = target || (ie ? document.documentElement : window);

    if (document.createEvent) {
      var opts;

      switch (eventName) {
        case 'click':
        case 'doubleclick':
        case 'mousedown':
        case 'mousemove':
        case 'mouseout':
        case 'mouseover':
        case 'mouseup':
          opts = $.extend({}, this.mouseEventOptions, options);
          opts.srcElement = target;

          if (typeof MouseEvent === 'function') {
            ev = new MouseEvent(eventName, opts);
          } else {
            ev = document.createEvent('MouseEvents');
            ev.initMouseEvent(eventName, opts.bubbles, opts.cancelable, opts.view, opts.detail, opts.screenX, opts.screenY, opts.clientX, opts.clientY, opts.ctrlKey, opts.altKey, opts.shiftKey, opts.metaKey, opts.button, opts.relatedTarget);
          }

          break;

        default:
          opts = $.extend({}, this.eventOptions, options);
          opts.srcElement = target;
          ev = document.createEvent('HTMLEvents');
          ev.initEvent(eventName, opts.bubble, opts.cancelable);
      }

      target.dispatchEvent(ev);
    } else {
      ev = document.createEventObject();
      ev.srcElement = target;

      if (eventName == 'click' && typeof target.click !== 'undefined') {
        target.click();
      } else {
        target.fireEvent('on' + eventName, ev);
      }
    }
  },
  simulateClickSequence: function simulateClickSequence(element) {
    this.triggerNativeEvent('mousedown', element);
    this.triggerNativeEvent('mouseup', element);
    this.triggerNativeEvent('click', element);
  },
  simulateDoubleClickSequence: function simulateDoubleClickSequence(element) {
    this.triggerNativeEvent('mousedown', element);
    this.triggerNativeEvent('mouseup', element);
    this.triggerNativeEvent('click', element);
    this.triggerNativeEvent('mousedown', element);
    this.triggerNativeEvent('mouseup', element);
    this.triggerNativeEvent('click', element);
    this.triggerNativeEvent('dblclick', element);
  }
};

});