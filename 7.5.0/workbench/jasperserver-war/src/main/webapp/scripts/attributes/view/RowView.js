define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!AttributesBundle");

var i18n2 = require("bundle!CommonBundle");

var BaseRow = require("runtime_dependencies/js-sdk/src/common/component/baseTable/childView/BaseRow");

var attributesTypesEnum = require('../../attributes/enum/attributesTypesEnum');

var rowTemplatesFactory = require('../../attributes/factory/rowTemplatesFactory');

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
var SECURE_VALUE_SUBSTITUTION = '~secure~';
var RowView = BaseRow.extend({
  className: 'table-row',
  template: _.template(rowTemplatesFactory({
    readOnly: true
  })),
  templateHelpers: function templateHelpers() {
    return {
      i18n: i18n,
      i18n2: i18n2,
      type: this.type,
      types: attributesTypesEnum,
      encrypted: SECURE_VALUE_SUBSTITUTION
    };
  },
  computeds: {
    encrypt: {
      get: function get() {
        return SECURE_VALUE_SUBSTITUTION;
      },
      set: function set(value) {
        this.setBinding('value', value);
      }
    }
  },
  initialize: function initialize(options) {
    this.type = options.type;
    BaseRow.prototype.initialize.apply(this, arguments);
  }
});
module.exports = RowView;

});