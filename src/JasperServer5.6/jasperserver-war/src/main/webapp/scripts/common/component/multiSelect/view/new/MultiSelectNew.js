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
 * @version: $Id: MultiSelectNew.js 48370 2014-08-19 09:03:48Z sergey.prilukin $
 */

/**
 * Selecteditems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var AvailableItemsListNew = require("common/component/multiSelect/view/new/AvailableItemsListNew"),
        SelectedItemsList = require("common/component/multiSelect/view/SelectedItemsList"),
        CacheableDataProvider = require("common/component/singleSelect/dataprovider/CacheableDataProvider"),
        MultiSelect = require("common/component/multiSelect/view/MultiSelect");

    var MultiSelectNew = MultiSelect.extend({

        _createAvailableItemsList: function(options) {
            return options.availableItemsList || new AvailableItemsListNew({
                getData: options.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor,
                chunksTemplate: options.chunksTemplate,
                scrollTimeout: options.scrollTimeout
            });
        },

        _createSelectedItemsListDataProvider: function(options) {
            return new CacheableDataProvider();
        },

        _createSelectedItemsList: function(options) {
            this.formatValue = options.formatValue;

            return new SelectedItemsList({
                getData: this.selectedItemsDataProvider.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor,
                chunksTemplate: options.chunksTemplate,
                scrollTimeout: options.scrollTimeout
            });
        },

        /* Event Handlers */

        selectionRemoved: function (selection) {
            var selection = _.compact(selection),
                //for performance reasons we broke encapsulation here
                //and get raw selection.
                //we even did not make copy of it, since it will be immediately reset
                currentRawSelection = this.availableItemsList.model.get("value");

            for (var i = 0; i < selection.length; i++) {
                delete currentRawSelection[selection[i]];
            }

            this.availableItemsList.setValue(_.keys(currentRawSelection));
        },

        /* Internal helper methods */
        selectionChangeInternal: function(selection) {
            var self = this;

            selection = this._sortSelection(selection);
            selection = this._convertSelectionToStandardData(selection);
            this.selectedItemsDataProvider.setData(selection);
            this.selectedItemsList.fetch(function() {
                self.selectedItemsList.resize();
            });

            if (!this.silent) {
                this.triggerSelectionChange();
            } else {
                delete this.silent;
            }
        },

        _sortSelection: function(selection) {
            return _.sortBy(selection, function(el) {
                var value = el.toUpperCase();
                //TODO: need to use settings service to get null label and null value
                if (value === "[NULL]" || value == "~NULL~") {
                    return ""; //Move nulls to the start of the list
                } else {
                    return el;
                }
            })
        },

        _convertSelectionToStandardData: function(selection) {
            var self = this;

            return _.map(selection, function(value) {
                return {
                    value: value,
                    label: self.formatValue && self.formatValue(value)
                }
            })
        },

        /* API */

        getValue: function() {
            return this.availableItemsList.getValue();
        }

    });

    return MultiSelectNew;
});

