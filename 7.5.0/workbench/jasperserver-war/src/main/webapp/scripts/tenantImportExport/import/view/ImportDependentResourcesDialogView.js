define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var i18n = require("bundle!CommonBundle");

var i18n2 = require("bundle!ImportExportBundle");

var BaseWarningDialogView = require('../../view/BaseWarningDialogView');

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
module.exports = BaseWarningDialogView.extend({
  constructor: function constructor(options) {
    options || (options = {});

    _.extend(options, {
      resizable: true,
      buttons: [{
        label: i18n2['import.button.include'],
        action: 'include',
        primary: true
      }, {
        label: i18n2['import.button.skip'],
        action: 'skip',
        primary: false
      }, {
        label: i18n['button.cancel'],
        action: 'cancel',
        primary: false
      }]
    });

    BaseWarningDialogView.prototype.constructor.call(this, options);
    this.on('button:include', _.bind(this.close, this));
    this.on('button:skip', _.bind(this.close, this));
    this.on('button:cancel', _.bind(this.close, this));
  },
  open: function open(options) {
    _.extend(options, {
      message: i18n2['import.dialog.broken.dependencies.intro']
    });

    BaseWarningDialogView.prototype.open.call(this, options);
  }
});

});