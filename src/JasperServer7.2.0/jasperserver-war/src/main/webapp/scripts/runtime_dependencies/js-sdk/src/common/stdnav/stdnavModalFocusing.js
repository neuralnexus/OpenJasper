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
 * Modal focus management handlers
 *
 */

define(function (require, exports, module) {
    "use strict";
    var
        $ = require("jquery"),
        _ = require("underscore"),
        logger = require("logger").register(module);

    return {

        // Suspends the focusability of all elements in the DOM which do not
        // have the element provided as a parent, except for the element itself.
        // TODO: Find a faster way to do this!
        beginModalFocus: function(element){
            if (!element){
                return;
            }

            // If a modal dialog was already active, assume it has been
            // superceded by this one.  This should also handle the case
            // where two different "threads" pop up the same dialog.
            if (this.modalDialogActive===true){
                this.endModalFocus(this.modalDialogRoot);
            }

            var $el=$(element);
            var $nonDialog=$('body *').filter(function(){
                return (!($.contains(element, this)||($el.is(this))));
            });
            this.modalDialogActive=true;
            this.modalDialogRoot=element;
            $nonDialog.each(function(index, ndEl){
                var $ndEl=$(ndEl);
                var priorTabIndex=$ndEl.attr('tabIndex');
                if (typeof(priorTabIndex)==='undefined'){
                    $ndEl.attr('js-nonmodal-tabindex', 'undefined');
                    $ndEl.attr('tabIndex', '-1');
                }else{
                    $ndEl.attr('js-nonmodal-tabindex', priorTabIndex);
                    $ndEl.attr('tabIndex', '-1');
                }
            });
        },

        // Resumes the focusability of all elements in the DOM which do not
        // have the element provided as a parent.
        // TODO: Find a faster way to do this!
        endModalFocus: function(element){
            var $el, $nonDialog;
            if (!element) {
                // Try to allow failsafe tabindex restoration.
                $el=$(); // (Returns an empty jQuery set.)
                $nonDialog=$('body *');
            } else {
                $el=$(element);
                $nonDialog=$('body *').filter(function(){
                    return (!($.contains($el, this)||($el.is(this))));
                });
            }
            $nonDialog.each(function(index, ndEl){
                var $ndEl=$(ndEl);
                var priorTabIndex=$ndEl.attr('js-nonmodal-tabindex');
                if (typeof(priorTabIndex)==='undefined'){
                    // Shouldn't happen, but it might.  Implies content was
                    // added to the non-modal DOM sections WHILE the modal
                    // subDOM was being shown.  For now, do nothing.
                }
                else if (priorTabIndex==='undefined'){
                    $ndEl.removeAttr('js-nonmodal-tabindex');
                    $ndEl.removeAttr('tabIndex');
                }else{
                    $ndEl.removeAttr('js-nonmodal-tabindex');
                    $ndEl.attr('tabIndex', priorTabIndex);
                }
            });
            this.modalDialogActive=false;
            this.modalDialogRoot=null;
        }
    }
});