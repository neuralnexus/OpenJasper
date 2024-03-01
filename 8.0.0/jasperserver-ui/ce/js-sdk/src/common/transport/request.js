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
import _ from 'underscore';
import requestSettingsDefault from '../config/requestSettings';
import requestRegistry from "./requestRegistry";

const defaultRequest = (...args) => {
    const requestSettings = requestSettingsDefault();

    var mergedOptions = _.extend({}, requestSettings, args[0]);

    if (requestSettings.headers && args[0].headers) {
        mergedOptions.headers = _.extend({}, requestSettings.headers, args[0].headers);
    }

    args[0] = mergedOptions;

    return $.ajax.apply($, args);
}

export default function (...args) {
    if (requestRegistry.request) {
        return requestRegistry.request.call(null, ...args);
    }

    return defaultRequest(...args);
}