/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import jrsConfigs from 'js-sdk/src/jrs.configs';
import request from 'js-sdk/src/common/transport/request';
// need to rename require, requirejs and define vars
// because otherwise webpack 5 trying to process them
import {require as requireLocal, requirejs as requirejsLocal, define as defineLocal} from 'requirejs/require';

const baseUrl = `${jrsConfigs.contextPath}/scripts`;

const getModuleUrl = (module, url) => {
    const realBaseUrl = new URL(baseUrl, window.location.href).href;
    const fullUrl = new URL(url || `${realBaseUrl}/${module}`, realBaseUrl);
    return fullUrl.pathname.match(/\.js$/g) ? fullUrl.href : `${fullUrl.href}.js`;
}

const modulesPromises = {};

export const loadDynamicModule = (module, url) => {
    if (modulesPromises[module]) {
        return modulesPromises[module];
    }

    var settings = {
        type: 'GET',
        dataType: 'text',
        cache: true,
        url: getModuleUrl(module, url)
    };

    const promise = new Promise((resolve, reject) => {
        request(settings).then((response) => {
            const func = new Function("__visualize__", "__jrio__", "require", "requirejs", "define", response);

            const namespace = {define: defineLocal, require: requireLocal, requirejs: requirejsLocal};
            func(namespace, namespace, requireLocal, requirejsLocal, defineLocal);

            requireLocal([module], (module) => {
                resolve(module);
            }, reject);
        }).catch(reject);
    });

    modulesPromises[module] = promise;

    return promise;
}
