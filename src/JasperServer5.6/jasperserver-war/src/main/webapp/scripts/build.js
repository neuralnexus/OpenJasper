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
 * @version: $Id: build.js 47331 2014-07-18 09:13:06Z kklein $
 */

({
    //The top level directory that contains your app.
    //by default equals to dir that holds build.js file
    //appDir: "."

    //Path to require config. Path is relative to dir that holds build.js file
    mainConfigFile: 'require.config.js',

    //Base url for all scripts in require config. Is relative to appDir
    baseUrl: './',

    //Allow CSS optimizations.
    optimizeCss: "none",

    //How to optimize all the JS files in the build output directory.
    optimize: "uglify2",

    //Whether to skip optimization of whole appDir
    skipDirOptimize: false,

    //If set to true, any files that were combined into a build bundle will be
    //removed from the output folder.
    //Should be set to false since it removes necessary files
    removeCombined: false,

    //Whether to preserve license comments in optimized files
    preserveLicenseComments: false,

    //Set paths for modules.
    //"empty:" means that optimizer will skip this file - good for CDN resources
    paths: {
        "jquery": "empty:",

        //Prototype has issues with uglifying so excude it from optimization process
        //It has to be included as usual script bafore any module can use it
        "prototype": "empty:",

        "report.global": "empty:",
        "csrf.guard": "empty:",
        "wcf.scroll": "empty:",
        "ReportRequireJsConfig": "empty:"
    },

    //Exclude prototype, Owasp.CsrfGuard so they will not be copied to optimized scripts folder
    //because they have troubles with uglifying
    fileExclusionRegExp: /(^\.|prototype.*patched\.js|Owasp\.CsrfGuard\.js)/,

    shim: {

        //Raw version of jasper.js
        "common/jasper/jasper": {
            deps: [
                "lib/require-2.1.10",
                "lib/jquery-1.11.0",
                "common/jasper/core/Root"
            ],
            exports: "jasper"
        },

        //Built version of jasper.js
        "client/jasper": {
            exports: "jasper"
        },

        "xdm.remote": {
            deps: [
                "lib/require-2.1.10",
                "require.config",
                "lib/jquery-1.11.0"
            ]
        }
    },

    //List the modules that will be optimized.
    modules: [

        {name: "commons.main"},
        {name: "dataSource/addDataSourcePage"},
        {name: "addDataType.page"},
        {name: "addFileResource.page"},
        {name: "addInputControl.page"},
        {name: "addInputControlQueryInformation.page"},
        {name: "addJasperReport.page"},
        {name: "addJasperReportLocateControl.page"},
        {name: "addJasperReportResourceNaming.page"},
        {name: "addJasperReportResourcesAndControls.page"},
        {name: "addListOfValues.page"},
        {name: "addMondrianXML.page"},
        {name: "addOLAPView.page"},
        {name: "addQuery.page"},
        {name: "admin.export.page"},
        {name: "admin.import.page"},
        {name: "admin.logging.page"},
        {name: "admin.options.page"},
        {name: "admin.roles.page"},
        {name: "admin.users.page"},
        {name: "connectionType.page"},
        {name: "dataTypeLocate.page"},
        {name: "listOfValuesLocate.page"},
        {name: "locateDataSource.page"},
        {name: "locateMondrianConnectionSource.page"},
        {name: "locateQuery.page"},
        {name: "locateXmlConnectionSource.page"},
        {name: "login.page"},
        {name: "olap.view.page"},
        {name: "report.viewer.page"},
        {name: "plain.report.viewer.page"},
        {name: "results.page"},
        {name: "messages/details/messageDetails.page"},
        {name: "messages/list/messageList.page"},
        {name: "scheduler/JobsPage"},
        {name: "encrypt.page"},
        {name: "error.system"},
        {name: "xdm.remote"}
    ]
})
