define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var template = require("text!./template/paginationContentTemplate.htm");

var containerTemplate = require("text!./template/paginationContainerTemplate.htm");

var PaginationModel = require('./model/PaginationModel');

var i18n = require("bundle!CommonBundle");

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
var DEFAULT_SET_OPTIONS = {
  silent: false,
  validate: true
};
module.exports = Backbone.View.extend({
  template: _.template(template),
  el: containerTemplate,
  events: {
    'click button.toLeft.first': 'firstPage',
    'click button.left.prev': 'prevPage',
    'change input.current': 'currentPage',
    'click button.right.next': 'nextPage',
    'click button.toRight.last': 'lastPage'
  },
  constructor: function constructor(options) {
    var options = options || {};
    this.options = _.extend({}, DEFAULT_SET_OPTIONS, _.pick(options, ['silent', 'validate']));
    this.model = new PaginationModel(options, this.options);
    this.listenTo(this.model, 'validated:invalid', this.onError);
    this.listenTo(this.model, 'change', this.render);
    Backbone.View.apply(this, arguments);
  },
  firstPage: function firstPage() {
    this.model.set('current', 1, this.options);
    this.trigger('pagination:change', this.model.get('current'));
  },
  prevPage: function prevPage() {
    var current = this.model.get('current');
    var step = this.model.get('step');
    this.model.set('current', current - step, this.options);
    this.trigger('pagination:change', this.model.get('current'));
  },
  currentPage: function currentPage() {
    this.model.set('current', parseInt(this.$el.find('.current').val()) || this.model.get('current'), this.options);
    this.trigger('pagination:change', this.model.get('current'));
  },
  nextPage: function nextPage() {
    var current = this.model.get('current');
    var step = this.model.get('step');
    this.model.set('current', current + step, this.options);
    this.trigger('pagination:change', this.model.get('current'));
  },
  lastPage: function lastPage() {
    var total = this.model.get('total');
    this.model.set('current', total, this.options);
    this.trigger('pagination:change', this.model.get('current'));
  },
  onError: function onError(model, error) {
    this.trigger('pagination:error', error);
  },
  hide: function hide() {
    this.$el.hide();
  },
  show: function show() {
    this.$el.show();
  },
  resetSetOptions: function resetSetOptions(options) {
    this.options = options ? _.extend({}, DEFAULT_SET_OPTIONS, _.pick(options, ['silent', 'validate'])) : DEFAULT_SET_OPTIONS;
  },
  render: function render() {
    this.$el.html(this.template(_.extend({
      i18n: i18n
    }, this.model.toJSON())));
    return this;
  },
  remove: function remove() {
    Backbone.View.prototype.remove.apply(this, arguments);
  }
});

});