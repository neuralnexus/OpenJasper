define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

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
var DEFAULT_BUFFER_SIZE = 100;
var DEFAULT_LOAD_FACTOR = 0.95;
var ScalableListModel = Backbone.Model.extend({
  initialize: function initialize(options) {
    _.bindAll(this, '_fetchComplete', 'fetchFailed', 'afterFetchComplete', 'fetch');

    this.set({
      items: []
    }, {
      silent: true
    });
    this.getData = options.getData;
    this.bufferSize = options.bufferSize || DEFAULT_BUFFER_SIZE;
    this.loadFactor = options.loadFactor || DEFAULT_LOAD_FACTOR;
  },
  _fetchComplete: function _fetchComplete(values) {
    this.attributes.total = values.total;
    this.set({
      items: values.data
    }, {
      silent: true
    });
    this.attributes.bufferEndIndex = Math.max(0, Math.min(this.attributes.bufferStartIndex + this.bufferSize - 1, this.attributes.bufferStartIndex + values.data.length - 1));

    if (this.attributes.bufferEndIndex < this.attributes.bufferStartIndex + this.bufferSize - 1) {
      this.attributes.total = Math.min(values.total, this.attributes.bufferEndIndex + 1);
    }

    this.afterFetchComplete && this.afterFetchComplete(values.data, this.attributes.total);
    this.trigger('change', this);
  },
  _isBufferReloadNecessary: function _isBufferReloadNecessary(topVisibleItem, bottomVisibleItem) {
    if (typeof this.get('bufferStartIndex') === 'undefined' || typeof this.get('bufferEndIndex') === 'undefined') {
      return true;
    }

    if (topVisibleItem >= 0 && topVisibleItem < this.get('bufferStartIndex') || bottomVisibleItem < this.get('total') && bottomVisibleItem > this.get('bufferEndIndex')) {
      return true;
    }

    if (topVisibleItem === this.get('bufferStartIndex') && bottomVisibleItem === this.get('bufferEndIndex')) {
      return false;
    }

    var topLoadFactor = 1 - (topVisibleItem - this.get('bufferStartIndex')) / this.bufferSize;
    var bottomLoadFactor = 1 - (this.get('bufferEndIndex') - bottomVisibleItem) / this.bufferSize;
    return this.get('bufferStartIndex') > 0 && topLoadFactor >= this.loadFactor || this.get('bufferEndIndex') < this.get('total') && bottomLoadFactor >= this.loadFactor;
  },
  afterFetchComplete: function afterFetchComplete(items, total) {},
  fetchFailed: function fetchFailed(responseStatus, error) {
    this.trigger('fetchFailed', responseStatus, error);
  },
  fetch: function fetch(options) {
    options = _.extend({
      top: this.get('bufferStartIndex') || 0,
      bottom: this.get('bufferEndIndex') || this.bufferSize - 1
    }, options);

    if (options.force || this._isBufferReloadNecessary(options.top, options.bottom)) {
      if (options.top !== this.attributes.bufferStartIndex || options.bottom !== this.attributes.bufferEndIndex || options.force) {
        var bufferCenter = options.top + Math.floor((options.bottom - options.top) / 2);
        var bufferHalf = Math.floor(this.bufferSize / 2);
        this.attributes.bufferStartIndex = Math.max(0, bufferCenter - bufferHalf);

        if (this.get('total') && !options.force) {
          this.attributes.bufferEndIndex = Math.min(this.get('total'), this.attributes.bufferStartIndex + this.bufferSize) - 1;
        } else {
          this.attributes.bufferEndIndex = this.attributes.bufferStartIndex + this.bufferSize - 1;
        }
      }

      this.getData({
        offset: this.get('bufferStartIndex'),
        limit: this.get('bufferEndIndex') - this.get('bufferStartIndex') + 1
      }).done(this._fetchComplete).fail(this.fetchFailed);
    } else {
      this.afterFetchComplete && this.afterFetchComplete(this.get('items'), this.get('total'));
    }
  },
  reset: function reset(options) {
    this.attributes = {};
    this.set({
      items: []
    }, {
      silent: true
    });

    if (!options || !options.silent) {
      this.trigger('change', this);
    }
  }
});
module.exports = ScalableListModel;

});