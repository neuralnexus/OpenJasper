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
var classUtil = {
  mixin: function mixin() {
    var arg,
        prop,
        arr = Array.prototype,
        to = arr.shift.call(arguments);

    for (arg = 0; arg < arguments.length; arg += 1) {
      if (!arguments[arg]) {
        continue;
      }

      for (prop in arguments[arg]) {
        if (arguments[arg].hasOwnProperty(prop)) {
          to[prop] = arguments[arg][prop];
        }
      }
    }

    return to;
  },
  inherit: function inherit(parent, obj) {
    var Subclass;

    if (obj && obj.hasOwnProperty('constructor')) {
      Subclass = obj.constructor;
    } else {
      Subclass = function Subclass() {
        return parent.apply(this, arguments);
      };
    }

    var Chain = function Chain() {};

    Chain.prototype = parent.prototype;
    Subclass.prototype = new Chain();

    if (obj) {
      _.extend(Subclass.prototype, obj);
    }

    Subclass.prototype.constructor = Subclass;
    Subclass.parent = parent.prototype;

    Subclass.extend = function (obj) {
      return classUtil.inherit(Subclass, obj);
    };

    return Subclass;
  },
  extend: function extend(obj) {
    return classUtil.inherit(function () {}, obj);
  }
};
module.exports = classUtil;

});