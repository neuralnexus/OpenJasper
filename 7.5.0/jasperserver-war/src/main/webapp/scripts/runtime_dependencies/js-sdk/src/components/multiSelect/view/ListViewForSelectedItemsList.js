define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

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
var ListViewForSelectedItemsList = ListWithNavigation.extend({
  _multiSelect: function _multiSelect(event, value, index) {
    if ($(event.target).hasClass('jr-mSelectlist-item-delete')) {
      if (this.model.selectionContains(value, index)) {} else {
        this._singleSelect(event, value, index);
      }
    } else {
      ListWithNavigation.prototype._multiSelect.call(this, event, value, index);
    }
  }
});
module.exports = ListViewForSelectedItemsList;

});