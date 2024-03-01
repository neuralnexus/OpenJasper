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

module.exports = {
    dir: "../optimized-scripts",

    mainConfigFile: "require.config.js",

    optimizeCss: "none",

    optimize: "uglify2",

    skipDirOptimize: false,

    removeCombined: false,

    preserveLicenseComments: false,

    paths: {
        // stub jquery, r.js throws an error
        "jquery": "empty:",
        "prototype": "empty:",

        "report.global": "empty:",
        "ReportRequireJsConfig": "empty:"
    },
    shim: {
        "commons/commons.main": {
            deps: [
                // include jquery in a bundle via deps, because it cannot be traced by r.js during optimization
                "runtime_dependencies/jquery/dist/jquery"
            ]
        }
    },
    modules: [
        {
            "name": "commons/commons.main"
        },
        {
            "name": "addResource/dataType/addDataTypeMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/fileResource/addFileResourceMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/inputControls/addInputControlMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/inputControls/addInputControlQueryInformationMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/inputControls/dataTypeLocateMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/inputControls/listOfValuesLocateMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/jasperReport/addJasperReportMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/jasperReport/addJasperReportLocateControlMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/jasperReport/addJasperReportResourceNamingMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/jasperReport/addJasperReportResourcesAndControlsMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/listOfValues/addListOfValuesMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/mondrianXml/addMondrianXmlMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/analysisView/addOLAPViewMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/query/addQueryMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/query/addQueryWithResourceLocatorMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/analysisClientConnection/addAnalysisClientConnectionMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "administer/administerCustomAttributesMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "administer/administerExportMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "administer/administerImportMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "administer/administerLoggingMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "administer/administerAnalysisOptionsMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "administer/resetSettings/resetSettingsMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "manage/manageRolesMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "manage/manageUsersMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "dataSource/dataSourceMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/analysisClientConnection/locateDataSourceMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/analysisClientConnection/locateMondrianConnectionSourceMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/query/locateQueryMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "addResource/analysisClientConnection/locateXmlConnectionSourceMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "login/loginMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "olapView/olapViewMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "reportViewer/reportViewerMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "repository/repositoryMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "messages/details/messageDetailsMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "messages/list/messageListMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "scheduler/schedulerMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "encrypt/encryptMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "system/systemErrorMain",
            "exclude": ["commons/commons.main"]
        },
        {
            "name": "system/errorMain",
            "exclude": ["commons/commons.main"]
        }
    ]
};
