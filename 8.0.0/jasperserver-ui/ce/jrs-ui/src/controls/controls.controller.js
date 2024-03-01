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
 */
import jQuery from 'jquery';
import {JRS} from "../namespace/namespace";
import _ from 'underscore';

import './controls.core';
import './controls.datatransfer';
import './controls.viewmodel';
import './controls.components';

import 'jquery.urldecoder';

import request from 'js-sdk/src/common/transport/request';
import InputControlsService from './rest/InputControlsService';
import InputControlsServiceWithCache from './rest/InputControlsServiceWithCache';
import InputControlsReportViewerService from './service/InputControlsReportViewerService';

import { showErrorPopup } from '../core/core.ajax.utils';

import InputControlTypeEnum from './enum/inputControlTypeEnum';

const validatePaginatedValuesResponse = (response, controls) => {
    const controlsData = response && response.inputControlState ? response.inputControlState : [];
    let areControlsValid = true;

    controlsData.forEach((controlData) => {
        const control = controls[controlData.id];

        if (control && control.get('error')) {
            control.set({
                error: null
            });
        }

        if (controlData.error) {
            control.set({
                error: controlData.error
            });

            areControlsValid = false;
        }

        // singleValueNumber ICs may have a validated value e.g. from "1.20" to "1.2" that needs to be re-applied
        if (!controlData.error &&
            control && InputControlTypeEnum.SINGLE_VALUE_NUMBER === control.type &&
            controlData.value) {

            control.set({
                values: [controlData.value]
            });
        }
    });

    return areControlsValid;
};

