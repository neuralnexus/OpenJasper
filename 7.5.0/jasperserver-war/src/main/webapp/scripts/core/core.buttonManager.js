define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _prototype = require('prototype');

var $ = _prototype.$;

var layoutModule = require("./core.layout");

var _utilUtilsCommon = require("../util/utils.common");

var isIPad = _utilUtilsCommon.isIPad;

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
var buttonManager = {
  over: function over(element, findTargetFn) {
    if (element && !this.isSelected(element)) {
      var target = findTargetFn ? findTargetFn(element) : element;
      $(target).addClassName(layoutModule.HOVERED_CLASS);
    }
  },
  out: function out(element, findTargetFn) {
    if (element) {
      var target = findTargetFn ? findTargetFn(element) : element;
      $(target).removeClassName(layoutModule.HOVERED_CLASS).removeClassName(layoutModule.PRESSED_CLASS);
    }
  },
  down: function down(element, findTargetFn) {
    if (element && !this.isSelected(element)) {
      var target = findTargetFn ? findTargetFn(element) : element;
      $(target).removeClassName(layoutModule.HOVERED_CLASS).addClassName(layoutModule.PRESSED_CLASS);
    }
  },
  up: function up(element, findTargetFn) {
    if (element && !this.isSelected(element)) {
      var target = findTargetFn ? findTargetFn(element) : element;
      target = $(target);
      target.removeClassName(layoutModule.PRESSED_CLASS);
      !isIPad() && target.addClassName(layoutModule.HOVERED_CLASS);
    }
  },
  disable: function disable(element) {
    if (element) {
      buttonManager.out(element);
      $(element).writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
    }
  },
  enable: function enable(element) {
    if (element) {
      buttonManager.out(element);
      $(element).writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
    }
  },

  /**
   * @deprecated custom jasperhandler in Prototype.js will suppress disabled elems
   * @param {Object} element
   */
  isDisabled: function isDisabled(element) {
    if (element) {
      return $(element).readAttribute(layoutModule.DISABLED_ATTR_NAME) === layoutModule.DISABLED_ATTR_NAME || $(element).hasClassName(layoutModule.DISABLED_CLASS);
    }
  },
  ///////////////////////////////////////////////////////////////////////////////////////
  // TODO: Only used by tab manager - maybe we should use up and down functions instead.
  // (just need to make tabs use 'pressed' class instead of 'selected')
  ///////////////////////////////////////////////////////////////////////////////////////
  unSelect: function unSelect(element) {
    if (element) {
      $(element).removeClassName(layoutModule.SELECTED_CLASS);
    }
  },
  select: function select(element) {
    if (element) {
      $(element).addClassName(layoutModule.SELECTED_CLASS);
    }
  },
  isSelected: function isSelected(element, findTargetFn) {
    if (element) {
      var target = findTargetFn ? findTargetFn(element) : $(element);
      var tagetListItem = target.up('li');
      return tagetListItem && tagetListItem.hasClassName(layoutModule.SELECTED_CLASS);
    }

    return false;
  }
};
module.exports = buttonManager;

});