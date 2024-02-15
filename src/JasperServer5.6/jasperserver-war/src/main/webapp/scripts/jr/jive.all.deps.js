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
 * @version $Id: jive.all.deps.js 47331 2014-07-18 09:13:06Z kklein $
 */

/**
 * Just a stub for r.js to have ability
 * to get all jive dependencies included at build time
 * */

 define(function (require) {

    require("jquery");
    require("jquery.ui");
    require("jquery.timepicker");
    require("jasperreports-loader");

    require("jive.table");
    require("jive");
    require("jive.column");
    require("jasperreports-component-registrar");
    require("jasperreports-status-checker");
    require("jive.crosstab.interactive");
    require("jasperreports-viewer");
    require("jasperreports-report-processor");
    require("jasperreports-utils");
    require("jasperreports-map");
    require("jive.sort");
    require("jive.crosstab");
    require("jive.interactive.column");
    require("jasperreports-event-manager");
    require("jasperreports-ajax");
    require("jasperreports-url-manager");
    require("jasperreports-report");
    require("jive.interactive.sort");
    require("jive.fusion");
    require("itemHyperlinkSettingService");
    require("defaultSettingService");
    require("jive.highcharts");
    require("dualPieSettingService");
    require("yAxisSettingService");
    require("jive.interactive.highchart");
    require("adhocHighchartsSettingService");

    return {};
});
