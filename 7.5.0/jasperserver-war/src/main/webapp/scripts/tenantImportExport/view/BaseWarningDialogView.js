define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Dialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/Dialog");

var i18n = require("bundle!ImportExportBundle");

var template = require("text!../import/template/dependentResourcesDialogTemplate.htm");

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

/**
 * @author: Olesya Bobruyko
 * @version:
 */
module.exports = Dialog.extend({
  events: {
    "resize": "onResizeHeight"
  },
  onResizeHeight: function onResizeHeight() {
    this.$contentContainer.height(this.$el.height() - this._resizableContainerShiftHeight);
    this.$(".control.groupBox").css("min-height", this.$contentContainer.height() - this.$(".message").outerHeight(true));
  },
  constructor: function constructor(options) {
    options = options || {};
    this.options = options;
    Dialog.prototype.constructor.call(this, {
      modal: true,
      resizable: options.resizable,
      minWidth: options.minWidth,
      minHeight: options.minHeight,
      additionalCssClasses: options.additionalCssClasses || "dependent-resources-dialog jr-uWidth-725px jr-uHeight-500px",
      title: options.title || i18n["dialog.broken.dependencies.title"],
      content: "",
      buttons: options.buttons
    });
    this.template = _.template(options.template || template);
  },
  open: function open(options) {
    this.setContent(this.template(_.defaults(options, {
      message: ""
    })));
    Dialog.prototype.open.apply(this, arguments); // refreshes the previous scroll position to default one.

    this.$contentContainer.scrollTop(0);
    var a = this.$(".jr-mDialog-footer").outerHeight();
    var b = this.$(".jr-mDialog-header").outerHeight();
    var c = this.$contentContainer.outerHeight();
    var d = this.$contentContainer.height();
    this._resizableContainerShiftHeight = a + b + c - d;
    this.onResizeHeight();
  }
});

});