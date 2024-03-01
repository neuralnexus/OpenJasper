define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var _prototype = require('prototype');

var $A = _prototype.$A;

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
function RegExpRepresenter(expression) {
  this.charMap = {
    '\\s': ' '
  };
  this.expressionTokens = this.__parse(expression);
}

_.extend(RegExpRepresenter.prototype, {
  __parse: function __parse(expression) {
    var result = $A();

    for (var i = 1; i < expression.length - 1; i++) {
      var ch = expression.charAt(i);

      if (ch == '\\') {
        ch += expression.charAt(++i);
      }

      if (ch == "\\u") {
        ch += expression.substring(++i, i + 4);
        i += 3;
      }

      result.push(ch);
    }

    return result;
  },
  getCharacters: function getCharacters() {
    var result = $A();
    this.expressionTokens.each(function (token) {
      if (token.startsWith("\\u")) {
        /*eslint-disable-next-line no-eval*/
        result.push(eval('"' + token + '"'));
      } else if (token.startsWith('\\')) {
        var ch = this.charMap[token];
        result.push(ch ? ch : token.substring(1, token.length));
      } else {
        result.push(token);
      }
    }.bind(this));
    return result;
  },
  getRepresentedString: function getRepresentedString() {
    var result = '';
    var chars = this.getCharacters();
    var len = chars.length;

    for (var i = 0; i < len; i++) {
      var ch = chars[i];

      if (ch == ' ' || ch == '.' || ch == ',') {
        result += '[' + ch + ']';
      } else {
        result += ch;
      }

      if (i < len - 1) {
        result += ', ';
      }
    }

    return result;
  }
});

module.exports = RegExpRepresenter;

});