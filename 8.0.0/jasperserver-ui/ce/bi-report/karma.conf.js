/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

const webpackConfigFactory = require("./webpack.config");
const karmaConfig = require("js-sdk/karma.conf.default");
const karmaCoverageExcludes = require("./karma.coverage.excludes");
const getZeroCoverageOverridesUtil = require("js-sdk/util/coverage/getZeroCoverageOverridesUtil");
const isCoverageEnabled = require("js-sdk/util/coverage/isCoverageEnabled");

let conf = Object.assign({}, karmaConfig, {
    webpack: webpackConfigFactory({test: true, coverage: isCoverageEnabled()}),
    coverageIstanbulReporter: Object.assign({}, karmaConfig.coverageIstanbulReporter, {
        thresholds: {
            global: {
                statements: 21,
                branches: 10,
                functions: 26,
                lines: 21
            },
            each: {
                statements: 12,
                lines: 13,
                branches: 2,
                functions: 0,
                overrides: getZeroCoverageOverridesUtil(karmaCoverageExcludes)
            }
        }
    })
});

module.exports = function (config) {
    config.set(conf);
};
