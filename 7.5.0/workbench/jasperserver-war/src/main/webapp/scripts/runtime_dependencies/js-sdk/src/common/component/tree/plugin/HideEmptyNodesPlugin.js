define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var TreePlugin = require('./TreePlugin');

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
module.exports = TreePlugin.extend({
  initialize: function initialize() {
    this.$el.hide();
  },
  itemsRendered: function itemsRendered(model) {
    if (this.$el.hasClass('node')) {
      if (this.$('> .subcontainer > .j-view-port-chunk > ul > li').length) {
        this.$el.show();
      } else {
        this.$el.hide();
      }
    }
  }
});

});