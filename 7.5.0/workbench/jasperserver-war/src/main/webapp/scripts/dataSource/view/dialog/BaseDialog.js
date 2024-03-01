define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

var $ = require('jquery');

var dialogs = require('../../../components/components.dialogs');

var dialogTemplate = require("text!../../template/dialog/baseDialogTemplate.htm");

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
    'click button.action.primary': 'primaryButtonOnClick',
    'click button.action:not(.primary)': 'secondaryButtonOnClick'
  },
  isModal: true,
  TITLE: '',
  PRIMARY_BUTTON_LABEL: '',
  SECONDARY_BUTTON_LABEL: '',
  el: function el() {
    return _.template(dialogTemplate, {
      title: this.TITLE,
      primaryButtonLabel: this.PRIMARY_BUTTON_LABEL,
      secondaryButtonLabel: this.SECONDARY_BUTTON_LABEL
    });
  },
  initialize: function initialize() {
    $('body').append(this.render().$el);
  },
  show: function show() {
    // TODO: get rid of using "dialogs" module
    dialogs.popup.show(this.el, this.isModal);
  },
  hide: function hide() {
    // TODO: get rid of using "dialogs" module
    dialogs.popup.hide(this.el);
  },
  render: function render() {
    return this;
  },
  primaryButtonOnClick: function primaryButtonOnClick() {
    throw new Error('Method not implemented');
  },
  secondaryButtonOnClick: function secondaryButtonOnClick() {
    this.hide();
  }
});

});