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

const webpackConfigFactory = require("./webpack.config");
const karmaConfig = require("js-sdk/karma.conf.default");
const isCoverageEnabled = require("js-sdk/util/coverage/isCoverageEnabled");

let conf = Object.assign({}, karmaConfig, {
    webpack: webpackConfigFactory({test: true, coverage: isCoverageEnabled()}),
    coverageIstanbulReporter: Object.assign({}, karmaConfig.coverageIstanbulReporter, {
        thresholds: {
            global: {
                statements: 88,
                branches: 85,
                functions: 68,
                lines: 88
            },
            each: {
                statements: 49,
                lines: 49,
                branches: 66,
                functions: 0
            }
        }
    })
});

module.exports = function (config) {
    config.set(conf);
};
