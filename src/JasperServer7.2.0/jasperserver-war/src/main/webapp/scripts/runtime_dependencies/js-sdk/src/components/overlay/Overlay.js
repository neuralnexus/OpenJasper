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
 * @author: valeriy.abornyev
 * @version: $Id: LoadingOverlay.js 1025 2016-08-11 12:58:43Z psavushc $
 */

define(function (require, exports, module) {
    "use strict";

    var _ = require("underscore"),
        $ = require("jquery"),
        Backbone = require("backbone"),

        overlayTemplate = require("text!./template/overlayTemplate.htm");

    var DEFAULT_ZINDEX = 4000;

    return Backbone.View.extend({

        el: function () {
            return this.template();
        },

        template: _.template(overlayTemplate),

        initialize: function (options) {
            options = _.defaults(options || {}, {
                zIndex: DEFAULT_ZINDEX
            });

            this.delay = options.delay;

            this.$el.css({
                zIndex: options.zIndex
            });

            this.$el.parent().css({position: "relative"});
        },

        show: function(delay) {
            var self = this,
                show = function () {
                    self.$el.show();
                    self.$el.removeClass("jr-isHidden");
                };

            if (this.delay || delay){
                if (!this._timer){
                    this._timer = setTimeout(show, this.delay || delay);
                }
            } else {
                show();
            }
        },

        hide: function() {
            if (this._timer) {
                clearTimeout(this._timer);
                this._timer = null;
            }

            this.$el.hide();
            this.$el.addClass("jr-isHidden");
        }
    });

});
