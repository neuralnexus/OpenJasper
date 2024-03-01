/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
 * @author: Yuriy Plakosh
 * @version: $Id$
 */
/**
 * Dialogs Module.
 */

import _ from 'underscore';
import jQuery from 'jquery';
import stdnav from 'js-sdk/src/common/stdnav/stdnav';
import dialogTemplate from 'js-sdk/src/common/templates/dialogSystemConfirmTemplate.htm';
import {appear} from '../util/utils.animation';
import {reParent, cascadeElement, centerElement, isIPad, matchMeOrUp, matchAny, pageDimmer} from "../util/utils.common";
import bundle1 from '../i18n/jasperserver_messages.properties';
import bundle2 from '../i18n/jsexceptions_messages.properties';
import TouchController from '../util/touch.controller';
import layoutModule from '../core/core.layout';
import xssUtil from 'js-sdk/src/common/util/xssUtil';
import Builder from 'scriptaculous/src/builder';

let $elem;
var dialogs = {};    ///////////////////////////////////////////
// System confirm object and methods
///////////////////////////////////////////
/**
 * System confirm is used to show system confirm about the last action.
 * It fades up rapidly after the action completes, and fades away on the next mouseDown anywhere on the page.
 */
///////////////////////////////////////////
// System confirm object and methods
///////////////////////////////////////////
/**
 * System confirm is used to show system confirm about the last action.
 * It fades up rapidly after the action completes, and fades away on the next mouseDown anywhere on the page.
 */
dialogs.systemConfirm = {
    container: null,
    message: null,
    show: function (message, duration, isWarning) {
        if (window.isEmbeddedDesigner) {
            window.parent.postMessage({
                event_id: "adhocDesigner:notification",
                message: message,
                duration: duration
            }, window.location.origin);
        } else {
            this.container = this.container || jQuery('#systemMessageConsole').on('mouseup touchend', function () {
                dialogs.systemConfirm.container.slideUp();
            });
            this.message = this.message || document.getElementById('systemMessage');
            if (!this.message) {
                //unable to find DOM elem to output the system message
                /*eslint-disable-next-line no-console*/
                console.warn(message);
                return;
            }
            if (!this.closeText) {
                this.closeText = jQuery(this.message).html().toLowerCase();
            }
            jQuery(this.message).html(_.template(dialogTemplate, {
                messages: message,
                isWarning: isWarning,
                closeText: this.closeText
            }));
            dialogs.systemConfirm.container.slideDown();
            setTimeout(function() {
                dialogs.systemConfirm.hide();
            }, duration ? duration : 2000);
        }
    },
    showWarning: function (message, duration) {
        this.show(message, duration, true);
    },
    hide: function () {
        if (dialogs.systemConfirm.container) {
            dialogs.systemConfirm.container.slideUp();
        }
    }
};

jQuery(document).on('systemDialogWarn', function(event) {
    var message = event.detail.message,
        duration = event.detail.duration;

    dialogs.systemConfirm.showWarning(message, duration);
});    //////////////////////////////////////////////
// Ajax Error Popup Dialog object and methods
//////////////////////////////////////////////
/**
 * Ajax Error dialog is used then any ajax call returns and server side error which was not
 * catched by server. It show popup dialog with stackrtace and close button.
 */
//////////////////////////////////////////////
// Ajax Error Popup Dialog object and methods
//////////////////////////////////////////////
/**
 * Ajax Error dialog is used then any ajax call returns and server side error which was not
 * catched by server. It show popup dialog with stackrtace and close button.
 */
