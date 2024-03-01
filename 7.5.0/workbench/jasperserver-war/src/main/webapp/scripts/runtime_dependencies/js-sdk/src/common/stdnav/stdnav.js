define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var browserDetection = require('../util/browserDetection');

var log = require("../logging/logger");

var stdnavEventHandlers = require("./stdnavEventHandlers");

var stdnavFocusing = require("./stdnavFocusing");

var stdnavModalFocusing = require("./stdnavModalFocusing");

var stdnavDebugger = require("./stdnavDebugger");

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

/* Standard Navigation library (stdnav)
* ------------------------------------
* This library provides keyboard and screen-reader support for areas of the
* application that do not explicitly enable that support.  It attempts to
* stay out of the way of areas that already handle it properly, and can be
* disabled in problem areas by adding 'js-stdnav="false"' to any element
* that contains the area.
*
* This module supports enhancements intended to improve compliance with
* section 508 of the Rehabilitation Act of 1973, 29 USC 798, as amended
* 1998.
*/
var version = "0.0.1",
    singleton = null,
    stdnav_gserial = 0;
var logger = log.register('stdnav'); // Local object definition.
// The top-level stdnav object is a function.  You can get the Effective
// Navigation Topology for a given element be passing in a jQuery selector,
// then use that to check what a particular key will do when that element
// focus/subfocus, like this:
// var onLeft = stdnav("#HomeButton")[stdnav-left];
// ...or change it, like this:
// stdnav("#HomeButton")[stdnav-left]="#";
// (This example disables the "left arrow" key on the HomeButton element,
// by pointing it to "#", or "self".)
//
// If you call it with no parameters, you interact with the library instance
// itself.  For example, the critical:
// stdnav.activate();

var stdnav = function stdnav() {
  //logger.debug('JSAM('+selector+")\n");
  stdnav_gserial++;
  this.serial = stdnav_gserial;
  this.menuItemCallbacks = {
    click: {}
  }; // This hash stores navtype plugins.  Look up the plugin objects by the
  // navtype string, if you need to tweak one for some reason.
  // For example: var gridPluginObj = root.stdnav.plugins['grid'];

  this.plugins = {}; // True if a modal dialog seems to be active (specifically, if
  // stdnav.beginModalFocus was called).

  this.modalDialogActive = false; // If a modal dialog seems to be active, this is the DOM object of its
  // root element.  Otherwise, this should be null.

  this.modalDialogRoot = null; // If set, focus debugging data will be formatted into this element,
  // when it exists, as a simple string.  The element will be updated as
  // focus changes.  If left null, the system is disabled.

  this.debugElementID = null; //this.debugElementID='copyright';
  // Number of non-blocking spaces to increment the delta sink area with;
  // these are used to convince Internet Explorer its rendering cache
  // (and therefore its tab order) are out of date and force it to
  // recompute them.

  this.chaffLength = 1; // Maximum length of the rendering cache countermeasure string.

  this.maxChaffLength = 8;
};

