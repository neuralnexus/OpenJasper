define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var i18n = require("bundle!all");

var config = require("runtime_dependencies/js-sdk/src/jrs.configs");

var Backbone = require('backbone');

var scheduleTabTemplate = require("text!../../template/editor/scheduleTabTemplate.htm");

var DateAndTimePicker = require("runtime_dependencies/js-sdk/src/components/dateAndTime/DateAndTimePicker");

var holydayCalView = require('./holidayCalView');

var holidayCalsCollection = require('../../collection/holidayCalsCollection');

var moment = require("momentExtension");

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
var jqueryDateFormat = config.calendar.timepicker.dateFormat;
var jqueryTimeFormat = config.calendar.timepicker.timeFormat.replace(':ss', ''); // in Scheduler we don't use "seconds", so momentjs should not take seconds into consideration

module.exports = Backbone.View.extend({
  // view events
  events: {
    'change [name=startType]': 'setStartType',
    'change [name=startDate]': 'setStartDate',
    'change [name=schedulerTimeZone]': 'setTimeZone',
    'change [name=recurrenceType]': 'setRecurrenceType',
    'change [name=recurrenceInterval]': 'setRecurrenceInterval',
    'change [name=recurrenceIntervalUnit]': 'setRecurrenceIntervalUnit',
    'change [name=endat]': 'setEnDat',
    'change [name=occurrenceCount]': 'setOccurrenceCount',
    'change [name=simpleEndDate]': 'setEndDate',
    'change [name=calendarEndDate]': 'setEndDate',
    'change [name=whichMonth]': 'monthRadioSelector',
    'change [name=monthSelector]': 'setMonthSelector',
    'change [name=whichDay]': 'dayRadioSelector',
    'change [name=daySelector]': 'setDaySelector',
    'change [name=datesInMonth]': 'setDatesInMonth',
    'change [name=hours]': 'setHours',
    'change [name=minutes]': 'setMinutes',
    'change [name=calendarSelect]': 'setCalendar'
  },
  // initialize view
  initialize: function initialize() {
    // Some calendars which are not used anymore
    this.calCollection = new holidayCalsCollection();
    this.calView = new holydayCalView({
      collection: this.calCollection
    });
    this.calCollection.fetch({
      reset: true,
      data: {
        calendarType: 'holiday'
      }
    }); // getter for trigger section
    // getter for trigger section

    this.listenTo(this.model, 'change:trigger', this.triggerChanged);
  },
  render: function render() {
    this.setElement($(_.template(scheduleTabTemplate, {
      _: _,
      i18n: i18n,
      timeZones: config.timeZones
    })));
    this.$el.find('[name=calendarBlockHolder]').append(this.calView.$el);
    this.setupDatepickersOn();
  },
  setupDatepickersOn: function setupDatepickersOn() {
    var self = this;
    this.$el.find(".datepicker").each(function (index, calendar) {
      var $calendar = $(calendar);
      new DateAndTimePicker({
        el: $calendar[0],
        constrainInput: true,
        dateFormat: jqueryDateFormat,
        timeFormat: jqueryTimeFormat,
        showOn: "button",
        buttonText: ""
      });
      $calendar.next().addClass('button').addClass('picker');
      self.listenTo(self.model, "change:trigger", function () {
        var appTimeZone = moment.tz(this.model.get("trigger").timezone).utcOffset();
        $.datepicker._getInst($(calendar)[0]).settings.timepicker.timezone = appTimeZone;
      }); // Prototype.js compatibility

      $(calendar)[0].getValue = function () {
        return $(this).val();
      };
    });
  },
  triggerChanged: function triggerChanged() {
    // get trigger value
    var trigger = this.model.get('trigger'); // set startType
    // set startType

    this.$el.find('[name=startType]').filter('[value=' + trigger.startType + ']').prop('checked', true); // set startDate
    // set startDate

    this.$el.find('[name=startDate]').val(trigger.startDate); // set timezone
    // set timezone

    this.$el.find('[name=schedulerTimeZone]').val(trigger.timezone); // set recurrenceType
    // set recurrenceType

    this.$el.find('[name=recurrenceType]').val(trigger.type);
    this.$el.find('fieldset[data-recurrence]').addClass('hidden');
    this.$el.find('fieldset[data-recurrence="' + trigger.type + '"]').removeClass('hidden');

    if (trigger.type === 'none') {
      this.$el.find('[name=calendarBlockHolder]').addClass('hidden');
    }

    if (trigger.type === 'simple') {
      // set recurrenceInterval
      this.$el.find('[name=recurrenceInterval]').val(trigger.recurrenceInterval); // set recurrenceIntervalUnit
      // set recurrenceIntervalUnit

      this.$el.find('[name=recurrenceIntervalUnit]').val(trigger.recurrenceIntervalUnit); // set repeat logic
      // set repeat logic

      this.$el.find('[name=endat]').filter('[value=' + trigger.radioEndDate + ']').prop('checked', true);
      this.$el.find('[name=occurrenceCount]').val(trigger.occurrenceCount);
      this.$el.find('[name=simpleEndDate]').val(trigger.endDate);
      this.$el.find('[name=calendarBlockHolder]').removeClass('hidden');
      this.$el.find('[name=calendarSelect]').val(trigger.calendarName);
    }

    if (trigger.type === 'calendar') {
      // set calendar months
      this.$el.find('[name=whichMonth]').filter('[value=' + trigger.radioWhichMonth + ']').prop('checked', true);
      this.$el.find('[name=monthSelector]').val(trigger.months.month); // set calendar days
      // set calendar days

      this.$el.find('[name=whichDay]').filter('[value=' + trigger.radioWhichDay + ']').prop('checked', true);
      this.$el.find('[name=daySelector]').val(trigger.weekDays.day);
      this.$el.find('[name=datesInMonth]').val(trigger.monthDays);
      this.$el.find('[name=hours]').val(trigger.hours);
      this.$el.find('[name=minutes]').val(trigger.minutes);
      this.$el.find('[name=calendarEndDate]').val(trigger.endDate);
      this.$el.find('[name=calendarBlockHolder]').removeClass('hidden');
      this.$el.find('[name=calendarSelect]').val(trigger.calendarName);
    }
  },
  // trigger section changed
  setStartType: function setStartType(event) {
    this.model.update('trigger', {
      startType: $(event.target).val()
    });
  },
  // setter for startDate
  setStartDate: function setStartDate(event) {
    this.model.update('trigger', {
      startType: '2',
      startDate: $(event.target).val()
    });
  },
  // setter for timezone
  setTimeZone: function setTimeZone(event) {
    this.model.update('trigger', {
      timezone: $(event.target).val()
    });
  },
  // setter for recurrence type
  setRecurrenceType: function setRecurrenceType(event) {
    this.model.update('trigger', {
      type: $(event.target).val()
    });
  },
  // setter for recurrence interval
  setRecurrenceInterval: function setRecurrenceInterval(event) {
    this.model.update('trigger', {
      recurrenceInterval: $(event.target).val()
    });
  },
  // setter for recurrence interval units
  setRecurrenceIntervalUnit: function setRecurrenceIntervalUnit(event) {
    this.model.update('trigger', {
      recurrenceIntervalUnit: $(event.target).val()
    });
  },
  // setter for repeat logic
  setEnDat: function setEnDat(event) {
    this.model.update('trigger', {
      radioEndDate: $(event.target).val()
    });
  },
  // setter for occurrenceCount
  setOccurrenceCount: function setOccurrenceCount(event) {
    this.model.update('trigger', {
      occurrenceCount: $(event.target).val()
    });
  },
  // setter for end date
  setEndDate: function setEndDate(event) {
    this.model.update('trigger', {
      endDate: $(event.target).val()
    });
  },
  // set month in calendar view
  monthRadioSelector: function monthRadioSelector(event) {
    this.model.update('trigger', {
      radioWhichMonth: $(event.target).val()
    });
  },
  // set month selector
  setMonthSelector: function setMonthSelector(event) {
    var value = $(event.target).val() || [];

    for (var i = 0, l = value.length; i < l; i++) {
      value[i] = parseInt(value[i]);
    }

    this.model.update('trigger', {
      months: {
        month: value
      }
    });
  },
  // set which day
  dayRadioSelector: function dayRadioSelector(event) {
    this.model.update('trigger', {
      radioWhichDay: $(event.target).val()
    });
  },
  // set day selector
  setDaySelector: function setDaySelector(event) {
    var value = $(event.target).val() || [];

    for (var i = 0, l = value.length; i < l; i++) {
      value[i] = parseInt(value[i]);
    }

    this.model.update('trigger', {
      weekDays: {
        day: value
      }
    });
  },
  setDatesInMonth: function setDatesInMonth(event) {
    this.model.update('trigger', {
      monthDays: $(event.target).val()
    });
  },
  setHours: function setHours(event) {
    this.model.update('trigger', {
      hours: $(event.target).val()
    });
  },
  setMinutes: function setMinutes(event) {
    this.model.update('trigger', {
      minutes: $(event.target).val()
    });
  },
  setCalendar: function setCalendar(event) {
    this.model.update('trigger', {
      calendarName: $(event.target).val()
    });
  }
});

});