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
 * @version: $Id: Menu.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        $ = require("jquery"),
        _ = require("underscore"),
        OptionView = require("common/component/option/OptionView"),
        menuOptionTemplate = require("text!common/component/menu/template/menuOptionTemplate.htm"),
        menuContainerTemplate = require("text!common/component/menu/template/menuContainerTemplate.htm");

    /*
     * Basic Menu component.
     *
     * Usage:
     *      var menu = new Menu([ { label: "Save Dashboard", action: "save" } ]);
     *
     * @param options Array containing objects with "label" and "action" properties, e.g.
     *      [ { label: "Save Dashboard", action: "save" }, { label: "Save Dashboard As...", action: "saveAs" } ]
     */
    return Backbone.View.extend({
        template: _.template(menuContainerTemplate),

        el: function() {
            return this.template();
        },

        constructor: function(options) {
            if (!options || !_.isArray(options) || options.length === 0) {
                throw new Error("Menu should have options");
            }

            this.options = [];

            this.collection = new Backbone.Collection(options);

            this.listenTo(this.collection, "click", this._selectOption);

            Backbone.View.apply(this, arguments);
        },

        initialize: function() {
            this.$list = this.$("> .content > ul");

            this.render();
        },

        _selectOption: function(optionModel) {
            this.trigger("option:" + optionModel.get("action"), optionModel.toJSON());
        },

        render: function() {
            var self = this;

            this.collection.forEach(function(optionModel) {
                var view = new OptionView({
                    template: menuOptionTemplate,
                    model: optionModel
                });

                self.options.push(view);
                self.$list.append(view.$el);
            });

            $("body").append(this.$el);

            return this;
        },

        show: function() {
            this.$el.show();

            this.trigger("show", this);

            return this;
        },

        hide: function() {
            this.$el.hide();

            this.trigger("hide", this);

            return this;
        },

        remove: function() {
            _.invoke(this.options, "remove");

            Backbone.View.prototype.remove.apply(this, arguments);
        }
    });
});