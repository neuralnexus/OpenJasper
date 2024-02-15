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
 * @version: $Id: job.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/view/job', function(require){

    var _ = require('underscore'),
        Backbone = require('backbone'),
        dialogs = require('components.dialogs'),
        template = require('text!scheduler/template/job.htm'),
        i18n = require('bundle!jasperserver_messages'),
        config = require('jrs.configs'),
        moment = require("moment");

    return Backbone.View.extend({

        // view element tagname
        tagName: 'li',
        className: 'jobs first leaf',

        // binded events for view
        events: {
            'click .editJob': 'edit',
            'click .deleteJob': 'remove',
            'change .enableJob': 'enable'
        },

        // initialize view
        initialize: function(options){
            // save link to app
            options.app && (this.app = options.app);

            // create template
            this.template = _.template(template);

            // handle model changes
            this.model.on('change', this.render, this);
        },

        // render view
        render: function(){
            this.$el.html(this.template({
                model: this.model.toJSON(),
                i18n: i18n,
                timeZoneOffsetFunction: function(dateString){
                    return -1 * getTZOffset(config.usersTimeZone, dateString) * 60;
                }
            }));
            return this;
        },

        // edit job
        edit: function(){
            this.app.router.navigate('edit/' + this.model.id, true);
        },

        // remove job
        remove: function(){
            var t = this, text;

            text = i18n['report.scheduling.delete.label'].
                replace('{label}', t.model.get('label')).
                replace('{newline}', '<br><br>');

            this.app.handleTwoButtonDialog(text, function(){
                t.model.destroy();
            });
        },

        // enable/disable job
        enable: function(event){
            this.model.state(event.target.checked);
        }

    });

});