;(function(jQuery,_, Controls) {

    //module:
    //
    //  controls.controller
    //
    //summary:
    //
    //  Connect input controls with server
    //
    //main types:
    //
    //  Controller - provide common functions around input controls, like update, reset, etc.
    //
    //dependencies:
    //
    //  jQuery          - v1.7.1
    //  _,              - underscore.js 1.3.1
    //  Controls        - controls.viewmodel and controls.datatransfer

    return _.extend(Controls, {

        //Provides common operations under input controls
        Controller : Controls.Base.extend({

            constructor: function(options) {
                _.bindAll(this,
                    "getViewModel",
                    "resetControlsToSelection",
                    "validate"
                );

                this.inputControlsService = new InputControlsServiceWithCache({
                    inputControlsService: new InputControlsService({
                        request
                    })
                });

                this.inputControlsReportViewerService = new InputControlsReportViewerService({
                    inputControlsService: this.inputControlsService
                });

                this.viewModel = options && options.viewModel?  options.viewModel : new Controls.ViewModel();

                // Common initialization
                this.initialize(options);

                Controls.ignore('reportoptions:selection:changed');
                Controls.listen({
                    "viewmodel:selection:changed": (...args) => {
                        this._onViewModelSelectionChange(...args);
                    },

                    "reportoptions:selection:changed": (...args) => {
                        this._onReportOptionsSelectionChanged(...args);
                    }
                });

                Controls.getController =_.bind(function () {
                    return this;
                }, this);

                // Triggered right after controller is initialized but before
                // this allows custom code to listen when controls are actually initialized
                // and override draw method of viewModel
                var event = new CustomEvent('controls:initialized', {
                    detail: this.getViewModel()
                });

                document.dispatchEvent(event);
            },

            _onViewModelSelectionChange: function(event, controlId, value, inCascade) {
                const viewModel = this.getViewModel();
                const controls = viewModel.getControls();
                if (value && inCascade) {
                    const dfd = this.inputControlsReportViewerService.fetchInputControlsValuesOnControlSelectionChange({
                        controlId,
                        value,
                        uri: this.dataUri,
                        structure: viewModel.structure,
                        selection: viewModel.get('selection'),
                        initialSelectedValues: viewModel.controlsOptions.initialSelectedValues
                    }).then((response, selectionPerControl, paginationOptions) => {
                        const promises = paginationOptions.map((controlPaginationOptions) => {
                            const controlId = controlPaginationOptions.name;

                            return controls[controlId].fetch(this.dataUri, paginationOptions);
                        });

                        return jQuery.when(...promises).then(() => {
                            return jQuery.Deferred().resolve(selectionPerControl, response);
                        });
                    }).then((selectionPerControl, response) => {
                        selectionPerControl = Object.assign({}, selectionPerControl, {
                            [controlId]: value
                        });

                        _.each(selectionPerControl, (values, id) => {
                            controls[id].set({ values });
                        });

                        validatePaginatedValuesResponse(response, controls);
                    }).catch((xhr) => {
                        const control = controls[controlId];

                        control.set({
                            values: control.selection
                        });

                        showErrorPopup(xhr.responseJSON.message);

                        return jQuery.Deferred().reject(xhr);
                    });

                    Controls.Utils.showLoadingDialogOn(dfd, null, true);
                }else{
                    controlId && this.viewModel.controls[controlId].set({'selection':value},true);
                }
            },

            _onReportOptionsSelectionChanged: function(event, data) {
                const {
                    reportOption,
                    previousReportOption
                } = data;

                const viewModel = this.viewModel;
                const controls = viewModel.getControls();

                this.dataUri = reportOption && reportOption.uri || this.dataUri;

                const dfd = this.inputControlsReportViewerService.fetchInitialInputControlsValuesByUri(
                    this.dataUri,
                    viewModel.structure,
                    data.selectedData
                ).then((options) => {
                    const {
                        selection, paginatedValuesResponse, paginationOptionsPerControl
                    } = options;

                    const promises = _.map(paginationOptionsPerControl, (controlPaginatedOptions, id) => {
                        const control = controls[id];
                        // update initialselectedvalues on report option change
                        control.updateSelectionOnOptionChange && control.updateSelectionOnOptionChange(selection[id] || [])

                        return control.fetch(this.dataUri, controlPaginatedOptions);
                    });

                    return jQuery.when(...promises).then(() => {
                        return jQuery.Deferred().resolve(selection, paginatedValuesResponse);
                    });
                }).then((selection, response) => {
                    _.each(controls,(control,key) =>{
                        control.set({
                            values: _.pluck(selection[key], 'value')
                        })
                    })

                    validatePaginatedValuesResponse(response, controls);
                }).catch((xhr) => {
                    showErrorPopup(xhr.responseJSON.message);

                    Controls.reportOptions.set({
                        selection: previousReportOption
                    }, true);

                    return jQuery.Deferred().reject(xhr);
                });

                Controls.Utils.showLoadingDialogOn(dfd, null, true);
            },

            /**
             * Common initialization method that can be overridden by inherited classes
             * @param args
             */
            initialize : function(options) {
                this.dataUri = options.reportOptionUri || options.reportUri;
            },

            fetchAndSetInputControlsState: function (allRequestParameters) {
                var dfd = this.inputControlsReportViewerService.fetchInputControlsInitialState(
                    this.dataUri, allRequestParameters
                ).then((options) => {
                    if(options) {
                        this.viewModel.set({
                            structure: options.structure,
                            controlsOptions: {
                                dataUri: this.dataUri,
                                inputControlsService: this.inputControlsService,
                                initialSelectedValues: options.selection,
                                paginatedValuesOptions: options.paginationOptionsPerControl,
                                paginatedValuesResponse: options.paginatedValuesResponse
                            }
                        });

                        const viewModel = this.getViewModel();
                        const controls = viewModel.getControls();

                        validatePaginatedValuesResponse(options.paginatedValuesResponse, controls);
                    }
                    return jQuery.Deferred().resolve(options);
                });
                Controls.Utils.showLoadingDialogOn(dfd, null, true);
                return dfd;
            },

            //Returns object responsible for initialization and drawing of controls
            getViewModel:function () {
                return this.viewModel;
            },

            //update controls by given selection or by current selection
            resetControlsToSelection: function(selectedData) {
                const viewModel = this.getViewModel();
                const controls = viewModel.getControls();

                if (!selectedData) {
                    selectedData = viewModel.get("selection");
                }

                return this.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri(
                    selectedData,
                    this.dataUri,
                    viewModel.structure
                ).then((response, paginationOptions, paginationOptionsPerControl) => {
                    const promises = paginationOptions.map((controlPaginationOptions) => {
                        const control = controls[controlPaginationOptions.name];
                        control.clearFilter();
                        return control.fetch(this.dataUri,
                            paginationOptionsPerControl[controlPaginationOptions.name] || paginationOptions);
                    });

                    return jQuery.when(...promises).then(() => {
                        return response;
                    });
                }).then((response) => {
                    validatePaginatedValuesResponse(response, controls);

                    _.each(controls, (control, id) => {
                        const value = selectedData[id];
                        let options;

                        if (value) {
                            options = {
                                values: value
                            };
                        } else {
                            options = {
                                error: null,
                                values: undefined,
                                selection: undefined
                            };
                        }

                        control.reset(options);
                    });
                });
            },

            //validate controls values and update only invalid controls
            validate: function() {
                const viewModel = this.getViewModel();
                const controls = viewModel.getControls();

                const selectedData = viewModel.get("selection");
                const dfd = this.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri(
                    selectedData,
                    this.dataUri,
                    viewModel.structure
                ).then((response) => {
                    const areControlsValid = validatePaginatedValuesResponse(response, controls);
                    return jQuery.Deferred().resolve(areControlsValid);
                });
                Controls.Utils.showLoadingDialogOn(dfd, null, true);
                return dfd;
            }
        })
    });
})(
    jQuery,
    _,
    JRS.Controls
);

export default JRS.Controls;
