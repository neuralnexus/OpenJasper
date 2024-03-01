define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _prototype = require('prototype');

var $ = _prototype.$;

var Report = require('./report.view.base');

var Controls = require('../controls/controls.report');

var _controlsControlsBase = require("../controls/controls.base");

var ControlsBase = _controlsControlsBase.ControlsBase;

var toolbarButtonModule = require('../components/components.toolbarButtons.events');

var _utilUtilsCommon = require("../util/utils.common");

var matchAny = _utilUtilsCommon.matchAny;
var isIPad = _utilUtilsCommon.isIPad;
var isSupportsTouch = _utilUtilsCommon.isSupportsTouch;
var centerElement = _utilUtilsCommon.centerElement;
var addDataToForm = _utilUtilsCommon.addDataToForm;

var _namespaceNamespace = require("../namespace/namespace");

var JRS = _namespaceNamespace.JRS;

var TouchController = require('../util/touch.controller');

var layoutModule = require('../core/core.layout');

var dialogs = require('../components/components.dialogs');

var _coreCoreAjax = require("../core/core.ajax");

var ajax = _coreCoreAjax.ajax;

var buttonManager = require('../core/core.events.bis');

var _ = require('underscore');

var jQuery = require('jquery');

var stdnav = require("runtime_dependencies/js-sdk/src/common/stdnav/stdnav");

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

/**
 * @version: $Id$
 */

/* global alert*/
Report.toolbarActionMap = {
  'back': 'Report.goBack',
  'ICDialog': 'Controls.show',
  'export': 'doNothing',
  'undo': 'Report.undo',
  'redo': 'Report.redo',
  'undoAll': 'Report.undoAll'
};
/*
Init & reportRefreshed methods.
*/

/*
    Init & reportRefreshed methods.
 */

