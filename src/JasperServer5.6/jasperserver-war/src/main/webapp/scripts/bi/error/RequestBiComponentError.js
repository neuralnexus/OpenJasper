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
        json3 = require("json3"),
        errorCodes = require("./enum/biComponentErrorCodes"),
        messages = require("./enum/biComponentErrorMessages");

    return BiComponentError.extend({
        constructor: function (xhr) {
            this.xmlHttpRequest = xhr;

            var errorCode = xhr.status === 401 ? errorCodes.AUTHENTICATION_ERROR : errorCodes.UNEXPECTED_ERROR,
                responseJson,
                msg = messages[errorCode],
                parameters = undefined;

            try {
                responseJson = json3.parse(xhr.responseText);
            } catch(ex) {}

            if (responseJson) {
                if(responseJson.errorCode){
                    errorCode = responseJson.errorCode;
                    msg = responseJson.message;
                    parameters = responseJson.parameters;
                } else {
                    msg += (" : " + responseJson.message);
                }
            }

            BiComponentError.prototype.constructor.call(this, errorCode, msg, parameters);
        }
    });
});