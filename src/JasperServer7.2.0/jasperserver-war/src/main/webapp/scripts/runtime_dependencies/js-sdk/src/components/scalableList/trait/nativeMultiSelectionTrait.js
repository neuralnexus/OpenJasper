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


/**
 * @author: Taras Bidyuk
 */

define(function(require) {

    var $ = require("jquery"),
        _ = require("underscore"),

        ScalableList = require("components/scalableList/view/ScalableList"),
        ListWithSelection = require("components/scalableList/view/ListWithSelection");

    return {

        initListeners: function() {
            ScalableList.prototype.initListeners.call(this);

            this.listenTo(this.model, "selection:clear", this.clearSelection, this);
            this.listenTo(this.model, "selection:add", this.selectValue, this);
            this.listenTo(this.model, "selection:addRange", this.selectRange, this);
            this.listenTo(this.model, "selection:remove", this.deselectValue, this);

            $("body").on("mouseup", this.onGlobalMouseup);
        },

        delegateEvents: function() {
            var res = ListWithSelection.prototype.delegateEvents.apply(this, arguments);

            this.$el.on("mouseup", this.eventListenerPattern, _.bind(this._onMouseup, this));

            return res;
        },

        onMousedblclick: function(event) {
            if ((this.selection.allowed.left || this.selection.allowed.right) && !this.getDisabled()) {
                var itemData = this._getDomItemData(event.currentTarget);
                this._singleSelect(event, itemData.value, itemData.index);

                if (this.selectionChanged) {
                    this._triggerSelectionChanged();
                    this._triggerDblclicked();
                    this.selectionChanged = false;
                }
            }
        },

        onMousemove: function() {},

        onGlobalMouseup: function() {
            this.selectionStarted = false;
        },

        // private methods

        _multiSelect: function(event, value, index) {
            this.selectionStarted = true;

            var model = this.model;

            if (event.shiftKey) {
                model.addRangeToSelection(value, index);
            } else if (event.ctrlKey || event.metaKey) {
                model.toggleSelection(value, index);
            } else {
                if(_.compact(model.getSelection()).length <= 1) {
                    this._singleSelect(event, value, index);
                    this._triggerSelectionChanged();
                }
            }
        },

        _onMouseup: function(event) {
            var itemData;

            if (this.selectionStarted) {
                itemData = this._getDomItemData(event.currentTarget);

                if(!event.ctrlKey && !event.shiftKey && !event.metaKey) {
                    this._singleSelect(event, itemData.value, itemData.index);
                }

                if (this.selectionChanged) {
                    this._triggerSelectionChanged();
                    this.selectionChanged = false;
                }
            }

            this.selectionStarted = false;
        }
    }
});
