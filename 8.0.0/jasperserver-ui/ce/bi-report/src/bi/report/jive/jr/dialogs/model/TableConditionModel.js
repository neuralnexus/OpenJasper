/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Epoxy from 'backbone.epoxy';
import _ from 'underscore';
import jiveDataConverter from '../../../util/jiveDataConverter';
import NumberUtils from 'js-sdk/src/common/util/parse/NumberUtils';

var numberUtil = new NumberUtils();

function onOperatorChange() {
    var currentValue = this.get('value'),
        operator = this.get('operator'),
        isMultiValue,
        isNullCheck;

    isMultiValue = operator && operator.toLowerCase().indexOf('between') != -1;
    isNullCheck = operator && operator.toLowerCase().indexOf('null') !== -1;

    if (isMultiValue === true) {
        if (!_.isArray(currentValue)) {
            this.set({ value: [currentValue] }, { silent: true });
        }
    } else if (isMultiValue === false) {
        if (_.isArray(currentValue)) {
            this.set({ value: currentValue[0] }, { silent: true });
        }
    }

    if (isNullCheck) {
        this.set({
            value: ''
        });
    }
}
function validateValue(dataType, conditionPattern, genericProperties, value, attr) {
    var error, source, tokens;
    if (value === null || value === undefined) {
        error = 'No value supplied!';
    } else if ('datetime' === dataType) {
        try {
            if (jiveDataConverter.jQueryUiTimestampToIsoTimestamp(value, genericProperties) === undefined) {
                error = 'Unparsable date: ' + value + ' !';
            }
        } catch (ex) {
            error = ex;
        }
    } else if ('time' === dataType) {
        try {
            if (jiveDataConverter.jQueryUiTimeToIsoTime(value, genericProperties) === undefined) {
                error = 'Unparsable time: ' + value + ' !';
            }
        } catch (ex) {
            error = ex;
        }
    } else if ('numeric' === dataType) {
        if (jiveDataConverter.DURATION_PATTERN === conditionPattern) {
            source = _.trim(value);
            if (source.length > 0) {
                tokens = source.split(':');
                if (tokens.length > 3) {
                    error = 'Unparsable duration: ' + value + ' !';
                } else {
                    if (numberUtil.parseNumber(tokens[0]) === undefined || tokens.length > 1 && (numberUtil.parseNumber(tokens[1]) === undefined || numberUtil.parseNumber(tokens[1]) > 59) || tokens.length > 2 && (numberUtil.parseNumber(tokens[2]) === undefined || numberUtil.parseNumber(tokens[2]) > 59)) {
                        error = 'Unparsable duration: ' + value + ' !';
                    }
                }
            } else {
                error = 'Empty value supplied!';
            }
        }
    }
    this.trigger('validate:' + attr, this, attr, error);
    return error;
}
export default Epoxy.Model.extend({
    defaults: function () {
        return {
            operator: null,
            value: null,
            backgroundColor: null,
            font: {
                bold: null,
                italic: null,
                underline: null,
                color: null
            }
        };
    },
    computeds: function () {
        var ctds = {};    // dinamically create computeds for each font property
        // dinamically create computeds for each font property
        _.each(this.defaults().font, function (val, key) {
            ctds['font' + key.charAt(0).toUpperCase() + key.substring(1, key.length)] = {
                deps: ['font'],
                get: function (font) {
                    return font[key];
                },
                set: function (value) {
                    this.modifyObject('font', key, value);
                    return value;
                }
            };
        });
        ctds.isMultiValueOperator = function () {
            var op = this.get('operator');
            return op && op.toLowerCase().indexOf('between') != -1 ? true : false;
        };
        ctds.isNotNullCheckOperator = function() {
            var op = this.get('operator'),
                isNullCheck = op && op.toLowerCase().indexOf('null') !== -1 ? true : false;
            return !isNullCheck;
        };
        return ctds;
    },
    initialize: function () {
        this.on('change:operator', _.bind(onOperatorChange, this));
        Epoxy.Model.prototype.initialize.apply(this, arguments);
    },
    reset: function () {
        this.set(this.defaults(), { silent: true });
        return this;
    },
    validate: function (attributes, options) {
        var val = attributes.value,
            operator = attributes.operator,
            dataType = jiveDataConverter.dataTypeToSchemaFormat[this.collection.dataType],
            conditionPattern = this.collection.conditionPattern,
            genericProperties = this.collection.genericProperties,
            valueStartValidation,
            valueEndValidation;

        if (operator !== 'IS_NULL' && operator !== 'IS_NOT_NULL') {
            // for boolean conditions, the value is not set, therefore we need to set it to something not null
            if (operator === 'IS_TRUE' || operator === 'IS_NOT_FALSE' || operator === 'IS_NOT_TRUE' || operator === 'IS_FALSE') {
                val = 'true/false';
            }
            if (val === null || val === undefined) {
                if (this.get('isMultiValueOperator')) {
                    val = [
                        null,
                        null
                    ];
                }
            }
            if (_.isArray(val)) {
                valueStartValidation = validateValue.call(this, dataType, conditionPattern, genericProperties, val[0], 'valueStart');
                valueEndValidation = validateValue.call(this, dataType, conditionPattern, genericProperties, val[1], 'valueEnd');
                if (valueStartValidation || valueEndValidation) {
                    return 'Invalid value supplied!';
                }
            } else if (validateValue.call(this, dataType, conditionPattern, genericProperties, val, 'valueStart')) {
                return 'Invalid value supplied!';
            }
        }
    },
    remove: function () {
    }
});