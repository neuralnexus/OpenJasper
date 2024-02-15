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
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var JavaScriptExceptionBiComponentError = require("../error/JavaScriptExceptionBiComponentError"),
        BiComponentError = require("../error/BiComponentError"),
        SchemaValidationBiComponentError = require("../error/SchemaValidationBiComponentError"),
        ReportStatusError = require("../error/ReportStatusError"),
        ContainerNotFoundBiComponentError = require("../error/ContainerNotFoundBiComponentError"),
        RequestBiComponentError = require("../error/RequestBiComponentError");

    return {
        genericError: function(errorCode, message, parameters) {
            return new BiComponentError(errorCode, message, parameters);
        },
        validationError: function(validationError) {
            return new SchemaValidationBiComponentError(validationError);
        },
        javaScriptException: function(ex) {
            return new JavaScriptExceptionBiComponentError(ex);
        },
        requestError: function(xhr) {
            return new RequestBiComponentError(xhr);
        },
        reportStatus: function(errorObj) {
            return new ReportStatusError(errorObj);
        },
        containerNotFoundError: function(container) {
            return new ContainerNotFoundBiComponentError(container);
        }
    };
});