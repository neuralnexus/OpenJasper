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
 * @author: Yuriy Plakosh
 * @version: $Id: components.dialogs.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

/**
 * Dialogs Module.
 */

var dialogs = {};

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
    show: function(message, duration) {
        this.container = this.container || jQuery('#systemMessageConsole').on('mouseup touchend', function() {
            dialogs.systemConfirm.container.slideUp();
        });
        this.message = this.message || document.getElementById('systemMessage');
        if (!this.closeText){
            this.closeText = this.message.innerHTML.toLowerCase();
        }
        this.message.innerHTML = message + ' <span>| <a href="#">'+ this.closeText +'</a></span>';
        dialogs.systemConfirm.container.slideDown();
        setTimeout('dialogs.systemConfirm.hide()', duration ? duration : 2000);
    },

    showWarning: function(message, duration) {
        message = '<span class="warning">' + message + '</span>';
        this.show(message, duration);
    },

    hide: function() {
        if (dialogs.systemConfirm.container) {
            dialogs.systemConfirm.container.slideUp();
        }
    }
};

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
    _DOM_ID: "standardAlert",

    /**
     * The identifier of the element there error content should be placed.
     */
    _CONTENT_ID: "errorPopupContents",

    /**
     * Pattern of element in response where error is placed
     */
    _PAGE_CONTENT_PATTERN: "#errorPageContent",

    _DIALOG_WIDTH: "546px",

    _DIALOG_HEIGHT: "350px",

    clickHandler: null,

    /**
     * Shows popup dialog.
     *
     * @param errorContent error content to be showed
     */
    show: function(errorContent, isStackTraceContent, options) {
        options || (options = {});

        var fromSource = Builder.node('DIV', {style:'display:none'});
        fromSource.innerHTML = errorContent;
        document.body.insertBefore(fromSource, document.body.firstChild);
        var content = $$(this._PAGE_CONTENT_PATTERN)[0];
        var contentText = content ? content.innerHTML : errorContent;
        var isStackTrace = (content && content.innerHTML);

        fromSource.remove();

        if (contentText) {
           if (!this._dom) {
                this._dom = $(this._DOM_ID);
                this._content = $(this._CONTENT_ID);
                this.clickHandler = this._clickHandler.bindAsEventListener(this);
            }

            if (this._dom) {
                var finalContent = contentText;

                //If error is a plain text - wrap it into <p> element
                if (!isStackTrace) {
                    finalContent = Builder.node('P', {'class':'message'});
                    finalContent.update(contentText);
                }

                this._content.update(finalContent);
                this._dom.observe('click', this.clickHandler);

                this._dom.setStyle({height: options.height || this._DIALOG_HEIGHT, width: options.width || this._DIALOG_WIDTH});
                isStackTraceContent && this._dom.addClassName(layoutModule.STACKTRACE_CLASS);
                dialogs.popup.show(this._dom, isStackTraceContent);

                var st = document.getElementById('completeStackTrace');
                if(st) {
                	isIPad() && new TouchController(st,st.parentNode,{noInit3d:true});
                }
            }
        }
    },

    /**
     * Hides popup dialog.
     */
    _hide: function() {
        if (this._dom) {
            this._dom.stopObserving('click', this.clickHandler);
            dialogs.popup.hide(this._dom);
        }
    },

    /*
     * Mouse click handler for close button
     */
    _clickHandler: function(event) {
        var element = event.element();

        if (matchAny(element, ['button'], true)) {
            this._hide();
        }
    }
};

 dialogs.clusterErrorPopup = _.extend({}, dialogs.errorPopup, {
    show: function(errorContent) {
        var that = this;
        require(["bundle!jasperserver_messages", "bundle!jsexceptions_messages"], function(bundle1, bundle2) {
            var clusterErrorPopupMsg = bundle2["cluster.exception.session.attribute.missing.popup"];
            dialogs.errorPopup.show.call(that, clusterErrorPopupMsg);

            var buttonLabel = bundle1["button.home"];
            jQuery("#"+that._DOM_ID).find("button span.wrap").html(buttonLabel);
        })
    },

    _hide: function(event) {
        dialogs.errorPopup._hide.apply(this, arguments);
        window.location = "home.html";
    }
 });


/**
 * generic 'popup' dialog controller
 * @param {Object} elem
 */
