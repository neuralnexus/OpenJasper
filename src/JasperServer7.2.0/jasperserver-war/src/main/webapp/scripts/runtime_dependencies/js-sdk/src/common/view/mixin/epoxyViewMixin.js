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

define(function(require){

    var Epoxy = require("backbone.epoxy"),
        _ = require("underscore"),
        epoxyCustomBindingHandlers = require("./epoxyCustomBindingHandlers"),
        epoxyCustomBindingFilters = require("./epoxyCustomBindingFilters");

    return {

        epoxifyView: function(){
            var originalRemove = this.remove;

            Epoxy.View.mixin(this);

            // Epoxy overrides remove method by it's own when mixin;
            originalRemove && (this.remove = originalRemove);

            this.bindingFilters ? (this.bindingFilters = _.extend({}, epoxyCustomBindingFilters, this.bindingFilters))
                : epoxyCustomBindingFilters;
            this.bindingHandlers ? (this.bindingHandlers = _.extend({}, epoxyCustomBindingHandlers, this.bindingHandlers))
                : epoxyCustomBindingHandlers;
        },

        applyEpoxyBindings: function(){
            this.applyBindings && this.applyBindings();
        },

        removeEpoxyBindings: function(){
            this.removeBindings && this.removeBindings();
        }
    };
});