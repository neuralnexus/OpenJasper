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

define('scheduler/model/job', function (require) {

    var EMAIL_SEPARATOR = ",";
    var SERVICE_DATE_PATTERN = "YYYY-MM-DD HH:mm";
    var UI_DATE_PATTERN = "YYYY-MM-DD HH:mm";
    var ftpIsProcessings = false;

    var $ = require('jquery'),
        Backbone = require('backbone'),
        timepicker = require('jquery.timepicker'),
        datepicker = require('jquery.datepicker.extensions'),
        config = require('jrs.configs'),
        moment = require("moment");

    return Backbone.Model.extend({

        // url for fetching collection
        urlRoot: config.contextPath + '/rest_v2/jobs',

        isValid: function (editMode) {
            return !this.validate(this.attributes, {validate: true, editMode: editMode}).length;
        },

        validate: function (model, options) {
            options = options || {};

            if (options.validate !== true) return;

            var results = [], tmp;

            // tab #1
            if (model.trigger) {

                if (model.trigger.startType == 2) {
                    if (!model.trigger.startDate) {
                        results.push({
                            field: 'startDate',
                            errorCode: 'report.scheduling.job.edit.specify.startdate'
                        });
                    } else {
                        // check if the date is valid
                        if (!(moment(model.trigger.startDate, UI_DATE_PATTERN, true).isValid())) {
                            results.push({
                                field: 'startDate',
                                errorCode: 'error.invalid.date'
                            });
                        // check if date is in the past
                        } else if (!options.editMode && this.ifPastDate(model.trigger.startDate, model.trigger.timezone)) {
                            results.push({
                                field: 'startDate',
                                errorCode: 'error.before.current.date.trigger.startDate'
                            });
                        }
                    }
                }


                if (model.trigger.type === "simple") {
                    if (!this.isNumeric(model.trigger.recurrenceInterval)) {
                        results.push({
                            field: 'recurrenceInterval',
                            errorCode: 'report.scheduling.job.edit.specify.recurrenceinterval'
                        });
                    } else {
                        // now, check this value against max and min values
                        if (!this.isNumeric(model.trigger.recurrenceInterval, {minValue: -2147483648, maxValue: 2147483647})) {
                            results.push({
                                field: 'recurrenceInterval',
                                errorCode: 'typeMismatch.java.lang.Integer'
                            });
                        }
                    }

                    if (model.trigger.radioEndDate === "numberOfTimes") {
                        if (!this.isNumeric(model.trigger.occurrenceCount)) {
                            results.push({
                                field: 'occurrenceCount',
                                errorCode: 'report.scheduling.job.edit.specify.numberoftimestorunreport'
                            });
                        } else {
                            // now, check this value against max and min values
                            if (!this.isNumeric(model.trigger.occurrenceCount, {minValue: -2147483648, maxValue: 2147483647})) {
                                results.push({
                                    field: 'occurrenceCount',
                                    errorCode: 'typeMismatch.java.lang.Integer'
                                });
                            }
                        }
                    }

                    if (model.trigger.radioEndDate === "specificDate") {
                        if (!model.trigger.endDate) {
                            results.push({
                                field: 'simpleEndDate',
                                errorCode: 'report.scheduling.job.edit.specify.enddate'
                            });
                        } else {
                            // check if the date is valid
                            if (!(moment(model.trigger.endDate, UI_DATE_PATTERN, true).isValid())) {
                                results.push({
                                    field: 'simpleEndDate',
                                    errorCode: 'error.invalid.date'
                                });
                            }
                            else {
                                // check if date is in the past
                                if (this.ifPastDate(model.trigger.endDate, model.trigger.timezone)) {
                                    results.push({
                                        field: 'simpleEndDate',
                                        errorCode: 'error.before.current.date.trigger.endDate'
                                    });
                                }

                                // check if start date is before the end date
                                if (model.trigger.startType == 2 && model.trigger.startDate) {
                                    if ((+new Date(model.trigger.startDate)) >= (+new Date(model.trigger.endDate))) {
                                        results.push({
                                            field: 'simpleEndDate',
                                            errorCode: 'error.before.start.date.trigger.endDate'
                                        });
                                    }
                                }
                            }
                        }
                    }
                }

                else if (model.trigger.type === "calendar") {

                    if (model.trigger.radioWhichMonth === "selectedMonths") {
                        if (model.trigger.months.month.length == 0) {
                            results.push({
                                field: 'monthSelector',
                                errorCode: 'report.scheduling.job.edit.specify.monthswhenjobshouldrun'
                            });
                        }
                    }

                    if (model.trigger.endDate) {
                        if (!(moment(model.trigger.endDate, UI_DATE_PATTERN, true).isValid())) {
                            results.push({
                                field: 'calendarEndDate',
                                errorCode: 'error.invalid.date'
                            });
                        } else {
                            // check if date is in the past
                            if (this.ifPastDate(model.trigger.endDate, model.trigger.timezone)) {
                                results.push({
                                    field: 'calendarEndDate',
                                    errorCode: 'error.before.current.date.trigger.endDate'
                                });
                            }

                            // check if start date is before the end date
                            if (model.trigger.startType == 2 && model.trigger.startDate) {
                                if ((+new Date(model.trigger.startDate)) >= (+new Date(model.trigger.endDate))) {
                                    results.push({
                                        field: 'calendarEndDate',
                                        errorCode: 'error.before.start.date.trigger.endDate'
                                    });
                                }
                            }
                        }
                    }

                    if (model.trigger.radioWhichDay === "selectedDays") {
                        if (model.trigger.weekDays.day.length == 0) {
                            results.push({
                                field: 'daySelector',
                                errorCode: 'report.scheduling.job.edit.specify.dayswhenjobshouldrun'
                            });
                        }
                    }

                    if (model.trigger.radioWhichDay === "datesInMonth") {
                        tmp = this.parseIntervals(model.trigger.monthDays, "daysInMonth");
                        if (!tmp) {
                            results.push({
                                field: 'datesInMonth',
                                errorCode: 'report.scheduling.job.edit.specify.whenjobshouldrun'
                            });
                        }
                    }


                    if (!this.parseIntervals(model.trigger.hours, "hours")) {
                        results.push({
                            field: 'hours',
                            errorCode: 'error.not.empty.trigger.hours'
                        });
                    }

                    if (!this.parseIntervals(model.trigger.minutes, "minutes")) {
                        results.push({
                            field: 'minutes',
                            errorCode: 'error.not.empty.trigger.minutesshould'
                        });
                    }
                }
            }

            // tab #2
            // no validation required here !

            // tab #3
            var output = model.repositoryDestination;

            if (output.outputDescription && output.outputDescription.length > 250) {
                results.push({
                    field: 'outputDescription',
                    errorCode: 'report.scheduling.job.output.long.description'
                });
            }

            if (output.sequentialFilenames && !output.timestampPattern) {
                results.push({
                    field: 'timestampPattern',
                    errorCode: 'report.scheduling.job.output.timestamppattern'
                });
            }

            if (!model.baseOutputFilename) {
                results.push({
                    field: 'baseOutputFilename',
                    errorCode: 'error.not.empty.baseOutputFilename'
                });
            }

            if (model.baseOutputFilename && !this.isValidFileName(model.baseOutputFilename)) {
                results.push({
                    field: 'baseOutputFilename',
                    errorCode: 'error.invalid.chars.baseOutputFilename'
                });
            }

            if (!model.outputFormats.outputFormat || !model.outputFormats.outputFormat.length) {
                results.push({
                    field: 'outputFormats',
                    errorCode: 'error.report.job.no.output.formats'
                });
            }

            if (output.saveToRepository && !output.folderURI) {
                results.push({
                    field: 'outputRepository',
                    errorCode: 'error.not.empty.folderURI'
                });
            }

            if (output.outputLocalFolder === '') {
                results.push({
                    field: 'outputHostFileSystem',
                    errorCode: 'error.not.empty.folderURI'
                });
            }

            if (output.outputFTPInfo.enabled) {

                if (!output.outputFTPInfo.serverName) {
                    results.push({
                        field: 'ftpAddress',
                        errorCode: 'error.report.scheduling.empty.ftp.server'
                    });
                } else {
                    if (!this.isHostName(output.outputFTPInfo.serverName)) {
                        results.push({
                            field: 'ftpAddress',
                            errorCode: 'error.report.scheduling.empty.ftp.server'
                        });
                    }
                }

                if (!output.outputFTPInfo.port) {
                    results.push({
                        field: 'ftpPort',
                        errorCode: 'error.report.scheduling.empty.ftp.port'
                    });
                } else {
                    if (!this.isNumeric(output.outputFTPInfo.port)) {
                        results.push({
                            field: 'ftpPort',
                            errorCode: 'error.report.scheduling.empty.ftp.port'
                        });
                    } else {
                        tmp = parseInt(output.outputFTPInfo.port, 10);
                        if (!(0 < tmp && tmp <= 65535)) {
                            results.push({
                                field: 'ftpPort',
                                errorCode: 'error.report.scheduling.empty.ftp.port'
                            });
                        }
                    }
                }
            }

            // tab #4
            if (model.mailNotification) {

                // check the left side of the Notification tab

                var
                    toAddress = (model.mailNotification.toAddresses && model.mailNotification.toAddresses.address) || "",
                    ccAddress = (model.mailNotification.ccAddresses && model.mailNotification.ccAddresses.address) || "",
                    bccAddress = (model.mailNotification.bccAddresses && model.mailNotification.bccAddresses.address) || "",
                    subject = model.mailNotification.subject || "",
                    message = model.mailNotification.messageText || "";

                if (toAddress && !this.validateEmails(toAddress)) {
                    results.push({
                        field: 'to_suc',
                        errorCode: 'error.invalid.mailNotification.invalidEmailaddresses'
                    });
                }

                if (ccAddress && !this.validateEmails(ccAddress)) {
                    results.push({
                        field: 'cc_suc',
                        errorCode: 'error.invalid.mailNotification.invalidEmailaddresses'
                    });
                }

                if (bccAddress && !this.validateEmails(bccAddress)) {
                    results.push({
                        field: 'bcc_suc',
                        errorCode: 'error.invalid.mailNotification.invalidEmailaddresses'
                    });
                }

                // subject must be valid if TO has been specified and vice versa
                if (toAddress || subject) {
                    if (!toAddress) {
                        results.push({
                            field: 'to_suc',
                            errorCode: 'error.invalid.mailNotification.specify.oneaddresses'
                        });
                    }
                    if (!subject) {
                        results.push({
                            field: 'subject_suc',
                            errorCode: 'report.scheduling.job.edit.specify.messagesubject'
                        });
                    }
                }

                if (message) {
                    // if user has entered message, when he need to enter subject and TO
                    if (!toAddress) {
                        results.push({
                            field: 'to_suc',
                            errorCode: 'error.invalid.mailNotification.specify.oneaddresses'
                        });
                    }
                    if (!subject) {
                        results.push({
                            field: 'subject_suc',
                            errorCode: 'report.scheduling.job.edit.specify.messagesubject'
                        });
                    }
                }
            }

            // now, check the right side of the Notification tab

            if (model.alert) {
                toAddress = (model.alert.toAddresses && model.alert.toAddresses.address) || "";
                subject = model.alert.subject || "";

                var
                    sucMessage = model.alert.messageText || "",
                    failMessage = model.alert.messageTextWhenJobFails || "";

                if (toAddress && !this.validateEmails(toAddress)) {
                    results.push({
                        field: 'job_status_to',
                        errorCode: 'error.invalid.mailNotification.invalidEmailaddresses'
                    });
                }
    
                // subject must be valid if TO has been specified and vice versa
                if (toAddress || subject) {
                    if (!toAddress) {
                        results.push({
                            field: 'job_status_to',
                            errorCode: 'error.invalid.mailNotification.specify.oneaddresses'
                        });
                    }
                    if (!subject) {
                        results.push({
                            field: 'job_status_subject',
                            errorCode: 'report.scheduling.job.edit.specify.messagesubject'
                        });
                    }
                }

                // if user has decided to success send message, when he need to enter subject and TO
                if (model.alert.jobState.indexOf("SUCCESS_ONLY") !== -1 || model.alert.jobState.indexOf("ALL") !== -1) {
                    if (!toAddress) {
                        results.push({
                            field: 'job_status_to',
                            errorCode: 'error.invalid.mailNotification.specify.oneaddresses'
                        });
                    }
                    if (!subject) {
                        results.push({
                            field: 'job_status_subject',
                            errorCode: 'report.scheduling.job.edit.specify.messagesubject'
                        });
                    }
                }

                // if user has decided to send failure message, when he need to enter subject and TO
                if (model.alert.jobState.indexOf("FAIL_ONLY") !== -1 || model.alert.jobState.indexOf("ALL") !== -1) {
                    if (!toAddress) {
                        results.push({
                            field: 'job_status_to',
                            errorCode: 'error.invalid.mailNotification.specify.oneaddresses'
                        });
                    }
                    if (!subject) {
                        results.push({
                            field: 'job_status_subject',
                            errorCode: 'report.scheduling.job.edit.specify.messagesubject'
                        });
                    }
                }
            }

            // clear all errors on the form
            this.trigger('clearAllErrors');

            if (results.length)
                this.trigger('invalid', this, results);
            else
                this.trigger('valid', this, []);

            return results;
        },

        // update nested attribute
        update: function (key, value) {
            if ('object' === typeof value) {
                value = _.extend({}, this.get(key), value);
                for (var i in value) if (value.hasOwnProperty(i))
                    if (value[i] === undefined) delete value[i];
            }

            this.set(key, value);
        },

        // get nested value
        value: function (name) {
            var o = this.attributes;
            name = name.split('/');
            for (var i = 0, l = name.length - 1; i < l; i++){
                if (!o) return undefined;
                o = o[name[i]];
            }

            if (!o) return undefined;
            return o[name[name.length - 1]]
        },

        // use custom sync logic
        sync: function (method, model, options) {
            // swap 'update' and 'create' methods
            // to force backbone use POST for update
            // and PUT for creating new job
            if (method === 'update') method = 'create';
            else if (method === 'create') method = 'update';

            options || (options = {});
            options.contentType = 'application/job+json';
            options.beforeSend = function(request){
                request.setRequestHeader('Accept', 'application/job+json');
            };

            return Backbone.sync(method, model, options);
        },

        // parse model data
        parse: function (data) {

            if (data.trigger) {

                // make trigger data more flat
                // convert trigger key to trigger value type
                if ('simpleTrigger' in data.trigger)
                    data.trigger.type = 'simpleTrigger';
                else if ('calendarTrigger' in data.trigger)
                    data.trigger.type = 'calendarTrigger';
                // merge trigger sub-values
                if (data.trigger.type) {
                    _.extend(data.trigger, data.trigger[data.trigger.type]);
                    delete data.trigger[data.trigger.type];
                }


                // determine the recurrence type
                if (data.trigger.type === 'simpleTrigger' && data.trigger.recurrenceInterval === null) data.trigger.type = 'none';
                else if (data.trigger.type === 'simpleTrigger') data.trigger.type = 'simple';
                else if (data.trigger.type === 'calendarTrigger') data.trigger.type = 'calendar';
                else data.trigger.type = 'none';

                // adjust some fields "by default" if the current recurrence type is none
                if (data.trigger.type === "none") {
                    data.trigger.occurrenceCount = "";
                }

                // check start type and start date
                if (typeof data.trigger.startType === "undefined") data.trigger.startType = 1;
                if (data.trigger.startDate) {
                    data.trigger.startDate = this.formatDate(data.trigger.startDate);
                }


                if (typeof data.trigger.timezone === "undefined" || data.trigger.timezone === null) {
                    data.trigger.timezone = config.usersTimeZone || "America/Los_Angeles";
                }


                if (data.trigger.type === 'simple' || data.trigger.type === 'calendar') {
                    if (typeof data.trigger.calendarName === "undefined" || data.trigger.calendarName === null) {
                        data.trigger.calendarName = "";
                    }
                }

                // ========================================
                // Simple recurrence fields

                // set the default values if nothing is sent from the server
                if (typeof data.trigger.recurrenceInterval === "undefined" || data.trigger.recurrenceInterval === null) {
                    data.trigger.recurrenceInterval = 1;
                }
                if (typeof data.trigger.recurrenceIntervalUnit === "undefined" || data.trigger.recurrenceIntervalUnit === null) {
                    data.trigger.recurrenceIntervalUnit = "DAY";
                }
                if (typeof data.trigger.occurrenceCount === "undefined" || data.trigger.occurrenceCount === null) {
                    data.trigger.occurrenceCount = "";
                }
                if (typeof data.trigger.endDate === "undefined" || data.trigger.endDate === null) {
                    data.trigger.endDate = "";
                } else {
                    data.trigger.endDate = this.formatDate(data.trigger.endDate);
                }

                // now, detect what endDate option has been selected by user
                if (!data.trigger.endDate) {
                    if (data.trigger.occurrenceCount == -1 || !data.trigger.occurrenceCount) {
                        data.trigger.radioEndDate = 'indefinitely';
                        data.trigger.occurrenceCount = "";
                    }
                    else {
                        data.trigger.radioEndDate = 'numberOfTimes';
                    }
                }
                else if (data.trigger.endDate) {
                    data.trigger.radioEndDate = 'specificDate';
                    data.trigger.occurrenceCount = "";
                }


                // ========================================
                // Calendar recurrence fields

                // months block
                if (!data.trigger.months) data.trigger.months = {};
                if (!_.isArray(data.trigger.months.month)) data.trigger.months.month = [];
                data.trigger.radioWhichMonth = "everyMonth"; // fake field
                if (0 < data.trigger.months.month.length && data.trigger.months.month.length < 12) {
                    // it means we are in the editing mode and some selection across months has been made
                    data.trigger.radioWhichMonth = 'selectedMonths';
                } else if (data.trigger.months.month.length == 12) {
                    data.trigger.radioWhichMonth = "everyMonth";
                    data.trigger.months.month = [];
                }

                // days block
                if (!data.trigger.weekDays) data.trigger.weekDays = {};
                if (!_.isArray(data.trigger.weekDays.day)) data.trigger.weekDays.day = [];

                if (typeof data.trigger.monthDays === "undefined" || data.trigger.monthDays === null) {
                    data.trigger.monthDays = "";
                } else {
                    data.trigger.monthDays = data.trigger.monthDays.toString().replace(/ /g, "").replace(/,/g, ", ");
                }

                // if daysType is set, whem we'll follow it's value
                if (typeof data.trigger.daysType !== "undefined") {
                    if (data.trigger.daysType.toLowerCase() == "month") {
                        data.trigger.radioWhichDay = "datesInMonth";
                        data.trigger.weekDays.day = [];
                    } else if (data.trigger.daysType.toLowerCase() == "week") {
                        data.trigger.radioWhichDay = "selectedDays";
                        data.trigger.monthDays = "";
                    } else {
                        data.trigger.radioWhichDay = "everyDay";
                        data.trigger.weekDays.day = [];
                        data.trigger.monthDays = "";
                    }
                } else {
                    // in other case, we'll try to determine what user has chosen by variables
                    if ( 0 < data.trigger.weekDays.day.length && data.trigger.weekDays.day.length < 7) {
                        // it means we are in the editing mode and some selection across days has been made
                        data.trigger.radioWhichDay = 'selectedDays';
                    } else if (data.trigger.monthDays) {
                        data.trigger.radioWhichDay = "datesInMonth";
                    } else {
                        data.trigger.radioWhichDay = "everyDay";
                    }
                }

                // times block
                if (typeof data.trigger.hours === "undefined" || data.trigger.hours === null) {
                    data.trigger.hours = "0";
                } else {
                    data.trigger.hours = data.trigger.hours.toString().replace(/ /g, "").replace(/,/g, ", ");
                }

                if (typeof data.trigger.minutes === "undefined" || data.trigger.minutes === null) {
                    data.trigger.minutes = "0";
                } else {
                    data.trigger.minutes = data.trigger.minutes.toString().replace(/ /g, "").replace(/,/g, ", ");
                }

            }

            // ==============================
            // output tab
            if (typeof data.repositoryDestination === "undefined" || data.repositoryDestination === null) {
                data.repositoryDestination = {
                    overwriteFiles: true,
                    sequentialFilenames: false,
                    saveToRepository: true,
                    timestampPattern: "yyyyMMddHHmm",
                    outputFTPInfo: {
                        propertiesMap: {},
                        type: "ftp",
                        port: "21",
                        implicit: true,
                        pbsz: 0
                    }
                };
            }
            else {
                if (typeof data.repositoryDestination.outputFTPInfo === "undefined" || data.repositoryDestination.outputFTPInfo === null) {
                    data.repositoryDestination.outputFTPInfo = {};
                }
                else {

                    var ftp = data.repositoryDestination.outputFTPInfo;
                    data.repositoryDestination.outputFTPInfo.type = data.repositoryDestination.outputFTPInfo.type == "ftp" ? "TYPE_FTP" : "TYPE_FTPS";
                    ftp.enabled = !!ftp.serverName && !!ftp.userName;
                }

                if (!data.repositoryDestination.timestampPattern)
                    data.repositoryDestination.timestampPattern =  "yyyyMMddHHmm";
            }

            if (typeof data.repositoryDestination.outputFTPInfo.port === "undefined" || data.repositoryDestination.outputFTPInfo.port === null) {
                data.repositoryDestination.outputFTPInfo.port = "21";
            }

            // ==============================
            // Notification tab

            var defaultResultSendTypeValue = "SEND";

            data.mailNotification = data.mailNotification || {
                toAddresses:{address: ""},
                ccAddresses:{address: ""},
                bccAddresses:{address: ""},
                subject: "",
                messageText: "",
                resultSendType: defaultResultSendTypeValue
            };
            data.alert = data.alert || {
                toAddresses:{address: ""},
                subject: "",
                messageText: "",
                messageTextWhenJobFails: "",
                jobState: "NONE"
            };


            // check if resultSendType has valid value !
            if (typeof data.mailNotification.resultSendType === "undefined" || data.mailNotification.resultSendType === null) {
                data.mailNotification.resultSendType = defaultResultSendTypeValue;
            } else {

                var rs = data.mailNotification.resultSendType; // shortcut
                if (rs !== "SEND" && rs !== "SEND_ATTACHMENT" && rs !== "SEND_ATTACHMENT_NOZIP" && rs !== "SEND_EMBED" &&
                    rs !== "SEND_ATTACHMENT_ZIP_ALL" && rs !== "SEND_EMBED_ZIP_ALL_OTHERS" ) {
                    data.mailNotification.resultSendType = defaultResultSendTypeValue;
                }
            }

            // convert arrays into strings
            var keys = ["toAddresses", "ccAddresses", "bccAddresses"], i, from , to;
            for (i = 0; i < keys.length; i++) {

                if (typeof data.mailNotification[keys[i]] === "undefined" || data.mailNotification[keys[i]] === null) {
                    data.mailNotification[keys[i]] = {address: ""};
                }
                else if (typeof data.mailNotification[keys[i]].address === "undefined" || data.mailNotification[keys[i]].address === null) {
                    data.mailNotification[keys[i]].address = "";
                }

                if (_.isArray(data.mailNotification[keys[i]].address) && data.mailNotification[keys[i]].address.length > 0) {
                    data.mailNotification[keys[i]].address = data.mailNotification[keys[i]].address.join(EMAIL_SEPARATOR + " ");
                } else {
                    data.mailNotification[keys[i]].address = "";
                }
            }

            // drop all fields to empty values, since it's not possible what they can have some states if email is not entered
            if (!data.mailNotification.toAddresses.address) {
                data.mailNotification = {
                    toAddresses:{address: ""},
                    ccAddresses:{address: ""},
                    bccAddresses:{address: ""},
                    subject: "",
                    messageText: "",
                    resultSendType: defaultResultSendTypeValue
                };
            }

            if (typeof data.alert.toAddresses === "undefined" || data.alert.toAddresses === null) {
                data.alert.toAddresses = {address: ""};
            }
            else if (typeof data.alert.toAddresses.address === "undefined" || data.alert.toAddresses.address === null) {
                data.alert.toAddresses.address = "";
            }

            if (_.isArray(data.alert.toAddresses.address) && data.alert.toAddresses.address.length > 0) {
                data.alert.toAddresses.address = data.alert.toAddresses.address.join(EMAIL_SEPARATOR + " ");
            } else {
                // drop all fields to empty values, since it's not possible what they can have some states if email is not entered
                data.alert = {
                    toAddresses: {address: ""},
                    subject: "",
                    messageText: "",
                    messageTextWhenJobFails: "",
                    jobState: "NONE",
                    includingReportJobInfo: false,
                    includingStackTrace: false
                };
            }


            return data;
        },

        // stringify model
        toJSON: function () {
            var data = jQuery.extend(true, {}, this.attributes),
                trigger = {};

            if (data.trigger) {

                // in case of immediately start date, this field must be zero
                data.trigger.startType = parseInt(data.trigger.startType, 10);
                if (data.trigger.startType === 1) {
                    data.trigger.startDate = null;
                } else {
                    data.trigger.startDate = moment(data.trigger.startDate).format(SERVICE_DATE_PATTERN);
                }

                // set recurrenceType
                if (data.trigger.type === 'none') {

                    data.trigger.type = 'simpleTrigger';
                    data.trigger.endDate = null;
                    data.trigger.occurrenceCount = 1;
                    data.trigger.recurrenceInterval = null;
                    data.trigger.recurrenceIntervalUnit = null;

                    // remove fields from 'Calendar' section
                    delete data.trigger.hours;
                    delete data.trigger.minutes;
                    delete data.trigger.months;
                    delete data.trigger.daysType;
                    delete data.trigger.weekDays;
                    delete data.trigger.monthDays;
                    delete data.trigger.calendarName;
                }
                else if (data.trigger.type === 'simple') {

                    data.trigger.type = 'simpleTrigger';

                    // occurrence count selected
                    if (data.trigger.radioEndDate == 'numberOfTimes') {
                        data.trigger.endDate = null;
                    }
                    // specific date selected
                    if (data.trigger.radioEndDate == 'specificDate') {
                        data.trigger.occurrenceCount = -1;
                        data.trigger.endDate = moment(data.trigger.endDate).format(SERVICE_DATE_PATTERN);
                    }
                    // indefinitely selected
                    if (data.trigger.radioEndDate == 'indefinitely') {
                        data.trigger.occurrenceCount = -1;
                        data.trigger.endDate = null;
                    }

                    data.trigger.recurrenceInterval = parseInt(data.trigger.recurrenceInterval, 10);
                    data.trigger.recurrenceIntervalUnit = data.trigger.recurrenceIntervalUnit.toUpperCase();

                    if (data.trigger.calendarName == "") {
                        data.trigger.calendarName = null;
                    }

                    // remove fields from 'Calendar' section
                    delete data.trigger.hours;
                    delete data.trigger.minutes;
                    delete data.trigger.months;
                    delete data.trigger.daysType;
                    delete data.trigger.weekDays;
                    delete data.trigger.monthDays;
                }
                else if (data.trigger.type === 'calendar') {

                    data.trigger.type = 'calendarTrigger';

                    //months block
                    if (data.trigger.radioWhichMonth === 'everyMonth') {
                        data.trigger.months.month = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
                    }

                    //days block
                    if (data.trigger.radioWhichDay === 'everyDay' || data.trigger.weekDays.day.length == 7) {
                        data.trigger.daysType = "ALL"; // it means we selected all days
                        data.trigger.weekDays.day = [];
                        data.trigger.monthDays = "";
                    } else if (data.trigger.radioWhichDay === 'selectedDays') {
                        data.trigger.daysType = "WEEK"; // don't ask why -- it's the server-side requirement
                        data.trigger.monthDays = "";
                    } else if (data.trigger.radioWhichDay === 'datesInMonth') {
                        data.trigger.daysType = "MONTH"; // don't ask why -- it's the server-side requirement
                        data.trigger.weekDays.day = [];
                    }
                    data.trigger.monthDays = data.trigger.monthDays.replace(/ /g, "");


                    // remove this field if it's not specified
                    if (data.trigger.endDate === "") {
                        delete data.trigger.endDate;
                    } else {
                        data.trigger.endDate = moment(data.trigger.endDate).format(SERVICE_DATE_PATTERN);
                    }

                    data.trigger.hours = data.trigger.hours.replace(/ /g, "");
                    data.trigger.minutes = data.trigger.minutes.replace(/ /g, "");

                    if (data.trigger.calendarName == "") {
                        data.trigger.calendarName = null;
                    }

                    // remove fields from 'simple' section
                    delete data.trigger.occurrenceCount;
                    delete data.trigger.recurrenceInterval;
                    delete data.trigger.recurrenceIntervalUnit;
                }

                // remove all fake fields
                delete data.trigger.radioEndDate; // remove fake field
                delete data.trigger.radioWhichMonth; // remove fake field
                delete data.trigger.radioWhichDay; // remove fake field

                // TODO: remove next line
                //if (typeof data.trigger.calendarName !== "undefined") delete data.trigger.calendarName;

                // now, convert trigger type to sub-object
                if (data.trigger && data.trigger.type) {
                    trigger[data.trigger.type] = data.trigger;
                    delete trigger[data.trigger.type].type;
                    data.trigger = trigger;
                }
            }

            // output tab
            if (data.repositoryDestination){
                if (data.repositoryDestination.outputFTPInfo){
                    if (!data.repositoryDestination.outputFTPInfo.enabled){
                        data.repositoryDestination.outputFTPInfo = {
                            type: "TYPE_FTP",
                            port: 21,
                            folderPath: null,
                            password: null,
                            propertiesMap: {},
                            serverName: null,
                            userName: null
                        }
                    }

                    if (data.repositoryDestination.outputFTPInfo.type)
                        data.repositoryDestination.outputFTPInfo.type = data.repositoryDestination.outputFTPInfo.type == "TYPE_FTP" ? "ftp" : "ftps";

                    if ('enabled' in data.repositoryDestination.outputFTPInfo)
                        delete data.repositoryDestination.outputFTPInfo.enabled;
                }

                if (!data.repositoryDestination.sequentialFilenames) {
                    data.repositoryDestination.timestampPattern = null;
                }
            }


            // notification tab

            if (data.mailNotification) {
                // convert strings into arrays
                var keys = ["toAddresses", "ccAddresses", "bccAddresses"], i, from , to;
                for (i = 0; i < keys.length; i++) {
                    if (data.mailNotification[keys[i]] && typeof data.mailNotification[keys[i]].address !== "undefined") {
                        // if key is empty, remove it
                        if (!data.mailNotification[keys[i]].address) {
                            delete data.mailNotification[keys[i]];
                            continue;
                        }
                        // else convert it
                        data.mailNotification[keys[i]].address = data.mailNotification[keys[i]].address.replace(/ /g, "").split(EMAIL_SEPARATOR);
                    }
                }

                // if there is no TO field, remove the block completely
                if (!data.mailNotification.toAddresses || typeof data.mailNotification.toAddresses.address === "undefined") {
                    delete data.mailNotification;
                }
            }

            if (data.alert) {
                if (data.alert.toAddresses && typeof data.alert.toAddresses.address !== "undefined") {
                    // if key is empty, remove it
                    if (!data.alert.toAddresses.address) {
                        delete data.alert.toAddresses.address;
                    } else {
                        // convert it
                        data.alert.toAddresses.address = data.alert.toAddresses.address.replace(/ /g, "").split(EMAIL_SEPARATOR);
                    }
                }

                // if there is no TO field, remove the block completely
                if (!data.alert.toAddresses || typeof data.alert.toAddresses.address === "undefined") {
                    delete data.alert;
                }
            }

            // TODO: remove next line
            //data.baseOutputFilename = data.baseOutputFilename + Math.round((Math.random(1000) * 1000));

            return data;
        },

        // change job state
        state: function (state) {
            // prepare data to post
            var data = {
                // job ids to change state
                jobId: [this.id],

                // trick to make it work with prototype.js
                toJSON: function () {
                    return this
                },

				// trick to make it work with backbone-1.1.0
				trigger: function() {
					return this
				}
            };

            // change model attributes
            this.get('state').value = state ? 'NORMAL' : 'PAUSED';
            this.trigger('change');

            // call backbone sync method manually
            return Backbone.sync.call(this, 'update', data, {
                url: this.collection.url + '/' + (state ? 'resume' : 'pause'),
                type: 'POST'
            });
        },

        // get parameters for job
        parameters: function (reportUnitURI, callback) {

			reportUnitURI = reportUnitURI || this.get('source').reportUnitURI;

            var url = config.contextPath + '/rest_v2/reports' + reportUnitURI + '/inputControls/';

            // call backbone sync method manually
            return Backbone.sync.call(this, 'read', new Backbone.Model(), {
                url: url,
                type: 'GET',
                success: function(data, xhr){
                    if ('function' === typeof callback)
                        callback(undefined, data);
                },
                error: function(err){
                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        },

        // override save
        save: function(data, options){
            var timer,
                t = this,
                count = 1,
                save = function(){
                    // call parent's save method
                    Backbone.Model.prototype.save.call(t, data, options);
                },
                check = function(err){
                    if (err) return;
                    --count;
                    timer && clearTimeout(timer);
                    timer = setTimeout(function(){
                        if (count === 0) save();
                    });
                },
                lock = function(){
                    count++;
                    return check;
                };

            // check output folder before save
            this.testOutputLocalFolder(check);

            // notify about save
            this.trigger('save', this, lock);
        },

        // test model's ftp attributes
        testFTPConnection: function(callback){

            if (ftpIsProcessings) {
                if ('function' === typeof callback)
                    callback();
                return;
            }

            var ftpInfo = this.get('repositoryDestination');

            if (!(ftpInfo && ftpInfo.outputFTPInfo && ftpInfo.outputFTPInfo.enabled)) {
                if ('function' === typeof callback)
                    callback();
                return;
            }

            // make life easier
            ftpInfo = ftpInfo.outputFTPInfo;

            var t = this,
                data = {
                    host: ftpInfo.serverName,
                    userName: ftpInfo.userName,
                    password: ftpInfo.password,
                    folderPath: ftpInfo.folderPath,
                    type: ftpInfo.type == "TYPE_FTP" ? "ftp" : "ftps",
                    protocol: ftpInfo.protocol,
                    port: ftpInfo.port,
                    implicit: ftpInfo.implicit,
                    prot: ftpInfo.prot,
                    pbsz: ftpInfo.pbsz,

                    // trick to make it work with prototype.js
                    toJSON: function(){ return this; },
					// trick to make a fake model for backbone.js
					trigger: function(){ return this; }
                };

            ftpIsProcessings = true;
            // disable FTP button
            $("#ftpTestButton").addClass("checking");

            // clear the erro while we are performing checking...
            $("[data-field=ftpTest]").text("");

            // call backbone sync method manually
            return Backbone.sync.call(t, 'update', data, {
                url: config.contextPath + '/rest_v2/connections',
                contentType: 'application/connections.ftp+json',
                headers:{ 'Accept': 'application/json' },
                type: 'POST',
                success: function (data, xhr) {
                    ftpIsProcessings = false;
                    // enable FTP button
                    $("#ftpTestButton").removeClass("checking");
                    t.trigger('valid', t, [{
                        field: 'ftpTest',
                        errorCode: 'report.scheduling.connection.passed'
                    }]);

                    if ('function' === typeof callback)
                        callback(undefined);
                },
                error: function (err) {
                    ftpIsProcessings = false;
                    // enable FTP button
                    $("#ftpTestButton").removeClass("checking");
                    t.trigger('invalid', t, [{
                        field: 'ftpTest',
                        errorCode: 'report.scheduling.connection.failed'
                    }]);

                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        },

        // test model's outputLocalFolder
        testOutputLocalFolder: function(callback){
            var t = this;

            if (!t.get('repositoryDestination') || !t.get('repositoryDestination').outputLocalFolder){
                if ('function' === typeof callback) callback();
                return;
            }

            var data = {
                path: t.get('repositoryDestination').outputLocalFolder,
                toJSON: function(){ return this; },
                trigger: function(){ return this; }
            };

            return Backbone.sync.call(t, 'update', data, {
                url: config.contextPath + '/rest_v2/connections',
                contentType: 'application/connections.lfs+json',
                type: 'POST',
                headers:{ 'Accept': 'application/json' },
                success: function (data, xhr) {
                    if ('function' === typeof callback)
                        callback(undefined);
                },
                error: function (err) {
                    t.trigger('invalid', t, [{
                        field: 'outputHostFileSystem',
                        errorCode: 'report.scheduling.output.localhostpath'
                    }], {
                        switchToErrors: true
                    });

                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        },

        // 
        permission: function(callback){
            var path = this.get('repositoryDestination').folderURI;

            // call backbone sync method manually
            return Backbone.sync.call(this, 'read', new Backbone.Model(), {
                url: config.contextPath + '/rest_v2/resources' + path,
                headers:{ 'Accept': 'application/repository.folder+json' },
                type: 'GET',
                success: function(data, xhr){
                    if ('function' === typeof callback)
                        callback(undefined, data.permissionMask);
                },
                error: function(err){
                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        },

        // 
        resource: function(type, callback){
            // call backbone sync method manually
            return Backbone.sync.call(this, 'read', new Backbone.Model(), {
                url: config.contextPath + '/rest_v2/resources' + this.get('source').reportUnitURI,
                headers:{ 'Accept': 'application/repository.' + type + '+json' },
                type: 'GET',
                success: function(data, xhr){
                    if ('function' === typeof callback)
                        callback(undefined, data);
                },
                error: function(err){
                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        },

        // parse job uri and provide parsed values
        parseUri: function (uri) {
            // private variables
            var match;

            // check first char of uri, make it / in any case
            if (uri[0] !== '/') uri = '/' + uri;

            // check for pattern and return parsed values if matches
            if (uri && (match = uri.match(/^(.*)\/([^\/]+)$/))) {
                if (match[1] === "") match[1] = "/";
                return {
                    full: match[0],
                    folder: match[1],
                    file: match[2]
                };
            }

            // not match
            return {};
        },

        // create new model from uri
        createFromUri: function (uri) {

            // clear all errors on the form
            this.trigger('clearAllErrors');

            // clear model
            this.clear({ silent: true });

            // parse uri components
            uri = this.parseUri(uri);

            // set new model attributes
            this.set(this.parse({
                baseOutputFilename: uri.file,
                outputFormats: { outputFormat: ['PDF'] },
                source: {
                    reportUnitURI: uri.full
                },
                trigger: {
                    // send almost an empty object to indicate what it's model for editing
                    timezone: config.usersTimeZone || "America/Los_Angeles"
                },
                outputTimeZone: config.usersTimeZone || "America/Los_Angeles",
                repositoryDestination: {
                    overwriteFiles: true,
                    sequentialFilenames: false,
                    folderURI: uri.folder,
                    saveToRepository: true,
                    timestampPattern: "yyyyMMddHHmm",
                    outputFTPInfo: {
                        propertiesMap: {},
                        type: "ftp",
                        port: "21",
                        implicit: true,
                        pbsz: 0
                    }
                }
            }));
        },

        // format date from timestamp
        formatDate: function (timestamp) {
            if (!timestamp) return '';
            return moment(timestamp, SERVICE_DATE_PATTERN).format(UI_DATE_PATTERN);
        },

        parseIntervals: function (str, type) {
            str = str || false;
            if (!str) return false;

            str = str.replace(/ /g, "");

            var strs = str.split(","), i, k, numbers = [], control = {}, tmp;

            // be default, we are checking DatesInMonth
            var MIN = 1;
            var MAX = 31;
            if (type === "hours") {
                MIN = 0;
                MAX = 23;
            }
            if (type === "minutes") {
                MIN = 0;
                MAX = 59;
            }

            if (strs.length < 1) return false;

            for (i = 0; i < strs.length; i++) {
                if (strs[i].indexOf("-") === -1) {

                    if (!this.isNumeric(strs[i], {allowZero: true})) return false;
                    strs[i] = parseInt(strs[i], 10);

                    if (!(MIN <= strs[i] && strs[i] <= MAX)) return false;
                    if (typeof control[strs[i]] === "undefined") {
                        numbers.push(strs[i]);
                        control[strs[i]] = 1;
                    }
                    continue;
                }

                tmp = strs[i].split("-");

                if (!this.isNumeric(tmp[0], {allowZero: true})) return false;
                if (!this.isNumeric(tmp[1], {allowZero: true})) return false;

                tmp[0] = parseInt(tmp[0], 10);
                tmp[1] = parseInt(tmp[1], 10);
                if (!(MIN <= tmp[0] && tmp[0] <= MAX)) return false;
                if (!(MIN <= tmp[1] && tmp[1] <= MAX)) return false;
                if (tmp[0] >= tmp[1]) return false;
                for (k = tmp[0]; k <= tmp[1]; k++) {
                    if (typeof control[k] === "undefined") {
                        numbers.push(k);
                        control[k] = 1;
                    }
                }
            }
            return numbers;
        },

        ifPastDate: function(date, date_tz) {
            if (!date || !date_tz) return false;

            var target_tz, my_tz, tz_diff, currentTime, selectedTime;

            target_tz = getTZOffset(date_tz);
            my_tz = -1 * moment().zone() / 60;
            tz_diff = my_tz - target_tz;

            currentTime = +moment().format("X");
            selectedTime = +moment(date).add("minute", 1).format("X") + 3600 * (tz_diff);

            return selectedTime < currentTime;
        },

        isHostName: function(hostname) {
            if (!hostname) return false;
            var hostnameRegex = /^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])$/;
            return hostnameRegex.test(hostname);
        },

        isEmail: function(email) {
            if (!email) return false;
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        },

        // makes sure it contains only valid chars - moved from DefaultReportJobValidator#validateJobOutput
        isValidFileName: function(fileName) {
            if (!fileName) return false;
            var re = /^(\w|\d|\_|\.|\-|[;@]|[^\x00-\x80])+$/;
            return re.test(fileName);
        },

        validateEmails: function(emails) {
            if (!emails) return false;
            emails = emails.replace(/ /g, "");
            emails = emails.split(EMAIL_SEPARATOR);
            for (var i = 0; i < emails.length; i++) {
                if (!this.isEmail(emails[i]))
                    return false;
            }
            return true;
        },

        isNumeric: function(val, cfg) {

            if (!val) return false;

            cfg = cfg || {};
            cfg.allowNegative = cfg.allowNegative || false;
            cfg.allowZero = cfg.allowZero || false;
            cfg.maxValue = cfg.maxValue || false;
            cfg.minValue = cfg.minValue || false;

            if (typeof val === "string") {

                // check for any characters expect digits
                if (val.match(/\D/)) return false;

                var i = parseInt(val, 10);
                if (_.isNaN(i)) return false;
            }

            val = i;

            if (cfg.allowNegative === false && i < 0) return false;
            if (cfg.allowZero === false && i === 0) return false;
            if (cfg.maxValue && cfg.maxValue < i) return false;
            if (cfg.minValue && i < cfg.minValue) return false;

            return true;
        },

        buildDate: function (date, tz) {
            var offset = getTZOffset(tz) + 4;
            var sign = (offset > 0) ? "+" : "-";
            offset = Math.abs(offset);
            if (offset < 10) offset = "0" + offset;
            var str = date.replace(" ", "T") + ":00" + sign + offset + ":00";
            return str;
        }

    });

});
