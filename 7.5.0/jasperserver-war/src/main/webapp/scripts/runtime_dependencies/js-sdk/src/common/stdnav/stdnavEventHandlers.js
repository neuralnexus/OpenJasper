define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var log = require('../logging/logger');

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
 * Standard events handlers
 *
 */
var logger = log.register('stdnav');
module.exports = {
  // Callback run when an element in the DOM is clicked.
  _onClick: function _onClick(ev) {
    var element = $(ev.target); // Sometimes this happens...

    if (element.length !== 1) {
      return;
    } // Get the behavior for the clicked element.


    var behavior = this._buildBehavior(element);

    if (behavior['click'] != null) {
      var bubble = this.runAction('click', ev.target);

      if (bubble === false) {
        ev.stopPropagation();
        ev.preventDefault();
      }
    }
  },
  // Callback run when an element in the DOM is clicked.
  _onMouseDown: function _onMouseDown(ev) {
    var element = $(ev.target); // Sometimes this happens...

    if (element.length !== 1) {
      return;
    } // Get the behavior for the clicked element.


    var behavior = this._buildBehavior(element);

    if (behavior['mousedown'] != null) {
      var bubble = this.runAction('mousedown', ev.target);

      if (bubble === false) {
        ev.stopPropagation();
        ev.preventDefault();
      }
    }
  },
  // Callback run when an element in the DOM is clicked.
  _onMouseUp: function _onMouseUp(ev) {
    var element = $(ev.target); // Sometimes this happens...

    if (element.length !== 1) {
      return;
    } // Get the behavior for the clicked element.


    var behavior = this._buildBehavior(element);

    if (behavior['mouseup'] != null) {
      var bubble = this.runAction('mouseup', ev.target);

      if (bubble === false) {
        ev.stopPropagation();
        ev.preventDefault();
      }
    }
  },
  // Callback run when cursor is over on element with aria-label attribute.
  _onLabeledTagOver: function _onLabeledTagOver(ev) {
    var $target = $(ev.currentTarget);

    if ($target.attr("aria-label") && $target.data("title")) {
      $target.attr("title", $target.attr("aria-label"));
    }
  },
  _onKeydown: function _onKeydown(ev) {
    // Determine precisely what had browser focus when keyboard input was received.
    var elFocus = ev.target; // If nothing has subfocus, we cannot presume stdnav is safe
    // to use; bail out.

    if ($('.subfocus').length === 0) {
      return;
    }

    var elSubfocus = $('.subfocus')[0]; // While we allow form elements to be subfocused for visual consistency,
    // we never handle keyboard input from them, with a single exception:
    // the ESCAPE key must still allow focus to be returned to the menu.

    var nodeName = $(elSubfocus).prop('nodeName'); // NOTE: 'BUTTON' was in the array below, but we desire ENTER to work
    // on this as well.

    if ($(elSubfocus).is(':input') || $.inArray(nodeName, ['INPUT', 'SELECT', 'OBJECT', 'TEXTAREA']) > -1) {
      // This is special rule which allow continue execute code in the stdnavToolBarPlugin if focus element is 'input'
      if (nodeName === 'INPUT') {
        if ($(elSubfocus).closest(".j-toolbar").length === 0) {
          return;
        }
      } else if (nodeName !== 'BUTTON' && ev.keyCode !== 27) {
        return;
      }
    }

    var action;

    switch (ev.keyCode) {
      case 13:
        // Enter (CR)
        action = 'enter';
        break;

      case 27:
        // Escape.
        action = 'exit';
        break;

      case 32:
        // Space.
        action = 'toggle';
        break;

      case 33:
        // Page up.
        action = 'pageup';
        break;

      case 34:
        // Page down.
        action = 'pagedown';
        break;

      case 35:
        // End.
        action = 'end';
        break;

      case 36:
        // Home.
        action = 'home';
        break;

      /* Arrow key behavior depends on menu orientation. */

      case 37:
        // Left arrow
        action = 'left';
        break;

      case 38:
        // Up arrow
        action = 'up';
        break;

      case 39:
        // Right arrow
        action = 'right';
        break;

      case 40:
        // Down arrow
        action = 'down';
        break;

      case 91:
        // '[':
        // FIXME: Ignore unless CTRL or ALT is pressed.
        action = 'structleft';
        break;

      case 93:
        // ']':
        // FIXME: Ignore unless CTRL or ALT is pressed
        action = 'structright';
        break;

      default: // Allow default behavior for other keys.

    }

    if (!this.nullOrUndefined(action)) {
      var newFocus = this.runAction(action, elSubfocus);

      if (!this.nullOrUndefined(newFocus)) {
        // Returning null indicates the event should bubble up.
        // Otherwise, it indicates a new focus target.
        ev.stopPropagation();
        ev.preventDefault(); // If focus should change as a result of navigation, force the
        // element to be focusable, and focus it.
        // Note that redundant focus sets will be suppressed.

        this.forceFocus(newFocus);
      } else {
        return;
      }
    } else {
      return;
    }
  },
  // ===DEFAULT BEHAVIOR HANDLERS========================================
  // The default click handler tries to find an appropriate element to
  // focus based on what you clicked, but also allows the click event to
  // bubble to other handlers.  This behavior may be redundant with basic
  // mousedown behavior, so consider whether you really want to use both.
  basicClick: function basicClick(element, args) {
    var fixedFocus = this.runAction('fixfocus', element);

    if (this.nullOrUndefined(fixedFocus)) {
      logger.debug("stdnav.basicClick: " + element.nodeName + "#" + element.id + " has no navigable ancestor, ignoring");
    } else {
      logger.debug("stdnav.basicClick(" + element.nodeName + "#" + element.id + ") refocusing to " + fixedFocus.nodeName + '#' + fixedFocus.id);

      if (!this.nullOrUndefined(fixedFocus)) {
        this.forceFocus(fixedFocus);
      }
    } // Let the event bubble up.


    return true;
  },
  // The default fixfocus callback returns the element if it is navigable,
  // or if it is a form field/input.  Otherwise, the closest navigable
  // ancestor is returned.  If no navigable ancestor exists, the function
  // returns null, and focus should not change; this is useful in click
  // handlers.
  basicFixFocus: function basicFixFocus(element, args) {
    if (this.isNavigable(element) || $(element).is(':input')) {
      return element;
    }

    var ancestor = this.closestNavigableAncestor(element);

    if (!this.nullOrUndefined(ancestor)) {
      return ancestor;
    }

    return null;
  },
  // FIXME-- UPDATE DOCS FOR LOGIC CHANGES
  // The default fixsuperfocus callback returns the closest navigable
  // ancestor for form elements, buttons, and links.  For other elements,
  // it returns the element itself, if it is navigable, otherwise the
  // closest navigable ancestor.  If nothing navigable can be identified,
  // superfocus is returned as BODY.
  //
  // If you place a tabindex="0" on an element that contains a form, this
  // will give you the behavior you expect: you can tab between the form
  // elements, but the overall form will be indicated as the active UI
  // region, and a fixsuperfocus call on the container itself will return
  // the container.
  basicFixSuperfocus: function basicFixSuperfocus(element, args) {
    var newSuperfocus = null;

    if ($(element).is(':input,fieldset')) {
      // Form element, input, button, or link.
      var $forms = $(element).closest('form');
      $(element).closest('form');

      if ($forms.length > 0) {
        newSuperfocus = $forms[0];
      }
    } else {
      // Non-[input,form,link] element.
      if (this.isNavigable(element)) {
        newSuperfocus = element;
      } else {
        newSuperfocus = this.closestNavigableAncestor(element);
      }
    }

    if (newSuperfocus === null || typeof newSuperfocus === 'undefined') {
      newSuperfocus = element;
    }

    return newSuperfocus;
  },
  // This handy callback lets you set superfocus to whatever is appropriate
  // for your parent.  Use it when working with navtypes that have to
  // specify "inherit=false" but shouldn't be superfocii.
  parentFixSuperfocus: function parentFixSuperfocus(element, args) {
    return this.basicFixSuperfocus(element.parentNode, args);
  },
  // The default fixsubfocus callback simply returns the element passed.
  // This has the effect of setting subfocus to focus, which is almost
  // always what you want.
  basicFixSubfocus: function basicFixSubfocus(element, args) {
    return element;
  },
  // The default focusin handler returns the same element as the new
  // subfocus.
  basicFocusIn: function basicFocusIn(element, args) {
    logger.debug("stdnav.basicFocusIn(" + element.nodeName + "#" + element.id + ")");
    return element;
  },
  // The default focusout handler disallows demotion to ghostfocus.  This
  // is to prevent visual clutter with forms and other areas where there are
  // groups of tabbed elements with only one subfocusable region.
  basicFocusOut: function basicFocusOut(element, args) {
    logger.debug("stdnav.basicFocusOut(" + element.nodeName + "#" + element.id + ")");
    return null;
  },
  // The default mousedown handler tries to find an appropriate element to
  // focus based on what you clicked, but also allows the mousedown event to
  // bubble to other handlers.
  //
  // Optional Arguments:
  //
  // TODO:
  // 'onlyfor': An optional jQuery selector whitelist.  Only elements matching
  //            this selector will have this behavior.  The event will bubble
  //            up whether the element matches the selector or not, however.
  basicMouseDown: function basicMouseDown(element, args) {
    var fixedFocus = this.runAction('fixfocus', element);

    if (this.nullOrUndefined(fixedFocus)) {
      logger.debug("stdnav.basicClick: " + element.nodeName + "#" + element.id + " has no navigable ancestor, ignoring");
    } else {
      logger.debug("stdnav.basicClick(" + element.nodeName + "#" + element.id + ") refocusing to " + fixedFocus.nodeName + '#' + fixedFocus.id);

      if (!this.nullOrUndefined(fixedFocus)) {
        this.forceFocus(fixedFocus);
      }
    } // Let the event bubble up.


    return true;
  },
  // The default mouseup handler takes no action, but allows the
  // mouseup event to bubble to other handlers.
  basicMouseUp: function basicMouseUp(element, args) {
    // Let the event bubble up.
    return true;
  },
  // The default superfocusin handler indicates that focus should be given
  // to the same element as superfocus, unless an optional jQuery selector
  // matches one or more descendant elements within a maximum depth.  If
  // matches are found, and one also has the special CSS class "ghostfocus",
  // then it will be returned.  Otherwise, the first match will be returned.
  // Note that if the region contains any input controls, these will also
  // be focused.
  basicSuperfocusIn: function basicSuperfocusIn(element, args) {
    logger.debug("stdnav.basicSuperfocusIn(" + element.nodeName + "#" + element.id + ")");
    var newFocus = null;

    if ('focusSelector' in args) {
      var focusSelector = ':input,fieldset,' + args['focusSelector'] + '[js-navigable!="false"]';
      var barrier = null;

      if ('barrier' in args) {
        barrier = args['barrier'];
      }

      var maxdepth = this._maxNavDepth;

      if ('maxdepth' in args) {
        maxdepth = args['maxdepth'];
      }

      if ('ghostfocus' in args && args['ghostfocus'] === true) {
        newFocus = this.closestDescendant(element, focusSelector + ' .ghostfocus', barrier, maxdepth); // Remove ghostfocus, since it is being promoted to focus.

        $(newFocus).removeClass('ghostfocus');
      }

      if (newFocus == null) {
        // No ghost available, or ghostfocus not allowed.
        newFocus = this.closestDescendant(element, focusSelector, barrier, maxdepth);
      }
    }

    if (newFocus == null) {
      // If other approaches have failed, return the same element that has
      // superfocus.  This is appropriate for form fields, etc.
      newFocus = element;
    }

    return newFocus;
  },
  // The default superfocusout handler adds ghostfocus to the prior focus,
  // if the args hash includes 'ghostfocus':true.
  basicSuperfocusOut: function basicSuperfocusOut(element, args) {
    logger.debug("stdnav.basicSuperfocusOut(" + element.nodeName + "#" + element.id + ")");

    if ('ghostfocus' in args) {
      if (args['ghostfocus'] === true) {
        if (this._priorFocus) {
          $(this._priorFocus).addClass('ghostfocus');
        }
      }
    }

    return null;
  },
  // The default subfocusin handler takes no action.
  basicSubfocusIn: function basicSubfocusIn(element, args) {
    logger.debug("stdnav.basicSubfocusIn(" + element.nodeName + "#" + element.id + ")");
    return null;
  },
  // The default subfocusout handler takes no action.
  basicSubfocusOut: function basicSubfocusOut(element, args) {
    logger.debug("stdnav.basicSubfocusOut(" + element.nodeName + "#" + element.id + ")");
    return null;
  },
  // "Enter" is a catch-all "activate" function, normally triggered by
  // pressing the ENTER key.  It is distinct from acquiring focus or
  // subfocus, because it implies a deliberate user action _within_ the
  // element when it already has focus.
  //
  // The default "enter" handler finds the closest navigable descendant,
  // if one exists, and fires its "enter" handler.
  //
  // EXAMPLE:
  // If the Anchor and Button plugins are loaded, their "enter"
  // handlers will fire when they are entered; those handlers simulate a
  // "click" event but do not move focus or subfocus.  The net effect of
  // this is that pressing ENTER in a table cell that contains a button
  // will press the button, but pressing RIGHT ARROW immediately after
  // this will still move one cell to the right, because subfocus remains
  // on the table cell.
  basicEnter: function basicEnter(element, args) {
    logger.debug("stdnav.basicEnter(" + element.nodeName + "#" + element.id + ")");
    var target = this.closestNavigableDescendant(element);

    if (target !== undefined) {
      this.runAction('enter', target);
    }

    return element;
  },
  // "Toggle" is kind of "switch state" function, normally triggered by
  // pressing the SPACE key on toggle-buttons, check-boxes, etc. If button
  // has a single state (no toggling) - then SPACE key behaves like ENTER and
  // fires the "activate" function.
  basicToggle: function basicToggle(element, args) {
    logger.debug("stdnav.basicToggle(" + element.nodeName + "#" + element.id + ")");
    var target = this.closestNavigableDescendant(element);

    if (target !== undefined) {
      this.runAction('toggle', target);
    }

    return null;
  },
  // "exit" is the operation when the user presses Escape.  (There is no
  // mouse equivalent for this, so do not use this as a sort of
  // "onDefocus" event.)  The default behavior for "exit" is to look for
  // the closest navigable ancestor of the superfocus, and return that as
  // the new focus.  If no such ancestor exists, if BODY is returned, or
  // if NULL is somehow returned, focus instead moves to the main menu.
  // In any case, the "rejoined" action will fire for the new focus.
  basicExit: function basicExit(element, args) {
    logger.debug("stdnav.basicExit(" + element.nodeName + "#" + element.id + ")");
    var target = this.closestNavigableAncestor($('.superfocus'));

    if (!target) {
      // Something's gotten confused.  Refocus to the global entry-point
      target = $('#searchInput'); //TODO: get rid of dependency on JRS mark-up
    }

    if (target !== undefined) {
      this.runAction('rejoined', target[0]);
    }

    return target[0];
  },
  // "rejoined" behavior is run when a child element is being exited due to
  // a user action, normally the ESCAPE key.  When ESCAPE is pressed,
  // "exit" behavior will run in the element that has focus, and "rejoin"
  // behavior will run in the nearest navigable parent.  This default
  // handler simply forces focus to the element, which causes superfocus to
  // be recomputed, etc.
  basicRejoined: function basicRejoined(element, args) {
    logger.debug("stdnav.basicRejoined(" + element.nodeName + "#" + element.id + ")"); //var fixedFocus = this.runAction('fixfocus', element);
    //$(element).focus();
  }
};

});