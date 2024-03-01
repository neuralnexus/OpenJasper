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

import jrsConfigs from 'js-sdk/src/jrs.configs';
import {ControlsBase as baseControls} from '../controls/controls.base';
import _ from 'underscore';
import $ from 'jquery';
import layoutModule from '../core/core.layout';
import Report from './report.view.runtime';
import Viewer from './viewer';
import logger from 'js-sdk/src/common/logging/logger';

import '../commons/commons.bare.main';
import '../components/components.dialogs';
import '../components/components.toolbarButtons.events';
import '../core/core.events.bis';
import '../core/core.ajax';

import 'jquery.urldecoder';
import '../controls/controls.report';
import 'jquery-ui-touch-punch';
import {Hash} from 'prototype';

window.printRequest = function () {
}    // Leave empty. This function is invoked by Flash charts in report for managed print.
// Printing is not available in report view, only in dashboard view.
;
window.FC_Rendered = function (DOMId) {
    $('#' + DOMId).hide().show();
};
var logEnabled = false,
    logLevel = "error";

if (window.Prototype) {
    delete Object.prototype.toJSON;
    delete Array.prototype.toJSON;
    delete Hash.prototype.toJSON;
    delete String.prototype.toJSON;
}
_.extend(baseControls, jrsConfigs.inputControlConstants);
_.extend(Report, jrsConfigs.reportViewer.ReportSettings);
if (!Report.allRequestParameters.viewAsDashboardFrame && Report.hasInputControls && (Report.reportControlsLayout === 2 || Report.reportControlsLayout === 4)) {
    layoutModule.resizeOnClient('inputControlsForm', 'reportViewFrame');
}

if (Report.allRequestParameters &&
    Report.allRequestParameters["_logEnabled"] &&
    Report.allRequestParameters["_logEnabled"].length &&
    Report.allRequestParameters["_logEnabled"][0] === "true") {

    logEnabled = true;
}

if (Report.allRequestParameters["_logLevel"] &&
    Report.allRequestParameters["_logLevel"].length) {

    logLevel = Report.allRequestParameters["_logLevel"][0];
}

logger.initialize({
    level: logLevel,
    enabled: logEnabled,
    appenders: ["console"]
});

var page = null,
    anchor = null;

if (Report.allRequestParameters &&
    Report.allRequestParameters["pageIndex"] &&
    Report.allRequestParameters["pageIndex"].length) {

    page = Report.allRequestParameters["pageIndex"][0];
}

if (Report.allRequestParameters &&
    Report.allRequestParameters["anchor"] &&
    Report.allRequestParameters["anchor"].length) {

    anchor = Report.allRequestParameters["anchor"][0];
}

window.viewer = new Viewer({
    at: '#reportContainer',
    reporturi: Report.reportUnitURI,
    async: true,
    page: page,
    anchor: anchor,
    contextPath: jrsConfigs.contextPath
});
Report.initialize();