jQuery.extend(Report, {
  initialize: function initialize() {
    this.commonInit();
    Report.exportForm = $(Report.EXPORT_ACTION_FORM);
    this.nothingToDisplay = $('nothingToDisplay');

    if ($(Report.DATA_REFRESH_BUTTON)) {
      $(Report.DATA_REFRESH_BUTTON).observe('mouseup', function () {
        window.viewer.jive && window.viewer.jive.hide();

        if (typeof Controls !== 'undefined') {
          var selectedData = Controls.viewModel.get('selection');
          var controlsUri = ControlsBase.buildSelectedDataUri(selectedData);
          this.refreshReport({
            freshData: true
          }, null, controlsUri ? '&' + controlsUri : '');
        } else {
          this.refreshReport({
            freshData: true
          }, null, '');
        }
      }.bindAsEventListener(this));
    }

    if ($(Report.ASYNC_CANCEL_BUTTON)) {
      $(Report.ASYNC_CANCEL_BUTTON).observe('mouseup', function () {
        this.cancelReportAsyncExecution(true);
      }.bindAsEventListener(this));
    }
    /**
    *  Init toolbar
    */

    /**
     *  Init toolbar
     */


    toolbarButtonModule.ACTION_MODEL_TAG = Report.TOOLBAR_SUBMENU;
    toolbarButtonModule.initialize(Report.toolbarActionMap);

    if ($(Report.PAGINATION_PAGE_CURRENT)) {
      // observe page navigation input
      $(Report.PAGINATION_PAGE_CURRENT).observe('change', function (e) {
        this.goToPage($(Report.PAGINATION_PAGE_CURRENT).getValue());
        e.stop();
      }.bindAsEventListener(this)); // observe page navigation buttons
      // observe page navigation buttons

      jQuery('.toolsRight')[0].observe('click', function (e) {
        var elem = e.element();
        e.stop();

        for (var pattern in this.pageActions) {
          if (matchAny(elem, [pattern], true)) {
            this.navigateToReportPage(this.pageActions[pattern]);
            return;
          }
        }
      }.bindAsEventListener(this));
    }

    if (document.location.href.indexOf('noReturn=true') > 0) {
      buttonManager.disable($('back'));
    }

    if (typeof Controls !== 'undefined') {
      Controls.initialize.bind(Controls)(Report.refreshReport);
    } else {
      Report.refreshReport();
    }
  },
  reportRefreshed: function reportRefreshed(silentUpdate) {
    //        Report.snapshotSaveStatus = null;
    var fr, r;
    /*
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
      if (!isIPad()) fr.parent().parent().css('background-image', 'none');
      var h = r.height();
      var w = r.width();

      if (isSupportsTouch()) {
        new TouchController(r.get(0), fr.get(0).parentNode.parentNode, {
          use2Fingers: true,
          showBorders: true,
          noInit3d: true
        });
        r.children('div').bind({
          'touchstart': function touchstart() {
            fr.parent().parent().siblings().removeClass(layoutModule.HOVERED_CLASS);
            fr.parent().parent().addClass(layoutModule.HOVERED_CLASS);
          }
        });
      }

      r.show(); // bloody hack to remember initial size of report frame
      // bloody hack to remember initial size of report frame

      window.reportFrameHeight = window.reportFrameHeight || fr.parent().height();
      window.reportFrameWidth = window.reportFrameWidth || fr.parent().width();
    } else {
      var ic = $(ControlsBase.INPUT_CONTROLS_FORM);

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
          var m = ic.hasClassName('minimized');
          layoutModule.maximize(ic);
          m && layoutModule.minimize(ic);
        }
      }

      jQuery('#reportContainer').show().height();
    }

    jQuery('#reportContainer').parents('.body').slice(0, 1).scrollTop(0);
    /*
    Hide nothing to display html
    */

    /*
     Hide nothing to display html
     */

    if (Report.nothingToDisplay) {
      Report.nothingToDisplay.addClassName(layoutModule.HIDDEN_CLASS);
      jQuery("#" + Report.DATA_REFRESH_BUTTON).removeAttr(layoutModule.DISABLED_ATTR_NAME);
    }

    var paginationState = jQuery('#paginationIndexHolder');
    var emptyReportState = jQuery('#emptyReportMessageHolder');

    if (paginationState.length > 0) {
      var lastPageIndex = paginationState.attr("data-lastPageIndex");
      jQuery("#emptyReportID").addClass(layoutModule.HIDDEN_CLASS);

      if (lastPageIndex === "0") {
        jQuery(".control.paging").addClass(layoutModule.HIDDEN_CLASS).prev(".divider").addClass(layoutModule.HIDDEN_CLASS);
      } else {
        jQuery(".control.paging").removeClass(layoutModule.HIDDEN_CLASS).prev(".divider").removeClass(layoutModule.HIDDEN_CLASS);
      }
    }

    if (emptyReportState && emptyReportState.length > 0) {
      var emptyReportMessage = emptyReportState.html();
      jQuery(".control.paging").addClass(layoutModule.HIDDEN_CLASS).prev(".divider").addClass(layoutModule.HIDDEN_CLASS);
      jQuery(jQuery("#emptyReportID p.message")[1]).html(emptyReportMessage);
      jQuery("#emptyReportID").removeClass(layoutModule.HIDDEN_CLASS);
      centerElement(jQuery("#emptyReportID"), {
        horz: true,
        vert: true
      });
    }

    Report.refreshPagination(silentUpdate);
    Report.refreshExporters();
    Report.refreshSave();
    Report.refreshAsyncCancel();

    if (this.dataTimestampMessage && $(Report.DATA_TIMESTAMP_SPAN)) {
      $(Report.DATA_TIMESTAMP_SPAN).update(this.dataTimestampMessage);
    }

    if (!Report.allRequestParameters.viewAsDashboardFrame) {
      jQuery('#innerPagination').css('margin', 0);
    } else {
      jQuery('#innerPagination').css('margin', 'none');
    }

    dialogs.popup.hideShared($(ajax.LOADING_ID), Report.REPORT_COMPONENT_ID);
  }
});
/*
UI updates after report is updated or canceled
*/

/*
    UI updates after report is updated or canceled
*/

