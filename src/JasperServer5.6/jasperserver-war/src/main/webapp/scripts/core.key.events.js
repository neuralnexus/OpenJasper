/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: core.key.events.js 47331 2014-07-18 09:13:06Z kklein $
 */

/* 
 * event handler for generic key events  
 */

var keyManager = {
    ignoreKeyDown : [
        Keys.DOM_VK_SHIFT,
        Keys.DOM_VK_CONTROL,
        Keys.DOM_VK_ALT
    ],

    noBubbleOnKeyUp : [
        Keys.DOM_VK_F10
    ]
};

document.observe('keyup', function(event){
    if (keyManager.noBubbleOnKeyUp.include(event.keyCode)) {
        Event.stop(event);
    }
});

document.observe('keydown', function(event){


    if (keyManager.ignoreKeyDown.include(event.keyCode)) {
        return;
    }

    //debug only
    //window.console && console.log('document.activeElement',document.activeElement);
    //window.console && console.log('keydown',event.element(),event.keyCode);

    var focused = event.element();

    if(event.keyCode == Event.KEY_RETURN) {
        focused.fire('key:enter', {targetEvent: event, node: focused});
        return;
    }
    if(event.shiftKey && (event.keyCode == Keys.DOM_VK_F10)) {
        //prevent from showing native menu, specially in IE9
    	Event.stop(event);
        focused.fire('key:contextMenu', {targetEvent: event, node: focused});
        return;
    }
    if(event.keyCode == Event.KEY_LEFT) {
        focused.fire('key:left', {targetEvent: event, node: focused});
        return;
    }
    if(event.keyCode == Event.KEY_RIGHT) {
        focused.fire('key:right', {targetEvent: event, node: focused});
        return;
    }
    if(event.keyCode == Event.KEY_UP) {
        focused.fire('key:up', {targetEvent: event, node: focused});
        return;
    }
    if(event.keyCode == Event.KEY_DOWN) {
        focused.fire('key:down', {targetEvent: event, node: focused});
        return;
    }
    //ctrl + y
    if(isMetaHeld(event) && ((event.keyCode == 89))) {
        focused.fire('key:redo', {targetEvent: event, node: focused});
        return;
    }
    //ctrl + z
    if(isMetaHeld(event) && ((event.keyCode == 90))) {
        focused.fire('key:undo', {targetEvent: event, node: focused});
        return;
    }
    //escape
    if(event.keyCode == Event.KEY_ESC) {
        focused.fire('key:escape', {targetEvent: event, node: focused});
        return;
    }

    //escape
    if(event.keyCode == Event.KEY_TAB) {
        focused.fire('key:tab', {targetEvent: event, node: focused});
        return;
    }

    //delete
    if(event.keyCode == Event.KEY_DELETE) {
        focused.fire('key:delete', {targetEvent: event, node: focused});
        return;
    }


    /**
     * Adhoc and Dashboard specific keys events
     */

    //command / ctrl + a
    if((event.keyCode == 65) && isMetaHeld(event)) {
        focused.fire('key:selectAll', {targetEvent: event, node: focused});
        return;
    }

    //fire for save
    //command / ctrl + s (save)
    if((event.keyCode == 83) && isMetaHeld(event) && !isShiftHeld(event)) {
        focused.fire('key:save', {targetEvent: event, node: focused});
        return;
    }


    //fire for zoom in
    //command / ctrl + shift + l (zoom in)
    if((event.keyCode == 76) && isMetaHeld(event) && isShiftHeld(event)) {
        focused.fire('key:chartZoomIn', {targetEvent: event, node: focused});
        return;
    }


    //fire for zoom  out
    //command / ctrl + shift + o (zoom out)
    if((event.keyCode == 79) && isMetaHeld(event) && isShiftHeld(event)) {
        focused.fire('key:chartZoomOut', {targetEvent: event, node: focused});
        return;
    }

});

////////////////////////////////////////////////
// global custom event handlers
////////////////////////////////////////////////

document.observe('key:esc', function(event){
    if(actionModel.isMenuShowing()){
        actionModel.hideActionModelMenu();
    }
});

