define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var ScalableListModel = require('../model/ScalableListModel');

var defaultChunksTemplate = require("text!../templates/viewPortChunksTemplate.htm");

var defaultItemsTemplate = require("text!../templates/itemsTemplate.htm");

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
var DEFAULT_VIEW_PORT_CHUNK_HEIGHT = 1000000;
var DEFAULT_SCROLL_TIMEOUT = 50;
var DEFAULT_MANUAL_SCROLL_INTERVAL = 50;
var DEFAULT_LIST_ITEM_HEIGHT = 21;
var MIN_ITEM_HEIGHT_TRESHOLD = 2;
var ScalableList = Backbone.View.extend({
  ListModel: ScalableListModel,
  listItemSelector: 'li',
  events: {
    'scroll': 'onScroll',
    'touchmove': 'onScroll'
  },
  attributes: {
    'style': 'overflow-y: auto; height: 100px; min-height: 100px;'
  },
  initialize: function initialize(options) {
    _.bindAll(this, '_fetchVisibleData');

    this.model = options.model || new this.ListModel(options);
    this.chunksTemplate = _.template(options.chunksTemplate || defaultChunksTemplate);
    this.itemsTemplate = _.template(options.itemsTemplate || defaultItemsTemplate);
    this.defaultChunkHeight = this.chunkHeight = options.chunkHeight || DEFAULT_VIEW_PORT_CHUNK_HEIGHT;
    this.scrollTimeout = typeof options.scrollTimeout !== 'undefined' ? options.scrollTimeout : DEFAULT_SCROLL_TIMEOUT;
    this.manualScrollInterval = options.manualScrollInterval || DEFAULT_MANUAL_SCROLL_INTERVAL;
    this.lazy = options.lazy;
    this.defaultItemHeight = options.listItemHeight || DEFAULT_LIST_ITEM_HEIGHT;
    this.listItemSelector = options.listItemSelector || this.listItemSelector;
    this.render();
    this.initListeners();

    if (!this.lazy) {
      this.model.fetch();
    }

    return this;
  },
  initListeners: function initListeners() {
    this.listenTo(this.model, 'change', this.onModelChange, this);
    this.listenTo(this.model, 'fetchFailed', this.onFetchFailed, this);
  },
  render: function render() {
    return this;
  },
  onScroll: function onScroll(event) {
    if (this.scrollTimeout > 0) {
      clearTimeout(this.scrollTimer);
      this.scrollTimer = setTimeout(this._fetchVisibleData, this.scrollTimeout);
    } else {
      this._fetchVisibleData();
    }
  },
  onModelChange: function onModelChange() {
    this.renderData();
  },
  onFetchFailed: function onFetchFailed(responseStatus, error) {
    this.trigger('listRenderError', responseStatus, error);
  },
  postProcessChunkModelItem: function postProcessChunkModelItem(item, i) {
    item.index = this.model.get('bufferStartIndex') + i;

    if (item.label === undefined) {
      item.label = item.value;
    }

    item.label = $.trim(item.label);
  },
  _renderItems: function _renderItems() {
    var bufferStart = this.model.get('bufferStartIndex');
    var bufferEnd = this.model.get('bufferEndIndex');
    var firstChunkWithData = this.itemsPerChunk ? Math.ceil((bufferStart + 1) / this.itemsPerChunk) : 1;
    var lastChunkWithData = this.itemsPerChunk ? Math.ceil((bufferEnd + 1) / this.itemsPerChunk) : 1;
    var that = this;
    this.$el.find('.jr-jViewPortChunk').each(function (index, chunk) {
      var $chunk = $(chunk);

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

      var chunkHtml = chunkModel ? that.itemsTemplate(chunkModel) : '';
      var child = chunk.children[0];
      child && chunk.removeChild(child);
      $chunk.append(chunkHtml);
    });
  },
  _renderViewChunks: function _renderViewChunks(force) {
    var self = this,
        childChunks;

    if (this.totalItems !== this.model.get('total') || force) {
      this.totalItems = this.model.get('total');
      childChunks = Array.prototype.slice.call(this.$el[0].children);
      childChunks.forEach(function (chunk) {
        self.$el[0].removeChild(chunk);
      });
      this.$el.append(this.chunksTemplate({
        _: _,
        chunks: this._getViewChunksModel()
      }));
      this.$firstViewChunk = this.$el.find('.jr-jViewPortChunk:first');
    }
  },
  _getViewChunksModel: function _getViewChunksModel() {
    var totalChunksToShow = this.itemsPerChunk ? Math.ceil(this.totalItems / this.itemsPerChunk) : 1;
    var lastChunkHeight = totalChunksToShow > 0 ? this.itemHeight ? this.totalItems * this.itemHeight - (totalChunksToShow - 1) * this.chunkHeight : this.chunkHeight : 1;
    var viewChunks = [];

    for (var i = 1; i < totalChunksToShow; i++) {
      viewChunks.push({
        height: this.chunkHeight
      });
    }

    viewChunks.push({
      height: lastChunkHeight
    });
    return viewChunks;
  },
  _calcViewPortConstants: function _calcViewPortConstants() {
    if (!this.viewPortConstantsInitialized) {
      this.itemHeight = this.defaultItemHeight;

      if (this.$firstViewChunk) {
        this.itemHeight = this.$el.find(this.listItemSelector + ':first').outerHeight(true);

        if (!this.itemHeight || this.itemHeight <= MIN_ITEM_HEIGHT_TRESHOLD) {
          this.itemHeight = this.defaultItemHeight;
          this.viewPortConstantsInitialized = false;
        } else {
          this.viewPortConstantsInitialized = true;
        }
      }

      this._calcViewPortHeight();

      this.itemsPerChunk = Math.floor(this.defaultChunkHeight / this.itemHeight);
      this.chunkHeight = this.itemsPerChunk * this.itemHeight;

      this._renderViewChunks(true);

      this._renderItems();
    }
  },
  resizeViewPort: function resizeViewPort(viewPortHeight) {
    this.viewPortHeight = viewPortHeight;
    this.$el.outerHeight(this.viewPortHeight);
    this.itemsPerView = Math.floor(this.viewPortHeight / this.itemHeight) - 1;
    this.itemsPerChunk = Math.floor(this.defaultChunkHeight / this.itemHeight);
    this.chunkHeight = this.itemsPerChunk * this.itemHeight;

    this._renderViewChunks(true);

    this._renderItems();
  },
  _calcViewPortHeight: function _calcViewPortHeight() {
    this.viewPortHeight = this.$el.height();
    this.itemsPerView = Math.floor(this.viewPortHeight / this.itemHeight) - 1;
  },
  _getItemsPerView: function _getItemsPerView() {
    this._calcViewPortConstants();

    return this.itemsPerView;
  },
  _getChunkModel: function _getChunkModel(options) {
    var index = options.index;

    if (index < options.firstChunkWithData || index > options.lastChunkWithData) {
      return null;
    }

    var firstItemForChunk = options.itemsPerChunk ? Math.max(options.bufferStart, options.itemsPerChunk * (index - 1)) : options.bufferStart;
    var lastItemForChunk = options.itemsPerChunk ? Math.min(options.bufferEnd, options.itemsPerChunk * index - 1) : options.bufferEnd;
    var top = options.itemHeight ? firstItemForChunk * options.itemHeight - (index - 1) * options.chunkHeight : 0;
    var items = Array.prototype.slice.call(options.model.get('items'), firstItemForChunk - options.bufferStart, lastItemForChunk - options.bufferStart + 1);

    _.each(items, function (item, i) {
      this.postProcessChunkModelItem(item, i + (firstItemForChunk - options.bufferStart));
    }, this);

    return {
      top: top,
      items: items
    };
  },
  _fetchVisibleData: function _fetchVisibleData() {
    var visibleItems = this._getVisibleItems();

    this.model.fetch({
      top: visibleItems.top,
      bottom: visibleItems.bottom
    });
  },
  _getVisibleItems: function _getVisibleItems() {
    var scrollPos = this._getScrollPos();

    var total = this.model.get('total');
    var topVisibleItem = Math.min(total - this.itemsPerView, Math.floor(scrollPos / this.itemHeight));
    var bottomVisibleItem = Math.min(total - 1, topVisibleItem + this.itemsPerView + 1);
    return {
      top: topVisibleItem,
      bottom: bottomVisibleItem
    };
  },
  _getScrollPos: function _getScrollPos() {
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
  renderData: function renderData() {
    if (this.lazy || !this.model.has('bufferStartIndex') || !this.model.has('bufferEndIndex')) {
      this.lazy = null;
      this.model.fetch();
    } else {
      this.trigger('before:render:data');

      this._renderViewChunks();

      this._renderItems();

      this._calcViewPortConstants();

      this.trigger('render:data');
    }

    return this;
  },
  resize: function resize() {
    if (!this.$el.is(':visible')) {
      return;
    }

    var componentHeight = this.$el.outerHeight();

    if (componentHeight === this._componentHeight) {
      return;
    } else {
      this._componentHeight = componentHeight;
    }

    this.viewPortConstantsInitialized = false;

    this._calcViewPortConstants();
  },
  fetch: function fetch(callback, options) {
    this.lazy = false;
    options = options || {};
    var scrollPos;

    if (options.keepPosition) {
      scrollPos = this.$el.get(0).scrollTop;
    } else {
      this.reset({
        silent: true,
        lazy: false
      });
    }

    this.model.once('change', function () {
      if (options.keepPosition) {
        this.$el.scrollTop(scrollPos);
      }

      callback && callback.apply(this, arguments);
    }, this).fetch({
      force: true
    });
  },
  reset: function reset(options) {
    options = _.defaults(options || {}, {
      lazy: true
    });
    this.$el.scrollTop(0);
    this.lazy = options.lazy;
    this.model.reset(options);
  },
  scrollTo: function scrollTo(index) {
    if (typeof index !== 'number') {
      return;
    }

    var scrollPos = this._getScrollPos();

    if (!this.viewPortConstantsInitialized) {
      return;
    }

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
module.exports = ScalableList;

});