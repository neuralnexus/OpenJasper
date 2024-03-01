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

import jrsConfigs from 'js-sdk/src/jrs.configs';
import $ from 'jquery';
import _ from 'underscore';

import dateTimeSettings from '../settings/dateTimeSettings.settings';
import DateAndTimePicker from 'js-sdk/src/components/dateAndTime/DateAndTimePicker';

function getLocale(userLocale, availableLocales) {
    var postfix = 'en';
    if (userLocale) {
        if (_.contains(availableLocales, userLocale)) {
            postfix = userLocale;
        } else if (_.contains(availableLocales, userLocale.substring(0, 2))) {
            postfix = userLocale.substring(0, 2);
        }
    }
    return postfix.replace('_', '-');
}
var locale = getLocale(jrsConfigs.userLocale, jrsConfigs.availableLocales);
DateAndTimePicker.setDefaults({
    locale: locale,
    date: dateTimeSettings.datepicker,
    time: dateTimeSettings.timepicker
});
export default $;