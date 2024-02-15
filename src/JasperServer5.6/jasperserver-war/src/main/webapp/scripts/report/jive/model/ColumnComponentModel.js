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
 * @author Igor Nesterenko
 * @version $Id: ColumnComponentModel.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {

    var BaseComponentModel = require("./BaseComponentModel"),
        FormatModel = require("./FormatModel"),
        jiveTypes = require("../enum/jiveTypes"),
        $ = require("jquery"),
        _ = require("underscore"),
        ConditionCollection = require("../collection/ConditionCollection"),
        interactiveComponentTypes = require("../enum/interactiveComponentTypes"),
        reportEvents = require("../../enum/reportEvents"),
        jiveDataConverter = require("../util/jiveDataConverter");

    function sortingToSchemaFormat() {
        if (this.get("canSort")) {
            if (this.get("sortOrder")) {
                return { order: this.get("sortOrder") }
            } else {
                return {};
            }
        }

        return undefined;
    }

    function filteringToSchemaFormat() {
        if (this.get("canFilter")) {
            var fieldValueStart = this.get("filtering") ? this.get("filtering").filterData.fieldValueStart : undefined,
                fieldValueEnd = this.get("filtering") ? this.get("filtering").filterData.fieldValueEnd : undefined;

            // TODO: optimize this

            // dirty hack to update date/time filter after we already have settings from table
            if (jiveDataConverter.dataTypeToSchemaFormat[this.get("dataType")] === "datetime") {
                if ((this.get("filterOperator") === "between" || this.get("filterOperator") === "not_between")
                    && fieldValueStart != null && fieldValueEnd != null) {
                    this.set("filterValue", [
                        jiveDataConverter.jQueryUiTimestampToIsoTimestamp(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                        jiveDataConverter.jQueryUiTimestampToIsoTimestamp(fieldValueEnd, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined)
                    ], { silent: true });
                } else if (fieldValueStart != null) {
                    this.set("filterValue", jiveDataConverter.jQueryUiTimestampToIsoTimestamp(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined), { silent: true });
                }
            } else if (jiveDataConverter.dataTypeToSchemaFormat[this.get("dataType")] === "time") {
                    if ((this.get("filterOperator") === "between" || this.get("filterOperator") === "not_between")
                        && fieldValueStart != null && fieldValueEnd != null) {
                        this.set("filterValue", [
                            jiveDataConverter.jQueryUiTimeToIsoTime(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                            jiveDataConverter.jQueryUiTimeToIsoTime(fieldValueEnd, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined)
                        ], { silent: true });
                    } else if (fieldValueStart != null) {
                        this.set("filterValue", jiveDataConverter.jQueryUiTimeToIsoTime(fieldValueStart, this.parent && this.parent.config ? this.parent.config.genericProperties : undefined), { silent: true });
                    }
                }


            if (this.get("filterOperator") != null && this.get("filterValue") != null) {
                return {
                    operator: this.get("filterOperator"),
                    value: this.get("filterValue")
                };
            } else {
                return {};
            }

        }

        return undefined;
    }

    function onHeadingChange() {
        return {
            "actionName":"editTextElement",
            "editTextElementData": {
                "applyTo": "heading",
                "tableUuid": this.get("parentId"),
                "columnIndex": this.get("columnIndex"),
                "dataType": this.get("dataType"),
                "headingName": this.get("columnLabel"),

                "fontName": this.headingFormat.get("font").name,
                "fontSize": this.headingFormat.get("font").size + "",
                "fontBold": this.headingFormat.get("font").bold,
                "fontItalic": this.headingFormat.get("font").italic,
                "fontUnderline": this.headingFormat.get("font").underline,
                "fontColor": this.headingFormat.get("font").color,

                "fontHAlign": (this.headingFormat.get("align").charAt(0).toUpperCase() + this.headingFormat.get("align").slice(1)),
                "fontBackColor": this.headingFormat.get("backgroundColor") === "transparent" ? "000000" : this.headingFormat.get("backgroundColor"),
                "mode": this.headingFormat.get("backgroundColor") === "transparent" ? "Transparent" : "Opaque"
            }
        }
    }

    return BaseComponentModel.extend({

        api : {
            sort: {},
            move: {},
            format: {},
            filter: {},
            hide: {},
            unhide: {},
            resize: {}
        },

        defaults: function() {
            return {
                canFilter: false,
                canFormatConditionally: false,
                canSort: false,
                clearData: {},
                columnIndex: 0,
                columnLabel: "",
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
                module: "jive.interactive.column",
                type: jiveTypes.COLUMN,
                valuesTabContent: {},

                sortOrder: undefined,
                filterOperator: undefined,
                filterValue: undefined
            };
        },

        constructor: function() {
            this.headingFormat = new FormatModel();
            this.detailsRowFormat = new FormatModel();
            this.conditions = new ConditionCollection();

            BaseComponentModel.prototype.constructor.apply(this, arguments);
        },

        initialize: function(config){
            this.config = config;

            this.events = {
                ACTION_PERFORMED: "action",
                BEFORE_ACTION_PERFORMED: "beforeAction"
            };

            this.attachEvents();
        },

        attachEvents: function() {
            this.listenTo(this.headingFormat, "change", function() {
                this.trigger("change:headingFormat", this);
            }, this);

            this.listenTo(this.detailsRowFormat, "change", function() {
                this.trigger("change:detailsRowFormat", this);
            }, this);

            this.listenTo(this.conditions, "reset", function() {
                this.trigger("change:conditions", this);
            }, this);

            this.on("change", function() {
                if (this.hasChanged("filterValue") || this.hasChanged("filterOperator")) {
                    this.trigger("change:filter", this);
                }
            }, this);
        },

        sort: function(parms) {
            var it = this,
                payload = {
                    action: this.config.headerToolbar['sort' + parms.order + 'Btn'].sortData
                };
            payload.action.sortData.tableUuid = it.config.parentId;
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },
        move: function(parms) {
            var it = this,
                payload = {
                    action: {
                        actionName: 'move',
                        moveColumnData: {
                            tableUuid: it.config.parentId,
                            columnToMoveIndex: it.config.columnIndex,
                            columnToMoveNewIndex: parms.index
                        }
                    }
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },
        format: function(parms) {
            var it = this,
                payload = {
                    action: parms
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },
        filter: function(parms) {
            var it = this,
                filterParms = $.extend({}, it.config.filtering.filterData, parms),
                payload = {
                    action: {
                        actionName: 'filter',
                        filterData: filterParms
                    }
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },
        hide: function() {
            var it = this,
                payload = {
                    action: {
                        actionName: 'hideUnhideColumns',
                        columnData: {
                            tableUuid: it.config.parentId,
                            hide: true,
                            columnIndexes: [this.config.columnIndex]
                        }
                    }
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },
        unhide: function(columnIds) {
            var it = this,
                payload = {
                    action: {
                        actionName: 'hideUnhideColumns',
                        columnData: {
                            tableUuid: it.config.parentId,
                            hide: false,
                            columnIndexes: columnIds ? columnIds : [this.config.columnIndex]
                        }
                    }
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },
        resize: function(parms) {
            var it = this,
                payload = {
                    action: {
                        actionName: 'resize',
                        resizeColumnData: {
                            tableUuid: it.config.parentId,
                            columnIndex: this.config.columnIndex,
                            direction: "right",
                            width: parms.width
                        }
                    }
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            it.trigger(reportEvents.ACTION, payload.action);
        },

        parse: function(response) {
            if (response.headerToolbar && response.headerToolbar.sortAscBtn && response.headerToolbar.sortDescBtn) {
                var ascBtnSortOrder = response.headerToolbar.sortAscBtn.sortData.sortData.sortOrder,
                    dscBtnSortOrder = response.headerToolbar.sortDescBtn.sortData.sortData.sortOrder,
                    sortOrder;

                if (ascBtnSortOrder === "None") {
                    sortOrder = "asc";
                } else if (dscBtnSortOrder === "None") {
                    sortOrder = "desc";
                }

                response.sortOrder = sortOrder;
            }

            if (response.filtering && response.filtering.filterData) {
                var dataType = jiveDataConverter.dataTypeToSchemaFormat[response.dataType],
                    operator = response.filtering.filterData.filterTypeOperator,
                    fieldValueStart = response.filtering.filterData.fieldValueStart,
                    fieldValueEnd = response.filtering.filterData.fieldValueEnd;

                if(response.filtering.filterData.clearFilter || (fieldValueStart == null && operator == null)) {
                    response.filterOperator = undefined;
                    response.filterValue = undefined;
                } else {
                    var obj = jiveDataConverter.operatorAndValueToSchemaFormat(operator, dataType, fieldValueStart, fieldValueEnd);

                    response.filterOperator = obj.operator;
                    response.filterValue = obj.value;
                }
            }

            if (response.headingsTabContent) {
                this.headingFormat.set(this.headingFormat.parse(response.headingsTabContent), { silent: true });
            }

            if (response.valuesTabContent) {
                this.detailsRowFormat.dataType = jiveDataConverter.dataTypeToSchemaFormat[response.dataType];
                this.detailsRowFormat.set(this.detailsRowFormat.parse(response.valuesTabContent), { silent: true });
            }

            if (response.conditionalFormattingData) {
                this.conditions.dataType = jiveDataConverter.dataTypeToSchemaFormat[response.dataType];
                this.conditions.reset(response.conditionalFormattingData.conditions, { silent: true, parse: true });
            }

            return response;
        },

        actions: {
            "change:sortOrder": function() {
                var sortColumnName = this.get("headerToolbar").sortAscBtn.sortData.sortData.sortColumnName,
                    sortColumnType = this.get("headerToolbar").sortAscBtn.sortData.sortData.sortColumnType,
                    sortOrder;

                switch (this.get("sortOrder")) {
                    case "asc":
                        sortOrder = "Asc";
                        break;
                    case "desc":
                        sortOrder = "Dsc";
                        break;
                    default:
                        sortOrder = "None";
                        break;
                }

                return {
                    "actionName": "sort",
                    "sortData": {
                        "sortColumnName": sortColumnName,
                        "sortColumnType": sortColumnType,
                        "sortOrder": sortOrder,
                        "tableUuid": this.get("parentId")
                    }
                }
            },
            "change:filter": function() {
                var clearFilter = this.get("filterValue") == null && this.get("filterOperator") == null,
                    filterData = this.get("filtering").filterData;

                return {
                    "actionName": "filter",
                    "filterData": {
                        "fieldValueStart": jiveDataConverter.filterStartValue(this.get("filterOperator"), this.get("filterValue"), jiveDataConverter.dataTypeToSchemaFormat[this.get("dataType")], this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                        "fieldValueEnd": jiveDataConverter.filterEndValue(this.get("filterOperator"), this.get("filterValue"), jiveDataConverter.dataTypeToSchemaFormat[this.get("dataType")], this.parent && this.parent.config ? this.parent.config.genericProperties : undefined),
                        "filterTypeOperator": jiveDataConverter.schemaFormatOperatorToFilterOperator(this.get("filterOperator"), this.get("filterValue"), jiveDataConverter.dataTypeToSchemaFormat[this.get("dataType")]),
                        "clearFilter": clearFilter,

                        "filterPattern": filterData.filterPattern,
                        "fieldName": filterData.fieldName,
                        "localeCode": filterData.localeCode,
                        "timeZoneId": filterData.timeZoneId,
                        "isField": filterData.isField,

                        "tableUuid": this.get("parentId"),
                        "filterType": this.get("dataType")
                    }
                };
            },
            "change:headingFormat": onHeadingChange,
            "change:columnLabel": onHeadingChange,
            "change:detailsRowFormat": function() {
                return {
                    "actionName":"editTextElement",
                    "editTextElementData": {
                        "applyTo": "detailrows",
                        "tableUuid": this.get("parentId"),
                        "columnIndex": this.get("columnIndex"),
                        "dataType": this.get("dataType"),
                        "headingName": this.get("columnLabel"),

                        "fontName": this.detailsRowFormat.get("font").name,
                        "fontSize": this.detailsRowFormat.get("font").size + "",
                        "fontBold": this.detailsRowFormat.get("font").bold,
                        "fontItalic": this.detailsRowFormat.get("font").italic,
                        "fontUnderline": this.detailsRowFormat.get("font").underline,
                        "fontColor": this.detailsRowFormat.get("font").color,

                        "formatPattern": this.detailsRowFormat.toJiveFormat(),

                        "fontHAlign": (this.detailsRowFormat.get("align").charAt(0).toUpperCase() + this.detailsRowFormat.get("align").slice(1)),
                        "fontBackColor": this.detailsRowFormat.get("backgroundColor") === "transparent" ? "000000" : this.detailsRowFormat.get("backgroundColor"),
                        "mode": this.detailsRowFormat.get("backgroundColor") === "transparent" ? "Transparent" : "Opaque"
                    }
                }
            },
            "change:conditions": function() {
                var genericProperties = this.parent && this.parent.config ? this.parent.config.genericProperties : undefined;

                return {
                    "actionName":"conditionalFormatting",
                    "conditionalFormattingData":{
                        "applyTo":"detailrows",
                        "tableUuid":this.get("parentId"),
                        "columnIndex":this.get("columnIndex"),
                        "conditionPattern":this.get("conditionalFormattingData").conditionPattern,
                        "conditionType":this.get("conditionalFormattingData").conditionType,
                        "conditions": this.conditions.map(function(conditionModel) {
                            return conditionModel.toJiveFormat(genericProperties);
                        })
                    }
                }
            }
        },

        toReportComponentObject: function() {
            var columns = this.parent.get("allColumnsData");

            if (columns) {
                var thisColumnProps = _.findWhere(columns, { uuid: this.get("id") });

                if (thisColumnProps) {
                    if (!thisColumnProps.interactive) {
                        return undefined;
                    } else {
                        return {
                            id: this.get("id"),
                            componentType: interactiveComponentTypes.TABLE_COLUMN,
                            dataType: jiveDataConverter.dataTypeToSchemaFormat[this.get("dataType")],
                            label: this.get("columnLabel"),
                            name: this.get("name"),
                            sort: sortingToSchemaFormat.call(this),
                            filter: filteringToSchemaFormat.call(this),
                            headingFormat: this.headingFormat.toJSON(),
                            detailsRowFormat: this.detailsRowFormat.toJSON(),
                            conditions: this.get("canFormatConditionally") ? this.conditions.toJSON() : undefined
                        };
                    }
                }
            }

            return undefined;
        },

        updateFromReportComponentObject: function(obj) {
            var setterObj = {};

            if(obj.label) {
                setterObj.columnLabel = obj.label;
            }

            if (this.get("canSort") && obj.sort) {
                if ("order" in obj.sort) {
                    setterObj.sortOrder = obj.sort.order;
                } else if (_.keys(obj.sort).length === 0) {
                    setterObj.sortOrder = undefined;
                }
            }

            if (this.get("canFilter") && obj.filter) {
                if (_.keys(obj.filter).length === 0 || obj.filter.operator == null || obj.filter.value == null) {
                    setterObj.filterOperator = undefined;
                    setterObj.filterValue = undefined;
                } else {
                    setterObj.filterOperator = obj.filter.operator;
                    setterObj.filterValue = obj.filter.value;
                }
            }

            if (obj.headingFormat) {
                obj.headingFormat.font = _.extend({}, this.headingFormat.get("font"), obj.headingFormat.font || {});

                if (obj.headingFormat.backgroundColor && obj.headingFormat.backgroundColor !== "transparent") {
                    obj.headingFormat.backgroundColor = obj.headingFormat.backgroundColor.toUpperCase();
                }

                if (obj.headingFormat.font && obj.headingFormat.font.color) {
                    obj.headingFormat.font.color = obj.headingFormat.font.color.toUpperCase();
                }

                this.headingFormat.set(obj.headingFormat);
            }

            if (obj.detailsRowFormat) {
                obj.detailsRowFormat.font = _.extend({}, this.detailsRowFormat.get("font"), obj.detailsRowFormat.font || {});

                if (_.isObject(this.detailsRowFormat.get("pattern"))) {
                    obj.detailsRowFormat.pattern = _.extend({}, this.detailsRowFormat.get("pattern"), obj.detailsRowFormat.pattern || {});
                }

                if (obj.detailsRowFormat.backgroundColor && obj.detailsRowFormat.backgroundColor !== "transparent") {
                    obj.detailsRowFormat.backgroundColor = obj.detailsRowFormat.backgroundColor.toUpperCase();
                }

                if (obj.detailsRowFormat.font && obj.detailsRowFormat.font.color) {
                    obj.detailsRowFormat.font.color = obj.detailsRowFormat.font.color.toUpperCase();
                }

                this.detailsRowFormat.set(obj.detailsRowFormat);
            }

            if (this.get("canFormatConditionally") && obj.conditions && obj.conditions.length !== 0) {
                this.conditions.reset(obj.conditions);
            }

            this.set(setterObj);
        }
    });
});

