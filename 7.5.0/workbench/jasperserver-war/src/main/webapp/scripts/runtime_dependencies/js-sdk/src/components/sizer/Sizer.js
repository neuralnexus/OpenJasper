define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var sizerTemplate = require("text!./template/sizerTemplate.htm");

var logger = require("../../common/logging/logger");

require('jquery-ui/ui/widgets/resizable');

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
var log = logger.register("Sizer");
module.exports = Backbone.View.extend({
  template: _.template(sizerTemplate),
  el: function el() {
    return this.template();
  },
  initialize: function initialize(options) {
    this.container = options.container;
    this.sizerOptions = _.clone(options);
    delete this.sizerOptions.container;
    this.$container = $(this.container);
    this.render();
  },
  render: function render() {
    _.defaults(this.sizerOptions, {
      minHeight: this.$container.height(),
      handles: {
        s: this.$el
      }
    });

    this.$container.resizable(this.sizerOptions);
    return this;
  },
  show: function show() {
    this.$el.removeClass('jr-isInvisible');
    return this;
  },
  hide: function hide() {
    this.$el.addClass('jr-isInvisible');
    return this;
  },
  updateMinMax: function updateMinMax(boundaries) {
    this.$container.resizable('option', 'minHeight', boundaries.minHeight);
    this.$container.resizable('option', 'maxHeight', boundaries.maxHeight);
  },
  remove: function remove() {
    try {
      this.$container.resizable('destroy');
    } catch (err) {
      log.debug(err);
    }

    Backbone.View.prototype.remove.apply(this, arguments);
  }
});

});