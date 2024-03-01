define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var configs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var Report = require('./report.view.runtime');

var _coreCoreAjaxUtils = require("../core/core.ajax.utils");

var showErrorPopup = _coreCoreAjaxUtils.showErrorPopup;

var _namespaceNamespace = require("../namespace/namespace");

var JRS = _namespaceNamespace.JRS;

var _utilUtilsCommon = require("../util/utils.common");

var isIPad = _utilUtilsCommon.isIPad;

var buttonManager = require('../core/core.buttonManager');

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

/*global alert, ajax*/
var ctxpath = configs.contextPath;
var failedToChangeChartType;
var ERROR_POPUP_DIALOG_WIDTH = "546px",
    ERROR_POPUP_DIALOG_HEIGHT = "350px";
var UrlManager = {
  reportcontexturl: null,
  reportoutputurl: ctxpath + "/flow.html",
  reportactionurl: ctxpath + "/runReportAction.html",
  reportcomponentsurl: ctxpath + "/getReportComponents.html",
  reportpagestatusurl: ctxpath + "/viewReportPageUpdateCheck.html",
  cancelExecutionUrl: ctxpath + '/viewReportCancel.html',
  cancelAsyncExecutionUrl: ctxpath + '/viewReportAsyncCancel.html'
};

var Loader = function Loader(o) {
  this.config = {
    reporturi: null,
    async: true
  };
  $.extend(this.config, o);
  this.jasperPrintName = null;
  this.contextid = null;
};

Loader.prototype = {
  getHtmlForPage: function getHtmlForPage(page, boolGotoPage, inputParameters) {
    // do-not re-render chart if we failed to change it's type and called Undo action. Fixes bug #35973.
    if (failedToChangeChartType) {
      failedToChangeChartType = false;

      if (Report && Report.hideAjaxDialog) {
        Report.hideAjaxDialog();
      }

      var dfd = new $.Deferred();
      dfd.reject();
      return dfd;
    }
    /*
     Report refers to the global Report object declared in report.view.base.js, report.view.runtime.js,
     and report.view.js
     */


    var params = {
      _flowExecutionKey: Report.reportExecutionKey(this.config.reporturi),
      _flowId: "viewReportFlow",
      _eventId: boolGotoPage ? "navigate" : "refreshReport",
      pageIndex: page,
      decorate: "no",
      confirm: "true",
      decorator: "empty",
      ajax: "true"
    };

    if (Report && Report.requestedInputParameters) {
      inputParameters = Report.requestedInputParameters;
    }

    return request({
      url: UrlManager.reportoutputurl,
      urlParams: $.param(params),
      params: inputParameters
    }, 'html', this.config.reporturi);
  },
  cancelExecution: function cancelExecution(async) {
    return request({
      url: async ? UrlManager.cancelAsyncExecutionUrl + '?jasperPrintName=' + this.jasperPrintName : UrlManager.cancelExecutionUrl + '?_flowExecutionKey=' + Report.reportExecutionKey(this.config.reporturi)
    }, 'json');
  },

  /*
   Called by jasperreports-status-checker.js included and served by JR jar.
   */
  getStatusForPage: function getStatusForPage(page, timestamp) {
    return request({
      url: UrlManager.reportpagestatusurl,
      params: {
        jasperPrintName: this.jasperPrintName,
        pageIndex: page,
        pageTimestamp: timestamp
      }
    }, 'json');
  },
  getComponentsForPage: function getComponentsForPage(page) {
    return request({
      url: UrlManager.reportcomponentsurl,
      params: {
        jasperPrintName: this.jasperPrintName || Report.jasperPrintName,
        pageIndex: page
      }
    }, 'json');
  },
  runAction: function runAction(o) {
    var it = this,
        showAjaxDialog = o.showAjaxDialog != null ? o.showAjaxDialog : true;
    if (showAjaxDialog && Report && Report.showAjaxDialog) Report.showAjaxDialog();

    try {
      return request({
        url: UrlManager.reportactionurl,
        params: {
          jr_ctxid: it.contextid,
          jr_action: JSON.stringify(o.action)
        }
      }, 'json', it.config.reporturi);
    } catch (ex) {
      alert(ex);
    }
  },
  save: function save(options) {
    var action = options.folder && options.name ? 'saveReportAs' : 'saveReport';
    var parms = {
      _flowExecutionKey: Report.reportExecutionKey(this.config.reporturi),
      _eventId: action
    };

    if (action == 'saveReportAs') {
      parms.folder = options.folder;
      parms.name = options.name;
      parms.description = options.description || '';
      parms.overwrite = !!options.overwrite;
    }

    try {
      return request({
        url: UrlManager.reportoutputurl,
        params: parms
      }, 'json', this.config.reporturi);
    } catch (ex) {
      alert(ex);
    }
  },
  exit: function exit() {
    var params = {
      _flowExecutionKey: Report.reportExecutionKey(),
      _eventId: "end"
    };
    return request({
      url: UrlManager.reportoutputurl,
      urlParams: $.param(params)
    }, 'html');
  },
  setPageUpdateStatus: function setPageUpdateStatus(data) {
    this.lastPageUpdateStatus = data.result;
    this.config.eventManager.triggerEvent('pageUpdateAvailable');
  }
};

