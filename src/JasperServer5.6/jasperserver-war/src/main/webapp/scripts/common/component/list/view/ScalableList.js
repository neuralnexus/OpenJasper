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
 * @version: $Id: ScalableList.js 47820 2014-08-05 13:30:41Z sergey.prilukin $
 */

/**
 * List component which supports view port
 *
 * Usage:
 *
 *    var list = new jaspersoft.components.ScalableList({
 *        el: <DOM element or it's jQuery wrapper which will be used to render list component>,
 *        * getData: <function to retrieve data> @see description in ScalableListModel,
 *        lazy: <true|false> - whether list items will be generated lazily, after calling renderData or immediately
 *        * itemsTemplate: <text of template for rendering items>,
 *        chunksTemplate: <text of template for renering view chunks>
 *        scrollTimeout: <timeout in millis then data fetching can occurs after scroll stopped>
 *        listItemHeight: <default height of list item in pixels>
 *        model: <alternative model>,
 *        bufferSize: <size of the buffer>,
 *        loadFactor: <load factor>
 *    });
 *
 *    * - required items
 *
 * ScalableList API:
 *      render                      - renders view. Rendering occurred immediately after
 *                                    initialization
 *      renderData                  - renders list items. can be called manually if lazy loading was used,
 *                                    also it happens automatically on data fetching (like during scroll)
 *      resize                      - call this method then height of list element was changed
 *                                    to recalculate dimensions which is necessary for viewport
 *      fetch(callback)             - forces list to fetch fresh data, and then calls callback if present
 *      remove                      - unbind all events and remove element from the DOM
 *
 *
 *
 * HTML templates (tips)
 *
 *
 * ViewPortChunks template example (usually it's not necessary to override it so this template is loaded as a dependency):
 *
 * {{#chunks}}
 *    <div class="viewPortChunk" style="height: {{height}}px"></div>
 * {{/chunks}}
 *
 *
 * List items template example:
 *
 * <ul class="list" style="top: {{top}}px; position: relative;">
 *   {{#items}}
 *     <li class="leaf {{#selected}}selected{{/selected}}" data-index="{{index}}">
 *       <div>{{label}}</div>
 *     </li>
 *   {{/items}}
 * </ul>
 *
 * In order to correctly show list its root element (el property passed to constructor)
 * should have fllowing styles assigned in any way (either via inline styles or via css):
 *
 *      overflow-y :    auto;         // to show scrollbar
 *      height     :    some_height;  //so overflow-y will take effect
 *
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        ScalableListModel = require("common/component/list/model/ScalableListModel"),
        defaultChunksTemplate = require("text!common/component/list/templates/viewPortChunksTemplate.htm");
        require("core.layout");

    // ListView constants with default values
    var DEFAULT_VIEW_PORT_CHUNK_HEIGHT = 1000000; //default height in pixels of viewPort (this is max value for IE)
    var DEFAULT_SCROLL_TIMEOUT = 50; //scroll activation timeout in millis
    var DEFAULT_MANUAL_SCROLL_INTERVAL = 50; //interval in millis for timer which will scroll view port
    var DEFAULT_LIST_ITEM_HEIGHT = 20; //default height of list item

    /**
     View for View Port List component.
     It's purpose is to render items and react on UI events,
     like scroll click, etc.

     this View implementation look for following options in hash provided to constructor:
     * el            - DOM element or it's jQuery wrapper where component will be rendered
     model           - custom model, see description for default ScalableListModel about implementation details.
     itemsTemplate   - template of list of items. should follow default itemsTemplate pattern.
     chunkHeight     - height of view port chunk. Usually not necessary to set this value.
     scrollTimeout   - scroll timeout when actual data fetching may happen.

     * - required elements.

     also it passes options hash to default model if not custom model was used, so look also at ScalableListModel description
     for additional options hash elements.

     Handy prototype properties:
     ListModel - default model which could be used to extend default model functionality
     */
    var ScalableList = Backbone.View.extend({

        //Default list model,
        ListModel: ScalableListModel,

        events: {
            "scroll": "onScroll",
            "touchmove": "onScroll"
        },

        /*
         Main init method
         */
        initialize: function(options) {
            _.bindAll(this, "_fetchVisibleData");

            //Set up instance variables from passed options

            this.model = options.model || new this.ListModel(options);
            this.chunksTemplate = _.template(options.chunksTemplate || defaultChunksTemplate);
            this.itemsTemplate = _.template(options.itemsTemplate);
            if (!this.itemsTemplate) {
                throw "itemsTemplate is a required attribute. Please see doc for ScalableList.js component";
            }

            this.defaultChunkHeight = this.chunkHeight = options.chunkHeight || DEFAULT_VIEW_PORT_CHUNK_HEIGHT;
            this.scrollTimeout = options.scrollTimeout || DEFAULT_SCROLL_TIMEOUT;
            this.manualScrollInterval = options.manualScrollInterval || DEFAULT_MANUAL_SCROLL_INTERVAL;
            this.lazy = options.lazy;
            this.defaultItemHeight = options.listItemHeight || DEFAULT_LIST_ITEM_HEIGHT;

            //attach listeners
            this.initListeners();

            //Renders container
            this.render();

            //Fire data loading and thus rendering
            //use all buffer size as bottom.
            //Only necessary amount of data will be loaded thus this is safe
            if (!this.lazy) {
                this.model.fetch();
            }

            return this;
        },

        /*
         Bind event listeners on model
         */
        initListeners: function() {
            this.listenTo(this.model, "change", this.onModelChange, this);
        },

        /*
         Main render method.
         Renders component container but not data
         */
        render: function() {
            return this;
        },

        /*-------------------------
         * Event handlers
         -------------------------*/

        /*
         Listen for scroll events with some useful timeout
         to about unusual data fetching
         */
        onScroll: function(event) {
            if (this.scrollTimeout > 0) {
                clearTimeout(this.scrollTimer);

                this.scrollTimer = setTimeout(this._fetchVisibleData, this.scrollTimeout);
            } else {
                this._fetchVisibleData();
            }
        },

        /*
            Listen for model's change event
         */
        onModelChange: function() {
            this.renderData();
        },

        /*-------------------------
         * Methods to be overridden
         -------------------------*/

        postProcessChunkModelItem: function(item, i) {
            item.index = this.model.get("bufferStartIndex") + i;
            if (item.label == undefined) {
                item.label = item.value;
            }
        },

        /*-------------------------
         * Internal helper methods
         -------------------------*/

        /*
         Does actual rendering of items but only those which are currently in buffer
         */
        _renderItems: function() {
            var bufferStart = this.model.get("bufferStartIndex");
            var bufferEnd = this.model.get("bufferEndIndex");
            var firstChunkWithData = this.itemsPerChunk ? Math.ceil((bufferStart + 1) / this.itemsPerChunk) : 1;
            var lastChunkWithData = this.itemsPerChunk ? Math.ceil((bufferEnd + 1) / this.itemsPerChunk) : 1;
            var that = this;

            this.$el.find(".viewPortChunk").each(function(index, chunk) {

                //Calculate chunksModel for this chunk
                var chunkModel = that._getChunkModel({
                    index: index + 1,
                    firstChunkWithData: firstChunkWithData,
                    lastChunkWithData: lastChunkWithData,
                    bufferStart: bufferStart,
                    bufferEnd: bufferEnd,
                    total: that.totalItems,
                    itemsPerChunk: that.itemsPerChunk,
                    itemHeight: that.itemHeight,
                    chunkHeight: that.chunkHeight,
                    model: that.model
                });

                //Get html for this chunk or just clear it if not elements should be rendered in this chunk
                var chunkHtml = chunkModel ? that.itemsTemplate(chunkModel) : "";

                $(chunk).html(chunkHtml);
            });
        },

        /*
         This method does rendering of viewPort chunks.
         Should re-render them if total amount of data was changed
         */
        _renderViewChunks: function(force) {
            if (this.totalItems !== this.model.get("total") || force) {
                this.totalItems = this.model.get("total");

                this.$el.html(this.chunksTemplate({chunks: this._getViewChunksModel()}));
                this.$firstViewChunk = this.$el.find(".viewPortChunk:first");
            }
        },

        /*
         Calculates model for rendering viewPort chunks,
         namely count of chunks and height for each viewPort chunk
         */
        _getViewChunksModel: function() {
            var totalChunksToShow = this.itemsPerChunk ? Math.ceil(this.totalItems / this.itemsPerChunk) : 1;
            var lastChunkHeight = totalChunksToShow > 0
                ? (this.itemHeight ? this.totalItems * this.itemHeight -
                (totalChunksToShow - 1) * this.chunkHeight : this.chunkHeight)
                : 1; //If no chunks to show we still should show one empty chunk with 1px height to fix rendering issues

            var viewChunks = [];
            for (var i = 1; i < totalChunksToShow; i++) {
                viewChunks.push({height: this.chunkHeight});
            }

            //Last viewPort chunk probably will have different height.
            viewChunks.push({height: lastChunkHeight});

            return viewChunks;
        },

        /*
         Calculates constants which are necessary to render items,
         namely:
         - height of one item
         - number of items which will be visible
         */
        _calcViewPortConstants: function() {
            if (!this.viewPortConstantsInitialized) {

                this.itemHeight = this.defaultItemHeight;

                if (this.$firstViewChunk) {
                    //item height could be calculated only after it is present in DOM and visible
                    //this.itemHeight = this.$firstViewChunk.find("li:first").height();
                    this.itemHeight = this.$el.find("li:first").height();

                    if (!this.itemHeight) {
                        //Ok, we still can not find height of one item, not a problem, use default value
                        this.itemHeight = this.defaultItemHeight;
                        this.viewPortConstantsInitialized = false;
                    } else {
                        this.viewPortConstantsInitialized = true;
                    }
                }

                //calc viewPortHeight
                this._calcViewPortHeight();

                //Calibrate canvas chunk height so even number of items will fit into one chunk
                this.itemsPerChunk = Math.floor(this.defaultChunkHeight / this.itemHeight);
                this.chunkHeight = this.itemsPerChunk * this.itemHeight;

                //We have to rerender list with new dimensions
                this._renderViewChunks(true);
                this._renderItems();
            }
        },

        _calcViewPortHeight: function() {
            //calc viewPortHeight
            this.viewPortHeight = this.$el.height();

            //Calc number of items which will be visible
            this.itemsPerView = Math.floor(this.viewPortHeight / this.itemHeight) - 1;
        },

        _getItemsPerView: function() {
            this._calcViewPortConstants();

            return this.itemsPerView;
        },

        /*
         Returns model for specified viewPort chunk,
         or null if no items should be rendered in this chunk
         */
        _getChunkModel: function(options) {
            var index = options.index;

            //Nothing to render in this chunk
            if (index < options.firstChunkWithData || index > options.lastChunkWithData) {
                return null;
            }

            //Put only one item in model when itemsPerChunk is not yet calculated
            var firstItemForChunk = options.itemsPerChunk
                ? Math.max(options.bufferStart, options.itemsPerChunk * (index - 1)) : options.bufferStart;
            var lastItemForChunk = options.itemsPerChunk
                ? Math.min(options.bufferEnd , options.itemsPerChunk * index - 1) : options.bufferEnd;

            var top = options.itemHeight ? firstItemForChunk * options.itemHeight -
                (index - 1) * options.chunkHeight : 0;
            var items = Array.prototype.slice.call(options.model.get("items"), firstItemForChunk -
                options.bufferStart, lastItemForChunk - options.bufferStart + 1);

            _.each(items, function(item, i) {
                this.postProcessChunkModelItem(item, i + (firstItemForChunk - options.bufferStart));
            }, this);

            return {top: top, items: items};
        },

        /*
         Does actual scroll calculations
         and call data fetching if necessary
         */
        _fetchVisibleData: function() {
            var visibleItems = this._getVisibleItems();

            this.model.fetch({
                top: visibleItems.top,
                bottom: visibleItems.bottom
            });
        },

        /*
         Returns top and bottom items which are now visible
         */
        _getVisibleItems: function() {
            var scrollPos = this._getScrollPos();

            var total = this.model.get("total");
            var topVisibleItem = Math.min(total - this.itemsPerView, Math.floor(scrollPos / this.itemHeight));
            var bottomVisibleItem = Math.min(total, topVisibleItem + this.itemsPerView);

            return {
                top: topVisibleItem,
                bottom: bottomVisibleItem
            }
        },

        _getScrollPos: function() {
            //If dimensions were not calculated properly on renderData
            //calculate them now
            this._calcViewPortConstants();

            var scrollPos = 0;

            if (this.$firstViewChunk) {
                var elOffset = this.$el.offset();
                var viewChunkOffset = this.$firstViewChunk.offset();

                if (elOffset && viewChunkOffset) {
                    scrollPos = elOffset.top - viewChunkOffset.top;
                }
            }

            return scrollPos;
        },

        /*-------------------------
         * API
         -------------------------*/

        /*
         Method to render list data
         Should be called then model is already filled with data
         */
        renderData: function() {
            if (this.lazy) {
                //We have to fetch data here on lazy init
                this.lazy = null;
                this.model.fetch();
            } else {
                this._renderViewChunks();
                this._renderItems();
                this._calcViewPortConstants();
            }

            return this;
        },

        /*
         Used to unbind events in case if this instance doesn't needed anymore
         */

        /*
         This method should be called then height of component was changed.
         It recalculates dimensions and re-renders itself.
         */
        resize: function() {
            this.viewPortConstantsInitialized = false;
            this._calcViewPortConstants();
        },

        /*
         Forces data fetching and thus re-rendering
         */
        fetch: function(callback) {
            //We do not know whether we will receive same data
            //or no so we have to scroll up
            this.$el.scrollTop(0);
            this.model.once("change", callback).fetch({force: true});
        },

        /*
         Forces resetting list to the initial state.
         */
        reset: function(options) {
            //We do not know whether we will receive same data
            //or no so we have to scroll up
            this.$el.scrollTop(0);
            //restore lazy value
            this.lazy = true;
            this.model.reset(options);
        },

        /*
         Scrolls to list item with given index if it's not visible
         */
        scrollTo: function(index) {
            if (typeof index !== "number") {
                return;
            }

            var scrollPos = this._getScrollPos();
            if (!this.viewPortConstantsInitialized) {
                //could not scroll to element properly
                return;
            }

            //We have to refresh view port height every time before scrolling
            this._calcViewPortHeight();

            var newTopVisibleItemPos = null;

            var itemPos = index * this.itemHeight;
            if (itemPos < scrollPos) {
                newTopVisibleItemPos = itemPos;
            } else if (itemPos + this.itemHeight > scrollPos + this.viewPortHeight) {
                newTopVisibleItemPos = itemPos + this.itemHeight - this.viewPortHeight;
            }

            if (newTopVisibleItemPos !== null) {
                this.$el.scrollTop(newTopVisibleItemPos);
            }
        }
    });

    return ScalableList;
});
