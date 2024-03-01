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
module.exports = Backbone.View.extend({
  events: {
    'click': 'select',
    'mouseover': 'mouseover',
    'mouseout': 'mouseout'
  },
  constructor: function constructor(options) {
    if (!options || !options.template) {
      throw new Error('Option should have defined template');
    }

    if (!options.model || !(options.model instanceof Backbone.Model)) {
      throw new Error('Option should have associated Backbone.Model');
    }

    this.template = _.template(options.template);
    this.disabledClass = options.disabledClass;
    this.hiddenClass = options.hiddenClass;
    this.toggleClass = options.toggleClass;
    this.overClass = options.overClass || 'over';
    Backbone.View.apply(this, arguments);
  },
  initialize: function initialize() {
    Backbone.View.prototype.initialize.apply(this, arguments);
    var self = this;

    if (this.model.get('disabled') === true) {
      this.disable();
    }

    if (this.model.get('hidden') === true) {
      this.hide();
    }

    if (this.model.get('selected') === true) {
      this.addSelection();
    }

    this.listenTo(this.model, 'change:over', function (model, value) {
      if (value) {
        self.$('> p').addClass(self.overClass);
      } else {
        self.$('> p').removeClass(self.overClass);
      }
    });
  },
  el: function el() {
    return this.template(this.model.toJSON());
  },
  enable: function enable() {
    this.$el.removeAttr('disabled').removeClass(this.disabledClass);
  },
  disable: function disable() {
    this.$el.attr('disabled', 'disabled').addClass(this.disabledClass);
  },
  over: function over() {
    this.model.set({
      over: true
    });
  },
  leave: function leave() {
    this.model.set({
      over: false
    });
  },
  show: function show() {
    this.$el.show().removeClass(this.hiddenClass);
  },
  hide: function hide() {
    this.$el.hide().addClass(this.hiddenClass);
  },
  isVisible: function isVisible() {
    return this.$el.is(':visible');
  },
  isDisabled: function isDisabled() {
    return this.$el.is(':disabled');
  },
  select: function select(ev) {
    this.model.trigger('select', this, this.model, ev);
    this.trigger('click', this, this.model, ev);
  },
  addSelection: function addSelection() {
    this.$el.addClass(this.toggleClass);
    this.model.set('selected', true);
  },
  removeSelection: function removeSelection() {
    this.$el.removeClass(this.toggleClass);
    this.model.unset('selected');
  },
  mouseover: function mouseover(ev) {
    this.trigger('mouseover', this, this.model, ev);
  },
  mouseout: function mouseout(ev) {
    this.trigger('mouseout', this, this.model, ev);
  }
});

});