dialogs.errorPopup = {
    _dom: null,
    _content: null,
    /**
     * The identifier of the DOM element.
     */
    _DOM_ID: 'standardAlert',
    /**
     * The identifier of the element there error content should be placed.
     */
    _CONTENT_ID: 'errorPopupContents',
    /**
     * Pattern of element in response where error is placed
     */
    _PAGE_CONTENT_PATTERN: '#errorPageContent',
    _DIALOG_WIDTH: '546px',
    _DIALOG_HEIGHT: '350px',
    clickHandler: null,

    onClose: null,

    getZIndex: function() {
        var zIndex = 0;

        if (this._dom) {
            zIndex = parseInt(this._dom.getStyle('zIndex'), 10);
        }

        return zIndex;
    },

    /**
     * Shows popup dialog.
     *
     * @param errorContent error content to be showed
     */
    show: function (errorContent, isStackTraceContent, options) {
        options || (options = {});
        var fromSource = Builder.node('DIV', { style: 'display:none' });

        jQuery(fromSource).html(errorContent);

        document.body.insertBefore(fromSource, document.body.firstChild);
        var content = jQuery(this._PAGE_CONTENT_PATTERN)[0];
        var contentText = content ? jQuery(content).html() : errorContent;
        var isStackTrace = content && jQuery(content).html();
        fromSource.remove();
        if (contentText) {
            if (!this._dom) {
                this._dom = jQuery('#' + this._DOM_ID)[0];
                this._content = jQuery('#' + this._CONTENT_ID)[0];
                this.clickHandler = this._clickHandler.bindAsEventListener(this);
            }
            if (this._dom) {
                var finalContent = contentText;    //If error is a plain text - wrap it into <p> element
                //If error is a plain text - wrap it into <p> element
                if (!isStackTrace) {
                    finalContent = Builder.node('P', { 'class': 'message' });
                    finalContent.update(contentText);
                }
                this._content.update(finalContent);
                this._dom.observe('click', this.clickHandler);
                this._dom.setStyle({
                    height: options.height || this._DIALOG_HEIGHT,
                    width: options.width || this._DIALOG_WIDTH
                });
                isStackTraceContent && this._dom.addClass(layoutModule.STACKTRACE_CLASS);
                dialogs.popup.show(this._dom, isStackTraceContent);
                var st = document.getElementById('completeStackTrace');
                if (st) {
                    isIPad() && new TouchController(st, st.parentNode, { noInit3d: true });
                }
            }
        }
    },
    /**
     * Hides popup dialog.
     */
    _hide: function () {
        if (this._dom) {
            this._dom.stopObserving('click', this.clickHandler);

            if (this.onClose) {
                this.onClose();
                this.onClose = null;
            }

            dialogs.popup.hide(this._dom);
        }
    },
    /*
     * Mouse click handler for close button
     */
    _clickHandler: function (event) {
        var element = event.target;
        if (matchAny(element, ['button'], true)) {
            this._hide();
        }
    }
};
dialogs.clusterErrorPopup = _.extend({}, dialogs.errorPopup, {
    show: function (errorContent) {
        var clusterErrorPopupMsg = bundle2['cluster.exception.session.attribute.missing.popup'];
        dialogs.errorPopup.show.call(this, clusterErrorPopupMsg);
        var buttonLabel = bundle1['button.home'];
        jQuery('#' + this._DOM_ID).find('button span.wrap').html(buttonLabel);
    },
    _hide: function (event) {
        dialogs.errorPopup._hide.apply(this, arguments);
        window.location = 'home.html';
    }
});    /**
 * generic 'popup' dialog controller
 * @param {Object} elem
 */
/**
 * generic 'popup' dialog controller
 * @param {Object} elem
 */
