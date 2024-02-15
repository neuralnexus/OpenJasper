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

define('scheduler/view/app', function(require){

    // dependencies
    require('commons.main');
    require("utils.common");

    var $ = require('jquery'),
        Backbone = require('backbone'),
        domReady = require('!domReady'),
        jrsConfigs = require('jrs.configs'),
        jobsView = require('scheduler/view/jobs'),
        editorView = require('scheduler/view/editor'),
        schedulerRouter = require('scheduler/router/app'),
        jobUtil = require('scheduler/util/jobUtil');

    domReady(function(){
        _.extend(ControlsBase, jrsConfigs.inputControlsConstants);
    });

    return Backbone.View.extend({

        REST_V2_BASE: '/jobs',
        // root element of app
        el: '#display',
		_runNowClick: false,
		handleTwoButtonDialogIsOpen: false,

        // initialize app
        initialize: function(){
            // initialize app views
            this.list = new jobsView({ app: this });
            this.editor = new editorView({ app: this });

			// check if we have an parent Report object
			// it may happen in case when we are the Report Version object
			if (document.location.hash.indexOf("@@parentReportURI=") !== -1) {
				var match = document.location.hash.match(/@@parentReportURI=([^@]*)@@/);
				this.list.parentReportURI = match[1];
				this.editor.parentReportURI = match[1];
			}

            // initialize app router
            this.router = new schedulerRouter(this);
            Backbone.history.start();

            // suppress http basic auth for all requests
            $.ajaxSetup({ headers: { 'X-Suppress-Basic': true } });

            // handle ajax errors and reload page if request unauthorized
            $(document).on('ajaxError', function(e, xhr, settings, exception){
                if (401 === xhr.status || 'Unauthorized' === exception) location.reload();
            });

            // fix for links which rewrites url hash. for example, such links as this one: <a href="#">About</a>
            $(document).on('click', 'a[href=#]', function(event){ event.preventDefault(); });

            // saving the URL from which we came - we need this url when user press Back button
            if (document.referrer.indexOf("login.html") === -1) {
                new JSCookie(jobUtil.SCHEDULER_BACK_COOKIE_NAME, encodeURIComponent(document.referrer));
                new JSCookie(jobUtil.SCHEDULER_LIST_BACK_COOKIE_NAME, encodeURIComponent(location.href));
            }
        },

        // show app page
        page: function(view){
            // track current page
            this.current = view;

            // show passed view page
            view.$el.removeClass('hidden').siblings().addClass('hidden');
        },

        handleTwoButtonDialog: function(message, OKButtonAction, CancelButtonAction) {
			if (this.handleTwoButtonDialogIsOpen === true) {
				return;
			}
            var confirmElement = $('#standardConfirm'), self = this;

            confirmElement.find('.body').html(message);

            dialogs.popupConfirm.show(confirmElement.get(0), false, {
                okButtonSelector: '#runReportOK',
                cancelButtonSelector: '#runReportCancel'
            });
			this.handleTwoButtonDialogIsOpen = true;

            confirmElement.find('#runReportOK').on('click', function(event){
				self.handleTwoButtonDialogIsOpen = false;
                OKButtonAction();
            });

            confirmElement.find('#runReportCancel').on('click', function(event){
				self.handleTwoButtonDialogIsOpen = false;
                if (CancelButtonAction) CancelButtonAction();
            })
        }

    });

});