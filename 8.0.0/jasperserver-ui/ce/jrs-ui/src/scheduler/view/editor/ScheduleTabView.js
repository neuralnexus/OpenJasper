/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

import $ from 'jquery';
import _ from 'underscore';
import i18n from '../../../i18n/all.properties';
import config from 'js-sdk/src/jrs.configs';
import Backbone from 'backbone';
import DateAndTimePicker from 'js-sdk/src/components/dateAndTime/DateAndTimePicker';
import HolydayCalView from './HolidayCalView';
import HolidayCalsCollection from '../../collection/HolidayCalsCollection';
import moment from 'moment';

import scheduleTabTemplate from '../../template/editor/scheduleTabTemplate.htm';

const jqueryDateFormat = config.calendar.timepicker.dateFormat;
const jqueryTimeFormat = config.calendar.timepicker.timeFormat.replace(':ss', '');

// in Scheduler we don't use "seconds", so momentjs should not take seconds into consideration
export default Backbone.View.extend({
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
    initialize: function () {
        // Some calendars which are not used anymore
        this.calCollection = new HolidayCalsCollection();
        this.calView = new HolydayCalView({ collection: this.calCollection });

        this.calCollection.fetch({
            reset: true,
            data: { calendarType: 'holiday' }
        });

        this.listenTo(this.model, 'change:trigger', this.triggerChanged);
        this.listenTo(this.calCollection, 'reset change', this.calendarChanged);
    },

    render: function () {
        this.setElement($(_.template(scheduleTabTemplate, {
            _: _,
            i18n: i18n,
            timeZones: config.timeZones
        })));
        this.$el.find('[name=calendarBlockHolder]').append(this.calView.$el);
        this.setupDatepickersOn();
    },

    setupDatepickersOn: function () {
        this.$el.find(".datepicker").each((index, calendar) => {

            const $calendar = $(calendar);

            new DateAndTimePicker({
                el: $calendar[0],
                constrainInput: true,
                dateFormat: jqueryDateFormat,
                timeFormat: jqueryTimeFormat,
                showOn: "button",
                buttonText: ""
            });

            $calendar.next().addClass('button').addClass('picker');

            this.listenTo(this.model, "change:trigger", () => {
                const appTimeZone = moment.tz(this.model.get("trigger").timezone).utcOffset();
                $.datepicker._getInst($(calendar)[0]).settings.timepicker.timezone = appTimeZone;
            });

            // Prototype.js compatibility
            $(calendar)[0].getValue = function () {
                return $(this).val();
            };
        });
    },

    triggerChanged: function () {
        // get trigger value
        const trigger = this.model.get('trigger');
        // set startType
        this.$el.find('[name=startType]').filter('[value=' + trigger.startType + ']').prop('checked', true);
        // set startDate
        this.$el.find('[name=startDate]').val(trigger.startDate);
        // set timezone
        this.$el.find('[name=schedulerTimeZone]').val(trigger.timezone);
        // set recurrenceType
        this.$el.find('[name=recurrenceType]').val(trigger.type);
        this.$el.find('fieldset[data-recurrence]').addClass('hidden');
        this.$el.find('fieldset[data-recurrence="' + trigger.type + '"]').removeClass('hidden');
        if (trigger.type === 'none') {
            this.$el.find('[name=calendarBlockHolder]').addClass('hidden');
        }
        if (trigger.type === 'simple') {
            // set recurrenceInterval
            this.$el.find('[name=recurrenceInterval]').val(trigger.recurrenceInterval);
            // set recurrenceIntervalUnit
            this.$el.find('[name=recurrenceIntervalUnit]').val(trigger.recurrenceIntervalUnit);
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
            this.$el.find('[name=monthSelector]').val(trigger.months.month);
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

    calendarChanged: function () {
        // calendar might be changed (and re-rendered by 'calView'), and in such case we'd like to
        // update the current selected value for calendar
        const trigger = this.model.get('trigger');
        if (!trigger) {
            // since this event might occur before the model and the trigger is available,
            // we just need to exit because there is nothing to do right now.
            return;
        }
        this.$el.find('[name=calendarSelect]').val(trigger.calendarName);
    },

    // trigger section changed
    setStartType: function (event) {
        this.model.update('trigger', { startType: $(event.target).val() });
    },

    // setter for startDate
    setStartDate: function (event) {
        this.model.update('trigger', {
            startType: '2',
            startDate: $(event.target).val()
        });
    },

    // setter for timezone
    setTimeZone: function (event) {
        this.model.update('trigger', { timezone: $(event.target).val() });
    },

    // setter for recurrence type
    setRecurrenceType: function (event) {
        this.model.update('trigger', { type: $(event.target).val() });
    },

    // setter for recurrence interval
    setRecurrenceInterval: function (event) {
        this.model.update('trigger', { recurrenceInterval: $(event.target).val() });
    },

    // setter for recurrence interval units
    setRecurrenceIntervalUnit: function (event) {
        this.model.update('trigger', { recurrenceIntervalUnit: $(event.target).val() });
    },

    // setter for repeat logic
    setEnDat: function (event) {
        this.model.update('trigger', { radioEndDate: $(event.target).val() });
    },

    // setter for occurrenceCount
    setOccurrenceCount: function (event) {
        this.model.update('trigger', { occurrenceCount: $(event.target).val() });
    },

    // setter for end date
    setEndDate: function (event) {
        this.model.update('trigger', { endDate: $(event.target).val() });
    },

    // set month in calendar view
    monthRadioSelector: function (event) {
        this.model.update('trigger', { radioWhichMonth: $(event.target).val() });
    },

    // set month selector
    setMonthSelector: function (event) {
        const value = $(event.target).val() || [];
        for (let i = 0, l = value.length; i < l; i++)
            value[i] = parseInt(value[i]);
        this.model.update('trigger', { months: { month: value } });
    },

    // set which day
    dayRadioSelector: function (event) {
        this.model.update('trigger', { radioWhichDay: $(event.target).val() });
    },

    // set day selector
    setDaySelector: function (event) {
        const value = $(event.target).val() || [];
        for (let i = 0, l = value.length; i < l; i++)
            value[i] = parseInt(value[i]);
        this.model.update('trigger', { weekDays: { day: value } });
    },

    setDatesInMonth: function (event) {
        this.model.update('trigger', { monthDays: $(event.target).val() });
    },

    setHours: function (event) {
        this.model.update('trigger', { hours: $(event.target).val() });
    },

    setMinutes: function (event) {
        this.model.update('trigger', { minutes: $(event.target).val() });
    },

    setCalendar: function (event) {
        this.model.update('trigger', { calendarName: $(event.target).val() });
    }
});
