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
/* global viewer, confirm, alert, Report */
import {$, $$} from 'prototype';
import {JRS} from "../namespace/namespace";
import layoutModule from '../core/core.layout';
import Controls from './controls.core';
import {
    centerElement,
    matchAny,
    triggerNativeEvent,
    isIPad,
    selectAndFocusOn
} from "../util/utils.common";
import dialogs from '../components/components.dialogs';
import {ControlsBase, OptionsDialog, ControlDialog} from "./controls.base";
import ReportOptionsStub from './controls.options';
import '../reportViewer/report.view.base';
import {isProVersion} from "../namespace/namespace";
import Report from '../reportViewer/report.view.base';
import './controls.controller';
import inputControlsSettings from '../settings/inputControls.settings';
import jQuery from 'jquery';
import _ from 'underscore';
import ConfirmationDialog from 'js-sdk/src/common/component/dialog/ConfirmationDialog';
import Overlay from 'js-sdk/src/components/overlay/Overlay';
import { showErrorPopup } from '../core/core.ajax.utils';

const isMandatoryInputControls = () => {
    const atLeastOneParameterDoesntHaveDefaultOrUrlValue = !_.isUndefined(_.find(Report.parametersWithoutDefaultValues, function (parameterName) {
        return !_.contains(_.keys(Report.getAllRequestParameters()), parameterName);
    }));

    // Report.reportParameterValues means that parameters were saved in flow and were put on jsp, we do it when we go drill through.
    // _.isEmpty(Report.reportParameterValues) means that we are not returning from drill through.

    return (Report.hasInputControls && (Report.reportForceControls || atLeastOneParameterDoesntHaveDefaultOrUrlValue && _.isEmpty(Report.reportParameterValues)));
};

