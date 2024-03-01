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
var RelativeDate = function RelativeDate(keyword, sign, number) {
  this.setKeyword(keyword);
  this.setSign(sign);
  this.setNumber(number);
};

RelativeDate.prototype.setKeyword = function (keyword) {
  this.keyword = keyword.toUpperCase();
};

RelativeDate.prototype.setSign = function (sign) {
  this.sign = sign;
};

RelativeDate.prototype.setNumber = function (number) {
  if (_.isNumber(number)) {
    this.number = number;
  } else {
    var value = parseInt(number, 10);

    if (!isNaN(value)) {
      this.number = value;
    } else {
      this.number = 0;
    }
  }
};

RelativeDate.prototype.toString = function () {
  if (_.isNumber(this.number) && !isNaN(this.number) && this.number > 0 && (this.sign === '+' || this.sign === '-') && this.keyword in RelativeDate.PATTERNS) {
    return this.keyword + this.sign + this.number.toString();
  } else if (this.keyword in RelativeDate.PATTERNS) {
    return this.keyword;
  } else {
    return '';
  }
};

RelativeDate.PATTERNS = {
  DAY: /^(DAY)(([+|\-])(\d{1,9}))?$/i,
  WEEK: /^(WEEK)(([+|\-])(\d{1,9}))?$/i,
  MONTH: /^(MONTH)(([+|\-])(\d{1,9}))?$/i,
  QUARTER: /^(QUARTER)(([+|\-])(\d{1,9}))?$/i,
  SEMI: /^(SEMI)(([+|\-])(\d{1,9}))?$/i,
  YEAR: /^(YEAR)(([+|\-])(\d{1,9}))?$/i
};

RelativeDate.parse = function (relativeDateString) {
  if (RelativeDate.isValid(relativeDateString)) {
    for (var pattern in RelativeDate.PATTERNS) {
      var result = RelativeDate.PATTERNS[pattern].exec(relativeDateString);

      if (result !== null && _.isArray(result) && result.length === 5) {
        return new RelativeDate(result[1], result[3], result[4]);
      }
    }
  }

  return undefined;
};

RelativeDate.isValid = function (relativeDate) {
  if (relativeDate instanceof RelativeDate) {
    return relativeDate.toString() !== '';
  } else if (_.isString(relativeDate)) {
    for (var pattern in RelativeDate.PATTERNS) {
      if (RelativeDate.PATTERNS[pattern].test(relativeDate)) {
        return true;
      }
    }
  }

  return false;
};

module.exports = RelativeDate;

});