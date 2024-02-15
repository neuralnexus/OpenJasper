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

define(function (require) {
    "use strict";

    var RelativeDate = require("common/util/datetime/RelativeDate"),
        _ = require("underscore");

    var RelativeTimestamp = function() {
        RelativeDate.apply(this, arguments);
    };

    var F = function() { };
    F.prototype = RelativeDate.prototype;
    RelativeTimestamp.prototype = new F();
    RelativeTimestamp.prototype.constructor = RelativeTimestamp;

    RelativeTimestamp.PATTERNS = {
//        MINUTE : /^(MINUTE)(([+|\-])(\d{1,9}))?$/i,
//        HOUR : /^(HOUR)(([+|\-])(\d{1,9}))?$/i,
        DAY : /^(DAY)(([+|\-])(\d{1,9}))?$/i,
        WEEK : /^(WEEK)(([+|\-])(\d{1,9}))?$/i,
        MONTH : /^(MONTH)(([+|\-])(\d{1,9}))?$/i,
        QUARTER : /^(QUARTER)(([+|\-])(\d{1,9}))?$/i,
        SEMI : /^(SEMI)(([+|\-])(\d{1,9}))?$/i,
        YEAR : /^(YEAR)(([+|\-])(\d{1,9}))?$/i
    };

    RelativeTimestamp.parse = function(relativeTimestampString) {
        if (RelativeTimestamp.isValid(relativeTimestampString)) {
            for (var pattern in RelativeTimestamp.PATTERNS) {
                var result = RelativeTimestamp.PATTERNS[pattern].exec(relativeTimestampString);
                if (result !== null && _.isArray(result) && result.length === 5) {
                    return new RelativeTimestamp(result[1], result[3], result[4]);
                }
            }
        }

        return undefined;
    };

    RelativeTimestamp.isValid = function(relativeTimestamp) {
        if (relativeTimestamp instanceof RelativeTimestamp) {
            return relativeTimestamp.toString() !== "";
        } else if (_.isString(relativeTimestamp)) {
            for (var pattern in RelativeTimestamp.PATTERNS) {
                if (RelativeTimestamp.PATTERNS[pattern].test(relativeTimestamp)) {
                    return true;
                }
            }
        }

        return false;
    };

    return RelativeTimestamp;
});
