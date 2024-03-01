define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var CacheableDataProvider = require('../../singleSelect/dataprovider/CacheableDataProvider');

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
var SelectedItemsDataProvider = function SelectedItemsDataProvider(options) {
  options = options || {};

  _.defaults(options, {
    sortFunc: this._sortFunc,
    formatLabel: this._formatLabel
  });

  this.sortFunc = options.sortFunc;
  this.formatLabel = options.formatLabel;
  CacheableDataProvider.call(this, options.data);
};

_.extend(SelectedItemsDataProvider.prototype, CacheableDataProvider.prototype);

_.extend(SelectedItemsDataProvider.prototype, {
  setData: function setData(data) {
    data = this._convertToStandardData(data);
    data = this._sortData(data);
    CacheableDataProvider.prototype.setData.call(this, data);
  },
  _sortFunc: function _sortFunc(item1, item2) {
    return item1.label.localeCompare(item2.label);
  },
  _formatLabel: function _formatLabel(value) {
    return value;
  },
  _sortData: function _sortData(data) {
    data = data || [];
    return data.sort(this.sortFunc);
  },
  _convertToStandardData: function _convertToStandardData(data) {
    var self = this;
    return _.map(data, function (value) {
      return {
        value: value,
        label: self.formatLabel(value)
      };
    });
  }
});

module.exports = SelectedItemsDataProvider;

});