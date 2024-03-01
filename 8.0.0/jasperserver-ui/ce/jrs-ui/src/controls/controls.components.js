/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: afomin, inesterenko
 * @version: $Id$
 *
 * TODO: Not_Used_Anywhere
 */

/* global updateYearMonth*/

import {JRS} from "../namespace/namespace";
import jQuery from 'jquery';
import _ from 'underscore';
import './controls.core';
import {ControlsBase} from "./controls.base";
import SingleSelect from "js-sdk/src/components/singleSelect/view/SingleSelectNew";
import MultiSelect from "js-sdk/src/components/multiSelect/view/MultiSelect";
import DateAndTimePicker from "js-sdk/src/components/dateAndTime/DateAndTimePicker";
import selectedItemsDataProviderSorterFactory from "js-sdk/src/components/multiSelect/dataprovider/selectedItemsDataProviderSorterFactory";
import dateUtil from 'js-sdk/src/common/util/parse/date';

import InputControlsDataProvider from './dataprovider/InputControlsDataProvider';
import InputControlsDataProviderWithDataLabelHash from './dataprovider/InputControlsDataProviderWithDataLabelHash';
import DataProviderWithSearchCache from 'js-sdk/src/common/component/tree/dataprovider/DataProviderWithSearchCache';

import getInputControlsDataProviderNewPageOptions from './dataprovider/util/getInputControlsDataProviderNewPageOptions';

import "./controls.basecontrol";
import { showErrorPopup } from '../core/core.ajax.utils';
import RestParamsEnum from './rest/enum/restParamsEnum';

const getInitialSelectedValuesToLabelMap = (initialSelectedValues) => {
    return initialSelectedValues.reduce((memo, selectedValue) => {
        memo[selectedValue.value] = selectedValue.label || selectedValue.value;
        return memo;
    }, {});
};

