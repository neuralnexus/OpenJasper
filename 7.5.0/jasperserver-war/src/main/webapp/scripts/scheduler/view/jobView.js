define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

var templateJob = require("text!../template/list/oneJob.htm");

var templateMasterJob = require("text!../template/list/oneMasterJob.htm");

var i18n = require("bundle!all");

var xssUtil = require("runtime_dependencies/js-sdk/src/common/util/xssUtil");

var ConfirmationDialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/ConfirmationDialog");

var jobStateEnum = require('../enum/jobStateEnum');

var date = require("runtime_dependencies/js-sdk/src/common/util/parse/date");

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
var getLastRanDate = function getLastRanDate(model) {
  if (model.state.previousFireTime) {
    return date.isoTimestampToLocalizedTimestampByTimezone(model.state.previousFireTime);
  } else {
    return "";
  }
};

var getNextRunDate = function getNextRunDate(model) {
  var jobState = model.state.value;

  if (jobState !== jobStateEnum.NORMAL && jobState !== jobStateEnum.EXECUTING) {
    return i18n['report.scheduling.list.label.disabled'];
  } else if (model.state.nextFireTime) {
    return date.isoTimestampToLocalizedTimestampByTimezone(model.state.nextFireTime);
  } else {
    return "";
  }
};

var isJobEnabled = function isJobEnabled(model) {
  var jobState = model.state.value;
  return jobState === jobStateEnum.NORMAL || jobState === jobStateEnum.EXECUTING;
};

module.exports = Backbone.View.extend({
  // view element tagName
  tagName: 'li',
  className: 'jobs first leaf',
  // bind events for view
  events: {
    'click [name=editJob]': 'edit',
    'click [name=deleteJob]': 'remove',
    'change [name=enableJob]': 'enable'
  },
  // initialize view
  initialize: function initialize(options) {
    this.options = _.extend({}, options);
    var template = this.options.masterViewMode ? templateMasterJob : templateJob; // create template
    // create template

    this.template = _.template(template); // handle model changes
    // handle model changes

    this.model.on('change', this.render, this);
  },
  // render view
  render: function render() {
    var model = this.model.toJSON();
    this.$el.html(this.template({
      model: model,
      i18n: i18n,
      lastRanDate: getLastRanDate(model),
      nextRunDate: getNextRunDate(model),
      isJobEnabled: isJobEnabled(model)
    }));
    return this;
  },
  // edit job
  edit: function edit() {
    this.trigger('editJobPressed', this.model.get('id'));
  },
  // remove job
  remove: function remove() {
    var self = this;
    var text = i18n['report.scheduling.editing.job.confirm.delete'].replace('{name}', xssUtil.hardEscape(this.model.get('label'))).replace('{newline}', '<br><br>');
    var dialog = new ConfirmationDialog({
      title: i18n['report.scheduling.editing.job.confirm.title'],
      text: text,
      additionalCssClasses: 'schedulerJobRemoveDialog'
    });
    this.listenTo(dialog, 'button:yes', function () {
      self.model.destroy();
    });
    dialog.open();
  },
  // enable/disable job
  enable: function enable(event) {
    this.model.state(event.target.checked);
  }
});

});