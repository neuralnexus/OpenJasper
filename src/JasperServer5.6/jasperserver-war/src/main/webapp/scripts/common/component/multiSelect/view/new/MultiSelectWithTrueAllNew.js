/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author Sergey Prilukin
 * @version: $Id: MultiSelectWithTrueAllNew.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * Selecteditems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        DelegatingDataProvider = require("common/component/multiSelect/dataprovider/DelegatingDataProvider"),
        CacheableDataProvider = require("common/component/singleSelect/dataprovider/CacheableDataProvider"),
        AvailableItemsListWithTrueAllNew = require("common/component/multiSelect/view/new/AvailableItemsListWithTrueAllNew"),
        MultiSelectNew = require("common/component/multiSelect/view/new/MultiSelectNew"),
        MultiSelectWithTrueAll = require("common/component/multiSelect/view/MultiSelectWithTrueAll");

    var MultiSelectWithTrueAllNew = MultiSelectWithTrueAll.extend({
        _createSelectedItemsList: MultiSelectNew.prototype._createSelectedItemsList,
        selectionRemoved: MultiSelectNew.prototype.selectionRemoved,
        _sortSelection: MultiSelectNew.prototype._sortSelection,
        _convertSelectionToStandardData: MultiSelectNew.prototype._convertSelectionToStandardData,
        getValue:  MultiSelectNew.prototype.getValue
    });

    MultiSelectWithTrueAllNew = MultiSelectWithTrueAllNew.extend({
        _createAvailableItemsList: function(options) {
            this.availableItemsListDataProvider = options.getData;

            return options.availableItemsList || new AvailableItemsListWithTrueAllNew({
                getData: options.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor,
                chunksTemplate: options.chunksTemplate,
                scrollTimeout: options.scrollTimeout,
                trueAll: options.trueAll
            });
        },

        _createSelectedItemsListDataProvider: function(options) {
            return new DelegatingDataProvider();
        },

        selectionChangeInternal: function(selection) {
            var all = this.getTrueAll();

            if (all) {
                this.selectedItemsDataProvider.setGetData(this.availableItemsListDataProvider);
                this.selectedItemsDataProvider.setData = null;

                var self = this;
                this.selectedItemsList.fetch(function() {
                    self.selectedItemsList.resize();
                    self.selectedItemsList.setDisabled(true);
                });

                if (!this.silent) {
                    this.triggerSelectionChange();
                } else {
                    delete this.silent;
                }
            } else {
                var dataProvider = new CacheableDataProvider();
                this.selectedItemsDataProvider.setGetData(dataProvider.getData);
                this.selectedItemsDataProvider.setData = _.bind(dataProvider.setData, dataProvider);
                MultiSelectNew.prototype.selectionChangeInternal.call(this, selection);
                this.selectedItemsList.setDisabled(false);
            }
        }
    });

    return MultiSelectWithTrueAllNew;
});

