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
 * @version: $Id$
 */

import resource from './resource.base';
import resourceLocator from './resource.locate';
import {ValidationModule} from "../util/utils.common";
import DataSourceController from '../dataSource/DataSourceController';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import jQuery from 'jquery';
import _ from 'underscore';

var resourceQuery = {
    STEP1_PAGE_ID: 'addResource_query_step1',
    STEP2_PAGE_ID: 'addResource_locateDataSource',
    STEP3_PAGE_ID: 'addResource_query_step3',
    LABEL_ID: 'query.label',
    RESOURCE_ID_ID: 'query.name',
    DESCRIPTION_ID: 'query.description',
    JUMP_TO_PAGE_ID: 'jumpToPage',
    // PREVIOUS_BUTTON_ID: "previous",
    DONE_BUTTON_ID: 'done',
    NEXT_BUTTON_ID: 'next',
    JUMP_TO_BUTTON_ID: 'jumpButton',
    NEW_DATA_SOURCE_LINK_ID: 'newDataSourceLink',
    LOCAL_DATA_SOURCE_ID: 'LOCAL',
    RESOURCE_URI_INPUT_ID: 'resourceUri',
    QUERY_SQL: 'query.sql',
    _canGenerateId: true,
    initialize: function (options) {
        this._step1Page = jQuery('#' +this.STEP1_PAGE_ID)[0];
        this._step2Page = jQuery('#' +this.STEP2_PAGE_ID)[0];
        this._step3Page = jQuery('#' +this.STEP3_PAGE_ID)[0];
        if (this._step1Page) {
            this._form = jQuery(document.body).find('form')[0];
            this._label = document.getElementById(this.LABEL_ID);
            this._resourceId = document.getElementById(this.RESOURCE_ID_ID);
            this._description = document.getElementById(this.DESCRIPTION_ID);
            this._label.validator = resource.labelValidator.bind(this);
            this._resourceId.validator = resource.resourceIdValidator.bind(this);
            this._description.validator = resource.descriptionValidator.bind(this);
        }
        if (this._step2Page) {
            this._form = jQuery(document.body).find('form')[0];
            this._dataSourceUri = document.getElementById(this.RESOURCE_URI_INPUT_ID);
            this._dataSourceUri.validator = resource.dataSourceValidator.bind(this);
        }
        if (this._step3Page) {
            this._form = jQuery(document.body).find('form')[0];
            this._query = document.getElementById(this.QUERY_SQL);
            this._query.validator = resource.queryValidator.bind(this);
        }
        this._nextButton = jQuery('#' +this.NEXT_BUTTON_ID);
        this._doneButton = jQuery('#' +this.DONE_BUTTON_ID);
        this._jumpToPage = jQuery('#' +this.JUMP_TO_PAGE_ID)[0];
        this._jumpButton = jQuery('#' +this.JUMP_TO_BUTTON_ID)[0];
        this._newDataSourceLink = jQuery('#' +this.NEW_DATA_SOURCE_LINK_ID)[0];
        this._localDataSource = jQuery('#' +this.LOCAL_DATA_SOURCE_ID)[0];
        this._isEditMode = options ? options.isEditMode : false;
        resourceLocator.initialize({
            resourceInput: 'resourceUri',
            browseButton: 'browser_button',
            newResourceLink: 'newDataSourceLink',
            treeId: 'queryTreeRepoLocation',
            providerId: 'dsTreeDataProvider',
            dialogTitle: resource.messages['resource.Query.Title'],
            selectLeavesOnly: true
        });
        this._initEvents();
    },
    _initEvents: function () {
        this._nextButton.on('click', function (e) {
            if (!this._isDataValid()) {
                e.stopPropagation();
            }
            if (this._localDataSource && this._localDataSource.checked) {
                e.preventDefault();
                e.stopPropagation();
                this._newDataSourceLink.click();
            }
        }.bind(this));
        this._doneButton.on('click', function (e) {
            if (!this._isDataValid()) {
                e.preventDefault();
            }
        }.bind(this));
        jQuery(this._form).on('keyup', function (e) {
            var element = e.target;
            if (element == this._resourceId && this._resourceId.getValue() != resource.generateResourceId(this._label.getValue())) {
                this._canGenerateId = false;
            }
            if (element == this._label && !this._isEditMode && this._canGenerateId) {
                this._resourceId.setValue(resource.generateResourceId(this._label.getValue()));
                ValidationModule.validate(resource.getValidationEntries([this._resourceId]));
            }
        }.bind(this));
        if (this._newDataSourceLink) {
            var self = this;
            this._newDataSourceLink.observe('click', function (e) {
                if (self._localDataSource.checked) {
                    var options = jrsConfigs.addDataSource ? _.clone(jrsConfigs.addDataSource.initOptions) : {};
                    options.saveFn = function (dataSource, model) {
                        dataSource.type = model.type;
                        var input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = 'dataSourceJson';
                        input.value = JSON.stringify(_.extend(model.toJSON(), {name: dataSource.name}));
                        self._form.appendChild(input);
                        input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = 'dataSourceType';
                        input.value = model.type;
                        self._form.appendChild(input);
                        input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = '_eventId_saveDatasource';
                        self._form.appendChild(input);
                        self._form.submit();
                    };
                    options.cancelFn = function () {
                        self._form.submit();
                    };
                    options.skipLocation = true;
                    if (window.localContext && window.localContext.initOptions && window.localContext.initOptions) {
                        if (window.localContext.initOptions.dataSourceJson) {
                            // data source page has been opened. Here are previously edited attributes and client type
                            options.isEditMode = true;
                            options.dataSource = window.localContext.initOptions.dataSourceJson;
                            options.dataSourceClientType = window.localContext.initOptions.dataSourceJson.type;
                        } else if (window.localContext.initOptions.dataSourceUri) {
                            // first edit. No model yet.
                            options.isEditMode = true;
                            options.resourceUri = window.localContext.initOptions.dataSourceUri;
                        }
                    }
                    var dataSourceController = new DataSourceController(options);
                    jQuery('#display').append(dataSourceController.$el);    // remove conflicting dom elements
                    // remove conflicting dom elements
                    jQuery('#selectFromRepository').remove();
                    dataSourceController.render();
                }
            }.bindAsEventListener(this));
        }
    },
    _isDataValid: function () {
        var res = true;
        if (this._label)
            res = res && ValidationModule.validate(resource.getValidationEntries([this._label]));
        if (this._resourceId) {
            if (this._resourceId.getValue() != resource.generateResourceId(this._label.getValue())) {
                this._canGenerateId = false;
            }
            if (!this._isEditMode && this._canGenerateId) {
                this._resourceId.setValue(resource.generateResourceId(this._label.getValue()));
                res = res && ValidationModule.validate(resource.getValidationEntries([this._resourceId]));
            }
        }
        if (this._description)
            res = res && ValidationModule.validate(resource.getValidationEntries([this._description]));    // When using Prototype JS to disable element its sets disabled property to appropriate state.
        // So, we should check its state using this property to handle states correctly.
        // When using Prototype JS to disable element its sets disabled property to appropriate state.
        // So, we should check its state using this property to handle states correctly.
        if (this._dataSourceUri && !this._dataSourceUri.disabled)
            res = res && ValidationModule.validate(resource.getValidationEntries([this._dataSourceUri]));
        if (this._query)
            res = res && ValidationModule.validate(resource.getValidationEntries([this._query]));
        return res;
    },
    jumpTo: function (pageTo) {
        this._jumpToPage.setValue(pageTo);
        this._jumpButton.click();
        return false;
    }
};

export default resourceQuery;