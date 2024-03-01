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

import $ from 'jquery';
import AttachableMenu from './AttachableMenu';
import _ from 'underscore';
export default AttachableMenu.extend({
    _isVisible: false,
    _elementHovered: false,
    _menuHovered: false,
    TIME_BETWEEN_MOUSE_OVERS: 200,
    constructor: function (options, attachTo, padding, additionalSettings) {
        _.bindAll(this, '_onAttachToMouseOver', '_onAttachToMouseOut');
        this.padding = padding || {
            top: 0,
            left: 0
        };
        AttachableMenu.call(this, options, attachTo, this.padding, additionalSettings);
        this.on('mouseover container:mouseover', this._onMenuItemMouseOver);
        this.on('mouseout container:mouseout', this._onMenuItemMouseOut);
        this.on('selectionMade', this._hide);
    },
    setAttachTo: function (attachTo) {
        this._removeEventListeners();
        AttachableMenu.prototype.setAttachTo.call(this, attachTo);
        this._addEventListeners();
    },
    hide: function () {
        this._hide();
    },
    _onMenuItemMouseOver: function () {
        this._menuHovered = true;
        this._elementHovered = false;
    },
    _onMenuItemMouseOut: function () {
        this._menuHovered = false;
        this._hideByTimeout();
    },
    _onAttachToMouseOver: function () {
        if (this.$attachTo.is(':disabled')) {
            return;
        }
        this._elementHovered = true;
        if (!this._isVisible) {
            this.show();
            this._isVisible = true;
        }
    },
    _onAttachToMouseOut: function (event) {
        var relatedTarget = event.relatedTarget;
        if (this.el !== relatedTarget && !$.contains(this.el, relatedTarget)) {
            this._elementHovered = false;
            this._hideByTimeout();
        }
    },
    _hideByTimeout: function () {
        if (this._elementHovered || this._menuHovered) {
            return;
        }
        setTimeout(_.bind(this._tryHide, this), this.TIME_BETWEEN_MOUSE_OVERS);
    },
    _tryHide: function () {
        if (this._elementHovered || this._menuHovered) {
            return;
        }
        this._hide();
    },
    _hide: function () {
        AttachableMenu.prototype.hide.call(this);
        this._isVisible = false;
        this.trigger('hidden');
    },
    _addEventListeners: function () {
        this.$attachTo && this.$attachTo.on('mouseover', this._onAttachToMouseOver);
        this.$attachTo && this.$attachTo.on('mouseout', this._onAttachToMouseOut);
    },
    _removeEventListeners: function () {
        this.$attachTo && this.$attachTo.off('mouseover', this._onAttachToMouseOver);
        this.$attachTo && this.$attachTo.off('mouseout', this._onAttachToMouseOut);
    },
    remove: function () {
        this._removeEventListeners();
        AttachableMenu.prototype.remove.apply(this, arguments);
    }
});