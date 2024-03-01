define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var tv4 = require('tv4');

var timeUtil = require('../util/parse/time');

var dateUtil = require('../util/parse/date');

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
tv4.addFormat({
  'date-time': function dateTime(value) {
    if (dateUtil.isIso8601Timestamp(value)) {
      return null;
    }

    return 'A valid ISO 8601 date-time string (YYYY-MM-DDThh:mm:ss) is expected';
  },
  'time': function time(value) {
    if (timeUtil.isIso8601Time(value)) {
      return null;
    }

    return 'A valid ISO 8601 time string (hh:mm:ss) is expected';
  }
});
module.exports = tv4;

});