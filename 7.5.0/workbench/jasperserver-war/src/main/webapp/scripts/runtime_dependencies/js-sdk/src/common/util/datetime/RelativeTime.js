define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var RelativeDate = require('./RelativeDate');

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
var RelativeTime = function RelativeTime() {
  RelativeDate.apply(this, arguments);
};

var F = function F() {};

F.prototype = RelativeDate.prototype;
RelativeTime.prototype = new F();
RelativeTime.prototype.constructor = RelativeTime;
RelativeTime.PATTERNS = {
  MINUTE: /^(MINUTE)(([+|\-])(\d{1,9}))?$/i,
  HOUR: /^(HOUR)(([+|\-])(\d{1,9}))?$/i
};

RelativeTime.parse = function (relativeTimeString) {
  if (RelativeTime.isValid(relativeTimeString)) {
    for (var pattern in RelativeTime.PATTERNS) {
      var result = RelativeTime.PATTERNS[pattern].exec(relativeTimeString);

      if (result !== null && _.isArray(result) && result.length === 5) {
        return new RelativeTime(result[1], result[3], result[4]);
      }
    }
  }

  return undefined;
};

RelativeTime.isValid = function (relativeTime) {
  if (relativeTime instanceof RelativeTime) {
    return relativeTime.toString() !== '';
  } else if (_.isString(relativeTime)) {
    for (var pattern in RelativeTime.PATTERNS) {
      if (RelativeTime.PATTERNS[pattern].test(relativeTime)) {
        return true;
      }
    }
  }

  return false;
};

module.exports = RelativeTime;

});