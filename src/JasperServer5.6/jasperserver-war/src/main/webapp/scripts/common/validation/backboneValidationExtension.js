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
 * @version: $Id: backboneValidationExtension.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Validation = require("backbone.validation");

    _.extend(Validation.validators, {
        /*
         * Check that value does not contain forbidden symbols.
         *
         * @param value Value to be checked.
         * @param attr Name of the model's attribute to which validator belongs to.
         * @param forbiddenSymbols String containing forbidden symbols, they should be properly escaped,
         *      e.g. "~!#\\$%^|\\s`@&*()\\-+={}\\[\\]:;\"\"\\<\\>,?\/\\|\\\\"
         *
         * @return Undefined if value does not have forbidden symbols, error message otherwise.
         */
        doesNotContainSymbols: function(value, attr, forbiddenSymbols) {
            if (new RegExp("[" + forbiddenSymbols + "]", "g").test(value)){
                return "Attribute '" + attr + "' contains forbidden symbols";
            }
        }
    });

    return Validation;
});