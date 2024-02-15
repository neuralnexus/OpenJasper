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
 * @version: $Id: jobs.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/view/jobs', function (require) {

    require("utils.common");

    var $ = require('jquery'),
        Backbone = require('backbone'),
        jobView = require('scheduler/view/job'),
        jobModel = require('scheduler/model/job'),
        jobsCollection = require('scheduler/collection/jobs'),
        jrsConfigs = require("jrs.configs"),
        jobUtil = require("scheduler/util/jobUtil");

    return Backbone.View.extend({

        // list page
        el: '#list.page',

        // binded events for view
        events: {
            'click .backButton': 'back',
            'click .scheduleJob': 'create',
            'click .runJob': 'run',
            'click .refreshList': 'refresh'
        },

        // initialize view
        initialize: function(options){
            // initialize view app
            options.app && (this.app = options.app);

            // get title element link
            this.title = this.$('.header .title .path');

            // find inner container
            this.container = $('#resultsList');

            // create collection
            this.collection = new jobsCollection();

            // handle collection changes
            this.collection.on('add', this.add, this);
            this.collection.on('reset destroy', this.render, this);
        },

        // show jobs collection
        show: function(uri){
            // update uri
            if (uri) this.uri = uri;

            // set uri to title
            this.title.text('/' + this.uri);

            // show list page
            this.app.page(this);

            // refresh list
            this.refresh();
        },

        // render collection
        render: function(){
            // clear container
            this.container.empty();

            // render collection items
            if (this.collection.length){
                // hide empty placeholder
                $('#nothingToDisplay').addClass('hidden');

                // show items container
                this.container.parent().removeClass('hidden');

                // iterate over collection and add items
                return this.collection.each(_.bind(this.add, this));
            }

            // no items found, hide container
            this.container.parent().addClass('hidden');

            // show empty placeholder
            $('#nothingToDisplay').removeClass('hidden');
        },

        // refresh jobs from server
        refresh: function() {
            var uri = "/" + this.uri;
			if (this.parentReportURI) {
				uri = this.parentReportURI;
			}
            this.collection.fetch(uri);
        },

        // handle new job creation
        create: function(){
            this.app._ourPanelButtonClick = true;
			this.app._runNowClick = false;
            this.app.router.navigate('create/' + this.uri, true);
        },

        // add one element to collection
        add: function(item){
            var view = new jobView({ app: this.app, model: item });
            this.container.append(view.render().$el);
        },

        // go back to document referrer
        back: function(){
            jobUtil.back(jobUtil.SCHEDULER_BACK_COOKIE_NAME);
        },

        // create new job and run it immediately
        run: function(){
            this.app._ourPanelButtonClick = true;
			this.app._runNowClick = true;
            this.app.router.navigate('create/' + this.uri + '$fast', true);
        }

    });

});