dialogs.popup = {
	show: function(elem, showDimmer, options) {

        options = options || {};

        elem = $(elem);

        if (!elem) {
            return;
        }
        /*
         * Hack for all 4 sides drop shadow. Applying drop shadow effect directly on dialog.overlay element causes
         * cursor to be shifted out of inner input elements.
         */
        if(isIE7() || isIE8()) {
            jo = jQuery(elem);
            if(jo.children('div.msshadow').length == 0) {
                jo.prepend('<div class="msshadow" style="position:absolute;top:-10px;right:10px;bottom:10px;left:-10px;border:0;background:#fff;">&nbsp;</div>');
            }
        }

        //dimmer
        if (showDimmer) {
            pageDimmer.show();
            elem.match(layoutModule.DIALOG_LOADING_PATTERN) && pageDimmer.setZindex(elem.getStyle('zIndex') - 1);
        }

        //ensure body is parent
        reParent(elem, document.body);

        elem.setOpacity(0);

        elem.removeClassName(layoutModule.HIDDEN_CLASS);

        isIE7() && !elem.match(".sizeable") && setWidthStyleByReflection(elem, '.content');

        layoutModule.createMover.call(layoutModule, elem);
        layoutModule.createSizer.call(layoutModule, elem);

        if (options.cascade) {
            //cascade
            cascadeElement(elem, {position: options.position, number: options.number, horzOffset: 40, vertOffset: 40});
        } else {
            //center
            centerElement(elem, {horz: true, vert: true});
        }
        //raise if necessary
        dialogs.popup._setMaxZIndex(elem);

        // Drag'&'Drop depends depends from zIndex on dialog
        layoutModule.createMover.call(layoutModule, elem); // To move dialog on foreground we need initialize DnD after we have zIndex for dialog

        isIPad() ? (elem.setOpacity(1.0) && elem.show()) : appear(elem, 0.4);

        //focus
        elem.tabIndex = -1;

        //Set focus on dialog if options.focus is not present in options or it's set to true
        if (!options || typeof options.focus === "undefined" || options.focus) {
            if(isIE8()) {
                setTimeout(function() {
                    try {
                        // you may wonder, but IE8 can say: Can't move focus to the control because it is invisible, not enabled, or of a type that does not accept the focus
                        // so, let's cover this tricky case
                        elem.focus()
                    } catch(e){}
                }, 450);
            } else {
                elem.focus();
            }
        }

        !showDimmer && elem.observe('click', dialogs.popup.zIndexHandler);
	},

	hide: function(elem) {
        if (!elem) {
            return;
        }

		elem = $(elem);
        //hide dialog and dimmer
        if (!elem.hasClassName(layoutModule.HIDDEN_CLASS)) {
            elem.addClassName(layoutModule.HIDDEN_CLASS);
            pageDimmer.hide();
            elem.match(layoutModule.DIALOG_LOADING_PATTERN) && pageDimmer.setZindex(layoutModule.DIMMER_Z_INDEX);
        }
        elem.stopObserving('click', dialogs.popup.zIndexHandler);
	},

    /**
     * Handler for dialog z-index change on click.
     */
    zIndexHandler: function(event) {
        var element = Event.element(event);

        var dialog = matchMeOrUp(element, layoutModule.DIALOG_PATTERN);
        dialogs.popup._setMaxZIndex(dialog);
    },

    _setMaxZIndex: function(dialog, parentDialog) {
        if (dialog) {
            var zIndex = parentDialog ? parentDialog.getStyle('zIndex') : 0;

            var dialogs = document.body.select(layoutModule.DIALOG_PATTERN);
            var zIndexResolver = function(currentDialog) {
                // If visible and not loading dialog (it should have the highest z-index).
                if (currentDialog.visible() && !currentDialog.match(layoutModule.DIALOG_LOADING_PATTERN)) {
                    zIndex = Math.max(zIndex, currentDialog.getStyle('zIndex'),layoutModule.DIMMER_Z_INDEX + 1);
                }
            };
            dialogs.length == 1 ? zIndexResolver(dialogs[0]) : dialogs.each(zIndexResolver);

            dialog.setStyle({
                zIndex : Math.max(zIndex + 1, dialog.getStyle('zIndex'))
            });
        }
    }

};

dialogs.popupConfirm = _.extend({}, dialogs.popup,
    {
    show: function(elem, showDimmer, options) {
        dialogs.popup.show.apply(this, arguments);
        options = _.extend({okButtonSelector: "button.ok", cancelButtonSelector: "button.cancel"}, options);
        var $elem = jQuery(elem),
            $ok = $elem.find(options.okButtonSelector),
            $cancel = $elem.find(options.cancelButtonSelector),
            deferred = jQuery.Deferred();

        $ok.on("click", function() {
            if (options.validateFunc) {
                if (options.validateFunc() === false) {
                    return;
                }
            }
            $ok.off("click");
            $cancel.off("click");
            dialogs.popupConfirm.hide(elem);
            deferred.resolve();
        });

        $cancel.on("click", function() {
            $ok.off("click");
            $cancel.off("click");
            dialogs.popupConfirm.hide(elem);
            deferred.reject();
        });
        return deferred;
    }
});

dialogs.childPopup = _.extend({}, dialogs.popup,{
    cascadePopups: {},

    show: function(elem, showDimmer, parent, options) {
        if (parent) {
            options = _.extend({parent: parent}, options);

            this.cascadePopups[parent.id] || (this.cascadePopups[parent.id] = []);
            this.cascadePopups[parent.id].push(elem.id);

            $(elem).setAttribute("data-parentDialog", parent.id);
            dialogs.popup.show.apply(this, [elem, showDimmer, options]);

            dialogs.popup._setMaxZIndex(elem, parent);
            showDimmer && pageDimmer.setZindex(elem.getStyle('zIndex') - 1);

            parent.stopObserving('click', dialogs.popup.zIndexHandler);
        } else {
            dialogs.popup.show.apply(this, [elem, showDimmer, options]);
        }
    },

    hide: function(elem) {
        var parentId = $(elem).getAttribute("data-parentDialog");

        if (parentId) {
            if (this.cascadePopups[parentId]) {
                this.cascadePopups[parentId] = _.without(this.cascadePopups[parentId], elem.id);
            }
            dialogs.popup.hide.apply(this, [elem]);

            var parent = $(parentId);
            pageDimmer.show(parent);
            dialogs.popup._setMaxZIndex(parent);
        } else {
            dialogs.popup.hide.apply(this, [elem]);
        }
    }
});