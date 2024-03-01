define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var Backbone = require('backbone');

var i18n = require("bundle!CommonBundle");

var notificationTemplate = require("text!./template/notificationTemplate.htm");

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
var instance;
var NOTIFICATION_TYPES = {
  SUCCESS: 'success',
  WARNING: 'warning'
};
var NOTIFICATION_DEFAULT_DELAY = 2000;
var notificationTypeToCssClassMap = {};
notificationTypeToCssClassMap[NOTIFICATION_TYPES.WARNING] = NOTIFICATION_TYPES.WARNING;
var Notification = Backbone.View.extend({
  template: _.template(notificationTemplate),
  events: {
    'click .close a': 'hide'
  },
  el: function el() {
    return this.template({
      message: this.message,
      i18n: i18n
    });
  },
  initialize: function initialize() {
    this.render();
  },
  render: function render() {
    $('body').append(this.$el);
    this.$el.hide();
    this.$messageContainer = this.$('.notificationMessage > span:first-child');
    return this;
  },
  show: function show(options) {
    options = _.extend({
      type: NOTIFICATION_TYPES.WARNING,
      delay: NOTIFICATION_DEFAULT_DELAY
    }, options);
    this.$messageContainer.text(options.message);
    this.$messageContainer.removeClass().attr({
      'class': notificationTypeToCssClassMap[options.type]
    });
    this.$el.slideDown();
    options.delay && _.delay(_.bind(this.hide, this), options.delay);
    return this;
  },
  hide: function hide() {
    arguments.length && arguments[0].preventDefault && arguments[0].preventDefault();
    this.$el.slideUp();
    return this;
  },
  remove: function remove() {
    Backbone.View.prototype.remove.apply(this, arguments);
  }
}, {
  show: function show() {
    instance || (instance = new Notification());
    return instance.show.apply(instance, arguments);
  },
  hide: function hide() {
    instance || (instance = new Notification());
    return instance.hide.apply(instance, arguments);
  }
});
module.exports = Notification;

});