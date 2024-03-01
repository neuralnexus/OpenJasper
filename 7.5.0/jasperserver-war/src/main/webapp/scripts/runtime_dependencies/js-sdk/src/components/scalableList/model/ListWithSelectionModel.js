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
var ListWithSelectionModel = BaseListWithSelectionModel.extend({
  _addToSelection: function _addToSelection(value, index) {
    this.selection[index] = value;
  },
  _removeFromSelection: function _removeFromSelection(value, index) {
    this.selection[index] = undefined;
  },
  _clearSelection: function _clearSelection() {
    this.selection = [];
  },
  _selectionContains: function _selectionContains(value, index) {
    return this.selection[index] === value;
  },
  _getSelection: function _getSelection() {
    return this.selection;
  },
  _isSelectionEmpty: function _isSelectionEmpty(selection) {
    if (!selection) {
      return true;
    } else if (_.isArray(selection) && selection.length === 0) {
      return true;
    } else if (_.isArray(selection) || typeof selection === 'string') {
      return false;
    }

    for (var index in selection) {
      if (selection.hasOwnProperty(index) && selection[index] !== undefined) {
        return false;
      }
    }

    return true;
  },
  _selectPendingSelection: function _selectPendingSelection(pendingSelection, options) {
    if (this.get('bufferStartIndex') === 0 && this.get('bufferEndIndex') == this.get('total') - 1) {
      this._convertInitialArrayToSelection(this.get('items'), pendingSelection, options);
    } else {
      var that = this;
      this.getData().done(function (values) {
        that._convertInitialArrayToSelection(values.data, pendingSelection, options);
      }).fail(this.fetchFailed);
    }
  },
  _convertInitialArrayToSelection: function _convertInitialArrayToSelection(data, pendingSelection, options) {
    for (var i = 0; i < data.length; i++) {
      if (pendingSelection[data[i].value]) {
        this._addToSelection(data[i].value, i);
      }
    }

    this._triggerSelectionChange(options);
  },
  _triggerSelectionChange: function _triggerSelectionChange(options) {
    this._calcSelectionStartIndex && this._calcSelectionStartIndex();

    BaseListWithSelectionModel.prototype._triggerSelectionChange.call(this, options);
  },
  _calcSelectionStartIndex: function _calcSelectionStartIndex() {
    if (typeof this.selectionStartIndex !== 'undefined') {
      return;
    }

    for (var index in this.selection) {
      if (this.selection.hasOwnProperty(index) && this.selection[index] !== undefined) {
        this.selectionStartIndex = parseInt(index, 10);
        break;
      }
    }
  },
  select: function select(selection, options) {
    this._clearSelection();

    if (this._isSelectionEmpty(selection)) {
      this._triggerSelectionChange(options);

      return;
    }

    if (_.isArray(selection) || typeof selection === 'string') {
      var pendingSelection = {};

      if (typeof selection === 'string') {
        pendingSelection[selection] = true;
      } else {
        for (var i = 0; i < selection.length; i++) {
          pendingSelection[selection[i]] = true;
        }
      }

      this._selectPendingSelection(pendingSelection, options);
    } else {
      for (var index in selection) {
        if (selection.hasOwnProperty(index)) {
          var value = selection[index];

          if (value !== undefined) {
            this._addToSelection(value, index);
          }
        }
      }

      this._triggerSelectionChange(options);
    }
  }
});
module.exports = ListWithSelectionModel;

});