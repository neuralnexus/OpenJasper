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

import request from "js-sdk/src/common/transport/request";
import requestSettings from '../config/requestSettings';
import jrsConfigs from "js-sdk/src/jrs.configs";
import settingsStore from "../settings/settingsStore";

const settingsRestPath = "rest_v2/settings";
const scheduledSettingsGroupPromises = {};

const setSettingsGroupToStore = (settingsGroupUrl, context) => {
    settingsStore[settingsGroupUrl] = settingsStore[settingsGroupUrl] || {};
    Object.assign(settingsStore[settingsGroupUrl], context);
}

const loadSingleSettingsGroup = (settingsGroup, contextPath, headers) => {
    contextPath = contextPath || jrsConfigs.contextPath;
    const url = `${contextPath}/${settingsRestPath}/${settingsGroup}`;

    // if settings group already scheduled for loading or loaded - skip loading
    if (scheduledSettingsGroupPromises[settingsGroup]) {
        return scheduledSettingsGroupPromises[settingsGroup];
    }

    const originalSettings = requestSettings();
    const settings = {
        ...originalSettings,
        headers: {
            ...originalSettings.headers,
            // Changing default Cache Control directive to have possibility to decide caching on the server
            // Default value was 'no-cache'
            "Cache-Control": "private",
            "Pragma": "",
            ...headers
        },
        type: "GET",
        dataType: "json",
        url
    };

    const promise = request(settings).then(function (resp) {
        setSettingsGroupToStore(settingsGroup, resp)
        return resp;
    });

    // mark that settings group already scheduled for loading to avoid duplicate requests
    scheduledSettingsGroupPromises[settingsGroup] = promise;

    return promise;
}

export default (settingsPathes, contextPath, headers = {}) => {
    const promises = settingsPathes ? settingsPathes.map(path => loadSingleSettingsGroup(path, contextPath, headers)) : [Promise.resolve()];

    return Promise.all(promises);
}