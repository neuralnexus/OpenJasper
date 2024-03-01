define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

var epoxyViewMixin = require('./mixin/epoxyViewMixin');

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
var ViewWithEpoxy = Backbone.View.extend({
  el: function el() {
    return this.template({
      i18n: this.i18n,
      model: this.model.toJSON(),
      options: this.options
    });
  },
  constructor: function constructor(options) {
    options || (options = {});

    if (!options.template) {
      throw new Error('Template must be defined');
    }

    if (!options.model) {
      throw new Error('Model must be defined');
    }

    this.template = _.template(options.template);
    this.model = options.model;
    this.i18n = options.i18n || {};
    this.options = _.omit(options, 'model', 'template', 'i18n');
    Backbone.View.apply(this, arguments);
  },
  initialize: function initialize() {
    this.epoxifyView();
  },
  render: function render() {
    this.applyEpoxyBindings();
    return this;
  },
  remove: function remove() {
    this.removeEpoxyBindings();
    Backbone.View.prototype.remove.apply(this, arguments);
  }
});

_.extend(ViewWithEpoxy.prototype, epoxyViewMixin);

module.exports = ViewWithEpoxy;

});