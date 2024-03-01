/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import Epoxy from 'backbone.epoxy';
import TableCommonFormatView from './TableCommonFormatView';
import tableConditionTemplate from '../template/tableConditionTemplate.htm';
import TableConditionModel from '../model/TableConditionModel';
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
var ViewModel = Epoxy.Model.extend({
    defaults: {
        conditionIndex: 1,
        conditionOptions: [],
        dataType: 'text',
        calendarPatterns: {}
    },
    computeds: {
        isNotBooleanType: function () {
            return this.get('dataType') !== 'boolean';
        }
    },
    remove: function () {
    }
});
export default TableCommonFormatView.extend({
    events: {
        'click div.jive_inputbutton[name=\'conditionRemove\']': '_removeCondition',
        'click div.jive_inputbutton[name=\'conditionMoveUp\']': '_moveConditionUp',
        'click div.jive_inputbutton[name=\'conditionMoveDown\']': '_moveConditionDown'
    },
    el: function () {
        return _.template(tableConditionTemplate, { i18n: this.i18n });
    },
    initialize: function () {
        this.model = new TableConditionModel();
        this.viewModel = new ViewModel();
        Epoxy.View.prototype.initialize.apply(this, arguments);
    },
    computeds: {
        convertedOptions: {
            deps: ['conditionOptions', 'dataType'],
            get: function (conditionOptions, dataType) {
                return toEpoxyOptions(dataType, conditionOptions);
            }
        },
        conditionValueStart: {
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
                    this.model.set({ value: modelValue }, { validate: true });
                } else {
                    this.model.set({ value: val }, { validate: true });
                }
            }
        },
        conditionValueEnd: {
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
                    this.model.set({ value: modelValue }, { validate: true });
                }
            }
        },
        getColspan: function () {
            return this.getBinding('isMultiValueOperator') ? 1 : 3;
        }
    },
    _removeCondition: function (evt) {
        this.model.trigger('destroy', this.model, this.model.collection);
    },
    _moveConditionUp: function (evt) {
        var collection = this.model.collection, index = collection.indexOf(this.model);
        if (index > 0) {
            collection.remove(this.model, { silent: true });
            collection.add(this.model, { at: index - 1 }, { silent: true });
            this.parent.render();
        }
    },
    _moveConditionDown: function (evt) {
        var collection = this.model.collection, index = collection.indexOf(this.model);
        if (index < collection.length - 1) {
            collection.remove(this.model, { silent: true });
            collection.add(this.model, { at: index + 1 }, { silent: true });
            this.parent.render();
        }
    },
    remove: function () {
        Epoxy.View.prototype.remove.apply(this, arguments);
        this.model && this.model.remove();
        this.viewModel && this.viewModel.remove();
    }
});