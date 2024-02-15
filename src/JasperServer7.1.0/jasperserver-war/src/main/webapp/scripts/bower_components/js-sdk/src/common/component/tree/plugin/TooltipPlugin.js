/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
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
 * @author: Zakhar Tomchenko
 * @version: $Id$
 */

define(function(require){
    "use strict";

    var TreePlugin = require("./TreePlugin"),
        _ = require("underscore"),
        Tooltip = require("common/component/tooltip/Tooltip");

    return TreePlugin.extend({
        initialize: function(options){
            this.tooltip = Tooltip.attachTo(options.attachTo, _.omit(options, "el"));

            this.listensToList = false;

            TreePlugin.prototype.initialize.apply(this, arguments);
        },

        itemsRendered: function(model, list){
            var self = this;

            // this because of list triggers render:data event often
            if(!this.listensToList){
                this.listensToList = true;

                this.listenTo(list, "list:item:mouseover", function(item, e){
                    self.tooltip.show(item);
                });

                this.listenTo(list, "list:item:mouseout", function(){
                    self.tooltip.hide();
                });
            }

        },

        remove: function() {
            Tooltip.detachFrom(this.$el);

            this.tooltip.remove();

            TreePlugin.prototype.remove.apply(this, arguments);
        }
    });
});
