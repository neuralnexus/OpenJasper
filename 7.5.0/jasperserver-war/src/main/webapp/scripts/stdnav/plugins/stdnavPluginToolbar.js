define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var stdnav = require("runtime_dependencies/js-sdk/src/common/stdnav/stdnav");

var buttonManager = require('../../core/core.events.bis');

var layoutModule = require('../../core/core.layout');

var primaryNavigation = require('../../components/components.toolbarButtons');

var eventAutomation = require("runtime_dependencies/js-sdk/src/common/util/eventAutomation");

var actionModel = require('../../actionModel/actionModel.modelGenerator');

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
 * @version: $Id: $
 */

/* Standard Navigation library (stdnav) plugin
 * Elements: LI, OL, UL
 * Navtype:  toolbar
 *
 * Plugin for toolbar menus, which may consist of simple buttons, text inputs,
 * buttons with drop-down options, tabs, etc. It handles the basic keyboard
 * navigation: moving left/right, opening the drop-downs, firing the actions.
 *
 * This module supports enhancements intended to improve compliance with
 * section 508 of the Rehabilitation Act of 1973, 29 USC 798, as amended
 * 1998.
 */
var gserial = 0; // TODO: get rid of dependency on JRS mark-up

layoutModule.TOOLBAR_MENU_PATTERN = 'ul.j-toolbar';
layoutModule.TOOLBAR_MENU_ITEM_PATTERN = 'ul.j-toolbar li';
layoutModule.BUTTON_ZOOM_VALUE = 'button#zoom_value_button';
layoutModule.BUTTON_SEARCH_REPORT = 'button#search_report_button';
layoutModule.BUTTON_SEARCH_OPTIONS = 'button#search_options';
layoutModule.BUTTON_FILE_OPTIONS = 'button#fileOptions';
layoutModule.BUTTON_EXPORT = 'button#export';
layoutModule.INPUT_ZOOM_VALUE = 'input#zoom_value';
layoutModule.INPUT_SEARCH_REPORT = 'input#search_report';
layoutModule.VIEWER_TOOLBAR = '#viewerToolbar';
layoutModule.MENU_VWROPTIONS = '#vwroptions .menu.vertical.dropDown.fitable';
layoutModule.MENU_VWROPTIONS_LIST = '#vwroptions .menu.vertical.dropDown.fitable li';

var StdnavPluginToolbar = function StdnavPluginToolbar() {
  gserial++;
  this.serial = gserial;
};

