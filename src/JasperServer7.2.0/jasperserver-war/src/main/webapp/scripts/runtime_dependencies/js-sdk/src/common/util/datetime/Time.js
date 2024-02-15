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

    var _ = require("underscore");

    function getTimeRegexpPattern (timeFormat) {
        return new RegExp("^" + timeFormat.toLowerCase()
            .replace("hh", "([0-1][0-9]|2[0-3])").replace("mm", "([0-5][0-9])").replace("ss", "([0-5][0-9])") + "$");
    }

    function getTimeFormatComponents (timeFormat) {
        var matches = timeFormat.toLowerCase().match(/(hh|mm|ss)/g),
            componentOrders = { h: -1, m: -1, s: -1 };

        if (matches) {
            for (var i = 0; i < matches.length; i++) {
                if (componentOrders[matches[i].toString().charAt(0)] == -1) {
                    componentOrders[matches[i].toString().charAt(0)] = i + 1;
                }
            }
        }

        return componentOrders;
    }

    function Time(hours, minutes, seconds) {
        this.hours = hours || 0;
        this.minutes = minutes || 0;
        this.seconds = seconds || 0;
    }

    Time.prototype.total = function() {
        return this.hours * 3600 + this.minutes * 60 + this.seconds;
    };

    Time.prototype.isValid = function() {
        return this.hours >= 0 && this.hours <= 23
            && this.minutes >= 0 && this.minutes <= 59
            && this.seconds >=0 && this.seconds <= 59;
    };

    Time.prototype.format = function(pattern) {
        return pattern.toLowerCase()
            .replace("hh", this.hours < 10 ? ("0" + this.hours) : this.hours)
            .replace("mm", this.minutes < 10 ? ("0" + this.minutes) : this.minutes)
            .replace("ss", this.seconds < 10 ? ("0" + this.seconds) : this.seconds);
    };

    Time.compare = function(time1, time2) {
        var total1 = time1.total(),
            total2 = time2.total();

        if (total1 < total2) {
            return -1;
        } else if (total1 > total2) {
            return 1;
        } else {
            return 0;
        }
    };

    Time.parse = function(value, timeFormat) {
        var timeRegexpPattern = getTimeRegexpPattern(timeFormat);

        if (!_.isString(value) || !timeRegexpPattern.test(value)) {
            return undefined;
        } else {
            var timeObj = new Time(),
                components = timeRegexpPattern.exec(value);

            var timeFormatComponents = getTimeFormatComponents(timeFormat);

            timeObj.hours = parseInt(components[timeFormatComponents.h], 10);
            timeObj.minutes = parseInt(components[timeFormatComponents.m], 10);
            timeObj.seconds = parseInt(components[timeFormatComponents.s], 10);

            return timeObj;
        }
    };

    return Time;
});
