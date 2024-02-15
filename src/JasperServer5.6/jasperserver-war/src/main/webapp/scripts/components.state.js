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

/*
 * @author inesterenko
 */

jaspersoft.components.State = (function (jQuery, _, Backbone, exports, ServerErrorsBackboneTrait) {

    return Backbone.Model
        .extend(ServerErrorsBackboneTrait)
        .extend({

            name : "export.zip",
            urlTemplate : "rest_v2/{service}/{id}/state",

            defaults:{
                "id":null,
                "phase":"not started",
                "message": ""
            },

            initialize:function () {
                _.bindAll(this);

                this.on("change:phase", this.notify);

            },

            url:function () {
                if (!this.has('id')) {
                    throw Error("Can't initialize export state without 'id'")
                }
                return this.urlTemplate.replace("{id}", this.get("id"));
            },

            reset:function () {
                this.name = "export.zip";
                this.set(this.defaults);
            },

            notify: function(){
                if (this.get("phase") === exports.State.FAILED){
                    this.trigger("error:server", this.attributes);
                    this.reset();
                } else if (this.get("phase") === exports.State.READY){
                    this.trigger("notification:show", this.attributes);
                }
            }
        }, {
            NOT_STARTED:"not started",
            INPROGRESS:"inprogress",
            READY:"finished",
            FAILED: "failed",

            instance : function(options){
                var inst = new this();
                if (options && options.urlTemplate){
                    inst.urlTemplate = options.urlTemplate;
                }
                return inst;
            }
        })
})(
    jQuery,
    _,
    Backbone,
    jaspersoft.components,
    jaspersoft.components.ServerErrorsBackboneTrait

);