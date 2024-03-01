define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var ExportView = require('./ExportView');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var exportTypesEnum = require('../enum/exportTypesEnum');

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
    'click #exportButton': 'doExport'
  },
  initialize: function initialize() {
    this.exportView = new ExportView();
    this.exportView.render({
      type: jrsConfigs.isProVersion ? exportTypesEnum.SERVER_PRO : exportTypesEnum.SERVER_CE,
      tenantId: jrsConfigs.isProVersion ? 'organizations' : null
    });
    this.$el.find('.body').append(this.exportView.el);
    this.listenTo(this.exportView.model, 'validated', function (isValid) {
      var $exportButton = this.$('#exportButton'),
          value = isValid ? null : 'disabled';
      $exportButton.attr('disabled', value);
    }, this);
  },
  doExport: function doExport() {
    this.exportView.doExport();
  }
});

});