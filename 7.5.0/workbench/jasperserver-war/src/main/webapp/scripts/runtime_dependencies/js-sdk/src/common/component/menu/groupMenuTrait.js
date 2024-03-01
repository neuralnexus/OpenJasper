define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

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
var groupMenuTrait = {
  _onInitialize: function _onInitialize() {
    var $items = this.$contentContainer.find('li'),
        GROUP_PROPERTY_NAME = 'groupId',
        groupNames = this._getGroupNames(this.collection.models, GROUP_PROPERTY_NAME);

    _.each(groupNames, function (groupName) {
      var $el = $items.filter('[data-' + GROUP_PROPERTY_NAME + '=\'' + groupName + '\']').first();

      if ($el.index()) {
        $el.before('<li class=\'leaf separator\'></li>');
      }
    }, this);
  },
  _getGroupNames: function _getGroupNames(models, groupNameProperty) {
    return _.keys(_.groupBy(models, function (m) {
      return m.get(groupNameProperty);
    }));
  }
};
module.exports = groupMenuTrait;

});