$.extend(stdnav.prototype, stdnavEventHandlers, stdnavFocusing, stdnavModalFocusing, stdnavDebugger, {
  // ===LIMITS AND SAFETY BAILOUTS=======================================
  // Prevents runaway recursion in case of a jQuery filter bug or other
  // DOM search failure.  Keep this value significantly higher than your
  // maximum expected DOM depth.
  _maxNavDepth: 100,
  // ===GENERAL-PURPOSE PUBLIC UTILITY FUNCTIONS=========================
  // (NOTE: MOST OF THESE SHOULD BE MOVED TO JQUERY PLUGINS, UTILS, ETC.)
  nullOrUndefined: function nullOrUndefined(koan) {
    if (koan === null || typeof koan === 'undefined') {
      return true;
    }

    return false;
  },
  // Like "$.closest" but with an additional barrier filter and a maximum
  // number of levels.  All ancestors will be searched unless they match
  // the optional jQuery selector "barrier", in which case the search
  // stops and returns undefined.  If maxdepth is specified and exceeded,
  // the search stops and returns undefined.  If maxdepth is zero, only
  // the start is searched.  If a match is found, it is returned
  // immediately.  If no match is found all the way to the root element,
  // the function returns undefined.
  // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
  closestAncestor: function closestAncestor(start, target, barrier, maxdepth) {
    if (start === undefined || start === null) {
      // FAILURE, BAD START
      return undefined;
    }

    var iterator = $(start).parent();
    var depth = 1;

    while (iterator !== undefined) {
      if (maxdepth !== null && maxdepth !== undefined && depth >= maxdepth) {
        // FAILURE, RECURSION LIMIT EXCEEDED
        return undefined;
      }

      if (iterator.is(barrier)) {
        // FAILURE, BARRIER DETECTED
        return undefined;
      }

      if (iterator.is(target)) {
        // SUCCESS
        return iterator;
      }

      depth++;
      iterator = iterator.parent();
    } // FAILURE, NO ANCESTOR MATCHED CRITERIA


    return undefined;
  },
  // Like "$.closest" but working down.  Returns the first instance of
  // an element matching the jQuery selector "target" at or beneath the
  // element passed as "start".  All children will be recursively searched
  // unless they match the optional jQuery selector "barrier".  If "barrier"
  // is omitted, null, or undefined, no filtering is performed.  An optional
  // maximum depth can be specified as "maxdepth"; a value of 0 will search
  // nothing, because the starting point is not a solution candidate.
  // Negative values will abort immediately.  If this parameter is omitted,
  // null, or undefined, a library default is used.
  // TIPS:
  // - Keep in mind that either selector can use OR or NOT specifiers as
  //   usual for jQuery.
  // - "start" is never considered a match.
  // - If a node matches the target AND the barrier, it will still be
  //   returned.  The barrier prevents enumeration of children only, which
  //   is why it is named "barrier" and not "filter".
  // - If no candidate is found, the function returns undefined.
  // - This uses a breadth-first search.
  // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
  closestDescendant: function closestDescendant(start, target, barrier, maxdepth) {
    if (start === null || start === undefined || target === null || target === undefined) {
      // Parameters cannot result in a match.
      return undefined;
    }

    var effective_maxdepth;

    if (maxdepth === null || maxdepth === undefined) {
      effective_maxdepth = this._maxNavDepth;
    } else {
      effective_maxdepth = maxdepth;
    }

    var search_queue = []; // Perform a breadth-first search with the "start" element as the root.
    // Each iteration of this loop is a different absolute depth level,
    // relative to "start".
    // FIXME - This algorithm uses a naive queue because the depth is not
    // expected to be large.  Replace with a higher-performance queue if
    // one is available, or change this to TODO if no higher-performance
    // queue is available.

    var depth = 0,
        items_at_current_depth = 1,
        items_at_next_depth = 0,
        iterator = start;

    while (iterator !== undefined) {
      // See if the iterator is the element we're looking for.
      if ($(iterator).is(target) && depth > 0) {
        // Success!
        return iterator;
      } // See if its children should be enqueued for search.


      if (!$(iterator).is(barrier)) {
        var children = $(iterator).children();
        children.each(function (index, child) {
          // Enqueue children for search after this level is completed.
          search_queue.unshift(child);
          items_at_next_depth++;
        });
      } // See if this was our last element at this depth (our next dequeue
      // will be the first one at a lower depth), and swap tracking values
      // if it is.


      if (--items_at_current_depth === 0) {
        depth++;
        items_at_current_depth = items_at_next_depth;
        items_at_next_depth = 0;
      }

      if (depth > effective_maxdepth) {
        // Maximum depth exceeded, no match.
        return undefined;
      } // Get the next item to be checked.


      iterator = search_queue.pop();
    } // Search space exhausted, no match.


    return undefined;
  },
  zinit: function zinit(selector) {
    logger.debug('stdnav.init(' + selector + ")\n");
    return this;
  },
  // This is the node-to-navtype map.  This is used to determine the
  // default navtype for a given element.  Since all elements are
  // supported by plugins, this map is initially empty.
  navtype_nodeNames: {},
  //ToDo get object from stdnavDebugger.js
  // ===LIBRARY-SPECIFIC PUBLIC UTILITY FUNCTIONS========================
  // Returns true if the element is navigable.
  isNavigable: function isNavigable(element) {
    if ($(element).is(this._navigableFilter) && !$(element).is(this._unnavigableFilter)) {
      return true;
    }

    return false;
  },
  // Returns the closest parent or further ancestor that is navigable, or
  // undefined if the element is the shallowest navigable ancestor.
  // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
  closestNavigableAncestor: function closestNavigableAncestor(element) {
    return this.closestAncestor(element, this._navigableFilter, this._unnavigableFilter, this._maxNavDepth);
  },
  // Returns the closest child or further descendant that is navigable,
  // or undefined if no such descendant exists.
  // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
  closestNavigableDescendant: function closestNavigableDescendant(element) {
    return this.closestDescendant(element, this._navigableFilter, this._unnavigableFilter, this._maxNavDepth);
  },
  // ===PLUGIN MANAGEMENT FUNCTIONS======================================
  // Build the jQuery filters used to rapidly locate navigable elements.
  // This must be redone when plugins are registered.
  _rebuildNavigationFilters: function _rebuildNavigationFilters() {
    // Rebuild the jQuery filter used to determine if a component is
    // navigable.  This is a logical OR of all known nodeNames as well as
    // anything with a js-navtype explicitly set.  This whitelist is
    // applied first.
    var newFilter = '';
    $.each(this.navtype_nodeNames, function (navtype, nodeNames) {
      $.each(nodeNames, function (index, nodeName) {
        newFilter += nodeName + ',';
      });
    }); // Note that an element with js-navtype specified is still considered
    // unnavigable if no loaded plugin provides that navtype.  However, for
    // performance reasons, that check is only performed if we actually
    // have a candidate element.

    newFilter += '[js-navtype]';
    this._navigableFilter = newFilter; // Elements matching this filter are never navigable under any
    // circumstances.  This blacklist is applied last.

    this._unnavigableFilter = '[js-stdnav="false"],[js-navtype="none"]';
  },
  // Registers a new plugin.  Must be called by plugins in their AMD
  // loading sequence, NOT in their Activate methods.
  registerPlugin: function registerPlugin(name, plugin) {
    this.plugins[name] = plugin;
  },
  // Unregisters a plugin.  Only useful if you need to free the memory.
  unregisterPlugin: function unregisterPlugin(name) {
    this.plugins[name].unload();
    delete this.plugins[name];
  },
  // Registers a new navtype.  Intended for use by UI control modules,
  // such as JSAM (Jaspersoft Accessible Menus).  May be called at any
  // time, but it's probably wise to register all navtypes before
  // calling activate().
  //
  // Parameters:
  // navtype:    A string describing the new navigation type.
  //             (NOTE: navtypes are NOT case-sensitive)
  // behaviors:  An object mapping one or more of the nav actions to
  //             new callbacks, targets, or undefined.  Note that the
  //             value of "this" for these callbacks will be the
  //             stdnav object.
  // nodeNames:  An optional array of HTML node names (element types)
  //             that intrinsically imply the navtype.  Elements of this
  //             type will not require a "js-navtype" attribute to leverage
  //             the plugin's behavior; it becomes implied by default.
  registerNavtype: function registerNavtype(navtype, behaviors, nodeNames) {
    this.navtypeBehaviors[navtype.toLowerCase()] = behaviors;
    this.navtype_nodeNames[navtype.toLowerCase()] = nodeNames;

    this._rebuildNavigationFilters();
  },
  // Unregisters the navtype.  May be done at any time, for example, to
  // allow a user preference "Apply" to change navigation handlers.
  unregisterNavtype: function unregisterNavtype(navtype, behaviors) {
    delete this.navtypeBehaviors[navtype.toLowerCase()];
    delete this.navtype_nodeNames[navtype.toLowerCase()];

    this._rebuildNavigationFilters();
  },
  // ===PRIMARY ACTIVATION AND INITIALIZATION FUNCTIONS==================
  // Loads the library and initializes it.  After calling this function,
  // you may register plugins dynamically at any time.  When possible,
  // however, plugins should be loaded immediately after calling activate,
  // and before calling stdnav.start().  Calling stdnav.start() is still
  // required to initialize ARIA Live Regions by setting body[aria-busy] to
  // false.
  activate: function activate() {
    // This is the null behavior hash.  Its purpose is only to define valid
    // keys for the hash, but it is also used in some optimized overlays.
    // Any behavior keys used anywhere else MUST exist here.
    this.nullBehavior = {
      'ariaprep': null,
      'ariarefresh': null,
      'click': null,
      'down': null,
      'end': null,
      'enter': null,
      'exit': null,
      'fixfocus': null,
      'fixsubfocus': null,
      'fixsuperfocus': null,
      'focusin': null,
      'focusout': null,
      'home': null,
      'hoverin': null,
      'hoverout': null,
      'inherit': null,
      'inheritable': null,
      'left': null,
      'mousedown': null,
      'mouseover': null,
      'mouseout': null,
      'mouseup': null,
      'next': null,
      'pagedown': null,
      'pageup': null,
      'prev': null,
      'rejoined': null,
      'right': null,
      'subfocusin': null,
      'subfocusout': null,
      'superfocusin': null,
      'superfocusout': null,
      'toggle': null,
      'touchend': null,
      'touchstart': null,
      'up': null
    }; // This is the root "default" behaviour hash.  These defaults pass
    // everything through to the browser, and are normally overridden
    // with $.extend based on specific node names and stdnav attributes.

    this.defaultBehavior = {
      //          'click': [this, this.basicClick, null],
      'enter': [this, this.basicEnter, null],
      'toggle': [this, this.basicToggle, null],
      'exit': [this, this.basicExit, null],
      'fixfocus': [this, this.basicFixFocus, null],
      'fixsubfocus': [this, this.basicFixSubfocus, null],
      'fixsuperfocus': [this, this.basicFixSuperfocus, null],
      'focusin': [this, this.basicFocusIn, {
        'maxdepth': 0
      }],
      'focusout': [this, this.basicFocusOut, null],
      // By default, overrides in parents should be used (unless those parents
      // have inheritable===false).
      'inherit': true,
      // By default, allow overrides to apply to any children with inherit===true.
      'inheritable': true,
      'rejoined': [this, this.basicRejoined, null],
      'subfocusin': [this, this.basicSubfocusIn, null],
      'subfocusout': [this, this.basicSubfocusOut, null],
      'superfocusin': [this, this.basicSuperfocusIn, null],
      'superfocusout': [this, this.basicSuperfocusOut, {
        'ghostfocus': false
      }]
    };
    this.navtypeBehaviors = {
      'block': {},
      'global': {},
      'inline': {},
      'modal': {
        'next': this._onModalNext,
        'inherit': false,
        'inheritable': true
      }
    }; // Initialize state management

    this._refocusing = false;
    this._priorSuperfocus = null;
    this._priorFocus = null;
    this._priorSubfocus = null;
    /*
    this._currentSuperfocus = $('body')[0];
    this._currentSubfocus = $('body')[0];
    $(this._currentSuperfocus).addClass('superfocus');
    $(this._currentSubfocus).addClass('subfocus');
    */

    this._currentSuperfocus = document.activeElement;
    this._currentFocus = document.activeElement;
    this._currentSubfocus = document.activeElement;
    $(this._currentSuperfocus).addClass('superfocus');
    $(this._currentSubfocus).addClass('subfocus'); // Build the jQuery filters used to rapidly locate navigable elements.
    // This must be redone when plugins are registered.

    this._rebuildNavigationFilters();

    wrapEventHandlersIntoStdnavAvailabilityAspect(this);
    this.$body = $("body");

    this._bindFocusEvents();

    this._bindKeyboardEvents();

    this._bindMouseEvents();

    this._bindTouchEvents();
  },
  // Initializes ARIA Live Regions and performs final initial static
  // analysis, if any, prior to dynamic monitoring.  Finally, forces focus
  // to move to the LAST element found with the CSS class
  // "stdnavinitialfocus" (only one should be defined, however).
  // Call this function ONLY when the main DOM has been fully rendered
  // (it's okay if large, slow resources are not yet loaded).
  start: function start() {
    // WARNING-- THIS IS *NOT* REDUNDANT WITH TABINDEX VALUES!
    // Do not change this without understanding the accessibility issues
    // involved.  It is here to force correct initial browser focus even
    // when screen readers are in use, due to screen reader bugs in ARIA
    // support.
    //
    // If the templates get the tab order correct (which cannot always
    // be done), the above code may not actually result in a focus
    // change.  This means the focus events may not be triggered, and
    // as a result, the superfocus and subfocus reevaluation
    // code will not run.  Therefore, force a focus change.
    var $initialFocus = $('.stdnavinitialfocus');

    if ($initialFocus.length < 1) {
      if (document.activeElement && document.activeElement != $('body')[0]) {
        $initialFocus = $(document.activeElement);
      } else {
        $initialFocus = $('body').children("[tabindex='0']:first");

        if ($initialFocus.length < 1) {
          // Give up
          $initialFocus = $('body');
        }
      }
    } // Hack for IE11, force a potential visual change


    if (browserDetection.isIE11()) {
      this.forceFocus($('body')[0]);
      $('#IECM').html('&nbsp;');

      this._unforceFocus($('body')[0]);

      $('body').blur();
      $('#IECM').html('&nbsp;&nbsp;');
    }

    this.forceFocus($initialFocus[$initialFocus.length - 1]);
    $('body').attr('aria-busy', false);
    $('#ariastatus').attr('aria-label', "Standard Navigation initialized.");
    this.updateDebugInfo();
  },
  // ===CLEANUP AND SHUTDOWN FUNCTIONS===================================
  // Stops dynamic DOM monitoring without unbinding keyboard or mouse
  // events.  Presently no effect.
  stop: function stop() {},
  // Unbind the keyboard and mouse handlers.
  deactivate: function deactivate() {
    this._unbindTouchEvents();

    this._unbindMouseEvents();

    this._unbindKeyboardEvents();

    this._unbindFocusEvents();
  },
  // ===DOM EVENT HANDLERS, EVENT BINDING AND UNBINDING==================
  _bindFocusEvents: function _bindFocusEvents() {
    this.$body.on('focusin', this._onFocusIn);
    this.$body.on('focusout', this._onFocusOut);
  },
  _bindKeyboardEvents: function _bindKeyboardEvents() {
    $(document).on('keydown', this._onKeydown); // TODO: Evaluate whether focus and blur have better compatibility
  },
  _bindMouseEvents: function _bindMouseEvents() {
    this.$body.on('click', this._onClick);
    this.$body.on('dblclick', this._onDblClick);
    this.$body.on('mousedown', this._onMouseDown);
    this.$body.on('mouseover', this._onMouseOut);
    this.$body.on('mouseout', this._onMouseOver);
    this.$body.on('mouseup', this._onMouseUp);
    this.$body.on('mouseover', '[aria-label]', this._onLabeledTagOver);
  },
  _bindTouchEvents: function _bindTouchEvents() {
    this.$body.on('touchend', this._onTouchStart);
    this.$body.on('touchstart', this._onTouchStart);
  },
  _unbindTouchEvents: function _unbindTouchEvents() {
    this.$body.off('touchend', this._onTouchStart);
    this.$body.off('touchstart', this._onTouchStart);
  },
  _unbindMouseEvents: function _unbindMouseEvents() {
    this.$body.off('click', this._onClick);
    this.$body.off('dblclick', this._onDblClick);
    this.$body.off('mousedown', this._onMouseDown);
    this.$body.off('mouseover', this._onMouseOut);
    this.$body.off('mouseout', this._onMouseOver);
    this.$body.off('mouseup', this._onMouseUp);
  },
  _unbindKeyboardEvents: function _unbindKeyboardEvents() {
    $(document).off('keydown', this._onKeydown);
  },
  _unbindFocusEvents: function _unbindFocusEvents() {
    this.$body.off('focusin', this._onFocusIn);
    this.$body.off('focusout', this._onFocusOut);
  },
  // ===BEHAVIOR ASSEMBLY================================================
  // Takes a jQuery element and builds the a behavior overlay hash composed
  // of that element's explicit overrides only.
  // FIXME - Try to come up with a more generic mechanism so this isn't
  // brittle if we add new attributes/overrides.  However, this may be
  // complicated by differences in JS and HTML naming conventions in our
  // coding standards.  Also, see if we can optimize this by quickly
  // building a js-stdnav-* array/hash.
  _getExplicitBehavior: function _getExplicitBehavior(el) {
    var overlay = {};

    if (el.attr('js-stdanv-enter') !== undefined) {
      overlay['enter'] = el.attr('js-stdnav-enter');
    }

    if (el.attr('js-stdanv-exit') !== undefined) {
      overlay['exit'] = el.attr('js-stdnav-exit');
    }

    if (el.attr('js-stdanv-toggle') !== undefined) {
      overlay['toggle'] = el.attr('js-stdnav-toggle');
    }

    if (el.attr('js-stdanv-rejoin') !== undefined) {
      overlay['rejoin'] = el.attr('js-stdnav-rejoin');
    }

    if (el.attr('js-stdanv-up') !== undefined) {
      overlay['up'] = el.attr('js-stdnav-up');
    }

    if (el.attr('js-stdanv-down') !== undefined) {
      overlay['down'] = el.attr('js-stdnav-down');
    }

    if (el.attr('js-stdanv-left') !== undefined) {
      overlay['left'] = el.attr('js-stdnav-left');
    }

    if (el.attr('js-stdanv-right') !== undefined) {
      overlay['right'] = el.attr('js-stdnav-right');
    }

    if (el.attr('js-stdanv-next') !== undefined) {
      overlay['next'] = el.attr('js-stdnav-next');
    }

    if (el.attr('js-stdanv-prev') !== undefined) {
      overlay['prev'] = el.attr('js-stdnav-prev');
    }

    if (el.attr('js-stdanv-inherit') !== undefined) {
      overlay['inherit'] = el.attr('js-stdnav-inherit');
    }

    if (el.attr('js-stdanv-inheritable') !== undefined) {
      overlay['inheritable'] = el.attr('js-stdnav-inheritable');
    }

    return overlay;
  },
  // Builds the effective navigation behavior hash for the ancestry of the element,
  // NOT including the element itself.
  //
  // Right now, this includes node/element behavior, navtype behavior, and explicit
  // overrides; it may be more appropriate include only explicit overrides.
  //
  // Traversal stops when any of the following are true:
  // - The iterator has "inherit='false'"
  // - The parent has "inheritable='false'"
  // - The root is reached
  // - The parent is an IFRAME
  //
  // PERFORMANCE / MEMORY NOTE: RECURSIVE SOLUTION
  _buildParentBehaviorOBSOLETE: function _buildParentBehaviorOBSOLETE(element) {
    // IMPORTANT - DO NOT "OPTIMIZE" THIS BY JUST RETURNING
    // this.defaultBehavior!  We need to use a new object, because it gets
    // modified recursively!  This code does a deep copy:
    //        var compositeBehavior = $.extend(true, {}, this.defaultBehavior);
    // NOTE: the case where:
    //    element.prop('js-stdnav-inherit')==false
    // is ruled out by the caller.
    // ABORT CASE: Somehow, element has no parent.  Shouldn't happen,
    // but might occur in a testing framework, or detached DOM objects.
    var parentEl = $(element.parent()[0]);

    if (parentEl === undefined) {
      //return compositeBehavior;
      return this.defaultBehavior;
    } // ALL REMAINING CASES REQUIRE COMPUTATION OF PARENT BEHAVIOR
    // Therefore, we must now determine the parent's immediate behavior, so
    // we can figure out if we should 1) overlay it, and, 2) go further.


    var parentImmediateBehavior = this._buildImmediateBehavior(parentEl);

    if (parentImmediateBehavior['inheritable'] === false) {
      // BASE CASE: Parent disallows inheritance by element.
      return this.defaultBehavior;
    } // ALL REMAINING CASES ALLOW PARENT INHERITANCE
    // (the parent's behavior will be overlayed on the defaults and returned)
    // The remaining variants may or may not allow GRANDPARENT inheritance.


    if (parentImmediateBehavior['inherit'] === false || parentEl.is('body,iframe')) {
      // BASE CASE: Parent allows inheritance of itself but disallows inheritance of grandparent.
      // BASE CASE: Parent is BODY; no grandparent to check.
      // BASE CASE: Parent is an IFRAME; treat as document root.
      return $.extend({}, true, this.defaultBehavior, parentImmediateBehavior);
    } // RECURSIVE CASE
    // Go check the grandparent.


    var grandparentBehavior = this._buildParentBehavior(parentEl); // The special option 'inherit' is not, itself, inheritable; override
    // it back to true.


    return $.extend({}, true, grandparentBehavior, parentImmediateBehavior, {
      'inherit': false
    });
  },
  // Builds the effective navigation behavior hash for an element.
  //
  // This is a non-recursive version of the previous approach, to improve
  // performance, reduce stack overhead (critical), improve debugging, and
  // provide a failsafe bailout.
  //
  // The new approach is:
  // - copy the NULL behavior hash to a new object
  // - for each ancestor, build the immediate behavior for that ancestor
  // - use that behavior to fill in ONLY non-NULL elements in the working hash
  // - abort when inherit==false or parent->inheritable==false
  // - also abort if maximum ancestry depth is reached (failsafe)
  // - fill in remaining behavior from defaults hash
  //
  // ...basically, an underlay instead of an overlay.
  //
  // NOTE: This function returns a deep-copy throwaway you can modify at
  // will.
  _buildBehavior: function _buildBehavior(element) {
    // Make a copy of the default behavior, or you'll corrupt it!  And make
    // sure it's a DEEP copy, or you won't get the arrays and subobjects!
    var behavior = $.extend(true, {}, this.nullBehavior);
    var iter = element;
    var height = 0;

    var immediateBehavior = this._buildImmediateBehavior(iter);

    while (height < this._maxNavDepth) {
      // UNDERLAY this node's behavior
      $.each(immediateBehavior, function (key, value) {
        if (key in behavior) {
          if (behavior[key] === null) {
            // This key is known, but has not been seen yet, use the
            // ancestor's value.
            behavior[key] = value;
          } // ...else, the key has been overridden by a descendant and this
          // ancestor value is therefore unimportant.

        } else {
          // The key does not yet exist in the behavior hash.  This
          // indicates the behavior is missing from the NULL behavior hash,
          // which will cause performance issues.  However, it is not fatal,
          // so recover.  TEST THIS WITH A BREAKPOINT AND TAG IT DURING
          // PERFORMANCE AUDITS!
          logger.debug("StdNav: Key '" + key + "' is missing from the NULL-behavior hash");
          behavior[key] = value;
        }
      });

      if (immediateBehavior['inherit'] === false) {
        // Node does not want to inherit parent behavior, stop search.
        break;
      }

      height++;
      iter = $(iter).parent()[0];
      immediateBehavior = this._buildImmediateBehavior(iter);

      if (immediateBehavior['inheritable'] === false) {
        // Parent is not inheritable, stop search.
        break;
      }
    } // Fill in any remaining values from the default behavior hash.


    $.each(this.defaultBehavior, function (key, value) {
      if (key in behavior) {
        if (behavior[key] === null) {
          // This key is known, but has not been seen yet, use the
          // default value.
          behavior[key] = value;
        } // ...else, the key has been specified in the DOM, and the default
        // value is therefore unimportant.

      } else {
        // The key does not yet exist in the behavior hash.  This
        // indicates the behavior is missing from the NULL behavior hash,
        // which will cause performance issues.  However, it is not fatal,
        // so recover.  TEST THIS WITH A BREAKPOINT AND TAG IT DURING
        // PERFORMANCE AUDITS!
        logger.debug("StdNav: Key '" + key + "' is defined in defaults but missing from the NULL-behavior hash");
        behavior[key] = value;
      }
    }); // IMPORTANT!  The underlay technique above assigns SHALLOW COPIES.
    // That would be fine if we could count on being able to return a
    // const object, but that's not widely supported yet, so instead, we
    // fix the result into a new object with a deep copy.

    return $.extend(true, {}, behavior);
  },
  // Takes a DOM element and builds the "immediate behavior"-- the behavior
  // from the node/element type, navtype, and explicit overrides, but NOT
  // the ancestors and defaults.
  _buildImmediateBehavior: function _buildImmediateBehavior(element) {
    var self = this; // Do something sensible if subfocus was unset and we got bad input.

    if (this.nullOrUndefined(element)) {
      return this.defaultBehavior;
    }

    var el = $(element); // Figure out the default navtype for the node/element type.

    var defaultNavtype;
    var nodeName = el.prop('nodeName');
    $.each(this.navtype_nodeNames, function (navtype, supportedNodeNames) {
      if ($.inArray(nodeName, supportedNodeNames) > -1) {
        if (self.isNavigable(element)) {
          defaultNavtype = navtype;
        }
      }
    });
    var nodeBehavior = {};

    if (defaultNavtype !== undefined) {
      nodeBehavior = this.navtypeBehaviors[defaultNavtype];
    } // Determine the navtype explicitly requested, if any.
    // NOTE: Children inherit the resulting _behavior_ of a parent with
    // an explicit navtype; they do not inherit the navtype attribute
    // itself.  This means, for example, that a particular child cell in
    // a table or grid can have a navtype that provides special behavior
    // for a few keystrokes but otherwise leaves the rest of the behavior
    // as it was for the higher-level, more complete navtype.  While this
    // can also be done by using explicit behavior, using a navtype is
    // more efficient, as the callback lookup does not need to be parsed.


    var navtype = el.attr('js-navtype');
    var navtypeBehavior = {};

    if (navtype !== undefined) {
      navtypeBehavior = this.navtypeBehaviors[navtype.toLowerCase()];
    } // Get other explicitly-specified behavior for the element, if any.


    var explicitBehavior = this._getExplicitBehavior(el); // FIXME-- this may be expensive in actual use, consider just checking required tags
    // Return the overlaid "immediate behavior" hash.


    return $.extend(true, {
      'inherit': true
    }, nodeBehavior, navtypeBehavior, explicitBehavior);
  },
  // Takes a DOM element and builds the effective navigation behavior hash.
  _buildBehaviorOBSOLETE: function _buildBehaviorOBSOLETE(element) {
    // Do something sensible if subfocus was unset and we got bad input.
    if (element === undefined || element === null) {
      return this.defaultBehavior;
    }

    var el = $(element); // BUILD THE EFFECTIVE BEHAVIOR HASH FROM THE OVERLAYS
    // A. Build the inherited behavior:
    //  1. Start with default behavior (normally none).
    //  2. Overlay any inheritable behavior.
    // B. Build the immediate behavior:
    //  3. Overlay node/element type behavior.
    //  4. Overlay explicit navtype behavior, if specified.
    //  5. Finally, overlay any other explicitly-specified behavior.

    var immediateBehavior = this._buildImmediateBehavior(el);

    var inheritedBehavior = {};

    if (immediateBehavior['inherit'] === true) {
      inheritedBehavior = this._buildParentBehavior(el);
    }

    return $.extend(true, inheritedBehavior, immediateBehavior);
  },
  //ToDo get object from stdnavFocusing!
  //ToDo get object from stdnavEventHandlers!
  // ===ACTION LOGIC=====================================================
  // Runs the action defined in the effective behavior appropriate for a given element.
  // This function may be called by a testing framework or other automation.
  runAction: function runAction(actionName, element) {
    if (element === null) {
      logger.debug("tried to run action '" + actionName + "' on null element");
      return;
    }

    if (typeof element === 'undefined') {
      logger.debug("tried to run action '" + actionName + "' on undefined element");
      return;
    }

    logger.debug("stdnav.runAction(" + actionName + ", " + element.nodeName + "#" + element.id + ")");

    var behavior = this._buildBehavior(element);

    if (!this.nullOrUndefined(behavior[actionName])) {
      return this._runActionDesc(behavior[actionName], element);
    }
  },
  // Runs the action described by the object provided.  Normally called
  // by runAction, which will figure out the behavior appropriate for a
  // given element.
  //
  // 1. A string that specifies the ID of an element to navigate to,
  //    preceded by a '#'.  The element will be given the focus and/or
  //    subfocus, as appropriate.  You can also use these special codes:
  //    "##parent"    Parent element.
  //    "##child"     First child element.
  //    "##prev"      Previous sibling element.
  //    "##next"      Next sibling element.
  //    These actions come from behavior explicitly specified in the markup.
  //    Examples: "#otherDiv"
  //              "##parent"
  //
  // 2. A string that specifies a function to call, the object to call
  //    it on, and any optional parameters that can be reasonably
  //    specified as substrings, preceded by a '@' if the object name
  //    is registered with stdnav, or '@@' if the value of
  //    "data-stdnav-context" should be used as the object/context.
  //    These actions also come from behavior explicitly specified in
  //    the markup.
  //    Examples: "@myObj:myFunc('param1', 'param2', 'paramChicken')"
  //              "@myObj:myFunc"
  //              "@@myFunc('param1', 'parm2')"
  //              "@@myFunc"
  //
  // 3. An array that specifies a context object, callback function,
  //    and parameter object (or null).  These actions normally come
  //    from behavior overalys in StdNav plugins, or default behavior
  //    in StdNav itself.  While they can be changed dynamically, this
  //    is not usually the best approach.
  //
  // 4. If actionDesc is null, no action is taken.  However, the event
  //    handler that has called this function is expected to NOT stop
  //    event propagation in this case.  Explicitly specifying null as the
  //    event handler-- as opposed to leaving the key out entirely-- tells
  //    the system that something other than StdNav is going to handle the
  //    event.
  _runActionDesc: function _runActionDesc(actionDesc, element) {
    var retval = true;

    if (typeof actionDesc === "string" || actionDesc instanceof String) {
      // A string can be used to indicate a simple subfocus change to a new
      // element, for example, "#someOtherDiv".
      if (actionDesc.substr(0, 1) == "#") {
        if (actionDesc == "##parent") {
          this.forceFocus($(element).parent());
        } else if (actionDesc == "##child") {
          var children = $(element).children();

          if (children.length > 0) {
            this.forceFocus($(element).children()[0]);
          }
        } else if (actionDesc == "##prev") {
          var prev = $(element).prev();

          if (prev.length > 0) {
            this.forceFocus($(element).prev()[0]);
          }
        } else if (actionDesc == "##next") {
          var next = $(element).next();

          if (next.length > 0) {
            this.forceFocus($(element).next()[0]);
          }
        } else {
          var newEl = $(actionDesc);

          if (newEl !== undefined) {
            this.forceFocus(newEl);
          }
        }
      } else if (actionDesc.substr(0, 1) == '@') {
        var funcName, func, paramstr, colon, lparen, rparen; // If no context is specified, use the stdnav instance.

        var context = this;

        if (actionDesc.substr(1, 1) == '@') {
          context = $(element).data('stdnav-context');
        } else {
          colon = actionDesc.indexOf(':');

          if (colon > -1) {
            var contextName = actionDesc.substr(0, colon);
            context = this.actionContexts[contextName];
          }
        }

        lparen = actionDesc.indexOf('(');

        if (lparen == -1) {
          funcName = actionDesc.substr(2);
        } else {
          rparen = actionDesc.indexOf(')');
          funcName = actionDesc.substr(2, lparen - 2);

          if (rparen == -1) {
            logger.debug('Bad stdnav action: ' + actionDesc);
          } else {
            paramstr = actionDesc.substr(lparen, rparen - lparen);
          }
        }

        func = context[funcName];
        retval = func.call(context, paramstr);
      }
    } else if (actionDesc instanceof Array) {
      // The format is [context, callback, paramsObject].
      if (actionDesc[1] === undefined) {
        logger.debug("undefined actionDesc[1]");
      }

      var context = actionDesc[0];

      if (context === null || typeof context === 'undefined') {
        context = this;
      }

      var callback = actionDesc[1];
      var params = actionDesc[2];

      if (params === null || typeof params === 'undefined') {
        params = {};
      }

      retval = callback.call(context, element, params);
    }

    return retval;
  }
});

