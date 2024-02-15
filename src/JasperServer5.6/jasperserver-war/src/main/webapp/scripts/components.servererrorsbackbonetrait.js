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
 * @author: inesterenko
 * @version: $Id: components.servererrorsbackbonetrait.js 47331 2014-07-18 09:13:06Z kklein $
 */

if (typeof(jaspersoft) === "undefined"){
    jaspersoft = {};
}

if (!jaspersoft.components){
    jaspersoft.components = {};
}

jaspersoft.components.ServerErrorsBackboneTrait = (function ($, _) {

    return {

        statuses: {},

        defaultErrorDelegator:function (model, xhr) {
            if (xhr.status == 401){
                // refresh the page, user will be redirected to login automatically
                window.location.href = window.location.href;
            } else{
                this.trigger("error:server", this.parseServerError(xhr));
            }
        },

        parseServerError:function (xhr) {
            var error;
            try {
                error = $.parseJSON(xhr.responseText);
            } catch (e) { // this is not JSON
                error = this.mapUnserializableErrors(xhr)
            }
            return error;
        },

        mapUnserializableErrors: function(xhr){
            var error = {errorCode:"unserializable.error"};
            if (this.statuses[xhr.status]){
                error.message = this.statuses[xhr.status];
            } else {
                error.message = xhr.statusText;
            }
            return error;
        }
    }
})(
    jQuery,
    _
);
