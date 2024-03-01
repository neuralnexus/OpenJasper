define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var SingleSelectListModel = require('../model/SingleSelectListModel');

var ListWithNavigation = require('../../scalableList/view/ListWithNavigation');

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
var SingleSelectList = ListWithNavigation.extend({
  events: _.extend({}, ListWithNavigation.prototype.events, {
    'mouseup li': 'onMouseup'
  }),
  initialize: function initialize(options) {
    var model = options.model || new SingleSelectListModel(options);
    ListWithNavigation.prototype.initialize.call(this, _.extend({
      model: model,
      lazy: true,
      selection: {
        allowed: true,
        multiple: false
      }
    }, options));
  },
  onMouseup: function onMouseup() {
    this.trigger('item:mouseup');
  },
  activate: function activate(index) {
    if (this.getCanActivate()) {
      var active = this.getActiveValue();

      if (active && active.index === index) {
        return;
      }

      this.model.once('selection:change', this._triggerSelectionChanged, this).activate(index);
    }
  }
});
module.exports = SingleSelectList;

});