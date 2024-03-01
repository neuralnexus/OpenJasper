define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var log = require("runtime_dependencies/js-sdk/src/common/logging/logger");

var stdnav = require("runtime_dependencies/js-sdk/src/common/stdnav/stdnav");

var eventAutomation = require("runtime_dependencies/js-sdk/src/common/util/eventAutomation");

var layoutModule = require("../../core/core.layout");

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
* @author: ${username}
* @version: $Id$
*/

/* Standard Navigation library (stdnav) plugin
* Elements: FORM, INPUT, OPTION, OPTGROUP, SELECT, TEXTAREA
* Navtype:  forms (element default)
*
* Plugin for the "forms" navtype.  This is the default-- AND STRONGLY
* RECOMMENDED-- behavior for forms and inputs.  Primarily needed to ensure
* that input controls inside of lists and grids can properly obtain focus,
* and also so they don't get inappropriate navigation effects intended for
* their parents.  Be aware that "button" has its own plugin.
*
* Note that screen readers take classic, standard web forms more seriously
* than practically anything else.  They implement them well, and in general
* you will want to stay out of their way.  Avoid unexpected focus changes
* when dealing with forms.
*
* This module supports enhancements intended to improve compliance with
* section 508 of the Rehabilitation Act of 1973, 29 USC 798, as amended
* 1998.
*/
var logger = log.register('stdnav');
var version = "0.0.1",
    singleton = null,
    gserial = 0; // TODO: get rid of dependency on JRS mark-up

layoutModule.INPUT_ZOOM_VALUE = 'input#zoom_value';
layoutModule.INPUT_SEARCH_REPORT = 'input#search_report';
layoutModule.PAGE_CURRENT = 'input#page_current';
layoutModule.MENU_VWROPTIONS = '#vwroptions .menu.vertical.dropDown.fitable';
layoutModule.MENU_VWROPTIONS_LIST = '#vwroptions .menu.vertical.dropDown.fitable li'; // Local object definition.

var stdnavPluginForms = function stdnavPluginForms() {
  gserial++;
  this.serial = gserial;
}; // FIRST EXTENSION PASS - FUNCTIONS
// Provides forward-references for hashes in the second pass, so that
// references in that second pass to functions declared here resolve
// (since the entire pass is applied at once, they cannot be combined).


$.extend(stdnavPluginForms.prototype, {
  zinit: function zinit(selector) {
    logger.debug('stdnavPluginForms.init(' + selector + ")\n");
    return this;
  },
  // Registers the 'forms' navtype with stdnav.  stdnav must be loaded and
  // activated before this can be done.
  activate: function activate() {
    // This is the behaviour hash for the navtype.  These defaults pass
    // everything through to the browser, and are normally overridden
    // with $.extend based on specific tagnames and stdnav attributes.
    //
    // For dynamic lists, pass arrow keys through to the existing keyboard
    // behavior.
    this.behavior = {
      'ariaprep': [this, this._ariaPrep, null],
      'ariarefresh': [this, this._ariaRefresh, null],
      'down': [this, this._onDown, null],
      'enter': [this, this._onEnter, null],
      'exit': [this, this._onExit, null],
      'inherit': false,
      'inheritable': true,
      'fixsuperfocus': [stdnav, stdnav.basicFixSuperfocus, null],
      'superfocusin': [stdnav, stdnav.basicSuperfocusIn, {
        'maxdepth': 1,
        'focusSelector': ':input',
        'ghostfocus': false
      }]
    };
    stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
  },
  // Unregisters the 'forms' navtype from stdnav.  This must be done
  // before deactivating/unloading stdnav.
  deactivate: function deactivate() {
    stdnav.unregisterNavtype(this.navtype, this.behavior);
  },
  // This callback is run when the page is initially rendered.  Add the
  // appropriate ARIA tags for the handled construct, if they do not already
  // exist.  The element passed will be the superfocus for the construct
  // being instrumented, but this construct may not actually have focus at
  // the time this function is called.
  _ariaPrep: function _ariaPrep(el) {
    $(el).attr('role', 'form');
    return null;
  },
  // This callback is run when the superfocus changes to the construct.
  // Its two purposes are to update existing constructs, and to handle
  // dynamically-created content whose creation was not detected during
  // initial page construction (possibly because no part of the construct
  // existed yet).  The element passed will be the superfocus for the
  // construct, but this construct may not actually have focus at the time
  // this function is called.
  _ariaRefresh: function _ariaRefresh(el) {
    $(el).attr('role', 'form');
    return null;
  },

  /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */

  /* ========== KEYBOARD BEHAVIOR =========== */
  //ToDo need refactor!!!
  _onDown: function _onDown(element) {
    var $el = $(element);

    if ($el.is("input#zoom_value")) {
      this.parent = element;
      $el.closest("li").addClass("isParent");
      eventAutomation.simulateClickSequence($('button#zoom_value_button')[0]);
      $(layoutModule.MENU_VWROPTIONS_LIST).first().find("p").addClass("over");
      element = $(layoutModule.MENU_VWROPTIONS_LIST).first()[0];
    } else if ($el.is("input#search_report")) {
      this.parent = element;
      $el.closest("li").addClass("isParent");
      eventAutomation.simulateClickSequence($('button#search_options')[0]);
      $(layoutModule.MENU_VWROPTIONS_LIST).first().find("p").addClass("over");
      element = $(layoutModule.MENU_VWROPTIONS_LIST).first()[0];
    }

    return element;
  },
  _onExit: function _onExit(element) {
    var $el = $(element);

    if ($el.is(layoutModule.INPUT_ZOOM_VALUE)) {
      element = $("li.zoom.leaf.j-dropdown")[0];
    } else if ($el.is(layoutModule.INPUT_SEARCH_REPORT)) {
      element = $("li.search.leaf.j-dropdown")[0];
    } else if ($el.is(layoutModule.PAGE_CURRENT)) {
      element = $("li.paging.leaf.j-dropdown")[0];
    }

    return element;
  },
  _onEnter: function _onEnter(element) {
    var $el = $(element);

    if ($el.is(layoutModule.INPUT_ZOOM_VALUE)) {
      element = $("li.zoom.leaf.j-dropdown")[0];
    } else if ($el.is(layoutModule.INPUT_SEARCH_REPORT)) {
      element = $("li.search.leaf.j-dropdown")[0];
    } else if ($el.is(layoutModule.PAGE_CURRENT)) {
      element = $("li.paging.leaf.j-dropdown")[0];
    }

    return element;
  }
}); // SECOND EXTENSION PASS - ATTRIBUTES
// Hash members in this pass can reference functions from the last pass.

$.extend(stdnavPluginForms.prototype, {
  // This is the name of the new navtype.  Each stdnav plugin must
  // define a unique name.
  navtype: 'forms',
  // This arrary extends the tag-to-navtype map in stdnav.  If your
  // plugin should apply to all elements of a given type, add those
  // element tagnames, in lower case, to this array.  It is normally
  // empty, and the page templates simply set an appropriate
  // "data-navtype=" attribute to get the expected behavior.
  // Note that 'button' has its own plugin.
  // USE UPPER-CASE!
  navtype_tags: ['FORM', 'INPUT', 'OPTGROUP', 'OPTION', 'SELECT', 'TEXTAREA']
});
var newStdnavPluginForms = new stdnavPluginForms();
module.exports = newStdnavPluginForms;

});