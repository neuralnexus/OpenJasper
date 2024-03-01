define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var DelegatingDataProvider = require('../dataprovider/DelegatingDataProvider');

var SelectedItemsDataProvider = require('../dataprovider/SelectedItemsDataProvider');

var AvailableItemsListWithTrueAll = require('../view/AvailableItemsListWithTrueAll');

var MultiSelect = require('../view/MultiSelect');

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
module.exports = MultiSelect.extend({
  _createAvailableItemsList: function _createAvailableItemsList(options) {
    this.availableItemsListDataProvider = options.getData;
    return options.availableItemsList || new AvailableItemsListWithTrueAll({
      model: this.availableItemsListModel,
      getData: options.getData,
      bufferSize: options.bufferSize,
      loadFactor: options.loadFactor,
      chunksTemplate: options.chunksTemplate,
      scrollTimeout: options.scrollTimeout,
      trueAll: options.trueAll
    });
  },
  _createSelectedItemsListDataProvider: function _createSelectedItemsListDataProvider(options) {
    this.selectedListOptions = options ? options.selectedListOptions : {};
    this.selectedItemsDataProviderInstance = options ? options.selectedItemsDataProvider : null;
    return new DelegatingDataProvider();
  },
  selectionRemoved: function selectionRemoved(selection) {
    if (this.getTrueAll()) {
      var selectionAsHash;

      for (var index in selection) {
        if (selection.hasOwnProperty(index)) {
          selectionAsHash = {
            value: selection[index],
            index: index
          };
          break;
        }
      }

      this.availableItemsList.selectionAdd(selectionAsHash);
    } else {
      MultiSelect.prototype.selectionRemoved.call(this, selection);
    }
  },
  selectionChangeInternal: function selectionChangeInternal(selection) {
    var all = this.getTrueAll();

    if (all) {
      this.selectedItemsDataProvider.setGetData(this.availableItemsListDataProvider);
      this.selectedItemsDataProvider.setData = null;
      var self = this;
      this.selectedItemsList.fetch(function () {
        self._updateSelectedItemsCountLabel();

        self.selectedItemsList.resize();
      });

      if (!this.silent) {
        this.triggerSelectionChange();
      } else {
        delete this.silent;
      }
    } else {
      var dataProvider = this._getSelectedItemsDataProviderInstance();

      this.selectedItemsDataProvider.setGetData(dataProvider.getData);
      this.selectedItemsDataProvider.setData = _.bind(dataProvider.setData, dataProvider);
      this.selectedItemsDataProvider.getAllCachedData = _.bind(dataProvider.getAllCachedData, dataProvider);
      MultiSelect.prototype.selectionChangeInternal.call(this, selection);
    }
  },
  triggerSelectionChange: function triggerSelectionChange() {
    this.trigger('selection:change', this.getValue(), {
      isTrueAll: this.getTrueAll()
    });
  },
  setTrueAll: function setTrueAll(all, options) {
    options = options || {};

    if (options.silent) {
      this.silent = true;
    }

    delete options.silent;
    this.availableItemsList.setTrueAll(all, options);
  },
  getTrueAll: function getTrueAll() {
    return this.availableItemsList.getTrueAll();
  },
  _getSelectedItemsDataProviderInstance: function _getSelectedItemsDataProviderInstance() {
    var instance = this.selectedItemsDataProviderInstance || new SelectedItemsDataProvider(this.selectedListOptions);
    instance.setData([]);
    return instance;
  }
});

});