_.extend(StdnavPluginToolbar.prototype, {
  zinit: function zinit(selector) {
    return this;
  },
  // Registers the 'toolbar' navtype with stdnav.  Both stdnav and toolbar
  // must be loaded and activated before this can be done.
  activate: function activate() {
    // This is the behaviour hash for the navtype.  These defaults pass
    // everything through to the browser, and are normally overridden
    // with $.extend based on specific tagnames and stdnav attributes.
    this.behavior = {
      'ariaprep': [this, this._ariaPrep, null],
      'ariarefresh': [this, this._ariaRefresh, null],
      'down': [this, this._onDown, null],
      'enter': [this, this._onEnterOrEntered, null],
      'exit': [this, this._onExit, null],
      'fixfocus': [this, this._fixFocus, null],
      'fixsubfocus': [this, this._fixFocus, null],
      'fixsuperfocus': [this, this._fixSuperfocus, null],
      'focusin': [this, this._onFocusIn, null],
      'focusout': [this, this._onFocusOut, null],
      'left': [this, this._onLeft, null],
      'right': [this, this._onRight, null],
      'subfocusin': [this, this._onSubfocusIn, null],
      'superfocusin': [this, this._onSuperfocusIn, null],
      'superfocusout': [this, this._onSuperfocusOut, null],
      'up': [this, this._onUp, null],
      'inherit': false
    };
    stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
  },
  // Unregisters the 'toolbar' navtype from stdnav.  This must be done
  // before deactivating/unloading stdnav.
  deactivate: function deactivate() {
    stdnav.unregisterNavtype(this.navtype, this);
  },
  // This callback is run when the page is initially rendered.  Add the
  // appropriate ARIA tags for the handled construt, if they do not already
  // exist.  The element passed will be the superfocus for the construct
  // being instrumented, but this construct may not actually have focus at
  // the time this function is called.
  _ariaPrep: function _ariaPrep(el) {
    this._ariaRefresh(el);
  },
  // This callback is run when the superfocus changes to the construct.
  // Its two purposes are to update existing constructs, and to handle
  // dynamically-created content whose creation was not detected during
  // initial page construction (possibly because no part of the construct
  // existed yet).  The element passed will be the superfocus for the
  // construct, but this construct may not actually have focus at the time
  // this function is called.
  _ariaRefresh: function _ariaRefresh(el) {
    var $items = $(el).find('li').not(".divider");
    $(el).attr('role', 'application');
    $.each($items, function (key, item) {
      var $item = $(item);
      var $labeledEl = $item.find("[aria-label]");

      if (!$item.attr('aria-label') && $labeledEl.length) {
        $item.attr('aria-label', $labeledEl.attr('aria-label'));
      }
    });
    return null;
  },

  /* ====== Focus Management ====== */
  // Focus adjustment callback.  Ensures that if the DIV or UL elements
  // themselves are given focus, that it is promoted to the first LI.
  _fixFocus: function _fixFocus(element) {
    var newFocus;
    var $el = $(element);

    if ($el.is('div,ul,ol')) {
      // The usual case: the entire menu is gaining focus.  One of
      // the reasons this can happen, of course, is in response to a
      // mouse click, not a TAB press-- as a result, check to see if
      // any of the menu options has the "pressed" CSS class, and
      // focus that, if it does.  However, ignore the "over" class,
      // as the mouse pointer can wind up anywhere for a blind user
      // by total coincidence, so it must be ignored when setting
      // focus in response to TAB.
      //
      // If nothing seems to be in use yet, select the first list
      // item-- if there ARE any.
      //
      // Failing even that, select the list itself.
      var items = $el.find('.pressed');

      if (items.length > 0) {
        newFocus = items[0];
      } else {
        items = $el.find('li').not(".divider");

        if (items.length > 0) {
          newFocus = items[0];
        } else {
          // The entire list is empty-- set focus to the root list element
          // after all.
          newFocus = element;
        }
      }
    } else if ($el.is('button')) {
      // Focus is already appropriate.
      newFocus = element;
    } else {
      // Assume we're in a span or something within a list item.
      var lis = $el.closest('li');

      if (lis.length > 0) {
        if ($(lis[0]).prop['js-navigable'] === false) {
          // Clicked on a header or something; focus the list instead.
          newFocus = $el.closest('ul,ol');
        } else {
          newFocus = lis[0];
        }
      }
    } // Avoid focusing disabled button


    if ($(newFocus).is(":disabled") || $(newFocus).find(".button,.mutton").is(":disabled")) {
      newFocus = stdnav.closestNavigableAncestor(newFocus);
    }

    return newFocus;
  },
  // Superfocus adjustment callback.  Because of the way actionModel
  // works, the menu is expected to be rooted in an enclosing DIV that
  // contains the list, so use the DIV for the superfocus region.
  // However, context menus are _not_
  _fixSuperfocus: function _fixSuperfocus(element) {
    var newSuperfocus;
    var $root = $(element).closest('ul');

    if ($root.length > 0) {
      newSuperfocus = $root[0];
    } else {
      // FAULT, let StdNav fall back to BODY
      newSuperfocus = null;
    }

    return newSuperfocus;
  },

  /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */
  _onSuperfocusIn: function _onSuperfocusIn(element) {
    var $elem = $(element),
        $parentList = $(this.parent).closest(layoutModule.TOOLBAR_MENU_PATTERN),
        $currentList = $elem.closest(".menu").length && $elem;

    if ($currentList && $parentList.attr("tabindex") > -1) {
      this._parentTabindex = $parentList.attr("tabindex");

      if (this._parentTabindex > -1) {
        $currentList.attr('js-suspended-tabindex', this._parentTabindex);
        $currentList.find("li:first").attr('tabindex', this._parentTabindex);
      } else {
        $elem.attr('js-suspended-tabindex', 'none');
        $currentList.find("li:first").attr('tabindex', -1);
      } // Explicitly make the element unfocusable-- temporarily.


      $currentList.attr('tabindex', '-1');
      $parentList.attr("tabindex", "-1");
    }

    return element;
  },
  _onSubfocusIn: function _onSubfocusIn(element) {
    var $ul = $(element).closest(layoutModule.TOOLBAR_MENU_PATTERN);

    if ($ul.attr('js-suspended-tabindex') > -1) {
      $(element).attr("tabindex", $ul.attr('js-suspended-tabindex'));
    } else if ($ul.attr('tabindex') > -1) {
      $(element).attr("tabindex", $ul.attr('tabindex'));
      $ul.attr('js-suspended-tabindex', $ul.attr('tabindex'));
    }
  },
  _onFocusIn: function _onFocusIn(element) {
    var $thisItem, $matched;
    var $selected = $(element);

    if ($selected.length > 0) {
      $thisItem = $selected.closest(layoutModule.TOOLBAR_MENU_ITEM_PATTERN);

      if ($thisItem.length > 0) {
        // An item in the main menu bar has been focused.
        buttonManager.over($thisItem.find(".button")[0]); //buttonManager.over($thisItem.find(layoutModule.MENU_LIST_PATTERN)[0]); //ToDo

        $matched = $thisItem.find(".mutton");

        if ($matched.length > 0 && !buttonManager.isDisabled($matched.context)) {
          // The item has a submenu-- show it.
          //actionModel.hideMenu();
          primaryNavigation.showButtonMenu(null, $matched[0]);
        } else {
          // An item with no drop-down menu has been focused;
          // ensure any drop-down menus for other items are closed.
          actionModel.hideMenu();
          $(layoutModule.MENU_VWROPTIONS).hide();
        }
      } else {
        $thisItem = $selected.is(".menu li") ? $selected : $selected.closest(".menu");

        if ($thisItem.length > 0) {
          // An item in a drop-down or context menu has been focused.
          buttonManager.over($thisItem.find("p")[0]);
        } // TODO: get rid of dependency on JRS mark-up


        if ($selected.is(layoutModule.BUTTON_ZOOM_VALUE) || $selected.is(layoutModule.BUTTON_SEARCH_REPORT) || $selected.is(layoutModule.BUTTON_SEARCH_OPTIONS)) {
          $selected.click();
        }
      }
    }

    return element;
  },
  _onFocusOut: function _onFocusOut(element) {
    var $thisItem = $(element).closest(layoutModule.TOOLBAR_MENU_ITEM_PATTERN);

    if ($thisItem.length > 0) {
      // A top-level item in the main menu bar has lost focus.
      //actionModel.hideMenu();
      // Don't remove the style if we've moved into a drop-down menu.
      if (this.lastMenuBarItem !== element) {
        buttonManager.out($thisItem.find(layoutModule.BUTTON_PATTERN)[0]);
        $thisItem.removeAttr("tabindex");
      } //buttonManager.out($thisItem.find(layoutModule.MENU_LIST_PATTERN)[0]);

    } else {
      $thisItem = $(element).closest(layoutModule.MENU_LIST_PATTERN);

      if ($thisItem.length > 0) {
        // An item in a drop-down or context menu has lost focus.
        buttonManager.out($thisItem.find(layoutModule.BUTTON_PATTERN)[0]);
        $thisItem.removeAttr("tabindex");
      }
    }

    return null;
  },
  _onSuperfocusOut: function _onSuperfocusOut(element) {
    var $toolbar = $(layoutModule.VIEWER_TOOLBAR);

    if ($toolbar.length < 1) {
      // There is no main navigation in embedded mode.
      return element;
    } // Hide the dropdown menu unless the new focus is _in_ the
    // dropdown menu.  Because of the way actionModel works, the
    // dropdown menu is NOT a DOM descendant of the toolbar.


    var newFocus = $(document.activeElement);

    if (newFocus.closest('.dropDown').length < 1) {
      var $selected = $toolbar.find("." + layoutModule.HOVERED_CLASS);

      if ($selected.length > 0) {
        buttonManager.out($selected[0]);
      }

      actionModel.hideMenu();
      $(layoutModule.MENU_VWROPTIONS).hide(); // Explicitly make the element unfocusable-- temporarily.

      var $ul = $(this.parent).closest(layoutModule.TOOLBAR_MENU_PATTERN);
      $ul.attr("tabindex", this._parentTabindex);
      this.parent = null;
      this.lastMenuBarItem = null;
      $(".isParent").removeClass("isParent");
    }
  },

  /* ========== KEYBOARD BEHAVIOR =========== */

  /* ========== KEYBOARD BEHAVIOR =========== */
  _onLeft: function _onLeft(element) {
    var $thisItem = $(element).closest(layoutModule.TOOLBAR_MENU_ITEM_PATTERN);
    var $prev = $(element);

    if (!$thisItem.length && $(element).closest(".menu").length) {
      $thisItem = $(this._onExitHandler(element));
    }

    if ($thisItem.length > 0) {
      var $prevAll = $thisItem.prevAll(layoutModule.TOOLBAR_MENU_ITEM_PATTERN);
      $prevAll.each(function (key, elem) {
        var $elem = $(elem);

        if (!$elem.hasClass("divider") && $elem.find("button:disabled").length === 0 && $elem.find("button:hidden").length === 0) {
          $prev = $elem;
          return false;
        }
      });
    }

    return $prev[0];
  },
  _onRight: function _onRight(element) {
    var $thisItem = $(element).closest(layoutModule.TOOLBAR_MENU_ITEM_PATTERN);
    var $next = $(element);

    if (!$thisItem.length && $(element).closest(".menu").length) {
      $thisItem = $(this._onExitHandler(element));
    }

    if ($thisItem.length > 0) {
      var $nextAll = $thisItem.nextAll(layoutModule.TOOLBAR_MENU_ITEM_PATTERN);
      $nextAll.each(function (key, elem) {
        var $elem = $(elem);

        if (!$elem.hasClass("divider") && $elem.find("button:disabled").length === 0 && $elem.find("button:hidden").length === 0) {
          $next = $elem;
          return false;
        }
      });
    }

    return $next[0];
  },
  _onUp: function _onUp(element) {
    var $prev = $(element);

    if ($(element).closest(".menu").length) {
      if ($prev.prev().length) {
        $prev.find("p").removeClass(layoutModule.HOVERED_CLASS);
        $prev = $prev.prev().hasClass("separator") ? $prev.prev().prev() : $prev.prev();
        $prev.find("p").addClass(layoutModule.HOVERED_CLASS);
      } else {
        $prev.length = 0;
      }
    }

    if ($prev.length > 0) {
      return $prev[0];
    } else {
      return this._onExitHandler(element);
    }
  },
  _onDown: function _onDown(element) {
    var $menu,
        $button,
        $next = $(element),
        $input = $next.find("input"); // TODO: get rid of dependency on JRS mark-up

    if ($input.is(layoutModule.INPUT_ZOOM_VALUE)) {
      $button = $(layoutModule.BUTTON_ZOOM_VALUE);
    } else if ($input.is(layoutModule.INPUT_SEARCH_REPORT)) {
      $button = $(layoutModule.BUTTON_SEARCH_OPTIONS);
    }

    if ($button && $button.length) {
      this._setParentElem(element);

      eventAutomation.simulateClickSequence($button[0]);
      $(layoutModule.MENU_VWROPTIONS_LIST).first().find("p").addClass(layoutModule.HOVERED_CLASS);
      return $(layoutModule.MENU_VWROPTIONS_LIST).first()[0];
    }

    if ($next.parent().is("ul.buttonSet")) {
      // TODO: get rid of dependency on JRS mark-up
      if ($next.find(layoutModule.BUTTON_FILE_OPTIONS + "," + layoutModule.BUTTON_EXPORT).length === 0) {
        return element;
      }

      $("#" + layoutModule.MENU_ID).removeClass("hidden");
      $menu = $("#" + layoutModule.MENU_ID + " ul");
    } else {
      $menu = $next.closest("ul").not(".j-toolbar");
    }

    if ($(element).closest(".menu").length) {
      if ($next.next().length) {
        $next.find("p").removeClass(layoutModule.HOVERED_CLASS);
        $next = $next.next().hasClass("separator") ? $next.next().next() : $next.next();
        $next.find("p").addClass(layoutModule.HOVERED_CLASS);
      }
    } else if ($(element).closest(".buttonSet").length) {
      $next = $menu.find("li").first();

      this._setParentElem(element); // this.lastMenuBarItem = element;

    }

    return $next[0];
  },
  _onExit: function _onExit(element) {
    var $el = $(element);

    if ($el.find("p").length > 0) {
      element = this._onExitHandler(element);
    } else {
      element = $("#" + layoutModule.MAIN_SEARCH_INPUT_ID)[0];
    }

    return element;
  },
  _onEnterOrEntered: function _onEnterOrEntered(element) {
    // Activate the link.
    var simulateEl;
    var $el = $(element);

    if ($el.hasClass("j-dropdown")) {
      this._setParentElem(element);

      element = $el.find("input")[0]; // select the existing text value

      if (element) {
        $(element).select();
      }
    }

    if ($el.find("p").length > 0) {
      simulateEl = $el.find("p")[0];
    } else if ($el.find("input").length === 0) {
      simulateEl = $el.find(".button")[0];
    } // TODO: get rid of dependency on JRS mark-up


    if (simulateEl && $(this.parent).find(layoutModule.BUTTON_FILE_OPTIONS).length === 0 && $(this.parent).find(layoutModule.BUTTON_EXPORT).length === 0) {
      eventAutomation.simulateClickSequence(simulateEl);
    }

    if ($(element).find("p").length > 0) {
      element = this._onExitHandler(element);
    }

    return element;
  },
  _setParentElem: function _setParentElem(element) {
    this.parent = element;
    $(element).addClass("isParent");
    this.lastMenuBarItem = element;
  },
  _onExitHandler: function _onExitHandler(element) {
    element = this.parent;

    if (!element) {
      element = $(".isParent")[0];
    }

    $(element).removeClass("isParent");
    return element;
  }
});

$.extend(StdnavPluginToolbar.prototype, {
  // This is the name of the new navtype.  Each stdnav plugin must
  // define a unique name.
  navtype: 'toolbar',
  // This arrray extends the tag-to-navtype map in stdnav.  If your
  // plugin should apply to all elements of a given type, add those
  // element tagnames, in lower case, to this array.  It is normally
  // empty, and the page templates simply set an appropriate
  // "data-navtype=" attribute to get the expected behavior.
  // CASE SENSITIVE - USE UPPER-CASE!
  navtype_tags: []
});
module.exports = new StdnavPluginToolbar();

});