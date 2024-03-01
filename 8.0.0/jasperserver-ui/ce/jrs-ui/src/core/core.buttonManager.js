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


import layoutModule from "./core.layout";
import {isIPad} from "../util/utils.common";
import jQuery from 'jquery';


let buttonManager = {
    over: function (element, findTargetFn) {
        if (element && !this.isSelected(element)) {
            var target = findTargetFn ? findTargetFn(element) : element;
            jQuery(target).addClass(layoutModule.HOVERED_CLASS);
        }
    },
    out: function (element, findTargetFn) {
        if (element) {
            var target = findTargetFn ? findTargetFn(element) : element;
            jQuery(target).removeClass(layoutModule.HOVERED_CLASS).removeClass(layoutModule.PRESSED_CLASS);
        }
    },
    down: function (element, findTargetFn) {
        if (element && !this.isSelected(element)) {
            var target = findTargetFn ? findTargetFn(element) : element;
            jQuery(target).removeClass(layoutModule.HOVERED_CLASS).addClass(layoutModule.PRESSED_CLASS);
        }
    },
    up: function (element, findTargetFn) {
        if (element && !this.isSelected(element)) {
            var target = findTargetFn ? findTargetFn(element) : element;
            target = jQuery(target);
            target.removeClass(layoutModule.PRESSED_CLASS);
            !isIPad() && target.addClass(layoutModule.HOVERED_CLASS);
        }
    },
    disable: function (element) {
        if (element) {
            buttonManager.out(element);
            jQuery(element).attr(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
    },
    enable: function (element) {
        if (element) {
            buttonManager.out(element);
            jQuery(element).attr(layoutModule.DISABLED_ATTR_NAME, null);
        }
    },
    /**
     * @deprecated custom jasperhandler in Prototype.js will suppress disabled elems
     * @param {Object} element
     */
    isDisabled: function (element) {
        if (element) {
            return jQuery(element).attr(layoutModule.DISABLED_ATTR_NAME) === layoutModule.DISABLED_ATTR_NAME || jQuery(element).hasClass(layoutModule.DISABLED_CLASS);
        }
    },
    ///////////////////////////////////////////////////////////////////////////////////////
    // TODO: Only used by tab manager - maybe we should use up and down functions instead.
    // (just need to make tabs use 'pressed' class instead of 'selected')
    ///////////////////////////////////////////////////////////////////////////////////////
    unSelect: function (element) {
        if (element) {
            jQuery(element).removeClass(layoutModule.SELECTED_CLASS);
        }
    },
    select: function (element) {
        if (element) {
            jQuery(element).addClass(layoutModule.SELECTED_CLASS);
        }
    },
    isSelected: function (element, findTargetFn) {
        if (element) {
            var target = findTargetFn ? findTargetFn(element) : jQuery(element)[0];
            var tagetListItem = target.up('li');
            return tagetListItem && jQuery(tagetListItem).hasClass(layoutModule.SELECTED_CLASS);
        }
        return false;
    }
};

export default buttonManager;