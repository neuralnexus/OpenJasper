/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import Dialog from 'js-sdk/src/common/component/dialog/Dialog';
import tableFilterTemplate from './template/tableFilterDialogTemplate.htm';
import TableFilterView from './view/TableFilterView';
import jiveDataConverter from '../../util/jiveDataConverter';
import validationCodes from "../../util/validationCodes";
import adjustDialogPositionWithinViewportTrait from './trait/adjustDialogPositionWithinViewportTrait';
import i18n2 from 'js-sdk/src/i18n/CommonBundle.properties';

function schemaFilterToJiveFilter(schemaFilter, schemaDataType) {
    var jiveFilter = {}, genericProperties, isNullCheckOperator;
    if (schemaFilter.operator) {
        jiveFilter.operator = jiveDataConverter.schemaFormatOperatorToFilterOperator(schemaFilter.operator, schemaFilter.value, schemaDataType);
        genericProperties = this.columnComponentModel.parent.config.genericProperties;
        isNullCheckOperator = schemaFilter.operator.indexOf('null') !== -1;
        if (schemaDataType === 'datetime' && !isNullCheckOperator) {
            if (_.isArray(schemaFilter.value)) {
                jiveFilter.value = [];
                jiveFilter.value[0] = jiveDataConverter.isoTimestampTojQueryUiTimestamp(schemaFilter.value[0], genericProperties);
                jiveFilter.value[1] = jiveDataConverter.isoTimestampTojQueryUiTimestamp(schemaFilter.value[1], genericProperties);
            } else {
                jiveFilter.value = jiveDataConverter.isoTimestampTojQueryUiTimestamp(schemaFilter.value, genericProperties);
            }
        } else if (schemaDataType === 'time' && !isNullCheckOperator) {
            if (_.isArray(schemaFilter.value)) {
                jiveFilter.value = [];
                jiveFilter.value[0] = jiveDataConverter.isoTimeTojQueryUiTime(schemaFilter.value[0], genericProperties);
                jiveFilter.value[1] = jiveDataConverter.isoTimeTojQueryUiTime(schemaFilter.value[1], genericProperties);
            } else {
                jiveFilter.value = jiveDataConverter.isoTimeTojQueryUiTime(schemaFilter.value, genericProperties);
            }
        } else {
            jiveFilter.value = schemaFilter.value;
        }
    }
    return jiveFilter;
}
export default Dialog.extend({
    defaultTemplate: tableFilterTemplate,
    constructor: function (options) {
        this.tableFilterView = new TableFilterView({ i18n: options.i18n });
        this.i18n = options.i18n;
        Dialog.prototype.constructor.call(this, {
            buttons: [
                {
                    label: i18n2['button.cancel'],
                    action: 'cancel',
                    primary: false,
                    float: 'right'
                },
                {
                    label: i18n2['button.ok'],
                    action: 'ok',
                    primary: true,
                    float: 'right'
                }
            ],
            traits: [ adjustDialogPositionWithinViewportTrait ],
            additionalCssClasses: 'tableFilterDialog',
            modal: options.modal,
            resizable: false,
            contentContainer: '.dialogContent',
            content: this.tableFilterView
        });
        this.on('button:ok', this._applyFilter);
        this.on('button:cancel', this.close);
    },
    open: function (columnComponentModel) {
        this.columnComponentModel = columnComponentModel;
        var reportComponentObject = this.columnComponentModel.toReportComponentObject();

        var operator = this.columnComponentModel.get('dataType').toLowerCase();
        if (operator === "date" || operator === "time") {
            operator = "dateTime";
        }

        this.tableFilterView.viewModel.set({
            columnLabel: reportComponentObject.label ? reportComponentObject.label : '#' + (this.columnComponentModel.get('columnIndex') + 1),
            clearFilter: reportComponentObject.filter.operator == null ? 'true' : 'false',
            filterOptions: this.columnComponentModel.parent.config.genericProperties.operators[operator],
            dataType: this.columnComponentModel.get('dataType').toLowerCase(),
            calendarPatterns: this.columnComponentModel.parent.config.genericProperties.calendarPatterns
        });
        this.tableFilterView.model.reset().set(schemaFilterToJiveFilter.call(this, reportComponentObject.filter, reportComponentObject.dataType));
        Dialog.prototype.open.apply(this, arguments);
    },
    _applyFilter: function (evt) {
        var jiveFilter = this.tableFilterView.model.toJSON(),
            filterValidation,
            errMsg;
        if (jiveFilter.operator) {
            var componentObj = this.columnComponentModel.toReportComponentObject(),
                valueStart = _.isArray(jiveFilter.value) ? jiveFilter.value[0] : jiveFilter.value,
                valueEnd = _.isArray(jiveFilter.value) ? jiveFilter.value[1] : undefined;

            try {
                componentObj.filter = jiveDataConverter.operatorAndValueToSchemaFormat.call(
                    this.columnComponentModel,
                    jiveFilter.operator,
                    componentObj.dataType,
                    valueStart,
                    valueEnd,
                    componentObj.detailsRowFormat.pattern);
                this.columnComponentModel.updateFromReportComponentObject(componentObj);
            } catch(ex) {
                filterValidation = {
                    code: ex.errorCode,
                    params: ex.parameters,
                    originalException: ex
                }
            }
        } else {
            this.columnComponentModel.set({
                filterValue: null,
                filterOperator: null
            }, { silent: true });
        }

        if (filterValidation) {
            if (filterValidation.code) {
                errMsg = filterValidation.code;
                if (validationCodes.INVALID_TIMESTAMP === errMsg || validationCodes.INVALID_TIME === errMsg) {
                    errMsg = this.i18n['net.sf.jasperreports.components.headertoolbar.actions.filter.invalid.date'];
                    errMsg = errMsg.replace("{0}", filterValidation.params.value);
                }
            } else {
                errMsg = filterValidation.originalException;
            }
            this.trigger("validationError", errMsg);
        } else {
            var filterAction = this.columnComponentModel.actions['change:filter'].call(this.columnComponentModel);
            this.columnComponentModel.filter(filterAction.filterData);
            this.close();
        }
    },
    remove: function () {
        Dialog.prototype.remove.call(this);
        this.tableFilterView && this.tableFilterView.remove();
    }
});