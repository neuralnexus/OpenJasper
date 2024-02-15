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
 * @version: $Id: SingleSelect.js 47920 2014-08-07 11:08:53Z sergey.prilukin $
 */

/**
 * Single select component which supports view port, and filtering via search.
 */

define(function (require) {

    'use strict';

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        KeyboardManager = require("common/component/singleSelect/manager/KeyboardManager"),
        SingleSelectList = require("common/component/singleSelect/view/SingleSelectList"),
        SingleSelectListModel = require("common/component/singleSelect/model/SingleSelectListModel"),
        SearcheableDataProvider = require("common/component/singleSelect/dataprovider/SearcheableDataProvider"),
        DropDownManager = require("common/component/singleSelect/manager/DropDownManager"),
        singleSelectTemplate = require("text!common/component/singleSelect/templates/singleSelectTemplate.htm"),
        singleSelectListTemplate = require("text!common/component/singleSelect/templates/listTemplate.htm"),
        singleSelectItemsTemplate = require("text!common/component/singleSelect/templates/itemsTemplate.htm"),
        dropDownTemplate = require("text!common/component/singleSelect/templates/dropDownTemplate.htm"),
        i18n = require("bundle!ScalableInputControlsBundle");

    var SingleSelect = Backbone.View.extend({

        events: function() {
            return {
                "keydown input": this.keyboardManager.onKeydown,
                "focus input": "onFocus",
                "blur input": "onBlur",
                "mousedown": "onMousedown",
                "mouseup": "onMouseup",
                "click .sSelect-input": "onClickOnInput"
            };
        },

        initialize: function(options) {
            _.bindAll(this, "onGlobalMouseup", "onGlobalMousedown",
                "onGlobalMousemove", "onMousedown", "calcDimensionsForListView", "collapse");

            if (!this.model) {
                this.model = new Backbone.Model();
            }

            this.model.set("expanded", false, {silent: true});
            this.model.set("criteria", "", {silent: true});
            this.model.set("value", {}, {silent: true});

            this.label = options.label;
            this.template = _.template(options.template || singleSelectTemplate);
            this.dropDownTemplate = _.template(options.dropDownTemplate || dropDownTemplate);

            this.keyboardManager = new KeyboardManager({
                keydownTimeout: options.keydownTimeout,
                context: this,
                deferredKeydownHandler: this.processKeydown,
                immediateHandleCondition: this.immediateHandleCondition,
                immediateKeydownHandler: this.immediateKeydownHandler,
                stopPropagation: true
            });

            //Initialize internal list view model
            this.listViewModel = this._createListViewModel(options);

            //initialize list view
            this.listView = this._createListView(options);

            this.render();

            this.dropDownManager = new DropDownManager({
                dropDownEl: this.$dropDownEl,
                calcDimensions: this.calcDimensionsForListView,
                isDimensionsChanged: this.isDimensionsChangedforListView,
                onDimensionChanged: this.collapse
            });

            this.initListeners();
            this.setValue(options.value, {
                modelOptions: {silent: true}
            });
        },

        _createListView: function(options) {
            return new SingleSelectList({
                el: options.listElement || $(singleSelectListTemplate),
                model: this.listViewModel,
                chunksTemplate: options.chunksTemplate,
                itemsTemplate: options.itemsTemplate || singleSelectItemsTemplate,
                scrollTimeout: options.scrollTimeout
            });
        },

        _createListViewModel: function(options) {
            this.searcheableDataProvider = new SearcheableDataProvider({getData: options.getData});

            return new SingleSelectListModel({
                getData: this._getGetData(),
                bufferSize: options.bufferSize,
                loadFactor: options.loadFactor
            });
        },

        _getGetData: function() {
            return this.searcheableDataProvider.getData;
        },

        _getActiveValueIndex: function(active) {
            var indexMapping = this.searcheableDataProvider.getIndexMapping();
            return indexMapping ? indexMapping[active.index] : active.index;
        },

        /*
         Bind event listeners
         */
        initListeners: function() {
            this.listenTo(this.listView, "selection:change", this.selectionChange, this);
            this.listenTo(this.listView, "item:mouseup", this.itemMouseup, this);
            this.listenTo(this.model, "change:expanded", this.changeExpandedState, this);
            this.listenTo(this.model, "change:value", this.changeValue, this);
            this.listenTo(this.model, "change:disabled", this.changeDisabled, this);
            this.listenTo(this.model, "change:criteria", this.changeFilter, this);

            this.$dropDownEl.on("mousedown", this.onMousedown);

            $("body").on("mousedown", this.onGlobalMousedown)
                .on("dataavailable", this.onGlobalMousedown) /* hack to handle prototype custom events */
                .on("mouseup", this.onGlobalMouseup)
                .on("mousemove", this.onGlobalMousemove);
        },

        render: function() {

            var singleSelect = $(this.template({
                label: this.label,
                isIPad: navigator.platform === "iPad",
                value: this.model.get("value").label,
                expanded: this.model.get("expanded"),
                i18n: i18n
            }));

            this.renderListView();

            this.$el.empty();
            this.$el.append(singleSelect);

            return this;
        },

        renderListView: function() {
            if (!this.listRendered) {
                this.$dropDownEl = $(this.dropDownTemplate(
                    {isIPad: navigator.platform === "iPad"}
                ));

                this.listView.undelegateEvents();
                this.$dropDownEl.append(this.listView.$el);
                $("body").append(this.$dropDownEl);
                this.listView.delegateEvents();

                this.listRendered = true;
            }

            this.model.get("expanded")
                ? this.$dropDownEl.show()
                : this.$dropDownEl.hide();
        },

        renderData: function() {
            this.listView.renderData();
            return this;
        },

        /* Event handlers */

        itemMouseup: function() {
            if (this.model.get("disabled")) {
                return;
            }

            //Remove flag to allow blur
            delete this.preventBlur;

            //Preserve focus
            this.$el.find("input").focus();

            var activeValue = this.model.get("activeValue");

            if (activeValue.value !== this.model.get("value").value) {
                this.model.unset("activeValue");
                this.model.set("value", activeValue);
            } else {
                this.collapse();
            }
        },

        selectionChange: function() {
            var active = this.listView.getActiveValue();

            if (active) {
                this.listView.scrollTo(active.index);

                this.model.set("activeValue", {
                    value: active.value,
                    label: active.label,
                    index: this._getActiveValueIndex(active)
                });

                this.updateValueFromActive();
            } else {
                this.model.unset("activeValue");
            }
        },

        onClickOnInput: function() {
            this.model.get("expanded") ? this.collapse() : this.expand();
        },

        onFocus: function() {
            if (!this.model.get("expanded")) {
                this.$el.find(".sSelect").addClass("focused")
            }
        },

        onBlur: function() {
            if (!this.preventBlur) {
                this.collapse();

                this.$el.find(".sSelect").removeClass("focused");
            }
        },

        onMousedown: function() {
            this.preventBlur = true;
        },

        onMouseup: function() {
            if (this.preventBlur) {
                delete this.preventBlur;

                if (this.model.get("expanded")) {
                    this.$el.find("input").focus();
                }
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
            this.collapse();
            this.updateControlLabel();

            if (!this.silent) {
                this.trigger("selection:change", this.model.get("value").value);
            } else {
                delete this.silent;
            }

            this.$el.find("input").val("");
            this.model.set("criteria", "");
        },

        changeDisabled: function() {
            var disabled = this.model.get("disabled");

            if (disabled) {
                this.$el.addClass("disabled");
                this.$el.find("input[type='text']").attr("disabled", "disabled");
                this.collapse();
            } else {
                this.$el.find("input[type='text']").removeAttr("disabled");
                this.$el.removeClass("disabled");
            }

            this.listView.setDisabled(disabled);
        },

        onGlobalMousedown: function(event) {
            if (this.model.get("expanded")) {
                if (event.target === this.el || this.$el.find(event.target).length > 0 ||
                    event.target === this.$dropDownEl[0] || this.$dropDownEl.find(event.target).length > 0) {
                    //Do not collapse if mousedown performed on this component
                    this.$el.find("input").focus();
                    return;
                }

                this.preventBlur = false;
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

        onUpKey: function() {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                var active = this.listView.getActiveValue();

                if (!active || active.index === 0) {
                    this.collapse();
                    return;
                } else {
                    this.listView.activatePrevious();
                }
            }
        },

        onDownKey: function() {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                var active = this.listView.getActiveValue();

                if (active) {
                    this.listView.activateNext();
                } else {
                    this.listView.activateFirst();
                }
            }
        },

        onEnterKey: function(event) {
            event.preventDefault();

            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                if (this.model.get("activeValue")) {
                    this.itemMouseup();
                } else {
                    this.collapse();
                }
            }
        },

        onEscKey: function() {
            if (this.model.get("expanded")) {
                this.collapse();
            }
        },

        onHomeKey: function() {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                this.listView.activateFirst();
            }
        },

        onEndKey: function() {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                this.listView.activateLast();
            }
        },

        onPageUpKey: function() {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                this.listView.pageUp();
            }
        },

        onPageDownKey: function() {
            if (!this.model.get("expanded")) {
                this.expand();
            } else {
                this.listView.pageDown();
            }
        },

        onTabKey: function() {
            /* do nothing */
        },

        /* Internal helper methods */

        doExpand: function() {
            this.$el.find(".sSelect").removeClass("collapsed").addClass("expanded").addClass("focused");
            this.$el.find("input").focus();

            this.expandListView();

            if (this.listView.lazy) {
                this.listView.fetch(_.bind(this.listView.resize, this.listView));
            } else {
                this.listView.resize();
            }

            if (typeof this.model.get("value").value !== "undefined") {
                this.setValueToList();
            }

            this.trigger("expand", this);
        },

        expandListView: function() {
            this.$dropDownEl.show();
            this.dropDownManager.startCalc();
        },

        doCollapse: function() {
            this.model.unset("activeValue");
            this.$el.find(".sSelect").removeClass("expanded").addClass("collapsed");
            this.$el.find("input").val("");
            this.$dropDownEl.hide();
            this.model.set("criteria", "");
            this.dropDownManager.stopCalc();
            this.trigger("collapse", this);
        },

        calcDimensionsForListView: function() {
            var offset = this.$el.offset();
            var top = offset.top + this.$el.height() + this.$el.find(".sSelect-listContainer").height() - 2;

            return {
                top: top,
                left: offset.left,
                width: this.$el.find(".sSelect").width()
            }
        },

        isDimensionsChangedforListView: function(currentDimensions, newDimensions) {
            return (Math.floor(currentDimensions.top) !== (Math.floor(newDimensions.top))
                || Math.floor(currentDimensions.left) !== Math.floor(newDimensions.left));
        },

        immediateHandleCondition: function(event) {
            return !this.model.get("expanded");
        },

        immediateKeydownHandler: function(event) {
            this.expand();
            this.keyboardManager.deferredHandleKeyboardEvent(event);
        },

        processKeydown: function() {
            this.model.set("criteria", this.$el.find("input").val());
        },

        changeFilter: function() {
            var that = this;
            this._getGetData()({criteria: this.model.get("criteria")}).done(function() {
                that.listView.fetch(function () {
                    that.setValueToList();
                });
            });
        },

        setValueToList: function(options) {
            var value = this.model.get("value");

            var selection = value.value;

            if (typeof value.index !== "undefined") {
                var indexMapping = this.searcheableDataProvider.getReverseIndexMapping();

                var index = (indexMapping ? indexMapping[value.index] : value.index);
                selection = {};

                if (typeof index !== "undefined") {
                    selection[index] = value.value;
                }
            }

            this.listView.setValue(selection, options);
        },

        //value is external usual value: either string, or array of strings or object
        convertExternalValueToInternalFormat: function(value) {
            var internalValue = {};

            if (typeof value !== "undefined") {
                if (typeof value === "string" || value === null) {
                    internalValue = {value: value};
                } else {
                    for (var index in value) {
                        if (value.hasOwnProperty(index) && value[index] !== undefined) {
                            internalValue = {value: value[index], index: parseInt(index, 10)};
                            break;
                        }
                    }
                }
            }

            return internalValue;
        },

        updateValueFromActive: function() {
            var value = this.model.get("value");
            var activeValue = this.model.get("activeValue");

            if (value.value === activeValue.value && typeof value.label === "undefined") {
                value.label = activeValue.label;
                this.updateControlLabel();
            }
        },

        updateControlLabel: function() {
            var value = this.model.get("value");
            if (_.isEmpty(value) || value.value == null){
                this.render();
            } else {
                var label = this.getControlLabelByValue(value);

                this.$el.find(".sSelect-input")
                    .attr("title", label)
                    .find("> span").html(label);
            }
        },

        getControlLabelByValue: function (value) {
            return value.label == null
                ? value.value
                : value.label;
        },

        /* API */

        fetch: function(callback) {
            this.listView.fetch(callback);
            return this;
        },

        reset: function(options) {
            this.listView.reset(options);
            return this;
        },

        expand: function() {
            if (this.model.get("disabled")) {
                return;
            }

            this.model.set("expanded", true);
            return this;
        },

        collapse: function() {
            this.model.set("expanded", false);
            return this;
        },

        getValue: function() {
            return this.model.get("value").value;
        },

        setValue: function(value, options) {
            var value = this.convertExternalValueToInternalFormat(value);

            if (options && options.silent) {
                this.silent = true;
            }

            this.model.set("value", value);
            this.setValueToList();
        },

        setDisabled: function(disabled) {
            this.model.set("disabled", disabled);
            return this;
        },

        getDisabled: function() {
            return this.model.get("disabled");
        },

        remove: function() {
            this.$dropDownEl.off("mousedown", this.onMousedown).remove();
            this.listView.remove();
            Backbone.View.prototype.remove.call(this);
            $("body").off("mousedown", this.onGlobalMousedown)
                .off("dataavailable", this.onGlobalMousedown)
                .off("mouseup", this.onGlobalMouseup)
                .off("mousemove", this.onGlobalMousemove);
        }
    });

    //Support for non-AMD modules
    window.SingleSelect = SingleSelect;

    return SingleSelect;
});
