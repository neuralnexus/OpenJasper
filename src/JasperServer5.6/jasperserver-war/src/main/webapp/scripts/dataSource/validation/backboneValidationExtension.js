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

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore");

    require("backbone.validation");

    _.extend(Backbone.Validation.validators, {
        doesNotContainCharacters: function(value, attr, customValue, model) {
            if (new RegExp(customValue, "g").test(value)) {
                return "Value contains invalid characters";
            }
        },
        xRegExpPattern: function(value, attr, customValue, model) {
            if (!customValue.test(value)) {
                return "Value does not match pattern";
            }
        },
        startsWithLetter: function(value, attr, customValue, model) {
            if (!value.substr(0, 1).match(/[A-Za-z]/)) {
                return "Value should start with letter";
            }
        },
        containsOnlyWordCharacters: function(value, attr, customValue, model) {
            if (value.search(/\W/) >= 0 ) {
                return "Value should contain only word characters (letters, digits and underscore)";
            }
        },
        arrayMinLength: function(value, attr, minLength, model) {
            if (_.isArray(value) && value.length < minLength) {
                return "Array length is less than " + minLength;
            }
        }
    });

    return Backbone.Validation;
});