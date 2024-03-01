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

import $ from 'jquery';

import './jQueryDatepickerExtension';
import './jQueryUiSliderAccessExtension';
import 'jqueryui-timepicker-addon/dist/jquery-ui-timepicker-addon';

var originalNewInst = $.timepicker._newInst;

$.timepicker._newInst = function($input, opts) {
    if (!opts.onChangeMonthYear) {
        opts.onChangeMonthYear = function(year, month, datepicker, timepicker) {
            datepicker.currentYear = datepicker.selectedYear;
            datepicker.currentMonth = datepicker.selectedMonth;
            datepicker.currentDay = datepicker.selectedDay;

            timepicker._updateDateTime(datepicker);
        };
    }

    var instance = originalNewInst.call($.timepicker, $input, opts);

    var originalOnTimeChange = instance._onTimeChange;

    instance._onTimeChange = function() {
        this.$timeObj[0].setSelectionRange = null;

        return originalOnTimeChange.apply(this, arguments);
    };

    return instance;
};

export default $;