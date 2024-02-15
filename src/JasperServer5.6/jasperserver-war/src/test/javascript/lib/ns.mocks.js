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
 * @version: $Id: ns.mocks.js 47331 2014-07-18 09:13:06Z kklein $
 */

jQuery.noConflict();
/*
 * JasperServer namespace.
 */

if (typeof(jaspersoft) == "undefined"){
    jaspersoft = {
        components: {},
        i18n: {}
    };
}

if(typeof(JRS) == "undefined"){
    JRS = {
        Mocks : {}
    };
}

if (typeof(JRS.vars) == "undefined"){
    JRS.vars = {
        element_scrolled: false,
        ajax_in_progress: false,
        current_flow: null
    };
}

if(typeof(layoutModule) == "undefined"){
    layoutModule = {};

}

if (typeof(dialogs) == "undefined"){
    dialogs = {
        popup :  {
            show: function(){},
            hide : function(){}
        },
        systemConfirm : {
            show: function(){ }
        }
    };
}

if (typeof(serverTimeoutInterval) == "undefined") {
    serverTimeoutInterval = 1200;
}
if (typeof(adHocSessionExpireCode) == "undefined") {
    adHocSessionExpireCode = "Click OK to keep your session alive, otherwise it will expire at:";
}
if (typeof(adHocExitConfirmation) == "undefined") {
    adHocExitConfirmation = "Changes you made will be lost. Confirm that you want to quit the Ad Hoc Editor.";
}

if(typeof(Mustache) == "undefined"){
    Mustache = {
        to_html : function(){}
    }
}

if(typeof(ControlsBase) == "undefined"){
    ControlsBase = {
        convertJQueryParams : function(){

        }
    }
}

if(typeof(Report) == "undefined"){
    Report = {
        reportOptionsURI : "testReportOptionUri"
    }
}

if (typeof(clientKey) == "undefined"){
    clientKey = "testClientKey";
}

if (typeof(Draggable) == "undefined"){
    Draggable = function(){}

}

if (typeof(JRS.Export) == "undefined"){
    JRS.Export = {i18n : {}};
}

if (typeof(jive) == "undefined"){
    jive = {
        started: false,
        hide : function(){}
    }
}

// Variables added for the AdHoc Table
if (typeof(serverTimeoutInterval) == "undefined") {
    serverTimeoutInterval = 1200;
}
adHocSessionExpireCode = "Click OK to keep your session alive, otherwise it will expire at:";
adHocExitConfirmation = "Changes you made will be lost. Confirm that you want to quit the Ad Hoc Editor.";
dynamicFilterInputError = "Check the value provided.";
filterAutoSubmitTimer = 500;
layoutManagerLabels = {
    "column": {
        "table": "Columns",
        "ichart": "Columns",
        "olap_ichart": "Columns",
        "crosstab": "Columns"
    },
    "row": {
        "table": "Groups",
        "ichart": "Rows",
        "olap_ichart": "Rows",
        "crosstab": "Rows"
    }
};
urlContext = "/jrs-pro-trunk";
saveLabel = "";

// Variables added for the AdHoc Crosstab
dynamicFilterInputError = "Check the value provided.";
filterAutoSubmitTimer = 500;

// Variables added for the AdHoc Table
if (typeof(serverTimeoutInterval) == "undefined") {
    serverTimeoutInterval = 1200;
}
adHocSessionExpireCode = "Click OK to keep your session alive, otherwise it will expire at:";
adHocExitConfirmation = "Changes you made will be lost. Confirm that you want to quit the Ad Hoc Editor.";
dynamicFilterInputError = "Check the value provided.";
filterAutoSubmitTimer = 500;
layoutManagerLabels = {
    "column": {
        "table": "Columns",
        "ichart": "Columns",
        "olap_ichart": "Columns",
        "crosstab": "Columns"
    },
    "row": {
        "table": "Groups",
        "ichart": "Rows",
        "olap_ichart": "Rows",
        "crosstab": "Rows"
    }
};
urlContext = "/jrs-pro-trunk";
saveLabel = "";

// Variables added for the AdHoc Crosstab
dynamicFilterInputError = "Check the value provided.";
filterAutoSubmitTimer = 500;