define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var cloneDeep = require('../util/cloneDeep');

var xssUtil = require('../util/xssUtil');

var underscoreString = require('underscore.string');

var jrsConfigs = require('../../jrs.configs');

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
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
var jsNonceTag = RegExp(/(<js-templateNonce>\s*<\/js-templateNonce>)|(<js-templateNonce\s*\/>)/gi);
_.str = underscoreString; //provide default templates

_.templateSettings = {
  evaluate: /\{\{([\s\S]+?)\}\}/g,
  interpolate: /\{\{=([\s\S]+?)\}\}/g,
  escape: /\{\{-([\s\S]+?)\}\}/g
}; // Mix in only non-conflict functions to Underscore namespace

var strExports = _.str.exports();

var conflictedStrFunctions = ["wrap"];

_.mixin(_.omit(strExports, conflictedStrFunctions));

var originalTemplate = _.template,
    originalIsEqual = _.isEqual;

_.mixin({
  xssSoftEscape: xssUtil.softHtmlEscape,
  xssHardEscape: xssUtil.hardEscape,
  template: function template(text, data, options) {
    var settings = _.templateSettings,
        reNoMatch = /($^)/;
    text = String(text || '');
    options = _.defaults({}, options, settings);
    var reDelimiters = RegExp((options.escape || reNoMatch).source + '|$', 'g');
    text = text.replace(reDelimiters, '{{ print(_.xssHardEscape($1)); }}');
    text = text.replace(jsNonceTag, "<!-- " + jrsConfigs.xssNonce + " (htm xss nonce) -->");
    var tmplFunction = originalTemplate.call(_, text, options);

    if (data) {
      return tmplFunction(data);
    }

    return tmplFunction;
  },
  isEqual: function isEqual(a, b) {
    // we need to override isEquals, because it doesn't support dom or jquery elements
    // generally it's a bad idea to check if dom objects are equal, but we have them set into a
    // backbone model which uses isEquals to produce changed attributes property
    if (_.isElement(a) && _.isElement(b) || a instanceof $ && b instanceof $) {
      return a === b;
    }

    return originalIsEqual.apply(_, arguments);
  },
  cloneDeep: cloneDeep
});

module.exports = _;

});