define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var jrsConfigs = require("../../../jrs.configs");

var RelativeTime = require('../datetime/RelativeTime');

var Time = require('../datetime/Time');

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
var ISO_8061_TIME_PATTERN = 'HH:mm:ss';
var localeSettingsObject = jrsConfigs.localeSettings;

function isRelativeTime(value) {
  return RelativeTime.isValid(value);
}

function isTime(value, timeFormat) {
  var time = Time.parse(value, timeFormat != null ? timeFormat : localeSettingsObject.timeFormat);
  return typeof time !== 'undefined' && time.isValid();
}

function isIso8601Time(value) {
  return isTime(value, ISO_8061_TIME_PATTERN);
}

function compareTimes(value1, value2, timeFormat) {
  var time1 = value1 instanceof Time ? value1 : Time.parse(value1, timeFormat != null ? timeFormat : localeSettingsObject.timeFormat),
      time2 = value2 instanceof Time ? value2 : Time.parse(value2, timeFormat != null ? timeFormat : localeSettingsObject.timeFormat);

  if (typeof time1 === 'undefined' || typeof time2 === 'undefined') {
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

module.exports = {
  isRelativeTime: isRelativeTime,
  isTime: isTime,
  isIso8601Time: isIso8601Time,
  compareTimes: compareTimes,
  timeToIso8061Time: timeToIso8061Time,
  iso8601TimeToTimeObject: iso8601TimeToTimeObject
};

});