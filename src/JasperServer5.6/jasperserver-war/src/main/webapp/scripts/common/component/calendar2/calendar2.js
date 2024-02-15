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
 * @author: Dmitriy Gorbenko, Kostia Tsaregradskyi
 * @version: $Id: calendar2.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

/*

    The Date or/and Time picker calendar with Relative Dates support (Calendar2)
    ================================================================

    In other words, this is the extension to the jQuery's datepicker calendar and datetimpicker plugin.

    Created for the Emerald 2 version, by Dmitriy Gorbenko, Kostia Tsaregradskyi.

    Depends on: jQuery (+1.5), jQuery UI,  Underscore.

    This module is AMD compliant.


    Running:
    ===================
    To run the Calendar2 calendar you need to call Calendar2.instance() function.
    It returns the Calendar2 instance.
    Here are params to .instance() function:
    * inputField: the element to attach to
    * calendarType: can be "datetime", or "date" or "time" -- specifies the Calendar2 instance type
    * jqueryPickerOptions: an object, which we can pass to jQuery's datepicker
    * disabled: boolean, allows to disable the calendar
    * relativeTime: boolean, default - false, flag indicating if we need to show relative time tab

*/

define(function(require) {
    "use strict";

    var jQuery = require("jquery"),
        _ = require("underscore"),
        RelativeDate = require("common/util/datetime/RelativeDate"),
        RelativeTime = require("common/util/datetime/RelativeTime"),
        RelativeTimestamp = require("common/util/datetime/RelativeTimestamp"),
        calendar2Template = require("text!calendar2Folder/template/calendar2Template.htm"),
        i18n = require("bundle!calendar");

    require("jquery.ui");
    require("jquery.datepicker.extensions");
    require("jquery.timepicker");

    var lodashTemplateSettings = {
        evaluate:/\{\{([\s\S]+?)\}\}/g,
        interpolate:/\{\{=([\s\S]+?)\}\}/g,
        escape:/\{\{-([\s\S]+?)\}\}/g
    };

    var capitalize = function(str){
        str = str == null ? '' : String(str);
        str = str.toLowerCase();
        return str.charAt(0).toUpperCase() + str.slice(1);
    };

    var compiledTemplate = _.template(calendar2Template, null, lodashTemplateSettings);

    // definition for the new version of date time picker with relative dates support
    var Calendar2 = {

        counter: 0, // instance counter
        activeMark: "activeCalendar2", // the constant to mark if some element has attached and opened calendar


        // the function can tell you if there is an open calendar on this instance
        hasActiveCalendar: function(elem) {
            return jQuery(elem).hasClass(this.activeMark);
        },


        // service function, returns the Calendar2 object which you can use after
        instance: function(config) {

            config = config || {};

            var root = this, // pointer to Calendar2 object itself
                state = "hidden", // just a flag
                uniqueId = "calendar2_" + (++this.counter);


            // calendar is the object which represents Date or/and Time picker with Relative Date support
            var calendar = {

                calContainer: false, // here we store the link to calendar's DOM tree
                tabs: [], // this is the array of links to tabs' DOM trees
                tabButtons: [], // the same but for tab controls
                rdFunction: undefined,


                // this is 'main' function, which starts the calendar.
                // this function called automatically
                build: function() {

                    // set values by default
                    config.calendarType = config.calendarType || "datetime";
                    config.disabled = config.disabled || false;
                    config.relativeTime = config.relativeTime || false;
                    config.inputField = jQuery(config.inputField);

                    if (config.calendarType == "date") {
                        calendar.rdFunction = RelativeDate;
                    } else if (config.calendarType == "datetime") {
                        calendar.rdFunction = RelativeTimestamp;
                    } else if (config.calendarType == "time") {
                        calendar.rdFunction = RelativeTime;
                    }

                    var rdWords = _.map(_.keys(calendar.rdFunction.PATTERNS), function(word) {
                        return {
                            label: capitalize(word) + "(s)",
                            value: word
                        }
                    });

                    config.inputField.addClass("hasCalendar2");

                    // get the HTML for the Calendar2
                    calendar.calContainer = jQuery(compiledTemplate({
                        uniqueId: uniqueId,
                        i18n: i18n,
                        rdWords: rdWords,
                        calendarType:  config.calendarType
                    }));

                    calendar.calContainer.appendTo("body");

                    jQuery("<button type='button' class='ui-datepicker-trigger button picker'></button>").insertAfter(config.inputField);

                    calendar.initTabs();
                    calendar.attachListeners();

                    // if user decided to disabled the calendar, then attach a special class to indicate this state
                    if (config.disabled) {
                        calendar.calContainer.addClass("disabled");
                    }

                    // init relative dates panel and Calendar as well
                    this.RD.init(calendar.rdFunction);
                    this.Calendar.init();
                },

                attachListeners: function() {
                    calendar.tabButtons.each(function(index, tab){
                        jQuery(tab).bind("click", function(){calendar.activateTab(index)});
                    });
                    config.inputField.next(".ui-datepicker-trigger").bind("click", calendar.show);
                    calendar.calContainer.find(".closeButton").bind("click", calendar.hide);
                    calendar.calContainer.find(".nowButton").bind("click", _.bind(calendar.Calendar.setCurrent, calendar.Calendar));
                    jQuery(document).bind("mousedown", calendar.checkExternalClick);
                },

                removeListeners: function() {
                    calendar.tabButtons.each(function(index, tab){
                        jQuery(tab).unbind("click", function(){calendar.activateTab(index)});
                    });
                    config.inputField.next(".ui-datepicker-trigger").unbind("click", calendar.show);
                    calendar.calContainer.find(".closeButton").unbind("click", calendar.hide);
                    calendar.calContainer.find(".nowButton").unbind("click", _.bind(calendar.Calendar.setCurrent, calendar.Calendar));
                    jQuery(document).unbind("mousedown", calendar.checkExternalClick);
                },

                // this function used to determine the moment when user clicked outside the calendar
                // If this happens, this is the indicator to close the calendar
                checkExternalClick: function(event) {
                    if (state == "shown" && jQuery(event.target).parents("#" + uniqueId).length === 0) {
                        calendar.hide();
                    }
                },

                initTabs: function() {
                    calendar.tabs = calendar.calContainer.find(".tabs > div");
                    calendar.tabButtons = calendar.calContainer.find(".tabsControl > .tabSelect");

                    if (config.calendarType === "time" && !config.relativeTime) {
                        calendar.tabs[1].remove();
                        calendar.tabButtons[1].remove();
                        calendar.calContainer.find(".tabsControl").hide();
                    }

                    calendar.activateTab(0);
                },

                // used to activate
                activateTab: function(index) {

                    // sanity check
                    if (index >= calendar.tabs.length) {
                        return;
                    }

                    calendar.tabButtons.removeClass("opened");
                    calendar.tabs.removeClass("opened");

                    jQuery(calendar.tabs[index]).addClass("opened");
                    jQuery(calendar.tabButtons[index]).addClass("opened");

                    calendar.calContainer.find(".nowButton")[index === 1 ? "hide" : "show"]();
                },

                destroy: function() {
                    config.inputField.removeClass("hasCalendar2");
                    calendar.removeListeners();
                    calendar.RD.destroy();
                    calendar.calContainer.remove();
                },

                show: function() {
                    config.inputField.attr("readonly", "readonly");

                    var inputValue = config.inputField.val(),
                        inputIsValidRD = calendar.rdFunction.isValid(inputValue);

                    calendar.activateTab(inputIsValidRD ? 1 : 0);

                    calendar.calContainer.show();
                    calendar.Calendar.create();

                    inputIsValidRD ? calendar.RD.setValue(inputValue) : calendar.Calendar.setValue(inputValue);

                    calendar.adjustPosition(); // place calendar relatively to the called element
                    state = "shown";

                    // 'attachTo' literally means not to attach to the DOM, but to keep relation with this element
                    var anchor = config.inputField;
                    if (anchor.length) {
                        anchor.addClass(root.activeMark);
                    }
                },

                adjustPosition: function() {
                    var anchor = config.inputField;

                    var off = anchor.offset();

                    var myw = calendar.calContainer.width(), myh = calendar.calContainer.height();
                    var $w = jQuery(window), ww = $w.width(), wh = $w.height();

                    if ((off.top + 25 + myh) <= wh || (off.top - myh - 5) < 0) {
                        // if we don't exceed window height or there is not enough space at the top of the input
                        // we put control at the bottom of input field
                        off.top += 25;
                    } else {
                        // in other case we put control at the top of input field
                        off.top = off.top - myh - 5;
                    }

                    if ((off.left + myw) > ww) {
                        off.left = Math.max(ww - (myw + 20), 50); // not closer than 50 pixels to the left of the screen
                    }

                    calendar.calContainer.offset(off);
                },

                // just hides the calendar from the screen
                hide: function() {
                    config.inputField.removeAttr("readonly");

                    calendar.calContainer.hide();
                    calendar.Calendar.destroy();
                    state = "hidden";

                    // remove the mark from the 'inputField' element
                    var anchor = config.inputField;
                    if (anchor.length) {
                        anchor.removeClass(root.activeMark);
                    }
                },

                // RD stands for Relative Dates
                RD: {

                    possibleValues: [], // possible values which user can select
                    holder: false, // just the container
                    date: {},

                    init: function(rdConstructor) {
                        this.date = new rdConstructor("", "+", "");
                        this.holder = calendar.calContainer.find(".relativeDates > .dates");
                        this.attachEventListeners();
                    },

                    attachEventListeners: function() {
                        this.holder.find(".measure select, .sign select").bind("change", this.onSelect);
                        this.holder.find(".amount input").bind("keyup", this.onSelect);
                    },

                    removeEventListeners: function() {
                        this.holder.find(".measure select, .sign select").unbind("change", this.onSelect);
                        this.holder.find(".amount input").unbind("keyup", this.onSelect);
                    },

                    onSelect: function() {
                        config.inputField.val(calendar.RD.getValue());
                        // Bug 38481 - Incorrect behavior for Date/Relative date dialog
                        // dialog should be closed only when user click "Close" button
                        // config.inputField.trigger("change");
                    },

                    destroy: function() {
                        this.removeEventListeners();
                    },

                    getValue: function() {
                        this.date.setKeyword(this.holder.find(".measure select").val());
                        this.date.setSign(this.holder.find(".sign select").val());
                        this.date.setNumber(this.holder.find(".amount input").val());

                        return this.date.toString();
                    },

                    setValue: function(rdValue) {
                        var rd = calendar.rdFunction.parse(rdValue);

                        if (rd) {
                            this.date = rd;
                        } else {
                            this.date.setKeyword("");
                            this.date.setSign("+");
                            this.date.setNumber("");
                        }

                        this.holder.find(".measure select").val(this.date.keyword);
                        this.holder.find(".sign select").val(this.date.sign);
                        this.holder.find(".amount input").val(this.date.number);
                    }
                },

                Calendar: {

                    jqueryCalendar: false,
                    jqueryCalendarConfig: {},
                    jqueryCalendarType: false,

                    init: function() {

                        this.jqueryCalendarType = "datetimepicker";
                        if (config.calendarType == "time") {
                            this.jqueryCalendarType = "timepicker";
                        } else if (config.calendarType == "date") {
                            this.jqueryCalendarType = "datepicker";
                        }

                        this.jqueryCalendarConfig = {
                            dateFormat: "yy-mm-dd",
                            timeFormat: "HH:mm:ss",
                            showHour: false,
                            showMinute: false,
                            showSecond: false,
                            showTime: false,
                            constrainInput: false,
                            showButtonPanel: false,
                            onSelect: function(dateStr, instance) {
                                config.inputField.val(dateStr);
                                config.inputField.trigger("change");
                            }
                        };

                        if (config.calendarType == "datetime" || config.calendarType == "date") {
                            this.jqueryCalendarConfig = _.extend(this.jqueryCalendarConfig, {
                                changeYear: true,
                                changeMonth: true
                            });
                        }
                        if (config.calendarType == "datetime" || config.calendarType == "time") {
                            this.jqueryCalendarConfig = _.extend(this.jqueryCalendarConfig, {
                                showTime: true,
                                showHour: true,
                                showMinute: true,
                                showSecond: true
                            });
                        }

                        // apply supplied by user config options to jquery's calendar
                        this.jqueryCalendarConfig = _.extend(this.jqueryCalendarConfig, config.jqueryPickerOptions || {});
                        // apply special 'disabled' state option
                        this.jqueryCalendarConfig.disabled = config.disabled;

                        // initialize the jquery's calendar
                        this.jqueryCalendar = calendar.calContainer.find("#" + uniqueId + "_calendar");
                    },

                    destroy: function() {
                        this.jqueryCalendar[this.jqueryCalendarType]("destroy");
                    },

                    create: function() {
                        this.jqueryCalendar[this.jqueryCalendarType](this.jqueryCalendarConfig);
                    },

                    getValue: function() {
                        return this.jqueryCalendar[this.jqueryCalendarType]("getDate");
                    },

                    setCurrent: function() {
                        var currentDate = new Date();

                        this.jqueryCalendar[this.jqueryCalendarType](config.calendarType === "time" ? "setTime" : "setDate", currentDate);
                        this.jqueryCalendar.find('.ui-datepicker-today').click();
                    },

                    setValue: function(date) {
                        if (config.calendarType == "time") {
                            try {
                                if (_.isString(date)) {
                                    date = jQuery.datepicker.parseTime(this.jqueryCalendarConfig.timeFormat, date);
                                    date && this.jqueryCalendar[this.jqueryCalendarType]("setTime", date);
                                } else {
                                    date = new Date(date.getTime());
                                    if (date.toString() !== 'Invalid Date') {
                                        this.jqueryCalendar[this.jqueryCalendarType]("setTime", date);
                                    }
                                }
                            }
                            catch(e) {}
                        } else {
                            if (!(date instanceof Date)) {
                                try {
                                    if (config.calendarType == "datetime") {
                                        date = jQuery.datepicker.parseDateTime(this.jqueryCalendarConfig.dateFormat, this.jqueryCalendarConfig.timeFormat, date);
                                    } else if (config.calendarType == "date") {
                                        date = jQuery.datepicker.parseDate(this.jqueryCalendarConfig.dateFormat, date);
                                    }
                                }
                                catch(e) {}
                            }

                            if (date instanceof Date && date.toString() !== 'Invalid Date') {
                                return this.jqueryCalendar[this.jqueryCalendarType]("setDate", date);
                            }
                        }
                    }
                }
            };


            // Ok, now, when the object is constructed, call the build function to initialize the Calendar2
            calendar.build();

            // and then, return this object
            return calendar;
        }
    };

    // expose module to global scope for modules that are not AMD-compliant
    window.Calendar2 = Calendar2;

    return Calendar2;
});