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
 * @version: $Id: report.view.base.js 47331 2014-07-18 09:13:06Z kklein $
 */

;(function (exports) {

    exports["Report"] = {
        _messages: {},
        _scroll: null,
        pageActions: {},
        viewReportForm: null,
        pageIndex: 0,
        lastPageIndex: 0,
        pageTimestamp: null,
        checkPageUpdatedTimeoutId: null,
        emptyReport: true,
        reportForceControls: false,
        hasInputControls: false,
        reportControlsLayout: null,
        refreshReportCanceled: false,
        reportParameterValues: null,
        allRequestParameters: null,
        requestedInputParameters: {},
        parametersWithoutDefaultValues: null,
        isLoaded: false,

        // ViewReport id's
        EXPORT_ACTION_FORM: "exportActionForm",
        TOOLBAR_SUBMENU: "toolbarText",
        PAGINATION_CONTAINER: "pagination",
        PAGINATION_PAGE_FIRST: "page_first",
        PAGINATION_PAGE_PREV: "page_prev",
        PAGINATION_PAGE_CURRENT: "page_current",
        PAGINATION_PAGE_NEXT: "page_next",
        PAGINATION_PAGE_LAST: "page_last",
        PAGINATION_PAGE_TOTAL: "page_total",
        DATA_TIMESTAMP_SPAN: "dataTimestampMessage",
        DATA_REFRESH_BUTTON: "dataRefreshButton",

        getMessage: function (messageId, object) {
            var message = this._messages[messageId];
            return message ? new Template(message).evaluate(object ? object : {}) : "";
        },

        /**
         *  The common init function. Invoke it from page-specific initialize()
         */
        commonInit: function (options) {
            var ajaxLoading = jQuery('#' + ajax.LOADING_ID);
            if (ajaxLoading.length > 0) {
                ajaxLoading.addClass(layoutModule.CANCELLABLE_CLASS);
                ajaxLoading.find("#cancel").click(function () {
                    Report.cancelReportExecution();
                });
            }
            /*
                Used for embedding report viewer with no decoration in CE
             */
            if (document.location.href.indexOf("frame=0") > 0) {
                jQuery('#reportViewFrame > .content > .header').hide();
                jQuery('#reportViewFrame').css('margin', 0);
                jQuery('#innerPagination').css('margin', 'none');
                jQuery('#reportViewFrame > .content > .body').css({'top': 0, 'margin-top': 0});
            }
        },

        navigateToReportPage: function (page, silentUpdate, isAutomaticRefresh) {
            if(viewer.jive) viewer.jive.hide();
            viewer.reportInstance.gotoPage(page);
        },

        goToPage: function (page) {
            if (parseInt(page, 10) && parseInt(page, 10) > 0
                && (Report.lastPageIndex == null || parseInt(page, 10) <= Report.lastPageIndex + 1)) {
                Report.navigateToReportPage(parseInt(page, 10) - 1);
            } else {
                doNothing();
            }
        },

        /**
         * Returns the updated flowExecutionKey after every report refresh
         */
        reportExecutionKey: function () {
            if (Report.flowExecutionKeyOutput) {
                return Report.flowExecutionKeyOutput;
            } else if (window.dashboardViewFrame) {
                return dashboardViewFrame.flowExecutionKey;
            } else {
                return Report.flowExecutionKey;
            }
        },

        //////////////////////////////
        // Communication with server
        //////////////////////////////
        /**
         * The main API function to view or update the report on the page
         * @param {Object} urlParams - an object literal optionally defining:
         * @option {String} _flowExecutionKey (default value will be resolved by Report.reportExecutionKey())
         * @option {String} _eventId (default is "refreshReport")
         * @param {Object} options - an object literal optionally defining:
         * @option {String} fillLocation - id indicating where in the DOM to dump the updated report (default is "reportContainer")
         * @option {Array} callback - JS functions to evaluate after report update (default is Report.reportRefreshed())
         * @option {String} fromLocation - id indicating which part of the ajax response to use (default is "reportOutput" implemented in DefaultJasperViewer)
         */
        refreshReport: function (urlParams, options, reportParameters) {
            var result = /(\?|&)output=([^&]*)/.exec(location.href);
            var output = result && result.length === 3 ? result[2] : '';

            dialogs.popup.show($(ajax.LOADING_ID), true);

            urlParams = urlParams || {};

            urlParams._flowExecutionKey = urlParams._flowExecutionKey ? urlParams._flowExecutionKey : Report.reportExecutionKey();
            urlParams._eventId = urlParams._eventId ? urlParams._eventId : "refreshReport";
            urlParams.decorate = "no";
            urlParams.confirm = "true";
            urlParams.decorator = "empty";
            if (!urlParams.ajax) {
                urlParams.ajax = "true";
            }

            var url = 'flow.html?' + Object.toQueryString(urlParams);

            //(reportParameters) ? reportParameters : reportParameters = this.getParametersFromRequest();
            Report.requestedInputParameters = reportParameters ?  reportParameters : this.getParametersFromRequest();

            if(urlParams.freshData) {
                Report.requestedInputParameters += '&freshData=true';
            }

            if (output === '' || output === 'html') {
                /*
                    TODO: remove after Emerald2 release
                 */
                /*
                options = options || {};
                ajaxTargettedUpdate(url, {
                    postData: reportParameters,
                    fillLocation: options.fillLocation ? options.fillLocation : "reportContainer",
                    fromLocation: options.fromLocation ? options.fromLocation : "reportOutput",
                    callback: options.callback ? options.callback : "Report.reportRefreshed();",
                    errorHandler: options.errorHandler ? options.errorHandler : Report.refreshReportErrorHandler,
                    isAutomaticRefresh: options.isAutomaticRefresh
                });
                */
                if(Report.isLoaded) {
                    viewer.reportInstance.refreshPage(0);
                } else {
                    Report.isLoaded = true;
                    viewer.loadReport();
                }
            } else {
                if ('pdf' === output || 'swf' === output) {
                    Report.exportReport(output, url);
                } else {
                    new jaspersoft.components.AjaxDownloader().start(url + "&" + Report.requestedInputParameters);
                }
            }
        },

        /**
         *  Cancels the report execution on the server
         */
        cancelReportExecution: function () {
            document.body.style.cursor = "default";
            dialogs.popup.hide($(ajax.LOADING_ID));
            viewer.reportInstance.cancelExecution().then(function() {
                // Cancel request returns nothing.
            }, function(){
                Report.goBack();
            });
            /*
                TODO: remove after Emerald 2 release.
             */
            //var url = "viewReportCancel.html?_flowExecutionKey=" + Report.reportExecutionKey();
            //ajaxNonReturningUpdate(url, {});
        },

        /**
         * Get report parameters from request in case if JRS.Controls is not available.
         */
        getParametersFromRequest: function () {
            return ControlsBase.buildParams(Report.getAllRequestParameters());
        },

        /**
         * All request parameters get formed into JSONObject on server side,
         * where key is a string and value is always array of strings even if there is single value.
         */
        getAllRequestParameters: function () {
            return _.isObject(Report.allRequestParameters) ? Report.allRequestParameters : {};
        },

        /*
            Used as test in actionModel
         */
        canSaveReport: function () {
            return !Report.isReportReadOnly;
        },

        confirmExit: function () {
            // empty in CE because there's no save; overridden in Pro
            return true;
        },

        confirmAndLeave: function () {
            return Report.confirmExit() ? Report.closeViewerOnExit : false;
        },

        closeViewerOnExit: function (exitCallback) {
            viewer.exit().then(function() {
                exitCallback();
            });
        },
        /*
            Convenience method to show dialog, called from jasperreports-loader
         */
        showAjaxDialog: function() {
            dialogs.popup.show($(ajax.LOADING_ID), true);
        },
        hideAjaxDialog: function() {
            dialogs.popup.hide($(ajax.LOADING_ID));
        }
    };
})(
   window
);





/*
refreshReportErrorHandler: function (ajaxAgent) {
    if (Report.refreshReportCanceled) {
        Report.goBack();
        return true;
    } else {
        return baseErrorHandler(ajaxAgent);
    }
},

confirmAndLeave: function () {
    console.info('k');
    var confirmed = Report.confirmExit();
    if (!confirmed) {
        return false;
    }
    // return a function to be called with an exit callback
    return Report.closeViewerOnExit;
},

closeViewerOnExit: function (exitCallback) {
    var params = {
        _flowExecutionKey: Report.reportExecutionKey(),
        _eventId: "end"
    };
    var url = 'flow.html?' + Object.toQueryString(params);

    var exitCallbackOnce = function () {
        if (!Report.exitCallbackCalled) {
            Report.exitCallbackCalled = true;
            exitCallback();
        }
    }

    // set a timeout as well because we don't want to delay the exit if the ajax call takes too much
    window.setTimeout(exitCallbackOnce, 3000);

    // send a request to cleanup the current execution.
    ajaxUpdate(url, {
        errorHandler: function () {
            return false;//ignore all errors
        },
        responseHandler: exitCallbackOnce
    });
},
 */



