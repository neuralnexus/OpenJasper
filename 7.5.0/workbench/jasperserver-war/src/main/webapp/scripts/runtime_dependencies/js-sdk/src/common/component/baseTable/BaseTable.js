define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Marionette = require('backbone.marionette');

var Tooltip = require('../tooltip/Tooltip');

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
var BaseTable = Marionette.CompositeView.extend({
  initialize: function initialize(options) {
    options = options || {};
    var tooltip = options.tooltip;

    if (tooltip && tooltip.template && tooltip.i18n) {
      this.tooltip = new Tooltip({
        i18n: tooltip.i18n,
        attachTo: this.$el,
        contentTemplate: tooltip.template
      });
    }
  },
  remove: function remove() {
    this.tooltip && this.tooltip.remove();
    Marionette.CompositeView.prototype.remove.apply(this, arguments);
  }
});
module.exports = BaseTable;

});