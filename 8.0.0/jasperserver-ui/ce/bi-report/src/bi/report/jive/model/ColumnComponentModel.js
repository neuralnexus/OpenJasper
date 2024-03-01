/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseComponentModel from './BaseComponentModel';
import FormatModel from './FormatModel';
import jiveTypes from '../enum/jiveTypes';
import $ from 'jquery';
import _ from 'underscore';
import ConditionCollection from '../collection/ConditionCollection';
import interactiveComponentTypes from '../enum/interactiveComponentTypes';
import reportEvents from '../../enum/reportEvents';
import jiveDataConverter from '../util/jiveDataConverter';

import logger from "js-sdk/src/common/logging/logger";

let localLogger = logger.register("ColumnComponentModel");

function sortingToSchemaFormat() {
    if (this.get('canSort')) {
        if (this.get('sortOrder')) {
            return { order: this.get('sortOrder') };
        } else {
            return {};
        }
    }
    return undefined;
}
function filteringToSchemaFormat() {
    if (this.get('canFilter')) {
        // checking whether filterValue is set, we need to prevent an updated value getting overwritten by the original filterData
        var filterValueInBetweenIsUnderfinedOrNull = _.isArray(this.get('filterValue')) && !_.every(this.get('filterValue'));
        if (_.isUndefined(this.get('filterValue')) || _.isNull(this.get('filterValue')) || filterValueInBetweenIsUnderfinedOrNull) {
            var fieldValueStart = this.get('filtering') ? this.get('filtering').filterData.fieldValueStart : undefined, fieldValueEnd = this.get('filtering') ? this.get('filtering').filterData.fieldValueEnd : undefined;    // TODO: optimize this
            // dirty hack to update date/time filter after we already have settings from table
            // TODO: optimize this
            // dirty hack to update date/time filter after we already have settings from table
            if (jiveDataConverter.dataTypeToSchemaFormat[this.get('dataType')] === 'datetime') {
                if ((this.get('filterOperator') === 'between' || this.get('filterOperator') === 'not_between') && fieldValueStart != null && fieldValueEnd != null) {
                    this.set('filterValue', [
                        jiveDataConverter.jQueryUiTimestampToIsoTimestamp(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                        jiveDataConverter.jQueryUiTimestampToIsoTimestamp(fieldValueEnd, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined)
                    ], { silent: true });
                } else if (fieldValueStart != null) {
                    this.set('filterValue', jiveDataConverter.jQueryUiTimestampToIsoTimestamp(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined), { silent: true });
                }
            } else if (jiveDataConverter.dataTypeToSchemaFormat[this.get('dataType')] === 'time') {
                if ((this.get('filterOperator') === 'between' || this.get('filterOperator') === 'not_between') && fieldValueStart != null && fieldValueEnd != null) {
                    this.set('filterValue', [
                        jiveDataConverter.jQueryUiTimeToIsoTime(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                        jiveDataConverter.jQueryUiTimeToIsoTime(fieldValueEnd, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined)
                    ], { silent: true });
                } else if (fieldValueStart != null) {
                    this.set('filterValue', jiveDataConverter.jQueryUiTimeToIsoTime(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined), { silent: true });
                }
            }
        }
        if (this.get('filterOperator') != null && this.get('filterValue') != null) {
            return {
                operator: this.get('filterOperator'),
                value: this.get('filterValue')
            };
        } else {
            return {};
        }
    }
    return undefined;
}
function onHeadingChange() {
    return {
        'actionName': 'editTextElement',
        'editTextElementData': {
            'applyTo': 'heading',
            'tableUuid': this.get('parentId'),
            'columnIndex': this.get('columnIndex'),
            'dataType': this.get('dataType'),
            'headingName': this.get('columnLabel'),
            'fontName': this.headingFormat.get('font').name,
            'fontSize': this.headingFormat.get('font').size,
            'fontBold': this.headingFormat.get('font').bold,
            'fontItalic': this.headingFormat.get('font').italic,
            'fontUnderline': this.headingFormat.get('font').underline,
            'fontColor': this.headingFormat.get('font').color,
            'fontHAlign': this.headingFormat.get('align').charAt(0).toUpperCase() + this.headingFormat.get('align').slice(1),
            'fontBackColor': this.headingFormat.get('backgroundColor') === 'transparent' ? '000000' : this.headingFormat.get('backgroundColor'),
            'mode': this.headingFormat.get('backgroundColor') === 'transparent' ? 'Transparent' : 'Opaque'
        }
    };
}
export default BaseComponentModel.extend({
    api: {
        sort: {},
        move: {},
        format: {},
        filter: {},
        hide: {},
        unhide: {},
        resize: {}
    },
    defaults: function () {
        return {
            canFilter: false,
            canFormatConditionally: false,
            canFormatHeading: false,
            canSort: false,
            clearData: {},
            columnIndex: 0,
            columnLabel: '',
            conditionalFormattingData: {},
            dataType: undefined,
            filterData: {},
            filtering: {},
            headerToolbar: {},
            headingsTabContent: {},
            id: null,
            parentId: null,
            proxySelector: null,
            selector: null,
            module: 'jive.interactive.column',
            type: jiveTypes.COLUMN,
            valuesTabContent: {},
            sortOrder: undefined,
            filterOperator: undefined,
            filterValue: undefined
        };
    },
    constructor: function () {
        this.detailsRowFormat = new FormatModel();
        this.conditions = new ConditionCollection();
        BaseComponentModel.prototype.constructor.apply(this, arguments);
    },
    initialize: function (config) {
        this.config = config;
        this.events = {
            ACTION_PERFORMED: 'action',
            BEFORE_ACTION_PERFORMED: 'beforeAction'
        };
        if (this.get('canFormatHeading')) {
            this.headingFormat = new FormatModel(this.get('headingsTabContent'), {
                parse: true,
                silent: true
            });
        }
        this.attachEvents();
    },
    attachEvents: function () {
        this.headingFormat && this.listenTo(this.headingFormat, 'change', function () {
            this.trigger('change:headingFormat', this);
        }, this);
        this.listenTo(this.detailsRowFormat, 'change', function () {
            this.trigger('change:detailsRowFormat', this);
        }, this);
        this.listenTo(this.conditions, 'reset', function () {
            this.trigger('change:conditions', this);
        }, this);
        this.on('change', function () {
            if (this.hasChanged('filterValue') || this.hasChanged('filterOperator')) {
                this.trigger('change:filter', this);
            }
        }, this);
        this.on('parentTableComponentAttached', function () {
            if (this.config.conditionalFormattingData) {
                this.conditions.dataType = jiveDataConverter.dataTypeToSchemaFormat[this.config.dataType];
                this.conditions.conditionPattern = this.config.conditionalFormattingData.conditionPattern;
                this.conditions.parent = this;
                this.conditions.reset(this.config.conditionalFormattingData.conditions, {
                    silent: true,
                    parse: true
                });
            }
        });
    },
    sort: function (parms) {
        var it = this, payload = { action: this.config.headerToolbar['sort' + parms.order + 'Btn'].sortData };
        payload.action.sortData.tableUuid = it.config.parentId;
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action);
    },
    move: function (parms) {
        var it = this, payload = {
            action: {
                actionName: 'move',
                moveColumnData: {
                    tableUuid: it.config.parentId,
                    columnToMoveIndex: it.config.columnIndex,
                    columnToMoveNewIndex: parms.index
                }
            }
        };
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action);
    },
    format: function (parms) {
        var it = this, payload = { action: parms };
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action, { showErrorDialog: true });
    },
    filter: function (parms) {
        var it = this, filterParms = $.extend({}, it.config.filtering.filterData, parms), payload = {
            action: {
                actionName: 'filter',
                filterData: filterParms
            }
        };
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action, { showErrorDialog: true });
    },
    hide: function () {
        var it = this, payload = {
            action: {
                actionName: 'hideUnhideColumns',
                columnData: {
                    tableUuid: it.config.parentId,
                    hide: true,
                    columnIndexes: [this.config.columnIndex]
                }
            }
        };
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action);
    },
    unhide: function (columnIds) {
        var it = this, payload = {
            action: {
                actionName: 'hideUnhideColumns',
                columnData: {
                    tableUuid: it.config.parentId,
                    hide: false,
                    columnIndexes: columnIds ? columnIds : [this.config.columnIndex]
                }
            }
        };
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action);
    },
    resize: function (parms) {
        var it = this, payload = {
            action: {
                actionName: 'resize',
                resizeColumnData: {
                    tableUuid: it.config.parentId,
                    columnIndex: this.config.columnIndex,
                    direction: 'right',
                    width: parms.width
                }
            }
        };
        it._notify({ name: it.events.BEFORE_ACTION_PERFORMED });
        it.trigger(reportEvents.ACTION, payload.action);
    },
    parse: function (response) {
        if (response.headerToolbar && response.headerToolbar.sortAscBtn && response.headerToolbar.sortDescBtn) {
            var ascBtnSortOrder = response.headerToolbar.sortAscBtn.sortData.sortData.sortOrder, dscBtnSortOrder = response.headerToolbar.sortDescBtn.sortData.sortData.sortOrder, sortOrder;
            if (ascBtnSortOrder === 'None') {
                sortOrder = 'asc';
            } else if (dscBtnSortOrder === 'None') {
                sortOrder = 'desc';
            }
            response.sortOrder = sortOrder;
        }
        if (response.filtering && response.filtering.filterData) {
            var dataType = jiveDataConverter.dataTypeToSchemaFormat[response.dataType], operator = response.filtering.filterData.filterTypeOperator, fieldValueStart = response.filtering.filterData.fieldValueStart, fieldValueEnd = response.filtering.filterData.fieldValueEnd, filterPattern = response.filtering.filterData.filterPattern;
            if (response.filtering.filterData.clearFilter || fieldValueStart == null && operator == null) {
                response.filterOperator = undefined;
                response.filterValue = undefined;
            } else {
                var obj = jiveDataConverter.operatorAndValueToSchemaFormat(operator, dataType, fieldValueStart, fieldValueEnd, filterPattern);
                response.filterOperator = obj.operator;
                response.filterValue = obj.value;
            }
        }
        if (response.headingsTabContent) {
            response.canFormatHeading = true;
        } else {
            response.canFormatHeading = false;
        }
        if (response.valuesTabContent) {
            this.detailsRowFormat.dataType = jiveDataConverter.dataTypeToSchemaFormat[response.dataType];
            this.detailsRowFormat.set(this.detailsRowFormat.parse(response.valuesTabContent), { silent: true });
        }
        if (response.columnLabel) {
            response.columnLabel = _.unescape(response.columnLabel);
        }
        return response;
    },
    actions: {
        'change:sortOrder': function () {
            var sortColumnName = this.get('headerToolbar').sortAscBtn.sortData.sortData.sortColumnName, sortColumnType = this.get('headerToolbar').sortAscBtn.sortData.sortData.sortColumnType, sortOrder;
            switch (this.get('sortOrder')) {
            case 'asc':
                sortOrder = 'Asc';
                break;
            case 'desc':
                sortOrder = 'Dsc';
                break;
            default:
                sortOrder = 'None';
                break;
            }
            return {
                'actionName': 'sort',
                'sortData': {
                    'sortColumnName': sortColumnName,
                    'sortColumnType': sortColumnType,
                    'sortOrder': sortOrder,
                    'tableUuid': this.get('parentId')
                }
            };
        },
        'change:filter': function () {
            var filterOperator = this.get('filterOperator');
            var filterValue = this.get('filterValue');
            var isClearFilter = filterValue == null && filterOperator == null;
            var filterData = this.get('filtering').filterData;
            var filterType = this.get('dataType');
            var filterValueDataType = jiveDataConverter.dataTypeToSchemaFormat[filterType];
            var genericProperties = this.parent && this.parent.config ? this.parent.config.genericProperties : undefined;
            var filterStartValue = jiveDataConverter.filterStartValue(filterOperator, filterValue, filterValueDataType, genericProperties);
            var filterEndValue = jiveDataConverter.filterEndValue(filterOperator, filterValue, filterValueDataType, genericProperties);
            var filterTypeOperator = jiveDataConverter.schemaFormatOperatorToFilterOperator(filterOperator, filterValue, filterValueDataType);
            return {
                'actionName': 'filter',
                'filterData': {
                    'fieldValueStart': filterStartValue,
                    'fieldValueEnd': filterEndValue,
                    'filterTypeOperator': filterTypeOperator,
                    'clearFilter': isClearFilter,
                    'filterPattern': filterData.filterPattern,
                    'fieldName': filterData.fieldName,
                    'localeCode': filterData.localeCode,
                    'timeZoneId': filterData.timeZoneId,
                    'isField': filterData.isField,
                    'tableUuid': this.get('parentId'),
                    'filterType': filterType
                }
            };
        },
        'change:headingFormat': onHeadingChange,
        'change:columnLabel': onHeadingChange,
        'change:detailsRowFormat': function () {
            return {
                'actionName': 'editTextElement',
                'editTextElementData': {
                    'applyTo': 'detailrows',
                    'tableUuid': this.get('parentId'),
                    'columnIndex': this.get('columnIndex'),
                    'dataType': this.get('dataType'),
                    'fontName': this.detailsRowFormat.get('font').name,
                    'fontSize': this.detailsRowFormat.get('font').size,
                    'fontBold': this.detailsRowFormat.get('font').bold,
                    'fontItalic': this.detailsRowFormat.get('font').italic,
                    'fontUnderline': this.detailsRowFormat.get('font').underline,
                    'fontColor': this.detailsRowFormat.get('font').color,
                    'formatPattern': this.detailsRowFormat.toJiveFormat(),
                    'fontHAlign': this.detailsRowFormat.get('align').charAt(0).toUpperCase() + this.detailsRowFormat.get('align').slice(1),
                    'fontBackColor': this.detailsRowFormat.get('backgroundColor') === 'transparent' ? '000000' : this.detailsRowFormat.get('backgroundColor'),
                    'mode': this.detailsRowFormat.get('backgroundColor') === 'transparent' ? 'Transparent' : 'Opaque'
                }
            };
        },
        'change:conditions': function () {
            var genericProperties = this.parent && this.parent.config ? this.parent.config.genericProperties : undefined;
            return {
                'actionName': 'conditionalFormatting',
                'conditionalFormattingData': {
                    'applyTo': 'detailrows',
                    'tableUuid': this.get('parentId'),
                    'columnIndex': this.get('columnIndex'),
                    'conditionPattern': this.get('conditionalFormattingData').conditionPattern,
                    'conditionType': this.get('conditionalFormattingData').conditionType,
                    'conditions': this.conditions.map(function (conditionModel) {
                        return conditionModel.toJiveFormat(genericProperties);
                    })
                }
            };
        }
    },
    toReportComponentObject: function () {
        var columns = this.parent.get('allColumnsData');
        if (columns) {
            var thisColumnProps = _.findWhere(columns, { uuid: this.get('id') });
            if (thisColumnProps) {
                if (!thisColumnProps.interactive) {
                    return undefined;
                } else {
                    return {
                        id: this.get('id'),
                        componentType: interactiveComponentTypes.TABLE_COLUMN,
                        dataType: jiveDataConverter.dataTypeToSchemaFormat[this.get('dataType')],
                        label: this.get('columnLabel'),
                        name: this.get('name'),
                        sort: sortingToSchemaFormat.call(this),
                        filter: filteringToSchemaFormat.call(this),
                        headingFormat: this.get('canFormatHeading') ? this.headingFormat.toJSON() : undefined,
                        detailsRowFormat: this.detailsRowFormat.toJSON(),
                        conditions: this.get('canFormatConditionally') ? this.conditions.toJSON() : undefined
                    };
                }
            }
        }
        return undefined;
    },
    updateFromReportComponentObject: function (obj) {
        var setterObj = {};
        if (obj.sort) {
            if (this.get('canSort')) {
                if ('order' in obj.sort) {
                    setterObj.sortOrder = obj.sort.order;
                } else if (_.keys(obj.sort).length === 0) {
                    setterObj.sortOrder = undefined;
                }
            } else {
                localLogger.warn('The column cannot be sorted!');
            }
        }
        if (obj.filter) {
            if (this.get('canFilter')) {
                if (_.keys(obj.filter).length === 0 || obj.filter.operator == null) {
                    setterObj.filterOperator = undefined;
                    setterObj.filterValue = undefined;
                } else {
                    setterObj.filterOperator = obj.filter.operator;
                    setterObj.filterValue = obj.filter.value;
                }
            } else {
                localLogger.warn('The column cannot be filtered!');
            }
        }
        if (obj.label != null) {
            if (this.get('canFormatHeading')) {
                setterObj.columnLabel = obj.label;
            } else {
                localLogger.warn('The column label cannot be modified!');
            }
        }
        if (obj.headingFormat) {
            if (this.get('canFormatHeading')) {
                obj.headingFormat.font = _.extend({}, this.headingFormat.get('font'), obj.headingFormat.font || {});
                if (obj.headingFormat.backgroundColor && obj.headingFormat.backgroundColor !== 'transparent') {
                    obj.headingFormat.backgroundColor = obj.headingFormat.backgroundColor.toUpperCase();
                }
                if (obj.headingFormat.font && obj.headingFormat.font.color) {
                    obj.headingFormat.font.color = obj.headingFormat.font.color.toUpperCase();
                }
                this.headingFormat.set(obj.headingFormat);
            } else {
                localLogger.warn('The column heading cannot be modified!');
            }
        }
        if (obj.detailsRowFormat) {
            obj.detailsRowFormat.font = _.extend({}, this.detailsRowFormat.get('font'), obj.detailsRowFormat.font || {});
            if (_.isObject(this.detailsRowFormat.get('pattern'))) {
                if (!('Numeric' === this.get('dataType') && jiveDataConverter.DURATION_PATTERN === obj.detailsRowFormat.pattern)) {
                    obj.detailsRowFormat.pattern = _.extend({}, this.detailsRowFormat.get('pattern'), obj.detailsRowFormat.pattern || {});
                }
            }
            if (obj.detailsRowFormat.backgroundColor && obj.detailsRowFormat.backgroundColor !== 'transparent') {
                obj.detailsRowFormat.backgroundColor = obj.detailsRowFormat.backgroundColor.toUpperCase();
            }
            if (obj.detailsRowFormat.font && obj.detailsRowFormat.font.color) {
                obj.detailsRowFormat.font.color = obj.detailsRowFormat.font.color.toUpperCase();
            }
            this.detailsRowFormat.set(obj.detailsRowFormat);
        }
        if (obj.conditions) {
            if (this.get('canFormatConditionally')) {
                if (!(obj.conditions.length === 0 && this.conditions.length === 0)) {
                    this.conditions.reset(obj.conditions);
                }
            } else {
                localLogger.warn('The column cannot be formatted conditionally!');
            }
        }
        this.set(setterObj);
    }
});