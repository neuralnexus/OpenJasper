/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import {loadScript} from './scriptLoader';

let uid = 0;

const generateUid = () => {
    uid = uid + 1;
    return `__async_req_${uid}__`;
}

export const loadJsonp = (url, jsonpCallbackParam = 'callback') => {
    return new Promise((resolve, reject) => {
        const callbackFunctionName = generateUid();
        window[callbackFunctionName] = resolve;

        const initialUrl = new URL(url, window.location.href);
        initialUrl.searchParams.append(jsonpCallbackParam, callbackFunctionName);

        loadScript(initialUrl.href, {scriptProps: {async: true}}).catch(reject);
    });
};