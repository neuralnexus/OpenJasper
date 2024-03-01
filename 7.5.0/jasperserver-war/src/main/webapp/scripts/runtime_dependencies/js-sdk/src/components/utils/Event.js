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
var EventClass = function EventClass(options) {
  options = options || {};
  this.name = options.name;
  this.data = options.data || {};
  this._isDefaultPrevented = false;
};

EventClass.prototype = Object.create({
  get name() {
    return this._name;
  },

  set name(value) {
    if (_.isString(value)) {
      if (value.length) {
        this._name = value;
      } else {
        throw new Error('\'name\' should\'t be an empty string');
      }
    } else {
      throw new TypeError('\'name\' must be a \'string\'');
    }
  },

  get data() {
    return this._data;
  },

  set data(value) {
    if (_.isObject(value)) {
      this._data = value;
    } else {
      throw new TypeError('\'data\' must be an \'object\'');
    }
  },

  isDefaultPrevented: function isDefaultPrevented() {
    return this._isDefaultPrevented;
  },
  preventDefault: function preventDefault() {
    this._isDefaultPrevented = true;
  }
});
module.exports = EventClass;

});