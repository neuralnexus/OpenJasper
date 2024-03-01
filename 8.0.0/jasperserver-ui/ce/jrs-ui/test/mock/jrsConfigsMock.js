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

/* global __jrsConfigs__, window */
// define the application specific variable
__jrsConfigs__ = {

    i18n: {},
    localContext: {},
    isIPad: "",
    contextPath: "/jasperserver-pro",
    userLocale: "en",
    userTimezone: "userTimezone",
    availableLocales: ["de", "en", "es", "fr", "it", "ja", "ro", "zh_TW", "zh_CN"],
    publicFolderUri: "/public",
    organizationId: "",
    advNotSelected: "To generate a report, first select an Ad Hoc view.",
    calendar: {

        userLocale: "en",

        timepicker: {
            timeText: 'Time:',
            hourText: 'Hour',
            minuteText: 'Minute',
            currentText: 'Now',
            closeText: 'Close',
            timeFormat: 'HH:mm:ss',
            dateFormat: 'yy-mm-dd'
        },

        i18n: {
            bundledCalendarTimeFormat: 'HH:mm:ss',
            bundledCalendarFormat: 'yy-mm-dd'
        }
    },

    webHelpModuleState: {
        contextMap: {"default":"100","bi_overview":"100","login":"150","dashboard":"200","ad_hoc":"300","domain":"500","search":"600","analysis":"700","admin":"800","repo":"900"},
        hostURL: 'http://help.jaspersoft.com/js-help-v5-en/',
        pagePrefix: 'Default_CSH.htm#'
    },

    urlContext: "/jasperserver-pro",
    defaultSearchText: "",
    serverIsNotResponding: "The server is slow or isn't responding. Confirm that you want to continue.",
    Export: {i18n: {
        "file.name.empty": "export.file.name.empty",
        "file.name.too.long": "export.file.name.too.long",
        "file.name.not.valid": "export.file.name.not.valid",
        "export.select.users": "export.select.users",
        "export.select.roles": "export.select.roles",
        "export.session.expired": "export.session.expired",
        "error.timeout": "export.file.name.empty"
    }, configs: {
        TIMEOUT: 1200000,
        DELAY: 3000
    }},

    xssNonce: '<js-templateNonce></js-templateNonce>'
};

__jrsConfigs__.isProVersion = window.__edition__ !== "pro";

// TODO: check this with zh-CN and zh-TW
__jrsConfigs__.calendar.i18n.module = "datepicker.i18n." + __jrsConfigs__.calendar.userLocale;

// TODO: check this with zh-CN and zh-TW
__jrsConfigs__.calendar.i18n.module = "datepicker.i18n." + __jrsConfigs__.calendar.userLocale;

//Heartbeat

__jrsConfigs__.heartbeatInitOptions = {
    baseUrl: "/jasperserver-pro",
    showDialog: false,
    sendClientInfo: false
};

__jrsConfigs__.localeSettings = {
    locale: "en_US",
    decimalSeparator: ".",
    groupingSeparator: ",",
    timeFormat: "hh:mm:ss",
    dateFormat: "yy-mm-dd",
    timestampSeparator: " "
};

__jrsConfigs__.inputControlsConstants = {};
__jrsConfigs__.inputControlsConstants.NULL_SUBSTITUTION_VALUE = "~NULL~";
__jrsConfigs__.inputControlsConstants.NULL_SUBSTITUTION_LABEL = "[Null]";