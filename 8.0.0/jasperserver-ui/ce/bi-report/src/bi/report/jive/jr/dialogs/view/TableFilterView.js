/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Epoxy from 'backbone.epoxy';
import _ from 'underscore';
import tableFilterTemplate from '../template/tableFilterTemplate.htm';
import TableFilterModel from '../model/TableFilterModel';
import datePickerBindingHandler from './dateTimePickerEpoxyBindingHandler';
import i18n from "../../../../../../i18n/jasperreports_messages.properties";
import {getFilterOperatorKey} from "../util/filterLabelResolver";

function toEpoxyOptions(dataType, jiveOptions) {
    var epoxyOptions = [];
    _.each(jiveOptions, function (o) {
        epoxyOptions.push({
            label: i18n[getFilterOperatorKey(dataType, o)],
            value: o
        });
    });
    return epoxyOptions;
}
var TableFilterViewModel = Epoxy.Model.extend({
    defaults: {
        columnLabel: '',
        clearFilter: 'true',
        filterOptions: [],
        dataType: 'text',
        emptyFilterOption: '...'
    },
    computeds: {
        isNotClearFilter: function () {
            return this.get('clearFilter') !== 'true';
        },
        isNotBooleanType: function () {
            return this.get('dataType') !== 'boolean';
        },
        transformedFilterOptions: {
            deps: [
                'clearFilter',
                'filterOptions',
                'dataType'
            ],
            get: function (clearFilter, filterOptions, dataType) {
                if (clearFilter === 'true') {
                    return [];
                } else {
                    return toEpoxyOptions(dataType, filterOptions);
                }
            }
        }
    },
    remove: function () {
    }
});
export default Epoxy.View.extend({
    constructor: function (options) {
        this.i18n = options.i18n;
        Epoxy.View.prototype.constructor.call(this, options);
    },
    initialize: function () {
        this.model = new TableFilterModel();
        this.viewModel = new TableFilterViewModel();
        this.listenTo(this.viewModel, 'change:clearFilter', this._onClearFilterChanged);
        Epoxy.View.prototype.initialize.apply(this, arguments);
    },
    el: function () {
        return _.template(tableFilterTemplate, { i18n: this.i18n });
    },
    computeds: {
        filterValueStart: {
            deps: ['value'],
            get: function (value) {
                if (_.isArray(value)) {
                    return value[0];
                } else {
                    return value;
                }
            },
            set: function (val) {
                var modelValue = this.getBinding('value');
                if (_.isArray(modelValue)) {
                    modelValue[0] = val;
                } else {
                    this.setBinding('value', val);
                }
            }
        },
        filterValueEnd: {
            deps: ['value'],
            get: function (value) {
                if (_.isArray(value)) {
                    return value[1];
                }
            },
            set: function (val) {
                var modelValue = this.getBinding('value');
                if (_.isArray(modelValue)) {
                    modelValue[1] = val;
                }
            }
        }
    },
    bindingHandlers: { dateTimePicker: datePickerBindingHandler },
    _onClearFilterChanged: function () {
        // reset filter on show all rows
        if (this.viewModel.get('clearFilter') === 'true') {
            this.model.reset();
        }
        // set the operator to the first value from filterOptions
        else {
            this.model.set('operator', this.viewModel.get('filterOptions')[0]);
        }
    },
    remove: function () {
        Epoxy.View.prototype.remove.apply(this, arguments);
        this.model && this.model.remove();
        this.viewModel && this.viewModel.remove();
    }
});