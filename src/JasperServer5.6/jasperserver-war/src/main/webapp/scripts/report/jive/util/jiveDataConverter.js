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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: jiveDataConverter.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var $ = require("jquery"),
        numberUtil = require("common/util/parse/number"),
        dateUtil = require("common/util/parse/date"),
        timeUtil = require("common/util/parse/time");

    require("jquery.timepicker");

    var jiveDataConverter = {
        jQueryUiTimestampToIsoTimestamp: function(val, genericProperties) {
            if (genericProperties && genericProperties.calendarPatterns) {
                var datePattern = genericProperties.calendarPatterns.date,
                    timePattern = genericProperties.calendarPatterns.time;

                return dateUtil.dateObjectToIso8601Timestamp($.datepicker.parseDateTime(datePattern, timePattern, val));
            }

            return undefined;
        },

        jQueryUiTimeToIsoTime: function(val, genericProperties) {
            if (genericProperties && genericProperties.calendarPatterns) {
                var timePattern = genericProperties.calendarPatterns.time;

                var parsedTimeObj = $.datepicker.parseTime(timePattern, val);

                if (parsedTimeObj) {
                    return timeUtil.timeToIso8061Time(parsedTimeObj.hour, parsedTimeObj.minute, parsedTimeObj.second);
                }
            }

            return undefined;
        },

        isoTimestampTojQueryUiTimestamp: function(val, genericProperties) {
            if (genericProperties && genericProperties.calendarPatterns) {
                var date = dateUtil.iso8601TimestampToDateObject(val),
                    datePattern = genericProperties.calendarPatterns.date,
                    timePattern = genericProperties.calendarPatterns.time,
                    separator = $.timepicker && $.timepicker._defaults && $.timepicker._defaults.separator ? $.timepicker._defaults.separator : " ",
                    dateStr = $.datepicker.formatDate(datePattern, date),
                    timeStr = $.datepicker.formatTime(timePattern, {
                        hour: date.getHours(),
                        minute: date.getMinutes(),
                        second: date.getSeconds()
                    });

                return dateStr + separator + timeStr;
            }

            return undefined;
        },

        isoTimeTojQueryUiTime: function(val, genericProperties) {
            if (genericProperties && genericProperties.calendarPatterns) {
                var time = timeUtil.iso8601TimeToTimeObject(val),
                    timePattern = genericProperties.calendarPatterns.time;

                if (time) {
                    return $.datepicker.formatTime(timePattern, {
                        hour: time.hours,
                        minute: time.minutes,
                        second: time.seconds
                    });
                }
            }

            return undefined;
        },

        filterStartValue: function(operator, value, dataType, genericProperties) {
            var clearFilter = value == null && operator == null,
                isBooleanColumn = dataType === "boolean",
                isNumericColumn = dataType === "numeric",
                isDatetimeColumn = dataType === "datetime",
                isTimeColumn = dataType === "time";

            if (clearFilter) {
                return "";
            } else {
                if (_.isArray(value)) {
                    if (isDatetimeColumn) {
                        return jiveDataConverter.isoTimestampTojQueryUiTimestamp(value[0], genericProperties);
                    } else if (isTimeColumn) {
                        return jiveDataConverter.isoTimeTojQueryUiTime(value[0], genericProperties);
                    } else if (isNumericColumn) {
                        return numberUtil.formatNumber(value[0]);
                    } else {
                        return value[0];
                    }
                } else {
                    if (isBooleanColumn) {
                        return "";
                    } else if (isDatetimeColumn) {
                        return jiveDataConverter.isoTimestampTojQueryUiTimestamp(value, genericProperties);
                    } else if (isTimeColumn) {
                        return jiveDataConverter.isoTimeTojQueryUiTime(value, genericProperties);
                    } else if (isNumericColumn) {
                        return numberUtil.formatNumber(value);
                    } else {
                        return value;
                    }
                }
            }
        },

        filterEndValue: function(operator, value, dataType, genericProperties) {
            var clearFilter = value == null && operator == null,
                isNumericColumn = dataType === "numeric",
                isDatetimeColumn = dataType === "datetime",
                isTimeColumn = dataType === "time";

            if (clearFilter) {
                return "";
            } else {
                if (_.isArray(value)) {
                    if (isDatetimeColumn) {
                        return jiveDataConverter.isoTimestampTojQueryUiTimestamp(value[1], genericProperties);
                    } else if (isTimeColumn) {
                        return jiveDataConverter.isoTimeTojQueryUiTime(value[1], genericProperties);
                    } else if (isNumericColumn) {
                        return numberUtil.formatNumber(value[1]);
                    } else {
                        return value[1];
                    }
                } else {
                    return "";
                }
            }
        },

        dataTypeToSchemaFormat: {
            "Text": "string",
            "Numeric": "numeric",
            "Date": "datetime",
            "Boolean": "boolean",
            "Time": "time"
        },

        schemaFormatOperatorToFilterOperatorMap: (function() {
            var mapping = {};

            mapping["string"] = {};
            mapping["string"]["equal"] = "EQUALS";
            mapping["string"]["not_equal"] = "IS_NOT_EQUAL_TO";
            mapping["string"]["contain"] = "CONTAINS";
            mapping["string"]["not_contain"] = "DOES_NOT_CONTAIN";
            mapping["string"]["start_with"] = "STARTS_WITH";
            mapping["string"]["not_start_with"] = "DOES_NOT_START_WITH";
            mapping["string"]["end_with"] = "ENDS_WITH";
            mapping["string"]["not_end_with"] = "DOES_NOT_END_WITH";
            mapping["datetime"] = {};
            mapping["datetime"]["equal"] = "EQUALS";
            mapping["datetime"]["not_equal"] = "IS_NOT_EQUAL_TO";
            mapping["datetime"]["between"] = "IS_BETWEEN";
            mapping["datetime"]["not_between"] = "IS_NOT_BETWEEN";
            mapping["datetime"]["on_or_before"] = "IS_ON_OR_BEFORE";
            mapping["datetime"]["before"] = "IS_BEFORE";
            mapping["datetime"]["on_or_after"] = "IS_ON_OR_AFTER";
            mapping["datetime"]["after"] = "IS_AFTER";
            mapping["time"] = {};
            mapping["time"]["equal"] = "EQUALS";
            mapping["time"]["not_equal"] = "IS_NOT_EQUAL_TO";
            mapping["time"]["between"] = "IS_BETWEEN";
            mapping["time"]["not_between"] = "IS_NOT_BETWEEN";
            mapping["time"]["on_or_before"] = "IS_ON_OR_BEFORE";
            mapping["time"]["before"] = "IS_BEFORE";
            mapping["time"]["on_or_after"] = "IS_ON_OR_AFTER";
            mapping["time"]["after"] = "IS_AFTER";
            mapping["numeric"] = {};
            mapping["numeric"]["equal"] = "EQUALS";
            mapping["numeric"]["not_equal"] = "DOES_NOT_EQUAL";
            mapping["numeric"]["greater"] = "GREATER_THAN";
            mapping["numeric"]["greater_or_equal"] = "GREATER_THAN_EQUAL_TO";
            mapping["numeric"]["less"] = "LESS_THAN";
            mapping["numeric"]["less_or_equal"] = "LESS_THAN_EQUAL_TO";
            mapping["numeric"]["between"] = "IS_BETWEEN";
            mapping["numeric"]["not_between"] = "IS_NOT_BETWEEN";

            return mapping;
        })(),

        filterOperatorToSchemaFormatMap: (function() {
            var mapping = {};
            mapping["string"] = {};
            mapping["string"]["EQUALS"] = "equal";
            mapping["string"]["IS_NOT_EQUAL_TO"] = "not_equal";
            mapping["string"]["CONTAINS"] = "contain";
            mapping["string"]["DOES_NOT_CONTAIN"] = "not_contain";
            mapping["string"]["STARTS_WITH"] = "start_with";
            mapping["string"]["DOES_NOT_START_WITH"] = "not_start_with";
            mapping["string"]["ENDS_WITH"] = "end_with";
            mapping["string"]["DOES_NOT_END_WITH"] = "not_end_with";
            mapping["datetime"] = {};
            mapping["datetime"]["EQUALS"] = "equal";
            mapping["datetime"]["IS_NOT_EQUAL_TO"] = "not_equal";
            mapping["datetime"]["IS_BETWEEN"] = "between";
            mapping["datetime"]["IS_NOT_BETWEEN"] = "not_between";
            mapping["datetime"]["IS_ON_OR_BEFORE"] = "on_or_before";
            mapping["datetime"]["IS_BEFORE"] = "before";
            mapping["datetime"]["IS_ON_OR_AFTER"] = "on_or_after";
            mapping["datetime"]["IS_AFTER"] = "after";
            mapping["numeric"] = {};
            mapping["numeric"]["EQUALS"] = "equal";
            mapping["numeric"]["DOES_NOT_EQUAL"] = "not_equal";
            mapping["numeric"]["GREATER_THAN"] = "greater";
            mapping["numeric"]["GREATER_THAN_EQUAL_TO"] = "greater_or_equal";
            mapping["numeric"]["LESS_THAN"] = "less";
            mapping["numeric"]["LESS_THAN_EQUAL_TO"] = "less_or_equal";
            mapping["numeric"]["IS_BETWEEN"] = "between";
            mapping["numeric"]["IS_NOT_BETWEEN"] = "not_between";
            mapping["time"] = {};
            mapping["time"]["EQUALS"] = "equal";
            mapping["time"]["IS_NOT_EQUAL_TO"] = "not_equal";
            mapping["time"]["IS_BETWEEN"] = "between";
            mapping["time"]["IS_NOT_BETWEEN"] = "not_between";
            mapping["time"]["IS_ON_OR_BEFORE"] = "on_or_before";
            mapping["time"]["IS_BEFORE"] = "before";
            mapping["time"]["IS_ON_OR_AFTER"] = "on_or_after";
            mapping["time"]["IS_AFTER"] = "after";

            return mapping;
        })(),

        schemaFormatOperatorToFilterOperator: function(operator, value, dataType) {
            if (dataType !== "boolean") {
                return jiveDataConverter.schemaFormatOperatorToFilterOperatorMap[dataType][operator];
            } else {
                if (operator === "equal" && value === true) {
                    return "IS_TRUE";
                } else if (operator === "equal" && value === false) {
                    return "IS_FALSE";
                } else if (operator === "not_equal" && value === true) {
                    return "IS_NOT_TRUE";
                } else if (operator === "not_equal" && value === false) {
                    return "IS_NOT_FALSE";
                }
            }
        },

        operatorAndValueToSchemaFormat: function(operator, dataType, valueStart, valueEnd) {
            var response = {};

            if (dataType === "boolean") {
                if (operator === "IS_TRUE") {
                    response.operator = "equal";
                    response.value = true;
                } else if (operator === "IS_FALSE") {
                    response.operator = "equal";
                    response.value = false;
                } else if (operator === "IS_NOT_TRUE") {
                    response.operator = "not_equal";
                    response.value = true;
                } else if (operator === "IS_NOT_FALSE") {
                    response.operator = "not_equal";
                    response.value = false;
                }
            } else {
                response.operator = jiveDataConverter.filterOperatorToSchemaFormatMap[dataType][operator];

                if (dataType === "string") {
                    response.value = valueStart;
                } else if (dataType === "numeric") {
                    try {
                        if (response.operator === "between" || response.operator === "not_between") {
                            response.value = [numberUtil.parseNumber(valueStart), numberUtil.parseNumber(valueEnd)];
                        } else {
                            response.value = numberUtil.parseNumber(valueStart);
                        }
                    } catch(ex) {
                        throw new Error("Cannot convert filter value to number", ex);
                    }
                } else if (dataType === "datetime") {
                    if (response.operator === "between" || response.operator === "not_between") {
                        response.value = [
                            jiveDataConverter.jQueryUiTimestampToIsoTimestamp(valueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                            jiveDataConverter.jQueryUiTimestampToIsoTimestamp(valueEnd, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined)
                        ];
                    } else {
                        response.value = jiveDataConverter.jQueryUiTimestampToIsoTimestamp(valueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined);
                    }
                } else if (dataType === "time") {
                    if (response.operator === "between" || response.operator === "not_between") {
                        response.value = [
                            jiveDataConverter.jQueryUiTimeToIsoTime(valueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                            jiveDataConverter.jQueryUiTimeToIsoTime(valueEnd, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined)
                        ];
                    } else {
                        response.value = jiveDataConverter.jQueryUiTimeToIsoTime(valueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined);
                    }
                }
            }

            return response;
        }
    };

    return jiveDataConverter;
});