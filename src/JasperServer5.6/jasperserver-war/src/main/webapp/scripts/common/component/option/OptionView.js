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


define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore");

    return Backbone.View.extend({
        events: {
            "click": "_click"
        },

        constructor: function(options) {
            if (!options || !options.template) {
                throw new Error ("Option should have defined template");
            }

            if (!options.model || !(options.model instanceof Backbone.Model)) {
                throw new Error ("Option should have associated Backbone.Model");
            }

            this.template = _.template(options.template);
            this.toggle = !!options.toggle;
            this.toggleClass = options.toggleClass || "active";

            Backbone.View.apply(this, arguments);
        },

        el: function() {
            return this.template(this.model.toJSON());
        },

        _click: function() {
            this.model.trigger("click", this.model);

            this.toggle && this.$el.toggleClass(this.toggleClass);
        }
    });
});