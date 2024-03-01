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

const karmaConfig = require("./karma.conf.default");
const karmaCoverageExcludes = require("./karma.coverage.excludes");
const getZeroCoverageOverridesUtil = require("./util/coverage/getZeroCoverageOverridesUtil");

let conf = Object.assign({}, karmaConfig, {
    files: karmaConfig.files.concat([
        //include our CSS
        {pattern: 'themes/default/jasper-ui/**/*.css', include: false},
        {pattern: 'themes/default/jquery-ui/**/*.css', include: false}
    ]),
    coverageIstanbulReporter: Object.assign({}, karmaConfig.coverageIstanbulReporter, {
        thresholds: {
            global: {
                statements: 74,
                branches: 66,
                functions: 61,
                lines: 74
            },
            each: {
                statements: 37,
                lines: 37,
                branches: 18,
                functions: 0,
                overrides: getZeroCoverageOverridesUtil(karmaCoverageExcludes)
            }
        }
    })
});

module.exports = function(config) {
    config.set(conf);
};