function request(o, dataType, reportUri) {
  var dfd = new $.Deferred();
  $.ajax(o.url + (o.urlParams ? '?' + o.urlParams : ''), {
    type: 'POST',
    dataType: dataType,
    data: o.params,
    beforeSend: function beforeSend(jqXHR) {
      if (typeof ajax !== 'undefined' && ajax.csrfRequestHeaders) {
        ajax.csrfRequestHeaders.each(function (pair) {
          jqXHR.setRequestHeader(pair.key, pair.value);
        });
      }
    },
    success: function success(data, textStatus, jqXHR) {
      if (checkResponseForErrors(jqXHR, reportUri)) {
        if (Report && Report.hideAjaxDialog) Report.hideAjaxDialog();
        dfd.reject(jqXHR, textStatus);
      } else {
        dfd.resolve(data, textStatus, jqXHR);
      }
    },
    error: function error(jqXHR, textStatus, errorThrown) {
      var jrsErrorFound = checkResponseForErrors(jqXHR, reportUri);

      if (!jrsErrorFound) {
        if (jqXHR.status >= 400) {
          var errorMessage = '<p>HTML response error code: ' + jqXHR.status + ', ' + textStatus + '</p>';

          if (jqXHR.responseText.indexOf('id="errorPageContent"') >= 0) {
            errorMessage += $(jqXHR.responseText).find('#errorPageContent .body').html();
          } else {
            if (jqXHR.responseText.indexOf('{"result":{"msg":"') >= 0) {
              var jsonErrorMessage = JSON.parse(jqXHR.responseText);

              if (jsonErrorMessage.result.devmsg.indexOf('<#_#>')) {
                errorMessage = '';
                $.each(jsonErrorMessage.result.devmsg.split('<#_#>'), function () {
                  errorMessage += '<p>' + this + '</p>';
                });
              } else {
                errorMessage = '<p>' + jsonErrorMessage.result.devmsg + '</p>';
              }
            } else {
              errorMessage += '<p>HTML response was: ' + jqXHR.responseText + '</p>';
            }
          }

          var $body = $('body'),
              insideDashboardFrame = $body.hasClass("dashboardViewFrame"),
              popupOptions = {
            reportUri: reportUri,
            type: 'responseError'
          };

          if (insideDashboardFrame && window.reportFrameWidth && window.reportFrameHeight) {
            popupOptions.width = window.reportFrameWidth < parseInt(ERROR_POPUP_DIALOG_WIDTH, 10) ? window.reportFrameWidth - 20 + "px" : ERROR_POPUP_DIALOG_WIDTH;
            popupOptions.height = window.reportFrameHeight < parseInt(ERROR_POPUP_DIALOG_HEIGHT, 10) ? window.reportFrameHeight - 20 + "px" : ERROR_POPUP_DIALOG_HEIGHT;
            $body.css({
              width: window.reportFrameWidth + "px",
              height: window.reportFrameHeight + "px"
            });
          }

          showErrorPopup(errorMessage, popupOptions);

          if (insideDashboardFrame && !isIPad()) {
            $body.css({
              width: "auto",
              height: "auto"
            });
          }
        }
      }

      if (Report && Report.hideAjaxDialog) Report.hideAjaxDialog();
      dfd.reject(jqXHR, textStatus);
    }
  });
  return dfd.promise();
}

