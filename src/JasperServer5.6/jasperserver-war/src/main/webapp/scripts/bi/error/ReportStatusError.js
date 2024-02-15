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

    var BiComponentError = require("./BiComponentError"),
        _ = require("underscore"),
        reportStatuses = require("report/enum/reportStatuses"),
        errorCodes = require("./enum/biComponentErrorCodes"),
        messages = require("./enum/biComponentErrorMessages");

    return BiComponentError.extend({
        constructor: function (errorObj) {
            _.extend(this, errorObj);
            if(errorObj.errorDescriptor){
                BiComponentError.prototype.constructor.call(this,
                    errorObj.errorDescriptor.errorCode,
                    errorObj.errorDescriptor.message,
                    errorObj.errorDescriptor.parameters);
            } else {

                var code;

                if (errorObj.source === "execution") {
                    code = errorCodes[errorObj.status === reportStatuses.CANCELLED ? "REPORT_EXECUTION_CANCELLED" : "REPORT_EXECUTION_FAILED"];
                } else {
                    code = errorCodes[errorObj.status === reportStatuses.CANCELLED ? "REPORT_EXPORT_CANCELLED" : "REPORT_EXPORT_FAILED"];
                }

                var msg = messages[code];

                if (errorObj.format) {
                    msg += (" : format - '" + errorObj.format + "'");
                }

                BiComponentError.prototype.constructor.call(this, code, msg);
            }
        }
    });
});