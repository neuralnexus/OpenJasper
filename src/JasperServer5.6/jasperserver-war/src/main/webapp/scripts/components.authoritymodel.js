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
 * @author: ztomchenco
 * @version: $Id: components.authoritymodel.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.AuthorityModel = (function (jQuery, _, Backbone, Engine) {

    return Backbone.Model.extend({

            defaults: {
                items: []
            },

            url: function(){
                return Engine.renderUrl(this.urlTemplate, this.context, true);
            },

            parse: function(responce) {
                return {items:responce ? ((responce.role)? responce.role : responce.user) : []};
            },

            defaultErrorHandler: function(model, xnr){
                var data;
                try{
                    data = jQuery.parseJSON(xnr.responseText);
                }catch (error){ // this is not JSON
                    data = null;
                }
                this.trigger("error:server",xnr.status, data, model);
            },

            setContext:  function(context){
                this.context = context;
                this.fetch({error:_.bind(this.defaultErrorHandler, this)});
            }

        }, {
            instance: function(urlTemplate, context){
                var inst = new this();
                inst.urlTemplate = urlTemplate;
                context && inst.setContext(context);
                return inst;
            }
        });
})(
    jQuery,
    _,
    Backbone,
    jaspersoft.components.templateEngine
);
