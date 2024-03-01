define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Marionette = require('backbone.marionette');

var epoxyViewMixin = require('../../../view/mixin/epoxyViewMixin');

var Tooltip = require('../behaviors/TooltipRowBehavior');

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
var BaseRow = Marionette.ItemView.extend({
  behaviors: {
    Tooltip: {
      behaviorClass: Tooltip
    }
  },
  initialize: function initialize() {
    this.epoxifyView();
  },
  render: function render() {
    Marionette.ItemView.prototype.render.apply(this, arguments);
    this.applyEpoxyBindings();
    return this;
  },
  remove: function remove() {
    this.removeEpoxyBindings();
    Marionette.ItemView.prototype.remove.apply(this, arguments);
  }
});

_.extend(BaseRow.prototype, epoxyViewMixin);

module.exports = BaseRow;

});