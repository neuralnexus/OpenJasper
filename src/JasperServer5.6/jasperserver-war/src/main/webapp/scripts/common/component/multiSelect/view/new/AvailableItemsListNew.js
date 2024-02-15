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
 * @version: $Id: AvailableItemsListNew.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * AvailableItems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        AvailableItemsList = require("common/component/multiSelect/view/AvailableItemsList"),
        listWithNavigationModelTrait = require("common/component/list/model/listWithNavigationModelTrait"),
        ListWithSelectionAsObjectHashModel = require("common/component/list/model/ListWithSelectionAsObjectHashModel");

    var AvailableItemsListNew = AvailableItemsList.extend({

        _createListViewModel: function(options) {
            this.getData = options.getData;

            var Model = ListWithSelectionAsObjectHashModel.extend(listWithNavigationModelTrait);
            return options.listViewModel || new Model({
                getData: options.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor
            });
        },

        /* Event Handlers */

        selectionAdd: function(selection) {
            this.model.get("value")[selection.value] = true;
            this.model.trigger("change:value");
        },

        selectionAddRange: function(range) {
            var selection = range.selection,
                value = this.model.get("value");

            for (var i = 0; i < selection.length; i++) {
                value[selection[i]] = true;
            }

            this.model.trigger("change:value");
        },

        selectionRemove: function(selection) {
            delete this.model.get("value")[selection.value];
            this.model.trigger("change:value");
        },

        selectionClear: function() {
            this.model.attributes.value = {};
        },

        /* Key handlers for KeyboardManager */

        /* Internal helper methods */

        changeFilter: function(callback) {
            var self = this;
            this.listView.scrollTo(0);

            this.getData({
                criteria: this.model.get("criteria"),
                offset: 0,
                limit: 1
            }).done(
                callback && typeof callback === "function" ? callback : function() {
                    self.listView.fetch(function () {
                        self.listView.resize();
                        self.listView.setValue(self.model.get("value"), {silent: true});
                    });
                }
            );
        },

        processSelectionThroughApi: function(selection) {

            var value = {};

            this.selectionClear();

            for (var i = 0; i < selection.length; i++) {
                value[selection[i]] = true;
            }

            this.model.attributes.value = value;
            this.model.trigger("change:value");
        },


        convertSelectionForListViewModel: function(selection) {
            return selection;
        },

        /* Internal methods */
        //none

        /* API */

        getValue: function() {
            return _.keys(this.model.get("value"));
        },

        setValue: function(value, options) {
            options = options || {};

            this.listViewModel.once("selection:change", function() {
                this.selectionClear();
                var selection = this.listViewModel.getSelection();

                for (var i = 0; i < selection.length; i++) {
                    this.model.attributes.value[selection[i]] = true;
                }

                if (!options || !options.silent) {
                    this.changeValue();
                }
            }, this);


            if (!_.isArray(value) && !(typeof value === "string")) {
                value = this.convertSelectionForListViewModel(value);
            }

            this.listViewModel.select(value, options.modelOptions);
        }
    });

    return AvailableItemsListNew;
});

