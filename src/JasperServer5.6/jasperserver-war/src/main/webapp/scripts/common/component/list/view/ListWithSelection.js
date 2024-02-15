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
 * @author: Sergey Prilukin
 * @version: $Id: ListWithSelection.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * List component which extends ScalableList component
 * and supports single and multi selection.
 *
 * Usage:
 *
 *    var list = new jaspersoft.components.ListWithSelection({
 *        el:               @see ScalableList,
 *        * getData:        @see ScalableList,
 *        lazy:             @see ScalableList,
 *        itemsTemplate:    @see ScalableList,
 *        scrollTimeout:    @see ScalableList,
 *        model:            @see ScalableList,
 *        bufferSize:       @see ScalableList,
 *        loadFactor:       @see ScalableList,
 *        selection: {
 *            allowed:      <whether component allows to select items>
 *            multiple:     <whether component allows multiple selection>
 *        }
 *        value:            <initial selection it could be a string with value,
 *                          array with values or object hash
 *                          where key is index of value in dataset and value is value>
 *    });
 *
 *    * - required items
 *
 * ListWithSelection API:
 *      on("selection:change",
 *          function(selection) {}) - "selection:change" event occurred then selection was changed
 *      render                      - @see ScalableList
 *      renderData                  - @see ScalableList
 *      resize                      - @see ScalableList
 *      fetch(callback)             - @see ScalableList
 *      getValue                    - returns selected values
 *      setValue(value)             - select passed value. Format of the items is the same as in
 *                                      value parameter in list constructor
 *                                    constructor parameter and triggers "selection:change" event
 *      selectAll                   - select all items and triggers "selection:change" event
 *      selectNone                  - clear selection and triggers "selection:change" event
 *      invertSelection             - does selection inversion and triggers "selection:change" event
 *      remove                      - @see ScalableList
 *
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        _ = require("underscore"),
        ListWithSelectionModel = require("common/component/list/model/ListWithSelectionModel"),
        ScalableList = require("common/component/list/view/ScalableList");

    var MOUSE_MOVE_MIN_PIXELS = 1;

    /**
        Extension for ScalableList which allows to select elements in list.

        in addition to what ScalableList expect to see in options hash this extension also looks for following options:

        selection       - object with following structure
                            {allowed: <true|false>,  //whether selection allowed. if not then no selections event
                                                     //will be triggered and no selection will be rendered.
                                                     //true by default
                             multiple: <true|false>  //whether multiple selection is allowed.
                                                     //true by default
                            }
        value           - initial selected value(s)

         also read description for ScalableList.
     */
    var ListWithSelection = ScalableList.extend({

        //Default list model,
        ListModel: ListWithSelectionModel,

        events: _.extend({}, ScalableList.prototype.events, {
            "mousedown li": "onMousedown",
            "mousemove li": "onMousemove"
        }),

        /*
            Main init method
         */
        initialize: function(options) {
            _.bindAll(this,
                "onGlobalMouseup", "onGlobalMousemove",
                "_autoScroll", "fetch");

            //init Selection
            this._initSelection(options.selection);

            return ScalableList.prototype.initialize.call(this, options);
        },

        /*
            Init selection
         */
        _initSelection: function(selection) {
            this.selection = {
                allowed: selection && typeof selection.allowed !== "undefined" ?  selection.allowed : true,
                multiple: selection && typeof selection.multiple !== "undefined" ?  selection.multiple : true
            };
        },

        /*
         Bind event listeners on model
         */
        initListeners: function() {
            ScalableList.prototype.initListeners.call(this);

            this.listenTo(this.model, "selection:clear", this.clearSelection, this);
            this.listenTo(this.model, "selection:add", this.selectValue, this);
            this.listenTo(this.model, "selection:addRange", this.selectRange, this);
            this.listenTo(this.model, "selection:remove", this.deselectValue, this);

            //Bind mouseup and mousemove to a body tag because we
            //need to capture these events fired outside list component
            $("body").on("mouseup", this.onGlobalMouseup).on("mousemove", this.onGlobalMousemove);
        },

        //Add selected attributes to model
        postProcessChunkModelItem: function(item, i) {
            ScalableList.prototype.postProcessChunkModelItem.call(this, item, i);
            item.selected = this.model.selectionContains(item.value, item.index);
        },

        /*-------------------------
         * Event handlers
         -------------------------*/

        // Handle on mousedown event on list items
        // decide whether to do single or multiselection
        onMousedown: function(event) {
            if (this.selection.allowed && !this.getDisabled() && event.which === 1) {

                if (this.selection.multiple) {
                    //rising a flag for selection via mouse move or scrolling
                    this.leftMouseButtonPressed = true;
                    this.mouseDownPos = this._getMousePos(event);
                }

                var itemData = this._getDomItemData(event.currentTarget);

                this.selection.multiple
                    ? this._multiSelect(event, itemData.value, itemData.index)
                    : this._singleSelect(event, itemData.value, itemData.index);
            }
        },

        // Handle on mousemove event on list items
        onMousemove: function(event) {
            if (this.selection.allowed && this.selection.multiple && !this.getDisabled() && this.leftMouseButtonPressed) {
                //Stopping manual scroll handler
                this._stopAutoScroll();

                if (this._mouseMoved(event, this.mouseDownPos)) {
                    this.mouseDownPos = this._getMousePos(event);

                    var itemData = this._getDomItemData(event.currentTarget);

                    if (!this.model.selectionContains(itemData.value, itemData.index)) {
                        this.model.addRangeToSelection(itemData.value, itemData.index);
                    }
                }
            }

            //event.stopPropagation();
        },

        //Global mouse up handler used handle case when mouse up was done outside the component
        onGlobalMouseup: function(event) {
            //Releasing a flag
            if (this.leftMouseButtonPressed) {
                this.leftMouseButtonPressed = false;
                delete this.mouseDownPos;

                this._stopAutoScroll();

                if (this.selectionChanged) {
                    this._triggerSelectionChanged();
                    this.selectionChanged = false;
                }
            }
        },

        //Global mousedown handler used to allow scroll of the component then mouse is outside the component
        onGlobalMousemove: function(event) {
            if (this.selection.allowed && this.selection.multiple && !this.getDisabled() && this.leftMouseButtonPressed) {

                //Used to prevent autoscroll if mouseup was down outside of the browser.
                //Unfortunately in IE8 event.which can sometimes return 0
                //even if mouse button is in fact pressed
                //if (event.which !== 1) {
                //    this.onGlobalMouseup(event);
                //    return;
                //}

                this.mousePosY = event.clientY;

                if (!this.scrollInterval) {
                    this.scrollInterval = setInterval(this._autoScroll, this.manualScrollInterval);
                }
            }
        },

        clearSelection: function() {
            this.$el.find("li.selected").removeClass("selected");

            this.selectionChanged = true;
        },

        selectValue: function(selection) {
            this.$el.find("li[data-index='" + selection.index + "']:not(.selected)").addClass("selected");

            if (this.selection.multiple) {
                this.selectionChanged = true;
            } else {
                this._triggerSelectionChanged();
            }
        },

        selectRange: function(options) {
            var that = this;

            var visibleRangeStart = Math.max(this.model.get("bufferStartIndex"), options.start);
            var visibleRangeEnd = Math.min(this.model.get("bufferEndIndex"), options.end);

            this.$el.find("li:not(.selected)").each(function() {
                var $item = $(this);
                var index = parseInt($item.attr("data-index"), 10);

                if (index >= visibleRangeStart && index <= visibleRangeEnd) {
                    var item = that.model.get("items")[index - that.model.get("bufferStartIndex")];

                    if (that.model.selectionContains(item.value, index)) {
                        $item.addClass("selected");
                    }
                }
            });

            this.selectionChanged = true;
        },

        deselectValue: function(selection) {
            this.$el.find("li[data-index='" + selection.index + "'].selected").removeClass("selected");

            if (this.selection.multiple) {
                this.selectionChanged = true;
            } else {
                this._triggerSelectionChanged();
            }
        },

        /* Internal helper methods */

        //Forces scrolling of items in the component if browser does not supports this
        _autoScroll: function() {
            var viewPortTop = this.$el.offset().top;
            var scrollTop = this.$el.scrollTop();
            var scrollPos = typeof this.scrollTop !== "undefined" ? this.scrollTop : scrollTop;

            var newScrollPos;
            var shouldScroll = false;

            if (this.mousePosY < viewPortTop) {
                //scroll up on 3 items
                newScrollPos = scrollPos - this.itemHeight * 3;
                shouldScroll = newScrollPos < scrollTop;
            } else if (this.mousePosY > viewPortTop + this.viewPortHeight) {
                //scroll down on 3 items
                newScrollPos = scrollPos + this.itemHeight * 3;
                shouldScroll = newScrollPos > scrollTop;
            }

            if (shouldScroll) {
                this.$el.scrollTop(newScrollPos);
                this.scrollTop = this.$el.scrollTop();
                this._fetchVisibleData();
            }

            this._selectRangeOnAutoScroll(scrollPos, newScrollPos);
        },

        //Fire range selection after scrolling
        _selectRangeOnAutoScroll: function(oldScrollPos, newScrollPos) {
            var index;
            if (oldScrollPos < newScrollPos) {
                index = this._getVisibleItems().bottom;
            } else if (oldScrollPos > newScrollPos) {
                index = this._getVisibleItems().top;
            }

            if (index) {
                var item = this.model.get("items")[index - this.model.get("bufferStartIndex")];

                if (item) {
                    if (!this.model.selectionContains(item.value, index)) {
                        this.model.addRangeToSelection(item.value, index);
                    }
                }
            }
        },

        //Resets autoscrolling
        _stopAutoScroll: function() {
            if (this.scrollInterval) {
                clearInterval(this.scrollInterval);
                this.scrollInterval = undefined;
                this.scrollTop = undefined;
            }
        },

        _multiSelect: function(event, value, index) {
            if (event.shiftKey) {
                this.model.addRangeToSelection(value, index);
            } else if (event.ctrlKey || event.metaKey) {
                this._singleSelect(event, value, index);
            } else {
                this.model.toggleSelection(value, index);
            }
        },

        _singleSelect: function(event, value, index) {
            this.model.clearSelection().addValueToSelection(value, index);
        },

        _triggerSelectionChanged: function() {
            this.trigger("selection:change", this.getValue());
        },

        _getDomItemData: function(item) {
            var $item = $(item);

            var index = parseInt($item.attr("data-index"), 10);
            var item = this.model.get("items")[index - this.model.get("bufferStartIndex")];

            return {
                value: item.value,
                index: index
            }
        },

        _mouseMoved: function(event, mousePos) {
            return Math.abs(mousePos.x - event.clientX) + Math.abs(mousePos.y - event.clientY) >= MOUSE_MOVE_MIN_PIXELS;
        },

        _getMousePos: function(event) {
            return {
                x: event.clientX,
                y: event.clientY
            };
        },

        /*-------------------------
         * API
         -------------------------*/

        getValue: function() {
            return this.model.getSelection();
        },

        setValue: function(selection, options) {
            options = options || {};
            if ((!options || !options.silent)
                && (!options.modelOptions || !options.modelOptions.silent)) {
                this.model.once("selection:change", this._triggerSelectionChanged, this);
            }

            options = options.modelOptions;
            this.model.select(selection, options);
            return this;
        },

        selectAll: function(options) {
            if (!options || !options.silent) {
                this.model.once("selection:change", this._triggerSelectionChanged, this);
            }
            this.model.selectAll();
            return this;
        },

        selectNone: function(options) {
            return this.setValue({}, options);
        },

        invertSelection: function(options) {
            if (!options || !options.silent) {
                this.model.once("selection:change", this._triggerSelectionChanged, this);
            }
            this.model.invertSelection();
            return this;
        },

        setDisabled: function(disabled) {
            this.disabled = disabled;
            this.disabled ? this.$el.addClass("disabled") : this.$el.removeClass("disabled");
            return this;
        },

        getDisabled: function() {
            return this.disabled;
        },

        remove: function() {
            $("body").off("mouseup", this.onGlobalMouseup).off("mousemove", this.onGlobalMousemove);
            return ScalableList.prototype.remove.call(this);
        }
    });

    return ListWithSelection;
});
