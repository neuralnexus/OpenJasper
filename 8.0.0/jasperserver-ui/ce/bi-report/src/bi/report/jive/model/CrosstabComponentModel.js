/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
import interactiveComponentTypes from '../enum/interactiveComponentTypes';
import _ from 'underscore';
import reportEvents from '../../enum/reportEvents';

export default BaseComponentModel.extend({
    defaults: function () {
        return {
            type: jiveTypes.CROSSTAB,
            module: 'jive.crosstab',
            uimodule: 'jive.crosstab.interactive',
            id: undefined,
            crosstabId: undefined,
            fragmentId: undefined,
            startColumnIndex: 0,
            rowGroups: [],
            dataColumns: []
        };
    },
    actions: {
        'change:order': function (xTabElement) {
            var order = null;
            xTabElement.sort.order === 'asc' && (order = 'ASCENDING');
            xTabElement.sort.order === 'desc' && (order = 'DESCENDING');
            return xTabElement.componentType === interactiveComponentTypes.CROSSTAB_COLUMN ? {
                'actionName': 'sortXTabByColumn',
                'sortData': {
                    'crosstabId': this.attributes.crosstabId,
                    'order': order,
                    'measureIndex': xTabElement.sortMeasureIndex,
                    'columnValues': xTabElement.columnValues
                }
            } : {
                'actionName': 'sortXTabRowGroup',
                'sortData': {
                    'crosstabId': this.attributes.crosstabId,
                    'order': order || 'NONE',
                    'groupIndex': xTabElement.groupIndex
                }
            };
        }
    },
    initialize: function (o) {
        this.config = {};
        _.extend(this.config, o);
        this.events = {
            ACTION_PERFORMED: 'action',
            BEFORE_ACTION_PERFORMED: 'beforeAction'
        };
    },
    getId: function () {
        return this.config.id;
    },
    getCrosstabId: function () {
        return this.config.crosstabId;
    },
    getFragmentId: function () {
        return this.config.fragmentId;
    },
    sortRowGroup: function (groupIndex, order) {
        var self = this, payload = {
            action: {
                'actionName': 'sortXTabRowGroup',
                'sortData': {
                    'crosstabId': this.getCrosstabId(),
                    'order': order,
                    'groupIndex': groupIndex
                }
            }
        };
        self._notify({ name: self.events.BEFORE_ACTION_PERFORMED });
        self.trigger(reportEvents.ACTION, payload.action);
    },
    isDataColumnSortable: function (columnIndex) {
        var dataColumn = this.config.dataColumns[columnIndex - this.config.startColumnIndex];
        return typeof dataColumn.sortMeasureIndex == 'number';
    },
    getColumnOrder: function (columnIndex) {
        return this.config.dataColumns[columnIndex - this.config.startColumnIndex].order;
    },
    sortByDataColumn: function (columnIndex, order) {
        var self = this, dataColumn = this.config.dataColumns[columnIndex - this.config.startColumnIndex], payload = {
            action: {
                'actionName': 'sortXTabByColumn',
                'sortData': {
                    'crosstabId': this.getCrosstabId(),
                    'order': order,
                    'measureIndex': dataColumn.sortMeasureIndex,
                    'columnValues': dataColumn.columnValues
                }
            }
        };
        self._notify({ name: self.events.BEFORE_ACTION_PERFORMED });
        self.trigger(reportEvents.ACTION, payload.action);
    },
    updateFromReportComponentObject: function (obj) {
        var setterObj = {};
        if ('order' in obj.sort) {
            setterObj.order = obj.sort.order;
        } else if (_.keys(obj.sort).length === 0) {
            setterObj.order = null;
        }
        this.set(setterObj, obj);
    },
    toReportComponentObject: function () {
        return this.getDataColumns(this.attributes.dataColumns).concat(this.getRowGroups(this.attributes.rowGroups));
    },
    getDataColumns: function () {
        var cloneAndCreateIdField = function (columns) {
                var id = {
                    id: this.getId() + '/dataColumns',
                    componentType: interactiveComponentTypes.CROSSTAB_COLUMN
                };
                return _.map(columns, function (column) {
                    return _.extend({}, id, column);
                });
            }, createIdAccordingToStructure = _.bind(generateIdsForColumns, this, 0), filterNotSortable = function (columns) {
                return _.filter(columns, function (column) {
                    return typeof column.sortMeasureIndex == 'number';
                });
            }, transformAccordingToTable = function (columns) {
                _.each(columns, function (column) {
                    column.sort = {};
                    column.order === 'ASCENDING' && (column.sort.order = 'asc');
                    column.order === 'DESCENDING' && (column.sort.order = 'desc');
                    delete column.order;
                });
                return columns;
            };
        return _.compose(transformAccordingToTable, filterNotSortable, createIdAccordingToStructure, cloneAndCreateIdField);
    }(),
    getRowGroups: function () {
        var cloneAndCreateIdField = function (rows) {
                var id = {
                    id: this.getId() + '/rowGroups',
                    componentType: interactiveComponentTypes.CROSSTAB_ROW
                };
                return _.map(rows, function (row) {
                    return _.extend({}, id, row);
                });
            }, createIdAccordingToStructure = generateIdsForRows, filterNotSortable = function (rows) {
                return _.filter(rows, function (row) {
                    return row.sortable;
                });
            }, transformAccordingToTable = function (rows) {
                _.each(rows, function (row) {
                    row.sort = {};
                    row.order === 'ASCENDING' && (row.sort.order = 'asc');
                    row.order === 'DESCENDING' && (row.sort.order = 'desc');
                    delete row.order;
                    delete row.sortable;
                });
                return rows;
            };
        return _.compose(transformAccordingToTable, filterNotSortable, createIdAccordingToStructure, cloneAndCreateIdField);
    }()
});
function generateIdsForColumns(index, columns) {
    var level = {}, subArr, name = 0;
    if (!columns.length || columns[0].columnValues && columns[0].columnValues.length > index) {
        for (var i = 0, l = columns.length; i < l; i++) {
            subArr = level[columns[i].columnValues[index].value] || (level[columns[i].columnValues[index].value] = []);
            subArr.push(columns[i]);
        }
        for (var key in level) {
            subArr = level[key];
            for (var i = 0, l = subArr.length; i < l; i++) {
                subArr[i].id += '/' + name;
            }
            generateIdsForColumns(index + 1, level[key]);
            name++;
        }
    }
    return columns;
}
function generateIdsForRows(rows) {
    for (var i = 0, l = rows.length; i < l; i++) {
        rows[i].groupIndex = i;
        rows[i].id += '/' + i;
    }
    return rows;
}