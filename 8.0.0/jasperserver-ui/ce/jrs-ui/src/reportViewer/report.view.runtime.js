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
/* global alert*/


import Report from './report.view.base';
import Controls from '../controls/controls.report';
import {ControlsBase} from "../controls/controls.base";
import toolbarButtonModule from '../components/components.toolbarButtons.events';
import {
    matchAny,
    isIPad,
    doNothing,
    isSupportsTouch,
    centerElement,
    addDataToForm
} from "../util/utils.common";
import {JRS} from "../namespace/namespace";
import TouchController from '../util/touch.controller';
import layoutModule from '../core/core.layout';
import dialogs from '../components/components.dialogs';
import {ajax} from "../core/core.ajax";
import buttonManager from '../core/core.events.bis';
import _ from 'underscore';
import jQuery from 'jquery';
import stdnav from 'js-sdk/src/common/stdnav/stdnav';
import scheduleDialog from '../scheduler/util/schedulerSaveDialog';
import i18n from '../i18n/jasperserver_messages.properties';

Report.toolbarActionMap = {
    'back': 'Report.goBack',
    'ICDialog': 'Controls.show',
    'export': doNothing,
    'undo': 'Report.undo',
    'redo': 'Report.redo',
    'undoAll': 'Report.undoAll',
    'schedule': 'Report.schedule',
    'embed':'Report.embedViz'
};    /*
    Init & reportRefreshed methods.
 */
/*
    Init & reportRefreshed methods.
 */
