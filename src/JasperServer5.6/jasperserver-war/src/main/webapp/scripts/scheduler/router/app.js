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
 * @version: $Id: app.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/router/app', ['backbone'], function(Backbone){

    return Backbone.Router.extend({

        // default app route
        routes: { '*url': 'defaultRoute' },

        // initialize router
        initialize: function(app){
            // save app
            this.app = app;

            // routing for list
            this.route(/^list\/([^@]*)?[@@]?.*$/, 'list');

            // routing for editing
            this.route(/^edit\/([^@]*)@?(.*)?$/, 'edit');

            // routing for creating
            this.route(/^create\/([^\$@]*)\$?([^@\/]*)?@?([^@]*)?[@@]?.*$/, 'create');

            // handle routing to save latest url
            this.on('all', function(){
                this.save(location.hash.substr(1));
            });
        },

        // show list
        list: function(url){
            url = decodeURI(url);
            //this.app.page(this.app.list);
            this.app.list.show(url);
        },

        // init editing
        edit: function(id, tab){
            this.app.editor.edit(id, tab);
        },

        // init creation
        create: function(url, option, tab){
            url = decodeURI(url);
            this.app.editor.create(url, tab, option);
        },

        // override navigate method to handle all url
        // changes and save current state to localstorage
        navigate: function(url, options){
            this.save(url);
            Backbone.Router.prototype.navigate.apply(this, arguments);
        },

        // save url to localstorage
        save: function(url){
            window.localStorage && localStorage.setItem('scheduler-last-url', url);
        },

        // default view
        defaultRoute: function(url){
            // if empty url try to load last url from localstorage
            if (!url && window.localStorage){
                url = localStorage.getItem('scheduler-last-url');
                if (url) return this.navigate(url, true);
            }

            // private variables
            var match;

            // add hash from query params
            if (match = location.search.match(/\?reportUnitURI=(.*)/))
                location.hash = 'list' + match[1];

            // default actions
            if (this.app.current)
                this.app.current.$el.addClass('hidden');
        }

    });

});