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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: require.config.js 47331 2014-07-18 09:13:06Z kklein $
 */

// temporary requirejs config taken for JR
requirejs.config({
    paths: {
        "async": "lib/async",
        "text": "common/plugin/text",
        "jasperreports-loader": "report/jasperreports-loader",

        // JIVE i18n
        "jive.i18n": "../reportresource/reportresource?resource=jive.i18n",
        
        // JIVE CSS
        "jive.vm": "../reportresource/reportresource?resource=jive.vm",
        "jive.sort.vm": "../reportresource/reportresource?resource=jive.sort.vm",
        "jive.crosstab.templates.styles": "../reportresource/reportresource?resource=jive.crosstab.templates.styles",
        "jive.highcharts.vm": "../reportresource/reportresource?resource=jive.highcharts.vm",

        // JIVE templates
        "jive.templates": "../reportresource/reportresource?resource=jive.templates",
        "jive.crosstab.templates": "../reportresource/reportresource?resource=net/sf/jasperreports/crosstabs/interactive/jive.crosstab.templates",
        "jive.chartSelector": "../reportresource/reportresource?resource=jive.chartSelector",
        "jive.filterDialog": "../reportresource/reportresource?resource=jive.filterDialog",

        // JR and JIVE JavaScript files
        "jive.table": "jr/jive.table",
        "jive": "jr/jive",
        "jive.column": "jr/jive.column",
        "jasperreports-component-registrar": "jr/jasperreports-component-registrar",
        "jasperreports-status-checker": "jr/jasperreports-status-checker",
        "jive.crosstab.interactive": "jr/jive.crosstab.interactive",
        "jasperreports-viewer": "jr/jasperreports-viewer",
        "jasperreports-report-processor": "jr/jasperreports-report-processor",
        "jasperreports-utils": "jr/jasperreports-utils",
        "jasperreports-map": "jr/jasperreports-map",
        "jive.sort": "jr/jive.sort",
        "jive.crosstab": "jr/jive.crosstab",
        "jive.interactive.column": "jr/jive.interactive.column",
        "jasperreports-event-manager": "jr/jasperreports-event-manager",
        "jasperreports-ajax": "jr/jasperreports-ajax",
        "jasperreports-url-manager": "jr/jasperreports-url-manager",
        "jasperreports-report": "jr/jasperreports-report",
        "jive.interactive.sort": "jr/jive.interactive.sort",
        "jive.fusion": "jr/jive.fusion",
        "itemHyperlinkSettingService": "jr/item.hyperlink.service",
        "defaultSettingService": "jr/default.service",
        "jive.highcharts": "jr/jive.highcharts",
        "dualPieSettingService": "jr/dual.pie.service",
        "yAxisSettingService": "jr/y.axis.service",
        "jive.interactive.highchart": "jr/jive.interactive.highchart",
        "adhocHighchartsSettingService": "jr/adhocHighchartsSettingService"
    }
});
