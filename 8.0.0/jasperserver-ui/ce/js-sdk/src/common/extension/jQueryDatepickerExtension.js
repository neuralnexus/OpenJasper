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

import 'jquery-ui/ui/widgets/datepicker';

var _jasperPrefix = "jr";
var _jasperPopupClass = "jr-jDatepickerPopupContainer";

var datepickerId = $.datepicker.dpDiv.attr("id"),
    _mainDivId = _jasperPrefix + "-" + datepickerId;

$.datepicker._mainDivId = _mainDivId;

$.datepicker.dpDiv.attr("id", _mainDivId);

$.datepicker.dpDiv.removeClass();

var dpDivClass = _jasperPopupClass
    + ' ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all '
    + _jasperPrefix;

$.datepicker.dpDiv.addClass(dpDivClass);

var originalNewInst = $.datepicker._newInst;

$.datepicker._newInst = function() {
    var result = originalNewInst.apply($.datepicker, arguments);

    result.dpDiv.removeClass(_jasperPrefix);
    result.dpDiv.addClass(_jasperPrefix);

    return result;
};

var originalGoToToday = $.datepicker._gotoToday;

$.datepicker._gotoToday = function(id) {
    originalGoToToday.call(this, id);
    this._selectDate(id);
};

export default $;