function isStdnavEnabledForElement(el) {
  var parents = $(el).parents(),
      isEnabled;

  for (var n = 0; n < parents.length; n++) {
    var attrValue = $(parents[n]).attr('js-stdnav');

    if (attrValue === 'true') {
      isEnabled = true;
    } else if (attrValue === 'false') {
      isEnabled = false;
    }

    if (_.isBoolean(isEnabled)) {
      break;
    }
  }

  return Boolean(isEnabled);
}

function stdnavAvailabilityAroundAspect(invocation, ev) {
  var args = Array.prototype.slice.call(arguments, 1);

  if (isStdnavEnabledForElement(ev.target)) {
    return invocation.apply(this, args);
  }

  logger.debug("StdNav is disabled in this subdom, aborting action");
}

function wrapEventHandlersIntoStdnavAvailabilityAspect(context) {
  var noop = function noop() {};

  var handlers = ["_onFocusIn", "_onFocusOut", "_onKeydown", "_onClick", "_onDblClick", "_onMouseDown", "_onMouseOut", "_onMouseOver", "_onMouseUp", "_onLabeledTagOver", "_onTouchStart"];

  _.each(handlers, function (handler) {
    var originalHandler = context[handler];

    if (originalHandler) {
      context[handler] = _.bind(stdnavAvailabilityAroundAspect, context, originalHandler);
    } else {
      context[handler] = noop;
    }
  });
}

module.exports = new stdnav();

});