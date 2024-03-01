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

import {store, MERGED_BUNDLES_NAME} from "./bundleStore";
import jrsConfigs from "js-sdk/src/jrs.configs";
import requestSettings from '../config/requestSettings';
import request from 'js-sdk/src/common/transport/request';
import logger from "js-sdk/src/common/logging/logger";

const log = logger.register("bundleLoader");

// use bundle name "all" to get all available bundles merged to single bundle
const bundlesRestPath = 'rest_v2/bundles';

export const scheduledBundlePromises = {};
export const NO_CONTENT = 204;

const setBundleToStore = (bundleName, content) => {
    store[bundleName] = store[bundleName] || {};

    Object.assign(store[bundleName], content);
}

const loadSingleBundle = (bundleName, contextPath) => {
    contextPath = contextPath || jrsConfigs.contextPath;

    // if bundle already scheduled for loading or loaded - skip loading
    const existingBundlePromise = scheduledBundlePromises[bundleName];
    if (existingBundlePromise) {
        return existingBundlePromise;
    }

    const isAllBundle = MERGED_BUNDLES_NAME === bundleName;
    const urlSuffix = isAllBundle ? "?expanded=true" : "/" + bundleName;
    const url = `${contextPath}/${bundlesRestPath}${urlSuffix}`;

    const originalSettings = requestSettings();
    const settings = {
        ...originalSettings,
        headers: {
            ...originalSettings.headers,
            // Changing default Cache Control directive to have possibility to decide caching on the server
            // Default value was 'no-cache'
            "Cache-Control": "private",
            "Pragma": ""
        },
        type: "GET",
        dataType: "json",
        url
    };

    const promise = request(settings).then(function (resp, statusText, xhr) {
        if (xhr.status === NO_CONTENT) {
            log.error(`No content for bundle: [${bundleName}]. Make sure bundles are deployed to web app`);
        }

        if (isAllBundle) {
            Object.keys(resp).forEach(key => {
                setBundleToStore(key, resp[key]);
            })
        } else {
            setBundleToStore(bundleName, resp);
        }

        return resp;
    });

    // mark that bundle already scheduled for loading to avoid duplicate requests
    scheduledBundlePromises[bundleName] = promise;

    return promise;
}

export default (bundlePathes, contextPath) => {
    const promises = bundlePathes ? bundlePathes.map(path => loadSingleBundle(path, contextPath)) : [Promise.resolve()];

    return Promise.all(promises);
}
