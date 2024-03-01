define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Validation = require('backbone-validation');

var _ = require('underscore');

var NumberUtils = require('../util/parse/NumberUtils');

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
var numberUtils = new NumberUtils();
var originalValidate = Validation.mixin.validate;

Validation.mixin.validate = function (attrs, options) {
  options || (options = {});
  var self = this;
  return originalValidate.call(this, attrs, _.extend({
    valid: function valid(view, attr) {
      self.trigger('validate:' + attr, self, attr);
    },
    invalid: function invalid(view, attr, error) {
      self.trigger('validate:' + attr, self, attr, error);
    }
  }, options));
};

_.extend(Validation.validators, {
  doesNotContainSymbols: function doesNotContainSymbols(value, attr, forbiddenSymbols) {
    if (new RegExp('[' + forbiddenSymbols + ']', 'g').test(value)) {
      return 'Attribute \'' + attr + '\' contains forbidden symbols';
    }
  },
  integerNumber: function integerNumber(value) {
    if (!numberUtils.isNumberInt(value)) {
      return 'Value is not a valid integer number';
    }
  },
  type: function type(value, attr, types) {
    function typeFn(type) {
      var fn;

      if ('string' == type) {
        fn = _.isString;
      } else if ('number' === type) {
        fn = _.isNumber;
      } else if ('object' === type) {
        fn = _.isObject;
      } else if ('boolean' === type) {
        fn = _.isBoolean;
      } else if ('null' === type) {
        fn = _.isNull;
      } else if ('undefined' === type) {
        fn = _.isUndefined;
      }

      return fn;
    }

    if (!_.isArray(types)) {
      types = [types];
    }

    var isValueOneOfTypes = types.some(function (type) {
      return typeFn(type)(value);
    });

    if (!isValueOneOfTypes) {
      return '\'{attr}\' is not {type}'.replace('{attr}', attr).replace('\'{type}\'', types.join(' '));
    }
  },
  url: function url(value) {
    if (!/(http|https):\/\/.*\..*./.test(value)) {
      return 'Value is not a valid url';
    }
  },
  hexColor: function hexColor(value) {
    if (!/^#[0-9a-f]{3,6}$/i.test(value)) {
      return 'Value is not a valid hex color';
    }
  },
  xRegExpPattern: function xRegExpPattern(value, attr, customValue, model) {
    if (!customValue.test(value)) {
      return 'Value does not match pattern';
    }
  },
  startsWithLetter: function startsWithLetter(value, attr, customValue, model) {
    if (!value.substr(0, 1).match(/[A-Za-z]/)) {
      return 'Value should start with letter';
    }
  },
  containsOnlyWordCharacters: function containsOnlyWordCharacters(value, attr, customValue, model) {
    if (value.search(/\W/) >= 0) {
      return 'Value should contain only word characters (letters, digits and underscore)';
    }
  },
  arrayMinLength: function arrayMinLength(value, attr, minLength, model) {
    if (_.isArray(value) && value.length < minLength) {
      return 'Array length is less than ' + minLength;
    }
  }
});

module.exports = Validation;

});