define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var abstractPanelTrait = require('./abstractPanelTrait');

var ClickMenu = require('../../menu/ClickMenu');

var groupMenuTrait = require('../../menu/groupMenuTrait');

var menuPanelMarkup = require("text!../template/menuPanelTemplate.htm");

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
var GroupMenu = ClickMenu.extend(groupMenuTrait);
module.exports = _.extend({}, abstractPanelTrait, {
  onConstructor: function onConstructor(options) {
    options || (options = {});
    this.menuOptions = options.menuOptions;
    this.menuOptionSelectable = options.menuOptionSelectable;
    this.menuPadding = options.menuPadding;
    this.menuToggleMode = options.menuToggleMode;
  },
  afterSetElement: function afterSetElement() {
    this.$menuEl = $(menuPanelMarkup);
    this.$el.find('.title').after(this.$menuEl);
    this.filterMenu = new GroupMenu(this.menuOptions, this.$menuEl, {
      toggle: this.menuOptionSelectable,
      toggleClass: 'active',
      padding: this.menuPadding,
      menuToggleMode: this.menuToggleMode
    });
    this.listenTo(this.filterMenu, 'all', function (name, view, model) {
      if (name.indexOf(this.filterMenu.contextName) >= 0) {
        this.filterMenu.hide();
        this.trigger(name, view, model);
      }
    }, this);
  },
  onRemove: function onRemove() {
    this.filterMenu.remove();
  }
});

});