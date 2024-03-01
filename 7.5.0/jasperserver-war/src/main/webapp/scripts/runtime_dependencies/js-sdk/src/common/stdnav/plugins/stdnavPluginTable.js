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
var localLogger = logger.register("stdnavPluginTable");
var version = '0.0.1';
var singleton = null;
var gserial = 0;

var stdnavPluginTable = function stdnavPluginTable() {
  gserial++;
  this.serial = gserial;
};

$.extend(stdnavPluginTable.prototype, {
  zinit: function zinit(selector) {
    localLogger.debug('stdnavPluginTable.init(' + selector + ')\n');
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
      'mousedown': [stdnav, stdnav.basicMouseDown],
      'right': [this, this._onRight, null],
      'up': [this, this._onUp, null],
      'superfocusin': [stdnav, stdnav.basicSuperfocusIn, {
        'maxdepth': 4,
        'focusSelector': 'td',
        'ghostfocus': false
      }],
      'superfocusout': [stdnav, stdnav.basicSuperfocusOut, {
        'ghostfocus': false
      }]
    };
    stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
  },
  deactivate: function deactivate() {
    stdnav.unregisterNavtype(this.navtype, this.behavior);
  },
  _ariaPrep: function _ariaPrep(el) {
    this._ariaRefresh(el);
  },
  _ariaRefresh: function _ariaRefresh(el) {
    var $table = $(el);
    $table.attr('role', 'application');
    var label = $table.attr('aria-label');
    var labelledBy = $table.attr('aria-labelledby');
    var $items = $table.find('td,th');
    var allItemsAreLinks = false;

    if (stdnav.nullOrUndefined(label) && stdnav.nullOrUndefined(labelledBy)) {
      var allLinks = $items.find('a');

      if (allLinks.length === $items.length) {
        $table.attr('aria-label', 'Table of ' + $items.length + ' links.');
        allItemsAreLinks = true;
      } else {
        $table.attr('aria-label', 'Table of ' + $items.length + ' cells.');
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
          itemLabel = itemText;
          $item.attr('aria-label', itemLabel);
        }
      }
    });
    return null;
  },
  _findSubfocus: function _findSubfocus(el) {
    var table = $(el).closest('table');
    var sfels = table.find('.subfocus');

    if (sfels !== undefined) {
      return $(sfels[0]);
    }
  },
  _getPreviousSection: function _getPreviousSection(element) {
    var thisSection = $(element).closest('thead,tbody,tfoot');
    var prevSection;

    if (thisSection === undefined) {
      return undefined;
    }

    switch (thisSection.prop('nodeType')) {
      case 'TFOOT':
        prevSection = thisSection.parent().children('TBODY');
        break;

      case 'TBODY':
        prevSection = thisSection.parent().children('THEAD');
        break;

      case 'THEAD':
        break;

      default:
    }

    if (prevSection !== undefined && prevSection.length > 0) {
      return prevSection[0];
    }

    return undefined;
  },
  _getNextSection: function _getNextSection(element) {
    var thisSection = $(element).closest('thead,tbody,tfoot');
    var nextSection;

    if (thisSection === undefined) {
      return undefined;
    }

    switch (thisSection.prop('nodeType')) {
      case 'THEAD':
        nextSection = thisSection.parent().children('TBODY');
        break;

      case 'TBODY':
        nextSection = thisSection.parent().children('TFOOT');
        break;

      case 'TFOOT':
        break;

      default:
    }

    if (nextSection !== undefined && nextSection.length > 0) {
      return nextSection[0];
    }

    return undefined;
  },
  _fixSuperfocus: function _fixSuperfocus(element) {
    var newSuperfocus;
    var $closestTable = $(element).closest('table');

    if ($closestTable.length > 0) {
      newSuperfocus = $closestTable[0];
    } else {
      newSuperfocus = null;
    }

    return newSuperfocus;
  },
  _fixFocus: function _fixFocus(element) {
    var newFocus;

    switch ($(element).prop('nodeName')) {
      case 'TH':
      case 'TD':
        newFocus = element;
        break;

      case 'TR':
        newFocus = stdnav.closestDescendant(element, 'td,th .ghostfocus', null, 1);

        if (newFocus === undefined) {
          newFocus = stdnav.closestDescendant(element, 'td,th', null, 1);

          if (newFocus === undefined) {
            newFocus = $(element).prev('tr');

            if (newFocus === undefined) {
              newFocus = this._fixFocus($(element).parent());
            }
          }
        }

        break;

      case 'THEAD':
      case 'TBODY':
      case 'TFOOT':
        newFocus = stdnav.closestDescendant(element, 'td,th .ghostfocus', null, 2);

        if (newFocus === undefined) {
          newFocus = stdnav.closestDescendant(element, 'td,th', null, 2);

          if (newFocus === undefined) {
            var nextSection = this._getNextSection(element);

            if (nextSection === undefined) {
              newFocus = this._fixFocus($(element).closest('table'));
            } else {
              newFocus = this._fixFocus(nextSection);
            }
          }
        }

        break;

      case 'COLGROUP':
      case 'COL':
        newFocus = this._fixFocus($(element).closest('table'));
        break;

      case 'CAPTION':
        newFocus = this._fixFocus($(element).closest('table'));
        break;

      case 'TABLE':
        newFocus = stdnav.closestDescendant(element, 'td,th .ghostfocus', null, 5);

        if (newFocus === undefined) {
          newFocus = stdnav.closestDescendant(element, 'td,th', null, 5);

          if (newFocus === undefined) {
            newFocus = element;
          }
        }

        break;

      default:
        newFocus = this._fixFocus($(element).closest('td,th,table'));
    }

    return newFocus;
  },
  _onSubfocusIn: function _onSubfocusIn(element) {
    var fixedSubfocus = element;

    if ($(element).is('td') === false) {
      fixedSubfocus = this._fixSubfocus($(element));
      stdnav.setSubfocus(fixedSubfocus, false);
    }

    $.call(this, stdnav.basicSubfocusIn, fixedSubfocus);
  },
  _onLeft: function _onLeft(element) {
    var sfel = this._findSubfocus(element);

    var newsf = $(false);

    if (sfel.is('td')) {
      newsf = sfel.prev('td');
    }

    if (newsf.length === 1) {
      return newsf;
    }

    return element;
  },
  _onRight: function _onRight(element) {
    var sfel = this._findSubfocus(element);

    var newsf = $(false);

    if (sfel.is('td')) {
      newsf = sfel.next('td');
    }

    if (newsf.length === 1) {
      return newsf;
    }

    return element;
  },
  _onUp: function _onUp(element) {
    var sfel = this._findSubfocus(element);

    var row = $(false);
    var newrow = $(false);
    var newsf = $(false);

    if (sfel.is('td')) {
      row = sfel.closest('tr');

      if (row.length === 1) {
        newrow = row.prev('tr');
      }

      if (newrow.length == 1) {
        var iter = sfel;
        var col = -1;

        do {
          col++;
          iter = $(iter.prev('td')[0]);
        } while (iter.length > 0);

        newsf = $(newrow.find('td')[0]);

        while (col > 0) {
          iter = newsf.next('td');

          if (iter.length > 0) {
            newsf = $(iter[0]);
          }

          col--;
        }
      }
    }

    if (newsf.length === 1) {
      return newsf;
    }

    return element;
  },
  _onDown: function _onDown(element) {
    var sfel = this._findSubfocus(element);

    var row = $(false);
    var newrow = $(false);
    var newsf = $(false);

    if (sfel.is('td')) {
      row = sfel.closest('tr');

      if (row.length > 0) {
        newrow = row.next('tr');
      }

      if (newrow.length > 0) {
        var iter = sfel;
        var col = -1;

        do {
          col++;
          iter = $(iter.prev('td')[0]);
        } while (iter.length > 0);

        newsf = $(newrow.find('td')[0]);

        while (col > 0) {
          iter = newsf.next('td');

          if (iter.length > 0) {
            newsf = $(iter[0]);
          }

          col--;
        }
      }
    }

    if (newsf.length === 1) {
      return newsf;
    }

    return element;
  }
});
$.extend(stdnavPluginTable.prototype, {
  navtype: 'table',
  navtype_tags: ['TABLE']
});
var newStdnavPluginTable = new stdnavPluginTable();
module.exports = newStdnavPluginTable;

});