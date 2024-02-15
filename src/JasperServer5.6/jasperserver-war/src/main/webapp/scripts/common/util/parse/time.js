/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * Licensed under commercial Jaspersoft Subscription License Agreement
 */


/**
 * @version: $Id: time.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var jrsConfigs = require("jrs.configs"),
        _ = require("underscore"),
        RelativeTime = require("common/util/datetime/RelativeTime"),
        Time = require("common/util/datetime/Time");

    var ISO_8061_TIME_PATTERN = "hh:mm:ss";

    function isRelativeTime(value) {
        return RelativeTime.isValid(value);
    }

    function isTime(value, timeFormat) {
        var time = Time.parse(value, timeFormat != null ? timeFormat : jrsConfigs.localeSettings.timeFormat);
        return typeof time !== "undefined" && time.isValid();
    }

    function isIso8601Time(value) {
        return isTime(value, ISO_8061_TIME_PATTERN);
    }

    function compareTimes(value1, value2, timeFormat) {
        var time1 = value1 instanceof Time ? value1 : Time.parse(value1, timeFormat != null ? timeFormat : jrsConfigs.localeSettings.timeFormat),
            time2 = value2 instanceof Time ? value2 : Time.parse(value2, timeFormat != null ? timeFormat : jrsConfigs.localeSettings.timeFormat);

        if (typeof time1 === "undefined" || typeof time2 === "undefined") {
            return;
        }

        return Time.compare(time1, time2);
    }

    function timeToIso8061Time(hours, minutes, seconds) {
        var obj = new Time(hours, minutes, seconds);

        if (obj.isValid()) {
            return obj.format(ISO_8061_TIME_PATTERN);
        }

        return undefined;
    }

    function iso8601TimeToTimeObject(val) {
        return Time.parse(val, ISO_8061_TIME_PATTERN);
    }

    return {
        isRelativeTime: isRelativeTime,
        isTime: isTime,
        isIso8601Time: isIso8601Time,
        compareTimes: compareTimes,
        timeToIso8061Time: timeToIso8061Time,
        iso8601TimeToTimeObject: iso8601TimeToTimeObject
    }
});
