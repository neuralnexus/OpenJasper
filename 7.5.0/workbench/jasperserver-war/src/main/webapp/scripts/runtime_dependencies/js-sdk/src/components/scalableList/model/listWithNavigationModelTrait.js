define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

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
var listWithNavigationModelTrait = {
  activate: function activate(index, options) {
    if (typeof index !== 'number') {
      this.setActive(_.extend({}, options));
      return;
    }

    options = options || {};

    if (index >= this.get('bufferStartIndex') && index <= this.get('bufferEndIndex')) {
      var item = this.get('items')[index - this.get('bufferStartIndex')];

      if (item) {
        this.setActive(_.extend(options, {
          index: index,
          item: item
        }));
      }
    } else {
      var that = this;
      this.getData({
        offset: index,
        limit: 1
      }).done(function (values) {
        that.setActive(_.extend(options, {
          index: index,
          item: values.data[0]
        }));
      }).fail(this.fetchFailed);
    }
  },
  setActive: function setActive(options) {
    if (this.active && options.index === this.active.index && options.item.value === this.active.value) {
      return;
    }

    this.active = options && typeof options.index === 'number' ? {
      index: options.index,
      value: options.item.value,
      label: options.item.label
    } : undefined;

    if (!options || !options.silent) {
      this.trigger('change');
      this.trigger('active:changed', this.active);
    }
  },
  getActive: function getActive() {
    return this.active;
  }
};
module.exports = listWithNavigationModelTrait;

});