function checkResponseForErrors(jqXHR, reportUri) {
  /*
   If session timed out redirect to login page.
   */
  if (jqXHR.getResponseHeader('LoginRequested')) {
    showErrorPopup('Login requested.', {
      reportUri: reportUri,
      type: 'sessionError'
    });
    if (window.viewer && window.viewer.setLocation) window.viewer.setLocation('.');
    return true;
  }

  if (jqXHR.getResponseHeader("reportError")) {
    showErrorPopup(jqXHR.responseText, {
      reportUri: reportUri,
      type: 'reportError'
    });
    return true;
  }

  if (jqXHR.getResponseHeader('JasperServerError')) {
    if (jqXHR.getResponseHeader('reportPageNonExisting')) {
      showErrorPopup('Page does not exist', {
        reportUri: reportUri,
        type: 'reportPageNonExistingError'
      });
    }

    if (!jqXHR.getResponseHeader('SuppressError')) {
      if ($('.dashboardViewFrame').length == 1) {
        $(document.body).html(jqXHR.responseText);
        $('#' + JRS.fid, window.parent.document).removeClass('hidden').show();
      } else {
        showErrorPopup(jqXHR.responseText, {
          reportUri: reportUri,
          type: 'JasperserverError'
        }); // Fix for bug #35803.
        // We shouldn't allow user to save report with unsupported chart type. That's why we should undo last
        // operation if this operation was chart type change and it failed and even remove it from history.
        // Unfortunately we have no easy marker to tell about such error. That is why we need to scan
        // response text for error text that denotes unsupported chart type.

        if (responseTextHasChartTypeChangeErrors(jqXHR.responseText)) {
          setTimeout(function () {
            var $alertBox = $("#standardAlert"),
                $dimmer = $(".dimmer"),
                $btn = $alertBox.find("button");
            $dimmer.removeClass("hidden").css("z-index", parseInt($alertBox.css("z-index"), 10) - 1);
            $btn.on("click", function () {
              // disable redo button on UI
              buttonManager.disable($('#redo')[0]); // remove last "failed" state from history

              window.viewer.stateStack.states.pop();
              failedToChangeChartType = true; // move to previous not-failed state

              window.viewer.reportInstance.undo(); // select correct chart type in chart type selection window

              if (window.viewer.reportInstance.components && window.viewer.reportInstance.components.chart && window.viewer.reportInstance.components.chart[0]) {
                $("[data-hcname]").removeClass("selected");
                $("[data-hcname='" + window.viewer.reportInstance.components.chart[0].config.charttype + "']").addClass("selected");
              }

              $dimmer.addClass("hidden");
              $btn.off("click");
            });
          }, 0);
        }
      }
    }

    return true;
  } else {
    return false;
  }
}

function responseTextHasChartTypeChangeErrors(responseText) {
  var hasError = false;

  for (var i = 0; i < Report.chartTypeChangeErrorMessages.length; i++) {
    if (responseText.indexOf(Report.chartTypeChangeErrorMessages[i]) > -1) {
      hasError = true;
      break;
    }
  }

  return hasError;
}

Loader.UrlManager = UrlManager;
module.exports = Loader;

});