jQuery.extend(Report, {
    initialize: function () {
        this.commonInit();
        Report.exportForm = jQuery('#' + Report.EXPORT_ACTION_FORM)[0];
        this.nothingToDisplay = jQuery('#nothingToDisplay');
        if (jQuery('#' + Report.DATA_REFRESH_BUTTON)[0]) {
            jQuery('#' + Report.DATA_REFRESH_BUTTON).on('mouseup', function () {
                let cancelRefresh = false;
                if (window.viewer && window.viewer.isExportRunning()) {
                    window.viewer.confirmedExportCancel = confirm(i18n["jasper.report.view.export.in.progress.confirm.continue"]);
                    if (!window.viewer.confirmedExportCancel) {
                        cancelRefresh = true;
                    }
                }

                if (!cancelRefresh) {
                    const freshData = true;
                    if (typeof Controls !== 'undefined') {
                        var selectedData = Controls.viewModel.get('selection');
                        var controlsUri = ControlsBase.buildSelectedDataUri(selectedData);
                        if (Report.hasInputControls && Controls.selectionChanged){
                            Controls.applyInputValues(null, freshData);
                        } else {
                            this.refreshReport({freshData: freshData}, null, controlsUri ? '&' + controlsUri : '', selectedData);
                        }
                    } else {
                        this.refreshReport({ freshData: freshData }, null, '', null);
                    }
                }
            }.bindAsEventListener(this));
        }
        if (jQuery('#' + Report.ASYNC_CANCEL_BUTTON)[0]) {
            jQuery('#' + Report.ASYNC_CANCEL_BUTTON).on('mouseup', function () {
                this.cancelReportAsyncExecution(true);
            }.bindAsEventListener(this));
        }    /**
         *  Init toolbar
         */
        /**
         *  Init toolbar
         */
        toolbarButtonModule.ACTION_MODEL_TAG = Report.TOOLBAR_SUBMENU;
        toolbarButtonModule.initialize(Report.toolbarActionMap);

        if (jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0]) {
            // observe page navigation input
            jQuery('#' + Report.PAGINATION_PAGE_CURRENT).on('change', function (e) {
                this.goToPage(jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0].getValue());
                e.stopPropagation();
            }.bindAsEventListener(this));    // observe page navigation buttons
            // observe page navigation buttons
            jQuery('.toolsRight').on('click', '.button', function(e) {
                var elem = e.target;
                e.stopPropagation();
                for (var pattern in this.pageActions) {
                    if (matchAny(elem, [pattern], true)) {
                        this.navigateToReportPage(this.pageActions[pattern]);
                        return;
                    }
                }
            }.bindAsEventListener(this));
        }
        if (document.location.href.indexOf('noReturn=true') > 0) {
            buttonManager.disable(jQuery('#back'));
        }
        if (typeof Controls !== 'undefined') {
            Controls.initialize.bind(Controls)(Report.refreshReport);
        } else {
            Report.refreshReport();
        }
    },
    reportRefreshed: function (silentUpdate) {
        //        Report.snapshotSaveStatus = null;
        var fr, r;    /*
         * Adding following line to force Google chrome to display ajax content
         * injected into #reportContainer. Bug fix 23988.
         */
        /*
         * Adding following line to force Google chrome to display ajax content
         * injected into #reportContainer. Bug fix 23988.
         */
        if (typeof JRS !== 'undefined' && JRS.fid) {
            fr = jQuery('#' + JRS.fid, window.parent.document);
            r = fr.contents().find('#reportContainer');
            fr.removeClass(layoutModule.HIDDEN_CLASS).show();

            if (!isIPad())
                fr.parent().parent().css('background-image', 'none');
            var h = r.height();
            var w = r.width();
            if (isSupportsTouch()) {
                new TouchController(r.get(0), fr.get(0).parentNode.parentNode, {
                    use2Fingers: true,
                    showBorders: true,
                    noInit3d: true
                });
                r.children('div').bind({
                    'touchstart': function () {
                        fr.parent().parent().siblings().removeClass(layoutModule.HOVERED_CLASS);
                        fr.parent().parent().addClass(layoutModule.HOVERED_CLASS);
                    }
                });
            }
            r.show();    // bloody hack to remember initial size of report frame
            // bloody hack to remember initial size of report frame
            window.reportFrameHeight = window.reportFrameHeight || fr.parent().height();
            window.reportFrameWidth = window.reportFrameWidth || fr.parent().width();
        } else {
            var ic = jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM)[0];
            if (isSupportsTouch()) {
                if (this.touchController) {
                    this.touchController.reset();
                } else {
                    var scrollWrapper = jQuery('#reportContainer').parent().parent();
                    var contentWidth = jQuery('#reportContainer > div').width();
                    scrollWrapper.width() < contentWidth && scrollWrapper.css('width', contentWidth + 'px');
                    this.touchController = new TouchController(scrollWrapper[0], scrollWrapper.parent()[0], {
                        noInit3d: true,
                        scrollbars: true
                    });
                }
                var bm = jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM + ' > button.minimize');
                if (ic && bm && bm.is(':visible')) {
                    var m = jQuery(ic).hasClass('minimized');
                    layoutModule.maximize(ic);
                    m && layoutModule.minimize(ic);
                }
            }
            jQuery('#reportContainer').show().height();
        }
        jQuery('#reportContainer').parents('.body').slice(0, 1).scrollTop(0);    /*
         Hide nothing to display html
         */
        /*
         Hide nothing to display html
         */
        if (Report.nothingToDisplay) {
            jQuery(Report.nothingToDisplay).addClass(layoutModule.HIDDEN_CLASS);
            jQuery("#" + Report.DATA_REFRESH_BUTTON).removeAttr(layoutModule.DISABLED_ATTR_NAME);
        }
        var reportStatus = window.viewer.getReportStatus();

        if (window.viewer.reportStatus.pages.total !== null) {
            var lastPageIndex = window.viewer.reportStatus.pages.total;
            jQuery("#emptyReportID").addClass(layoutModule.HIDDEN_CLASS);

            if (lastPageIndex === 1) {
                jQuery(".control.paging").addClass(layoutModule.HIDDEN_CLASS).prev(".divider").addClass(layoutModule.HIDDEN_CLASS);
            } else {
                jQuery(".control.paging").removeClass(layoutModule.HIDDEN_CLASS).prev(".divider").removeClass(layoutModule.HIDDEN_CLASS)[0];
            }
        }
        if ("empty" === reportStatus) {
            jQuery(jQuery("#emptyReportID p.message")[1]).html(i18n["jasper.report.view.empty"]);
            jQuery("#emptyReportID").removeClass(layoutModule.HIDDEN_CLASS);
            centerElement(jQuery("#emptyReportID"), {horz: true, vert: true});
        }
        if (_.contains(["empty", "failed"], reportStatus)) {
            jQuery(".control.paging").addClass(layoutModule.HIDDEN_CLASS).prev(".divider").addClass(layoutModule.HIDDEN_CLASS);
            // disable zoom
            jQuery("#zoom_out").prop("disabled", true);
            jQuery("#zoom_in").prop("disabled", true);
            jQuery("#zoom_value").prop("disabled", true);
            jQuery("#zoom_value_button").prop("disabled", true);
            // disable search
            jQuery("#search_report").prop( "disabled", true);
            jQuery("#search_report_button").prop( "disabled", true);
            jQuery("#search_options").prop( "disabled", true);
        } else {
            // enable zoom
            jQuery("#zoom_out").prop( "disabled", false);
            jQuery("#zoom_in").prop( "disabled", false);
            jQuery("#zoom_value").prop( "disabled", false);
            jQuery("#zoom_value_button").prop( "disabled", false);
            // enable search
            jQuery("#search_report").prop( "disabled", false);
            jQuery("#search_report_button").prop( "disabled", false);
            jQuery("#search_options").prop( "disabled", false);
        }
        Report.refreshPagination(silentUpdate);
        Report.refreshExporters(reportStatus);
        Report.refreshSave();
        Report.refreshAsyncCancel();
        if (window.viewer.reportStatus.pages.dataTimestampMessage && jQuery('#' + Report.DATA_TIMESTAMP_SPAN)[0]) {
            jQuery('#' + Report.DATA_TIMESTAMP_SPAN).html(window.viewer.reportStatus.pages.dataTimestampMessage);
        }
        if (!Report.allRequestParameters.viewAsDashboardFrame){
            jQuery('#innerPagination').css('margin', 0)[0];
        } else {
            jQuery('#innerPagination').css('margin', 'none')[0];
        }
        dialogs.popup.hideShared(jQuery('#' + ajax.LOADING_ID)[0], Report.REPORT_COMPONENT_ID);
        jQuery('#schedule') && jQuery('#schedule').removeAttr('disabled');
        jQuery('#embed') && jQuery('#embed').removeAttr('disabled');
    },
    showInvisibleICValidationMessages: function() {
        var iicValidationState = jQuery('#iicValidationErrorMessagesHolder');

        if (iicValidationState && iicValidationState.length > 0) {
            var iicValidationMessages = iicValidationState.html();
            jQuery(".control.paging").addClass(layoutModule.HIDDEN_CLASS).prev(".divider").addClass(layoutModule.HIDDEN_CLASS);

            // disable zoom
            jQuery("#zoom_out").prop("disabled", true);
            jQuery("#zoom_in").prop("disabled", true);
            jQuery("#zoom_value").prop("disabled", true);
            jQuery("#zoom_value_button").prop("disabled", true);
            // disable search
            jQuery("#search_report").prop( "disabled", true);
            jQuery("#search_report_button").prop( "disabled", true);
            jQuery("#search_options").prop( "disabled", true);

            dialogs.errorPopup.show(iicValidationMessages);
        }

        dialogs.popup.hideShared(jQuery('#' + ajax.LOADING_ID)[0], Report.REPORT_COMPONENT_ID);
    }
});    /*
    UI updates after report is updated or canceled
*/
/*
    UI updates after report is updated or canceled
*/
jQuery.extend(Report, {
    refreshExporters: function (reportStatus) {
        if (window.viewer.reportStatus.pages.total != null &&
            !window.viewer.isExportRunning() &&
            !_.contains(["empty", "failed"], reportStatus)) {
            buttonManager.enable(jQuery("#export")[0]);
        } else {
            buttonManager.disable(jQuery("#export")[0]);
        }
    },
    refreshSave: function () {
    }    /* overridden in Pro */,
    refreshPagination: function (silentUpdate) {
        var pageStatus = window.viewer.reportStatus.pages,
            currentPageIndex = pageStatus.current,
            lastPage = pageStatus.total;
        if (lastPage != null && lastPage <= 0) {
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_FIRST)[0]);
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_PREV)[0]);
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0]);
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_NEXT)[0]);
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_LAST)[0]);
            if (jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0]) {
                jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0].setValue('');
                jQuery('#' + Report.PAGINATION_PAGE_TOTAL).html('');
            }
            return;
        }
        if (currentPageIndex > 1) {
            buttonManager.enable(jQuery('#' + Report.PAGINATION_PAGE_FIRST)[0]);
            buttonManager.enable(jQuery('#' + Report.PAGINATION_PAGE_PREV)[0]);
        } else {
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_FIRST)[0]);
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_PREV)[0]);
        }
        if (lastPage == null) {
            if (pageStatus.lastPartialPage == null || currentPageIndex < pageStatus.lastPartialPage) {
                buttonManager.enable(jQuery('#' + Report.PAGINATION_PAGE_NEXT)[0]);
            } else {
                buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_NEXT)[0]);
            }
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_LAST)[0]);
        } else if (currentPageIndex < lastPage) {
            buttonManager.enable(jQuery('#' + Report.PAGINATION_PAGE_NEXT)[0]);
            buttonManager.enable(jQuery('#' + Report.PAGINATION_PAGE_LAST)[0]);
        } else {
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_NEXT)[0]);
            buttonManager.disable(jQuery('#' + Report.PAGINATION_PAGE_LAST)[0]);
        }
        buttonManager.enable(jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0]);
        if (jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0]) {
            if (!silentUpdate) {
                jQuery('#' + Report.PAGINATION_PAGE_CURRENT)[0].setValue(currentPageIndex);
            }
            if (lastPage == null) {
                jQuery('#' + Report.PAGINATION_PAGE_TOTAL).html('');
            } else {
                jQuery('#' + Report.PAGINATION_PAGE_TOTAL).html(this.getMessage('jasper.report.view.page.of') + lastPage);
            }
        }
        this.pageActions = {
            'button#page_first': 1,
            'button#page_prev': currentPageIndex - 1,
            'button#page_next': currentPageIndex + 1
        };
        if (lastPage != null) {
            this.pageActions['button#page_last'] = lastPage;
        }

        var $elem = jQuery(".toolsRight .paging.subfocus").find("button");
        if ($elem.length && $elem.is(":disabled")) {
            if ($elem.is("#" + Report.PAGINATION_PAGE_LAST)) {
                stdnav.forceFocus(".toolsRight li.page_first");
            } else if ($elem.is("#" + Report.PAGINATION_PAGE_FIRST)) {
                stdnav.forceFocus(".toolsRight li.page_last");
            } else if ($elem.is("#" + Report.PAGINATION_PAGE_NEXT)) {
                stdnav.forceFocus(".toolsRight li.page_prev");
            } else if ($elem.is("#" + Report.PAGINATION_PAGE_PREV)) {
                stdnav.forceFocus(".toolsRight li.page_next");
            } else {
                $elem.closest("ul").focus();
            }
        } else {
            $elem.addClass(layoutModule.HOVERED_CLASS);
        }

    },
    refreshAsyncCancel: function (canceled) {
        if (jQuery('#' + Report.ASYNC_CANCEL_BUTTON)[0]) {
            if (canceled) {
                jQuery('#' + 'asyncIndicator').addClass(layoutModule.HIDDEN_CLASS)[0];
                buttonManager.disable(jQuery('#' + Report.ASYNC_CANCEL_BUTTON)[0]);
                jQuery('#' + Report.DATA_REFRESH_BUTTON).removeClass(layoutModule.HIDDEN_CLASS)[0];
            } else {
                if (window.viewer.isReportRunning()) {
                    buttonManager.enable(jQuery('#' + Report.ASYNC_CANCEL_BUTTON)[0]);
                    jQuery('#' + 'asyncIndicator').removeClass(layoutModule.HIDDEN_CLASS)[0];
                    jQuery('#' + Report.DATA_REFRESH_BUTTON).addClass(layoutModule.HIDDEN_CLASS)[0];
                } else {
                    jQuery('#' + 'asyncIndicator').addClass(layoutModule.HIDDEN_CLASS)[0];
                    buttonManager.disable(jQuery('#' + Report.ASYNC_CANCEL_BUTTON)[0]);
                    jQuery('#' + Report.DATA_REFRESH_BUTTON).removeClass(layoutModule.HIDDEN_CLASS)[0];
                }
            }
        }
    }
});

