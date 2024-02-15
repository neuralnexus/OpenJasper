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
 * @version: $Id: AvailableItemsListWithTrueAll.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * AvailableItems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        _ = require("underscore"),
        availableItemsListDropdownTemplate = require("text!common/component/multiSelect/templates/availableItemsListDropdownWithTrueAllTemplate.htm"),
        AvailableItemsList = require("common/component/multiSelect/view/AvailableItemsList");

    var AvailableItemsListWithTrueAll = AvailableItemsList.extend({

        initialize: function(options) {
            _.bindAll(this, "onSetTrueAll");

            options = _.extend({
                dropDownTemplate: availableItemsListDropdownTemplate
            }, options);

            if (options.trueAll) {
                delete options.value;
            }

            AvailableItemsList.prototype.initialize.call(this, options);

            this.setTrueAll(options.trueAll);
        },

        initListeners: function() {
            AvailableItemsList.prototype.initListeners.call(this);

            this.listenTo(this.model, "change:isTrueAll", this.changeTrueAll, this);

            this.$dropDownEl.on("change", ".mSelect-footer input[type='checkbox']", this.onSetTrueAll);
        },

        /* Event Handlers */

        onSelectAll: function() {
            if (!this.model.get("disabled")) {
                var checkbox = this.$dropDownEl.find(".mSelect-footer input[type='checkbox']");
                checkbox.prop("checked", !checkbox.prop("checked"));
                this.model.set("isTrueAll", checkbox.is(":checked"));
            }
        },

        onSelectNone: function() {
            if (!this.model.get("isTrueAll")) {
                AvailableItemsList.prototype.onSelectNone.call(this);
            }
        },

        onInvertSelection: function() {
            if (!this.model.get("isTrueAll")) {
                AvailableItemsList.prototype.onInvertSelection.call(this);
            }
        },

        onSetTrueAll: function(event) {
            var value = $(event.target).is(":checked");
            this.model.set("isTrueAll", value);
        },

        changeTrueAll: function() {
            var isTrueAll = this.model.get("isTrueAll");

            var checkbox = this.$dropDownEl.find(".mSelect-footer input[type='checkbox']");
            if (isTrueAll !== checkbox.is(":checked")) {
                checkbox.prop("checked", isTrueAll);
            }

            if (isTrueAll) {
                this.$dropDownEl.find("a.none, a.invert").addClass("disabled");
                this.listView.activate(undefined);
                this.listView.reset({silent: true});

                var that = this;
                this.clearFilterForTrueAll(function() {
                    that.listView.once("selection:change", that.processSelectionThroughApi, that);
                    that.setTrueAllForListView(true);
                });
            } else {
                this.$dropDownEl.find("a.none, a.invert").removeClass("disabled");
                this.model.trigger("change:value");
                this.setTrueAllForListView(false);
            }
        },

        setTrueAllForListView: function(all) {
            if (all) {
                this.listView.selectAll();
            }

            this.listView.setDisabled(all);
        },

        clearFilterForTrueAll: function(callback) {
            if (this.model.get("criteria")) {
                this.$el.find("input").val("");
                this.model.set("criteria", "", {silent: true});

                this.changeFilter(callback);
            } else {
                callback && callback();
            }
        },

        changeDisabled: function() {
            var disabled = this.model.get("disabled");

            if (disabled) {
                this.$dropDownEl.find(".mSelect-footer input[type='checkbox']").attr("disabled", "disabled");
            } else {
                this.$dropDownEl.find(".mSelect-footer input[type='checkbox']").removeAttr("disabled");
            }

            AvailableItemsList.prototype.changeDisabled.call(this);
        },

        /* Internal methods */

        getModelForRendering: function() {
            return _.extend(AvailableItemsList.prototype.getModelForRendering.call(this), {
                isTrueAll: this.model.get("isTrueAll")
            });
        },

        /* API */

        setTrueAll: function(all) {
            this.model.set("isTrueAll", all);
        },

        getTrueAll: function() {
            return this.model.get("isTrueAll");
        },

        remove: function() {
            AvailableItemsList.prototype.remove.call(this);

            this.$dropDownEl.off("change", this.onSetTrueAll);
        }

    });

    return AvailableItemsListWithTrueAll;
});