dialogs.popup = {
    OWNER_ATTR: 'data-owner',
    show: function (elem, showDimmer, options) {
        options = options || {};
        elem = jQuery(elem)[0];
        if (!elem) {
            return;
        }
        if ('message' in options) {
            // Update the displayed message in the dialog.
            jQuery(elem).find('.body').text(xssUtil.unescape(options.message));
        }    //dimmer
        //dimmer
        if (showDimmer) {
            pageDimmer.show();
            jQuery(elem).is(layoutModule.DIALOG_LOADING_PATTERN) && pageDimmer.setZindex(elem.getStyle('zIndex') - 1);
        }    //ensure body is parent
        //ensure body is parent
        reParent(elem, document.body);
        elem.setOpacity(0);
        jQuery(elem).removeClass(layoutModule.HIDDEN_CLASS);
        layoutModule.createSizer.call(layoutModule, elem);
        if (options.cascade) {
            //cascade
            cascadeElement(elem, {
                position: options.position,
                number: options.number,
                horzOffset: 40,
                vertOffset: 40
            });
        } else {
            //center
            centerElement(elem, {
                horz: true,
                vert: true
            });
        }    // raise if necessary
        // raise if necessary
        dialogs.popup._setHigherZIndex(elem);    // Drag'&'Drop depends depends from zIndex on dialog
        // Drag'&'Drop depends depends from zIndex on dialog
        layoutModule.createMover.call(layoutModule, elem, options);    // To move dialog on foreground we need initialize DnD after we have zIndex for dialog
        // To move dialog on foreground we need initialize DnD after we have zIndex for dialog
        isIPad() ? elem.setOpacity(1) && elem.show() : appear(elem, 0.4);    // Keep track of the element that had focus prior to the dialog being
        // shown, so that it can be restored.
        // Keep track of the element that had focus prior to the dialog being
        // shown, so that it can be restored.
        jQuery('.preDialogFocus').removeClass('preDialogFocus');
        jQuery(document.activeElement).addClass('preDialogFocus');    // Set focus on dialog if options.focus is not present in options or it's set to true.
        // If there is a primary button, ensure it receives focus.  Otherwise, ensure the
        // dialog itself receives focus.
        // Set focus on dialog if options.focus is not present in options or it's set to true.
        // If there is a primary button, ensure it receives focus.  Otherwise, ensure the
        // dialog itself receives focus.
        var focusTarget = elem;
        if (elem && jQuery('.primary', elem).length > 0) {
            focusTarget = jQuery('.primary', elem)[0];
        }
        if (!options || typeof options.focus === 'undefined' || options.focus) {
            // In any case, ensure the dialog itself can be focused.  This helps
            // screen readers to read the text in the dialog itself.
            elem.tabIndex = 0;
            focusTarget.tabIndex = 0;
            focusTarget.focus();
        }
        !showDimmer && elem.observe('click', dialogs.popup.zIndexHandler);    // Ensure the TAB key cannot move focus outside of the dialog.  While
        // the "shader" dialog will intercept mouse clicks and touch events, it
        // does not prevent keyboard events.
        // NOTE: This must be done AFTER moving focus into the dialog.
        // Ensure the TAB key cannot move focus outside of the dialog.  While
        // the "shader" dialog will intercept mouse clicks and touch events, it
        // does not prevent keyboard events.
        // NOTE: This must be done AFTER moving focus into the dialog.
        stdnav.beginModalFocus(elem);
    },
    hide: function (elem) {
        // Ensure the TAB key can move focus to all the places it was able to
        // before the dialog was shown.
        // NOTE: This must be done BEFORE moving focus out of the dialog.
        stdnav.endModalFocus(elem);    // Restore keyboard focus to the element that had it prior to the
        // dialog.
        // Restore keyboard focus to the element that had it prior to the
        // dialog.
        var jqPreDialogFocus = jQuery('.preDialogFocus');
        if (jqPreDialogFocus.length && jQuery(elem).is(":visible")){
            stdnav.forceFocus(jqPreDialogFocus.first());
            jQuery(jqPreDialogFocus).removeClass('preDialogFocus');
        }
        if (!elem) {
            return;
        }

        var $elem = jQuery(elem)[0];
        //hide dialog and dimmer
        if (jQuery($elem).hasClass(layoutModule.CANCELLABLE_CLASS)) {
            jQuery($elem).removeClass(layoutModule.CANCELLABLE_CLASS);
        }
        if (!jQuery($elem).hasClass(layoutModule.HIDDEN_CLASS)) {
            jQuery($elem).addClass(layoutModule.HIDDEN_CLASS);
            pageDimmer.hide();
            jQuery($elem).is(layoutModule.DIALOG_LOADING_PATTERN) && pageDimmer.setZindex(layoutModule.DIMMER_Z_INDEX);
        }

        _.defer(()=>{
            // JRS-20956
            // in IE click event handler for dialog executed twice for some reason.
            // So flow is the following:
            //
            // 1. User clicks on close button of the dialog
            // 2. stopObserving executed so event removed from the events registry
            // 3. unexpected click event handler executed second time and it fails since there is no even in the registry
            //
            // wrapping in _.defer changes this to click->click->stopObserving
            $elem.stopObserving('click', dialogs.popup.zIndexHandler)
        });
    },
    showShared: function (elem, showDimmer, options) {
        if (!elem) {
            return;
        }
        $elem = jQuery(elem)[0];
        options = options || {};
        if (!options.owner) {
            return;
        }
        jQuery($elem).attr(this.OWNER_ATTR, options.owner);
        if (jQuery($elem).hasClass(layoutModule.HIDDEN_CLASS)) {
            this.show($elem, showDimmer, options);
        }
    },
    hideShared: function (elem, ownerId) {
        if (!elem) {
            return;
        }
        $elem = jQuery(elem)[0];
        var ownerAttr = jQuery($elem).attr(this.OWNER_ATTR);
        if (ownerAttr == ownerId) {
            jQuery($elem).attr(this.OWNER_ATTR, false);
            this.hide($elem);
        }
    },
    /**
     * Handler for dialog z-index change on click.
     */
    zIndexHandler: function (event) {
        var element = Event.element(event);
        var dialog = matchMeOrUp(element, layoutModule.DIALOG_PATTERN);
        if (!dialog) {
            return;
        }
        dialogs.popup._setHigherZIndex(dialog);
    },
    _setHigherZIndex: function (currentDialog, parentDialog) {
        var otherDialogs, biggestZIndex, currentDialogZIndex, parentsZIndex = 0, maxZIndexAcrossOtherDialogs = 0;
        if (!currentDialog) {
            return;
        }
        currentDialogZIndex = currentDialog.getStyle('zIndex');
        if (parentDialog) {
            parentsZIndex = parentDialog.getStyle('zIndex');
        }
        otherDialogs = document.body.select(layoutModule.DIALOG_PATTERN);    // skip dialogs which are:
        // 1) not active
        // 2) loading dialog
        // 3) the current dialog
        // skip dialogs which are:
        // 1) not active
        // 2) loading dialog
        // 3) the current dialog
        otherDialogs = otherDialogs.filter(function (dialog) {
            if (!dialog.visible()) {
                return false;
            }
            if (jQuery(dialog).is(layoutModule.DIALOG_LOADING_PATTERN)) {
                return false;
            }
            if (dialog === currentDialog) {
                return false;
            }
            return true;
        });
        otherDialogs.each(function (dialog) {
            maxZIndexAcrossOtherDialogs = Math.max(maxZIndexAcrossOtherDialogs, dialog.getStyle('zIndex'));
        });
        biggestZIndex = Math.max(parentsZIndex, currentDialogZIndex, maxZIndexAcrossOtherDialogs, layoutModule.DIMMER_Z_INDEX);    // set the current dialog the biggest found z-index
        // set the current dialog the biggest found z-index
        currentDialog.setStyle({ zIndex: biggestZIndex });    // now walk thr each other dialog and if it has the same z-index, decrease it by one
        // now walk thr each other dialog and if it has the same z-index, decrease it by one
        otherDialogs.each(function (dialog) {
            var currentZIndex = dialog.getStyle('zIndex');
            if (currentZIndex >= biggestZIndex) {
                dialog.setStyle({ zIndex: biggestZIndex - 1 });
            }
        });
    }
};
dialogs.popupConfirm = _.extend({}, dialogs.popup, {
    show: function (elem, showDimmer, options) {
        dialogs.popup.show.apply(this, arguments);
        options = _.extend({
            okButtonSelector: 'button.ok',
            cancelButtonSelector: 'button.cancel'
        }, options);
        var $elem = jQuery(elem), $ok = jQuery($elem).find(options.okButtonSelector), $cancel = jQuery($elem).find(options.cancelButtonSelector), deferred = jQuery.Deferred();
        jQuery($ok).on('click', function () {
            if (options.validateFunc) {
                if (options.validateFunc() === false) {
                    return;
                }
            }
            jQuery($ok).off('click');
            jQuery($cancel).off('click');
            dialogs.popupConfirm.hide(elem);
            deferred.resolve();
        });
        jQuery($cancel).on('click', function () {
            jQuery($ok).off('click');
            jQuery($cancel).off('click');
            dialogs.popupConfirm.hide(elem);
            deferred.reject();
        });
        return deferred;
    }
});
dialogs.childPopup = _.extend({}, dialogs.popup, {
    cascadePopups: {},
    show: function (elem, showDimmer, parent, options) {
        if (parent) {
            options = _.extend({ parent: parent }, options);
            this.cascadePopups[parent.id] || (this.cascadePopups[parent.id] = []);
            this.cascadePopups[parent.id].push(elem.id);
            jQuery(elem).attr('data-parentDialog', parent.id)[0];
            dialogs.popup.show.apply(this, [
                elem,
                showDimmer,
                options
            ]);
            dialogs.popup._setHigherZIndex(elem, parent);
            showDimmer && pageDimmer.setZindex(elem.getStyle('zIndex') - 1);
            parent.stopObserving('click', dialogs.popup.zIndexHandler);
        } else {
            dialogs.popup.show.apply(this, [
                elem,
                showDimmer,
                options
            ]);
        }
    },
    hide: function (elem) {
        var parentId = jQuery(elem).attr('data-parentDialog');
        if (parentId) {
            if (this.cascadePopups[parentId]) {
                this.cascadePopups[parentId] = _.without(this.cascadePopups[parentId], elem.id);
            }
            dialogs.popup.hide.apply(this, [elem]);
            var parent = jQuery('#' + parentId)[0];
            pageDimmer.show(parent);
            dialogs.popup._setHigherZIndex(parent);
        } else {
            dialogs.popup.hide.apply(this, [elem]);
        }
    }
});

//expose to global scope
window.dialogs = dialogs;

export default dialogs;