jQuery.extend(Report, {
  refreshExporters: function refreshExporters() {
    buttonManager[this.lastPageIndex != null ? 'enable' : 'disable']($('export'));
  },
  refreshSave: function refreshSave() {}
  /* overridden in Pro */
  ,
  refreshPagination: function refreshPagination(silentUpdate) {
    var currentPageIndex = window.viewer.reportInstance.currentpage;
    var lastPage = window.viewer.reportInstance.status.totalPages;

    if (this.emptyReport || lastPage != null && lastPage <= 0) {
      buttonManager.disable($(Report.PAGINATION_PAGE_FIRST));
      buttonManager.disable($(Report.PAGINATION_PAGE_PREV));
      buttonManager.disable($(Report.PAGINATION_PAGE_CURRENT));
      buttonManager.disable($(Report.PAGINATION_PAGE_NEXT));
      buttonManager.disable($(Report.PAGINATION_PAGE_LAST));

      if ($(Report.PAGINATION_PAGE_CURRENT)) {
        $(Report.PAGINATION_PAGE_CURRENT).setValue('');
        $(Report.PAGINATION_PAGE_TOTAL).update('');
      }

      return;
    }

    if (currentPageIndex > 0) {
      buttonManager.enable($(Report.PAGINATION_PAGE_FIRST));
      buttonManager.enable($(Report.PAGINATION_PAGE_PREV));
    } else {
      buttonManager.disable($(Report.PAGINATION_PAGE_FIRST));
      buttonManager.disable($(Report.PAGINATION_PAGE_PREV));
    }

    if (lastPage == null) {
      if (this.lastPartialPageIndex == null || currentPageIndex < this.lastPartialPageIndex) {
        buttonManager.enable($(Report.PAGINATION_PAGE_NEXT));
      } else {
        buttonManager.disable($(Report.PAGINATION_PAGE_NEXT));
      }

      buttonManager.disable($(Report.PAGINATION_PAGE_LAST));
    } else if (currentPageIndex + 1 < lastPage) {
      buttonManager.enable($(Report.PAGINATION_PAGE_NEXT));
      buttonManager.enable($(Report.PAGINATION_PAGE_LAST));
    } else {
      buttonManager.disable($(Report.PAGINATION_PAGE_NEXT));
      buttonManager.disable($(Report.PAGINATION_PAGE_LAST));
    }

    buttonManager.enable($(Report.PAGINATION_PAGE_CURRENT));

    if ($(Report.PAGINATION_PAGE_CURRENT)) {
      if (!silentUpdate) {
        $(Report.PAGINATION_PAGE_CURRENT).setValue(currentPageIndex + 1);
      }

      if (lastPage == null) {
        $(Report.PAGINATION_PAGE_TOTAL).update('');
      } else {
        $(Report.PAGINATION_PAGE_TOTAL).update(this.getMessage('jasper.report.view.page.of') + lastPage);
      }
    }

    this.pageActions = {
      'button#page_first': 0,
      'button#page_prev': currentPageIndex - 1,
      'button#page_next': currentPageIndex + 1
    };

    if (window.viewer.reportInstance.status.totalPages != null) {
      this.pageActions['button#page_last'] = lastPage - 1;
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
  refreshAsyncCancel: function refreshAsyncCancel(canceled) {
    var meta = window.viewer.reportInstance.status;

    if ($(Report.ASYNC_CANCEL_BUTTON)) {
      if (canceled) {
        $('asyncIndicator').addClassName(layoutModule.HIDDEN_CLASS);
        buttonManager.disable(Report.ASYNC_CANCEL_BUTTON);
        $(Report.DATA_REFRESH_BUTTON).removeClassName(layoutModule.HIDDEN_CLASS);
      } else {
        if (meta.jasperPrintName && meta.reportStatus == 'running' && meta.totalPages == null) {
          buttonManager.enable(Report.ASYNC_CANCEL_BUTTON);
          $('asyncIndicator').removeClassName(layoutModule.HIDDEN_CLASS);
          $(Report.DATA_REFRESH_BUTTON).addClassName(layoutModule.HIDDEN_CLASS);
        } else {
          $('asyncIndicator').addClassName(layoutModule.HIDDEN_CLASS);
          buttonManager.disable(Report.ASYNC_CANCEL_BUTTON);
          $(Report.DATA_REFRESH_BUTTON).removeClassName(layoutModule.HIDDEN_CLASS);
        }
      }
    }
  }
});
/*
Methods bound to toolbar buttons
*/

/*
    Methods bound to toolbar buttons
*/

jQuery.extend(Report, {
  exportReport: function exportReport(type, formAction) {
    var exportForm = Report.exportForm,
        savedActionValue,
        genericParams,
        postData,
        cleanupForm;
    jQuery(exportForm).empty();
    exportForm.method = 'post';
    exportForm.target = '_blank';
    savedActionValue = exportForm.action;

    cleanupForm = function cleanupForm() {
      exportForm.target = '_self';
      exportForm.action = savedActionValue;
    };

    genericParams = {
      _eventId: 'export',
      _flowExecutionKey: Report.reportExecutionKey(),
      output: '' + type
    };
    var selection = Controls.getLastSelection();
    postData = _.extend(genericParams, selection);
    addDataToForm(exportForm, postData);

    if (formAction) {
      exportForm.action = formAction;
    }

    exportForm.submit();
    setTimeout(cleanupForm, 500);
  },
  goBack: function goBack() {
    if (!Report.confirmExit()) {
      return;
    }

    var params = Report.getAllRequestParameters(); // disable back button after first click
    // disable back button after first click

    buttonManager.disable($('back'));

    if (params['_ddHyperlink']) {
      window.history.back();
    } else {
      // exportForm is used here to leave the page
      Report.exportForm._eventId.value = 'close';
      Report.exportForm._flowExecutionKey.value = Report.reportExecutionKey();
      Report.exportForm.submit();
    }
  },
  open: function open() {
    alert('Not implemented yet: open report');
  },
  save: function save() {
    alert('Report saving is not available in community edition');
  },
  saveAs: function saveAs() {
    alert('Report saving is not available in community edition');
  },
  undo: function undo() {
    window.viewer.reportInstance.undo();
  },
  redo: function redo() {
    window.viewer.reportInstance.redo();
  },
  undoAll: function undoAll() {
    window.viewer.reportInstance.undoAll();
  },
  cancelReportAsyncExecution: function cancelReportAsyncExecution(bAsync) {
    if (!window.viewer.reportInstance.status.jasperPrintName) {
      return;
    }

    buttonManager.disable($(Report.ASYNC_CANCEL_BUTTON));
    window.viewer.reportInstance.cancelExecution(bAsync).then(function (jsonResponse, textStatus, jqXHR) {
      var status = jsonResponse.result.status;
      /*
      If report execution finished do nothing.
      */

      /*
       If report execution finished do nothing.
       */

      if (window.viewer.reportInstance.status.reportStatus == 'finished' || window.viewer.reportInstance.status.totalPages != null) {
        return;
      }

      if (jsonResponse.result.lastPartialPageIndex) {
        Report.lastPartialPageIndex = jsonResponse.result.lastPartialPageIndex;
      }

      if (status == 'error') {
        alert('Report execution error: ' + jsonResponse.result.errorMessage);
        return;
      }

      if (status == 'canceled') {
        Report.refreshAsyncCancel(true);
        dialogs.systemConfirm.show(Report.getMessage('jasper.report.view.report.canceled'), 5000);
        return;
      } else {
        if (jsonResponse.result.lastPageIndex) {
          Report.lastPageIndex = jsonResponse.result.lastPageIndex;
          window.viewer.reportInstance.status.totalPages = Report.lastPageIndex + 1;
        }

        if (jsonResponse.result.snapshotSaveStatus) {
          Report.snapshotSaveStatus = jsonResponse.result.snapshotSaveStatus;
        }

        window.viewer.reportInstance.status.reportStatus = status;
        Report.refreshPagination(true);
        Report.refreshExporters();
        Report.refreshSave();
        Report.refreshAsyncCancel();
      }
    }, function (jqXHR) {
      if (jqXHR.getResponseHeader('lastPartialPageIndex')) {
        Report.lastPartialPageIndex = jqXHR.getResponseHeader('lastPartialPageIndex');
      }

      Report.refreshPagination(true);
    });
  }
});
module.exports = Report;

});