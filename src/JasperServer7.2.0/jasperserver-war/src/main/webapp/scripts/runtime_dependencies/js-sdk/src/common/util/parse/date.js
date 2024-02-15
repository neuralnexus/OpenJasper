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

define(function (require) {
    "use strict";

    var moment = require("localizedMoment"),
        localeSettings = require("settings/localeSettings").localeSettings,
        _ = require("underscore"),
        RelativeDate = require("common/util/datetime/RelativeDate"),
        RelativeTimestamp = require("common/util/datetime/RelativeTimestamp"),
        jrsConfigs = require("jrs.configs");

    require("momentTimezone");

    var userTimezone = jrsConfigs.userTimezone;

    var ISO_8601_TIMESTAMP_PATTERN = "YYYY-MM-DD[T]HH:mm:ss",
        ISO_8601_DATE_PATTERN = "YYYY-MM-DD",
        ISO_8601_TIME_PATTERN = "HH:mm:ss";

    function toMomentDatePattern(dateFormat) {
        var result = dateFormat;

        //convert year part of the format
        if (dateFormat.indexOf("yy") > -1) {
            result = result.replace("yy", "YYYY"); // four digit year: 2015
        } else {
            result = result.replace("y", "YY"); // two digit year: 15
        }


        //convert month part of the format
        if (dateFormat.indexOf("mm") > -1) {
            result = result.replace("mm", "MM"); // two digit month with leading zero: 02
        } else if (dateFormat.indexOf("MM") > -1) {
            result = result.replace("MM", "MMMM"); // Long month name: February
        } else if (dateFormat.indexOf("m") > -1) {
            result = result.replace("m", "M"); // two digit month withoud leading zero: 2
        } else if (dateFormat.indexOf("M") > -1) {
            result = result.replace("M", "MMM"); // Short month name: Feb
        }


        //convert day part of the format
        if (dateFormat.indexOf("dd") > -1) {
            result = result.replace("dd", "DD"); // day of month (two digit): 02
        } else if (dateFormat.indexOf("DD") > -1) {
            result = result.replace("DD", "dddd"); // week day name long: Monday
        } else if (dateFormat.indexOf("d") > -1) {
            result = result.replace("d", "D"); // day of month (two digit) without leading zero: 2
        } else if (dateFormat.indexOf("D") > -1) {
            result = result.replace("D", "ddd"); // week day short day name: Mon
        }

        return result;
    }

    function toMomentTimePattern(timeFormat) {
        return timeFormat.toLowerCase().replace("hh", "HH");
    }

    function toMomentTimestampPattern(dateFormat, timeFormat, separator) {
        var datePattern = toMomentDatePattern(dateFormat),
            timePattern = toMomentTimePattern(timeFormat);

        return datePattern + (separator != null ? separator : " ") + timePattern;
    }

    function toMomentDateOrTimeOrTimestampPattern(dateOrTimeOrTimestampFormat, dateTimeSeparator) {
        dateTimeSeparator = dateTimeSeparator || " ";

        var formatAsArray = dateOrTimeOrTimestampFormat.split(dateTimeSeparator);
        var dateFormat = formatAsArray[0];
        var timeFormat = formatAsArray.length > 1 ? formatAsArray[1] : null;

        if (dateFormat.indexOf("h") > 0 || dateFormat.indexOf("s") > 0) {
            timeFormat = dateFormat;
            dateFormat = null;
        }

        var result = "";

        if (dateFormat) {
            result += toMomentDatePattern(dateFormat);
        }

        if (timeFormat) {
            if (result) {
                result += dateTimeSeparator;
            }

            result += toMomentTimePattern(timeFormat);
        }

        return result;
    }

    function dateStringToMoment(value) {
        return moment(value, toMomentDatePattern(localeSettings.dateFormat), true);
    }

    function timestampStringToMoment(value) {
        return moment(value, toMomentTimestampPattern(
            localeSettings.dateFormat,
            localeSettings.timeFormat,
            localeSettings.timestampSeparator), true);
    }

    function isRelativeDate(value) {
        return RelativeDate.isValid(value);
    }

    function isRelativeTimestamp(value) {
        return RelativeTimestamp.isValid(value);
    }

    function isDate(value) {
        return (_.isDate(value) && moment(value).isValid())
            || (_.isString(value) && value !== "" && dateStringToMoment(value).isValid());
    }

    function isTimestamp(value) {
        return (_.isDate(value) && moment(value).isValid())
            || (_.isString(value) && value !== "" && timestampStringToMoment(value).isValid());
    }

    function isIso8601Timestamp(value) {
        return moment(value, ISO_8601_TIMESTAMP_PATTERN, true).isValid();
    }

    function compareDates(value1, value2) {
        if (!isDate(value1) || !isDate(value2)) {
            return;
        }

        var moment1 = _.isDate(value1) ? moment(value1) : dateStringToMoment(value1),
            moment2 = _.isDate(value2) ? moment(value2) : dateStringToMoment(value2);

        return compareMoments(moment1, moment2);
    }

    function compareTimestamps(value1, value2) {
        if (!isTimestamp(value1) || !isTimestamp(value2)) {
            return;
        }

        var moment1 = _.isDate(value1) ? moment(value1) : timestampStringToMoment(value1),
            moment2 = _.isDate(value2) ? moment(value2) : timestampStringToMoment(value2);

        return compareMoments(moment1, moment2);
    }

    function compareMoments(moment1, moment2) {
        if (moment1.isBefore(moment2)) {
            return -1;
        } else if (moment1.isAfter(moment2)) {
            return 1;
        } else {
            return 0;
        }
    }

    function dateObjectToIso8601Timestamp(value) {
        var momentDate = moment(value);

        if (_.isDate(value) && momentDate.isValid()) {
            return momentDate.format(ISO_8601_TIMESTAMP_PATTERN);
        }

        return undefined;
    }

    function iso8601TimestampToDateObject(value) {
        var momentDate = moment(value, ISO_8601_TIMESTAMP_PATTERN, true);

        if (momentDate.isValid()) {
            return momentDate.toDate();
        }

        return undefined;
    }

    function localizedTimestampToIsoTimestamp(localizedTimestampString){
        return convert(localizedTimestampString, toMomentTimestampPattern(
            localeSettings.dateFormat,
            localeSettings.timeFormat,
            localeSettings.timestampSeparator), ISO_8601_TIMESTAMP_PATTERN);
    }

    function isoTimestampToLocalizedTimestamp(isoTimestampString){
        return convert(isoTimestampString, ISO_8601_TIMESTAMP_PATTERN, toMomentTimestampPattern(
            localeSettings.dateFormat,
            localeSettings.timeFormat,
            localeSettings.timestampSeparator));
    }

    function localizedDateToIsoDate(localizedDateString){
        return convert(localizedDateString, toMomentDatePattern(localeSettings.dateFormat), ISO_8601_DATE_PATTERN);
    }

    function isoDateToLocalizedDate(isoDateString){
        return convert(isoDateString, ISO_8601_DATE_PATTERN, toMomentDatePattern(localeSettings.dateFormat));
    }

    function localizedTimeToIsoTime(localizedTimeString){
        return convert(localizedTimeString, toMomentTimePattern(localeSettings.timeFormat), ISO_8601_TIME_PATTERN);
    }

    function isoTimeToLocalizedTime(isoTimeString){
        return convert(isoTimeString, ISO_8601_TIME_PATTERN, toMomentTimePattern(localeSettings.timeFormat));
    }

    function convert(value, fromPattern, toPattern) {
        if (RelativeDate.isValid(value)) {
            // return original value if it's relative date
            return value;
        }
        var momentValue = moment(value, fromPattern, true);
        if (momentValue.isValid()) {
            // return formatted value if valid date
            return momentValue.format(toPattern);
        } else {
            // return original value if invalid
            return value;
        }
    }

    function iso8601DateToMoment(isoDateString) {
        return moment(isoDateString, ISO_8601_DATE_PATTERN, true);
    }
    function momentToIso8601Date(momentDate) {
        return momentDate.format(ISO_8601_DATE_PATTERN);
    }
    function momentToLocalizedDate(momentDate) {
        return momentDate.format(toMomentDatePattern(localeSettings.dateFormat));
    }

    function isoDateToLocalizedDateByTimezone(value, timezone) {
        var momentDate;

        if (timezone) {
            momentDate = moment.tz(value, timezone).tz(userTimezone);
        } else {
            momentDate = moment.utc(value).tz(userTimezone);
        }

        return momentDate.format(toMomentDatePattern(localeSettings.dateFormat));
    }

    function isoTimeToLocalizedTimeByTimezone(value, timezone) {
        var duration = moment.duration(value);

        var utcMoment = moment.utc();

        if (timezone) {
            utcMoment.tz(timezone);
        }

        utcMoment.hours(duration.hours());
        utcMoment.minutes(duration.minutes());
        utcMoment.seconds(duration.seconds());

        return utcMoment.tz(userTimezone).format(toMomentTimePattern(localeSettings.timeFormat));
    }

    function isoTimestampToLocalizedTimestampByTimezone(value, timezone) {
        var momentTimestamp;

        if (timezone) {
            momentTimestamp = moment.tz(value, timezone).tz(userTimezone);
        } else {
            momentTimestamp = moment.utc(value).tz(userTimezone);
        }

        return momentTimestamp.format(toMomentTimestampPattern(
            localeSettings.dateFormat,
            localeSettings.timeFormat,
            localeSettings.timestampSeparator));
    }

    function localizedDateToIsoDateByTimezone(value, timezone) {
        value = localizedDateToIsoDate(value);

        var momentDate = moment.tz(value, userTimezone);

        if (timezone) {
            momentDate = momentDate.tz(timezone);
        } else {
            momentDate = momentDate.utc();
        }

        return momentDate.format(ISO_8601_DATE_PATTERN);
    }

    function localizedTimeToIsoTimeByTimezone(value, timezone) {
        var duration = moment.duration(value);

        var utcMoment = moment.utc();

        var timezoneMoment = utcMoment.tz(userTimezone);

        timezoneMoment.hours(duration.hours());
        timezoneMoment.minutes(duration.minutes());
        timezoneMoment.seconds(duration.seconds());

        var momentTime;

        if (timezone) {
            momentTime = timezoneMoment.tz(timezone);
        } else {
            momentTime = timezoneMoment.utc();
        }


        return momentTime.format(ISO_8601_TIME_PATTERN);
    }

    function localizedTimestampToIsoTimestampByTimezone(value, timezone) {
        value = localizedTimestampToIsoTimestamp(value);

        var momentTimestamp = moment.tz(value, userTimezone);

        if (timezone) {
            momentTimestamp = momentTimestamp.tz(timezone);
        } else {
            momentTimestamp = momentTimestamp.utc();
        }

        return momentTimestamp.format(ISO_8601_TIMESTAMP_PATTERN);
    }

    return {
        isRelativeDate: isRelativeDate,
        isRelativeTimestamp: isRelativeTimestamp,
        isDate: isDate,
        isTimestamp: isTimestamp,
        isIso8601Timestamp: isIso8601Timestamp,
        compareDates: compareDates,
        compareTimestamps: compareTimestamps,
        iso8601DateToMoment: iso8601DateToMoment,
        momentToIso8601Date: momentToIso8601Date,
        momentToLocalizedDate: momentToLocalizedDate,
        dateObjectToIso8601Timestamp: dateObjectToIso8601Timestamp,
        iso8601TimestampToDateObject: iso8601TimestampToDateObject,
        localizedTimestampToIsoTimestamp: localizedTimestampToIsoTimestamp,
        isoTimestampToLocalizedTimestamp: isoTimestampToLocalizedTimestamp,
        localizedDateToIsoDate: localizedDateToIsoDate,
        isoDateToLocalizedDate: isoDateToLocalizedDate,
        localizedTimeToIsoTime: localizedTimeToIsoTime,
        isoTimeToLocalizedTime: isoTimeToLocalizedTime,
        toMomentDateOrTimeOrTimestampPattern: toMomentDateOrTimeOrTimestampPattern,
        isoDateToLocalizedDateByTimezone: isoDateToLocalizedDateByTimezone,
        localizedDateToIsoDateByTimezone: localizedDateToIsoDateByTimezone,
        isoTimeToLocalizedTimeByTimezone: isoTimeToLocalizedTimeByTimezone,
        localizedTimeToIsoTimeByTimezone: localizedTimeToIsoTimeByTimezone,
        isoTimestampToLocalizedTimestampByTimezone: isoTimestampToLocalizedTimestampByTimezone,
        localizedTimestampToIsoTimestampByTimezone: localizedTimestampToIsoTimestampByTimezone
    }
});