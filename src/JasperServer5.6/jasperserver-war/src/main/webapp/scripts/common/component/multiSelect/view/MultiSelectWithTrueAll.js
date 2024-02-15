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
 * @version: $Id: MultiSelectWithTrueAll.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * Selecteditems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        AvailableItemsListWithTrueAll = require("common/component/multiSelect/view/AvailableItemsListWithTrueAll"),
        MultiSelect = require("common/component/multiSelect/view/MultiSelect");

    var MultiSelectWithTrueAll = MultiSelect.extend({

        initialize: function(options) {
            if (options.trueAll) {
                delete options.value;
            }

            MultiSelect.prototype.initialize.call(this, options);
        },

        _createAvailableItemsList: function(options) {
            return options.availableItemsList || new AvailableItemsListWithTrueAll({
                getData: options.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor,
                chunksTemplate: options.chunksTemplate,
                scrollTimeout: options.scrollTimeout,
                trueAll: options.trueAll
            });
        },

        /* Event Handlers */

        /* Internal helper methods */

        selectionChangeInternal: function(selection) {
            this.selectedItemsList.setDisabled(this.getTrueAll());

            MultiSelect.prototype.selectionChangeInternal.call(this, selection);
        },

        triggerSelectionChange: function() {
            this.trigger("selection:change", this.getValue(), {isTrueAll: this.getTrueAll()});
        },

        /* API */

        getValue: function() {
            return this.getTrueAll() ? [] : this.availableItemsList.getValue();
        },

        setTrueAll: function(all, options) {
            options = options || {};

            if (options.silent) {
                this.silent = true;
            }

            delete options.silent;
            this.availableItemsList.setTrueAll(all, options);
        },

        getTrueAll: function() {
            return this.availableItemsList.getTrueAll();
        }
    });

    //Support for non-AMD modules
    window.MultiSelectWithTrueAll = MultiSelectWithTrueAll;

    return MultiSelectWithTrueAll;
});

