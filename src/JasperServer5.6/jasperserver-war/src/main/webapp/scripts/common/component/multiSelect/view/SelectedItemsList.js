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
 * @version: $Id: SelectedItemsList.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * Selecteditems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        KeyboardManager = require("common/component/singleSelect/manager/KeyboardManager"),
        ListWithNavigation = require("common/component/multiSelect/view/ListViewForSelectedItemsList"),
        ListWithSelectionModel = require("common/component/list/model/ListWithSelectionModel"),
        listWithNavigationModelTrait = require("common/component/list/model/listWithNavigationModelTrait"),
        selectedItemsListTemplate = require("text!common/component/multiSelect/templates/selectedItemsListTemplate.htm"),
        itemsTemplate = require("text!common/component/multiSelect/templates/selectedItemsTemplate.htm"),
        listTemplate = require("text!common/component/multiSelect/templates/listTemplate.htm");

    var SelectedItemsList = Backbone.View.extend({

        attributes: {
            "class": "svList"
        },

        events: function() {
            return {
                "keydown input": this.keyboardManager.onKeydown,
                "focus input": "onFocus",
                "blur input": "onBlur",
                "mouseup li .mSelect-svList-button": "onMouseupOnDeleteButton",
                "mousedown": "onMousedown",
                "mouseup": "onMouseup"
            };
        },

        keydownHandlers: _.extend({
            "65": "onAKey",
            "8": "onDeleteKey",
            "46": "onDeleteKey"
        }, KeyboardManager.prototype.keydownHandlers),

        initialize: function(options) {
            _.bindAll(this, "onGlobalMouseup", "onGlobalMousedown", "onGlobalMousemove");

            if (!this.model) {
                this.model = new Backbone.Model();
            }

            this.model.set("focused", false, {silent: true});

            this.template = _.template(selectedItemsListTemplate);

            this.keyboardManager = new KeyboardManager({
                keydownHandlers: this.keydownHandlers,
                keydownTimeout: options.keydownTimeout,
                context: this,
                deferredKeydownHandler: this.processKeydown,
                immediateHandleCondition: this.immediateHandleCondition,
                immediateKeydownHandler: this.immediateKeydownHandler,
                stopPropagation: true
            });

            var ListWithNavigationModel = ListWithSelectionModel.extend(listWithNavigationModelTrait);
            this.listViewModel = options.listViewModel || new ListWithNavigationModel({
                getData: options.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor
            });

            this.listView = options.listView || new ListWithNavigation({
                el: options.listElement || $(listTemplate),
                model: this.listViewModel,
                chunksTemplate: options.chunksTemplate,
                itemsTemplate: options.itemsTemplate || itemsTemplate,
                scrollTimeout: options.scrollTimeout,
                lazy: true,
                selection: {
                    allowed: true,
                    multiple: true
                }
            });
            this.listView.setCanActivate(false);

            this.initListeners();
            this.render().resize();
        },

        initListeners: function() {
            this.listenTo(this.listView, "active:changed", this.activeChange, this);
            this.listenTo(this.model, "change:focused", this.focusStateChanged, this);
            this.listenTo(this.model, "change:disabled", this.changeDisabled, this);

            $("body").on("mousedown", this.onGlobalMousedown).on("mouseup", this.onGlobalMouseup)
                .on("mousemove", this.onGlobalMousemove);
        },

        render: function() {
            this.listView.undelegateEvents();

            var selectedItemsList = $(this.template({
                disabled: this.model.get("disabled")
            }));

            selectedItemsList.append(this.listView.el);

            this.$el.empty();
            this.$el.append(selectedItemsList);

            this.listView.delegateEvents();

            return this;
        },

        renderData: function() {
            this.listView.renderData();
            return this;
        },

        /* Event Handlers */

        activeChange: function(active) {
            if (active) {
                if (this.activeChangedWithShift) {
                    delete this.activeChangedWithShift;
                    this.listViewModel.addRangeToSelection(active.value, active.index);
                }

                this.listView.scrollTo(active.index);
            }
        },

        changeDisabled: function() {
            var disabled = this.model.get("disabled");

            if (disabled) {
                this.$el.addClass("disabled");
                this.$el.find("input[type='text']").attr("disabled", "disabled");
                this.listView.setValue({});
                this.listView.activate(undefined);
            } else {
                this.$el.find("input[type='text']").removeAttr("disabled");
                this.$el.removeClass("disabled");
            }

            this.listView.setDisabled(disabled);
        },

        focusStateChanged: function() {
            if (this.model.get("focused")) {
                this.$el.find(".mSelect-svListPlaceholder").addClass("focused");
                this.listView.setCanActivate(true);
                this._activateFirstSelectedItem();
            } else {
                this.$el.find(".mSelect-svListPlaceholder").removeClass("focused");
                this.listView.setValue({});
                this.listView.activate(undefined);
                this.listView.setCanActivate(false);
            }
        },

        onFocus: function() {
            this.model.set("focused", true);
        },

        onBlur: function() {
            if (!this.preventBlur) {
                this.model.set("focused", false);
            }
        },

        onMouseupOnDeleteButton: function(event) {
            this.onDeleteKey(event);
        },

        onMousedown: function() {
            this.preventBlur = true;
            if (!this.model.get("focused")) {
                this.$el.find("input").focus();
            }
        },

        onMouseup: function() {
            if (this.preventBlur) {
                this.$el.find("input").focus();

                delete this.preventBlur;
            }
        },

        onGlobalMousedown: function(event) {
            if (this.preventBlur) {
                if (event.target === this.$el || this.$el.find(event.target).length > 0) {
                    //Do not collapse if mousedown performed on this component
                    return;
                }

                delete this.preventBlur;
                this.onBlur();
            }
        },

        onGlobalMouseup: function() {
            this.onMouseup();
        },

        onGlobalMousemove: function(event) {
            if (this.preventBlur) {
                event.stopPropagation();
            }
        },

        /* Key handlers for KeyboardManager */

        onUpKey: function(event) {
            if (event.shiftKey) {
                this.activeChangedWithShift = true;
            }

            this.listView.activatePrevious();
        },

        onDownKey: function(event) {
            var active = this.listView.getActiveValue();

            if (active) {
                if (event.shiftKey) {
                    this.activeChangedWithShift = true;
                }

                this.listView.activateNext();
            } else {
                this.listView.activateFirst();
            }
        },

        onEnterKey: function(event) {
            event.preventDefault();

            var active = this.listView.getActiveValue();

            if (event.shiftKey) {
                this.listViewModel.addRangeToSelection(active.value, active.index);
            } else if (event.ctrlKey || event.metaKey) {
                this.listViewModel.clearSelection();
                this.listViewModel.addValueToSelection(active.value, active.index);
            } else {
                this.listViewModel.toggleSelection(active.value, active.index);
            }
        },

        onHomeKey: function(event) {
            if (event.shiftKey) {
                this.activeChangedWithShift = true;
            }

            this.listView.activateFirst();
        },

        onEndKey: function(event) {
            if (event.shiftKey) {
                this.activeChangedWithShift = true;
            }

            this.listView.activateLast();
        },

        onPageUpKey: function(event) {
            if (event.shiftKey) {
                this.activeChangedWithShift = true;
            }

            this.listView.pageUp();
        },

        onPageDownKey: function(event) {
            if (event.shiftKey) {
                this.activeChangedWithShift = true;
            }

            this.listView.pageDown();
        },

        onTabKey: function() {
            /* do nothing */
        },

        onAKey: function(event) {
            if (event.ctrlKey || event.metaKey) {
                event.stopPropagation();
                this.listView.selectAll();
            }
        },

        onDeleteKey: function(event) {
            var value = this.listView.getValue();


            if (value && value.length > 0) {
                var emptySelection = true;

                for (var index in value) {
                    if (value.hasOwnProperty(index) && value[index] !== undefined) {
                        emptySelection = false;
                        break;
                    }
                }

                if (!emptySelection) {
                    this.trigger("selection:remove", value);
                }
            }
        },

        /* Internal helper methods */

        _activateFirstSelectedItem: function() {
            var selection = this.listView.getValue();
            for (var index in selection) {
                if (selection.hasOwnProperty(index) && selection[index] !== undefined) {
                    this.listViewModel.activate(parseInt(index, 10), {silent: true});
                    break;
                }
            }
        },

        /* API */

        fetch: function(callback) {
            this.listView.setValue({});
            this.listView.activate(undefined);
            this.listView.fetch(callback);
        },

        resize: function() {
            if (!this.listViewModel.get("items") || this.listViewModel.get("items").length === 0) {
                this.$el.hide();
            } else {
                this.$el.show();
                this.listView.resize();
            }
        },

        setDisabled: function(disabled) {
            this.model.set("disabled", disabled);
        },

        getDisabled: function() {
            return this.model.get("disabled");
        },

        remove: function() {
            this.listView.remove();
            Backbone.View.prototype.remove.call(this);
            $("body").off("mousedown", this.onGlobalMousedown).off("mouseup", this.onGlobalMouseup)
                .off("mousemove", this.onGlobalMousemove);
        }
    });

    return SelectedItemsList;
});

