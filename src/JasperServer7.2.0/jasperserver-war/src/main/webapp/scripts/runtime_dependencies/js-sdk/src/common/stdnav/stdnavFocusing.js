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

/* Standard Navigation library (stdnav) extension
 * ------------------------------------
 * Focus management handlers
 *
 */

define(function (require, exports, module) {
    "use strict";
    var
        $ = require("jquery"),
        _ = require("underscore"),
        logger = require("logger").register(module);

    return {

        // Returns true if the element is PROGRAMMATICALLY focusable-- in other
        // words, whether or not JavaScript logic can move focus to this
        // element.  This does NOT necessarily mean the TAB key can move to
        // this element, or that it will receive focus automatically if clicked
        // on; it simply means an element.focus() call should succeed.
        //
        // Element type is checked, then we check to see if the element is\
        // enabled, then we check explicit overrides from tabindex.
        //
        // Note that not all navigable controls can be focused.  For example, a
        // table cell is navigable if the table plugin is loaded, but is not
        // normally focusable (though the table itself will be if the table
        // element specifies a tabindex).
        isLogicFocusable: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.error("isLogicFocusable called on a null or undefined element");
                return false;
            }

            var $el = $(element),
                canFocus = false,
                nodeName = $el.prop('nodeName'),
                tabindex = $el.attr('tabindex');

            // These elements are always focusable (...unless you're in Safari
            // and haven't turned on keyboard accessibility!)
            if ($el.is(':input') || ($.inArray(nodeName, ['A', 'BUTTON', 'INPUT', 'SELECT', 'OBJECT', 'TEXTAREA']) > -1)) {
                // ...unless they've been disabled.
                if ($el.is(':enabled')) {
                    canFocus = true;
                }
            }

            /* else if (nodeName == 'A') {
              // Anchor tags are only focusable if they are clickable links.
              // NOTE: Anchor tags are sometimes given explicit tabindexes anyway.
              if (el.attr('href') != undefined) {
                canFocus = true;
              }
            }*/

            // If any tabindex value has been manually assigned, the control
            // can receive focus through JS logic.
            if ($el.is('[tabindex]')) {
                canFocus=true;
            }
            return canFocus;
        },

        // Returns true if the element is USER focusable-- the TAB key, a mouse
        // click, or a touch can focus this element.  This DOES imply that the
        // element can be given focus via JavaScript logic such as
        // element.focus().
        //
        // Element type is checked, then we check to see if the element is\
        // enabled, then we check explicit overrides from tabindex.
        //
        // Note that not all navigable controls can be focused.  For example, a
        // table cell is navigable if the table plugin is loaded, but is not
        // normally focusable (though the table itself will be if the table
        // element specifies a tabindex).
        isUserFocusable: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.error("isUserFocusable called on a null or undefined element");
                return false;
            }
            var el = $(element),
                canFocus = false,
                nodeName = el.prop('nodeName'),
                tabindex = el.attr('tabindex');

            // These elements are always focusable...
            if (el.is(':input') || ($.inArray(nodeName, ['A', 'BUTTON', 'INPUT', 'SELECT', 'OBJECT', 'TEXTAREA']) > -1)) {
                // ...unless they've been disabled.
                if (el.is(':enabled')) {
                    canFocus = true;
                }
            }

            /* else if (nodeName == 'A') {
              // Anchor tags are only focusable if they are clickable links.
              // NOTE: Anchor tags are sometimes given explicit tabindexes anyway.
              if (el.attr('href') != undefined) {
                canFocus = true;
              }
            }*/

            // Explicit overrides
            if (tabindex !== undefined) {
                if (tabindex === '-1') {
                    canFocus = false;
                } else {
                    // tabindex>=0
                    canFocus = true;
                }
            }
            return canFocus;
        },

        // This function temporarily makes an element unfocusable.  It is
        // normally used to make a container unfocusable while focused on a
        // contained element, so that SHIFT-TAB will navigate to the PRIOR
        // container, rather than merely the contained element's container
        // (which would immediately promote focus back to the contained element,
        // giving no net change).
        //
        // - Unfocusable items will not become focusable.
        // - Redundant calls are harmless.
        suspendFocusability: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.warn('stdnav.suspendFocusability called on null or undefined element');
                return;
            }
            var $el = $(element);
            if (!this.nullOrUndefined($el.attr('js-suspended-tabindex'))) {
                // Redundant call, abort.
                logger.warn('stdnav.suspendFocusability called on already suspended element ' + element.nodeType + '#' + element.id);
                return;
            }
            var currentTabindex = $el.attr('tabindex');
            if (!this.nullOrUndefined(currentTabindex)) {
                $el.attr('js-suspended-tabindex', currentTabindex);
            } else {
                $el.attr('js-suspended-tabindex', 'none');
            }
            // Explicitly make the element unfocusable-- temporarily.
            $el.attr('tabindex', '-1');
        },

        resumeFocusability: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.warn('stdnav.resumeFocusability called on null or undefined element');
                return;
            }
            var $el = $(element);
            var suspendedTabindex = $el.attr('js-suspended-tabindex');
            if (this.nullOrUndefined(suspendedTabindex)) {
                // Out-of-order call, abort.
                logger.warn('stdnav.resumeFocusability called on non-suspended element ' + element.nodeName + '#' + element.id);
                return;
            }
            if (suspendedTabindex === 'none') {
                // No tabindex was present, so remove the temporary one.
                $el.removeAttr('tabindex');
            } else {
                // The original tabindex must have explicitly been given a negative
                // number to suppress normal tab navigation; restore that value.
                $el.attr('tabindex', suspendedTabindex);
            }
            $el.removeAttr('js-suspended-tabindex');
        },

        // There are various weird ways we can lose focus beyond normal UI
        // actions and JavaScript logic-- ALT-TABbing to another app, etc.
        // For embedded uses, "alert" popups in third-party apps can cause
        // this.  And, the debugging tools can cause this-- which is very
        // frustrating.  That can result in a loss of focusability and cause
        // various other problems.  Therefore, this function looks for
        // anything with a suspended-tabindex and restores it, _unless_ it
        // is an ancestor of the element passed-- this avoid breaking
        // SHIFT-TAB, since most containers will auto-promote focus to their
        // children.
        ensureFocusabilityBeyond: function(element){
            var $suspendees=$('[suspended-tabindex]');
            $suspendees.each(function(suspendee){
                if (!$.contains(suspendee, element)){
                    var $suspendee=$(suspendee);
                    var suspendedTabindex=$suspendee.attr('js-suspended-tabindex');
                    $suspendee.attr('tabindex', suspendedTabindex);
                    $suspendee.removeAttr('js-suspended-tabindex');
                }
            });
        },



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////__F_O_C_U_S__I_N__////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // Set browser focus if and only if it will not be redundant.
        // Additionally, if the element is not normally focusable, keep track of
        // the reason why, but make it temporarily focusable anyway.  Finally,
        // if an ancestor has a tabindex greater than 0, use that value.
        //
        forceFocus: function (element) {
            logger.debug("stdnav.forceFocus(" + element.nodeName + "#" + element.id + ")");
            var $el = $(element);

            // See if we should use a tabindex of zero, or something else.  Keep in mind
            // that we need to check _suspended_ tabindexes as well, or the logic that
            // lets SHIFT-TAB get out of a region will break this.  For (example, a list
            // suspends focus on its UL while an LI has focus, so that SHIFT-TAB doesn't
            // just refocus the list.  But if the UL has tabindex=2, which will have
            // been suspended, we still want the LIs to get tabindex=2, to avoid breaking
            // the tab cycle in Internet Explorer.)
            var newTabIndex = 0;
            $.each($el.parents('[tabindex]'), function(index, ancestor){
                var $ancestor = $(ancestor);
                if ($ancestor.attr('tabindex')>newTabIndex) {
                    newTabIndex=$ancestor.attr('tabindex');
                }
                if ($ancestor.attr('js-suspended-tabindex')>newTabIndex) {
                    newTabIndex=$ancestor.attr('js-suspended-tabindex');
                }
            });

            if (!$el.is($(document.activeElement))) {
                // Avoid redundant focus sets.
                if ( (!this.isLogicFocusable(element)) || (newTabIndex>$el.attr('tabindex')) ) {
                    // Temporarily make the element focusable or have a higher tabindex.
                    // This SHOULD be undefined, but if it IS, leave things
                    // alone.
                    if (!$el.attr('priortabindex')){
                        var priorTabindex = $el.attr('tabindex');
                        if (priorTabindex !== undefined) {
                            $el.data('prior-tabindex', priorTabindex);
                        } else {
                            $el.data('prior-tabindex', 'none');
                        }
                        // Explicitly make the element focusable-- temporarily.
                        $el.attr('tabindex', newTabIndex);
                    }
                }
            }
            $(element).focus();
        },

        // Callback run when an element in the DOM is about to relinquish subfocus.
        // Only fires if this happens through stdnav.setSubfocus!
        //_prepSubfocusOut: function(element) {},
        // OBSOLETE

        // Sets superfocus to the element provided, if it can take superfocus, or
        // to the closest ancestor that can.  If no ancestor can take superfocus,
        // superfocus falls back to BODY.  If the new superfocus is the same as
        // the current superfocus, nothing happens and the function returns
        // immediately.
        //
        // If the superfocus is indeed different, the following things occur, in
        // this order:
        // 1. The "superfocusout" action runs on the prior superfocus element.
        // 2. The superfocus CSS style is removed from the prior superfocus element.
        // 3. The superfocus CSS style is added to the new superfocus element.
        // 4. The "superfocusin" action runs on the new superfocus element.
        // 4A. This action MUST set browser focus appropriately.
        //
        setSuperfocus: function (element, fireCallbacks) {
            logger.debug("stdnav.setSuperfocus(" + element.nodeName + "#" + element.id + ", " + fireCallbacks + ")");
            if (fireCallbacks === undefined) {
                fireCallbacks = true;
            }
            var oldSubfocus = $('.subfocus');
            if (oldSubfocus.length > 0) {
                if (fireCallbacks) {
                    // Call the subfocusout action on the old subfocus.
                    this.runAction('subfocusout', oldSubfocus[0]);
                }
                // Remove subfocus from the current (prior) element, if any (should be exactly one at all times).
                oldSubfocus.removeClass('subfocus');
            }

            // Take subfocus.
            $(element).addClass('subfocus');

            if (fireCallbacks) {
                // This default behavior, which ensures subfocus is always within
                // browser focus, runs no matter what.
                this._prepSubfocusIn(element);

                // Call the subfocusin action on the new subfocus.
                // This behavior can be overridden.
                this.runAction('subfocusin', element);
            }
        },

        // Sets subfocus to the element provided.
        // Your plugin should normally use this function instead of messing with
        // the "subfocus" class directly.
        // Using it ensures:
        //
        // - Exactly one element has subfocus at all times.
        //
        // - Subfocus never falls outside the inclusive subdom of the element
        //   that has superfocus.  Main focus will move if necessary.
        //
        // - Plugins can receive event callbacks when subfocus changes.
        //
        // fireCallbacks is optional, and defaults to true.
        setSubfocus: function (element, fireCallbacks) {
            logger.debug("stdnav.setSubfocus(" + element.nodeName + "#" + element.id + ", " + fireCallbacks + ")");
            if (fireCallbacks === undefined) {
                fireCallbacks = true;
            }
            var oldSubfocus = $('.subfocus');
            if (oldSubfocus.length > 0) {
                if (fireCallbacks) {
                    // Call the subfocusout action on the old subfocus.
                    this.runAction('subfocusout', oldSubfocus[0]);
                }
                // Remove subfocus from the current (prior) element, if any (should be exactly one at all times).
                oldSubfocus.removeClass('subfocus');
            }

            // Take subfocus.
            $(element).addClass('subfocus');

            if (fireCallbacks) {
                // This default behavior, which ensures subfocus is always within
                // browser focus, runs no matter what.
                this._prepSubfocusIn(element);

                // Call the subfocusin action on the new subfocus.
                // This behavior can be overridden.
                this.runAction('subfocusin', element);
            }
        },

        // Callback run when an element in the DOM has acquired focus.
        _onFocusIn: function (ev) {
            this._currentFocus = ev.target;
            var fixedFocus = null,
                $target = $(ev.target);

            if ($target.attr("aria-label")) {
                $target.removeAttr("title");
            }

            logger.info("stdnav._onFocusIn  ev.target==" + ev.target.nodeName + "#" + ev.target.id);
            this.updateDebugInfo();

            // Hack for IE11, force a potential visual change
            var chaff='';
            for (var n=0; n<this.chaffLength; n++) {
                chaff+='&nbsp;';
            }
            $('#IECM').html(chaff);
            this.chaffLength=(this.chaffLength+1)%this.maxChaffLength;
            if (this.chaffLength===0){
                this.chaffLength=1;
            }

            // Sometimes this happens...
            if ($(ev.target).length < 1) {
                // Safety
                this._refocusing = false;
                return;
            }
            var eventFocus = ev.target;

            // Figure out what element should actually have focus.  For example,
            // a table may have an initial tabstop, but the cells should actually
            // get focus, if there are any.
            fixedFocus = this.runAction('fixfocus', eventFocus);
            if (fixedFocus === null) {
                fixedFocus = eventFocus;
            }

            // Does that actually result in a change?
            if (fixedFocus !== eventFocus) {
                // Yes.  Run a refocusing cycle, and DO NOT fire callbacks yet.
                this._refocusing = true;
                // (NOTE: Do NOT call _unforceFocus for the initial target.)
                this.forceFocus(fixedFocus);
                // No further processing should occur.
            } else {
                // This is our final focus target.
                this._refocusing = false;
                // Was there a net change?
                if (eventFocus === this._priorFocus) {
                    // No-- no net change.  Suppress the redundant events and do NOT
                    // run callbacks.  Do NOT update prior latches.

                    if($(".superfocus").length === 0){
                        $(this._priorFocus).addClass("superfocus");
                    }
                    if($(".subfocus").length === 0){
                        $(this._priorFocus).addClass("subfocus");
                    }
                } else {
                    // Yes-- fixed focus has moved.
                    // Remove subfocus CSS class from prior subfocus element.
                    // As a safety, we just globally remove the class.
                    $('.subfocus').removeClass('subfocus');
                    if (this._priorSubfocus){
                        this.runAction('subfocusout', this._priorSubfocus);
                    }
                    if (this._priorFocus){
                        this.runAction('focusout', this._priorFocus);
                        // If the control was temporarily made focusable, undo that.
                        this._unforceFocus(this._priorFocus);
                    }
                    // Latch the new focus.
                    this._currentFocus = eventFocus;
                    // Figure out the new superfocus, partly so that we can tell if the
                    // superfocus has changed.
                    this._currentSuperfocus = this.runAction('fixsuperfocus', this._currentFocus);
                    if (this._currentSuperfocus === null) {
                        // Superfocus could not be determined; fall back to BODY.
                        this._currentSuperfocus = $('body')[0];
                    }
                    logger.debug("stdnav._onFocusIn: fixsuperfocus(" + this._currentFocus.nodeName + "#" + this._currentFocus.id + ") returned " + this._currentSuperfocus.nodeName + "#" + this._currentSuperfocus.id);
                    if (this._currentSuperfocus !== this._priorSuperfocus) {
                        // Superfocus has changed; move the CSS class and run the
                        // callbacks.
                        var $superfocus = $('.superfocus');
                        if ($superfocus.length > 0) {
                            logger.debug("Removing .superfocus from " + $superfocus[0].nodeName + '#' + $superfocus[0].id);
                        } else {
                            logger.warn("No current .superfocus to remove");
                        }
                        $('.superfocus').removeClass('superfocus');
                        if (this._priorSuperfocus){
                            this.runAction('superfocusout', this._priorSuperfocus);
                            this.resumeFocusability(this._priorSuperfocus);
                        }
                        // Refresh the automatic ARIA markup for the region.
                        logger.debug("Running ARIA refresh for " + this._currentSuperfocus.nodeName + '#' + this._currentSuperfocus.id);
                        this.runAction('ariarefresh', this._currentSuperfocus);
                        logger.debug("Adding .superfocus to " + this._currentSuperfocus.nodeName + '#' + this._currentSuperfocus.id);
                        $(this._currentSuperfocus).addClass('superfocus');
                        // This ensures SHIFT-TAB will properly move to the prior region,
                        // rather than moving from an inner focus to the container (which
                        // is not useful and breaks backwards navigation).  However, do
                        // NOT do this if the superfocus is the same element as the focus,
                        // since it is not needed.
                        if (!$(this._currentSuperfocus).is(this._currentFocus)) {
                            this.suspendFocusability(this._currentSuperfocus);
                        }
                        this.runAction('superfocusin', this._currentSuperfocus);
                    }
                    // Run the focusin handler on the new focus.  This callback returns
                    // the new subfocus.  If it returns null, use the same value as the
                    // focus itself.
                    this._currentSubfocus = this.runAction('focusin', this._currentFocus);
                    if (this._currentSubfocus === null) {
                        this._currentSubfocus = this._currentFocus;
                    }
                    // Finally, set the subfocus CSS class on the new subfocus, and run
                    // the subfocusin callback for the new subfocus.
                    $(this._currentSubfocus).addClass('subfocus');
                    this.runAction('subfocusin', this._currentSubfocus);

                    // Ensure focus can be moved to other areas in the DOM.
                    // This is a safety, and helps deal with ALT-TAB, alerts,
                    // popups, debuggers, etc.  It does NOT restore the
                    // tabindex of the current focus container.
                    this.ensureFocusabilityBeyond(this._currentFocus);

                }
            }
            this.updateDebugInfo();
        },

        // Callback run when an element in the DOM has just acquired subfocus.
        // Note that this is AFTER the change, which is different than the way
        // 'onfocusin' works-- this is DELIBERATE, to ensure either callback
        // can preempt the other if necessary without recursing.
        //
        // Thie callback only fires if the subfocus change happens through
        // stdnav.setSubfocus!  Do not just set the CSS class!
        _prepSubfocusIn: function (element) {
            // If you move subfocus outside of a parent that has browser focus,
            // browser focus will be set to the subfocus.  This means that if
            // you, for example, give tabindexes to grid cells, moving left will
            // move both focus and subfocus-- give tabindexes to the top level
            // grid element ONLY, if you want to avoid this.
            if ($(element).parents(':focus').length === 0) {
                // No parent has focus.  Set browser focus to the new subfocus element.
                $(element).focus();
            }
        },


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////__F_O_C_U_S__O_U_T__//////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Reset any tabindex changes that were made to force the element to be
        // temporarily focusable for navigation and screen reader purposes.  Note
        // that this function does not actually change browser focus, as it is
        // intended to be called as a reaction to "focusout".
        _unforceFocus: function (element) {
            var $el = $(element);
            var priorTabindex = $el.data('prior-tabindex');
            if (this.nullOrUndefined(priorTabindex)) {
                // forceFocus does not seem to have been called, was not needed, or
                // had no effect; do nothing.
            } else {
                if (priorTabindex === 'none') {
                    // No tabindex was originally present, so remove the temporary one.
                    $el.removeAttr('tabindex');
                } else {
                    // The original tabindex must have explicitly been given a negative
                    // number to suppress normal tab navigation; restore that value.
                    $el.attr('tabindex', priorTabindex);
                }
                $el.removeData('prior-tabindex');
            }
        },

        // Callback run when an element in the DOM is about to lose focus.
        _onFocusOut: function (ev) {
            this.updateDebugInfo();
            if (this._refocusing === true) {
                // Intermediate target-- do not update latches.
            } else {
                if ($('.superfocus').length > 0) {
                    this._priorSuperfocus = $('.superfocus')[0];
                } else {
                    logger.warn("no prior superfocus");
                    this._priorSuperfocus = $('body')[0];
                }
                this._priorFocus = ev.target;
                if ($('.subfocus').length > 0) {
                    this._priorSubfocus = $('.subfocus')[0];
                } else {
                    logger.warn("no prior subfocus");
                    this._priorSubfocus = ev.target;
                }
            }

            //TODO: get rid of dependency on JRS mark-up (move to core.layout.js)
            var MAIN_NAVIGATION_ENTRY_POINT = "#globalSearch input#searchInput";
            var MAIN_NAVIGATION_EXIT_POINT = "#about a.superfocus.subfocus";

            // Remove focus classes when leaving the global navigation entry-point and exit-point.
            if ($(ev.target).is(MAIN_NAVIGATION_ENTRY_POINT) || $(ev.target).is(MAIN_NAVIGATION_EXIT_POINT)) {
                $('.subfocus').removeClass('subfocus');
                $('.superfocus').removeClass('superfocus');
            }
            this.updateDebugInfo();
        }
    }
});