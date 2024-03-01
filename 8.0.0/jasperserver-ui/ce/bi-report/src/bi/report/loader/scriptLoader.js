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

/*global document, process */

const LOAD_TIMEOUT_MILLIS = Number(process.env.SCRIPT_LOAD_TIMEOUT_SEC || 60) * 1000;

const urlCache = {};

export const loadScript = (url, {cache = true, prefix = '', scriptProps = {}} = {}) => {
    const cacheKey = prefix + url;

    // return promise for the cache
    if (cache && urlCache[cacheKey]) {
        return urlCache[cacheKey];
    }

    const result = new Promise(((resolve, reject) => {
        const script = document.createElement('script');

        const props = {
            charset: 'utf-8',
            timeout: LOAD_TIMEOUT_MILLIS,
            ...scriptProps
        }

        Object.keys(props).forEach(p => script[p] = props[p]);

        script.src = url;

        const onScriptComplete = function () {
            script.onerror = null;
            script.onload = null;
            clearTimeout(scriptTimeout);
            resolve();
        };

        const onScriptError = function (...args) {
            script.onerror = null;
            script.onload = null;
            clearTimeout(scriptTimeout);
            reject(...args);
        };

        const scriptTimeout = setTimeout(function () {
            onScriptError(new Error(`Load timeout for the script ${url}`));
        }, props.timeout);

        script.onerror = onScriptError
        script.onload = onScriptComplete;

        document.head.appendChild(script);
    }))

    urlCache[cacheKey] = result;

    return result;
}