JRS.Controls = (function (jQuery, _, Controls) {

    //module:
    //
    //  controls.components
    //
    //summary:
    //
    //  Provide default input controls types
    //
    //main types:
    //
    //  Bool, SingleValue, SingleValueDate, SingleSelect, MultiSelect, MultiSelectCheckbox, SingleSelectRadio - default
    //  input controls
    //
    //  ReportOptions - special control, not connected with cascade
    //
    //dependencies:
    //
    //  jQuery          - jquery v1.7.1
    //  _,         - underscore.js 1.3.1
    //  Controls   - controls.core module
    function changeDateFunc(dateText) {
        const prevSelection = Controls.BaseControl.prototype.get.call(this, 'selection');
        if (!prevSelection || prevSelection[0] !== dateText) {
            this.set({ selection: [dateText] });
        }
    }

    function getNormalizedDatetimeValue(rawValue) {
        var normalizedValue = rawValue.toUpperCase().replace(/([\s]+$|^[\s]+)/g, "");
        return normalizedValue.replace(/[\s]*(\+|\-)[\s]*/g, "$1");
    }

    return _.extend(Controls, {
        Bool: Controls.BaseControl.extend({

            setValue:function (controlData) {
                var container = this.getElem();
                var input = container.find('input');
                if (controlData == "true") {
                    input.prop('checked', true);
                } else {
                    input.prop('checked', false);
                }
            },

            bindCustomEventListeners:function () {
                this.getElem() && this.getElem().on('change', 'input', _.bind(function (evt) {
                    var value = evt.target.getValue() == null ? "false" : "true";
                    this.set({ selection: [value] });
                }, this));
            }
        }),

        SingleValueText: Controls.BaseControl.extend({

            setValue:function (controlData) {
                var value = controlData == ControlsBase.NULL_SUBSTITUTION_VALUE ? ControlsBase.NULL_SUBSTITUTION_LABEL : controlData;
                var container = this.getElem();
                if (container) {
                    container.find('input').attr('value', value);
                    container.find('input').val(value);
                }
            },

            bindCustomEventListeners:function () {
                this.getElem() && this.getElem().on('change', 'input', _.bind(function (evt) {
                    var inputValue = evt.target.value;
                    var value = inputValue == ControlsBase.NULL_SUBSTITUTION_LABEL ? ControlsBase.NULL_SUBSTITUTION_VALUE : inputValue;
                    this.set({ selection: [value] });
                }, this));

            }
        }),

        SingleValueNumber: Controls.BaseControl.extend({

            setValue:function (controlData) {
                this.getElem().find('input').attr('value', controlData);
                this.getElem().find('input').val(controlData);
            },

            bindCustomEventListeners:function () {
                this.getElem() && this.getElem().on('change', 'input', _.bind(function (evt) {
                    this.set({ selection: [evt.target.value] });
                }, this));
            }
        }),

        SingleValueDate: Controls.BaseControl.extend({

            initialize:function (args) {
                this.baseRender(args);
                args.visible && this.getElem() && this.setupCalendar();
            },

            get: function(attribute){
                var localizedDate = Controls.BaseControl.prototype.get.call(this, attribute);
                return dateUtil.localizedDateToIsoDate(localizedDate);
            },

            set: function(attributes, preventNotification){
                if(attributes.values){
                    attributes.values = dateUtil.isoDateToLocalizedDate(attributes.values);
                }
                Controls.BaseControl.prototype.set.call(this, attributes, preventNotification);
            },

            setupCalendar:function () {
                var input = this.getElem().find('input');

                this.picker = new DateAndTimePicker({
                    el: input,
                    showOn: "button",
                    dateFormat: JRS.i18n["bundledCalendarFormat"],
                    disabled: input[0].disabled,
                    onSelect: _.bind(changeDateFunc, this)
                });

                input.change(_.bind(function (evt) {
                    //prevent triggering of global control change event
                    evt.stopPropagation();

                    //remove all spaces and convert to upper case
                    jQuery(evt.target).val(getNormalizedDatetimeValue(evt.target.value));

                    changeDateFunc.call(this, jQuery(evt.target).val());
                }, this));

                input.after('&nbsp;');
                var button = this.getElem().find('button');
                button.wrap(ControlsBase.CALENDAR_ICON_SPAN).empty();
            },

            setValue:function (controlData) {
                var container = this.getElem();
                container.find('input').attr('value', controlData);
                container.find('input').val(controlData);
            }

        }),

        SingleValueDatetime: Controls.BaseControl.extend({

            initialize:function (args) {
                this.baseRender(args);
                args.visible && this.getElem() && this.setupCalendar();
            },

            getInput: function() {
                var $el = this.getElem();

                return $el && $el.find('input');
            },

            destroyCalendar: function() {
                var $input = this.getInput();
                $input.off();

                this.picker && this.picker.remove();
            },

            setupCalendar:function () {
                var $el = this.getElem(),
                    $input = this.getInput();

                this.destroyCalendar();

                this.picker = new DateAndTimePicker({
                    el: $input[0],
                    showOn: "button",
                    dateFormat: JRS.i18n["bundledCalendarFormat"],
                    timeFormat: JRS.i18n["bundledCalendarTimeFormat"],
                    disabled: $input[0].disabled,
                    onSelect: _.bind(changeDateFunc, this)
                });

                $input.change(_.bind(function (evt) {
                    //prevent triggering of global control change event
                    evt.stopPropagation();

                    //remove all spaces an  d convert to upper case
                    jQuery(evt.target).val(getNormalizedDatetimeValue(evt.target.value));
                    changeDateFunc.call(this, jQuery(evt.target).val());
                }, this));
                $input.after('&nbsp;');

                var button = $el.find('button');

                button.wrap(ControlsBase.CALENDAR_ICON_SPAN).empty();
            },

            get: function(attribute){
                var localizedDateTime = Controls.BaseControl.prototype.get.call(this, attribute);
                return dateUtil.localizedTimestampToIsoTimestamp(localizedDateTime);
            },

            set: function(attributes, preventNotification) {
                if(attributes.values){
                    attributes.values = dateUtil.isoTimestampToLocalizedTimestamp(attributes.values);
                }

                if (!attributes.values && !attributes.selection && this.getInput()) {
                    this.setupCalendar();
                }

                Controls.BaseControl.prototype.set.call(this, attributes, preventNotification);
            },
            setValue:function (controlData) {
                var container = this.getElem();
                container.find('input').attr('value', controlData);
                container.find('input').val(controlData);
            }
        }),

        SingleValueTime: Controls.BaseControl.extend({

            initialize:function (args) {
                this.baseRender(args);
                args.visible && this.getElem() && this.setupCalendar();
            },

            getInput: function() {
                var $el = this.getElem();

                return $el && $el.find('input');
            },

            destroyCalendar: function() {
                var $input = this.getInput();
                $input.off();

                this.picker && this.picker.remove();
            },

            setupCalendar:function () {
                var $el = this.getElem(),
                    $input = this.getInput();

                this.destroyCalendar();

                this.picker = new DateAndTimePicker({
                    el: $input[0],
                    showOn: "button",
                    timeFormat: JRS.i18n["bundledCalendarTimeFormat"],
                    disabled: $input[0].disabled,
                    onClose: _.bind(changeDateFunc, this)
                });
                $input.change(_.bind(function (evt) {
                    //prevent triggering of global control change event
                    evt.stopPropagation();

                    //remove all spaces an  d convert to upper case
                    jQuery(evt.target).val(getNormalizedDatetimeValue(evt.target.value));
                    changeDateFunc.call(this, jQuery(evt.target).val());
                }, this));

                $input.after('&nbsp;');
                var button = $el.find('button');
                button.wrap(ControlsBase.CALENDAR_ICON_SPAN).empty();
            },

            get: function(attribute){
                var localizedTime = Controls.BaseControl.prototype.get.call(this, attribute);
                return dateUtil.localizedTimeToIsoTime(localizedTime);
            },

            set: function(attributes, preventNotification){
                if (attributes.values) {
                    attributes.values = dateUtil.isoTimeToLocalizedTime(attributes.values);
                }

                if (!attributes.values && !attributes.selection && this.getInput()) {
                    this.setupCalendar();
                }

                Controls.BaseControl.prototype.set.call(this, attributes, preventNotification);
            },
            setValue:function (controlData) {
                var container = this.getElem();
                container.find('input').attr('value', controlData);
                container.find('input').val(controlData);
            }
        }),

        SingleSelect: Controls.BaseControl.extend({
            initialize(controlStructure, options = {}) {
                const {
                    initialSelectedValues
                } = options;

                this.dataUri = options.dataUri;
                this.inputControlsService = options.inputControlsService;
                this.paginatedValuesOptions = options.paginatedValuesOptions

                if (!this.singleSelect) {
                    this.inputControlsDataProviderWithDataLabelHash = new InputControlsDataProviderWithDataLabelHash({
                        inputControlsDataProvider: new InputControlsDataProvider({
                            controlId: controlStructure.id,
                            inputControlsService: this.inputControlsService
                        })
                    });

                    this.dataProviderWithCache = new DataProviderWithSearchCache({
                        pageSize: 100,
                        maxSearchCacheSize: 1,
                        saveLastCriteria: true,
                        request: (options) => {
                            options = getInputControlsDataProviderNewPageOptions(Object.assign({
                                name: this.id,
                                paginatedValuesOptions: this.paginatedValuesOptions
                            }, options));

                            var dfd = this.inputControlsDataProviderWithDataLabelHash.getData(this.dataUri, options);
                            Controls.Utils.showLoadingDialogOn(dfd, null, true)
                            return dfd;

                        }
                    });

                    this.initialSelectedValuesToLabelMap = getInitialSelectedValuesToLabelMap(initialSelectedValues);

                    this.singleSelect = new SingleSelect({
                        getData: this.dataProviderWithCache.getData,
                        keydownTimeout: ControlsBase.KEY_DOWN_TIMEOUT,
                        formatValue: (value) => {
                            return this.inputControlsDataProviderWithDataLabelHash.getLabelByValue(value)
                                ?? this.initialSelectedValuesToLabelMap[value];
                        },
                        keydownTimeout: ControlsBase.KEY_DOWN_TIMEOUT
                    }).setDisabled(controlStructure.readOnly);

                    if (controlStructure) {
                        _.extend(this, controlStructure);
                    }
                }
            },

            render() {
                const template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    this.singleSelect.undelegateEvents();

                    const element = jQuery(template(this));

                    this.singleSelect.render().renderData();

                    element.find(".ssPlaceholder").append(this.singleSelect.el);

                    this.setElem(element);
                    this.singleSelect.delegateEvents();

                    this.makeResizable && this.makeResizable();
                    this.isVisible(this) && this.bindCustomEventListeners();
                }
            },

            setValue(selectedValues) {
                this.singleSelect.setValue(selectedValues, { silent: true });
            },

            updateSelectionOnOptionChange(selection) {
                this.initialSelectedValuesToLabelMap = getInitialSelectedValuesToLabelMap(selection);
            },

            fetch(uri, paginatedValuesOptions) {
                this.dataUri = uri;
                this.paginatedValuesOptions = paginatedValuesOptions;

                const dfd = jQuery.Deferred();

                this.singleSelect.off('listRenderError').once('listRenderError', (xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                    dfd.reject(xhr);

                    this.bindCustomEventListeners();
                });

                this.dataProviderWithCache.clear();

                this.singleSelect.fetch((...args) => {
                    dfd.resolve(...args);
                });

                return dfd;
            },

            bindCustomEventListeners() {
                this.singleSelect.off("selection:change").on("selection:change", function (selection) {
                    this.fireControlSelectionChangeEvent([selection]);
                }, this);

                this.singleSelect.off('listRenderError').on('listRenderError', (xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                });
            }
        }),

        MultiSelect: Controls.BaseControl.extend({
            initialize(controlStructure, options = {}) {
                const {
                    initialSelectedValues
                } = options;

                this.dataUri = options.dataUri;
                this.inputControlsService = options.inputControlsService;
                this.paginatedValuesOptions = options.paginatedValuesOptions;

                if (!this.multiSelect) {
                    this.inputControlsDataProviderWithDataLabelHash = new InputControlsDataProviderWithDataLabelHash({
                        inputControlsDataProvider: new InputControlsDataProvider({
                            controlId: controlStructure.id,
                            inputControlsService: this.inputControlsService
                        })
                    });

                    this.dataProviderWithCache = new DataProviderWithSearchCache({
                        pageSize: 100,
                        maxSearchCacheSize: 1,
                        saveLastCriteria: true,

                        request: (options) => {
                            options = getInputControlsDataProviderNewPageOptions(Object.assign({
                                name: this.id,
                                paginatedValuesOptions: this.paginatedValuesOptions
                            }, options));

                            var dfd = this.inputControlsDataProviderWithDataLabelHash.getData(this.dataUri, options);
                            Controls.Utils.showLoadingDialogOn(dfd, null, true)
                            return dfd;
                        }
                    });

                    this.initialSelectedValuesToLabelMap = getInitialSelectedValuesToLabelMap(initialSelectedValues);

                    this.multiSelect = new MultiSelect({
                        getData: this.dataProviderWithCache.getData,
                        selectedListOptions: {
                            formatLabel: (value) => {
                                return this.inputControlsDataProviderWithDataLabelHash.getLabelByValue(value)
                                     ?? this.initialSelectedValuesToLabelMap[value];
                            },
                            sortFunc: selectedItemsDataProviderSorterFactory.create(ControlsBase.NULL_SUBSTITUTION_LABEL)
                        },
                        resizable : true,
                        keydownTimeout: ControlsBase.KEY_DOWN_TIMEOUT
                    });

                    this.multiSelect.setDisabled(controlStructure.readOnly);

                    this._resize = _.debounce(_.bind(this.multiSelect.resize, this.multiSelect), 500);

                    if (controlStructure) {
                        _.extend(this, controlStructure);
                    }
                }
            },

            render() {
                const template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    this.multiSelect.undelegateEvents();

                    const element = jQuery(template(this));

                    this.multiSelect.render().renderData();

                    element.find(".msPlaceholder").append(this.multiSelect.el);

                    this.setElem(element);
                    this.multiSelect.delegateEvents();

                    this.makeResizable && this.makeResizable();
                    this.isVisible(this) && this.bindCustomEventListeners();
                }
            },

            setValue(selectedValues) {
                selectedValues = _.isArray(selectedValues) && _.first(selectedValues)=== RestParamsEnum.NOTHING_SUBSTITUTION_VALUE ? [] : selectedValues
                this.multiSelect.setValue(selectedValues, {silent: true});
                this._resize();
            },

            updateSelectionOnOptionChange(selection) {
                this.initialSelectedValuesToLabelMap = getInitialSelectedValuesToLabelMap(selection);
            },

            fetch(uri, paginatedValuesOptions) {
                const dfd = jQuery.Deferred();

                this.dataUri = uri;
                this.paginatedValuesOptions = paginatedValuesOptions;

                this.dataProviderWithCache.clear();

                this.multiSelect.off('listRenderError').once('listRenderError', (xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                    dfd.reject(xhr);

                    this.bindCustomEventListeners();
                });

                this.multiSelect.fetch((...args) => {
                    dfd.resolve(...args);
                });

                return dfd;
            },

            clearFilter() {
                this.multiSelect.clearFilter();
            },

            bindCustomEventListeners() {
                this.multiSelect.off("selection:change").on("selection:change", function (selection) {
                    this.fireControlSelectionChangeEvent(selection);
                }, this);

                this.multiSelect.off('listRenderError').on('listRenderError', (xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                });
            },

            get(attribute) {
                const result = this[attribute];

                if (attribute === 'selection' && _.isEmpty(result)) {
                    return [];
                } else {
                    return result;
                }
            },

            makeResizable() {
                const $sizer = this.multiSelect.$el.find(".jr-mSizer");

                //according to IC specifics, sizer should be after alert message
                if ($sizer.length){
                    $sizer.detach().insertAfter(this.getElem() && this.getElem().find('.resizeOverlay'));
                }
            }
        }),

        SingleSelectRadio: Controls.BaseControl.extend({
            initialize(controlStructure, options = {}) {
                controlStructure && _.extend(this, controlStructure);

                this.dataUri = options.dataUri;
                this.inputControlsService = options.inputControlsService;
                this.paginatedValuesOptions = options.paginatedValuesOptions;
                this.inputControlsDataProvider = new InputControlsDataProvider({
                    controlId: controlStructure.id,
                    inputControlsService: this.inputControlsService
                });
            },

            render() {
                const template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    const element = jQuery(template(this));
                    this.setElem(element);

                    this.makeResizable && this.makeResizable();
                    this.isVisible(this) && this.bindCustomEventListeners();
                }

                return this.fetch(this.dataUri, this.paginatedValuesOptions);
            },

            fetch(uri, paginatedValuesOptions) {
                return this._fetchData(uri, paginatedValuesOptions).then((result) => {
                    this._renderData(result.data);
                });
            },

            _fetchData(uri, paginatedValuesOptions) {
                this.dataUri = uri;
                this.paginatedValuesOptions = paginatedValuesOptions;

                const options = getInputControlsDataProviderNewPageOptions({
                    name: this.id,
                    paginatedValuesOptions: this.paginatedValuesOptions,
                    offset: 0
                });

                return this.inputControlsDataProvider.getData(this.dataUri, options);
            },

            _renderData(controlData) {
                let list = this.getElem() && this.getElem().find('ul')[0];
                if(list){
                    const data = controlData.map((val) => {
                        const result = _.clone(val);

                        result.readOnly = this.readOnly;
                        result.name = this.getOptionName();
                        result.uuid = _.uniqueId(this.id);

                        return result;
                    });

                    const template = this.getTemplateSection('data');

                    Controls.Utils.setInnerHtml(list, template, {
                        data
                    });

                    list = jQuery(list);
                    list.html(list.html() + "&nbsp;"); //workaround for IE scrollbar

                    //TODO move to decorator
                    if (list.find('li').length < 5 && this.getElem()[0].clientHeight < 125) {
                        this.getElem().find('.jr-mSizer').addClass('hidden');
                        this.getElem().find('.inputSet').removeClass('sizable').attr('style', false);
                    } else {
                        if (this.resizable) {
                            this.getElem().find('.jr-mSizer').removeClass('hidden');
                            this.getElem().find('.inputSet').addClass('sizable');
                        }
                    }
                }

            },

            getOptionName() {
                return this.id + "_option";
            },

            bindCustomEventListeners() {
                this.getElem().on('change', 'input', (evt) => {
                    const selection = evt.target.value;
                    // for better performance in IE7
                    setTimeout(() => {
                        this.set({selection:[selection]})
                    });
                });
            },

            //TODO move to decorator
            makeResizable() {
                this.resizable = true;

                const list = this.getElem().find('ul');
                const sizer = this.getElem().find('.jr-mSizer').removeClass('hidden');

                sizer.addClass("ui-resizable-s");
                list.resizable({
                    handles:{
                        's': sizer
                    }
                });
            }
        }),

        MultiSelectCheckbox: Controls.BaseControl.extend({
            initialize(controlStructure, options = {}) {
                controlStructure && _.extend(this, controlStructure);

                this.dataUri = options.dataUri;
                this.inputControlsService = options.inputControlsService;
                this.paginatedValuesOptions = options.paginatedValuesOptions;

                this.inputControlsDataProvider = new InputControlsDataProvider({
                    controlId: controlStructure.id,
                    inputControlsService: this.inputControlsService
                });
            },

            render() {
                const template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    const element = jQuery(template(this));
                    this.setElem(element);

                    this.makeResizable && this.makeResizable();
                    this.isVisible(this) && this.bindCustomEventListeners();
                }

                return this.fetch(this.dataUri, this.paginatedValuesOptions);
            },

            fetch(uri, paginatedValuesOptions) {
                return this._fetchData(uri, paginatedValuesOptions).then((result) => {
                    this._renderData(result.data);
                });
            },

            _fetchData(uri, paginatedValuesOptions) {
                this.dataUri = uri;
                this.paginatedValuesOptions = paginatedValuesOptions;

                const options = getInputControlsDataProviderNewPageOptions({
                    name: this.id,
                    paginatedValuesOptions: this.paginatedValuesOptions,
                    offset: 0
                });

                return this.inputControlsDataProvider.getData(this.dataUri, options);
            },

            _renderData(controlData) {
                const data = controlData.map((val) => {
                    const result = _.clone(val);

                    result.readOnly = this.readOnly;
                    result.uuid = _.uniqueId(this.id);

                    return result;
                });

                const list = this.getElem() && this.getElem().find('ul')[0];
                if(list){
                    const template = this.getTemplateSection('data');

                    Controls.Utils.setInnerHtml(list, template, {data:data});

                    //TODO move to decorator
                    if (jQuery(list).find('li').length < 5 && this.getElem()[0].clientHeight < 125) {
                        this.getElem().find('.jr-mSizer').addClass('hidden');
                        this.getElem().find('.inputSet').removeClass('sizable').attr('style', false);
                    } else {
                        if (this.resizable) {
                            this.getElem().find('.jr-mSizer').removeClass('hidden');
                            this.getElem().find('.inputSet').addClass('sizable');
                        }
                    }
                }

            },

            getSelection() {
                const boxes = this.getElem().find(":checkbox").filter(":checked");

                return _.map(boxes, function (box) {
                    return jQuery(box).val();
                });
            },

            bindCustomEventListeners() {
                this.getElem().on('change', 'input', (evt) => {
                    const selection = this.getSelection();
                    // for better performance in IE7
                    setTimeout(() => {
                        this.set({selection:selection})
                    });
                });

                this.getElem().on('click','a', (evt) => {
                    const options = this.getElem().find('input');
                    const name = jQuery(evt.target)[0].name;

                    if (name === "multiSelectAll" || name === "multiSelectNone") {
                        _.each(options, function(opt){
                            opt.checked = name === "multiSelectAll";
                        });
                    } else if (name === "multiSelectInverse") {
                        _.each(options,  function(opt){
                            opt.checked = !opt.checked;
                        });
                    }

                    this.getElem().find('input').change(); // trigger the cascading request
                });
            },

            get(attribute) {
                const result = this[attribute];

                if (attribute === 'selection' && _.isEmpty(result)) {
                    return [];
                } else {
                    return result;
                }
            },

            //TODO move to decorator
            makeResizable() {
                this.resizable = true;

                const list = this.getElem().find('ul');
                const sizer = this.getElem().find('.jr-mSizer').removeClass('hidden');

                sizer.addClass("ui-resizable-s");
                list.resizable({
                    handles:{
                        's': sizer
                    }
                });
            }
        })
    });
})(
    jQuery,
    _,
    JRS.Controls
);

export default JRS.Controls;

