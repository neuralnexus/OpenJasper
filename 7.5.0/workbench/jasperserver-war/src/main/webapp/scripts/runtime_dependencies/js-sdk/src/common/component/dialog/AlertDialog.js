define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Dialog = require('./Dialog');

var xssUtil = require('../../util/xssUtil');

var alertDialogTemplate = require("text!./template/alertDialogTemplate.htm");

var i18n = require("bundle!CommonBundle");

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
  contentTemplate: _.template(alertDialogTemplate),
  constructor: function constructor(options) {
    options || (options = {});
    Dialog.prototype.constructor.call(this, {
      modal: options.modal !== false,
      message: options.message,
      additionalCssClasses: 'alertDialog ' + (options.additionalCssClasses || ''),
      title: options.title || i18n['dialog.exception.title'],
      buttons: [{
        label: i18n['button.close'],
        action: 'close',
        primary: true
      }]
    }, options);
  },
  initialize: function initialize(options) {
    Dialog.prototype.initialize.apply(this, arguments);
    this.on('button:close', this.close);
    this.setMessage(options.message);
  },
  setMessage: function setMessage(message) {
    message = xssUtil.softHtmlEscape(message, {
      whiteList: ["br"]
    });
    this.content = this.contentTemplate({
      message: message
    });
    var rendered = this.renderContent();
    this.$contentContainer.html(rendered);
  }
});

});