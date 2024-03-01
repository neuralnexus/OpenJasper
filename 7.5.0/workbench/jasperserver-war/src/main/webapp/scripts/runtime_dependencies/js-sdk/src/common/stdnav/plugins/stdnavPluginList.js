define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var logger = require("../../logging/logger");

var stdnav = require('../stdnav');

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
var localLogger = logger.register("stdnavPluginList");
var version = '0.0.1';
var singleton = null;
var gserial = 0;

var stdnavPluginList = function stdnavPluginList() {
  gserial++;
  this.serial = gserial;
};

$.extend(stdnavPluginList.prototype, {
  zinit: function zinit(selector) {
    localLogger.debug('stdnavPluginList.init(' + selector + ')\n');
    return this;
  },
  activate: function activate() {
    this.behavior = {
      'ariaprep': [this, this._ariaPrep, null],
      'ariarefresh': [this, this._ariaRefresh, null],
      'down': [this, this._onDown, null],
      'fixfocus': [this, this._fixFocus, null],
      'fixsuperfocus': [this, this._fixSuperfocus, null],
      'inherit': true,
      'inheritable': true,
      'left': [this, this._onLeft, null],
      'right': [this, this._onRight, null],
      'up': [this, this._onUp, null],
      'superfocusin': [stdnav, stdnav.basicSuperfocusIn, {
        'maxdepth': 1,
        'focusSelector': 'li',
        'ghostfocus': false
      }],
      'superfocusout': [stdnav, stdnav.basicSuperfocusOut, {
        'ghostfocus': false
      }]
    };
    stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
  },
  deactivate: function deactivate() {
    stdnav.unregisterNavtype('list', this.behavior);
  },
  _ariaPrep: function _ariaPrep(el) {
    this._ariaRefresh(el);
  },
  _ariaRefresh: function _ariaRefresh(el) {
    var $list = $(el);
    $list.attr('role', 'application');
    var label = $list.attr('aria-label');
    var labelledBy = $list.attr('aria-labelledby');
    var $items = $list.children('li');
    var itemPlural = $list.attr('js-itemplural');

    if (itemPlural === '' || !itemPlural) {
      itemPlural = 'items';
    }

    var allItemsAreLinks = false;

    if (stdnav.nullOrUndefined(label) && stdnav.nullOrUndefined(labelledBy)) {
      var allLinks = $items.find('a');

      if (allLinks.length === $items.length) {
        $list.attr('aria-label', 'List of ' + $items.length + ' links.');
        allItemsAreLinks = true;
      } else {
        $list.attr('aria-label', 'List of ' + $items.length + itemPlural);
      }
    }

    $.each($items, function (index, item) {
      var $item = $(item);
      var $itemLinks = $item.find('a');

      if ($itemLinks.length > 0) {
        $item.attr('role', 'link');
        var itemLabel = $item.attr('aria-label');
        var itemLabelledBy = $item.attr('aria-labelledby');

        if (stdnav.nullOrUndefined(itemLabel) && stdnav.nullOrUndefined(itemLabelledBy)) {
          var itemText = $item.text();
          var itemLinkText = $($itemLinks[0]).text();
          itemLabel = itemText + '. ' + (index + 1) + ' of ' + $items.length + ' ' + itemPlural + '.';
          $item.attr('aria-label', itemLabel);
        }
      }
    });
    return null;
  },
  _fixFocus: function _fixFocus(element) {
    var ghosts;
    var newFocus;
    var $el = $(element);

    if ($el.is('fieldset') && $el.children('ul.inputSet').length) {
      return element;
    }

    if ($el.is('ul,ol')) {
      ghosts = $el.children('li .ghostfocus');

      if (ghosts.length > 0) {
        newFocus = ghosts[0];
      } else {
        var items = $el.children('li');

        if (items.length > 0) {
          newFocus = items[0];
        } else {
          newFocus = element;
        }
      }
    } else if ($el.is('li')) {
      newFocus = element;
    } else {
      var lis = $el.closest('li');

      if (lis.length > 0) {
        if ($(lis[0]).prop['js-navigable'] === false) {
          newFocus = $el.closest('ul,ol');
        } else {
          newFocus = lis[0];
        }
      } else {
        newFocus = $el.closest('ul,ol');
      }
    }

    return newFocus;
  },
  _fixSubfocus: function _fixSubfocus(element) {
    return null;
  },
  _fixSuperfocus: function _fixSuperfocus(element) {
    var newSuperfocus;
    var $closestList = $(element).closest('ol,ul');

    if ($closestList.length > 0) {
      newSuperfocus = $closestList[0];
    } else {
      newSuperfocus = null;
    }

    return newSuperfocus;
  },
  _onClick: function _onClick(element) {
    $(element).closest('ul, ol').focus();
    stdnav.setSubfocus(this._fixSubfocus(element));
  },
  _onSuperfocusIn: function _onSuperfocusIn(element) {
    var newFocus;
    var ghosts = $(element).children('li .ghostfocus');

    if (ghosts.length > 0) {
      ghosts.removeClass('ghostfocus');
      newFocus = ghosts[0];
    } else {
      var items = $(element).children('li[js-navigable!="false"]');

      if (items.length > 0) {
        newFocus = items[0];
      } else {
        newFocus = element;
      }
    }

    return newFocus;
  },
  _onSuperfocusOut: function _onSuperfocusOut(element) {},
  _onLeft: function _onLeft(element) {
    var newFocus = element;
    var $list = $(element).closest('ul,ol');

    if ($list.hasClass('horizontal')) {
      var $newFocus = $(element).prev('li');

      if ($newFocus.length === 0) {
        if ($list.attr('js-stdnav-wrap') == 'wrap') {
          var $items = $list.children('li');

          if ($items.length > 0) {
            newFocus = $items[$items.length - 1];
          }
        }
      } else {
        newFocus = $newFocus[0];
      }
    }

    return newFocus;
  },
  _onRight: function _onRight(element) {
    var newFocus = element;
    var $list = $(element).closest('ul,ol');

    if ($list.hasClass('horizontal')) {
      var $newFocus = $(element).next('li');

      if ($newFocus.length === 0) {
        if ($list.attr('js-stdnav-wrap') == 'wrap') {
          var $items = $list.children('li');

          if ($items.length > 0) {
            newFocus = $items[0];
          }
        }
      } else {
        newFocus = $newFocus[0];
      }
    }

    return newFocus;
  },
  _onUp: function _onUp(element) {
    var newFocus = element;
    var $list = $(element).closest('ul,ol');

    if (!$list.hasClass('horizontal')) {
      var $newFocus = $(element).prev('li');

      if ($newFocus.length === 0) {
        if ($list.attr('js-stdnav-wrap') == 'wrap') {
          var $items = $list.children('li');

          if ($items.length > 0) {
            newFocus = $items[$items.length - 1];
          }
        }
      } else {
        newFocus = $newFocus[0];
      }
    }

    return newFocus;
  },
  _onDown: function _onDown(element) {
    var newFocus = element;
    var $list = $(element).closest('ul,ol');

    if (!$list.hasClass('horizontal')) {
      var $newFocus = $(element).next('li');

      if ($newFocus.length === 0) {
        if ($list.attr('js-stdnav-wrap') == 'wrap') {
          var $items = $list.children('li');

          if ($items.length > 0) {
            newFocus = $items[0];
          }
        }
      } else {
        newFocus = $newFocus[0];
      }
    }

    return newFocus;
  }
});
$.extend(stdnavPluginList.prototype, {
  navtype: 'list',
  navtype_tags: ['UL', 'OL']
});
var newStdnavPluginList = new stdnavPluginList();
module.exports = newStdnavPluginList;

});