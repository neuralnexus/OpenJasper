define(function (require) {
    "use strict";

    var moment = require("moment"),
        jrsConfigs = require("jrs.configs"),
        _ = require("underscore"),
        RelativeDate = require("common/util/datetime/RelativeDate"),
        RelativeTimestamp = require("common/util/datetime/RelativeTimestamp");

    var ISO_8601_TIMESTAMP_PATTERN = "YYYY-MM-DD[T]HH:mm:ss",
        ISO_8601_DATE_PATTERN = "YYYY-MM-DD",
        ISO_8601_TIME_PATTERN = "hh:mm:ss";

    function toMomentDatePattern(dateFormat) {
        var datePattern = dateFormat.toLowerCase();

        return datePattern.replace(datePattern.indexOf("yyyy") > -1 ? "yyyy" : "yy", "YYYY")
            .replace("mm", "MM").replace("dd", "DD");
    }

    function toMomentTimePattern(timeFormat) {
        return timeFormat.toLowerCase().replace("hh", "HH");
    }

    function toMomentTimestampPattern(dateFormat, timeFormat, separator) {
        var datePattern = toMomentDatePattern(dateFormat),
            timePattern = toMomentTimePattern(timeFormat);

        return datePattern + (separator != null ? separator : " ") + timePattern;
    }

    function dateStringToMoment(value) {
        return moment(value, toMomentDatePattern(jrsConfigs.localeSettings.dateFormat), true);
    }

    function timestampStringToMoment(value) {
        return moment(value, toMomentTimestampPattern(
            jrsConfigs.localeSettings.dateFormat,
            jrsConfigs.localeSettings.timeFormat,
            jrsConfigs.localeSettings.timestampSeparator), true);
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
            jrsConfigs.localeSettings.dateFormat,
            jrsConfigs.localeSettings.timeFormat,
            jrsConfigs.localeSettings.timestampSeparator), ISO_8601_TIMESTAMP_PATTERN);
    }

    function isoTimestampToLocalizedTimestamp(isoTimestampString){
        return convert(isoTimestampString, ISO_8601_TIMESTAMP_PATTERN, toMomentTimestampPattern(
            jrsConfigs.localeSettings.dateFormat,
            jrsConfigs.localeSettings.timeFormat,
            jrsConfigs.localeSettings.timestampSeparator));
    }

    function localizedDateToIsoDate(localizedDateString){
        return convert(localizedDateString, toMomentDatePattern(jrsConfigs.localeSettings.dateFormat), ISO_8601_DATE_PATTERN);
    }

    function isoDateToLocalizedDate(isoDateString){
        return convert(isoDateString, ISO_8601_DATE_PATTERN, toMomentDatePattern(jrsConfigs.localeSettings.dateFormat));
    }

    function localizedTimeToIsoTime(localizedTimeString){
        return convert(localizedTimeString, toMomentTimePattern(jrsConfigs.localeSettings.timeFormat), ISO_8601_TIME_PATTERN);
    }

    function isoTimeToLocalizedTime(isoTimeString){
        return convert(isoTimeString, ISO_8601_TIME_PATTERN, toMomentTimePattern(jrsConfigs.localeSettings.timeFormat));
    }

    function convert(value, fromPattern, toPattern) {
        if (RelativeDate.isValid(value)) {
            // return original value if it's relative date
            return value;
        }
        var momentValue = moment(value, fromPattern);
        if (momentValue.isValid()) {
            // return formatted value if valid date
            return momentValue.format(toPattern);
        } else {
            // return original value if invalid
            return value;
        }
    }

    return {
        isRelativeDate: isRelativeDate,
        isRelativeTimestamp: isRelativeTimestamp,
        isDate: isDate,
        isTimestamp: isTimestamp,
        isIso8601Timestamp: isIso8601Timestamp,
        compareDates: compareDates,
        compareTimestamps: compareTimestamps,
        dateObjectToIso8601Timestamp: dateObjectToIso8601Timestamp,
        iso8601TimestampToDateObject: iso8601TimestampToDateObject,
        localizedTimestampToIsoTimestamp: localizedTimestampToIsoTimestamp,
        isoTimestampToLocalizedTimestamp: isoTimestampToLocalizedTimestamp,
        localizedDateToIsoDate: localizedDateToIsoDate,
        isoDateToLocalizedDate: isoDateToLocalizedDate,
        localizedTimeToIsoTime: localizedTimeToIsoTime,
        isoTimeToLocalizedTime: isoTimeToLocalizedTime
    }
});