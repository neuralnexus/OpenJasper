define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var BaseListWithSelectionModel = require('./BaseListWithSelectionModel');

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
var ListWithSelectionAsObjectHashModel = BaseListWithSelectionModel.extend({
  _addToSelection: function _addToSelection(value, index) {
    this.selection[value] = true;
  },
  _removeFromSelection: function _removeFromSelection(value, index) {
    delete this.selection[value];
  },
  _clearSelection: function _clearSelection() {
    this.selection = {};
  },
  _selectionContains: function _selectionContains(value, index) {
    return this.selection[value];
  },
  _getSelection: function _getSelection() {
    return _.keys(this.selection);
  },
  select: function select(selection, options) {
    this._clearSelection();

    if (typeof selection === 'string') {
      this._addToSelection(selection);
    } else if (_.isArray(selection)) {
      for (var i = 0; i < selection.length; i++) {
        this._addToSelection(selection[i]);
      }
    } else if (typeof selection !== 'undefined') {
      for (var key in selection) {
        if (selection.hasOwnProperty(key)) {
          var value = selection[key];

          if (value !== undefined) {
            this._addToSelection(value, key);
          }
        }
      }
    }

    this._afterSelect && this._afterSelect(selection, options);

    this._triggerSelectionChange(options);
  }
});
module.exports = ListWithSelectionAsObjectHashModel;

});