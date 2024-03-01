define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var eventAutomation = require('../../util/eventAutomation');

var log = require("../../logging/logger");

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

/**
* @author: ${username}
* @version: $Id$
*/

/* Standard Navigation library (stdnav) plugin
* Elements: BUTTON
* Navtype:  button (element default)
*
* Plugin for the "button" navtype.  This is the default behavior for "BUTTON"
* elements.
*
* This module supports enhancements intended to improve compliance with
* section 508 of the Rehabilitation Act of 1973, 29 USC 798, as amended
* 1998.
*/
var version = "0.0.1",
    singleton = null,
    gserial = 0;
var logger = log.register("stdnav"); // Local object definition.

var stdnavPluginButton = function stdnavPluginButton() {
  gserial++;
  this.serial = gserial;
}; // FIRST EXTENSION PASS - FUNCTIONS
// Provides forward-references for hashes in the second pass, so that
// references in that second pass to functions declared here resolve
// (since the entire pass is applied at once, they cannot be combined).


$.extend(stdnavPluginButton.prototype, {
  zinit: function zinit(selector) {
    logger.debug('stdnavPluginButton.init(' + selector + ")\n");
    return this;
  },
  // Registers the 'button' navtype with stdnav.  stdnav must be loaded and
  // activated before this can be done.
  activate: function activate() {
    // This is the behaviour hash for the navtype.  These defaults pass
    // everything through to the browser, and are normally overridden
    // with $.extend based on specific tagnames and stdnav attributes.
    this.behavior = {
      'enter': [this, this._onEnterOrEntered, null],
      'entered': [this, this._onEnterOrEntered, null],
      'exit': [this, this._onExit, null],
      'toggle': [this, this._onEnterOrEntered, null],
      'inherit': false,
      'inheritable': true
    };
    stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
  },
  // Unregisters the 'button' navtype from stdnav.  This must be done
  // before deactivating/unloading stdnav.
  deactivate: function deactivate() {
    stdnav.unregisterNavtype('button', this.behavior);
  },
  // Utility function: given a list, ensure the most appropriate element
  // available (in this case, the top-level link node, only) has focus.
  _fixSubfocus: function _fixSubfocus(element) {
    var ghosts;
    var newSubfocus;
    var $el = $(element);

    if ($el.is("BUTTON,[role='button']")) {
      newSubfocus = $el;
    } else {
      newSubfocus = $(element).closest("BUTTON,[role='button']");

      if (newSubfocus === undefined) {
        // Safety
        return undefined;
      }
    } // Nothing in or under this node should have ghostfocus; clear anything
    // that does, and ensure that only the link element itself has subfocus.


    newSubfocus.find('.ghostfocus').removeClass('.ghostfocus');
    newSubfocus.children().find('.subfocus').removeClass('.subfocus');
    return newSubfocus;
  },

  /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */
  _onFocusIn: function _onFocusIn(element) {
    var newSubfocus; // This should fire on the list itself, not the item.

    var ghosts = $(element).children('.ghostfocus');

    if (ghosts.length > 0) {
      ghosts.removeClass('ghostfocus'); // Promote ghost.

      newSubfocus = this._fixSubfocus(ghosts[0]);
    } else {
      // Set subfocus to first item, without firing callbacks.
      var items = $(element).children('li[js-navigable!="false"]');

      if (items.length > 0) {
        newSubfocus = this._fixSubfocus(items[0]);
      } else {
        // For visual reasons, in the case of an empty list, we give up and
        // set subfocus to the list itself.
        newSubfocus = element;
      }
    }

    return newSubfocus;
  },
  //FIXME: Do not set ghostfocus on focus out
  _onSubfocusIn: function _onSubfocusIn(element) {
    if ($(element).prop('nodeName') != 'BUTTON') {
      // Find a usable child element.
      var subel = this._fixSubfocus(element); // Adjust subfocus without firing callbacks.


      stdnav.setSubfocus(subel, false);
    }
  },
  // When activated as a member of a navigable control, such as a list or
  // table, this is a cascade event from the navigable control's "enter"
  // handler, which will cause our "entered" handler to fire.  However, if
  // the anchor is a simple link, such as an <a> tag embedded in a <p> block,
  // it will be a simple tabstop, which is therefore focusable.  If such a
  // link is focused, pressing ENTER will fire our "enter" handler.  The same
  // logic works for both cases, so this callback should be mapped to both
  // events.
  _onEnterOrEntered: function _onEnterOrEntered(element) {
    // Activate the link.
    var $el = $(element);

    if ($el.is("BUTTON,[role='button']")) {
      eventAutomation.simulateClickSequence(element);
    }

    return element;
  },
  _onExit: function _onExit() {
    return $('#searchInput')[0]; // re-use the common exit handler (stdnav.basicExit())
  }
}); // SECOND EXTENSION PASS - ATTRIBUTES
// Hash members in this pass can reference functions from the last pass.

$.extend(stdnavPluginButton.prototype, {
  // This is the name of the new navtype.  Each stdnav plugin must
  // define a unique name.
  navtype: 'button',
  // This arrary extends the tag-to-navtype map in stdnav.  If your
  // plugin should apply to all elements of a given type, add those
  // element tagnames, in lower case, to this array.  It is normally
  // empty, and the page templates simply set an appropriate
  // "data-navtype=" attribute to get the expected behavior.
  //
  // CASE SENSITIVE - USE UPPER-CASE!
  navtype_tags: ['BUTTON']
});
var newStdnavPluginButton = new stdnavPluginButton();
module.exports = newStdnavPluginButton;

});