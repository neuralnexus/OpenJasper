define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var i18n = require("bundle!all");

var Backbone = require('backbone');

var domReady = require('requirejs-domready');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var schedulerUtils = require('../util/schedulerUtils');

var jobModel = require('../model/jobModel');

var jobsView = require('./jobsView');

var jobEditorView = require('./jobEditorView');

var _controlsControlsBase = require("../../controls/controls.base");

var ControlsBase = _controlsControlsBase.ControlsBase;

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
domReady(function () {
  _.extend(ControlsBase, jrsConfigs.inputControlsConstants);
});
module.exports = Backbone.View.extend({
  className: 'schedulerApp',
  // initial state of the views
  jobsView: false,
  // initialize app
  initialize: function initialize(options) {
    this.options = _.extend({}, options); // check if we have a mark which says "simply create a job to execute immediately and exit"
    // check if we have a mark which says "simply create a job to execute immediately and exit"

    this.runInBackgroundMode = document.location.hash.indexOf('#runInBackground@') === 0; // saving the URL from which we came - we need this url when user would like to get back
    // saving the URL from which we came - we need this url when user would like to get back

    schedulerUtils.saveCurrentLocation(); // not, get our parameters from URL
    // not, get our parameters from URL

    this.schedulerStartupParams = schedulerUtils.getParamsFromUri(); // Master View Mode (MVM) is a mode then we only able to see the list of scheduled jobs for all
    // reports. In this mode we can't create new jobs, but we can control them: stop, continue, remove.
    // Master View Mode (MVM) is a mode then we only able to see the list of scheduled jobs for all
    // reports. In this mode we can't create new jobs, but we can control them: stop, continue, remove.

    this.masterViewMode = !this.schedulerStartupParams['reportUnitURI']; // we have two child views: job editor and list of jobs views.
    // let's prepare the object to initialize them later
    // we have two child views: job editor and list of jobs views.
    // let's prepare the object to initialize them later

    this.childViewInitParams = {
      model: new jobModel(),
      runInBackgroundMode: this.runInBackgroundMode,
      masterViewMode: this.masterViewMode,
      reportUri: this.schedulerStartupParams['reportUnitURI'],
      parentReportURI: this.schedulerStartupParams['parentReportURI'] || null
    }; // suppress http basic auth for all requests
    // suppress http basic auth for all requests

    $.ajaxSetup({
      headers: {
        'X-Suppress-Basic': true
      }
    }); // handle ajax errors and reload page if request unauthorized
    // handle ajax errors and reload page if request unauthorized

    $(document).on('ajaxError', function (e, xhr, settings, exception) {
      if (401 === xhr.status || 'Unauthorized' === exception) {
        location.reload();
      }
    }); // by default we open list of jobs unless we are in the 'runInBackground' mode
    // by default we open list of jobs unless we are in the 'runInBackground' mode

    if (this.runInBackgroundMode) {
      this.runNowRequest();
    } else {
      this.openJobsListInterface();
    }
  },
  prepareJobsView: function prepareJobsView() {
    if (this.jobsView) {
      return;
    }

    this.jobsView = new jobsView(this.childViewInitParams);
    this.listenTo(this.jobsView, 'createNewJobRequest', this.createNewJobRequest);
    this.listenTo(this.jobsView, 'runNowRequest', this.runNowRequest);
    this.listenTo(this.jobsView, 'backButtonPressed', this.backButtonPressed);
    this.listenTo(this.jobsView, 'editJobPressed', this.openEditJobInterface);
  },
  prepareJobEditorView: function prepareJobEditorView() {
    if (this.jobEditorView) {
      this.jobEditorView.remove();
    }

    this.jobEditorView = new jobEditorView(this.childViewInitParams);
    this.listenTo(this.jobEditorView, 'errorEditingJob', this.errorEditingJob);
    this.listenTo(this.jobEditorView, 'cancelJobCreation', this.cancelJobCreation);
    this.listenTo(this.jobEditorView, 'jobHasBeenCreated', this.jobHasBeenCreated);
  },
  //=======================================================================
  // next go methods which are responsive for some actions, like show list of jobs or create a new job
  openJobsListInterface: function openJobsListInterface() {
    // prepare a view which we need for this action
    this.prepareJobsView(); // empty container of the application
    // empty container of the application

    this.$el.empty(); // render the view
    // render the view

    this.jobsView.render(); // append view into application container
    // append view into application container

    this.$el.append(this.jobsView.$el);
    this.jobsView.refresh();
    document.title = i18n['company.name'] + ': ' + i18n['report.scheduling.list.title'];
  },
  createNewJobRequest: function createNewJobRequest() {
    this._openNewJobInterface(false);
  },
  runNowRequest: function runNowRequest() {
    this._openNewJobInterface(true);
  },
  editJob: function editJob(jobId) {
    this.openEditJobInterface(jobId);
  },
  // internal method, which not be called directly
  _openNewJobInterface: function _openNewJobInterface(runMode) {
    // prepare a view which we need for this action
    this.prepareJobEditorView(); // adjust the "run mode" of the view
    // adjust the "run mode" of the view

    this.jobEditorView.setRunNowMode(runMode); // empty container of the application
    // empty container of the application

    this.$el.empty(); // render the view
    // render the view

    this.jobEditorView.renderCreateNewJobInterface(); // append view into application container
    // append view into application container

    this.$el.append(this.jobEditorView.$el); // now, prepare the model to represent the new job interface
    // now, prepare the model to represent the new job interface

    this.jobEditorView.prepareModelForCreatingNewJob();
    document.title = i18n['company.name'] + ': ' + i18n['report.scheduling.job.edit.title'];
  },
  openEditJobInterface: function openEditJobInterface(jobId) {
    // prepare a view which we need for this action
    this.prepareJobEditorView(); // empty container of the application
    // empty container of the application

    this.$el.empty(); // render the view
    // render the view

    this.jobEditorView.editExistingJob(jobId); // append view into application container
    // append view into application container

    this.$el.append(this.jobEditorView.$el);
    document.title = i18n['company.name'] + ': ' + i18n['report.scheduling.job.edit.title'];
  },
  backButtonPressed: function backButtonPressed() {
    schedulerUtils.getBackToPreviousLocation();
  },
  errorEditingJob: function errorEditingJob() {
    this.openJobsListInterface();
  },
  cancelJobCreation: function cancelJobCreation() {
    if (this.runInBackgroundMode) {
      // in 'runInBackground' mode we have to get back to previous page
      schedulerUtils.getBackToPreviousLocation();
    } else {
      // in other case, we have to show the list of jobs
      this.openJobsListInterface();
    }
  },
  jobHasBeenCreated: function jobHasBeenCreated() {
    if (this.runInBackgroundMode) {
      // in 'runInBackground' mode we have to get back to previous page
      schedulerUtils.getBackToPreviousLocation();
    } else {
      // in other case, we have to show the list of jobs
      this.openJobsListInterface();
    }
  }
});

});