function getDisabledState(elemsIDsArr) {
    const result = [];
    for (const elemID of elemsIDsArr) {
        result.push({
            id: elemID,
            disabledState: jQuery(`#${elemID}`).prop("disabled")
        });
    }
    return result;
}

function disableElements(elemsIDsArr) {
    for (const elemID of elemsIDsArr) {
        jQuery(`#${elemID}`).prop("disabled", true);
    }
}

function restoreState(prevStateArr) {
    for (const prevState of prevStateArr) {
        jQuery(`#${prevState.id}`).prop("disabled", prevState.disabledState)
    }
}

/*
    Methods bound to toolbar buttons
*/
jQuery.extend(Report, {
    exportReport: function (type, formAction) {
        let exportLoadingIndicator = jQuery("#exportLoadingIndicator");
        exportLoadingIndicator.removeClass(layoutModule.HIDDEN_CLASS);

        // preserve export interfering elements' state
        const elemenIDs = ["fileOptions", "export"];
        const elementsState = getDisabledState(elemenIDs);

        // disable export interfering buttons disregarding their disabled state
        disableElements(elemenIDs);

        window.viewer.exportReport(type)
            .done(function(link) {
                window.open(link.href);
            })
            .always(function() {
                exportLoadingIndicator.addClass(layoutModule.HIDDEN_CLASS);

                // restore interfering elements' state
                restoreState(elementsState);
            });
    },
    goBack: function (noConfirm) {
        var confirm = noConfirm === true ? false : true;
        if (confirm && !Report.confirmExit()) {
            return;
        }
        var params = Report.getAllRequestParameters();    // disable back button after first click
        // disable back button after first click
        buttonManager.disable(jQuery('#back')[0]);
        if (params['_ddHyperlink']) {
            window.history.back();
        } else {
            // exportForm is used here to leave the page
            Report.exportForm._eventId.value = 'close';
            Report.exportForm._flowExecutionKey.value = Report.reportExecutionKey();
            Report.exportForm.submit();
        }
    },
    open: function () {
        alert('Not implemented yet: open report');
    },
    save: function () {
        alert('Report saving is not available in community edition');
    },
    saveAs: function () {
        alert('Report saving is not available in community edition');
    },
    undo: function () {
        window.viewer.undo();
    },
    redo: function () {
        window.viewer.redo();
    },
    undoAll: function () {
        window.viewer.undoAll();
    },
    cancelReportAsyncExecution: function (bAsync) {
        buttonManager.disable(jQuery('#' + Report.ASYNC_CANCEL_BUTTON)[0]);
        window.viewer.cancelAsync().then(function () {
            var status = window.viewer.getReportStatus();

            if (status === "failed" || status === "ready" || window.viewer.reportStatus.pages.total !== null) {
                Report.refreshAsyncCancel(true);
                return;
            } else if (status === "cancelled") {
                Report.refreshAsyncCancel(true);
                dialogs.systemConfirm.show(Report.getMessage('jasper.report.view.report.canceled'), 5000);
                return;
            } else {
                Report.refreshPagination(true);
                Report.refreshExporters(status);
                Report.refreshSave();
                Report.refreshAsyncCancel();
            }
        });
    },

    schedule: function(){
        let previousState = window.viewer.canSave && Report.savedState && Report.savedState != window.viewer.getReportStackState(),
            save = Report.save.bind(Report),
            paramsMap = {
                'reportUnitURI': self.viewer.config.reporturi,
                'resourceType': "ReportUnit",
                'parentReportUnitURI': self.viewer.config.reporturi,
                'decorate': 'no',
                'schedulerAccelerator': 'schedule'
            };
        scheduleDialog.scheduleDialogBox(previousState,paramsMap,save);
    }
});

export default Report;