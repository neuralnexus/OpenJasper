define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var classUtil = require('../../util/classUtil');

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
var $dimmer;
var counter;
module.exports = classUtil.extend({
  constructor: function constructor(options) {
    if (!$dimmer) {
      counter = 0;
      $dimmer = $('<div id=\'dialogDimmer\' class=\'dimmer\'></div>').css(options);
      $(document.body).append($dimmer);
      $dimmer.hide();
    }

    counter++;
  },
  css: function css(options) {
    $dimmer.css(options);
    return this;
  },
  show: function show() {
    var dimmerCount = this.getCount() || 0;
    this.setCount(++dimmerCount);
    $dimmer.show();
    return this;
  },
  hide: function hide() {
    if (this.isVisible()) {
      var dimmerCount = this.getCount();
      this.setCount(--dimmerCount);
      !dimmerCount && $dimmer.hide();
      return this;
    }
  },
  setCount: function setCount(value) {
    $dimmer.data({
      'count': value
    });
  },
  getCount: function getCount() {
    return parseInt($dimmer.data('count'), 10);
  },
  isVisible: function isVisible() {
    return $dimmer.is(':visible');
  },
  remove: function remove() {
    if (this._removed) {
      return;
    }

    this._removed = true;

    if (!$dimmer) {
      return;
    }

    counter--;

    if (!counter) {
      $dimmer.remove();
      $dimmer = null;
    }
  }
});

});