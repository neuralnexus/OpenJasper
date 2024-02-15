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
 * @version: $Id: AvailableItemsList.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * AvailableItems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        KeyboardManager = require("common/component/singleSelect/manager/KeyboardManager"),
        ListWithNavigation = require("common/component/list/view/ListWithNavigation"),
        ListWithSelectionModel = require("common/component/list/model/ListWithSelectionModel"),
        listWithNavigationModelTrait = require("common/component/list/model/listWithNavigationModelTrait"),
        SearcheableDataProvider = require("common/component/singleSelect/dataprovider/SearcheableDataProvider"),
        DropDownManager = require("common/component/singleSelect/manager/DropDownManager"),
        availableItemsListTemplate = require("text!common/component/multiSelect/templates/availableItemsListTemplate.htm"),
        availableItemsListDropdownTemplate = require("text!common/component/multiSelect/templates/availableItemsListDropdownTemplate.htm"),
        itemsTemplate = require("text!common/component/multiSelect/templates/availableItemsTemplate.htm"),
        listTemplate = require("text!common/component/multiSelect/templates/listTemplate.htm"),
        i18n = require("bundle!ScalableInputControlsBundle");

    var CONTROL_NORMAL_WIDTH_LIMIT = 200;

    var AvailableItemsList = Backbone.View.extend({

        events: function() {
            return {
                "keydown input.mSelect-input": this.keyboardManager.onKeydown,
                "focus input.mSelect-input": "onFocus",
                "blur input.mSelect-input": "onBlur",
                "click input.mSelect-input": "onClickOnInput",
                "touchend input.mSelect-input": "onClickOnInput",
                "mousedown": "onMousedown",
                "mouseup": "onMouseup"
            };
        },

        keydownHandlers: _.extend({
            "65": "onAKey"
        }, KeyboardManager.prototype.keydownHandlers),

        initialize: function(options) {
            _.bindAll(this, "onGlobalMouseup", "onGlobalMousedown", "onGlobalMousemove",
                "onMousedown", "onMouseup", "onDropdownMouseup", "onEscKey", "onSelectAll", "onSelectNone",
                "onInvertSelection", "calcDimensionsForDropDown", "collapse");

            if (!this.model) {
                this.model = new Backbone.Model();
            }

            this.model.set("expanded", false, {silent: true});
            this.model.set("criteria", "", {silent: true});

            this.label = options.label;
            this.template = _.template(options.availableItemsListTemplate || availableItemsListTemplate);
            this.dropDownTemplate = _.template(options.dropDownTemplate || availableItemsListDropdownTemplate);

            this.keyboardManager = new KeyboardManager({
                keydownHandlers: this.keydownHandlers,
                keydownTimeout: options.keydownTimeout,
                context: this,
                deferredKeydownHandler: this.processKeydown,
                immediateHandleCondition: this.immediateHandleCondition,
                immediateKeydownHandler: this.immediateKeydownHandler,
                stopPropagation: true
            });

            this.listViewModel = this._createListViewModel(options);

            this.setValue(options.value, {silent: true});

            this.listView = this._createListView(options);

            this.render();

            this.dropDownManager = new DropDownManager({
                dropDownEl: this.$dropDownEl,
                calcDimensions: this.calcDimensionsForDropDown,
                onDimensionChanged: this.collapse
            });

            this.initListeners();
        },

        _createListViewModel: function(options) {
            this.searcheableDataProvider = new SearcheableDataProvider({getData: options.getData});

            var ListWithNavigationModel = ListWithSelectionModel.extend(listWithNavigationModelTrait);
            return options.listViewModel || new ListWithNavigationModel({
                getData: this.searcheableDataProvider.getData,
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor
            });
        },

        _createListView: function(options) {
            return options.listView || new ListWithNavigation({
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
        },

        initListeners: function() {
            this.listenTo(this.listView, "active:changed", this.activeChange, this);
            //this.listenTo(this.listView, "mousedown", this.onMousedown, this);
            this.listenTo(this.listViewModel, "selection:clear", this.selectionClear, this);
            this.listenTo(this.listViewModel, "selection:add", this.selectionAdd, this);
            this.listenTo(this.listViewModel, "selection:addRange", this.selectionAddRange, this);
            this.listenTo(this.listViewModel, "selection:remove", this.selectionRemove, this);

            this.listenTo(this.model, "change:expanded", this.changeExpandedState, this);
            this.listenTo(this.model, "change:value", this.changeValue, this);
            this.listenTo(this.model, "change:disabled", this.changeDisabled, this);
            this.listenTo(this.model, "change:criteria", this.changeFilter, this);

            this.$dropDownEl.on("mousedown", this.onMousedown)
                .on("mouseup", this.onDropdownMouseup)
                .on("click", ".mSelect-footer-button", this.onEscKey)
                .on("click", "a.all", this.onSelectAll)
                .on("click", "a.none", this.onSelectNone)
                .on("click", "a.invert", this.onInvertSelection);

            $("body").on("mousedown", this.onGlobalMousedown)
                .on("dataavailable", this.onGlobalMousedown) /* hack to handle prototype custom events */
                .on("mouseup", this.onGlobalMouseup)
                .on("mousemove", this.onGlobalMousemove);
        },

        render: function() {
            this.renderDropdownPart();

            this.$el.empty();
            this.$el.append($(this.template(this.getModelForRendering())));

            this.changeDisabled();

            return this;
        },

        renderDropdownPart: function() {
            if (!this.$dropDownEl) {
                this.listView.undelegateEvents();

                this.$dropDownEl = $(this.dropDownTemplate(this.getModelForRendering()));
                this.$dropDownEl.prepend(this.listView.el);

                $("body").append(this.$dropDownEl);

                this.listView.delegateEvents();
            }

            this.model.get("expanded")
                ? this.$dropDownEl.show()
                : this.$dropDownEl.hide();
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

        selectionAdd: function(selection) {
            var indexMapping = this.searcheableDataProvider.getIndexMapping();
            var index = indexMapping ? indexMapping[selection.index] : selection.index;

            this.model.get("value")[index] = selection.value;
            this.model.trigger("change:value");
        },

        selectionAddRange: function(range) {
            var indexMapping = this.searcheableDataProvider.getIndexMapping();
            var value = this.model.get("value");

            for (var i = range.start; i <= range.end; i++) {
                var index = indexMapping ? indexMapping[i] : i;

                this.model.get("value")[index] = range.selection[i];
            }

            this.model.trigger("change:value");
        },

        selectionRemove: function(selection) {
            var indexMapping = this.searcheableDataProvider.getIndexMapping();
            var index = indexMapping ? indexMapping[selection.index] : selection.index;

            this.model.get("value")[index] = undefined;
            this.model.trigger("change:value");
        },

        selectionClear: function() {
            this.model.attributes.value = [];
        },

        onSelectAll: function() {
            if (!this.model.get("disabled")) {
                this.listView.once("selection:change", this.processSelectionThroughApi, this);
                this.listView.selectAll();
            }
        },

        onSelectNone: function() {
            if (!this.model.get("disabled")) {
                this.listView.once("selection:change", this.processSelectionThroughApi, this);
                this.listView.selectNone();
            }
        },

        onInvertSelection: function() {
            if (!this.model.get("disabled")) {
                this.listView.once("selection:change", this.processSelectionThroughApi, this);
                this.listView.invertSelection();
            }
        },

        changeExpandedState: function(model) {
            if (model.get("expanded")) {
                this.doExpand();
            } else {
                this.doCollapse();
            }
        },

        changeValue: function() {
            this.trigger("selection:change", this.getValue());
        },

        changeDisabled: function() {
            var disabled = this.model.get("disabled");

            if (disabled) {
                this.$el.addClass("disabled").find("input[type='text']").attr("disabled", "disabled");
                this.$dropDownEl.addClass("disabled").find(".mSelect-footer a").attr("disabled", "disabled");
            } else {
                this.$el.removeClass("disabled").find("input[type='text']").removeAttr("disabled");
                this.$dropDownEl.removeClass("disabled").find(".mSelect-footer a").removeAttr("disabled");
            }

            this.listView.setDisabled(disabled);
        },

        onClickOnInput: function() {
            this.onFocus();
        },

        onFocus: function() {
            if (!this.model.get("expanded")) {
                this.model.set("expanded", true);
            }

            //Workaround for input's HTML5 placeholder attribute
            var input = this.$el.find("input");
            if (input.val() == input.attr('placeholder')) {
                input.val('');
                input.removeClass('placeholder');
            }
        },

        onBlur: function() {
            //Workaround for input's HTML5 placeholder attribute
            var input = this.$el.find("input");
            if (input.val() == '' || input.val() == input.attr('placeholder')) {
                input.addClass('placeholder');
                input.val(input.attr('placeholder'));
            }
            // End of workaround

            if (!this.preventBlur) {
                this.collapse();
            } else {
                return false;
            }
        },

        onMousedown: function() {
            this.preventBlur = true;
        },

        onDropdownMouseup: function(event) {
            if (!$(event.target).hasClass("mSelect-footer-button")) {
                this.onMouseup();
            } else {
                delete this.preventBlur;
            }
        },

        onMouseup: function() {
            if (this.preventBlur) {
                delete this.preventBlur;

                if (this.model.get("expanded")) {
                    this.$el.find("input.mSelect-input").focus();
                }
            }
        },

        onGlobalMousedown: function(event) {
            if (this.model.get("expanded")) {
                if (event.target === this.el || this.$el.find(event.target).length > 0 ||
                    event.target === this.$dropDownEl[0] || this.$dropDownEl.find(event.target).length > 0) {
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
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                if (event.shiftKey) {
                    this.activeChangedWithShift = true;
                }

                this.listView.activatePrevious();
            }
        },

        onDownKey: function(event) {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                var active = this.listView.getActiveValue();

                if (active) {
                    if (event.shiftKey) {
                        this.activeChangedWithShift = true;
                    }

                    this.listView.activateNext()
                } else {
                    this.listView.activateFirst();
                }
            }
        },

        onEnterKey: function(event) {
            event.preventDefault();

            if (!this.model.get("expanded")) {
                this.expand();
                return;
            }

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

        onEscKey: function() {
            if (this.model.get("expanded")) {
                this.collapse();
            }
        },

        onHomeKey: function(event) {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                if (event.shiftKey) {
                    this.activeChangedWithShift = true;
                }

                this.listView.activateFirst();
            }
        },

        onEndKey: function(event) {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                if (event.shiftKey) {
                    this.activeChangedWithShift = true;
                }

                this.listView.activateLast();
            }
        },

        onPageUpKey: function(event) {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                if (event.shiftKey) {
                    this.activeChangedWithShift = true;
                }

                this.listView.pageUp();
            }
        },

        onPageDownKey: function(event) {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                if (event.shiftKey) {
                    this.activeChangedWithShift = true;
                }

                this.listView.pageDown();
            }
        },

        onTabKey: function() {
            /* do nothing */
        },

        onAKey: function(event) {
            if (!event.ctrlKey && !event.metaKey) {
                this.keyboardManager.deferredHandleKeyboardEvent(event);
            } else {
                event.stopPropagation();
                this.onSelectAll();
            }
        },

        /* Internal helper methods */

        doExpand: function() {
            this.$el.find(".mSelect-avListPlaceholder").removeClass("collapsed").addClass("expanded");
            this.expandDropdownPart();
            if (this.listView.lazy) {
                this.listView.fetch(_.bind(this.listView.resize, this.listView));
            } else {
                this.listView.resize();
            }

            this.trigger("expand");
        },

        expandDropdownPart: function() {
            //If control width is too small we have to add special class to dropdown part
            if (this.$el.width() < CONTROL_NORMAL_WIDTH_LIMIT) {
                this.$dropDownEl.find(".mSelect-footer").addClass("mSelect-footer-narrow");
            }

            this.$dropDownEl.show();
            this.dropDownManager.startCalc();
        },

        doCollapse: function() {
            this.$el.find(".mSelect-avListPlaceholder").removeClass("expanded").addClass("collapsed");
            this.$el.find("input").val("");
            this.$dropDownEl.hide().find(".mSelect-footer").removeClass("mSelect-footer-narrow");
            this.model.set("criteria", "");
            this.dropDownManager.stopCalc();
            this.trigger("collapse");
        },

        calcDimensionsForDropDown: function() {
            var offset = this.$el.offset();
            var top = offset.top + this.$el.height();

            return {
                top: top,
                left: offset.left,
                width: this.$el.width()
            }
        },

        processKeydown: function() {
            this.model.set("criteria", this.$el.find("input").val());
        },

        changeFilter: function(callback) {
            var that = this;
            this.searcheableDataProvider.getData({criteria: this.model.get("criteria")}).done(
                callback && typeof callback === "function" ? callback : function() {
                that.listView.fetch(function () {
                    that.listView.setValue(that.model.get("value"), {silent: true});
                });
            });
        },

        processSelectionThroughApi: function(selection) {
            var indexMapping = this.searcheableDataProvider.getIndexMapping();
            var total = this.listViewModel.get("total");

            var newValue = [];

            for (var i in selection) {
                if (selection.hasOwnProperty(i)) {
                    var index = indexMapping ? indexMapping[i] : i;
                    var value = selection[i];

                    if (value !== undefined) {
                        newValue[index] = value;
                    }
                }
            }

            this.model.attributes.value = newValue;
            this.model.trigger("change:value");
        },

        convertSelectionForListViewModel: function(selection) {
            var indexMapping = this.searcheableDataProvider.getReverseIndexMapping();
            if (!indexMapping) {
                return selection;
            }

            var newSelection = {};
            for (var i in selection) {
                if (selection.hasOwnProperty(i)) {
                    var index = indexMapping[i];

                    if (index) {
                        var value = selection[i];
                        if (value !== undefined) {
                            newSelection[index] = value;
                        }
                    }
                }
            }

            return newSelection;
        },

        /* Internal methods */

        getModelForRendering: function() {
            return {
                label: this.label,
                isIPad: navigator.platform === "iPad",
                expanded: this.model.get("expanded"),
                disabled: this.model.get("disabled"),
                i18n: i18n
            }
        },

        /* API */

        fetch: function(callback) {
            this.listView.fetch(callback);
        },

        reset: function(options) {
            this.listView.reset(options);
        },

        expand: function() {
            this.model.set("expanded", true);
            return this;
        },

        collapse: function() {
            this.model.set("expanded", false);
            return this;
        },

        getValue: function() {
            return this.model.get("value");
        },

        setValue: function(value, options) {
            this.listViewModel.once("selection:change", function() {
                this.model.attributes.value = this.listViewModel.getSelection();
                if (!options || !options.silent) {
                    this.changeValue();
                }
            }, this);


            if (!_.isArray(value) && !(typeof value === "string")) {
                value = this.convertSelectionForListViewModel(value);
            }

            this.listViewModel.select(value);
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

            this.$dropDownEl.off("mousedown", this.onMousedown)
                .off("mouseup", this.onDropdownMouseup)
                .off("click", this.onEscKey)
                .off("click", this.onSelectAll)
                .off("click", this.onSelectNone)
                .off("click", this.onInvertSelection).remove();

            $("body").off("mousedown", this.onGlobalMousedown)
                .off("dataavailable", this.onGlobalMousedown)
                .off("mouseup", this.onGlobalMouseup)
                .off("mousemove", this.onGlobalMousemove);
        }
    });

    return AvailableItemsList;
});

