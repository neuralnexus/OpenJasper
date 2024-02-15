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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: HoverMenu.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Menu = require("common/component/menu/Menu"),
        $ = require("jquery"),
        _ = require("underscore");

    var TIME_BETWEEN_MOUSE_OVERS = 10;

    /*
     * HoverMenu component - shows menu near element when element is mouseovered.
     *
     * Usage:
     *      var hoverMenu = new HoverMenu([ { label: "Save Dashboard", action: "save" } ], "#someElement");
     *
     * @param options Array containing objects with "label" and "action" properties, e.g.
     *      [ { label: "Save Dashboard", action: "save" }, { label: "Save Dashboard As...", action: "saveAs" } ]
     *
     * @param attachTo String (selector), HTML element or jQuery object. Element to attach menu to.
     */
    return Menu.extend({
        events: {
            "mouseover": "_onMenuMouseOver",
            "mouseout": "_onMenuMouseOut"
        },

        constructor: function(options, attachTo) {
            if (!attachTo || $(attachTo).length === 0) {
                throw new Error("HoverMenu should be attached to an element");
            }

            Menu.call(this, options);

            _.bindAll(this, "_onElementMouseOver", "_onElementMouseOut");

            this.$attachTo = $(attachTo);
            this.$attachTo.on("mouseover", this._onElementMouseOver);
            this.$attachTo.on("mouseout", this._onElementMouseOut);
        },

        _onMenuMouseOver: function() {
            this._menuHovered = true;
        },

        _onMenuMouseOut: function() {
            this._menuHovered = false;

            this._tryHide();
        },

        _onElementMouseOver: function() {
            this._elementHovered = true;

            if (!this.$el.is(":visible")) {
                this.show();
            }
        },

        _onElementMouseOut: function() {
            this._elementHovered = false;

            this._tryHide();
        },

        _tryHide: function() {
            setTimeout(_.bind(function() {
                if (!this._elementHovered && !this._menuHovered) {
                    this.hide();
                }
            }, this), TIME_BETWEEN_MOUSE_OVERS);
        },

        show: function() {
            var offset = this.$attachTo.offset(),
                height = this.$attachTo.height();

            this.$el.css({
                top: offset.top + height,
                left: offset.left
            });

            return Menu.prototype.show.apply(this, arguments);
        },

        remove: function() {
            this.$attachTo.off("mouseover", this._onElementMouseOver);
            this.$attachTo.off("mouseout", this._onElementMouseOut);

            Menu.prototype.remove.apply(this, arguments);
        }
    });
});