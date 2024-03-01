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

import BiComponentError from './BiComponentError';
import errorCodes from './enum/biComponentErrorCodes';
import messages from './enum/biComponentErrorMessages';
export default BiComponentError.extend({
    constructor: function (xhr, code) {
        this.xmlHttpRequest = xhr;
        var errorCode = code, responseJson, msg, parameters;
        if (!code) {
            switch (xhr.status) {
            case 401:
                errorCode = errorCodes.AUTHENTICATION_ERROR;
                break;
            case 403:
                errorCode = errorCodes.AUTHORIZATION_ERROR;
                break;
            default:
                errorCode = errorCodes.UNEXPECTED_ERROR;
            }
        }
        msg = messages[errorCode];
        try {
            responseJson = JSON.parse(xhr.responseText);
        } catch (ex) {
        }
        if (responseJson) {
            if (responseJson.errorCode) {
                errorCode = responseJson.errorCode;
                msg = responseJson.message;
                parameters = (responseJson.parameters || !responseJson.properties) ? responseJson.parameters : responseJson.properties[0].value;
            } else {
                msg += ' : ' + responseJson.message;
            }
            if (responseJson.result && responseJson.result.msg) {
                errorCode = responseJson.result.msg;
                if (responseJson.result.devmsg) {
                    msg = responseJson.result.devmsg;
                }
            }
        }
        BiComponentError.prototype.constructor.call(this, errorCode, msg, parameters);
    }
});