document.observe('key:enter', function(event){
    var elem = event.element(), matched;

    //if menu showing - simulate mouseup
    var menuElement = $(layoutModule.MENU_ID);
    if (layoutModule.isVisible(menuElement)) {
        Event.stop(event);
        var selected = menuElement.select("." + layoutModule.HOVERED_CLASS)[0];
        if (selected) {
            var thisItem = selected.up(layoutModule.MENU_LIST_PATTERN);
            setTimeout(thisItem.onmouseup.curry(event.memo.targetEvent),0);
        }
    }

    matched = matchMeOrUp(elem, layoutModule.NAVIGATION_PATTERN);
    if (matched) {
        if (matched.identify() == layoutModule.MAIN_NAVIGATION_HOME_ITEM_ID) {
            Event.stop(event);
            primaryNavModule.navigationOption("home");
        } else if (matched.identify() == layoutModule.MAIN_NAVIGATION_LIBRARY_ITEM_ID) {
            Event.stop(event);
            primaryNavModule.navigationOption("library");
        }
    }
});

document.observe('key:down', function(event){
    var elem = event.element(), matched;

    //if menu showing select next
    if (layoutModule.isVisible(layoutModule.MENU_ID)) {
        Event.stop(event);
        var selected = $(layoutModule.MENU_ID).select("." + layoutModule.HOVERED_CLASS)[0];
        if (selected) {
            var thisItem = selected.up(layoutModule.MENU_LIST_PATTERN);
            //skip separators etc.
            var next = getNextNonMatchingSibling(thisItem, layoutModule.SEPARATOR_PATTERN);
            if (next) {
                buttonManager.out(selected);
                buttonManager.over(next.down(layoutModule.BUTTON_PATTERN));
                next.focus();
            }
            return;
        }
    }

    //navigation button focused? move to first menu item
    if (matched = matchAny(event.element(), [layoutModule.NAVIGATION_MUTTON_PATTERN ], true)) {
        //if mousedown on nav button navigate to top of menu (if any) 
        //alert('dufus');
        buttonManager.out(matched.down(layoutModule.BUTTON_PATTERN));
        buttonManager.over(actionModel.getFirstMenuButton());
        actionModel.focusMenu();
    }
});

document.observe('key:up', function(event){
    var elem = event.element(), matched;

    //if menu showing select previous
    if (layoutModule.isVisible(layoutModule.MENU_ID)) {
        Event.stop(event);
        var selected = $(layoutModule.MENU_ID).select("." + layoutModule.HOVERED_CLASS)[0];
        if (selected) {
            var thisItem = selected.up(layoutModule.MENU_LIST_PATTERN);
            //skip separators etc.
            var next = getPreviousNonMatchingSibling(thisItem, layoutModule.SEPARATOR_PATTERN) || actionModel.getMenuParent();
            if (next) {
                buttonManager.out(selected);
                buttonManager.over(next.down(layoutModule.BUTTON_PATTERN));
                next.focus();
            }
        }
    }
});	

document.observe('key:left', function(event){
    var elem = event.element(), matched;

    var selected = $(layoutModule.MAIN_NAVIGATION_ID).select("." + layoutModule.HOVERED_CLASS)[0];
    if (selected) {
        Event.stop(event);
        var thisItem = selected.up(layoutModule.NAVIGATION_PATTERN);
        var next = getPreviousMatchingSibling(thisItem, layoutModule.NAVIGATION_PATTERN);
        if (next) {
            actionModel.hideMenu();

            buttonManager.out(selected);
            buttonManager.over(next.down(layoutModule.BUTTON_PATTERN));
            next.focus();

            matched = matchMeOrUp(next, layoutModule.NAVIGATION_MUTTON_PATTERN);
            if (matched) {
                primaryNavModule.showNavButtonMenu(event, matched);
            }
        }
    }
});

document.observe('key:right', function(event){
    var elem = event.element(), matched;

    var selected = $(layoutModule.MAIN_NAVIGATION_ID).select("." + layoutModule.HOVERED_CLASS)[0];
    if (selected) {
        Event.stop(event);

        var thisItem = selected.up(layoutModule.NAVIGATION_PATTERN);
        var next = getNextMatchingSibling(thisItem, layoutModule.NAVIGATION_PATTERN);
        if (next) {
            actionModel.hideMenu();

            buttonManager.out(selected);
            buttonManager.over(next.down(layoutModule.BUTTON_PATTERN));
            next.focus();

            matched = matchMeOrUp(next, layoutModule.NAVIGATION_MUTTON_PATTERN);
            if (matched) {
                primaryNavModule.showNavButtonMenu(event, matched);
            }
        }
    }
});
