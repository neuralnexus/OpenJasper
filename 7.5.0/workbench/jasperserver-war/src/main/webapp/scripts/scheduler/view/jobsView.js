define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var i18n = require("bundle!all");

var Backbone = require('backbone');

var _utilUtilsCommon = require('../../util/utils.common');

var isSupportsTouch = _utilUtilsCommon.isSupportsTouch;
var isIPad = _utilUtilsCommon.isIPad;

var domUtil = require("runtime_dependencies/js-sdk/src/common/util/domUtil");

var TouchController = require('../../util/touch.controller');

var InfiniteScroll = require('../../util/tools.infiniteScroll');

var jobView = require('./jobView');

var jobsCollection = require('../collection/jobsCollection');

var jobsViewTemplate = require("text!../template/jobsViewTemplate.htm");

var masterViewTemplate = require("text!../template/masterViewTemplate.htm");

var listOfJobsTemplate = require("text!../template/list/listOfJobsTemplate.htm");

var nothingToDisplayTemplate = require("text!../template/list/nothingToDisplayTemplate.htm");

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
module.exports = Backbone.View.extend({
  // binded events for view
  events: {
    'click [name=backButton]': 'backButtonClick',
    'click [name=scheduleJob]': 'createButtonClick',
    'click [name=runJob]': 'runButtonClick',
    'click [name=refreshList]': 'refreshButtonClick',
    // search listeners
    'keydown [name=jobSearchInput]': 'jobSearchInputKeyDown',
    'click [name=jobSearchIcon]': 'jobSearchIconClick',
    'click [name=jobSearchClear]': 'clearJobSearch'
  },
  // initialize view
  initialize: function initialize(options) {
    this.options = _.extend({}, options);
    this.options = _.omit(this.options, 'model'); // create collection
    // create collection

    this.jobsViewCollection = new jobsCollection(null, this.options); // handle collection changes
    // handle collection changes

    this.listenTo(this.jobsViewCollection, 'add', this.createAndAddJobView);
    this.listenTo(this.jobsViewCollection, 'reset destroy', this.renderCollection);
  },
  // get jobs from the server
  refresh: function refresh() {
    this.jobsViewCollection.fetch();
  },
  searchJobs: function searchJobs() {
    var searchTerm = this.$el.find('[name=jobSearchInput]').val();

    if (!searchTerm) {
      return;
    }

    this.$el.find('[name=jobSearchClear]').addClass('up');
    this.jobsViewCollection.setSearchingTerm(searchTerm);
    this.jobsViewCollection.fetch();
  },
  clearJobSearch: function clearJobSearch() {
    this.$el.find('[name=jobSearchClear]').removeClass('up');
    this.$el.find('[name=jobSearchInput]').val('');
    this.jobsViewCollection.setSearchingTerm('');
    this.jobsViewCollection.fetch();
  },
  render: function render() {
    var template = this.options.masterViewMode ? masterViewTemplate : jobsViewTemplate;
    var templateData = {
      i18n: i18n,
      reportUri: this.options.reportUri,
      searchTerm: this.jobsViewCollection.getSearchingTerm()
    };
    this.setElement($(_.template(template, templateData)));
    this.renderCollection();
  },
  renderCollection: function renderCollection() {
    var jobsContainer = this.$el.find('[name=jobsContainer]').empty(); // render collection items
    // render collection items

    if (this.jobsViewCollection.length) {
      jobsContainer.append($(_.template(listOfJobsTemplate, {}))); // iterate over collection and add items
      // iterate over collection and add items

      this.jobsViewCollection.each(_.bind(this.createAndAddJobView, this)); // initialize the infinite scroll.
      // initialize the infinite scroll.

      this.initInfiniteScroll();
      this.adjustTableHeaderWidth();
    } else {
      jobsContainer.append($(_.template(nothingToDisplayTemplate, {
        message: this.options.masterViewMode ? i18n['report.scheduling.list.no.jobs'] : i18n['report.scheduling.list.no.jobs.for.report']
      })));
    }
  },
  initInfiniteScroll: function initInfiniteScroll() {
    var resultList = this.$el.find('#listOfJobs')[0];
    var resultsContainer = this.$el.find('#jobsSummaryContainer')[0];

    if (!this.options.masterViewMode || !resultList) {
      return;
    }

    if (isSupportsTouch()) {
      if (!this._touchController) {
        this._touchController = new TouchController(resultList, resultList.up(), {
          forceLayout: true
        });
      }

      this.listenTo(resultList, 'layout_update orientationchange', function () {
        $(resultList).css('min-width', $(resultsContainer).width() + 'px');
        resultList.width($(resultsContainer).width());
      });
    }

    if (isIPad()) {
      this._infiniteScroll = new InfiniteScroll({
        control: resultsContainer,
        content: resultList,
        scroll: this._touchController || undefined
      });
    } else {
      this._infiniteScroll = new InfiniteScroll({
        control: resultsContainer,
        content: resultList
      });
    }

    this._infiniteScroll.onLoad = _.bind(function () {
      this._infiniteScroll.wait();

      this.jobsViewCollection.loadMoreJobs();

      this._infiniteScroll.stopWaiting();
    }, this);
  },
  // yes, it's a CSS hack
  adjustTableHeaderWidth: function adjustTableHeaderWidth() {
    var header = this.$el.find('.content > .subheader .wrap');
    var table = this.$el.find('.content > .body');
    var hasScroll = domUtil.hasScrollBar(table[0], 'vertical');
    header.css('margin-right', (hasScroll ? 15 : 0) + 'px');
  },
  jobSearchInputKeyDown: function jobSearchInputKeyDown(event) {
    // on Esc key we'll clean the search area
    if (event.keyCode === 27) {
      // prevent other keyboard related logic to handle this Esc keypress
      event.preventDefault();
      event.stopPropagation();
      this.clearJobSearch();
    } // we expect Enter/Return key to run the search
    // we expect Enter/Return key to run the search


    if (event.keyCode === 13) {
      // prevent other keyboard related logic to handle this Esc keypress
      event.preventDefault();
      event.stopPropagation();
      this.searchJobs();
    }
  },
  jobSearchIconClick: function jobSearchIconClick() {
    this.searchJobs();
  },
  // add one element to collection
  createAndAddJobView: function createAndAddJobView(item) {
    var view = new jobView({
      model: item,
      masterViewMode: this.options.masterViewMode
    });
    this.listenTo(view, 'editJobPressed', this.editJobPressed);
    var element = view.render().$el;
    this.$el.find('[name=listOfJobs]').append(element);
  },
  editJobPressed: function editJobPressed(jobId) {
    this.trigger('editJobPressed', jobId);
  },
  // go back to previous location
  backButtonClick: function backButtonClick() {
    this.trigger('backButtonPressed');
  },
  // handle new job creation
  createButtonClick: function createButtonClick() {
    this.trigger('createNewJobRequest');
  },
  // create new job and run it immediately
  runButtonClick: function runButtonClick() {
    this.trigger('runNowRequest');
  },
  refreshButtonClick: function refreshButtonClick() {
    this.refresh();
  }
});

});