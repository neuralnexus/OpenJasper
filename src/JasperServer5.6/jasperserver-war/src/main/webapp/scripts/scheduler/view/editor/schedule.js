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
 * @version: $Id: schedule.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/view/editor/schedule', function(require){

	var Backbone = require('backbone'),
		$ = require('jquery'),
		config = require('jrs.configs'),
		timepicker = require('jquery.timepicker'),
		datepicker = require('jquery.datepicker.extensions'),
		holydayCalView = require('scheduler/view/editor/holidayCal'),
		holidayCalsCollection = require('scheduler/collection/holidayCals');

	var setupDatepickersOn = function (selector){
		$(selector).each(function (index, selector) {
			$(selector).datetimepicker({
				dateFormat: config.calendar.timepicker.dateFormat,
				timeFormat: "HH:mm",
				showOn: "button",
				buttonText: "",
				changeYear: true,
				changeMonth: true,
				showButtonPanel: true,
				showSecond: false,
				onChangeMonthYear: null,
				beforeShow: $.datepicker.movePickerRelativelyToTriggerIcon
			}).next().addClass('button').addClass('picker');

			// Prototype.js compatibility
			$(selector)[0].getValue = function () {
				return $(this).val()
			};
		});
	};

	return Backbone.View.extend({

		// view tab
		el: '#scheduler_editor .tab[data-tab=schedule]',

		// view events
		events: {
			'change [name=startType]': 'setStartType',
			'change [name=startDate]': 'setStartDate',
			'change [name=schedulerTimeZone]': 'setTimeZone',
            'change [name=recurrenceType]': 'recurrenceType',
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
            "change [name=calendarSelect]": "setCalendar"
		},

		// initialize view
		initialize: function(){
			// save context
			var t = this;

			t.calCollection = new holidayCalsCollection();
			t.calView = new holydayCalView({ collection: t.calCollection });

			// setup datepicker
			setupDatepickersOn('.datepicker');

            this.calCollection.fetch( { reset: true, data:{calendarType:'holiday'} });

			// getter for trigger section
			t.model.on('change:trigger', function(){

				// get trigger value
				var trigger = t.model.get('trigger');

				// set startType
				t.$('[name=startType]').filter('[value=' + trigger.startType + ']').prop('checked', true);

				// set startDate
				t.$('[name=startDate]').val(trigger.startDate);

				// set timezone
				t.$('[name=schedulerTimeZone]').val(trigger.timezone);

				// set recurrenceType
                t.$('[name=recurrenceType]').val(trigger.type);
                t.$('fieldset[data-recurrence]').addClass('hidden');
                t.$('fieldset[data-recurrence="' + trigger.type + '"]').removeClass('hidden');

                if (trigger.type === "none") {
                    t.$(".calendarBlock").addClass("hidden");
                }

                if (trigger.type === "simple") {
                    // set recurrenceInterval
                    t.$('[name=recurrenceInterval]').val(trigger.recurrenceInterval);

                    // set recurrenceIntervalUnit
                    t.$('[name=recurrenceIntervalUnit]').val(trigger.recurrenceIntervalUnit);

                    // set repeat logic
                    t.$('[name=endat]').filter('[value=' + trigger.radioEndDate +']').prop('checked', true);
                    t.$('[name=occurrenceCount]').val(trigger.occurrenceCount);
                    t.$('[name=simpleEndDate]').val(trigger.endDate);

                    t.$(".calendarBlock").removeClass("hidden");
                    t.$('[name=calendarSelect]').val(trigger.calendarName);
                }

                if (trigger.type === "calendar") {
                    // set calendar months
                    t.$('[name=whichMonth]').filter('[value=' + trigger.radioWhichMonth +']').prop('checked', true);
                    t.$('[name=monthSelector]').val(trigger.months.month);

                    // set calendar days
                    t.$('[name=whichDay]').filter('[value=' + trigger.radioWhichDay +']').prop('checked', true);
                    t.$('[name=daySelector]').val(trigger.weekDays.day);
                    t.$('[name=datesInMonth]').val(trigger.monthDays);


                    t.$('[name=hours]').val(trigger.hours);
                    t.$('[name=minutes]').val(trigger.minutes);

                    t.$('[name=calendarEndDate]').val(trigger.endDate);

                    t.$(".calendarBlock").removeClass("hidden");
                    t.$('[name=calendarSelect]').val(trigger.calendarName);
                }
			});
		},

        // trigger section changed
        setStartType: function(event){
            this.model.update('trigger', {startType: $(event.target).val()});
        },

        // setter for startDate
        setStartDate: function(event){
            this.model.update('trigger', {
                startType: "2",
                startDate: $(event.target).val()
            });
        },

        // setter for timezone
        setTimeZone: function(event){
            this.model.update('trigger', {timezone: $(event.target).val()});
        },

        // setter for recurrence type
        recurrenceType: function(event){
            this.model.update('trigger', {type: $(event.target).val()});
        },

        // setter for recurrence interval
        setRecurrenceInterval : function(event){
            this.model.update('trigger', {recurrenceInterval: $(event.target).val()});
        },

        // setter for recurrence interval units
        setRecurrenceIntervalUnit: function(event){
            this.model.update('trigger', {recurrenceIntervalUnit: $(event.target).val()});
        },

        // setter for repeat logic
        setEnDat: function(event){
            this.model.update('trigger', {radioEndDate: $(event.target).val()});
        },

        // setter for occurrenceCount
        setOccurrenceCount: function(event){
            this.model.update('trigger', {occurrenceCount: $(event.target).val()});
        },

        // setter for end date
        setEndDate: function(event){
            this.model.update('trigger', {endDate: $(event.target).val()});
        },


        // set month in calendar view
        monthRadioSelector: function(event){
            this.model.update('trigger', {radioWhichMonth: $(event.target).val()});
        },

        // set month selector
        setMonthSelector: function(event){
            var value = $(event.target).val() || [];

            for(var i=0, l=value.length; i<l; i++)
                value[i] = parseInt(value[i]);

            this.model.update('trigger', {
                months: { month: value }
            });
        },

        // set which day
        dayRadioSelector: function(event){
            this.model.update('trigger', {radioWhichDay: $(event.target).val()});
        },

        // set day selector
        setDaySelector: function(event){
            var value = $(event.target).val() || [];

            for(var i=0, l=value.length; i<l; i++)
                value[i] = parseInt(value[i]);

            this.model.update('trigger', {
                weekDays: { day: value }
            });
        },

        setDatesInMonth: function(event){
            this.model.update('trigger', {monthDays: $(event.target).val()});
        },

        setHours: function(event){
            this.model.update('trigger', {hours: $(event.target).val()});
        },

        setMinutes: function(event){
            this.model.update('trigger', {minutes: $(event.target).val()});
        },

        setCalendar: function(event){
            this.model.update('trigger', {calendarName: $(event.target).val()});
        }
	});

});