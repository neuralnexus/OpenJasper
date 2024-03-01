define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var _namespaceNamespace = require("../namespace/namespace");

var jaspersoft = _namespaceNamespace.jaspersoft;

var Backbone = require('backbone');

var templateEngine = require('./components.templateengine');

var dialogs = require('./components.dialogs');

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
var Dialog = Backbone.View.extend({
  rendered: false,
  contentSelector: '.body',
  events: {
    'click .cancel': 'hide'
  },
  initialize: function initialize(options) {
    this.templateId = options && options.templateId || this.templateId;
    this.contentSelector = options && options.contentSelector || this.contentSelector;

    if (!this.templateId) {
      throw 'Dialog template is not provided';
    }

    _.bindAll(this, "render", "hide", "show", "setContent", "_updateMessage");

    this.options = _.extend({}, options);
  },
  render: function render(parentContainer) {
    this.undelegateEvents();
    this.$el = $(templateEngine.getTemplateText(this.templateId)).closest('div');
    this.el = this.$el[0];
    $(parentContainer ? parentContainer : document.body).append(this.$el);
    this.delegateEvents();
    this.rendered = true;
    return this;
  },
  hide: function hide(event) {
    dialogs.popup.hide(this.el);
    event && event.stopPropagation();
  },
  show: function show(parentContainer) {
    if (!this.rendered) {
      this.render(parentContainer);
    }

    dialogs.popup.show(this.el, this.options.modal);
  },
  setContent: function setContent(content) {
    this.$el.find(this.contentSelector).html($(content));
  },

  /*
       * Replace current dialog messages with given ones
       *
       * @param messages Messages array. Each element of the array will be wrapped with <p>
       * @private
       */
  _updateMessage: function _updateMessage(messages) {
    messages = _.isString(messages) ? [messages] : messages;
    var messageWrapper = document.createDocumentFragment();

    _.each(messages || [], function (message) {
      messageWrapper.appendChild($('<p/>', {
        'text': message,
        'class': 'message'
      })[0]);
    }, this);

    this.$el.find('.body').html(messageWrapper);
  }
});
var ConfirmDialog = Dialog.extend({
  templateId: 'standardConfirmTemplate',
  events: {
    'click button.cancel': 'hide',
    'click button.ok': 'onOk'
  },
  initialize: function initialize(options) {
    Dialog.prototype.initialize.call(this, options);

    _.extend(this, _.defaults(options || {}, {
      messages: '',
      ok: function ok() {}
    }));
  },

  /*
       * Show dialog with the given message and callback for OK action
       *
       * @param options {messages : [""], ok : function(){}};
       */
  show: function show(options) {
    Dialog.prototype.show.call(this);

    this._updateMessage(options.messages || this.messages);

    options.ok && (this.ok = options.ok);
  },
  onOk: function onOk() {
    this.hide();
    this.ok();
  }
}); // //keep that old-school for backwarcompatibility
// jaspersoft || (jaspersoft = { components: {} });
// jaspersoft.components || (jaspersoft.components = {});

jaspersoft.components.Dialog = Dialog;
jaspersoft.components.ConfirmDialog = ConfirmDialog;
module.exports = Dialog;

});