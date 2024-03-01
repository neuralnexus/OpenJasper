define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var $ = require('jquery');

var layoutModule = require('../../core/core.layout');

var _componentsListBase = require('../../components/list.base');

var dynamicList = _componentsListBase.dynamicList;

var subDataSourceItemTemplate = require("text!../template/subDataSourceItemTemplate.htm");

var Validation = require("runtime_dependencies/js-sdk/src/common/extension/backboneValidationExtension");

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
    'keyup input[type=\'text\']': 'updateDataSourceId',
    'change input[type=\'text\']': 'updateDataSourceId'
  },
  initialize: function initialize(options) {
    var templateData = this.model.toJSON();
    this._listItem = new dynamicList.UnderscoreTemplatedListItem({
      template: subDataSourceItemTemplate,
      cssClassName: layoutModule.LEAF_CLASS,
      value: templateData,
      tooltipText: this.model.get('uri')
    });
    Validation.bind(this, {
      valid: this.fieldIsValid,
      invalid: this.fieldIsInvalid,
      forceUpdate: true
    });
  },
  updateDataSourceId: function updateDataSourceId(e) {
    var $targetEl = this.$('input[type=\'text\']'),
        valueObj = {};
    valueObj['id'] = $.trim($targetEl.val());
    this.model.set(valueObj);
    this.model.validate(valueObj);

    this._listItem.setValue(this.model.toJSON());
  },
  getListItem: function getListItem() {
    return this._listItem;
  },
  setRootElement: function setRootElement() {
    this.setElement(this._listItem._getElement());
  },
  fieldIsValid: function fieldIsValid(view, attr, selector) {
    var $parentEl = view.$('input[type=\'text\']').parent();
    $parentEl.removeClass('error');
    $parentEl.find('.validatorMessageContainer').removeClass('error');
    $parentEl.find('.message.warning').text('');
  },
  fieldIsInvalid: function fieldIsInvalid(view, attr, error, selector) {
    var $parentEl = view.$('input[type=\'text\']').parent();
    $parentEl.addClass('error');
    $parentEl.find('.validatorMessageContainer').addClass('error');
    $parentEl.find('.message.warning').text(error);
  }
});

});