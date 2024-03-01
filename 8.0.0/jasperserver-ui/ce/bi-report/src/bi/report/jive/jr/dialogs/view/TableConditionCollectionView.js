/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import Epoxy from 'backbone.epoxy';
import _ from 'underscore';
import jiveDataConverter from '../../../util/jiveDataConverter';
import AlertDialog from 'js-sdk/src/common/component/dialog/AlertDialog';
import FormatModelCache from '../util/FormatModelCache';
import i18n from '../../../../../../i18n/jasperreports_messages.properties';
import tableConditionCollectionTemplate from '../template/tableConditionCollectionTemplate.htm';
import TableConditionView from './TableConditionView';
import TableConditionModel from '../model/TableConditionModel';

function onApplyToChange(viewModel, value, options) {
    var cache = this.cache, currentApplyTo = value, previousApplyTo = viewModel.previous('applyTo'), columnComponentModel = this.columnComponentModel, key, keyModel, columnGroup;
    if (currentApplyTo == null || options.isReset) {
        return;
    }
    if (!this.validate()) {
        // restore previousApplyTo
        viewModel.set({ applyTo: previousApplyTo }, { isReset: true });
        return;
    }
    if (previousApplyTo) {
        if (previousApplyTo === 'detailrows') {
            key = cache.createKey(previousApplyTo, columnComponentModel);
        } else {
            key = cache.createKey(previousApplyTo, columnComponentModel.parent.columnGroups.findWhere({ id: previousApplyTo }), true);
        }
        cache.set(key, this.collection.toJSON());
    }
    if (currentApplyTo === 'detailrows') {
        key = cache.createKey(currentApplyTo, columnComponentModel);
        if (cache.get(key) === null) {
            cache.set(key, schemaConditionsToJiveConditions.call(this, columnComponentModel.conditions.toJSON(), columnComponentModel.get('dataType')));
        }
    } else {
        columnGroup = columnComponentModel.parent.columnGroups.findWhere({ id: currentApplyTo });
        key = cache.createKey(currentApplyTo, columnGroup, true);
        if (cache.get(key) === null) {
            cache.set(key, schemaConditionsToJiveConditions.call(this, columnGroup.conditions.toJSON(), columnGroup.get('dataType')));
        }
    }
    keyModel = cache.keyInfo[key].model;
    this.collection.conditionPattern = keyModel.get('conditionalFormattingData').conditionPattern;
    this.collection.dataType = keyModel.get('dataType');
    this.addAll(cache.get(key));
}
function updateApplyToOptions() {
    var self = this, columnModel = this.columnComponentModel, columnIndex = columnModel.get('columnIndex'), columnGroups = columnModel.parent.columnGroups, options = [], groupHeadingOptions = [], groupSubTotalsOptions = [], totalsOptions = [];
    columnGroups.each(function (group) {
        if (_.indexOf(group.get('forColumns'), columnIndex) != -1 && group.get('conditionalFormattingData') !== null) {
            if (group.get('groupType') === 'groupheading') {
                groupHeadingOptions.push({
                    value: group.get('id'),
                    label: group.get('groupName') + ' ' + self.i18n['net.sf.jasperreports.components.headertoolbar.groupheading.prefix']
                });
            } else if (group.get('groupType') === 'groupsubtotal') {
                groupSubTotalsOptions.push({
                    value: group.get('id'),
                    label: group.get('groupName') + ' ' + self.i18n['net.sf.jasperreports.components.headertoolbar.groupsubtotal.prefix']
                });
            } else if (group.get('groupType') === 'tabletotal') {
                totalsOptions.push({
                    value: group.get('id'),
                    label: self.i18n['net.sf.jasperreports.components.headertoolbar.applyto.option.tabletotal']
                });
            }
        }
    });
    options.push.apply(options, groupHeadingOptions);
    if (columnModel.get('canFormatConditionally')) {
        options.push({
            value: 'detailrows',
            label: this.i18n['net.sf.jasperreports.components.headertoolbar.applyto.option.detailrows']
        });
    }
    options.push.apply(options, groupSubTotalsOptions);
    options.push.apply(options, totalsOptions);
    this.viewModel.set('applyToOptions', options);    // try to keep the same applyTo across columns
    // try to keep the same applyTo across columns
    var previousApplyTo = this.viewModel.previous('applyTo');
    if (previousApplyTo) {
        this.viewModel.set('applyTo', null, { silent: true });
        if (_.findWhere(options, { value: previousApplyTo })) {
            this.viewModel.set('applyTo', previousApplyTo);
        } else {
            this.viewModel.set('applyTo', 'detailrows');
        }
    } else {
        this.viewModel.set('applyTo', 'detailrows');
    }    // when no options there is nothing to format conditionally
    // when no options there is nothing to format conditionally
    if (!options.length) {
        this.viewModel.set('canAddCondition', false);
    } else {
        this.viewModel.set('canAddCondition', true);
    }
}
function cacheCurrentCollection() {
    var applyTo = this.viewModel.get('applyTo'), key;
    if (applyTo === 'detailrows') {
        key = this.cache.createKey(applyTo, this.columnComponentModel);
    } else if (applyTo) {
        key = this.cache.createKey(applyTo, this.columnComponentModel.parent.columnGroups.findWhere({ id: applyTo }), true);
    }
    key && this.cache.set(key, this.collection.toJSON());
}
function schemaConditionsToJiveConditions(schemaConditions, jiveDataType) {
    var self = this, schemaDataType = jiveDataConverter.dataTypeToSchemaFormat[jiveDataType], genericProperties;
    return _.map(schemaConditions, function (condition) {
        var jiveOpVal = {};
        if (condition.operator) {
            jiveOpVal.operator = jiveDataConverter.schemaFormatOperatorToFilterOperator(condition.operator, condition.value, schemaDataType);
            genericProperties = self.columnComponentModel.parent.config.genericProperties;
            if (schemaDataType === 'datetime') {
                if (_.isArray(condition.value)) {
                    jiveOpVal.value = [];
                    jiveOpVal.value[0] = jiveDataConverter.isoTimestampTojQueryUiTimestamp(condition.value[0], genericProperties);
                    jiveOpVal.value[1] = jiveDataConverter.isoTimestampTojQueryUiTimestamp(condition.value[1], genericProperties);
                } else {
                    jiveOpVal.value = jiveDataConverter.isoTimestampTojQueryUiTimestamp(condition.value, genericProperties);
                }
            } else if (schemaDataType === 'time') {
                if (_.isArray(condition.value)) {
                    jiveOpVal.value = [];
                    jiveOpVal.value[0] = jiveDataConverter.isoTimeTojQueryUiTime(condition.value[0], genericProperties);
                    jiveOpVal.value[1] = jiveDataConverter.isoTimeTojQueryUiTime(condition.value[1], genericProperties);
                } else {
                    jiveOpVal.value = jiveDataConverter.isoTimeTojQueryUiTime(condition.value, genericProperties);
                }
            } else {
                jiveOpVal.value = condition.value;
            }
        }
        return _.extend(condition, jiveOpVal);
    });
}
function jiveConditionsToSchemaConditions(jiveConditions, jiveDataType, conditionPattern) {
    var self = this, schemaOpVal;
    return jiveConditions.map(function (condition) {
        if (condition.operator) {
            var valueStart = _.isArray(condition.value) ? condition.value[0] : condition.value, valueEnd = _.isArray(condition.value) ? condition.value[1] : undefined;
            schemaOpVal = jiveDataConverter.operatorAndValueToSchemaFormat.call(self.columnComponentModel, condition.operator, jiveDataConverter.dataTypeToSchemaFormat[jiveDataType], valueStart, valueEnd, conditionPattern);
        }
        return _.extend(condition, schemaOpVal);
    });
}
var TableConditionCollection = Backbone.Collection.extend({ model: TableConditionModel });
var TableConditionCollectionViewModel = Epoxy.Model.extend({
    defaults: function () {
        return {
            dataType: "text",
            applyToOptions: [],
            applyTo: null,
            canAddCondition: true
        };
    },
    computeds: {
        isNotBooleanType: function () {
            return this.get('dataType') !== 'boolean';
        }
    },
    reset: function () {
        this.clear({ silent: true }).set(this.defaults());
        return this;
    },
    remove: function () {
    }
});
export default Epoxy.View.extend({
    events: { 'click .jive_inputbutton[name=\'conditionAdd\']': 'addNewCondition' },
    el: function () {
        return _.template(tableConditionCollectionTemplate, { i18n: i18n });
    },
    constructor: function (options) {
        this.i18n = options.i18n;
        Epoxy.View.apply(this, arguments);
    },
    initialize: function () {
        this.viewModel = new TableConditionCollectionViewModel();
        this.collection = new TableConditionCollection();
        this.cache = new FormatModelCache();
        this._subviews = [];
        this._$subviewsContainer = this.$('table.conditionList > tbody');
        this.listenTo(this.viewModel, 'change:applyTo', _.bind(onApplyToChange, this));
        this.listenTo(this.collection, 'remove', this.removeSubview);
        this.on('tabSwitched', function () {
            cacheCurrentCollection.call(this);
        });
        this.errorDialog = new AlertDialog({ additionalCssClasses: 'jive_dialog' });
        Epoxy.View.prototype.initialize.apply(this, arguments);
    },
    addSubview: function (model) {
        var subview = new TableConditionView(_.extend({}, {
            i18n: this.i18n,
            model: model,
            genericProperties: this.columnComponentModel.parent.config.genericProperties
        }));
        this._$subviewsContainer.append(subview.render().$el);
        this._subviews.push(subview);
        return subview;
    },
    addModel: function (modelJSON, skipRegistration) {
        var subview = new TableConditionView({
                i18n: this.i18n,
                genericProperties: this.columnComponentModel.parent.config.genericProperties
            }), applyTo = this.viewModel.get('applyTo'), dataType, model;
        subview.parent = this;
        if (applyTo === 'detailrows') {
            dataType = this.columnComponentModel.get('dataType').toLowerCase();
        } else {
            dataType = this.columnComponentModel.parent.columnGroups.findWhere({ id: applyTo }).get('dataType').toLowerCase();
        }

        var operator = dataType;
        if (operator === "date" || operator === "time") {
            operator = "dateTime";
        }

        subview.viewModel.set({
            conditionOptions: this.columnComponentModel.parent.config.genericProperties.operators[operator],
            dataType: dataType,
            calendarPatterns: this.columnComponentModel.parent.config.genericProperties.calendarPatterns
        });
        subview.model.set(modelJSON);
        this._$subviewsContainer.append(subview.render().$el);
        this._subviews.push(subview);
        if (!skipRegistration) {
            model = this.collection.add(subview.model);
            subview.viewModel.set('conditionIndex', this.collection.indexOf(model) + 1);
        }
        return subview.model;
    },
    addAll: function (arrJsonModels) {
        var self = this;
        this.collection.set(_.map(arrJsonModels, function (modelJSON, index) {
            var model = self.addModel(modelJSON, true), view = self.getSubviewByModel(model);
            view.viewModel.set('conditionIndex', index + 1);
            return model;
        }));
    },
    removeSubview: function (model) {
        var subview = this.getSubviewByModel(model);
        if (subview) {
            var index = _.indexOf(this._subviews, subview);
            this._subviews.splice(index, 1);
            subview.remove();

            // recalculate the condition index for the remaining subviews
            if (this._subviews.length) {
                _.each(this._subviews, function(subview, index) {
                    subview.viewModel.set('conditionIndex', index + 1);
                });
            }
        }
    },
    getSubviewByModel: function (model) {
        return _.find(this._subviews, function (subview) {
            return subview.model === model;
        });
    },
    render: function () {
        _.invoke(this._subviews, 'remove');
        this._subviews = [];
        this._$subviewsContainer.empty();
        var currentConditions = this.collection.toJSON();
        this.collection.reset();
        this.addAll(currentConditions);    //this.collection.forEach(_.bind(this.addSubview, this));
        //this.collection.forEach(_.bind(this.addSubview, this));
        return this;
    },
    addNewCondition: function (evt) {
        if (this.$(evt.target).is('.disabled')) {
            return;
        }
        var tableConditionModel = new TableConditionModel(), model = this.addModel(tableConditionModel.defaults()), view = this.getSubviewByModel(model);    // preselect operator
        // preselect operator
        model.set('operator', view.getBinding('convertedOptions')[0].value);
        tableConditionModel.remove();
    },
    setColumnComponentModel: function (columnComponentModel, isUpdate) {
        if (isUpdate) {
            cacheCurrentCollection.call(this);
        } else {
            this.cache.clear();
            this.viewModel.set({ applyTo: null }, { silent: true });
            this.collection.genericProperties = columnComponentModel.parent.config.genericProperties;
        }
        this.columnComponentModel = columnComponentModel;
        this.viewModel.set("dataType", jiveDataConverter.dataTypeToSchemaFormat[this.columnComponentModel.get("dataType")]);
        updateApplyToOptions.call(this);
    },
    getActions: function () {
        var self = this, cache = this.cache, actions = [];
        cacheCurrentCollection.call(this);
        _.each(cache.map, function (cacheEntry, key) {
            var keyInfo = cache.keyInfo[key];    // FIXME: check if collection has changed before adding it to action
            // FIXME: check if collection has changed before adding it to action
            keyInfo.model.updateFromReportComponentObject({ conditions: jiveConditionsToSchemaConditions.call(self, cacheEntry.current, keyInfo.model.get('dataType'), keyInfo.model.get('conditionalFormattingData').conditionPattern) });
            actions.push(keyInfo.model.actions['change:conditions'].call(keyInfo.model));
        });
        return actions;
    },
    isValid: function () {
        var invalidModels = [];
        this.collection.reduce(function (memo, model) {
            if (!model.isValid()) {
                memo.push(model);
            }
            return memo;
        }, invalidModels);
        return !invalidModels.length;
    },
    validate: function () {
        if (!this.isValid()) {
            this.errorDialog.setMessage(i18n['net.sf.jasperreports.components.headertoolbar.conditions.invalid']);
            this.errorDialog.open();
            return false;
        }
        return true;
    },
    clear: function () {
        _.invoke(this._subviews, 'remove');
        this._subviews = [];
        this.collection.reset();
        this.cache.clear();
    },
    remove: function () {
        Epoxy.View.prototype.remove.apply(this, arguments);
        this.viewModel && this.viewModel.remove();
        this.collection && this.collection.remove();
        this.cache && this.cache.remove();
        this.errorDialog && this.errorDialog.remove();
        _.invoke(this._subviews, 'remove');
        this._subviews = [];
    }
});