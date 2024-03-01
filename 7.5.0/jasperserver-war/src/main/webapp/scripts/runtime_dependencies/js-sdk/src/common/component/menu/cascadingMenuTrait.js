define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Menu = require('./Menu');

var HoverMenu = require('./HoverMenu');

var AttachableMenu = require('./AttachableMenu');

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

/**
 * @author: Andriy Godovanets, Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

/**
 * @mixin cascadingMenuTrait
 * @description Object that extends any Menu component with options cascade. Adds support of "children" property for "options" param.
 * @example
 *  var CascadingContextMenu = ContextMenu.extend(cascadingMenuTrait);
 *
 *  var menu = new CascadingContextMenu([
 *      { label: "Remove Summary", action: "removeSummary" },
 *      { label: "Change Function", children: [
 *          { label : "Distinct Count", action: "distinctCount" },
 *          { label : "Count All", action: "countAll" }
 *      ]
 *      }
 *  ], "#someElement", { toggle: true });
 */
var cascadingMenuTraitConstr = function cascadingMenuTraitConstr(options, attachTo, padding, additionalSettings) {
  _.bindAll(this, "_onAttachToMouseOver", "_onAttachToMouseOut");

  this.padding = padding || {
    top: 0,
    left: 0
  };
  AttachableMenu.call(this, options, attachTo, this.padding, additionalSettings); // listen to synthetic events from OptionContainer which we inherit from

  this.on("mouseover container:mouseover", this._onMenuItemMouseOver);
  this.on("container:mouseout", this._onMenuItemMouseOut);
  this.on("mouseout", this._onCascadeMenuItemMouseOut);
  this.on("selectionMade", this._hide);
};

var cascadingMenuTrait = {
  _subMenuHovered: false,
  _onConstructor: function _onConstructor(options, additionalOptions) {
    this.additionalOptions = additionalOptions;
  },

  /**
   * @description initializes mouseover handler. Calls _showSubMenu.
   * @memberof cascadingMenu
   * @access protected
   */
  _onInitialize: function _onInitialize() {
    this.on("mouseover", _.bind(this._showSubMenu, this));
  },

  /**
   * @description show sub menu on option mouse over.
   * @memberof cascadingMenuTrait
   * @access protected
   */
  _showSubMenu: function _showSubMenu(menuItem) {
    if (menuItem.model.has("children")) {
      var subView = this._getSubMenu(menuItem.cid, menuItem.model.get("children"), menuItem, this);

      subView && subView.show();
    }
  },
  _onCascadeMenuItemMouseOut: function _onCascadeMenuItemMouseOut(menuItem, _ignored, ev) {
    var children = menuItem.model.get("children");
    this._menuHovered = false;

    if (children) {
      var subView = this._getSubMenu(menuItem.cid),
          subViewOffset = subView.$el.offset(),
          posX = ev.pageX - subViewOffset.left,
          posY = ev.pageY - subViewOffset.top;

      this._subMenuHovered = posX >= 0 && posX <= subView.$el.width() + 3 && posY >= 0 && posY <= subView.$el.height() + 3;
    }

    this._hideByTimeout();
  },
  _initCascadingMenuEvents: function _initCascadingMenuEvents(subMenu) {
    var self = this;
    this.listenTo(subMenu.collection, "select", function (view, model, ev) {
      self._selectOption(view, model, ev);
    });
    this.listenTo(subMenu, "hidden", function () {
      self._subMenuHovered = false;

      self._tryHide();
    });
  },

  /**
   * @description tried to hide menu after some timeout
   * @access protected
   */
  _hideByTimeout: function _hideByTimeout() {
    if (this._elementHovered || this._menuHovered || this._subMenuHovered) {
      return;
    }

    setTimeout(_.bind(this._tryHide, this), this.TIME_BETWEEN_MOUSE_OVERS);
  },

  /**
   * @description tries to hide menu.
   * @access protected
   */
  _tryHide: function _tryHide() {
    if (this._elementHovered || this._menuHovered || this._subMenuHovered) {
      return;
    }

    this._hide();
  },

  /**
   * @description returns new SubMenu instance.
   * @access protected
   * @memberof cascadingMenuTrait
   * @returns {SubMenu}
   */
  _getSubMenu: _.memoize(function (cid, menuItems, parentMenuItem, context) {
    var subMenu = new SubMenu(menuItems, parentMenuItem, context.additionalOptions);

    context._initCascadingMenuEvents(subMenu);

    return subMenu;
  })
};
/**
 *  @memberof cascadingMenuTrait
 *  @access private
 */

var SubMenu = HoverMenu.extend(_.extend(
/** @lends SubMenu.prototype */
{
  /**
   * @constructor SubMenu
   * @extends HoverMenu
   * @access private
   * @description SubMenu component.
   * @param {object} menuItems
   * @param {Backbone.View} parentMenuItem
   * @param {object} additionalOptions
   * @mixes cascadingMenuTrait
   */
  constructor: function constructor(menuItems, parentMenuItem, additionalOptions) {
    cascadingMenuTraitConstr.call(this, menuItems, parentMenuItem.$el, null, additionalOptions);
  },

  /**
   * @desc show sub menu on parent option hover with some left offset
   */
  show: function show() {
    var offset = this.$attachTo.offset(),
        width = this.$attachTo.outerWidth();
    this.$el.css({
      top: offset.top,
      left: offset.left + width
    });
    return Menu.prototype.show.apply(this, arguments);
  }
}, cascadingMenuTrait));
module.exports = _.extend({}, cascadingMenuTrait, {
  constructor: cascadingMenuTraitConstr
});

});