var ControlsReport = function (jQuery, _, Controls, Report) {
    return _.extend(Controls, {
        _messages: {},
        layouts: {
            LAYOUT_POPUP_SCREEN: 1,
            LAYOUT_SEPARATE_PAGE: 2,
            LAYOUT_TOP_OF_PAGE: 3,
            LAYOUT_IN_PAGE: 4
        },
        controlDialog: null,
        reportOptionsDialog: null,
        inputControlsLocation: null,
        toggleControlsOn: false,
        controller: null,
        hideControls: null,
        selectionChanged: true,
        initialInputControlsFetched: false,
        initialize: function () {
            this.controller = new JRS.Controls.Controller({
                reportUri: Report.reportUnitURI,
                reportOptionUri: Report.reportOptionsURI
            }); // Pre-selected parameters have priority over url ones.

            this.viewModel = Controls.controller.getViewModel();
            this.initializeOptions();

            const confirmInputValues = _.debounce(() => {
                Controls.applyInputValues(true);
            }, Controls.Utils.LOADING_DIALOG_DELAY, true);

            const applyInputValues = _.debounce(() => {
                Controls.applyInputValues();
            }, Controls.Utils.LOADING_DIALOG_DELAY, true);

            const cancelInputValues = _.debounce(() => {
                Controls.cancel();
            }, Controls.Utils.LOADING_DIALOG_DELAY, true);

            const resetInputValues = _.debounce(() => {
                Controls.resetToInitial();
            }, Controls.Utils.LOADING_DIALOG_DELAY, true);

            this.buttonActions = {
                'button#ok': confirmInputValues,
                'button#apply': applyInputValues,
                'button#cancel': cancelInputValues,
                'button#reset': resetInputValues,
                'button#save': Controls.save,
                'button#remove': Controls.remove
            };

            var dialogButtonActions = {
                'button#ok': confirmInputValues,
                'button#cancel': cancelInputValues,
                'button#reset': resetInputValues,
                'button#apply': applyInputValues,
                'button#save': Controls.save,
                'button#remove': Controls.remove
            };

            if (jQuery('#' + ControlsBase.INPUT_CONTROLS_DIALOG)[0]) {
                this.controlDialog = new ControlDialog(dialogButtonActions);
            }

            if (jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM)[0]) {
                jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM).on('click', function (e) {
                    var elem = e.target;    //                observe Input Controls buttons
                    for (var pattern in this.buttonActions) {
                        if (matchAny(elem, [pattern], true)) {
                            this.buttonActions[pattern]();
                            e.stopPropagation();
                            return;
                        }
                    }
                }.bindAsEventListener(this));
            }

            this.inputControlsLocation = jQuery('#' + ControlsBase.INPUT_CONTROLS_CONTAINER)[0] ? jQuery('#' + ControlsBase.INPUT_CONTROLS_CONTAINER)[0] : jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM)[0];

            if (jQuery('#' + ControlsBase.TOOLBAR_CONTROLS_BUTTON)[0]) {
                this.toggleControlsOn = jQuery('#' + ControlsBase.TOOLBAR_CONTROLS_BUTTON).hasClass('down');
            }

            JRS.Controls.listen({
                'viewmodel:selection:changed': function () {
                    Controls.selectionChanged = true;
                },
                'reportoptions:selection:changed': function (event, data) {
                    Controls.selectionChanged = true;
                }
            });

            const mandatoryInputControls = isMandatoryInputControls();
            const shouldFetchInputControlsOnInitialize = mandatoryInputControls || (Report.hasInputControls &&  Controls.layouts.LAYOUT_IN_PAGE === Report.reportControlsLayout );

            if (Report.hasInputControls) {
                const dfd = this._fetchAndSetInputControlsStateOnce().then(() => {
                    const viewModel = Controls.controller.getViewModel();
                    const isValidSelection = viewModel.areAllControlsValid();

                    // when their is wrong URL params show control dailog
                    if (mandatoryInputControls || !isValidSelection) {
                        Controls.show();

                        Controls.controlDialog && Controls.controlDialog.show();
                    } else {
                        return Controls.refreshReport().catch(() => {
                            return jQuery.Deferred().resolve();
                        });
                    }
                }).catch((xhr) => {
                    const refreshReportDfd = Controls.refreshReport();
                    Controls.Utils.showLoadingDialogOn(refreshReportDfd, null, true);
                }).always(() => {
                    const viewModel = Controls.controller.getViewModel();
                    const isValidSelection = viewModel.areAllControlsValid();

                    if (mandatoryInputControls || !isValidSelection) {
                        if (Report && Report.nothingToDisplay) {
                            Report.nothingToDisplay.removeClass(layoutModule.HIDDEN_CLASS);

                            centerElement(Report.nothingToDisplay, {
                                horz: true,
                                vert: true
                            });

                            jQuery('#' + Report.DATA_REFRESH_BUTTON).attr(layoutModule.DISABLED_ATTR_NAME,
                                layoutModule.DISABLED_ATTR_NAME);
                        }
                    }
                });

                Controls.Utils.showLoadingDialogOn(dfd, null, true);
            } else {
                const dfd = Controls.refreshReport();

                Controls.Utils.showLoadingDialogOn(dfd, null, true);
            }
        },

        initializeOptions: function () {
            function showSubHeader() {
                var parent;
                if (Controls.layouts.LAYOUT_POPUP_SCREEN == Report.reportControlsLayout) {
                    parent = jQuery('#' + ControlsBase.INPUT_CONTROLS_DIALOG);
                } else {
                    parent = jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM);
                }
                if (parent && parent.length > 0) {
                    parent.addClass('showingSubHeader');
                }
            }
            function hideSubHeader() {
                var parent;
                if (Controls.layouts.LAYOUT_POPUP_SCREEN == Report.reportControlsLayout) {
                    parent = jQuery('#' + ControlsBase.INPUT_CONTROLS_DIALOG);
                } else {
                    parent = jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM);
                }
                if (parent && parent.length > 0) {
                    parent.removeClass('showingSubHeader');
                }
            }
            if (isProVersion()) {
                var optionsContainerSelector;
                if (this.layouts.LAYOUT_POPUP_SCREEN == Report.reportControlsLayout) {
                    optionsContainerSelector = '#' + ControlsBase.INPUT_CONTROLS_DIALOG + ' .sub.header';
                } else if (this.layouts.LAYOUT_TOP_OF_PAGE == Report.reportControlsLayout) {
                    optionsContainerSelector = '#' + ControlsBase.INPUT_CONTROLS_FORM + ' .sub.header';
                } else {
                    optionsContainerSelector = '#' + ControlsBase.INPUT_CONTROLS_FORM + ' .sub.header';
                }
                const ReportOptions = JRS.Controls.ReportOptions || ReportOptionsStub;
                var reportOptions = new ReportOptions();
                reportOptions.fetch(Report.reportUnitURI, Report.reportOptionsURI).done(function () {
                    jQuery(optionsContainerSelector).append(reportOptions.getElem());
                    Controls.lastReportOptionsSelection = reportOptions.get('selection');
                }).fail(function () {
                    jQuery(optionsContainerSelector).addClass('hidden');
                }).always(function () {
                    if (!Controls.lastReportOptionsSelection) {
                        Controls.lastReportOptionsSelection = reportOptions.get('defaultOption');
                    }
                });
                reportOptions.updateWarningMessage = function () {
                    Controls.reportOptionsDialog.showWarning(this.error);
                };
                JRS.Controls.listen({
                    'viewmodel:selection:changed': function () {
                        var option = reportOptions.find({ uri: Report.reportUnitURI });
                        reportOptions.set({ selection: option }, true);
                    },
                    'viewmodel:order:changed': _.bind(function (event, reorderedStructure) {
                        Report.newIcOrder = _.pluck(reorderedStructure, 'id').join(';');
                    }, this)
                });
                var optionsButtonActions = {
                    'button#saveAsBtnSave': function () {
                        var optionName = Controls.reportOptionsDialog.input.getValue();
                        var selectedData = Controls.viewModel.get('selection');
                        var overwrite = optionName === Controls.reportOptionsDialog.optionNameToOverwrite;
                        reportOptions.add(Report.reportUnitURI, optionName, selectedData, overwrite).done(function () {
                            Controls.reportOptionsDialog.hideWarning();
                            dialogs.systemConfirm.show(ControlsBase.getMessage('report.options.option.saved'));
                            showSubHeader();
                            var container = reportOptions.getElem().parent();
                            if (container.length > 0) {
                                container.removeClass('hidden');
                            } else {
                                jQuery(optionsContainerSelector).removeClass('hidden');
                                jQuery(optionsContainerSelector).append(reportOptions.getElem());
                            }
                            if (Controls.layouts.LAYOUT_TOP_OF_PAGE == Report.reportControlsLayout) {
                                jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM + ' .header').removeClass('hidden');
                            }
                            Controls.reportOptionsDialog.hide();
                            delete Controls.reportOptionsDialog.optionNameToOverwrite;
                        }).fail(function (err) {
                            if (err) {
                                try {
                                    var errorResponse = JSON.parse(err.responseText);
                                    //check on error  for overwrite
                                    if (errorResponse.errorCode === 'report.options.dialog.confirm.message') {
                                        !overwrite && (Controls.reportOptionsDialog.optionNameToOverwrite = optionName);
                                    }
                                } catch (e) {
                                }    // In this scenario security error is handled earlier, in errorHandler, so we can ignore exception here.
                                // Comment this because it will not work in IE, but can be uncommented for debug purpose.
                                // console.error("Can't parse server response: %s", "controls.core", err.responseText);
                            }
                        });
                    },
                    'button#saveAsBtnCancel': function () {
                        Controls.reportOptionsDialog.hide();
                        delete Controls.reportOptionsDialog.optionNameToOverwrite;
                    }
                };
                if (jQuery('#' + ControlsBase.SAVE_REPORT_OPTIONS_DIALOG)[0]) {
                    this.reportOptionsDialog = new OptionsDialog(optionsButtonActions);
                }
                this.remove = function () {
                    var optionName = reportOptions.get('selection').label;

                    var dialog = new ConfirmationDialog({
                        text: ControlsBase.getMessage("report.options.option.confirm.remove", {option: optionName})
                    });

                    dialog.on("button:yes", function() {
                        reportOptions.removeOption(Report.reportUnitURI, reportOptions.get('selection').id).done(function () {
                            if (!reportOptions.get('values')) {
                                hideSubHeader();
                                var container = reportOptions.getElem().parent();
                                if (container.length > 0) {
                                    container.addClass('hidden');
                                } else {
                                    jQuery(optionsContainerSelector).addClass('hidden');
                                    jQuery(optionsContainerSelector).html('');
                                }
                                if (Controls.layouts.LAYOUT_TOP_OF_PAGE == Report.reportControlsLayout) {
                                    jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM + ' .header').addClass('hidden');
                                }
                            }
                            reportOptions.enableRemoveButton(false);    // change the Remove button to Save
                            dialogs.systemConfirm.show(ControlsBase.getMessage("report.options.option.removed"));
                        });
                        dialog.remove();
                    });

                    dialog.on("button:no", function(){
                        dialog.remove();
                    });
                    dialog.open();
                };
                Controls.reportOptions = reportOptions;
            }
        },
        cancel: function () {
            if (Report.reportControlsLayout === Controls.layouts.LAYOUT_SEPARATE_PAGE
                && Controls.separatePageICLayoutFirstShow) {
                Report.goBack();
            } else {
                const refreshControlsToSelectionDfd = this._refreshControlsToSelection(Controls.lastSelection);

                refreshControlsToSelectionDfd.then(() => {
                    // below call is to fetch report, since we already fetch report with last selected values
                    // return Controls.refreshReport().catch(() => {
                    //     return jQuery.Deferred().resolve();
                    // });
                }).catch((xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                }).always(function () {
                    if (Report.reportControlsLayout === Controls.layouts.LAYOUT_POPUP_SCREEN) {
                        Controls.controlDialog.hide();
                    } else if (Report.reportControlsLayout === Controls.layouts.LAYOUT_SEPARATE_PAGE) {
                        Controls.showReport();
                    }
                    if (Controls.reportOptions) {
                        if (Controls.lastReportOptionsSelection) {
                            Controls.reportOptions.set({ 'selection': Controls.lastReportOptionsSelection }, true);
                        }
                    }
                });

                Controls.Utils.showLoadingDialogOn(refreshControlsToSelectionDfd, null, true);
            }
        },

        _refreshControlsToSelection: function(selection) {
            return Controls.controller.resetControlsToSelection(selection);
        },

        getLastSelection: function () {
            return this.lastSelection;
        },

        save: function () {
            if (Controls.selectionChanged) {
                Controls.controller.validate().then(Controls.showOptionDialog);
            } else {
                Controls.showOptionDialog();
            }
        },
        refreshReport: function (checkOnChangedSelection, freshData) {
            if (checkOnChangedSelection) {
                const deferred = new jQuery.Deferred();
                let promise = deferred.promise();
                const selectedData = Controls.viewModel.get('selection');

                const isSelectionChanged = JRS.Controls.ViewModel.isSelectionChanged(Controls.lastSelection, selectedData);

                if (!isSelectionChanged) {
                    deferred.resolve();
                    return promise();
                }
            }

            return this._refreshReportOnConfirm(freshData);
        },

        _refreshReportOnConfirm: function (freshData) {
            const selectedData = Controls.viewModel.get('selection');
            let promise;

            try {
                if (selectedData && !_.isEmpty(selectedData)) {
                    promise = Report.refreshReport({freshData: freshData}, null,selectedData);
                    Controls.lastSelection = selectedData;
                } else {
                    promise = Report.refreshReport();
                    Controls.lastSelection = {};
                }

                return promise;
            } catch (ex) {
                return jQuery.Deferred().reject(ex);
            }
        },

        _refreshReportOnApply: function() {
            const dfd = jQuery.Deferred();
            const selectedData = Controls.viewModel.get('selection');

            if (selectedData && !_.isEmpty(selectedData)) {
                return Report.refreshReport(null, null, selectedData);
            }

            return dfd.resolve();
        },

        applyInputValues: function (hideControls, freshData) {
            let _continue = true;
            if (window.viewer && window.viewer.isExportRunning()) {
                _continue = confirm(Report.getMessage("jasper.report.view.export.in.progress.confirm.continue"));
                window.viewer.confirmedExportCancel = _continue;
            }
            if (!_continue) {
                Controls.resetToInitial();
                hideControls && Controls.hide();
                return;
            }

            const viewModel = Controls.viewModel;
            if (Controls.selectionChanged) {
                Controls.controller.validate().then((areAllControlsValid) => {
                    if (areAllControlsValid) {
                        const lastSuccessfulSelection = Controls.lastSelection;
                        let lastSuccessfulReportOption;

                        if (Controls.reportOptions) {
                            lastSuccessfulReportOption = Controls.lastReportOptionsSelection;
                        }
                        // Fix for bug #42128 - jive must be hidden before applying input controls values
                        window.viewer && window.viewer.jive && window.viewer.jive.hide();
                        hideControls && Controls.hide();
                        var refreshReportPromise = Controls.refreshReport(null, freshData);
                        refreshReportPromise && refreshReportPromise.then(function () {
                            Controls.selectionChanged = false;
                        }, function () {
                            // using overlay here solely for this error dialog,
                            // because components.dialogs share state, closing built-in pageDimmer
                            // when the option is set to make it visible
                            const overlay = new Overlay({
                                zIndex: dialogs.errorPopup.getZIndex() - 1
                            });

                            jQuery("body").append(overlay.$el);

                            overlay.show();
                            //application of input controls was failed probably because of
                            //report cancellation
                            //in this case if hideControls is true we have to set values to
                            //latest successfull value
                            Controls.lastSelection = lastSuccessfulSelection;
                            Controls.lastReportOptionsSelection = lastSuccessfulReportOption;

                            // refresh report with IC after an error, on error dialog close
                            dialogs.errorPopup.onClose = function() {
                                Report.hasError = true;
                                if (Controls.lastSelection && !_.isEmpty(Controls.lastSelection)) {
                                    Report.refreshReport({freshData: freshData}, null,
                                        Controls.lastSelection);
                                } else {
                                    Report.refreshReport({freshData: freshData});
                                }

                                overlay.remove();
                            };

                            if (hideControls) {
                                Controls.cancel();
                                Controls.hide();

                                Controls.selectionChanged = false;
                            }
                        });

                        if (Controls.reportOptions) {
                            Controls.lastReportOptionsSelection = Controls.reportOptions.get('selection');
                            if(!refreshReportPromise) {
                                dialogs.popup.hide(jQuery('#loading')[0]);
                            }
                        }
                    }
                });
            } else if (viewModel.areAllControlsValid()) {
                hideControls && Controls.hide();
            }
        },
        resetToInitial: function () {
            const refreshControlsToSelectionDfd = this._refreshControlsToSelection(Controls.initialSelection)
                .then(() => {
                    Controls.selectionChanged = true;

                    // after reset button click "saved values" dropdown should be update
                    var reportOptions = Controls.reportOptions;
                    if (reportOptions) {
                        var option = reportOptions.find({ uri: Report.reportOptionsURI ? Report.reportOptionsURI : Report.reportUnitURI });
                        if (option) {
                            reportOptions.set({ selection: option }, true);
                            return;
                        }
                    }
                }, (xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                });

            Controls.Utils.showLoadingDialogOn(refreshControlsToSelectionDfd, null, true);
            // Controls.selectionChanged = true;
            //
            // var reportOptions = Controls.reportOptions;
            //
            // if (reportOptions) {
            //     var option = reportOptions.find({ uri: Report.reportOptionsURI ? Report.reportOptionsURI : Report.reportUnitURI });
            //     if (option) {
            //         reportOptions.set({ selection: option });
            //         return;
            //     }
            // }
            //
            // var finalInitialParameters = null;
            //
            // if (inputControlsSettings.useUrlParametersOnReset === 'true') {
            //     //use params passed through url as defaults
            //     finalInitialParameters = _.extend(Report.getAllRequestParameters(), Report.reportParameterValues);
            // }
            //
            // Controls.controller.reset(null, finalInitialParameters);
        },
        show: function () {
            switch (Report.reportControlsLayout) {
            case 2:
                Controls.showControls();
                break;
            case 3:
                Controls.toggleControls();
                break;
            case 4:
                /* Controls "in page" cannot be opened or closed, they're always shown. */
                break;
            default:
                Controls.showDialog();
            }
        },
        hide: function () {
            switch (Report.reportControlsLayout) {
            case 2:
                Controls.showReport();
                break;
            case 3:
                /* Controls "top of page" can be closed only using input controls button. */
                break;
            case 4:
                /* Controls "in page" cannot be opened or closed, they're always shown. */
                break;
            default:
                Controls.controlDialog.hide();
            }
        },
        toggleControls: function () {
            const hideInputControlsPanel = () => {
                jQuery('#' + ControlsBase.TOOLBAR_CONTROLS_BUTTON).removeClass('down').addClass('up');
                jQuery('.panel.pane.inputControls').addClass(layoutModule.HIDDEN_CLASS)[0];    // Due to the html markup of the AdHoc Chart, there is an issue when the

                // Due to the html markup of the AdHoc Chart, there is an issue when the
                // AdHoc Chart Report is rendering and there is an TopOfThePage Control there:
                // they interfere with each other because AdHoc uses absolute positioning to get as much space as it needs,
                // and this prevents controls from being visible.
                // So, when controls are getting closed, we need to re-launch the "resize" event to let viewer.js to re-calculate the position of the window
                // TODO: This should use eventAutomation.triggerNativeEvent when this is converted to use RequireJS
                triggerNativeEvent('resize');
            };

            const showInputControlsPanel = () => {
                jQuery('#' + ControlsBase.TOOLBAR_CONTROLS_BUTTON).removeClass('up').addClass('down');
                jQuery('.panel.pane.inputControls').removeClass(layoutModule.HIDDEN_CLASS)[0];
            };

            if (Controls.toggleControlsOn) {
                hideInputControlsPanel();
            } else {
                this._fetchAndSetInputControlsStateOnce().then(() => {
                    showInputControlsPanel();
                }).catch((xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                });
            }

            Controls.toggleControlsOn = !Controls.toggleControlsOn;

            isIPad() && Report.touchController.reset();
            /**
             * Fix to force rendering of input controls on webkit.
             */
            jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM).show().height();
        },
        showDialog: function () {
            if (Controls.controlDialog) {
                this._fetchAndSetInputControlsStateOnce().then(() => {
                    Controls.controlDialog.show();
                }).catch((xhr) => {
                    showErrorPopup(xhr.responseJSON.message);
                });
            }
        },
        showReport: function () {
            jQuery('#' + layoutModule.PAGE_BODY_ID).removeClass(layoutModule.CONTROL_PAGE_CLASS).addClass(layoutModule.ONE_COLUMN_CLASS);
        },
        showControls: function () {
            const showControlsPage = () => {
                jQuery('#' + layoutModule.PAGE_BODY_ID).removeClass(layoutModule.ONE_COLUMN_CLASS).addClass(layoutModule.CONTROL_PAGE_CLASS);
                document.getElementById(ControlsBase.INPUT_CONTROLS_FORM) && jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM).show();
            };

            this._fetchAndSetInputControlsStateOnce().then(() => {
                showControlsPage();
            }).catch((xhr) => {
                showErrorPopup(xhr.responseJSON.message);
            });
        },
        showOptionDialog: function () {
            JRS.Controls.Utils.wait(200).then(function () {
                //workaround for bug 27415,
                //because can't prevent bubbling up to parent dialog window,
                //so add delay and only then shows options dialog on top of controls dialog
                if (Controls.viewModel.areAllControlsValid()) {
                    Controls.reportOptionsDialog.show();
                    selectAndFocusOn(Controls.reportOptionsDialog.input);
                }
            });
        },

        _fetchAndSetInputControlsStateOnce: function() {
            const dfd = jQuery.Deferred();

            if (!this.initialInputControlsFetched) {
                const allRequestParameters = _.extend(Report.getAllRequestParameters(), Report.reportParameterValues);
                this.controller.fetchAndSetInputControlsState(allRequestParameters).then(() => {
                    this.initialInputControlsFetched = true;

                    const viewModel = Controls.controller.getViewModel();
                    const selection = viewModel.get('selection');

                    Controls.lastSelection = selection;
                    Controls.initialSelection = selection;

                    return dfd.resolve();
                }, (...args) => {
                    dfd.reject(...args);
                });

                Controls.Utils.showLoadingDialogOn(dfd, null, true);
            } else {
                dfd.resolve();
            }

            return dfd;
        }
    });
}(jQuery, _, Controls, Report);

export default ControlsReport;