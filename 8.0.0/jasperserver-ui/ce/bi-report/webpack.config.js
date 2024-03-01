/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

const webpackConfigFactory = require("js-sdk/webpack.config");
const webpack = require('webpack');
const ruleTest = require('js-sdk/webpack/utils/ruleTest');

module.exports = (env = {}, argv) => {
    const webpackConfig = webpackConfigFactory(env, argv);

    return {
        ...webpackConfig,
        module: {
            ...webpackConfig.module,
            rules: [
                ...webpackConfig.module.rules,
                // used by bi-report/src/bi/report/loader/dynamicModuleLoader.js
                {
                    test: ruleTest('requirejs/require'),
                    use: ['exports-loader?requirejs,require,define']
                }
            ]
        },
        plugins: [
            ...webpackConfig.plugins,
            new webpack.EnvironmentPlugin({
                SCRIPT_LOAD_TIMEOUT_SEC: process.env.SCRIPT_LOAD_TIMEOUT_SEC || 60,
            })
        ],
    }
}