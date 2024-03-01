/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

import $ from 'jquery';
import _ from 'underscore';
import dateUtil from 'js-sdk/src/common/util/parse/date';
import timeUtil from 'js-sdk/src/common/util/parse/time';
import BiComponentError from 'js-sdk/src/common/bi/error/BiComponentError';
import validationCodes from "./validationCodes";

import 'js-sdk/src/common/extension/jQueryTimepickerExtension';

var jiveDataConverter = {

    DURATION_PATTERN: "[h]:mm:ss",

    jQueryUiTimestampToIsoTimestamp: function(val, genericProperties) {
        if (val && genericProperties && genericProperties.calendarPatterns) {
            var datePattern = genericProperties.calendarPatterns.date,
                timePattern = genericProperties.calendarPatterns.time;

            try {
                return dateUtil.dateObjectToIso8601Timestamp($.datepicker.parseDateTime(datePattern, timePattern, val));
            } catch(ex) {
                throw new BiComponentError(validationCodes.INVALID_TIMESTAMP, ex, { value: val });
            }
        }

        return undefined;
    },

    jQueryUiTimeToIsoTime: function(val, genericProperties) {
        if (val && genericProperties && genericProperties.calendarPatterns) {
            var timePattern = genericProperties.calendarPatterns.time;

            try {
                var parsedTimeObj = $.datepicker.parseTime(timePattern, val);

                if (parsedTimeObj) {
                    return timeUtil.timeToIso8061Time(parsedTimeObj.hour, parsedTimeObj.minute, parsedTimeObj.second);
                }
            } catch(ex) {
                throw new BiComponentError(validationCodes.INVALID_TIME, ex, { value: val });
            }
        }

        return undefined;
    },

    isoTimestampTojQueryUiTimestamp: function(val, genericProperties) {
        if (val && genericProperties && genericProperties.calendarPatterns) {
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
        if (val && genericProperties && genericProperties.calendarPatterns) {
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
            isNullCheckOperator = operator != null && operator.indexOf("null") !== -1,
            isBooleanColumn = dataType === "boolean",
            isDatetimeColumn = dataType === "datetime",
            isTimeColumn = dataType === "time";

        if (clearFilter || isNullCheckOperator) {
            return "";
        } else {
            if (_.isArray(value)) {
                if (isDatetimeColumn) {
                    return jiveDataConverter.isoTimestampTojQueryUiTimestamp(value[0], genericProperties);
                } else if (isTimeColumn) {
                    return jiveDataConverter.isoTimeTojQueryUiTime(value[0], genericProperties);
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
                } else {
                    return value;
                }
            }
        }
    },

    filterEndValue: function(operator, value, dataType, genericProperties) {
        var clearFilter = value == null && operator == null,
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
        mapping["string"]["is_null"] = "IS_NULL";
        mapping["string"]["is_not_null"] = "IS_NOT_NULL";
        mapping["datetime"] = {};
        mapping["datetime"]["equal"] = "EQUALS";
        mapping["datetime"]["not_equal"] = "IS_NOT_EQUAL_TO";
        mapping["datetime"]["between"] = "IS_BETWEEN";
        mapping["datetime"]["not_between"] = "IS_NOT_BETWEEN";
        mapping["datetime"]["on_or_before"] = "IS_ON_OR_BEFORE";
        mapping["datetime"]["before"] = "IS_BEFORE";
        mapping["datetime"]["on_or_after"] = "IS_ON_OR_AFTER";
        mapping["datetime"]["after"] = "IS_AFTER";
        mapping["datetime"]["is_null"] = "IS_NULL";
        mapping["datetime"]["is_not_null"] = "IS_NOT_NULL";
        mapping["time"] = {};
        mapping["time"]["equal"] = "EQUALS";
        mapping["time"]["not_equal"] = "IS_NOT_EQUAL_TO";
        mapping["time"]["between"] = "IS_BETWEEN";
        mapping["time"]["not_between"] = "IS_NOT_BETWEEN";
        mapping["time"]["on_or_before"] = "IS_ON_OR_BEFORE";
        mapping["time"]["before"] = "IS_BEFORE";
        mapping["time"]["on_or_after"] = "IS_ON_OR_AFTER";
        mapping["time"]["after"] = "IS_AFTER";
        mapping["time"]["is_null"] = "IS_NULL";
        mapping["time"]["is_not_null"] = "IS_NOT_NULL";
        mapping["numeric"] = {};
        mapping["numeric"]["equal"] = "EQUALS";
        mapping["numeric"]["not_equal"] = "DOES_NOT_EQUAL";
        mapping["numeric"]["greater"] = "GREATER_THAN";
        mapping["numeric"]["greater_or_equal"] = "GREATER_THAN_EQUAL_TO";
        mapping["numeric"]["less"] = "LESS_THAN";
        mapping["numeric"]["less_or_equal"] = "LESS_THAN_EQUAL_TO";
        mapping["numeric"]["between"] = "IS_BETWEEN";
        mapping["numeric"]["not_between"] = "IS_NOT_BETWEEN";
        mapping["numeric"]["is_null"] = "IS_NULL";
        mapping["numeric"]["is_not_null"] = "IS_NOT_NULL";

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
        mapping["string"]["IS_NULL"] = "is_null";
        mapping["string"]["IS_NOT_NULL"] = "is_not_null";
        mapping["datetime"] = {};
        mapping["datetime"]["EQUALS"] = "equal";
        mapping["datetime"]["IS_NOT_EQUAL_TO"] = "not_equal";
        mapping["datetime"]["IS_BETWEEN"] = "between";
        mapping["datetime"]["IS_NOT_BETWEEN"] = "not_between";
        mapping["datetime"]["IS_ON_OR_BEFORE"] = "on_or_before";
        mapping["datetime"]["IS_BEFORE"] = "before";
        mapping["datetime"]["IS_ON_OR_AFTER"] = "on_or_after";
        mapping["datetime"]["IS_AFTER"] = "after";
        mapping["datetime"]["IS_NULL"] = "is_null";
        mapping["datetime"]["IS_NOT_NULL"] = "is_not_null";
        mapping["numeric"] = {};
        mapping["numeric"]["EQUALS"] = "equal";
        mapping["numeric"]["DOES_NOT_EQUAL"] = "not_equal";
        mapping["numeric"]["GREATER_THAN"] = "greater";
        mapping["numeric"]["GREATER_THAN_EQUAL_TO"] = "greater_or_equal";
        mapping["numeric"]["LESS_THAN"] = "less";
        mapping["numeric"]["LESS_THAN_EQUAL_TO"] = "less_or_equal";
        mapping["numeric"]["IS_BETWEEN"] = "between";
        mapping["numeric"]["IS_NOT_BETWEEN"] = "not_between";
        mapping["numeric"]["IS_NULL"] = "is_null";
        mapping["numeric"]["IS_NOT_NULL"] = "is_not_null";
        mapping["time"] = {};
        mapping["time"]["EQUALS"] = "equal";
        mapping["time"]["IS_NOT_EQUAL_TO"] = "not_equal";
        mapping["time"]["IS_BETWEEN"] = "between";
        mapping["time"]["IS_NOT_BETWEEN"] = "not_between";
        mapping["time"]["IS_ON_OR_BEFORE"] = "on_or_before";
        mapping["time"]["IS_BEFORE"] = "before";
        mapping["time"]["IS_ON_OR_AFTER"] = "on_or_after";
        mapping["time"]["IS_AFTER"] = "after";
        mapping["time"]["IS_NULL"] = "is_null";
        mapping["time"]["IS_NOT_NULL"] = "is_not_null";

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
            } else if (operator === "is_null") {
                return "IS_NULL";
            } else if (operator === "is_not_null") {
                return "IS_NOT_NULL";
            }
        }
    },

    operatorAndValueToSchemaFormat: function(operator, dataType, valueStart, valueEnd, pattern) {
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
            } else if (operator === "IS_NULL") {
                response.operator = "is_null";
                response.value = "";
            } else if (operator === "IS_NOT_NULL") {
                response.operator = "is_not_null";
                response.value = "";
            }
        } else {
            response.operator = jiveDataConverter.filterOperatorToSchemaFormatMap[dataType][operator];
            var isRange = response.operator === "between" || response.operator === "not_between";
            var isNullCheck = response.operator === "is_null" || response.operator === "is_not_null";

            if (dataType === "string") {
                response.value = valueStart == null ? '' : valueStart;
            } else if (dataType === "numeric") {
                if (isRange) {
                    response.value = [valueStart, valueEnd]
                } else {
                    response.value = valueStart;
                }
            } else {
                var genericProperties = this.parent && this.parent.config ? this.parent.config.genericProperties : undefined;

                if (dataType === "datetime") {
                    if (isRange) {
                        response.value = [
                            jiveDataConverter.jQueryUiTimestampToIsoTimestamp(valueStart, genericProperties),
                            jiveDataConverter.jQueryUiTimestampToIsoTimestamp(valueEnd, genericProperties)
                        ];
                    } else {
                        response.value = isNullCheck ?
                            valueStart :
                            jiveDataConverter.jQueryUiTimestampToIsoTimestamp(valueStart, genericProperties);
                    }
                } else if (dataType === "time") {
                    if (isRange) {
                        response.value = [
                            jiveDataConverter.jQueryUiTimeToIsoTime(valueStart, genericProperties),
                            jiveDataConverter.jQueryUiTimeToIsoTime(valueEnd, genericProperties)
                        ];
                    } else {
                        response.value = isNullCheck ?
                            valueStart :
                            jiveDataConverter.jQueryUiTimeToIsoTime(valueStart, genericProperties);
                    }
                }
            }
        }

        return response;
    }
};

export default jiveDataConverter;
