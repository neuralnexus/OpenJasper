define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var Dialog = require('./Dialog');

var Validation = require('../../extension/backboneValidationExtension');

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
module.exports = Dialog.extend({
  events: _.extend({
    'keyup input[type=text], textarea, select': 'updateModelProperty',
    'change input[type=text], input:checkbox, textarea, select': 'updateModelProperty'
  }, Dialog.prototype.events),
  initialize: function initialize() {
    this._bindEvents();

    Dialog.prototype.initialize.apply(this, arguments);
  },
  _bindEvents: function _bindEvents() {},
  _unbindEvents: function _unbindEvents() {},
  updateModelProperty: function updateModelProperty(e) {
    var update = {},
        target = $(e.target);

    if (target[0].tagName.toLowerCase() === 'input' && target.attr('type') === 'text' || target[0].tagName.toLowerCase() === 'select' && target.attr('multiple') !== 'true' || target[0].tagName.toLowerCase() === 'textarea') {
      update[target.attr('name')] = $.trim(target.val());
    } else if (target[0].tagName.toLowerCase() === 'input' && target.attr('type') === 'checkbox') {
      update[target.attr('name')] = target.is(':checked');
    }

    this.beforeModelPropertySet && this.beforeModelPropertySet(update);
    this.model.set(update);
    this.model.validate(update);
  },
  bindValidation: function bindValidation() {
    Validation.bind(this, {
      valid: this.fieldIsValid,
      invalid: this.fieldIsInvalid,
      forceUpdate: true,
      selector: 'name'
    });
  },
  unbindValidation: function unbindValidation() {
    Validation.unbind(this);
  },
  fieldIsValid: function fieldIsValid(view, attr, selector) {
    var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
    $parentEl.removeClass('error');
    $parentEl.find('.message.warning').text('');
  },
  fieldIsInvalid: function fieldIsInvalid(view, attr, error, selector) {
    var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
    $parentEl.addClass('error');
    $parentEl.find('.message.warning').text(error.toString());
  },
  validField: function validField(selector) {
    var $parentEl = this.$(selector).parent();
    $parentEl.removeClass('error');
    $parentEl.find('.message.warning').text('');
  },
  invalidField: function invalidField(selector, error) {
    var $parentEl = this.$(selector).parent();
    $parentEl.addClass('error');
    $parentEl.find('.message.warning').text(error.toString());
  },
  clearValidationErrors: function clearValidationErrors() {
    this.$('label').removeClass('error');
    this.$('.message.warning').text('');
  },
  remove: function remove() {
    this.unbindValidation();

    this._unbindEvents();

    Dialog.prototype.remove.apply(this, arguments);
  }
});

});